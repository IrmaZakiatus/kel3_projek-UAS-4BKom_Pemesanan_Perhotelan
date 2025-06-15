package Form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MenuUtama extends JFrame {
    private JPanel panelMain;
    private JButton btnKamar;
    private JButton btnHistori;
    private JButton btnLogout;
    private JButton btnClose;
    private JLabel lblTanggal;

    public MenuUtama() {
        setTitle("Menu Utama");
        setSize(360, 640);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/Model/background_login.jpg"));
        Image bg = backgroundImage.getImage();

        JPanel bgPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(0, 0, 0, 120));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(new BoxLayout(bgPanel, BoxLayout.Y_AXIS));
        bgPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Menu Utama", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        btnKamar = createButton("Kelola Kamar", "/Model/kamar.jpg");
        btnHistori = createButton("Histori Booking", "/Model/histori.jpg");
        btnLogout = createButton("Logout", "/Model/logout.jpg");
        btnClose = createButton("Tutup Aplikasi", "/Model/tutup.jpg");

        lblTanggal = new JLabel("", SwingConstants.CENTER);
        lblTanggal.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTanggal.setForeground(Color.WHITE);
        lblTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updateTanggal();
        startClock();

        bgPanel.add(title);
        bgPanel.add(Box.createVerticalStrut(20));
        bgPanel.add(btnKamar);
        bgPanel.add(Box.createVerticalStrut(15));
        bgPanel.add(btnHistori);
        bgPanel.add(Box.createVerticalStrut(15));
        bgPanel.add(btnLogout);
        bgPanel.add(Box.createVerticalStrut(15));
        bgPanel.add(btnClose);
        bgPanel.add(Box.createVerticalGlue());
        bgPanel.add(lblTanggal);

        setContentPane(bgPanel);
        setVisible(true);

        btnKamar.addActionListener(e -> {
            dispose();
            new FormKamar();
        });

        btnHistori.addActionListener(e -> {
            dispose();
            new FormHistory();
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Anda yakin ingin logout?",
                    "Konfirmasi Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new Login();
            }
        });

        btnClose.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Yakin ingin keluar dari aplikasi?",
                    "Tutup Aplikasi",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    private JButton createButton(String text, String iconPath) {
        ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
        Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        JButton button = new JButton(text, new ImageIcon(img));
        button.setMaximumSize(new Dimension(250, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        return button;
    }

    private void updateTanggal() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy | HH:mm:ss");
        lblTanggal.setText(sdf.format(new Date()));
    }

    private void startClock() {
        Timer timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTanggal();
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        new MenuUtama();
    }
}