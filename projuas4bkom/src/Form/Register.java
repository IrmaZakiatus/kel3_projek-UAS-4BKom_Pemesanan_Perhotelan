package Form;

import Database.DatabaseConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Register extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtKonfirmasi;
    private JButton btnRegister;
    private JLabel lblLogin;
    private JLabel background;

    public Register() {
        setTitle("Registrasi");
        setSize(360, 640);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background
        try {
            InputStream bgStream = getClass().getResourceAsStream("/Model/Background_Login.jpg");
            if (bgStream != null) {
                Image img = ImageIO.read(bgStream);
                background = new JLabel(new ImageIcon(img.getScaledInstance(360, 640, Image.SCALE_SMOOTH)));
                background.setBounds(0, 0, 360, 640);
                setContentPane(background);
                background.setLayout(null);
            }
        } catch (Exception e) {
            System.out.println("Gagal load background: " + e.getMessage());
        }

        // Username
        JLabel lblUser = new JLabel("Username:");
        lblUser.setForeground(Color.WHITE);
        lblUser.setBounds(50, 180, 100, 20);
        txtUsername = new JTextField();
        txtUsername.setBounds(50, 200, 250, 30);

        // Password
        JLabel lblPass = new JLabel("Password:");
        lblPass.setForeground(Color.WHITE);
        lblPass.setBounds(50, 240, 100, 20);
        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 260, 250, 30);

        // Konfirmasi
        JLabel lblKonfirm = new JLabel("Konfirmasi:");
        lblKonfirm.setForeground(Color.WHITE);
        lblKonfirm.setBounds(50, 300, 100, 20);
        txtKonfirmasi = new JPasswordField();
        txtKonfirmasi.setBounds(50, 320, 250, 30);

        // Tombol Register
        btnRegister = new JButton("Daftar");
        btnRegister.setBounds(50, 370, 250, 35);
        btnRegister.setFocusPainted(false);

        // Label Login
        lblLogin = new JLabel("<HTML><U>Sudah punya akun? Klik di sini</U></HTML>", SwingConstants.CENTER);
        lblLogin.setForeground(Color.CYAN);
        lblLogin.setBounds(50, 420, 250, 25);
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Pajangan Google
        JLabel googleLabel = new JLabel("  Daftar dengan Google", SwingConstants.CENTER);
        googleLabel.setForeground(Color.WHITE);
        googleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        googleLabel.setBounds(50, 470, 250, 30);
        try {
            InputStream iconStream = getClass().getResourceAsStream("/Model/google.png");
            if (iconStream != null) {
                Image icon = ImageIO.read(iconStream);
                Image scaledIcon = icon.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                googleLabel.setIcon(new ImageIcon(scaledIcon));
            }
        } catch (Exception e) {
            System.out.println("Gagal load icon Google");
        }

        // Tambah ke background
        background.add(lblUser);
        background.add(txtUsername);
        background.add(lblPass);
        background.add(txtPassword);
        background.add(lblKonfirm);
        background.add(txtKonfirmasi);
        background.add(btnRegister);
        background.add(lblLogin);
        background.add(googleLabel);

        // Aksi tombol daftar
        btnRegister.addActionListener(e -> daftarAkun());

        // Aksi klik label login
        lblLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new Login().setVisible(true);
            }
        });

        setVisible(true);
    }

    private void daftarAkun() {
        String username = txtUsername.getText();
        String password = String.valueOf(txtPassword.getPassword());
        String konfirmasi = String.valueOf(txtKonfirmasi.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username dan password tidak boleh kosong!");
            return;
        }

        if (!password.equals(konfirmasi)) {
            JOptionPane.showMessageDialog(null, "Password tidak cocok!");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registrasi berhasil!");
            dispose();
            new Login().setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal registrasi: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new Register();
    }
}