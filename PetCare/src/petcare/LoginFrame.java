/**
 *
 * @author sushmaka
 */

package petcare;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("PetCare - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(new GridLayout(1, 2));

        // Left Panel (Image)
        JPanel panelLeft = new JPanel(new BorderLayout());
        panelLeft.setBackground(new Color(240, 248, 255));
        JLabel lblImage = new JLabel(new ImageIcon("Images/Logo.png"));
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        panelLeft.add(lblImage, BorderLayout.CENTER);

        // Right Panel (Login Form)
        JPanel panelRight = new JPanel(new GridBagLayout());
        panelRight.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Login to PetCare", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelRight.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panelRight.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        JTextField txtUsername = new JTextField(15);
        panelRight.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panelRight.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField txtPassword = new JPasswordField(15);
        panelRight.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panelRight.add(new JLabel("Select Role:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"Admin", "Receptionist", "Doctor"});
        panelRight.add(cmbRole, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        JButton btnLogin = new JButton("Login");
        styleButton(btnLogin, new Color(66, 133, 244), new Color(30, 90, 200));
        panelRight.add(btnLogin, gbc);

        gbc.gridy++;
        JButton btnCreateAccount = new JButton("Create Account");
        styleButton(btnCreateAccount, new Color(100, 149, 237), new Color(65, 105, 225));
        panelRight.add(btnCreateAccount, gbc);

        getContentPane().add(panelLeft);
        getContentPane().add(panelRight);

        // Login Action
        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String role = cmbRole.getSelectedItem().toString();

            String fullName = validateLogin(username, password, role);
            if (fullName != null) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                openDashboard(role, username, fullName);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Create Account Action
        btnCreateAccount.addActionListener(e -> {
            new SignupFrame().setVisible(true);
        });
    }

    // CSV Validation - returns Full Name if login successful, else null
    private String validateLogin(String username, String password, String role) {
        try (BufferedReader br = new BufferedReader(new FileReader("CSV/users.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String fileUser = parts[0].trim();
                    String filePass = parts[1].trim();
                    String fileRole = parts[2].trim();
                    String fullName = parts[3].trim();
                    if (fileUser.equals(username) && filePass.equals(password) && fileRole.equalsIgnoreCase(role)) {
                        return fullName;
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading users file!", "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    // Dashboard Logic
    private void openDashboard(String role, String username, String fullName) {
        switch (role.toLowerCase()) {
            case "admin":
                new AdminDashboard().setVisible(true);
                break;
            case "receptionist":
                new ReceptionistDashboard(username).setVisible(true);
                break;
            case "doctor":
                new DoctorDashboard(fullName).setVisible(true); // Pass full name for filtering
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown role.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }

    // Styling Helper
    private void styleButton(JButton button, Color baseColor, Color hoverColor) {
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(10));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });
    }

    // Rounded button border class
    static class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() {
            return false;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}
