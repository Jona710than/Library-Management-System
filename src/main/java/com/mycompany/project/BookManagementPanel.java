package com.mycompany.project;

import db.DBConnection;
import utils.OpenLibrarySearch;
import utils.OpenLibrarySearch.Book;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       

public class BookManagementPanel extends JPanel {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField titleField, authorField, publisherField, isbnField, categoryField, searchField;
    private JCheckBox availabilityCheck;
    private JButton addButton;
    private String adminName;

    public BookManagementPanel(String adminName) {
        this.adminName = adminName;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search Bar
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
          searchField = new JTextField("Search...");
        searchField.setForeground(Color.GRAY);
        searchField.setFont(new Font("Georgia", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension (200,30));
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search...");
                }
            }
        });

        JButton searchLocalButton = new JButton("Search Local");
        JButton searchOpenButton = new JButton("Search Open Library");

        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel buttonBox = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonBox.add(searchLocalButton);
        buttonBox.add(searchOpenButton);
        searchPanel.add(buttonBox, BorderLayout.EAST);

        add(searchPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 6, 10, 10));
        titleField = new JTextField();
        authorField = new JTextField();
        publisherField = new JTextField();
        isbnField = new JTextField();
        categoryField = new JTextField();
        availabilityCheck = new JCheckBox("Available");

        formPanel.add(new JLabel("Title:"));       formPanel.add(titleField);
        formPanel.add(new JLabel("Author:"));      formPanel.add(authorField);
        formPanel.add(new JLabel("Publisher:"));   formPanel.add(publisherField);
        formPanel.add(new JLabel("ISBN:"));        formPanel.add(isbnField);
        formPanel.add(new JLabel("Category:"));    formPanel.add(categoryField);
        formPanel.add(new JLabel(""));             formPanel.add(availabilityCheck);
        add(formPanel, BorderLayout.SOUTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add Book");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton backButton = new JButton("Back");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.AFTER_LAST_LINE);

        backButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
            new AdminDashboard(adminName);
        });

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Publisher", "ISBN", "Category", "Available"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load Initial Data
        loadBooks();

        // Enable Add button only when all fields are filled
        DocumentListener dl = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { checkFields(); }
            public void removeUpdate(DocumentEvent e) { checkFields(); }
            public void insertUpdate(DocumentEvent e) { checkFields(); }
        };

        titleField.getDocument().addDocumentListener(dl);
        authorField.getDocument().addDocumentListener(dl);
        publisherField.getDocument().addDocumentListener(dl);
        isbnField.getDocument().addDocumentListener(dl);
        categoryField.getDocument().addDocumentListener(dl);

        checkFields();

        addButton.addActionListener(e -> {
            try {
                addBook();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Add Error: " + ex.getMessage());
            }
        });

        updateButton.addActionListener(e -> {
            try {
                updateBook();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Update Error: " + ex.getMessage());
            }
        });

        deleteButton.addActionListener(e -> {
            try {
                deleteBook();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage());
            }
        });

        bookTable.getSelectionModel().addListSelectionListener(e -> fillFormFromSelectedRow());

        searchLocalButton.addActionListener(e -> searchLocalBooks(searchField.getText().trim()));
        searchOpenButton.addActionListener(e -> searchOpenLibrary(searchField.getText().trim()));
    }

    private void checkFields() {
        boolean allFilled = !titleField.getText().trim().isEmpty() &&
                            !authorField.getText().trim().isEmpty() &&
                            !publisherField.getText().trim().isEmpty() &&
                            !isbnField.getText().trim().isEmpty() &&
                            !categoryField.getText().trim().isEmpty();
        addButton.setEnabled(allFilled);
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("bookID"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getString("isbn"),
                        rs.getString("category"),
                        rs.getBoolean("availability")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addBook() throws Exception {
        if (!validateForm()) return;

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO books (title, author, publisher, isbn, category, availability) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, titleField.getText().trim());
            stmt.setString(2, authorField.getText().trim());
            stmt.setString(3, publisherField.getText().trim());
            stmt.setString(4, isbnField.getText().trim());
            stmt.setString(5, categoryField.getText().trim());
            stmt.setBoolean(6, availabilityCheck.isSelected());

            if (stmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Book added!");
                clearForm();
                loadBooks();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add book.");
        }
    }

    private void updateBook() throws Exception {
        int row = bookTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a book to update.");
            return;
        }

        if (!validateForm()) return;

        int bookId = (int) tableModel.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE books SET title=?, author=?, publisher=?, isbn=?, category=?, availability=? WHERE bookID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, titleField.getText().trim());
            stmt.setString(2, authorField.getText().trim());
            stmt.setString(3, publisherField.getText().trim());
            stmt.setString(4, isbnField.getText().trim());
            stmt.setString(5, categoryField.getText().trim());
            stmt.setBoolean(6, availabilityCheck.isSelected());
            stmt.setInt(7, bookId);

            if (stmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Book updated.");
                clearForm();
                loadBooks();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update book.");
        }
    }

    private void deleteBook() throws Exception {
        int row = bookTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a book to delete.");
            return;
        }

        int bookId = (int) tableModel.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM books WHERE bookID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookId);

            if (stmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Book deleted.");
                clearForm();
                loadBooks();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete book.");
        }
    }

    private void searchLocalBooks(String keyword) {
        tableModel.setRowCount(0);
        String sql = """
            SELECT * FROM books
            WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? OR category LIKE ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String kw = "%" + keyword + "%";
            for (int i = 1; i <= 4; i++) {
                stmt.setString(i, kw);
            }

            ResultSet rs = stmt.executeQuery();
            boolean found = false;

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("bookID"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getString("isbn"),
                        rs.getString("category"),
                        rs.getBoolean("availability")
                });
                found = true;
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No books found in local database.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Local search error: " + ex.getMessage());
        }
    }

    private void searchOpenLibrary(String keyword) {
        tableModel.setRowCount(0);

        try {
            ArrayList<Book> results = OpenLibrarySearch.searchBooks(keyword);

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No books found in Open Library.");
                return;
            }

            for (Book book : results) {
                tableModel.addRow(new Object[]{
                        "-", book.title, book.author, book.publisher, book.isbn, "-", false
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Open Library search failed: " + ex.getMessage());
        }
    }

    private void fillFormFromSelectedRow() {
        int row = bookTable.getSelectedRow();
        if (row != -1) {
            titleField.setText((String) tableModel.getValueAt(row, 1));
            authorField.setText((String) tableModel.getValueAt(row, 2));
            publisherField.setText((String) tableModel.getValueAt(row, 3));
            isbnField.setText((String) tableModel.getValueAt(row, 4));
            categoryField.setText((String) tableModel.getValueAt(row, 5));
            availabilityCheck.setSelected((Boolean) tableModel.getValueAt(row, 6));
        }
    }

    private void clearForm() {
        titleField.setText("");
        authorField.setText("");
        publisherField.setText("");
        isbnField.setText("");
        categoryField.setText("");
        availabilityCheck.setSelected(false);
        checkFields();
    }

    private boolean validateForm() {
        return !titleField.getText().trim().isEmpty() &&
               !authorField.getText().trim().isEmpty() &&
               !publisherField.getText().trim().isEmpty() &&
               !isbnField.getText().trim().isEmpty() &&
               !categoryField.getText().trim().isEmpty();
    }
}
