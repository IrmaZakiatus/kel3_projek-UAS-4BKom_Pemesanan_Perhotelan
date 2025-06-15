package Form;

import Database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormKamar extends JFrame {
    private JTextField txtNoKamar, txtTipe, txtHarga;
    private JComboBox<String> cmbStatus;
    private JButton btnTambah, btnEdit, btnHapus, btnClear, btnBooking, btnBack;
    private JTable tableKamar;
    private DefaultTableModel model;

    public FormKamar() {
        setTitle("Kelola Kamar");
        setSize(400, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panelUtama = new JPanel();
        panelUtama.setLayout(new BoxLayout(panelUtama, BoxLayout.Y_AXIS));
        panelUtama.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Input
        JPanel panelInput = new JPanel(new GridLayout(5, 2, 10, 10));
        txtNoKamar = new JTextField();
        txtTipe = new JTextField();
        txtHarga = new JTextField();
        cmbStatus = new JComboBox<>(new String[]{"Tersedia", "Dibooking"});

        panelInput.setBorder(BorderFactory.createTitledBorder("Form Kamar"));
        panelInput.add(new JLabel("Nomor Kamar:"));
        panelInput.add(txtNoKamar);
        panelInput.add(new JLabel("Tipe:"));
        panelInput.add(txtTipe);
        panelInput.add(new JLabel("Harga (Rp):"));
        panelInput.add(txtHarga);
        panelInput.add(new JLabel("Status:"));
        panelInput.add(cmbStatus);

        // Panel Tombol Aksi
        JPanel panelButton = new JPanel(new FlowLayout());
        btnTambah = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnHapus = new JButton("Hapus");
        btnClear = new JButton("Clear");
        panelButton.add(btnTambah);
        panelButton.add(btnEdit);
        panelButton.add(btnHapus);
        panelButton.add(btnClear);

        // Tabel
        model = new DefaultTableModel(new String[]{"ID", "Nomor", "Tipe", "Harga", "Status"}, 0);
        tableKamar = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tableKamar);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Data Kamar"));

        // Tombol booking dan kembali
        btnBooking = new JButton("Booking Kamar Ini");
        btnBooking.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBooking.setVisible(false); // disembunyiin dulu

        btnBack = new JButton("Kembali ke Menu Utama");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tambahkan ke panel utama
        panelUtama.add(panelInput);
        panelUtama.add(panelButton);
        panelUtama.add(Box.createVerticalStrut(10));
        panelUtama.add(scrollPane);
        panelUtama.add(Box.createVerticalStrut(10));
        panelUtama.add(btnBooking);
        panelUtama.add(Box.createVerticalStrut(5));
        panelUtama.add(btnBack);

        add(panelUtama);
        setVisible(true);

        loadKamar();

        // Event klik pada tabel
        tableKamar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableKamar.getSelectedRow();
                txtNoKamar.setText(model.getValueAt(row, 1).toString());
                txtTipe.setText(model.getValueAt(row, 2).toString());
                txtHarga.setText(model.getValueAt(row, 3).toString().replace("Rp ", "").replace(".", ""));
                cmbStatus.setSelectedItem(model.getValueAt(row, 4).toString());
                btnBooking.setVisible(true);
            }
        });

        // Action button
        btnTambah.addActionListener(e -> tambahKamar());
        btnEdit.addActionListener(e -> editKamar());
        btnHapus.addActionListener(e -> hapusKamar());
        btnClear.addActionListener(e -> clearInput());
        btnBooking.addActionListener(e -> bookingKamar());
        btnBack.addActionListener(e -> {
            dispose();
            new MenuUtama();
        });
    }

    private void loadKamar() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM kamar")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nomor_kamar"),
                        rs.getString("tipe"),
                        "Rp " + rs.getInt("harga"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tambahKamar() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO kamar (nomor_kamar, tipe, harga, status) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, txtNoKamar.getText());
            pst.setString(2, txtTipe.getText());
            pst.setInt(3, Integer.parseInt(txtHarga.getText()));
            pst.setString(4, cmbStatus.getSelectedItem().toString());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kamar berhasil ditambahkan!");
            clearInput();
            loadKamar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editKamar() {
        int row = tableKamar.getSelectedRow();
        if (row == -1) return;

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE kamar SET nomor_kamar=?, tipe=?, harga=?, status=? WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, txtNoKamar.getText());
            pst.setString(2, txtTipe.getText());
            pst.setInt(3, Integer.parseInt(txtHarga.getText()));
            pst.setString(4, cmbStatus.getSelectedItem().toString());
            pst.setInt(5, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kamar berhasil diupdate!");
            clearInput();
            loadKamar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hapusKamar() {
        int row = tableKamar.getSelectedRow();
        if (row == -1) return;

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin mau hapus kamar ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM kamar WHERE id=?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setInt(1, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Kamar berhasil dihapus!");
                clearInput();
                loadKamar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bookingKamar() {
        int row = tableKamar.getSelectedRow();
        if (row != -1) {
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            String nomor = model.getValueAt(row, 1).toString();
            String tipe = model.getValueAt(row, 2).toString();

            dispose();
            new FormTamu(id, nomor, tipe); // pastikan FormTamu ada constructor seperti ini
        }
    }

    private void clearInput() {
        txtNoKamar.setText("");
        txtTipe.setText("");
        txtHarga.setText("");
        cmbStatus.setSelectedIndex(0);
        tableKamar.clearSelection();
        btnBooking.setVisible(false);
    }

    public static void main(String[] args) {
        new FormKamar();
    }
}