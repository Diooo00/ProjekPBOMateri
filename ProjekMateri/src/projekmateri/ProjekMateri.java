import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * KalkulatorGUI - Antarmuka grafis kalkulator bangun ruang.
 *
 * Class ini menggabungkan semua pilar OOP:
 * - ABSTRACTION   : menggunakan BangunRuang sebagai tipe umum
 * - ENCAPSULATION : komponen UI disimpan sebagai field private
 * - INHERITANCE   : memanfaatkan hierarki BK -> Prisma/Limas
 * - POLYMORPHISM  : method tampilkanInfo() dipanggil polimorfis
 * - MULTI-THREADING: perhitungan dijalankan via KalkulatorWorker
 */
public class ProjekMateri extends JFrame {

    // ==================== ENCAPSULATION ====================
    // Semua komponen UI dibuat private
    private JComboBox<String> comboBangun;
    private JTextField txtDiagonal1, txtDiagonal2, txtTinggi;
    private JLabel lblTinggi;
    private JTextArea txtHasil;
    private JButton btnHitung, btnReset;
    private JLabel lblStatus;
    private JProgressBar progressBar;

    // Warna tema
    private static final Color WARNA_PRIMER   = new Color(41, 128, 185);
    private static final Color WARNA_AKSEN    = new Color(52, 152, 219);
    private static final Color WARNA_BG       = new Color(245, 248, 250);
    private static final Color WARNA_KARTU    = Color.WHITE;
    private static final Color WARNA_TEKS     = new Color(44, 62, 80);
    private static final Color WARNA_SUKSES   = new Color(39, 174, 96);

    public ProjekMateri() {
        initUI();
    }

    private void initUI() {
        setTitle("Kalkulator Bangun Ruang - Belah Ketupat");
        setSize(600, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(WARNA_BG);

        setLayout(new BorderLayout(0, 0));
        add(buatHeader(), BorderLayout.NORTH);
        add(buatPanelTengah(), BorderLayout.CENTER);
        add(buatStatusBar(), BorderLayout.SOUTH);
    }

    // ==================== PANEL HEADER ====================
    private JPanel buatHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WARNA_PRIMER);
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel judul = new JLabel("Kalkulator Bangun Ruang");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 20));
        judul.setForeground(Color.WHITE);

        JLabel subjudul = new JLabel("Berbasis Belah Ketupat");
        subjudul.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subjudul.setForeground(new Color(189, 220, 248));

        JPanel judulPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        judulPanel.setOpaque(false);
        judulPanel.add(judul);
        judulPanel.add(subjudul);
        header.add(judulPanel, BorderLayout.WEST);

        return header;
    }

    // ==================== PANEL TENGAH ====================
    private JPanel buatPanelTengah() {
        JPanel tengah = new JPanel();
        tengah.setLayout(new BoxLayout(tengah, BoxLayout.Y_AXIS));
        tengah.setBackground(WARNA_BG);
        tengah.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        tengah.add(buatKartuPilihan());
        tengah.add(Box.createVerticalStrut(12));
        tengah.add(buatKartuInput());
        tengah.add(Box.createVerticalStrut(12));
        tengah.add(buatKartuHasil());

        return tengah;
    }

    // ==================== KARTU PILIHAN BANGUN ====================
    private JPanel buatKartuPilihan() {
        JPanel kartu = buatKartu("Pilih Bangun Ruang");

        String[] pilihanBangun = {
            "Belah Ketupat (2D)",
            "Prisma Belah Ketupat",
            "Limas Belah Ketupat"
        };
        comboBangun = new JComboBox<>(pilihanBangun);
        comboBangun.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBangun.setBackground(Color.WHITE);
        comboBangun.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        comboBangun.addActionListener(e -> onBangunBerubah());

        kartu.add(comboBangun);
        return kartu;
    }

    // ==================== KARTU INPUT ====================
    private JPanel buatKartuInput() {
        JPanel kartu = buatKartu("Parameter Input");
        kartu.setLayout(new BoxLayout(kartu, BoxLayout.Y_AXIS));

        // Diagonal 1
        kartu.add(buatBarisinput("Diagonal 1 (d1) — cm:", txtDiagonal1 = buatTextField()));
        kartu.add(Box.createVerticalStrut(8));

        // Diagonal 2
        kartu.add(buatBarisinput("Diagonal 2 (d2) — cm:", txtDiagonal2 = buatTextField()));
        kartu.add(Box.createVerticalStrut(8));

        // Tinggi (hanya untuk prisma/limas)
        lblTinggi = new JLabel("Tinggi — cm:");
        lblTinggi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTinggi.setForeground(WARNA_TEKS);
        lblTinggi.setVisible(false);

        txtTinggi = buatTextField();
        txtTinggi.setVisible(false);

        kartu.add(buatBarisinputLabeled(lblTinggi, txtTinggi));
        kartu.add(Box.createVerticalStrut(14));

        // Tombol
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panelTombol.setOpaque(false);

        btnHitung = buatTombol("Hitung", WARNA_PRIMER);
        btnReset  = buatTombol("Reset", new Color(149, 165, 166));

        btnHitung.addActionListener(e -> onHitung());
        btnReset.addActionListener(e -> onReset());

        panelTombol.add(btnHitung);
        panelTombol.add(btnReset);
        kartu.add(panelTombol);

        return kartu;
    }

    // ==================== KARTU HASIL ====================
    private JPanel buatKartuHasil() {
        JPanel kartu = buatKartu("Hasil Perhitungan");

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        kartu.add(progressBar);
        kartu.add(Box.createVerticalStrut(8));

        txtHasil = new JTextArea(8, 40);
        txtHasil.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtHasil.setEditable(false);
        txtHasil.setBackground(new Color(248, 250, 252));
        txtHasil.setForeground(WARNA_TEKS);
        txtHasil.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        txtHasil.setText("Hasil akan muncul di sini...");

        JScrollPane scroll = new JScrollPane(txtHasil);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 240)));
        kartu.add(scroll);

        return kartu;
    }

    // ==================== STATUS BAR ====================
    private JPanel buatStatusBar() {
        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        status.setBackground(new Color(236, 240, 241));
        status.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 210, 220)));

        lblStatus = new JLabel("Siap. Pilih bangun ruang dan masukkan parameter.");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(100, 110, 120));
        status.add(lblStatus);

        return status;
    }

    // ==================== ACTION HANDLERS ====================

    private void onBangunBerubah() {
        int idx = comboBangun.getSelectedIndex();
        boolean butuhTinggi = (idx == 1 || idx == 2);
        lblTinggi.setVisible(butuhTinggi);
        txtTinggi.setVisible(butuhTinggi);
        txtHasil.setText("Hasil akan muncul di sini...");
        lblStatus.setText("Pilihan diubah ke: " + comboBangun.getSelectedItem());
        revalidate();
        repaint();
    }

    /**
     * MULTI-THREADING: Saat tombol hitung diklik, perhitungan
     * dilempar ke KalkulatorWorker (background thread).
     * UI tetap responsif selama proses berjalan.
     */
    private void onHitung() {
        try {
            double d1 = Double.parseDouble(txtDiagonal1.getText().trim());
            double d2 = Double.parseDouble(txtDiagonal2.getText().trim());

            if (d1 <= 0 || d2 <= 0) {
                tampilkanError("Diagonal harus lebih besar dari 0!");
                return;
            }

            double[] params;
            KalkulatorWorker.TipeBangun tipe;
            int idx = comboBangun.getSelectedIndex();

            if (idx == 0) {
                // Belah Ketupat
                params = new double[]{d1, d2};
                tipe = KalkulatorWorker.TipeBangun.BELAH_KETUPAT;
            } else {
                double tinggi = Double.parseDouble(txtTinggi.getText().trim());
                if (tinggi <= 0) {
                    tampilkanError("Tinggi harus lebih besar dari 0!");
                    return;
                }
                params = new double[]{d1, d2, tinggi};
                tipe = (idx == 1)
                    ? KalkulatorWorker.TipeBangun.PRISMA
                    : KalkulatorWorker.TipeBangun.LIMAS;
            }

            // Set UI ke mode loading
            btnHitung.setEnabled(false);
            progressBar.setVisible(true);
            txtHasil.setText("Menghitung...");

            // Jalankan di background thread (MULTI-THREADING)
            KalkulatorWorker worker = new KalkulatorWorker(tipe, params, new KalkulatorWorker.KalkulatorCallback() {
                @Override
                public void onProgress(String message) {
                    lblStatus.setText(message);
                }

                @Override
                public void onSelesai(String hasil) {
                    txtHasil.setText(hasil);
                    lblStatus.setText("✓ Perhitungan selesai!");
                    lblStatus.setForeground(WARNA_SUKSES);
                    progressBar.setVisible(false);
                    btnHitung.setEnabled(true);
                }

                @Override
                public void onError(String pesan) {
                    tampilkanError(pesan);
                    progressBar.setVisible(false);
                    btnHitung.setEnabled(true);
                }
            });

            worker.execute(); // mulai background thread

        } catch (NumberFormatException e) {
            tampilkanError("Input tidak valid! Masukkan angka yang benar.");
        }
    }

    private void onReset() {
        txtDiagonal1.setText("");
        txtDiagonal2.setText("");
        txtTinggi.setText("");
        txtHasil.setText("Hasil akan muncul di sini...");
        lblStatus.setText("Form direset.");
        lblStatus.setForeground(new Color(100, 110, 120));
        progressBar.setVisible(false);
        btnHitung.setEnabled(true);
    }

    // ==================== HELPER UI ====================

    private JPanel buatKartu(String judul) {
        JPanel kartu = new JPanel();
        kartu.setLayout(new BoxLayout(kartu, BoxLayout.Y_AXIS));
        kartu.setBackground(WARNA_KARTU);
        kartu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 230, 240)),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(6, 12, 12, 12),
                judul,
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                WARNA_PRIMER
            )
        ));
        return kartu;
    }

    private JTextField buatTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    private JButton buatTombol(String teks, Color warna) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(warna);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));
        return btn;
    }

    private JPanel buatBarisinput(String labelTeks, JTextField field) {
        JLabel lbl = new JLabel(labelTeks);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(WARNA_TEKS);
        return buatBarisinputLabeled(lbl, field);
    }

    private JPanel buatBarisinputLabeled(JLabel lbl, JTextField field) {
        JPanel baris = new JPanel(new BorderLayout(8, 0));
        baris.setOpaque(false);
        baris.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        baris.add(lbl, BorderLayout.WEST);
        lbl.setPreferredSize(new Dimension(200, 28));
        baris.add(field, BorderLayout.CENTER);
        return baris;
    }

    private void tampilkanError(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Input Tidak Valid", JOptionPane.ERROR_MESSAGE);
        lblStatus.setText("Error: " + pesan);
        lblStatus.setForeground(Color.RED);
    }

    // ==================== MAIN ====================
    public static void main(String[] args) {
        // Jalankan GUI di Event Dispatch Thread (EDT) - best practice Swing
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new ProjekMateri().setVisible(true);
        });
    }
}
