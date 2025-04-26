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


public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard - PetCare");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(245, 250, 255)); // Light cream-blue

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(66, 135, 245)); // Blue
        topPanel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel lblWelcome = new JLabel("Welcome, Admin!", SwingConstants.LEFT);
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(220, 53, 69)); // Red
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

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 15));
        tabbedPane.setBackground(new Color(230, 240, 255));
        tabbedPane.setForeground(new Color(33, 37, 41));

        tabbedPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 200), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Panels (Objects to Reset)
        AnimalPatientPanel petPanel = new AnimalPatientPanel();
        InventoryPanel inventoryPanel = new InventoryPanel();
        DoctorAppointmentPanel appointmentPanel = new DoctorAppointmentPanel();
        VetPanel vetPanel = new VetPanel();
        BillingPanel billingPanel = new BillingPanel();
        ReceptionistPanel receptionistPanel = new ReceptionistPanel();


        // Add Tabs
        tabbedPane.addTab("Manage Pets", petPanel);
        tabbedPane.addTab("Manage Vets", vetPanel);
        tabbedPane.addTab("Billing", billingPanel);
        tabbedPane.addTab("Inventory", inventoryPanel);
        tabbedPane.addTab("Appointments Viewer", appointmentPanel);
        tabbedPane.addTab("Manage Receptionists", receptionistPanel);

        // Tab Change Listener
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            String title = tabbedPane.getTitleAt(selectedIndex);

            switch (title) {
                case "Manage Pets":
                    petPanel.resetPanel();
                    break;
                case "Manage Vets":
                    vetPanel.resetPanel();
                    break;
                case "Billing":
                    billingPanel.resetPanel();
                    break;
                case "Manage Receptionists":
                    receptionistPanel.resetPanel();
                    break;
                case "Inventory":
                    inventoryPanel.resetPanel();
                    break;
                case "Appointments Viewer":
                    appointmentPanel.resetPanel();
                    break;
                // Add similar if needed for other panels
            }
        });

        // Main Layout
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
}
