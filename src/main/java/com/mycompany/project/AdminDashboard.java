package com.mycompany.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;
    private String adminName;

    public AdminDashboard(String adminName) {
        this.adminName = adminName;

        setTitle("Admin Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //Welcome Panel with Logout
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.X_AXIS));
        welcomePanel.setBackground(new Color(240, 248, 255));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Left section with welcome text
        JPanel leftWelcomePanel = new JPanel();
        leftWelcomePanel.setLayout(new BoxLayout(leftWelcomePanel, BoxLayout.Y_AXIS));
        leftWelcomePanel.setBackground(new Color(240, 248, 255));

        JLabel welcomeLabel = new JLabel("Welcome Admin", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Georgia", Font.BOLD, 26));
        welcomeLabel.setForeground(new Color(60, 60, 60));

        JLabel nameLabel = new JLabel(adminName, SwingConstants.LEFT);
        nameLabel.setFont(new Font("Georgia", Font.PLAIN, 22));
        nameLabel.setForeground(new Color(100, 100, 150));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        leftWelcomePanel.add(welcomeLabel);
        leftWelcomePanel.add(nameLabel);

        // Right section with logout button
        JButton logoutButton = new JButton("Log Out");
        logoutButton.setFont(new Font("Georgia", Font.PLAIN, 14));
//        logoutButton.setBackground(new Color(220, 53, 69));
//        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.setMaximumSize(new Dimension(100, 30));
        logoutButton.setAlignmentY(Component.TOP_ALIGNMENT);

        // Logout action
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Log Out", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // close current dashboard
                new LoginForm().setVisible(true); // go back to login form
            }
        });

        welcomePanel.add(leftWelcomePanel);
        welcomePanel.add(Box.createHorizontalGlue());
        welcomePanel.add(logoutButton);

        add(welcomePanel, BorderLayout.NORTH);

        //Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(10, 1, 10, 10));
        sidebar.setBackground(new Color(30, 30, 30));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JButton userMgmtButton = new JButton("User Management");
        JButton bookMgmtButton = new JButton("Book Management");
        JButton reportsButton = new JButton("Admin Reports");

        styleSidebarButton(userMgmtButton);
        styleSidebarButton(bookMgmtButton);
        styleSidebarButton(reportsButton);

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(userMgmtButton);
        sidebar.add(bookMgmtButton);
        sidebar.add(reportsButton);

        add(sidebar, BorderLayout.WEST);

        //Content Panel
        contentPanel = new JPanel(new BorderLayout());
        JLabel defaultLabel = new JLabel("Select an option from the sidebar", SwingConstants.CENTER);
        defaultLabel.setFont(new Font("Georgia", Font.ITALIC, 18));
        defaultLabel.setForeground(new Color(120, 120, 120));
        contentPanel.add(defaultLabel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // Actions
        userMgmtButton.addActionListener((ActionEvent e) -> {
            contentPanel.removeAll();
            contentPanel.add(new UserManagementPanel(adminName));
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        bookMgmtButton.addActionListener((ActionEvent e) -> {
            contentPanel.removeAll();
            contentPanel.add(new BookManagementPanel(adminName));
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        reportsButton.addActionListener((ActionEvent e) -> {
            contentPanel.removeAll();
            contentPanel.add(new AdminReportPanel(adminName));
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        setVisible(true);
    }

    private void styleSidebarButton(JButton button) {
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 50));
        button.setFont(new Font("Georgia", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
