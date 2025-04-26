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
import javax.swing.RowFilter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Map;

public class BillingPanel extends JPanel {
    private JComboBox<String> animalBox, treatmentBox, paymentMethodBox;
    private JTextField ownerField, costField, searchField;
    private JSpinner billingDateSpinner;
    private JButton generateButton, loadButton, updateButton, deleteButton, searchButton;
    private JTable billTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private final Map<String, String> treatmentCosts = Map.of(
            "Vaccination", "50", "Check-up", "30", "Surgery", "150", "Dental Care", "75", "Grooming", "40"
    );
    private final String[] paymentMethods = {"Cash", "Card", "UPI", "Insurance"};

    public BillingPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 245));

        animalBox = new JComboBox<>();
        treatmentBox = new JComboBox<>(treatmentCosts.keySet().toArray(new String[0]));
        paymentMethodBox = new JComboBox<>(paymentMethods);
        ownerField = new JTextField(15); ownerField.setEditable(false);
        costField = new JTextField(10);
        billingDateSpinner = new JSpinner(new SpinnerDateModel());
        billingDateSpinner.setEditor(new JSpinner.DateEditor(billingDateSpinner, "yyyy-MM-dd"));

        generateButton = createStyledButton("Generate");
        loadButton = createStyledButton("Load");
        updateButton = createStyledButton("Update");
        deleteButton = createStyledButton("Delete");
        searchButton = createStyledButton("Search");

        treatmentBox.addActionListener(e -> costField.setText(treatmentCosts.getOrDefault((String) treatmentBox.getSelectedItem(), "")));
        animalBox.addActionListener(e -> autoFillOwner());

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);

        String[] columns = {"Animal", "Owner", "Treatment", "Cost", "Payment", "Date"};
        tableModel = new DefaultTableModel(columns, 0);
        billTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        billTable.setRowSorter(sorter);
        JScrollPane tableScroll = new JScrollPane(billTable);
        add(tableScroll, BorderLayout.CENTER);

        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.SOUTH);

        generateButton.addActionListener(e -> generateBill());
        loadButton.addActionListener(e -> loadBills());
        updateButton.addActionListener(e -> updateBill());
        deleteButton.addActionListener(e -> deleteBill());
        searchButton.addActionListener(e -> filterBills());

        billTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && billTable.getSelectedRow() != -1) {
                int viewRow = billTable.getSelectedRow();
                int modelRow = billTable.convertRowIndexToModel(viewRow);
                loadSelectedBill(modelRow);
            }
        });

        loadAnimals();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 245));
        formPanel.setBorder(new TitledBorder("Billing Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Animal Name:"), gbc);
        gbc.gridx = 1; formPanel.add(animalBox, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Owner Name:"), gbc);
        gbc.gridx = 1; formPanel.add(ownerField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Treatment:"), gbc);
        gbc.gridx = 1; formPanel.add(treatmentBox, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Cost ($):"), gbc);
        gbc.gridx = 1; formPanel.add(costField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1; formPanel.add(paymentMethodBox, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Billing Date:"), gbc);
        gbc.gridx = 1; formPanel.add(billingDateSpinner, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(generateButton);
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
        searchField.setText("Enter Animal, Owner, or Treatment");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Enter Animal, Owner, or Treatment")) {
                    searchField.setText(""); searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Enter Animal, Owner, or Treatment");
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
        button.setBackground(new Color(66, 135, 245));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        return button;
    }

    private void loadAnimals() {
        animalBox.removeAllItems();
        List<String> lines = FileUtil.readFile("CSV/animal_patients.csv");
        for (String line : lines) {
            String[] parts = line.split(",", 2);
            if (parts.length > 0) animalBox.addItem(parts[0]);
        }
    }

    private void autoFillOwner() {
        String selectedAnimal = (String) animalBox.getSelectedItem();
        List<String> lines = FileUtil.readFile("CSV/animal_patients.csv");
        for (String line : lines) {
            String[] parts = line.split(",", 8);
            if (parts.length >= 6 && parts[0].equalsIgnoreCase(selectedAnimal)) {
                ownerField.setText(parts[5]);
                break;
            }
        }
    }

    private void generateBill() {
        if (!validateInput()) return;
        String[] row = getFormData();
        tableModel.addRow(row);
        FileUtil.writeFile("CSV/bills.csv", getAllTableRows());
        clearFields();
    }

    private void loadBills() {
        tableModel.setRowCount(0);  // Clear existing rows
        List<String> lines = FileUtil.readFile("CSV/bills.csv");
        for (String line : lines) {
            String[] data = line.split(",", 6);
            if (data.length == 6) {
                tableModel.addRow(data);
            } else {
                System.err.println("Invalid row format: " + line);
            }
        }
    }


    private void updateBill() {
        int viewRow = billTable.getSelectedRow();
        if (viewRow != -1 && validateInput()) {
            int modelRow = billTable.convertRowIndexToModel(viewRow);
            String[] rowData = getFormData();
            for (int i = 0; i < rowData.length; i++) {
                tableModel.setValueAt(rowData[i], modelRow, i);
            }
            FileUtil.writeFile("CSV/bills.csv", getAllTableRows());
            clearFields();
        } else {
            showError("Please select a bill to update.");
        }
    }

    private void deleteBill() {
        int viewRow = billTable.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = billTable.convertRowIndexToModel(viewRow);
            tableModel.removeRow(modelRow);
            FileUtil.writeFile("CSV/bills.csv", getAllTableRows());
            clearFields();
        } else {
            showError("Please select a bill to delete.");
        }
    }

    private void loadSelectedBill(int modelRow) {
        animalBox.setSelectedItem(tableModel.getValueAt(modelRow, 0));
        ownerField.setText((String) tableModel.getValueAt(modelRow, 1));
        treatmentBox.setSelectedItem(tableModel.getValueAt(modelRow, 2));
        costField.setText((String) tableModel.getValueAt(modelRow, 3));
        paymentMethodBox.setSelectedItem(tableModel.getValueAt(modelRow, 4));
        try {
            billingDateSpinner.setValue(new SimpleDateFormat("yyyy-MM-dd").parse((String) tableModel.getValueAt(modelRow, 5)));
        } catch (Exception e) {
            billingDateSpinner.setValue(new Date());
        }
    }

    private boolean validateInput() {
        if (!costField.getText().matches("\\d+(\\.\\d{1,2})?")) {
            showError("Cost must be a valid number.");
            return false;
        }
        return true;
    }

    private void filterBills() {
        String query = searchField.getText().trim();
        if (query.isEmpty() || query.equals("Enter Animal, Owner, or Treatment")) {
            showError("Please enter a search term.");
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        if (sorter.getViewRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No matching bill found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String[] getFormData() {
        return new String[]{
                (String) animalBox.getSelectedItem(),
                ownerField.getText(),
                (String) treatmentBox.getSelectedItem(),
                costField.getText(),
                (String) paymentMethodBox.getSelectedItem(),
                new SimpleDateFormat("yyyy-MM-dd").format(billingDateSpinner.getValue())
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
        searchField.setText("Enter Animal, Owner, or Treatment");
        searchField.setForeground(Color.GRAY);
        sorter.setRowFilter(null);
        tableModel.setRowCount(0);
        billTable.clearSelection();
        clearFields();
        loadAnimals();
    }
    
    private void clearFields() {
        animalBox.setSelectedIndex(0);
        treatmentBox.setSelectedIndex(0);
        costField.setText("");
        paymentMethodBox.setSelectedIndex(0);
        billingDateSpinner.setValue(new Date());
        ownerField.setText("");
    }
}
