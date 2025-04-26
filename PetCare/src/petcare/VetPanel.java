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

public class VetPanel extends JPanel {
    private JTextField nameField, ageField, qualificationField, searchField;
    private JComboBox<String> genderBox, specialtyBox, speciesBox;
    private JButton addButton, loadButton, updateButton, deleteButton, searchButton;

    private JTable vetTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public VetPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 245)); // Cream background

        // Fields
        nameField = new JTextField(15);
        ageField = new JTextField(5);
        qualificationField = new JTextField(15);
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        specialtyBox = new JComboBox<>(new String[]{
                "General Physician", "Internal Medicine", "Dermatology", "Dentistry",
                "Orthopedics", "Psychiatry", "Ophthalmology", "Gastroenterology"
        });
        speciesBox = new JComboBox<>(new String[]{"Dog", "Cat", "Bird", "Rabbit", "Reptile", "Other"});

        // Buttons
        addButton = createStyledButton("Add");
        loadButton = createStyledButton("Load");
        updateButton = createStyledButton("Update");
        deleteButton = createStyledButton("Delete");
        searchButton = createStyledButton("Search");

        // Table Setup
        String[] columns = {"Name", "Age", "Gender", "Qualification", "Specialty", "Species Handled"};
        tableModel = new DefaultTableModel(columns, 0);
        vetTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        vetTable.setRowSorter(sorter);

        JScrollPane tableScroll = new JScrollPane(vetTable);
        tableScroll.setPreferredSize(new Dimension(800, 200));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Vet Details"));
        formPanel.setBackground(new Color(255, 255, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; formPanel.add(nameField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1; formPanel.add(ageField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; formPanel.add(genderBox, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Qualification:"), gbc);
        gbc.gridx = 1; formPanel.add(qualificationField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Specialty:"), gbc);
        gbc.gridx = 1; formPanel.add(specialtyBox, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Species Handled:"), gbc);
        gbc.gridx = 1; formPanel.add(speciesBox, gbc);
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
        searchField.setText("Enter Name, Specialty, or Species");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Enter Name, Specialty, or Species")) {
                    searchField.setText(""); searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Enter Name, Specialty, or Species");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        bottomPanel.add(searchField);
        bottomPanel.add(searchButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> addVet());
        loadButton.addActionListener(e -> loadVets());
        updateButton.addActionListener(e -> updateVet());
        deleteButton.addActionListener(e -> deleteVet());
        searchButton.addActionListener(e -> filterVets());

        vetTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && vetTable.getSelectedRow() != -1) {
                loadSelectedVet(vetTable.convertRowIndexToModel(vetTable.getSelectedRow()));
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
        String ageStr = ageField.getText().trim();
        String qualification = qualificationField.getText().trim();

        if (name.isEmpty() || !name.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(this, "Name must contain only alphabets and cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Age cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 100) {
                JOptionPane.showMessageDialog(this, "Age must be between 1 and 100.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (qualification.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Qualification cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void addVet() {
        if (!validateInput()) return;

        String[] row = new String[]{
                nameField.getText(), ageField.getText(), (String) genderBox.getSelectedItem(),
                qualificationField.getText(), (String) specialtyBox.getSelectedItem(), (String) speciesBox.getSelectedItem()
        };

        tableModel.addRow(row);
        FileUtil.writeFile("CSV/doctors.csv", getAllTableRows());
        clearFields();
    }

    private void loadVets() {
        tableModel.setRowCount(0);
        List<String> lines = FileUtil.readFile("CSV/doctors.csv");
        for (String line : lines) {
            tableModel.addRow(line.split(",", 6));
        }
    }

    private void updateVet() {
        int viewRow = vetTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vet to update.", "Update Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = vetTable.convertRowIndexToModel(viewRow);
        if (!validateInput()) return;

        tableModel.setValueAt(nameField.getText(), modelRow, 0);
        tableModel.setValueAt(ageField.getText(), modelRow, 1);
        tableModel.setValueAt(genderBox.getSelectedItem(), modelRow, 2);
        tableModel.setValueAt(qualificationField.getText(), modelRow, 3);
        tableModel.setValueAt(specialtyBox.getSelectedItem(), modelRow, 4);
        tableModel.setValueAt(speciesBox.getSelectedItem(), modelRow, 5);

        FileUtil.writeFile("CSV/doctors.csv", getAllTableRows());
        clearFields();
    }

    private void deleteVet() {
        int viewRow = vetTable.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = vetTable.convertRowIndexToModel(viewRow);
            tableModel.removeRow(modelRow);
            FileUtil.writeFile("CSV/doctors.csv", getAllTableRows());
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a vet to delete.", "Delete Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadSelectedVet(int row) {
        nameField.setText((String) tableModel.getValueAt(row, 0));
        ageField.setText((String) tableModel.getValueAt(row, 1));
        genderBox.setSelectedItem(tableModel.getValueAt(row, 2));
        qualificationField.setText((String) tableModel.getValueAt(row, 3));
        specialtyBox.setSelectedItem(tableModel.getValueAt(row, 4));
        speciesBox.setSelectedItem(tableModel.getValueAt(row, 5));
    }

    private void filterVets() {
        String query = searchField.getText().trim();
        if (query.isEmpty() || query.equals("Enter Name, Specialty, or Species")) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Search Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        if (sorter.getViewRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No matching vet found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
        ageField.setText("");
        qualificationField.setText("");
        genderBox.setSelectedIndex(0);
        specialtyBox.setSelectedIndex(0);
        speciesBox.setSelectedIndex(0);
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
        searchField.setText("Enter Name, Specialty, or Species");
        searchField.setForeground(Color.GRAY);
        sorter.setRowFilter(null);
        tableModel.setRowCount(0);
        vetTable.clearSelection();
        clearFields();
    }
}


