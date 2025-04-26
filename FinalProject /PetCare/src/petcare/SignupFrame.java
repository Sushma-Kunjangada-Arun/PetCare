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
import java.io.*;

public class SignupFrame extends JFrame {
    private JTextField usernameField, fullNameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> roleBox;
    private JButton signupButton;

    public SignupFrame() {
        setTitle("Create Account - PetCare");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1; add(usernameField, gbc);

        // Full Name
        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Full Name:"), gbc);
        fullNameField = new JTextField(15);
        gbc.gridx = 1; add(fullNameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1; add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Confirm Password:"), gbc);
        confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1; add(confirmPasswordField, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("Role:"), gbc);
        roleBox = new JComboBox<>(new String[]{"Admin", "Receptionist", "Doctor", "Staff"});
        gbc.gridx = 1; add(roleBox, gbc);

        // Signup Button
        signupButton = new JButton("Sign Up");
        signupButton.setBackground(new Color(66, 135, 245));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setFont(new Font("SansSerif", Font.BOLD, 13));

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(signupButton, gbc);

        signupButton.addActionListener(e -> handleSignup());
    }

    private void handleSignup() {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String role = roleBox.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        File file = new File("CSV/users.csv");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error creating user file.");
                return;
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equalsIgnoreCase(username)) {
                    JOptionPane.showMessageDialog(this, "Username already exists.");
                    return;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading user file.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(username + "," + password + "," + role + "," + fullName);
            bw.newLine();
            JOptionPane.showMessageDialog(this, "Account created successfully!");
            dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving user.");
        }
    }
}
