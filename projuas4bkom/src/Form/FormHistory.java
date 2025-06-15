package Form;

import Database.DatabaseConnection;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;

public class FormHistory extends JFrame {
    private JTable tableHistori;
    private DefaultTableModel model;
    private JButton btnHapus, btnPrint, btnKembali;

    public FormHistory() {
        setTitle("Histori Booking");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new String[]{
                "ID Booking", "Tanggal Booking", "Nama Tamu", "NIK",
                "Check-in", "Check-out", "No Kamar", "Tipe", "Harga", "Subtotal"
        }, 0);

        tableHistori = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tableHistori);
        add(scrollPane, BorderLayout.CENTER);

        // Panel tombol
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnHapus = new JButton("Hapus");
        btnPrint = new JButton("Print ke PDF");
        btnKembali = new JButton("Kembali");

        panelButton.add(btnHapus);
        panelButton.add(btnPrint);
        panelButton.add(btnKembali);
        add(panelButton, BorderLayout.SOUTH);

        // Event klik tombol
        btnHapus.addActionListener(e -> hapusBooking());
        btnPrint.addActionListener(e -> printHistoriPDF());
        btnKembali.addActionListener(e -> {
            dispose();
            new MenuUtama();
        });

        loadHistoriBooking();
        setVisible(true);
    }

    private void loadHistoriBooking() {
        model.setRowCount(0);
        String query = """
                SELECT 
                    b.id AS id_booking,
                    b.tanggal_booking,
                    t.nama AS nama_tamu,
                    t.nik,
                    t.checkin,
                    t.checkout,
                    k.nomor_kamar,
                    k.tipe,
                    k.harga,
                    b.subtotal
                FROM 
                    booking b
                JOIN tamu t ON b.id_tamu = t.id
                JOIN kamar k ON b.id_kamar = k.id
                ORDER BY b.tanggal_booking DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_booking"),
                        rs.getString("tanggal_booking"),
                        rs.getString("nama_tamu"),
                        rs.getString("nik"),
                        rs.getString("checkin"),
                        rs.getString("checkout"),
                        rs.getString("nomor_kamar"),
                        rs.getString("tipe"),
                        "Rp " + rs.getInt("harga"),
                        "Rp " + rs.getInt("subtotal")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat histori booking!");
        }
    }

    private void hapusBooking() {
        int selectedRow = tableHistori.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data booking yang ingin dihapus!");
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        int idBooking = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sqlDelete = "DELETE FROM booking WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sqlDelete);
            pst.setInt(1, idBooking);
            int affected = pst.executeUpdate();

            if (affected > 0) {
                model.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Data booking berhasil dihapus.");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data booking.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus data.");
        }
    }

    private void printHistoriPDF() {
        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        chooser.setDialogTitle("Pilih folder untuk simpan PDF");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int userSelection = chooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        File folder = chooser.getSelectedFile();
        String filePath = folder.getAbsolutePath() + File.separator + "cetak_histori.pdf";

        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font textFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12);

            for (int row = 0; row < model.getRowCount(); row++) {
                document.newPage();

                Paragraph title = new Paragraph("Histori Booking", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20f);
                document.add(title);

                for (int col = 0; col < model.getColumnCount(); col++) {
                    String label = model.getColumnName(col);
                    String value = model.getValueAt(row, col).toString();

                    Paragraph detail = new Paragraph(label + " : " + value, textFont);
                    detail.setSpacingAfter(8f);
                    document.add(detail);
                }
            }

            document.close();
            writer.close();

            JOptionPane.showMessageDialog(this, "PDF berhasil disimpan di: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal membuat PDF!");
        }
    }


    public static void main(String[] args) {
        new FormHistory();
    }
}