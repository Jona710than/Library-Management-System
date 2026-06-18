package com.mycompany.project;

import db.DBConnection;
import utils.OpenLibrarySearch;
import utils.OpenLibrarySearch.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class StudentPanel extends JPanel {
    private final int studentId;
    private final String studentName;
    private JTable bookTable, borrowedTable;
    private DefaultTableModel bookModel, borrowedModel;
    private JTextField searchField;
    private JButton requestBtn, addBtn, logoutBtn, returnBtn;

    public StudentPanel(int userId) {
        this.studentId = userId;
        this.studentName = fetchStudentName(userId);

        setLayout(new BorderLayout());

        // Sidebar containing hellolabel,name, and logoutbutton
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel helloLabel = new JLabel("Hello,");
        helloLabel.setFont(new Font("Georgia", Font.BOLD, 20));
        helloLabel.setForeground(Color.WHITE);
        helloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(studentName.toUpperCase());
        nameLabel.setFont(new Font("Georgia", Font.PLAIN, 18));
        nameLabel.setForeground(Color.LIGHT_GRAY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 25, 0));

        logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, new Color(192, 57, 43));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            JFrame top = (JFrame) SwingUtilities.getWindowAncestor(this);
            top.dispose();
            new LoginForm().setVisible(true);
        });

        sidebar.add(helloLabel);
        sidebar.add(nameLabel);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutBtn);
        add(sidebar, BorderLayout.WEST);

        //Main Panel containing  searchpanel and splitpanel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel, BorderLayout.CENTER);

        // Search Section 
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField("Search...");
        searchField.setForeground(Color.GRAY);
        searchField.setFont(new Font("Georgia", Font.PLAIN, 14));
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

        JButton localSearchBtn = new JButton("Search Local");
        JButton openSearchBtn = new JButton("Search OpenLibrary");
        addBtn = new JButton("Add to Library");

        styleButton(localSearchBtn, new Color(52, 152, 219));
        styleButton(openSearchBtn, new Color(41, 128, 185));
        styleButton(addBtn, new Color(39, 174, 96));

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        searchButtons.add(localSearchBtn);
        searchButtons.add(openSearchBtn);
        searchButtons.add(addBtn);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButtons, BorderLayout.EAST);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Book Table 
        bookModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Publisher", "ISBN", "Category", "Available"}, 0);
        bookTable = new JTable(bookModel);
        JScrollPane bookScrollPane = new JScrollPane(bookTable);

        requestBtn = new JButton("Request Borrow");
        styleButton(requestBtn, new Color(155, 89, 182));

        JPanel bookPanel = new JPanel(new BorderLayout(5, 5));
        bookPanel.setBorder(BorderFactory.createTitledBorder("Available Books"));
        bookPanel.add(bookScrollPane, BorderLayout.CENTER);
        bookPanel.add(requestBtn, BorderLayout.SOUTH);

        // Borrowed books Table 
        borrowedModel = new DefaultTableModel(new String[]{"Book", "Borrow Date", "Due Date", "Return Date", "Fine"}, 0);
        borrowedTable = new JTable(borrowedModel);
        JScrollPane borrowedScrollPane = new JScrollPane(borrowedTable);

        returnBtn = new JButton("Return Book");
        styleButton(returnBtn, new Color(241, 196, 15));

        JPanel borrowedPanel = new JPanel(new BorderLayout(5, 5));
        borrowedPanel.setBorder(BorderFactory.createTitledBorder("My Borrowed Books"));
        borrowedPanel.add(borrowedScrollPane, BorderLayout.CENTER);
        borrowedPanel.add(returnBtn, BorderLayout.SOUTH);

        //Split main content 
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, bookPanel, borrowedPanel);
        splitPane.setDividerLocation(250);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        //  Event Listeners 
        localSearchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.equalsIgnoreCase("Search...")) keyword = "";
            loadAvailableBooks(keyword);
        });

        openSearchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.equalsIgnoreCase("Search...")) keyword = "";
            searchOpenLibrary(keyword);
        });

        addBtn.addActionListener(e -> addBookToLocal());
        requestBtn.addActionListener(e -> requestBorrow());
        returnBtn.addActionListener(e -> returnBook());

        loadAvailableBooks("");
        loadMyBorrowedBooks();
    }

    private void styleButton(JButton button, Color bg) {
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Georgia", Font.PLAIN, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // Function that get student name
    private String fetchStudentName(int userId) {
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
        return "Student";
    }

    // Function loadAvailableBooks
    private void loadAvailableBooks(String keyword) {
        bookModel.setRowCount(0);
        String sql = """
            SELECT * FROM books
            WHERE availability = true
              AND (title LIKE ? OR author LIKE ? OR category LIKE ? OR isbn LIKE ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String kw = "%" + keyword + "%";
            for (int i = 1; i <= 4; i++) stmt.setString(i, kw);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookModel.addRow(new Object[]{
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
            JOptionPane.showMessageDialog(this, "Error loading books: " + ex.getMessage());
        }
    }

    // Function searchOpenLibrary
    private void searchOpenLibrary(String keyword) {
        bookModel.setRowCount(0);
        ArrayList<Book> results = OpenLibrarySearch.searchBooks(keyword);

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No results found in Open Library.");
            return;
        }

        for (Book b : results) {
            bookModel.addRow(new Object[]{
                    "-", b.title, b.author, b.publisher, b.isbn, "-", false
            });
        }

        JOptionPane.showMessageDialog(this, "Use 'Add to Library' to borrow books from Open Library.");
    }

    // Function addBookToLocal
    private void addBookToLocal() {
        int row = bookTable.getSelectedRow();
        if (row == -1 || !bookModel.getValueAt(row, 0).toString().equals("-")) {
            JOptionPane.showMessageDialog(this, "Please select a book from Open Library.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO books (title, author, publisher, isbn, category, availability) VALUES (?, ?, ?, ?, ?, true)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bookModel.getValueAt(row, 1).toString());
            stmt.setString(2, bookModel.getValueAt(row, 2).toString());
            stmt.setString(3, bookModel.getValueAt(row, 3).toString());
            stmt.setString(4, bookModel.getValueAt(row, 4).toString());
            stmt.setString(5, "General");

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Book added successfully.");
            loadAvailableBooks("");

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Add failed: " + ex.getMessage());
        }
    }

    // Function requestBorrow
    private void requestBorrow() {
        int row = bookTable.getSelectedRow();
        if (row == -1 || bookModel.getValueAt(row, 0).equals("-")) {
            JOptionPane.showMessageDialog(this, "Cannot borrow Open Library book directly.");
            return;
        }

        int bookId = (int) bookModel.getValueAt(row, 0);
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(7);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO transactions (userID, bookID, borrow_date, due_date) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            stmt.setInt(2, bookId);
            stmt.setDate(3, Date.valueOf(borrowDate));
            stmt.setDate(4, Date.valueOf(dueDate));
            stmt.executeUpdate();

            conn.prepareStatement("UPDATE books SET availability = false WHERE bookID = " + bookId).executeUpdate();
            JOptionPane.showMessageDialog(this, "Book borrowed!");
            loadAvailableBooks("");
            loadMyBorrowedBooks();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Borrow failed: " + e.getMessage());
        }
    }

    // Function returnBook
    private void returnBook() {
        int row = borrowedTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a borrowed book to return.");
            return;
        }

        String title = borrowedModel.getValueAt(row, 0).toString();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement bookStmt = conn.prepareStatement("SELECT bookID FROM books WHERE title = ?");
            bookStmt.setString(1, title);
            ResultSet rs = bookStmt.executeQuery();

            if (rs.next()) {
                int bookId = rs.getInt("bookID");

                PreparedStatement returnStmt = conn.prepareStatement("""
                    UPDATE transactions SET return_date = CURRENT_DATE 
                    WHERE bookID = ? AND userID = ? AND return_date IS NULL
                """);
                returnStmt.setInt(1, bookId);
                returnStmt.setInt(2, studentId);
                returnStmt.executeUpdate();

                conn.prepareStatement("UPDATE books SET availability = true WHERE bookID = " + bookId).executeUpdate();

                JOptionPane.showMessageDialog(this, "Book returned successfully.");
                loadAvailableBooks("");
                loadMyBorrowedBooks();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Return failed: " + e.getMessage());
        }
    }

    // Function loadMyBorrowedBooks
    private void loadMyBorrowedBooks() {
        borrowedModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT b.title, t.borrow_date, t.due_date, t.return_date, t.fine_amount
                FROM transactions t JOIN books b ON t.bookID = b.bookID
                WHERE t.userID = ?
            """);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                borrowedModel.addRow(new Object[]{
                        rs.getString("title"),
                        rs.getDate("borrow_date"),
                        rs.getDate("due_date"),
                        rs.getDate("return_date"),
                        rs.getDouble("fine_amount")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
