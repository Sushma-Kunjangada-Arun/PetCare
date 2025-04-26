/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package petcare;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DoctorDashboard extends JFrame {

    public DoctorDashboard(String vetName) {
        setTitle("Doctor Dashboard - PetCare");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 250, 255));

        // Top Panel Setup
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(66, 135, 245));
        topPanel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel lblWelcome = new JLabel("Welcome Dr. " + vetName + "!", SwingConstants.LEFT);
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        topPanel.add(lblWelcome, BorderLayout.WEST);
        topPanel.add(btnLogout, BorderLayout.EAST);

        // Tabbed Pane Setup
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 15));
        tabbedPane.setBackground(new Color(230, 240, 255));
        tabbedPane.setForeground(new Color(33, 37, 41));
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panels
        AnimalPatientPanel patientPanel = new AnimalPatientPanel();
        DoctorAppointmentPanel appointmentPanel = new DoctorAppointmentPanel(vetName); // Only vetName is passed

        tabbedPane.addTab("Manage Patients", patientPanel);
        tabbedPane.addTab("My Appointments", appointmentPanel);

        // Reset Panels on Tab Switch
        tabbedPane.addChangeListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected instanceof AnimalPatientPanel) {
                ((AnimalPatientPanel) selected).resetPanel();
            } else if (selected instanceof DoctorAppointmentPanel) {
                ((DoctorAppointmentPanel) selected).resetPanel();
            }
        });

        // Layout
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private String findSpecialtyForDoctor(String vetName) {
        List<String> lines = FileUtil.readFile("CSV/doctors.csv");
        for (String line : lines) {
            String[] parts = line.split(",", -1);
            if (parts.length >= 5 && parts[0].trim().equalsIgnoreCase(vetName.trim())) {
                return parts[4].trim();
            }
        }
        return "Unknown";
    }
}
