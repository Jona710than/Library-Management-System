package com.mycompany.project;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminReportPanel extends JPanel {
    private JLabel totalBooksLabel;
    private JLabel totalUsersLabel;
    private JLabel totalStudentsLabel;
    private JLabel totalLibrariansLabel;
    private JLabel totalAdminsLabel;
    private JLabel totalTransactionsLabel;
    private JLabel totalFinesLabel;

    private String adminName;

    public AdminReportPanel(String adminName) {
        this.adminName = adminName;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // reportPanel
        JPanel reportPanel = new JPanel(new GridLayout(7, 1, 10, 10));

        totalBooksLabel = new JLabel();
        totalUsersLabel = new JLabel();
        totalStudentsLabel = new JLabel();
        totalLibrariansLabel = new JLabel();
        totalAdminsLabel = new JLabel();
        totalTransactionsLabel = new JLabel();
        totalFinesLabel = new JLabel();

        setFont(totalBooksLabel);
        setFont(totalUsersLabel);
        setFont(totalStudentsLabel);
        setFont(totalLibrariansLabel);
        setFont(totalAdminsLabel);
        setFont(totalTransactionsLabel);
        setFont(totalFinesLabel);

        reportPanel.add(totalBooksLabel);
        reportPanel.add(totalUsersLabel);
        reportPanel.add(totalStudentsLabel);
        reportPanel.add(totalLibrariansLabel);
        reportPanel.add(totalAdminsLabel);
        reportPanel.add(totalTransactionsLabel);
        reportPanel.add(totalFinesLabel);

        add(reportPanel, BorderLayout.CENTER);

        // Back Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("Georgia", Font.PLAIN, 14));
        backButton.setBackground(Color.DARK_GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
            new AdminDashboard(adminName);
        });

        loadReport();
    }

    private void setFont(JLabel label) {
        label.setFont(new Font("Georgia", Font.BOLD, 16));
        label.setForeground(new Color(50, 50, 80));
    }

    // Function loadReport
    private void loadReport() {
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();

            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM books");
            if (rs1.next()) totalBooksLabel.setText(" Total Books: " + rs1.getInt(1));

            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs2.next()) totalUsersLabel.setText(" Total Users: " + rs2.getInt(1));

            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'student'");
            if (rs3.next()) totalStudentsLabel.setText(" Total Students: " + rs3.getInt(1));

            ResultSet rs4 = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'librarian'");
            if (rs4.next()) totalLibrariansLabel.setText(" Total Librarians: " + rs4.getInt(1));

            ResultSet rs5 = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'admin'");
            if (rs5.next()) totalAdminsLabel.setText(" Total Admins: " + rs5.getInt(1));

            ResultSet rs6 = stmt.executeQuery("SELECT COUNT(*) FROM transactions");
            if (rs6.next()) totalTransactionsLabel.setText(" Total Transactions: " + rs6.getInt(1));

            ResultSet rs7 = stmt.executeQuery("SELECT SUM(fine_amount) FROM transactions");
            if (rs7.next()) {
                double totalFines = rs7.getDouble(1);
                totalFinesLabel.setText(" Total Fines Collected: ₦" + totalFines);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
