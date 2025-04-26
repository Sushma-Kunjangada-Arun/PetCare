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
import java.awt.*;

public class ReceptionistDashboard extends JFrame {

    public ReceptionistDashboard(String username) {
        setTitle("Receptionist Dashboard");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 250, 255));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(66, 135, 245));
        topPanel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel lblWelcome = new JLabel("Welcome " + username + "!", SwingConstants.LEFT);
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

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 15));
        tabbedPane.setBackground(new Color(230, 240, 255));
        tabbedPane.setForeground(new Color(33, 37, 41));
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        AnimalPatientPanel petPanel = new AnimalPatientPanel();
        BillingPanel billingPanel = new BillingPanel();
        PetAppointmentPanel appointmentPanel = new PetAppointmentPanel();

        tabbedPane.addTab("Animal Records", petPanel);
        tabbedPane.addTab("Billing", billingPanel);
        tabbedPane.addTab("Appointments", appointmentPanel);

        // Tab Change Listener to reset each panel when selected
        tabbedPane.addChangeListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected instanceof AnimalPatientPanel) {
                ((AnimalPatientPanel) selected).resetPanel();
            } else if (selected instanceof BillingPanel) {
                ((BillingPanel) selected).resetPanel();
            } else if (selected instanceof PetAppointmentPanel) {
                ((PetAppointmentPanel) selected).resetPanel();
            }
        });

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
}
