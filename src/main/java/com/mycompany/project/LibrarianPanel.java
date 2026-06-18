package com.mycompany.project;

import db.DBConnection;
// import org.apache.poi.ss.usermodel.Workbook;
// import org.apache.poi.ss.usermodel.Sheet;
// import org.apache.poi.ss.usermodel.Row;
// import org.apache.poi.ss.usermodel.Cell;
// import org.apache.poi.ss.usermodel.CreationHelper;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Font;
import java.awt.Color;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LibrarianPanel extends JPanel {
    private JComboBox<String> userDropdown, bookDropdown;
    private JTable borrowTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton logoutButton, updateButton, deleteButton;
    private int librarianId;
    private String librarianName;
    // private Image backgroundImage;


    public LibrarianPanel(int userId) {
        this.librarianId = userId;
        this.librarianName = fetchLibrarianName(userId);
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        

        // welcomePanel for welcome and name 
        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome Librarian");
        welcomeLabel.setFont(new java.awt.Font("Georgia", Font.BOLD, 26));
        welcomeLabel.setForeground(new java.awt.Color(60, 60, 60));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 0));
        
        
        // namelabel
        JLabel nameLabel = new JLabel(librarianName.toUpperCase());
        nameLabel.setFont(new java.awt.Font("Georgia", Font.PLAIN, 22));
        nameLabel.setForeground(new java.awt.Color(100, 100, 150));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 0));

        // welcomePanel.add(welcomeLabel,nameLabel);
        welcomePanel.setBackground(new java.awt.Color(240, 248, 255));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        welcomePanel.add(nameLabel, BorderLayout.SOUTH);
        welcomePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK ,2));
         

        //searchPanel for search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        //  User & Book Dropdowns below welcomePanel
        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userDropdown = new JComboBox<>();
        bookDropdown = new JComboBox<>();
        dropdownPanel.add(new JLabel("User:"));
        dropdownPanel.add(userDropdown);
        dropdownPanel.add(Box.createHorizontalStrut(10));
        dropdownPanel.add(new JLabel("Book:"));
        dropdownPanel.add(bookDropdown);
        dropdownPanel.add(Box.createHorizontalStrut(10));

        //TopPanel containing dropdownPanel & searchPanel 
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(dropdownPanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
       

        // TopContainer containing welcomePanel & topPanel
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topContainer.add(welcomePanel);
        topContainer.add(topPanel);
        add(topContainer, BorderLayout.NORTH);


        // === Center: Transaction Table ===
        tableModel = new DefaultTableModel(new String[]{
                "Transaction ID", "User", "Book", "Borrow Date", "Due Date", "Return Date", "Fine"
        }, 0);
        borrowTable = new JTable(tableModel);
        add(new JScrollPane(borrowTable), BorderLayout.CENTER);
        // backgroundImage = new ImageIcon(getClass().getResource("/images/library_bg.webp")).getImage();

        // Actionpanel for actionbuttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton borrowButton = new JButton("Borrow");
        JButton returnButton = new JButton("Return");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        // JButton exportButton = new JButton("Export");

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutButton = new JButton("Log Out");
        logoutPanel.add(logoutButton);

        actionPanel.add(borrowButton);
        actionPanel.add(returnButton);
        actionPanel.add(updateButton);
        actionPanel.add(deleteButton);
        // actionPanel.add(exportButton);

        // southPanel add actionpanel & logoutpanel
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
        southPanel.add(actionPanel);
        southPanel.add(Box.createHorizontalGlue()); 
        southPanel.add(logoutPanel);
        add(southPanel, BorderLayout.SOUTH);


        // Load data
        loadUsers();
        loadBooks();
        loadTransactions();

        // Listeners
        borrowButton.addActionListener(e -> borrowBook());
        returnButton.addActionListener(e -> returnBook());
        updateButton.addActionListener(e -> updateTransaction());
        deleteButton.addActionListener(e -> deleteTransaction());
        // exportButton.addActionListener(e -> exportToExcel());
        logoutButton.addActionListener(e -> logout());
        searchButton.addActionListener(e -> filterTable(searchField.getText().trim()));

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterTable(searchField.getText().trim());
            }
        });
    }

 // Paint background image
    // @Override
    // protected void paintComponent(Graphics g) {
    //     super.paintComponent(g);
    //     // Draw image scaled to fit
    //     g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    // }

    // Function that gets LibrarianName for the namelabel
    private String fetchLibrarianName(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT firstname, surname FROM users WHERE userID = ?");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("firstname") + " " + rs.getString("surname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Librarian";
    }

    //Function loadusers
    private void loadUsers() {
        userDropdown.removeAllItems();
        try (Connection conn = DBConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT userID, firstname, surname FROM users")) {
            while (rs.next()) {
                userDropdown.addItem(rs.getInt("userID") + " - " + rs.getString("firstname") + " " + rs.getString("surname"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //Function loadBooks
    private void loadBooks() {
        bookDropdown.removeAllItems();
        try (Connection conn = DBConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT bookID, title FROM books WHERE availability = true")) {
            while (rs.next()) {
                bookDropdown.addItem(rs.getInt("bookID") + " - " + rs.getString("title"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //Function loadTransactions
    private void loadTransactions() {
        tableModel.setRowCount(0);
        String sql = """
            SELECT t.transactionID, CONCAT(u.firstname, ' ', u.surname) AS name, b.title,
                   t.borrow_date, t.due_date, t.return_date, t.fine_amount
            FROM transactions t
            JOIN users u ON t.userID = u.userID
            JOIN books b ON t.bookID = b.bookID
            ORDER BY t.transactionID DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("transactionID"),
                        rs.getString("name"),
                        rs.getString("title"),
                        rs.getDate("borrow_date"),
                        rs.getDate("due_date"),
                        rs.getDate("return_date"),
                        rs.getDouble("fine_amount")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Function borrowBook
    private void borrowBook() {
        if (userDropdown.getSelectedItem() == null || bookDropdown.getSelectedItem() == null) return;

        int userId = Integer.parseInt(userDropdown.getSelectedItem().toString().split(" - ")[0]);
        int bookId = Integer.parseInt(bookDropdown.getSelectedItem().toString().split(" - ")[0]);

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(7);

        String sql = "INSERT INTO transactions (userID, bookID, borrow_date, due_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.setDate(3, Date.valueOf(borrowDate));
            stmt.setDate(4, Date.valueOf(dueDate));

            if (stmt.executeUpdate() > 0) {
                conn.prepareStatement("UPDATE books SET availability = false WHERE bookID = " + bookId).executeUpdate();
                JOptionPane.showMessageDialog(this, "Book borrowed successfully.");
                loadBooks();
                loadTransactions();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Function returnbook
    private void returnBook() {
        int row = borrowTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a transaction to return.");
            return;
        }

        Object returnDate = tableModel.getValueAt(row, 5);
        if (returnDate != null) {
            JOptionPane.showMessageDialog(this, "This book has already been returned.");
            return;
        }

        int transactionId = (int) tableModel.getValueAt(row, 0);
        LocalDate dueDate = ((Date) tableModel.getValueAt(row, 4)).toLocalDate();
        LocalDate today = LocalDate.now();

        long overdue = ChronoUnit.DAYS.between(dueDate, today);
        double fine = Math.max(0, overdue * 100);

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement update = conn.prepareStatement("""
                UPDATE transactions SET return_date = ?, fine_amount = ? WHERE transactionID = ?
            """);
            update.setDate(1, Date.valueOf(today));
            update.setDouble(2, fine);
            update.setInt(3, transactionId);
            update.executeUpdate();

            PreparedStatement getBook = conn.prepareStatement("SELECT bookID FROM transactions WHERE transactionID = ?");
            getBook.setInt(1, transactionId);
            ResultSet rs = getBook.executeQuery();

            if (rs.next()) {
                int bookId = rs.getInt("bookID");
                conn.prepareStatement("UPDATE books SET availability = true WHERE bookID = " + bookId).executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Returned. Fine: ₦" + fine);
            loadBooks();
            loadTransactions();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Function updateTransaction
    private void updateTransaction() {
        int row = borrowTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a transaction to update.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int transactionId = (int) tableModel.getValueAt(row, 0);

            String newDueDateStr = JOptionPane.showInputDialog(this, "Enter new due date (YYYY-MM-DD):");
            if (newDueDateStr == null || newDueDateStr.isBlank()) return;

            Date newDueDate = Date.valueOf(newDueDateStr);

            PreparedStatement stmt = conn.prepareStatement("UPDATE transactions SET due_date = ? WHERE transactionID = ?");
            stmt.setDate(1, newDueDate);
            stmt.setInt(2, transactionId);

            if (stmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Transaction updated successfully.");
                loadTransactions();
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Function deleteTransaction
    private void deleteTransaction() {
        int row = borrowTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a transaction to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this transaction?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int transactionId = (int) tableModel.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement getBookStmt = conn.prepareStatement("SELECT bookID, return_date FROM transactions WHERE transactionID = ?");
            getBookStmt.setInt(1, transactionId);
            ResultSet rs = getBookStmt.executeQuery();
            if (rs.next()) {
                int bookId = rs.getInt("bookID");
                Date returnDate = rs.getDate("return_date");
                if (returnDate == null) {
                    conn.prepareStatement("UPDATE books SET availability = true WHERE bookID = " + bookId).executeUpdate();
                }
            }

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM transactions WHERE transactionID = ?");
            stmt.setInt(1, transactionId);

            if (stmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Transaction deleted successfully.");
                loadBooks();
                loadTransactions();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Function exportToExcel
    // private void exportToExcel() {
    //     try (Workbook workbook = new XSSFWorkbook()) {
    //         Sheet sheet = workbook.createSheet("Transactions");
    //         Row headerRow = sheet.createRow(0);
    //         for (int i = 0; i < tableModel.getColumnCount(); i++) {
    //             Cell cell = headerRow.createCell(i);
    //             cell.setCellValue(tableModel.getColumnName(i));
    //         }

    //         for (int r = 0; r < tableModel.getRowCount(); r++) {
    //             Row row = sheet.createRow(r + 1);
    //             for (int c = 0; c < tableModel.getColumnCount(); c++) {
    //                 Cell cell = row.createCell(c);
    //                 Object value = tableModel.getValueAt(r, c);
    //                 if (value instanceof Number) {
    //                     cell.setCellValue(((Number) value).doubleValue());
    //                 } else if (value instanceof Date) {
    //                     cell.setCellValue(value.toString());
    //                 } else {
    //                     cell.setCellValue(value != null ? value.toString() : "");
    //                 }
    //             }
    //         }

    //         try (FileOutputStream fileOut = new FileOutputStream("transactions.xlsx")) {
    //             workbook.write(fileOut);
    //         }

    //         JOptionPane.showMessageDialog(this, "Exported to transactions.xlsx");
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //         JOptionPane.showMessageDialog(this, "Error exporting to Excel: " + ex.getMessage());
    //     }
    // }

    // Function filterTable
    private void filterTable(String query) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        borrowTable.setRowSorter(sorter);
        if (query.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    // Function logout
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SwingUtilities.getWindowAncestor(this).dispose();
            new LoginForm().setVisible(true);
        }
    }
}
