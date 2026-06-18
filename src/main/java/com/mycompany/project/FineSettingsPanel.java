package com.mycompany.project;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FineSettingsPanel extends JPanel {
    private JTextField fineField;
    private JLabel currentFineLabel;
    private JButton updateBtn;

    public FineSettingsPanel() {
        setLayout(new GridLayout(3, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        currentFineLabel = new JLabel("Current Fine Per Day: ₦0", SwingConstants.CENTER);
        currentFineLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        fineField = new JTextField();
        updateBtn = new JButton("Update Fine Amount");

        add(currentFineLabel);
        add(fineField);
        add(updateBtn);

        loadCurrentFine();

        updateBtn.addActionListener(e -> updateFine());
    }

    // Function loadCurrentFine
    private void loadCurrentFine() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT setting_value FROM settings WHERE setting_key = 'fine_per_day'")) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String fine = rs.getString("setting_value");
                currentFineLabel.setText("Current Fine Per Day: ₦" + fine);
                fineField.setText(fine);
            } else {
                currentFineLabel.setText("Current Fine Per Day: ₦0");
                fineField.setText("0");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading fine value.");
        }
    }

    // Function updateFine
    private void updateFine() {
        String newFine = fineField.getText().trim();
        if (!newFine.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid whole number.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Update fine to ₦" + newFine + "?", "Confirm Update", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        updateBtn.setEnabled(false);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE settings SET setting_value = ? WHERE setting_key = 'fine_per_day'")) {

            stmt.setString(1, newFine);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Fine updated successfully!");
                loadCurrentFine();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. Please try again.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update fine.");
        } finally {
            updateBtn.setEnabled(true);
        }
    }
}
