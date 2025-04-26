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
import java.util.List;

public class AnimalPatientPanel extends JPanel {
    private JComboBox<String> speciesBox, genderBox;
    private JTextField nameField, breedField, ageField, ownerField, insuranceField, searchField;
    private JTextArea historyArea;
    private JButton addButton, loadButton, updateButton, deleteButton, searchButton;

    private JTable patientTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public AnimalPatientPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 245)); // Cream background

        // Fields
        nameField = new JTextField(15);
        breedField = new JTextField(15);
        ageField = new JTextField(5);
        ownerField = new JTextField(15);
        insuranceField = new JTextField(15);
        speciesBox = new JComboBox<>(new String[]{"Dog", "Cat", "Bird", "Rabbit", "Reptile", "Other"});
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        historyArea = new JTextArea(3, 15);
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setPreferredSize(new Dimension(200, 60));

        // Buttons
        addButton = createStyledButton("Add");
        loadButton = createStyledButton("Load");
        updateButton = createStyledButton("Update");
        deleteButton = createStyledButton("Delete");
        searchButton = createStyledButton("Search");

        // Table Setup
        String[] columns = {"Name", "Species", "Breed", "Age", "Gender", "Owner", "History", "Insurance"};
        tableModel = new DefaultTableModel(columns, 0);
        patientTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        patientTable.setRowSorter(sorter);

        JScrollPane tableScroll = new JScrollPane(patientTable);
        tableScroll.setPreferredSize(new Dimension(800, 200));

        // Form Panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        // Search Panel
        JPanel bottomPanel = createSearchPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Actions
        addButton.addActionListener(e -> addPatient());
        loadButton.addActionListener(e -> loadPatients());
        updateButton.addActionListener(e -> updatePatient());
        deleteButton.addActionListener(e -> deletePatient());
        searchButton.addActionListener(e -> filterPatients());

        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && patientTable.getSelectedRow() != -1) {
                loadSelectedPatient(patientTable.convertRowIndexToModel(patientTable.getSelectedRow()));
            }
        });
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Animal Patient Details"));
        formPanel.setBackground(new Color(255, 255, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Animal Name:"), gbc);
        gbc.gridx = 1; formPanel.add(nameField, gbc); row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Species:"), gbc);
        gbc.gridx = 1; formPanel.add(speciesBox, gbc); row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Breed:"), gbc);
        gbc.gridx = 1; formPanel.add(breedField, gbc); row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1; formPanel.add(ageField, gbc); row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; formPanel.add(genderBox, gbc); row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Owner Name:"), gbc);
        gbc.gridx = 1; formPanel.add(ownerField, gbc); row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Medical History:"), gbc);
        gbc.gridx = 1; formPanel.add(new JScrollPane(historyArea), gbc); row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Insurance Number:"), gbc);
        gbc.gridx = 1; formPanel.add(insuranceField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.setBackground(new Color(255, 255, 245));
        buttonPanel.add(addButton); buttonPanel.add(loadButton);
        buttonPanel.add(updateButton); buttonPanel.add(deleteButton);
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    private JPanel createSearchPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(255, 255, 245));
        searchField = new JTextField(20);
        searchField.setText("Enter Name, Species, or Owner");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Enter Name, Species, or Owner")) {
                    searchField.setText(""); searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Enter Name, Species, or Owner");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        bottomPanel.add(searchField);
        bottomPanel.add(searchButton);
        return bottomPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(66, 135, 245)); button.setForeground(Color.WHITE);
        button.setFocusPainted(false); button.setFont(new Font("SansSerif", Font.BOLD, 13));
        return button;
    }

    private boolean validateInput() {
        String name = nameField.getText().trim();
        String breed = breedField.getText().trim();
        String owner = ownerField.getText().trim();
        String ageStr = ageField.getText().trim();

        if (!name.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(this, "Animal Name must contain only alphabets.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!breed.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(this, "Breed must contain only alphabets.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!owner.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(this, "Owner Name must contain only alphabets.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            int age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 100) {
                JOptionPane.showMessageDialog(this, "Age must be a whole number between 1 and 100.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid whole number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void addPatient() {
        if (!validateInput()) return;
        String[] row = new String[]{
                nameField.getText(), (String) speciesBox.getSelectedItem(), breedField.getText(),
                ageField.getText(), (String) genderBox.getSelectedItem(), ownerField.getText(),
                historyArea.getText().replace(",", " "), insuranceField.getText()
        };
        tableModel.addRow(row);
        FileUtil.writeFile("CSV/animal_patients.csv", getAllTableRows());
        clearFields();
    }

    private void loadPatients() {
        try {
            tableModel.setRowCount(0);
            List<String> lines = FileUtil.readFile("CSV/animal_patients.csv");
            for (String line : lines) {
                String[] data = line.split(",", 8);
                if (data.length == 8) tableModel.addRow(data);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load patients: " + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePatient() {
        int viewRow = patientTable.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = patientTable.convertRowIndexToModel(viewRow);
            if (!validateInput()) return;

            tableModel.setValueAt(nameField.getText(), modelRow, 0);
            tableModel.setValueAt(speciesBox.getSelectedItem(), modelRow, 1);
            tableModel.setValueAt(breedField.getText(), modelRow, 2);
            tableModel.setValueAt(ageField.getText(), modelRow, 3);
            tableModel.setValueAt(genderBox.getSelectedItem(), modelRow, 4);
            tableModel.setValueAt(ownerField.getText(), modelRow, 5);
            tableModel.setValueAt(historyArea.getText().replace(",", " "), modelRow, 6);
            tableModel.setValueAt(insuranceField.getText(), modelRow, 7);

            FileUtil.writeFile("CSV/animal_patients.csv", getAllTableRows());
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a patient to update.", "Update Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deletePatient() {
        int viewRow = patientTable.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = patientTable.convertRowIndexToModel(viewRow);
            tableModel.removeRow(modelRow);
            FileUtil.writeFile("CSV/animal_patients.csv", getAllTableRows());
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete.", "Delete Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadSelectedPatient(int modelRow) {
        nameField.setText((String) tableModel.getValueAt(modelRow, 0));
        speciesBox.setSelectedItem(tableModel.getValueAt(modelRow, 1));
        breedField.setText((String) tableModel.getValueAt(modelRow, 2));
        ageField.setText((String) tableModel.getValueAt(modelRow, 3));
        genderBox.setSelectedItem(tableModel.getValueAt(modelRow, 4));
        ownerField.setText((String) tableModel.getValueAt(modelRow, 5));
        historyArea.setText((String) tableModel.getValueAt(modelRow, 6));
        insuranceField.setText((String) tableModel.getValueAt(modelRow, 7));
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

    private void filterPatients() {
        String query = searchField.getText().trim();
        if (query.isEmpty() || query.equals("Enter Name, Species, or Owner")) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Search Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        if (sorter.getViewRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No matching patient found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void clearFields() {
        nameField.setText("");
        breedField.setText("");
        ageField.setText("");
        ownerField.setText("");
        insuranceField.setText("");
        historyArea.setText("");
        speciesBox.setSelectedIndex(0);
        genderBox.setSelectedIndex(0);
    }
    
    public void resetPanel() {
        // Clear search field
        searchField.setText("Enter Name, Species, or Owner");
        searchField.setForeground(Color.GRAY);

        // Clear filter
        sorter.setRowFilter(null);

        // Clear table rows
        tableModel.setRowCount(0);

        // Clear selected row (if any)
        patientTable.clearSelection();

        // Clear form fields
        clearFields();
    }

}
