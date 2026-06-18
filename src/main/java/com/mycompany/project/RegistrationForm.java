package com.mycompany.project;

//import com.toedter.calendar.JDateChooser;
import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class RegistrationForm extends JFrame {

    private JTextField firstnameField, surnameField, emailField, phoneField;
//    private JDateChooser dobChooser;
    private JComboBox<String> genderDropdown;
    private JComboBox<String> roleDropdown;
    private JPasswordField passwordField;
    private JButton registerButton, loginButton;

    public RegistrationForm() {
        setTitle("Library Registration Form");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel);

        // Left Image Panel
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/images/book3.jpg"));
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        JPanel imagePanel = new JPanel(new BorderLayout());
        imageLabel.setPreferredSize(new Dimension(350, 400));
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        getContentPane().add(imagePanel, BorderLayout.WEST);

        // Right Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBackground(new Color(52, 80, 80));
        mainPanel.add(formPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Registration Form", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        titleLabel.setForeground(Color.ORANGE);
        titleLabel.setBounds(20, 10, 300, 30);
        formPanel.add(titleLabel);
         
        //Textfield for firstname,surname,email,phone

        firstnameField = createField("First Name", 70, formPanel);
        surnameField = createField("Surname", 120, formPanel);
        emailField = createField("Email", 170, formPanel);
        phoneField = createField("Phone", 220, formPanel);


        

        // Gender Dropdown
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(70, 255, 100, 20);
        genderLabel.setForeground(Color.WHITE);
        formPanel.add(genderLabel);
        genderDropdown = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderDropdown.setBounds(70, 275, 200, 30);
        // genderDropdown.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formPanel.add(genderDropdown);

        // Date of Birth using JDateChooser
//        JLabel dobLabel = new JLabel("Date of Birth:");
//        dobLabel.setBounds(70, 330, 100, 20);
//        formPanel.add(dobLabel);
//        dobChooser = new JDateChooser();
//        dobChooser.setBounds(70, 350, 200, 30);
//        dobChooser.setDateFormatString("yyyy-MM-dd");
//        formPanel.add(dobChooser);

        // Role Dropdown
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(70, 310, 100, 20);
        roleLabel.setForeground(Color.WHITE);
        formPanel.add(roleLabel);
        roleDropdown = new JComboBox<>(new String[]{"student", "librarian", "admin"});
        // roleDropdown.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        roleDropdown.setBounds(70, 330, 200, 30);
        formPanel.add(roleDropdown);

        // Password Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(70, 365, 100, 20);
        passwordLabel.setForeground(Color.WHITE);
        formPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(70, 390, 200, 30);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formPanel.add(passwordField);

        //Register button
        registerButton = new JButton("Register");
        registerButton.setBounds(70, 450, 200, 30);
        registerButton.setBackground(Color.ORANGE);
        registerButton.setForeground(Color.WHITE);
        formPanel.add(registerButton);

        //login button
        loginButton = new JButton("Back to Login");
        loginButton.setBounds(70, 490, 200, 30);
        loginButton.setBackground(Color.DARK_GRAY);
        loginButton.setForeground(Color.ORANGE);
        formPanel.add(loginButton);

        // Action Listeners that changes from login to registration form
        registerButton.addActionListener(e -> registerUser());
        loginButton.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });
    }

        //Continuation of the Textfield for firstname,surname,email,phone
    private JTextField createField(String label, int y, JPanel panel) {
        JLabel jLabel = new JLabel(label + ":");
        jLabel.setBounds(70, y - 20, 200, 20);
        jLabel.setForeground(Color.WHITE);
        panel.add(jLabel);

        JTextField field = new JTextField();
        field.setBounds(70, y, 200, 30);
        field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.add(field);

        return field;
    }

    //Function for Registration
    private void registerUser() {
        String firstname = firstnameField.getText().trim();
        String surname = surnameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String gender = (String) genderDropdown.getSelectedItem();
        String role = (String) roleDropdown.getSelectedItem();
        String password = new String(passwordField.getPassword());

        if (firstname.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate email (simple regex)
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Invalid email address!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Date formatting
//        String dob = null;
//        if (dobChooser.getDate() != null) {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            dob = sdf.format(dobChooser.getDate());
//        } else {
//            JOptionPane.showMessageDialog(this, "Please select Date of Birth.", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }

        try {
            Connection conn = DBConnection.getConnection();

            String checkQuery = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Email already registered!", "Error", JOptionPane.ERROR_MESSAGE);
                conn.close();
                return;
            }

            String hashedPassword = hashPassword(password);

            String insertQuery = "INSERT INTO users (firstname, surname, email, phone, gender,  role, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, firstname);
            insertStmt.setString(2, surname);
            insertStmt.setString(3, email);
            insertStmt.setString(4, phone);
            insertStmt.setString(5, gender);
//            insertStmt.setString(6, dob);
            insertStmt.setString(6, role);
            insertStmt.setString(7, hashedPassword);

            int rows = insertStmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                new LoginForm().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            conn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashInBytes = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationForm().setVisible(true));
    }
}
