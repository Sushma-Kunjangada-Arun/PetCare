/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sushmaka
 */

package petcare;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class ReceptionistPanel extends JPanel {
    private JTextField nameField, contactField, searchField;
    private JComboBox<String> genderBox;
    private JButton addButton, loadButton, updateButton, deleteButton, searchButton;

    private JTable receptionistTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public ReceptionistPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 245));

        // Fields
        nameField = new JTextField(15);
        contactField = new JTextField(15);
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});

        // Buttons
        addButton = createStyledButton("Add");
        loadButton = createStyledButton("Load");
        updateButton = createStyledButton("Update");
        deleteButton = createStyledButton("Delete");
        searchButton = createStyledButton("Search");

        // Table Setup
        String[] columns = {"Name", "Gender", "Contact"};
        tableModel = new DefaultTableModel(columns, 0);
        receptionistTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        receptionistTable.setRowSorter(sorter);
        JScrollPane tableScroll = new JScrollPane(receptionistTable);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Receptionist Details"));
        formPanel.setBackground(new Color(255, 255, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; formPanel.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; formPanel.add(genderBox, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1; formPanel.add(contactField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(addButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        // Search Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(255, 255, 245));
        searchField = new JTextField(20);
        searchField.setText("Enter Name or Contact");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Enter Name or Contact")) {
                    searchField.setText(""); searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Enter Name or Contact");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        bottomPanel.add(searchField);
        bottomPanel.add(searchButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> addReceptionist());
        loadButton.addActionListener(e -> loadReceptionists());
        updateButton.addActionListener(e -> updateReceptionist());
        deleteButton.addActionListener(e -> deleteReceptionist());
        searchButton.addActionListener(e -> filterReceptionists());

        receptionistTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && receptionistTable.getSelectedRow() != -1) {
                loadSelectedReceptionist(receptionistTable.convertRowIndexToModel(receptionistTable.getSelectedRow()));
            }
        });
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(66, 135, 245));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        return button;
    }

    private boolean validateInput() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();

        if (name.isEmpty() || !name.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(this, "Name must contain only alphabets and cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (contact.isEmpty() || !contact.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Contact must be a valid 10-digit number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void addReceptionist() {
        if (!validateInput()) return;

        String[] row = new String[]{
                nameField.getText(), (String) genderBox.getSelectedItem(), contactField.getText()
        };

        tableModel.addRow(row);
        FileUtil.writeFile("CSV/receptionists.csv", getAllTableRows());
        clearFields();
    }

    private void loadReceptionists() {
        tableModel.setRowCount(0);
        List<String> lines = FileUtil.readFile("CSV/receptionists.csv");
        for (String line : lines) {
            tableModel.addRow(line.split(",", 3));
        }
    }

    private void updateReceptionist() {
        int viewRow = receptionistTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a receptionist to update.", "Update Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = receptionistTable.convertRowIndexToModel(viewRow);
        if (!validateInput()) return;

        tableModel.setValueAt(nameField.getText(), modelRow, 0);
        tableModel.setValueAt(genderBox.getSelectedItem(), modelRow, 1);
        tableModel.setValueAt(contactField.getText(), modelRow, 2);

        FileUtil.writeFile("CSV/receptionists.csv", getAllTableRows());
        clearFields();
    }

    private void deleteReceptionist() {
        int viewRow = receptionistTable.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = receptionistTable.convertRowIndexToModel(viewRow);
            tableModel.removeRow(modelRow);
            FileUtil.writeFile("CSV/receptionists.csv", getAllTableRows());
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a receptionist to delete.", "Delete Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadSelectedReceptionist(int row) {
        nameField.setText((String) tableModel.getValueAt(row, 0));
        genderBox.setSelectedItem(tableModel.getValueAt(row, 1));
        contactField.setText((String) tableModel.getValueAt(row, 2));
    }

    private void filterReceptionists() {
        String query = searchField.getText().trim();
        if (query.isEmpty() || query.equals("Enter Name or Contact")) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Search Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        if (sorter.getViewRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No matching receptionist found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
        contactField.setText("");
        genderBox.setSelectedIndex(0);
    }

    private List<String> getAllTableRows() {
        List<String> rows = new java.util.ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                sb.append(tableModel.getValueAt(i, j));
                if (j < tableModel.getColumnCount() - 1) sb.append(",");
            }
            rows.add(sb.toString());
        }
        return rows;
    }

    public void resetPanel() {
        searchField.setText("Enter Name or Contact");
        searchField.setForeground(Color.GRAY);
        sorter.setRowFilter(null);
        tableModel.setRowCount(0);
        receptionistTable.clearSelection();
        clearFields();
    }
}

