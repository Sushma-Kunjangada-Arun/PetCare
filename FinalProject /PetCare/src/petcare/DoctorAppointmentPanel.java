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
import java.util.*;
import java.util.List;

public class DoctorAppointmentPanel extends JPanel {
    private JComboBox<String> doctorNameBox, dateBox;
    private JButton loadButton, searchButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private String loggedInDoctorName = null;
    private boolean isDoctorMode = false;

    // Constructor for Admin
    public DoctorAppointmentPanel() {
        this.isDoctorMode = false;
        initComponents();
        loadFilterOptions();
    }

    // Constructor for Doctor
    public DoctorAppointmentPanel(String doctorName) {
        this.loggedInDoctorName = doctorName;
        this.isDoctorMode = true;
        initComponents();
        loadFilterOptions();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 250, 255));

        doctorNameBox = new JComboBox<>();
        dateBox = new JComboBox<>();
        loadButton = createStyledButton("Load All");
        searchButton = createStyledButton("Search");

        doctorNameBox.setEditable(true);
        dateBox.setEditable(true);

        String[] columns = {"Animal", "Owner", "Species", "Reason", "Doctor", "Specialty", "Date", "Time"};
        tableModel = new DefaultTableModel(columns, 0);
        resultTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        resultTable.setRowSorter(sorter);
        JScrollPane tableScroll = new JScrollPane(resultTable);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(new Color(245, 250, 255));
        filterPanel.setBorder(new TitledBorder("Appointments"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        if (!isDoctorMode) {
            gbc.gridx = 0; gbc.gridy = row; filterPanel.add(new JLabel("Doctor:"), gbc);
            gbc.gridx = 1; filterPanel.add(doctorNameBox, gbc);
            row++;
        }

        gbc.gridx = 0; gbc.gridy = row; filterPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1; filterPanel.add(dateBox, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(245, 250, 255));
        buttonPanel.add(loadButton);
        buttonPanel.add(searchButton);
        filterPanel.add(buttonPanel, gbc);

        add(filterPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        loadButton.addActionListener(e -> {
            if (isDoctorMode) {
                filterAppointmentsForDoctor();
                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No appointments found for Dr. " + loggedInDoctorName, "No Appointments", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                loadAllAppointments();
                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No appointments found.", "No Appointments", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        searchButton.addActionListener(e -> {
            if (isDoctorMode) {
                filterAppointmentsForDoctorByDate();
            } else {
                filterAppointmentsForAdmin();
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

    private void loadFilterOptions() {
        try {
            List<String> appointments = FileUtil.readFile("CSV/appointments.csv");
            Set<String> doctors = new HashSet<>();
            Set<String> dates = new HashSet<>();

            for (String line : appointments) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 8) {
                    doctors.add(parts[4].trim());
                    if (isDoctorMode && parts[4].trim().equalsIgnoreCase(loggedInDoctorName)) {
                        dates.add(parts[6].trim());
                    } else if (!isDoctorMode) {
                        dates.add(parts[6].trim());
                    }
                }
            }

            if (!isDoctorMode) {
                doctorNameBox.setModel(new DefaultComboBoxModel<>(addEmptyOption(doctors)));
            }
            dateBox.setModel(new DefaultComboBoxModel<>(addEmptyOption(dates)));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading filter options: " + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] addEmptyOption(Set<String> set) {
        List<String> list = new ArrayList<>(set);
        Collections.sort(list);
        list.add(0, "");  // Empty option
        return list.toArray(new String[0]);
    }

    private void loadAllAppointments() {
        try {
            tableModel.setRowCount(0);
            List<String> appointments = FileUtil.readFile("CSV/appointments.csv");
            for (String line : appointments) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 8) {
                    tableModel.addRow(parts);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterAppointmentsForDoctor() {
        tableModel.setRowCount(0);
        List<String> appointments = FileUtil.readFile("CSV/appointments.csv");

        for (String line : appointments) {
            String[] parts = line.split(",", -1);
            if (parts.length >= 8 && parts[4].trim().equalsIgnoreCase(loggedInDoctorName)) {
                tableModel.addRow(parts);
            }
        }
    }

    private void filterAppointmentsForDoctorByDate() {
        String selectedDate = dateBox.getSelectedItem() != null ? dateBox.getSelectedItem().toString().trim() : "";

        if (selectedDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a date to filter.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);
        List<String> appointments = FileUtil.readFile("CSV/appointments.csv");

        for (String line : appointments) {
            String[] parts = line.split(",", -1);
            if (parts.length >= 8 &&
                    parts[4].trim().equalsIgnoreCase(loggedInDoctorName) &&
                    parts[6].trim().equalsIgnoreCase(selectedDate)) {
                tableModel.addRow(parts);
            }
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No appointments found for Dr. " + loggedInDoctorName + " on " + selectedDate, "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filterAppointmentsForAdmin() {
        String selectedDoctor = doctorNameBox.getSelectedItem() != null ? ((String) doctorNameBox.getSelectedItem()).trim() : "";
        String selectedDate = dateBox.getSelectedItem() != null ? ((String) dateBox.getSelectedItem()).trim() : "";

        if (selectedDoctor.isEmpty() && selectedDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select Doctor or Date to filter.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);
        List<String> appointments = FileUtil.readFile("CSV/appointments.csv");

        for (String line : appointments) {
            String[] parts = line.split(",", -1);
            if (parts.length >= 8 &&
                    (selectedDoctor.isEmpty() || parts[4].trim().equalsIgnoreCase(selectedDoctor)) &&
                    (selectedDate.isEmpty() || parts[6].trim().equalsIgnoreCase(selectedDate))) {
                tableModel.addRow(parts);
            }
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No appointments found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void resetPanel() {
        loadFilterOptions();
        if (!isDoctorMode && doctorNameBox.getItemCount() > 0) {
            doctorNameBox.setSelectedIndex(0);
        }
        if (dateBox.getItemCount() > 0) {
            dateBox.setSelectedIndex(0);
        }
        tableModel.setRowCount(0);
    }
}
