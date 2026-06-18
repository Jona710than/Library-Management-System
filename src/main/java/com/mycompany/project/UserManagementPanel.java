package com.mycompany.project;

// import com.toedter.calendar.JDateChooser;
import db.DBConnection;

import javax.swing.*;import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Date;

public class UserManagementPanel extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField firstnameField, surnameField, emailField,phoneField;
    private JComboBox<String> roleCombo;
    private JComboBox<String> genderCombo;
    // private JDateChooser dobChooser;
    private JPasswordField passwordField;
    private String adminName;

    public UserManagementPanel(String adminName) {
        this.adminName = adminName;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // formPanel
        JPanel formPanel = new JPanel(new GridLayout(4, 6, 10, 10));
        firstnameField = new JTextField();
        surnameField = new JTextField();
        emailField = new JTextField();
        roleCombo =  new JComboBox<>(new String[]{"admin","librarian","student"});
        phoneField = new JTextField();
        // dobChooser = new JDateChooser();
        // dobChooser.setDateFormatString("yyyy-MM-dd");
        passwordField = new JPasswordField();
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});

        formPanel.add(new JLabel("First Name:"));   formPanel.add(firstnameField);
        formPanel.add(new JLabel("Surname:"));      formPanel.add(surnameField);
        formPanel.add(new JLabel("Email:"));        formPanel.add(emailField);
        formPanel.add(new JLabel("Role:"));         formPanel.add(roleCombo);
        formPanel.add(new JLabel("Phone:"));        formPanel.add(phoneField);
        // formPanel.add(new JLabel("Date of Birth:"));formPanel.add(dobChooser);
        formPanel.add(new JLabel("Gender:"));       formPanel.add(genderCombo);
        formPanel.add(new JLabel("Password:"));     formPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add User");
        JButton deleteButton = new JButton("Delete Selected");
        JButton backButton = new JButton("Back");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        backButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
            new AdminDashboard(adminName);
        });

        tableModel = new DefaultTableModel(new String[]{
                "ID", "First Name", "Surname", "Email", "Role", "Phone", "Gender"
        }, 0);
        userTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(userTable);

        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(tableScroll, BorderLayout.CENTER);

        loadUsers();

        addButton.addActionListener(e -> addUser());
        deleteButton.addActionListener(e -> deleteSelectedUser());
    }

    //Function loadusers
    private void loadUsers() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT userID, firstname, surname, email, role, phone, gender FROM users")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("userID"),
                        rs.getString("firstname"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("phone"),
                        // rs.getDate("dob"),
                        rs.getString("gender")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }
    }

    //Function addusers
    private void addUser() {
        String firstname = firstnameField.getText().trim();
        String surname = surnameField.getText().trim();
        String email = emailField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();
        String phone = phoneField.getText().trim();
        // Date dob = dobChooser.getDate();
        String gender = (String) genderCombo.getSelectedItem();
        String password = new String(passwordField.getPassword());

        if (firstname.isEmpty() || surname.isEmpty() || email.isEmpty() || role.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        if (!role.matches("admin|librarian|student")) {
            JOptionPane.showMessageDialog(this, "Role must be admin, librarian, or student.");
            return;
        }
        

        // if (dob == null) {
        //     JOptionPane.showMessageDialog(this, "Please select a valid date of birth.");
        //     return;
        // }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO users (firstname, surname, email, role, phone, gender, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            String hashedPassword = hashPassword(password);

            stmt.setString(1, firstname);
            stmt.setString(2, surname);
            stmt.setString(3, email);
            stmt.setString(4, role);
            stmt.setString(5, phone);
            // stmt.setDate(6, new java.sql.Date(dob.getTime()));
            stmt.setString(6, gender);
            stmt.setString(7, hashedPassword);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "User added successfully.");
                loadUsers();
                clearInputs();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage());
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashInBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found.", e);
        }
    }

    //Function deleteSelectedUser
    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }

        int userId = (int) tableModel.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM users WHERE userID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            if (stmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "User deleted successfully.");
                loadUsers();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage());
        }
    }

    // Function clearInputs
    private void clearInputs() {
        firstnameField.setText("");
        surnameField.setText("");
        emailField.setText("");
        roleCombo.setSelectedIndex(0);
        phoneField.setText("");
        // dobChooser.setDate(null);
        genderCombo.setSelectedIndex(0);
        passwordField.setText("");
    }
}
