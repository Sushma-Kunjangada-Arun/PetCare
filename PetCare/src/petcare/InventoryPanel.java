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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private JTextField itemNameField, restockedQtyField, availableQtyField, supplierField, unitPriceField, batchNumberField, searchField;
    private JComboBox<String> categoryBox;
    private JSpinner restockDateSpinner;
    private JButton addButton, loadButton, updateButton, deleteButton, searchButton;

    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private final String[] categories = {"tablets", "injections", "equipment", "creams", "ointment", "gels"};

    public InventoryPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 245)); // Cream background

        itemNameField = new JTextField(15);
        categoryBox = new JComboBox<>(categories);

        restockedQtyField = new JTextField(10);
        availableQtyField = new JTextField(10);
        supplierField = new JTextField(15);
        unitPriceField = new JTextField(10);
        batchNumberField = new JTextField(15);

        restockDateSpinner = new JSpinner(new SpinnerDateModel());
        restockDateSpinner.setEditor(new JSpinner.DateEditor(restockDateSpinner, "yyyy-MM-dd"));

        addButton = createStyledButton("Add");
        loadButton = createStyledButton("Load");
        updateButton = createStyledButton("Update");
        deleteButton = createStyledButton("Delete");
        searchButton = createStyledButton("Search");

        // Form Panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"Item Name", "Category", "Restocked Qty", "Available Qty", "Supplier", "Restock Date", "Unit Price", "Batch Number"};
        tableModel = new DefaultTableModel(columns, 0);
        inventoryTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        inventoryTable.setRowSorter(sorter);
        JScrollPane tableScroll = new JScrollPane(inventoryTable);
        tableScroll.setPreferredSize(new Dimension(800, 250));
        add(tableScroll, BorderLayout.CENTER);

        // Search Panel
        JPanel bottomPanel = createSearchPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> addItem());
        loadButton.addActionListener(e -> loadItems());
        updateButton.addActionListener(e -> updateItem());
        deleteButton.addActionListener(e -> deleteItem());
        searchButton.addActionListener(e -> filterItems());

        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && inventoryTable.getSelectedRow() != -1) {
                loadSelectedItem(inventoryTable.convertRowIndexToModel(inventoryTable.getSelectedRow()));
            }
        });
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Inventory Details"));
        formPanel.setBackground(new Color(255, 255, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Item Name:"), gbc);
        gbc.gridx = 1; formPanel.add(itemNameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; formPanel.add(categoryBox, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Qty Being Restocked:"), gbc);
        gbc.gridx = 1; formPanel.add(restockedQtyField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Qty Available:"), gbc);
        gbc.gridx = 1; formPanel.add(availableQtyField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Supplier:"), gbc);
        gbc.gridx = 1; formPanel.add(supplierField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Date of Last Restock:"), gbc);
        gbc.gridx = 1; formPanel.add(restockDateSpinner, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Unit Price:"), gbc);
        gbc.gridx = 1; formPanel.add(unitPriceField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Batch Number:"), gbc);
        gbc.gridx = 1; formPanel.add(batchNumberField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.setBackground(new Color(255, 255, 245));
        buttonPanel.add(addButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(255, 255, 245));
        searchField = new JTextField(20);
        searchField.setText("Enter Item Name, Category, or Supplier");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Enter Item Name, Category, or Supplier")) {
                    searchField.setText(""); searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Enter Item Name, Category, or Supplier");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        panel.add(searchField);
        panel.add(searchButton);
        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(66, 135, 245)); button.setForeground(Color.WHITE);
        button.setFocusPainted(false); button.setFont(new Font("SansSerif", Font.BOLD, 13));
        return button;
    }

    private boolean validateInput() {
        if (itemNameField.getText().trim().isEmpty()) {
            showError("Item Name cannot be empty."); return false;
        }
        if (!restockedQtyField.getText().matches("\\d+")) {
            showError("Qty Being Restocked must be a whole number."); return false;
        }
        if (!availableQtyField.getText().matches("\\d+")) {
            showError("Qty Available must be a whole number."); return false;
        }
        if (supplierField.getText().trim().isEmpty()) {
            showError("Supplier cannot be empty."); return false;
        }
        if (!unitPriceField.getText().matches("\\d+(\\.\\d{1,2})?")) {
            showError("Unit Price must be a valid number."); return false;
        }
        if (batchNumberField.getText().trim().isEmpty()) {
            showError("Batch Number cannot be empty."); return false;
        }
        return true;
    }

    private void addItem() {
        if (!validateInput()) return;
        String[] row = getFormData();
        tableModel.addRow(row);
        FileUtil.writeFile("CSV/inventory.csv", getAllTableRows());
        clearFields();
    }

    private void updateItem() {
        int viewRow = inventoryTable.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = inventoryTable.convertRowIndexToModel(viewRow);
            if (!validateInput()) return;
            String[] row = getFormData();
            for (int i = 0; i < row.length; i++) {
                tableModel.setValueAt(row[i], modelRow, i);
            }
            FileUtil.writeFile("CSV/inventory.csv", getAllTableRows());
            clearFields();
        } else {
            showError("Please select an item to update.");
        }
    }

    private void deleteItem() {
        int viewRow = inventoryTable.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = inventoryTable.convertRowIndexToModel(viewRow);
            tableModel.removeRow(modelRow);
            FileUtil.writeFile("CSV/inventory.csv", getAllTableRows());
            clearFields();
        } else {
            showError("Please select an item to delete.");
        }
    }

    private void loadItems() {
        try {
            tableModel.setRowCount(0);
            List<String> lines = FileUtil.readFile("CSV/inventory.csv");
            for (String line : lines) {
                String[] data = line.split(",", 8);
                if (data.length == 8) tableModel.addRow(data);
            }
        } catch (Exception e) {
            showError("Failed to load inventory: " + e.getMessage());
        }
    }

    private void loadSelectedItem(int modelRow) {
        itemNameField.setText((String) tableModel.getValueAt(modelRow, 0));
        categoryBox.setSelectedItem(tableModel.getValueAt(modelRow, 1));
        restockedQtyField.setText((String) tableModel.getValueAt(modelRow, 2));
        availableQtyField.setText((String) tableModel.getValueAt(modelRow, 3));
        supplierField.setText((String) tableModel.getValueAt(modelRow, 4));
        try {
            restockDateSpinner.setValue(new SimpleDateFormat("yyyy-MM-dd").parse((String) tableModel.getValueAt(modelRow, 5)));
        } catch (Exception e) {
            restockDateSpinner.setValue(new Date());
        }
        unitPriceField.setText((String) tableModel.getValueAt(modelRow, 6));
        batchNumberField.setText((String) tableModel.getValueAt(modelRow, 7));
    }

    private void filterItems() {
        String query = searchField.getText().trim();
        if (query.isEmpty() || query.equals("Enter Item Name, Category, or Supplier")) {
            showError("Please enter a search term.");
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        if (sorter.getViewRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No matching item found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearFields() {
        itemNameField.setText("");
        restockedQtyField.setText("");
        availableQtyField.setText("");
        supplierField.setText("");
        unitPriceField.setText("");
        batchNumberField.setText("");
        categoryBox.setSelectedIndex(0);
        restockDateSpinner.setValue(new Date());
    }

    private String[] getFormData() {
        return new String[]{
            itemNameField.getText().trim(),
            (String) categoryBox.getSelectedItem(),
            restockedQtyField.getText(),
            availableQtyField.getText(),
            supplierField.getText(),
            new SimpleDateFormat("yyyy-MM-dd").format(restockDateSpinner.getValue()),
            unitPriceField.getText(),
            batchNumberField.getText()
        };
    }

    private List<String> getAllTableRows() {
        List<String> rows = new ArrayList<>();
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

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public void resetPanel() {
        searchField.setText("Enter Item Name, Category, or Supplier");
        searchField.setForeground(Color.GRAY);
        sorter.setRowFilter(null);
        tableModel.setRowCount(0);
        inventoryTable.clearSelection();
        clearFields();
    }
}
