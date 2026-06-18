package com.mycompany.project;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginForm extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginForm() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());

        // === LEFT IMAGE PANEL ===
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/books.jpg"));
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(350, 400));
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        getContentPane().add(imagePanel, BorderLayout.WEST);

        // === RIGHT FORM PANEL ===
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setPreferredSize(new Dimension(350, 400));
        formPanel.setBackground(Color.DARK_GRAY);

        JLabel titleLabel = new JLabel("User Login");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 24));
        titleLabel.setForeground(Color.ORANGE);
        titleLabel.setBounds(100, 20, 200, 30);
        formPanel.add(titleLabel);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Georgia", Font.PLAIN, 14));
        emailLabel.setBounds(70, 60, 100, 25);
        formPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(70, 90, 200, 30);
        emailField.setBackground(Color.WHITE);
        emailField.setForeground(Color.BLACK);
        emailField.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 1));
        formPanel.add(emailField);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Georgia", Font.PLAIN, 14));
        passLabel.setBounds(70, 130, 100, 25);
        formPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(70, 160, 200, 30);
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(Color.BLACK);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 1));
        formPanel.add(passwordField);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setBounds(70, 210, 200, 30);
        loginButton.setBackground(Color.ORANGE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> login());
        formPanel.add(loginButton);

        //Reminder to register
        JLabel pLabel = new JLabel("Don't have an account please register");
        pLabel.setForeground(Color.WHITE);
        pLabel.setFont(new Font("Georgia", Font.PLAIN, 11));
        pLabel.setBounds(70, 250, 200, 25);
        formPanel.add(pLabel);

        // Register Button that opens the register form
        registerButton = new JButton("Register");
        registerButton.setBounds(70, 280, 200, 30);
        registerButton.setBackground(Color.DARK_GRAY);
        registerButton.setForeground(Color.ORANGE);
        registerButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> {
            dispose();
            new RegistrationForm().setVisible(true);
        });
        formPanel.add(registerButton);

        getContentPane().add(formPanel, BorderLayout.CENTER);
    }
    
      //Function login
    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "SELECT userID, firstname, surname, password, role FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String enteredHashedPassword = hashPassword(password);

                if (storedHashedPassword.equals(enteredHashedPassword)) {
                    String role = rs.getString("role").toLowerCase();
                    int userId = rs.getInt("userID");
                    String firstname = rs.getString("firstname");
                    String surname = rs.getString("surname");
                    String fullName = firstname + " " + surname;

                    Preloader preloader = new Preloader();

                preloader.showAndRun(() -> {
                    switch (role) {
                        case "admin":
                            new AdminDashboard(fullName);
                            break;
                        case "student":
                            JFrame studentFrame = new JFrame("Student Dashboard");
                            studentFrame.setSize(800, 600);
                            studentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            studentFrame.add(new StudentPanel(userId));
                            studentFrame.setVisible(true);
                            break;
                        case "librarian":
                            JFrame librarianFrame = new JFrame("Librarian Dashboard");
                            librarianFrame.setSize(800, 600);
                            librarianFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            librarianFrame.add(new LibrarianPanel(userId));
                            librarianFrame.setVisible(true);
                            break;
                        default:
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this, "Unknown user role.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                            });
                    }
                });
                dispose(); // close login window


                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No account found with this email.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String hashPassword(String password) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hashInBytes = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
     
    
}
