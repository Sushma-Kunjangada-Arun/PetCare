package petcare;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class PetAppointmentPanel extends JPanel {
    private JComboBox<String> animalBox, problemBox, vetBox;
    private JSpinner dateSpinner, timeSpinner;
    private JTextField searchField;
    private JButton bookButton, loadButton, updateButton, deleteButton, searchButton;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private final Map<String, String> problemToSpecialty = new LinkedHashMap<>() {{
        put("Vaccination", "Internal Medicine");
        put("Dental Issue", "Dentistry");
        put("Fracture", "Orthopedics");
        put("Skin Allergy", "Dermatology");
        put("Behavioral Issue", "Psychiatry");
        put("Eye Problem", "Ophthalmology");
        put("Stomach Issue", "Gastroenterology");
    }};

    public PetAppointmentPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 245));

        animalBox = new JComboBox<>();
        problemBox = new JComboBox<>(problemToSpecialty.keySet().toArray(new String[0]));
        vetBox = new JComboBox<>();

        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        timeSpinner = new JSpinner(new SpinnerDateModel());
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));

        bookButton = createStyledButton("Book");
        loadButton = createStyledButton("Load");
        updateButton = createStyledButton("Update");
        deleteButton = createStyledButton("Delete");
        searchButton = createStyledButton("Search");

        problemBox.addActionListener(e -> filterVets());
        animalBox.addActionListener(e -> filterVets());

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"Animal", "Owner", "Species", "Problem", "Vet", "Specialty", "Date", "Time"};
        tableModel = new DefaultTableModel(columns, 0);
        appointmentTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        appointmentTable.setRowSorter(sorter);
        JScrollPane tableScroll = new JScrollPane(appointmentTable);
        add(tableScroll, BorderLayout.CENTER);

        appointmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && appointmentTable.getSelectedRow() != -1) {
                loadSelectedAppointment(appointmentTable.convertRowIndexToModel(appointmentTable.getSelectedRow()));
            }
        });

        // Bottom Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(255, 255, 245));

        searchField = new JTextField(20);
        searchField.setText("Search by Animal Name, Vet, or Date");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Search by Animal Name, Vet, or Date")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Search by Animal Name, Vet, or Date");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.SOUTH);

        // Button Actions
        bookButton.addActionListener(e -> bookAppointment());
        loadButton.addActionListener(e -> loadAppointments());
        updateButton.addActionListener(e -> updateAppointment());
        deleteButton.addActionListener(e -> deleteAppointment());
        searchButton.addActionListener(e -> filterAppointments());

        loadAnimals();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Schedule Appointment"));
        formPanel.setBackground(new Color(255, 255, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Animal:"), gbc);
        gbc.gridx = 1; formPanel.add(animalBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Problem:"), gbc);
        gbc.gridx = 1; formPanel.add(problemBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Vet:"), gbc);
        gbc.gridx = 1; formPanel.add(vetBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1; formPanel.add(dateSpinner, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Time:"), gbc);
        gbc.gridx = 1; formPanel.add(timeSpinner, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        buttonPanel.add(bookButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        formPanel.add(buttonPanel, gbc);

        return formPanel;
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
        List<String> lines = FileUtil.readFile("CSV/animal_patients.csv");
        animalBox.removeAllItems();
        for (String line : lines) {
            String[] parts = line.split(",", 2);
            if (parts.length > 0) {
                animalBox.addItem(parts[0]);
            }
        }
    }

    private String loadSpeciesForAnimal(String animalName) {
        List<String> animals = FileUtil.readFile("CSV/animal_patients.csv");
        for (String line : animals) {
            String[] parts = line.split(",", 3);
            if (parts.length >= 2 && parts[0].equalsIgnoreCase(animalName)) {
                return parts[1];
            }
        }
        return null;
    }

    private String loadOwnerForAnimal(String animalName) {
        List<String> animals = FileUtil.readFile("CSV/animal_patients.csv");
        for (String line : animals) {
            String[] parts = line.split(",", -1);
            if (parts.length >= 6 && parts[0].equalsIgnoreCase(animalName)) {
                return parts[5].trim();
            }
        }
        return "";
    }

    private void filterVets() {
        String selectedProblem = (String) problemBox.getSelectedItem();
        String specialty = problemToSpecialty.getOrDefault(selectedProblem, "");
        List<String> vets = FileUtil.readFile("CSV/doctors.csv");
        vetBox.removeAllItems();
        for (String line : vets) {
            String[] parts = line.split(",");
            if (parts.length >= 6 && parts[4].trim().equalsIgnoreCase(specialty)) {
                vetBox.addItem(parts[0].trim());
            }
        }
    }

    private boolean validateInput() {
        if (animalBox.getSelectedItem() == null || problemBox.getSelectedItem() == null || vetBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        Date dateTime = getCombinedDateTime();
        if (dateTime.before(new Date())) {
            JOptionPane.showMessageDialog(this, "Cannot book an appointment in the past.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void bookAppointment() {
        if (!validateInput()) return;
        String[] row = getAppointmentData();
        tableModel.addRow(row);
        FileUtil.writeFile("CSV/appointments.csv", getAllTableRows());
        clearFields();
    }

    private void loadAppointments() {
        tableModel.setRowCount(0);
        List<String> lines = FileUtil.readFile("CSV/appointments.csv");
        for (String line : lines) {
            String[] parts = line.split(",", 8);
            if (parts.length == 8) {
                tableModel.addRow(parts);
            }
        }
    }

    private void updateAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow != -1 && validateInput()) {
            int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
            String[] row = getAppointmentData();
            for (int i = 0; i < row.length; i++) {
                tableModel.setValueAt(row[i], modelRow, i);
            }
            FileUtil.writeFile("CSV/appointments.csv", getAllTableRows());
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an appointment to update.", "Update Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
            tableModel.removeRow(modelRow);
            FileUtil.writeFile("CSV/appointments.csv", getAllTableRows());
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an appointment to delete.", "Delete Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void filterAppointments() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        if (appointmentTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No matching appointments found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadSelectedAppointment(int modelRow) {
        animalBox.setSelectedItem(tableModel.getValueAt(modelRow, 0));
        problemBox.setSelectedItem(tableModel.getValueAt(modelRow, 3));
        filterVets();
        vetBox.setSelectedItem(tableModel.getValueAt(modelRow, 4));
        try {
            dateSpinner.setValue(new SimpleDateFormat("yyyy-MM-dd").parse((String) tableModel.getValueAt(modelRow, 6)));
            timeSpinner.setValue(new SimpleDateFormat("HH:mm").parse((String) tableModel.getValueAt(modelRow, 7)));
        } catch (Exception e) {
            dateSpinner.setValue(new Date());
            timeSpinner.setValue(new Date());
        }
    }

    private Date getCombinedDateTime() {
        Calendar date = Calendar.getInstance();
        date.setTime((Date) dateSpinner.getValue());
        Calendar time = Calendar.getInstance();
        time.setTime((Date) timeSpinner.getValue());
        date.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        date.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTime();
    }

    private String[] getAppointmentData() {
        Date dateTime = getCombinedDateTime();
        return new String[]{
                (String) animalBox.getSelectedItem(),
                loadOwnerForAnimal((String) animalBox.getSelectedItem()),
                loadSpeciesForAnimal((String) animalBox.getSelectedItem()),
                (String) problemBox.getSelectedItem(),
                (String) vetBox.getSelectedItem(),
                problemToSpecialty.getOrDefault((String) problemBox.getSelectedItem(), ""),
                new SimpleDateFormat("yyyy-MM-dd").format(dateTime),
                new SimpleDateFormat("HH:mm").format(dateTime)
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

    private void clearFields() {
        if (animalBox.getItemCount() > 0) animalBox.setSelectedIndex(0);
        if (problemBox.getItemCount() > 0) problemBox.setSelectedIndex(0);
        filterVets();
        if (vetBox.getItemCount() > 0) vetBox.setSelectedIndex(0);
        dateSpinner.setValue(new Date());
        timeSpinner.setValue(new Date());
        searchField.setText("Search by Animal Name, Vet Name, or Appointment Date");
        searchField.setForeground(Color.GRAY);
        sorter.setRowFilter(null);
    }

    public void resetPanel() {
        tableModel.setRowCount(0);  // Clear all appointments
        if (animalBox.getItemCount() > 0) animalBox.setSelectedIndex(0);
        if (problemBox.getItemCount() > 0) problemBox.setSelectedIndex(0);
        filterVets();
        if (vetBox.getItemCount() > 0) vetBox.setSelectedIndex(0);
        dateSpinner.setValue(new Date());
        timeSpinner.setValue(new Date());
        searchField.setText("Search by Animal Name, Vet Name, or Appointment Date");
        searchField.setForeground(Color.GRAY);
        sorter.setRowFilter(null);
    }
}
