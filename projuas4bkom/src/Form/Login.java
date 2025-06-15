package Form;

import Database.DatabaseConnection;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnTutup;
    private JCheckBox showPassword;
    private JLabel linkRegister;

    public Login() {
        setTitle("Login Booking Hotel");
        setSize(375, 667);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel() {
            ImageIcon bgIcon = new ImageIcon(getClass().getResource("/model/Background_menu.jpg"));
            Image bgImage = bgIcon.getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(0, 0, 0, 120));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(null);

        Font fontField = new Font("Segoe UI", Font.PLAIN, 14);
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 15);
        Font fontButton = new Font("Segoe UI", Font.BOLD, 16);

        // Logo
        JLabel logo = new JLabel();
        logo.setBounds(100, 50, 160, 100);
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/model/logo1.jpg"));
        Image logoImg = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        logo.setIcon(new ImageIcon(logoImg));
        panel.add(logo);

        // Label Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setBounds(60, 180, 260, 20);
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(fontLabel);
        panel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(60, 205, 260, 40);
        txtUsername.setFont(fontField);
        txtUsername.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        addPlaceholder(txtUsername, "Masukkan Username");
        panel.add(txtUsername);

        // Label Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setBounds(60, 260, 260, 20);
        lblPass.setForeground(Color.WHITE);
        lblPass.setFont(fontLabel);
        panel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(60, 285, 260, 40);
        txtPassword.setFont(fontField);
        txtPassword.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        addPlaceholder(txtPassword, "Masukkan Password");
        panel.add(txtPassword);

        // Show Password
        showPassword = new JCheckBox("Tampilkan Password");
        showPassword.setBounds(60, 330, 200, 20);
        showPassword.setOpaque(false);
        showPassword.setForeground(Color.WHITE);
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });
        panel.add(showPassword);

        // Tombol Login
        btnLogin = new JButton("Login");
        btnLogin.setBounds(60, 360, 260, 45);
        btnLogin.setFont(fontButton);
        btnLogin.setBackground(new Color(0, 120, 215));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> prosesLogin());
        panel.add(btnLogin);

        // Tombol Tutup Aplikasi
        btnTutup = new JButton("Tutup Aplikasi");
        btnTutup.setBounds(60, 420, 260, 40);
        btnTutup.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnTutup.setBackground(new Color(220, 53, 69));
        btnTutup.setForeground(Color.WHITE);
        btnTutup.setFocusPainted(false);
        btnTutup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTutup.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        panel.add(btnTutup);

        // Link ke Register
        linkRegister = new JLabel("Belum punya akun? Daftar di sini.");
        linkRegister.setBounds(60, 470, 260, 30);
        linkRegister.setForeground(Color.WHITE);
        linkRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkRegister.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new Register().setVisible(true);
                dispose();
            }
        });
        panel.add(linkRegister);

        add(panel);
    }

    private void addPlaceholder(JTextComponent field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('•');
                    }
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });
        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0);
        }
    }

    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || username.equals("Masukkan Username") ||
                password.isEmpty() || password.equals("Masukkan Password")) {
            JOptionPane.showMessageDialog(this, "Harap isi Username dan Password.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {

            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Berhasil!");
                new MenuUtama().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!");
                txtPassword.setText("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal login: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}