package Form;

import Database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FormTamu extends JFrame {
    private int kamarId;
    private String kamarNomor, kamarTipe;

    private JLabel lblKamarInfo;
    private JTextField txtNama, txtNik;
    private JTextField txtCheckin, txtCheckout;
    private JButton btnBooking, btnKembali;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FormTamu(int kamarId, String kamarNomor, String kamarTipe) {
        this.kamarId = kamarId;
        this.kamarNomor = kamarNomor;
        this.kamarTipe = kamarTipe;

        setTitle("Form Data Tamu");
        setSize(350, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblKamarInfo = new JLabel("Booking Kamar: " + kamarNomor + " (" + kamarTipe + ")");
        panel.add(lblKamarInfo);
        panel.add(new JLabel());

        panel.add(new JLabel("Nama Tamu:"));
        txtNama = new JTextField();
        panel.add(txtNama);

        panel.add(new JLabel("NIK:"));
        txtNik = new JTextField();
        panel.add(txtNik);

        panel.add(new JLabel("Check-in (yyyy-MM-dd):"));
        txtCheckin = new JTextField(dtf.format(LocalDate.now()));
        panel.add(txtCheckin);

        panel.add(new JLabel("Check-out (yyyy-MM-dd):"));
        txtCheckout = new JTextField(dtf.format(LocalDate.now().plusDays(1)));
        panel.add(txtCheckout);

        btnBooking = new JButton("Booking");
        btnKembali = new JButton("Kembali");

        panel.add(btnBooking);
        panel.add(btnKembali);

        add(panel);
        setVisible(true);

        btnBooking.addActionListener(e -> simpanBooking());
        btnKembali.addActionListener(e -> {
            dispose();
            new FormKamar();
        });
    }

    private void simpanBooking() {
        String nama = txtNama.getText().trim();
        String nik = txtNik.getText().trim();
        String checkin = txtCheckin.getText().trim();
        String checkout = txtCheckout.getText().trim();

        if (nama.isEmpty() || nik.isEmpty() || checkin.isEmpty() || checkout.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harap isi semua data!");
            return;
        }

        try {
            LocalDate tglCheckin = LocalDate.parse(checkin, dtf);
            LocalDate tglCheckout = LocalDate.parse(checkout, dtf);

            if (!tglCheckout.isAfter(tglCheckin)) {
                JOptionPane.showMessageDialog(this, "Tanggal check-out harus setelah check-in!");
                return;
            }

            long lamaInap = java.time.temporal.ChronoUnit.DAYS.between(tglCheckin, tglCheckout);

            try (Connection conn = DatabaseConnection.getConnection()) {
                // Ambil harga kamar
                String sqlHarga = "SELECT harga FROM kamar WHERE id = ?";
                PreparedStatement pstHarga = conn.prepareStatement(sqlHarga);
                pstHarga.setInt(1, kamarId);
                ResultSet rsHarga = pstHarga.executeQuery();

                int hargaKamar = 0;
                if (rsHarga.next()) {
                    hargaKamar = rsHarga.getInt("harga");
                }

                int subtotal = (int) (lamaInap * hargaKamar);

                // Simpan data tamu
                String sqlTamu = "INSERT INTO tamu (nama, nik, checkin, checkout) VALUES (?, ?, ?, ?)";
                PreparedStatement pstTamu = conn.prepareStatement(sqlTamu, PreparedStatement.RETURN_GENERATED_KEYS);
                pstTamu.setString(1, nama);
                pstTamu.setString(2, nik);
                pstTamu.setString(3, checkin);
                pstTamu.setString(4, checkout);
                pstTamu.executeUpdate();

                ResultSet rsTamu = pstTamu.getGeneratedKeys();
                int tamuId = -1;
                if (rsTamu.next()) {
                    tamuId = rsTamu.getInt(1);
                }

                if (tamuId == -1) {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan data tamu!");
                    return;
                }

                // Simpan data booking + subtotal
                String sqlBooking = "INSERT INTO booking (id_kamar, id_tamu, subtotal) VALUES (?, ?, ?)";
                PreparedStatement pstBooking = conn.prepareStatement(sqlBooking);
                pstBooking.setInt(1, kamarId);
                pstBooking.setInt(2, tamuId);
                pstBooking.setInt(3, subtotal);
                pstBooking.executeUpdate();

                // Update status kamar jadi 'Dibooking'
                String sqlUpdateKamar = "UPDATE kamar SET status = 'Dibooking' WHERE id = ?";
                PreparedStatement pstUpdate = conn.prepareStatement(sqlUpdateKamar);
                pstUpdate.setInt(1, kamarId);
                pstUpdate.executeUpdate();

                JOptionPane.showMessageDialog(this, "Booking berhasil!\nSubtotal: Rp " + subtotal);
                dispose();
                new MenuUtama();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal melakukan booking!");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format tanggal harus yyyy-MM-dd");
        }
    }
}