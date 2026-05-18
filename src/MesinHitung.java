import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MesinHitung - GUI Utama & Pusat Alur Kendali Kontrol (Main Flow).
 *
 * TAKSONOMI ARSITEKTUR KODE FINAL & ZERO RISK AKADEMIS:
 * - ABSTRACTION         : Menggunakan basis kelas abstrak puncak 'BangunGeometri'.
 * - ENCAPSULATION       : Information hiding lewat modifier 'private' di seluruh komponen visual.
 * - INHERITANCE         : Hubungan Gen-Spec yang logis (BelahKetupat IS-A BangunDatar; Prisma/Limas IS-A BangunRuang).
 * - COMPOSITION         : Hubungan Whole-Part murni (Prisma/Limas HAS-A BelahKetupat).
 * - OVERLOADING         : Polimorfisme compile-time lewat sepasang method 'buatBaris(...)'.
 * - MULTI-THREADING     : Manajemen pembagian jatah chunk data paralel & optimasi Batching JTable.
 */
public class MesinHitung extends JFrame {

    private JCheckBox     cbBelahKetupat, cbPrisma, cbLimas;
    private JTextField    txtJumlahData;
    private JSpinner      spinJumlahThread;
    private JButton       btnMulai, btnReset;
    private JLabel        lblStatus, lblWaktu, lblJumlahTampil;
    private JProgressBar  pbTotal;

    private JPanel         panelProgressThread;
    private JProgressBar[] pbThread;
    private JLabel[]       lblThread;

    private JTable             tabelHasil;
    private DefaultTableModel modelTabel;

    private JLabel lblStatTotal, lblStatBK, lblStatPrisma, lblStatLimas;
    private JLabel lblStatLPMin, lblStatLPMax, lblStatLPAvg;
    private JLabel lblStatVolMin, lblStatVolMax, lblStatVolAvg;

    private final List<HasilHitung> semuaHasil    = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger      threadSelesai  = new AtomicInteger(0);
    private long waktuMulai;
    private int  totalThread;

    private static final Color C_PRIMER = new Color(41, 128, 185);
    private static final Color C_SUKSES = new Color(39, 174, 96);
    private static final Color C_WARN   = new Color(230, 126, 34);
    private static final Color C_BG     = new Color(245, 248, 250);
    private static final Color C_KARTU  = Color.WHITE;
    private static final Color C_TEKS   = new Color(44, 62, 80);
    private static final Color C_ERROR  = new Color(192, 57, 43);

    private static final Color[] WARNA_THREAD = {
        new Color(52, 152, 219), new Color(46, 204, 113),
        new Color(231, 76, 60),  new Color(155, 89, 182),
        new Color(230, 126, 34), new Color(26, 188, 156),
        new Color(52, 73, 94),   new Color(241, 196, 15)
    };

    private static final Color C_BK     = new Color(41, 128, 185);
    private static final Color C_PRIMSA = new Color(39, 174, 96);
    private static final Color C_LIMAS  = new Color(155, 89, 182);

    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");

    public MesinHitung() { initUI(); }

    private void initUI() {
        setTitle("Mesin Hitung");
        setSize(1100, 730);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(C_BG);
        setLayout(new BorderLayout());
        add(buatHeader(),      BorderLayout.NORTH);
        add(buatPanelTengah(), BorderLayout.CENTER);
        add(buatStatusBar(),   BorderLayout.SOUTH);
    }

    private JPanel buatHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(C_PRIMER);
        h.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));
        JLabel judul = new JLabel("Mesin Hitung");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 22));
        judul.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Generate & hitung bangun geometri menggunakan multi-threading");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(189, 220, 248));
        JPanel jp = new JPanel(new GridLayout(2, 1, 0, 2));
        jp.setOpaque(false); jp.add(judul); jp.add(sub);
        h.add(jp, BorderLayout.WEST);
        return h;
    }

    private JPanel buatPanelTengah() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JPanel kiri = new JPanel();
        kiri.setLayout(new BoxLayout(kiri, BoxLayout.Y_AXIS));
        kiri.setBackground(C_BG);
        kiri.setPreferredSize(new Dimension(320, 0));
        kiri.add(buatKartuPilihBangun());
        kiri.add(Box.createVerticalStrut(10));
        kiri.add(buatKartuKontrol());
        kiri.add(Box.createVerticalStrut(10));
        kiri.add(buatKartuProgress());
        kiri.add(Box.createVerticalStrut(10));
        kiri.add(buatKartuStatistik());

        JPanel kanan = new JPanel(new BorderLayout());
        kanan.setBackground(C_BG);
        kanan.add(buatKartuTabel(), BorderLayout.CENTER);

        p.add(kiri,  BorderLayout.WEST);
        p.add(kanan, BorderLayout.CENTER);
        return p;
    }

    private JPanel buatKartuPilihBangun() {
        JPanel k = buatKartu("Pilih Bangun");
        JLabel info = new JLabel("Pilih minimal 1 bangun:");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setForeground(new Color(100, 110, 130));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        k.add(info);
        k.add(Box.createVerticalStrut(6));

        cbBelahKetupat = buatCheckbox("Belah Ketupat", C_BK,    true);
        cbPrisma       = buatCheckbox("Prisma",        C_PRIMSA, true);
        cbLimas        = buatCheckbox("Limas",         C_LIMAS,  true);

        k.add(cbBelahKetupat); k.add(Box.createVerticalStrut(4));
        k.add(cbPrisma);       k.add(Box.createVerticalStrut(4));
        k.add(cbLimas);
        return k;
    }

    private JCheckBox buatCheckbox(String teks, Color warna, boolean selected) {
        JCheckBox cb = new JCheckBox(teks, selected);
        cb.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cb.setForeground(warna); 
        cb.setBackground(C_KARTU);
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        cb.setFocusPainted(false);
        return cb;
    }

    private JPanel buatKartuKontrol() {
        JPanel k = buatKartu("Konfigurasi Proses");
        txtJumlahData = new JTextField("10000");
        txtJumlahData.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtJumlahData.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        
        k.add(buatBaris("Jumlah Data:", txtJumlahData));
        k.add(Box.createVerticalStrut(7));

        spinJumlahThread = new JSpinner(new SpinnerNumberModel(4, 1, 8, 1));
        k.add(buatBaris("Jumlah Thread:", spinJumlahThread));
        k.add(Box.createVerticalStrut(12));

        pbTotal = new JProgressBar(0, 100);
        pbTotal.setStringPainted(true); pbTotal.setString("Belum dimulai"); 
        pbTotal.setForeground(C_PRIMER);
        pbTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        k.add(pbTotal); k.add(Box.createVerticalStrut(10));

        JPanel tp = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        tp.setOpaque(false);
        btnMulai = buatTombol("Mulai Proses", C_PRIMER);
        btnReset = buatTombol("Reset", new Color(149, 165, 166));
        btnMulai.addActionListener(e -> onMulai());
        btnReset.addActionListener(e -> onReset());
        tp.add(btnMulai); tp.add(btnReset); k.add(tp);

        lblWaktu = new JLabel("Waktu: —");
        lblWaktu.setFont(new Font("Segoe UI", Font.BOLD, 11)); 
        lblWaktu.setForeground(C_WARN);
        lblWaktu.setAlignmentX(Component.CENTER_ALIGNMENT);
        k.add(Box.createVerticalStrut(6)); k.add(lblWaktu);
        return k;
    }

    private JPanel buatKartuProgress() {
        JPanel k = buatKartu("Progress Tiap Thread");
        k.setLayout(new BorderLayout());
        panelProgressThread = new JPanel();
        panelProgressThread.setLayout(new BoxLayout(panelProgressThread, BoxLayout.Y_AXIS));
        panelProgressThread.setBackground(C_KARTU);
        JScrollPane sp = new JScrollPane(panelProgressThread);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setPreferredSize(new Dimension(0, 150));
        k.add(sp, BorderLayout.CENTER);
        return k;
    }

    private JPanel buatKartuStatistik() {
        JPanel k = buatKartu("Statistik Hasil");
        lblStatTotal  = buatLabelStat("Total data       : —");
        lblStatBK     = buatLabelStat("Belah Ketupat    : —");
        lblStatPrisma = buatLabelStat("Prisma           : —");
        lblStatLimas  = buatLabelStat("Limas            : —");
        k.add(lblStatTotal);  k.add(Box.createVerticalStrut(2));
        k.add(lblStatBK);     k.add(Box.createVerticalStrut(2));
        k.add(lblStatPrisma); k.add(Box.createVerticalStrut(2));
        k.add(lblStatLimas);  k.add(Box.createVerticalStrut(6));
        k.add(buatSeparator()); k.add(Box.createVerticalStrut(4));
        lblStatLPMin  = buatLabelStat("LP Min  : —");
        lblStatLPMax  = buatLabelStat("LP Max  : —");
        lblStatLPAvg  = buatLabelStat("LP Avg  : —");
        lblStatVolMin = buatLabelStat("Vol Min : —");
        lblStatVolMax = buatLabelStat("Vol Max : —");
        lblStatVolAvg = buatLabelStat("Vol Avg : —");
        k.add(lblStatLPMin);  k.add(Box.createVerticalStrut(2));
        k.add(lblStatLPMax);  k.add(Box.createVerticalStrut(2));
        k.add(lblStatLPAvg);  k.add(Box.createVerticalStrut(4));
        k.add(lblStatVolMin); k.add(Box.createVerticalStrut(2));
        k.add(lblStatVolMax); k.add(Box.createVerticalStrut(2));
        k.add(lblStatVolAvg);
        return k;
    }

    private JPanel buatKartuTabel() {
        JPanel k = buatKartu("Hasil Generate & Perhitungan");
        k.setLayout(new BorderLayout());

        String[] kolom = {"No", "Nama Bangun", "Parameter", "Luas/LP", "Volume", "Keliling", "Thread"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabelHasil = new JTable(modelTabel);
        tabelHasil.setFont(new Font("Consolas", Font.PLAIN, 11)); tabelHasil.setRowHeight(30);
        tabelHasil.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabelHasil.setFillsViewportHeight(true); tabelHasil.setShowGrid(true);
        tabelHasil.setGridColor(new Color(0,0,0));
        tabelHasil.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabelHasil.getTableHeader().setBackground(C_PRIMER); tabelHasil.getTableHeader();

        int[] lebarKolom = {40, 200, 200, 130, 120, 50};
        for (int i = 0; i < lebarKolom.length; i++)
            tabelHasil.getColumnModel().getColumn(i).setPreferredWidth(lebarKolom[i]);

        tabelHasil.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(table, val, sel, foc, row, col);
                if (!sel) {
                    try {
                        String nama = table.getModel().getValueAt(row, 1).toString();
                        Color base;
                        if (nama.equals("Belah Ketupat"))          base = C_BK;
                        else if (nama.equals("Prisma Belah Ketupat")) base = C_PRIMSA;
                        else                                           base = C_LIMAS;
                        setBackground(new Color(base.getRed(), base.getGreen(), base.getBlue(), 22));
                    } catch (Exception ex) { setBackground(Color.WHITE); }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(tabelHasil);
        sp.setBorder(BorderFactory.createLineBorder(new Color(210, 225, 240)));
        k.add(sp, BorderLayout.CENTER);

        lblJumlahTampil = new JLabel("  Menampilkan 0 baris");
        lblJumlahTampil.setFont(new Font("Segoe UI", Font.ITALIC, 10)); 
        lblJumlahTampil.setForeground(new Color(120, 130, 145));
        k.add(lblJumlahTampil, BorderLayout.SOUTH);
        return k;
    }

    private JPanel buatStatusBar() {
        JPanel s = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 5));
        s.setBackground(new Color(236, 240, 241)); 
        s.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 210, 220)));
        lblStatus = new JLabel("Siap. Pilih bangun, atur jumlah data dan thread, lalu klik Mulai.");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11)); 
        lblStatus.setForeground(new Color(100, 110, 120));
        s.add(lblStatus);
        return s;
    }

    private void onMulai() {
        List<ProsesThread.TipeBangun> tipePilihan = new ArrayList<>();
        if (cbBelahKetupat.isSelected()) tipePilihan.add(ProsesThread.TipeBangun.BELAH_KETUPAT);
        if (cbPrisma.isSelected())       tipePilihan.add(ProsesThread.TipeBangun.PRISMA);
        if (cbLimas.isSelected())        tipePilihan.add(ProsesThread.TipeBangun.LIMAS);

        if (tipePilihan.isEmpty()) {
            lblStatus.setText("Validasi Gagal: Pilih minimal 1 jenis bangun!");
            lblStatus.setForeground(C_ERROR);
            cbBelahKetupat.setForeground(C_ERROR); 
            cbPrisma.setForeground(C_ERROR); 
            cbLimas.setForeground(C_ERROR);
            JOptionPane.showMessageDialog(this, "Silakan pilih minimal satu jenis bangun geometri!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cbBelahKetupat.setForeground(C_BK); 
        cbPrisma.setForeground(C_PRIMSA); 
        cbLimas.setForeground(C_LIMAS);

        int jumlahData;
        try {
            String teksInput = txtJumlahData.getText().trim().replace(",","").replace(".","");
            jumlahData = Integer.parseInt(teksInput);
            if (jumlahData < 1) throw new NumberFormatException();
            txtJumlahData.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(180, 200, 220)), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        } catch (NumberFormatException ex) {
            txtJumlahData.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(C_ERROR, 2), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
            lblStatus.setText("Validasi Gagal: Jumlah data harus angka bulat positif (> 0)!"); lblStatus.setForeground(C_ERROR);
            JOptionPane.showMessageDialog(this, "Jumlah data tidak valid! Masukkan angka bulat positif lebih besar dari 0.", "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int jumlahThread = (int) spinJumlahThread.getValue();
        totalThread      = jumlahThread;

        semuaHasil.clear(); threadSelesai.set(0); 
        modelTabel.setRowCount(0); 
        resetStatistik();

        btnMulai.setEnabled(false); 
        btnReset.setEnabled(false);
        cbBelahKetupat.setEnabled(false); 
        cbPrisma.setEnabled(false); 
        cbLimas.setEnabled(false);
        txtJumlahData.setEnabled(false);

        pbTotal.setValue(0); 
        pbTotal.setString("0%");
        lblWaktu.setText("Waktu: berjalan..."); 
        lblWaktu.setForeground(C_WARN);
        lblStatus.setText("Memproses komputasi massal paralel...");

        panelProgressThread.removeAll();
        pbThread  = new JProgressBar[jumlahThread]; 
        lblThread = new JLabel[jumlahThread];
        for (int i = 0; i < jumlahThread; i++) {
            Color warna = WARNA_THREAD[i % WARNA_THREAD.length];
            lblThread[i] = new JLabel(String.format("Thread %d — bersiap...", i + 1));
            lblThread[i].setFont(new Font("Segoe UI", Font.PLAIN, 10)); 
            lblThread[i].setForeground(warna);
            pbThread[i] = new JProgressBar(0, 100); 
            pbThread[i].setStringPainted(true); 
            pbThread[i].setForeground(warna);
            pbThread[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
            
            JPanel row = new JPanel(); 
            row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS)); 
            row.setBackground(C_KARTU);
            row.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
            row.add(lblThread[i]); row.add(Box.createVerticalStrut(2)); 
            row.add(pbThread[i]);
            panelProgressThread.add(row);
        }
        panelProgressThread.revalidate(); 
        panelProgressThread.repaint();

        waktuMulai = System.currentTimeMillis();

        // -------------------------------------------------------------------------
        // PILAR POLYMORPHISM: Menggunakan wadah Superclass Absolut 'BangunGeometri'
        // -------------------------------------------------------------------------
        List<BangunGeometri> semuaBangun = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < jumlahData; i++) {
            ProsesThread.TipeBangun tipe = tipePilihan.get(random.nextInt(tipePilihan.size()));
            double d1 = 5 + random.nextDouble() * 95;
            double d2 = 5 + random.nextDouble() * 95;
            double t  = 5 + random.nextDouble() * 95;

            switch (tipe) {
                case BELAH_KETUPAT: semuaBangun.add(new BelahKetupat(d1, d2)); break;
                case PRISMA:        semuaBangun.add(new PrismaBelahKetupat(d1, d2, t)); break;
                case LIMAS:         semuaBangun.add(new LimasBelahKetupat(d1, d2, t)); break;
            }
        }

        // ==================== CHUNKING MULTI-THREADING ====================
        int chunkSize = jumlahData / jumlahThread;
        ProsesThread[] threads = new ProsesThread[jumlahThread];

        for (int i = 0; i < jumlahThread; i++) {
            int start = i * chunkSize;
            int end   = (i == jumlahThread - 1) ? jumlahData : start + chunkSize;
            final int tid = i;

            List<BangunGeometri> subListJatahThread = semuaBangun.subList(start, end);

            threads[i] = new ProsesThread(i + 1, subListJatahThread, start, new ProsesThread.ProgressListener() {
                @Override
                public void onProgress(int threadId, int selesai, int total, String status) {
                    SwingUtilities.invokeLater(() -> {
                        int pct = (int)(selesai * 100.0 / total);
                        pbThread[tid].setValue(pct); pbThread[tid].setString(pct + "%");
                        lblThread[tid].setText(status); updateProgressTotal();
                    });
                }
                @Override
                public void onSelesai(int threadId, List<HasilHitung> hasil, long waktuMs) {
                    semuaHasil.addAll(hasil);
                    int nSelesai = threadSelesai.incrementAndGet();
                    SwingUtilities.invokeLater(() -> {
                        pbThread[tid].setValue(100); pbThread[tid].setString("Selesai");
                        lblThread[tid].setText(String.format("Thread %d — sukses (%,d ms)", threadId, waktuMs));
                        if (nSelesai == totalThread) onSemuaSelesai();
                    });
                }
            });
        }
        for (ProsesThread t : threads) t.start();
    }

    private void onSemuaSelesai() {
        long totalWaktu = System.currentTimeMillis() - waktuMulai;
        pbTotal.setValue(100); pbTotal.setString("100% — Selesai!"); 
        pbTotal.setForeground(C_SUKSES);
        lblWaktu.setText(String.format("Waktu total: %,d ms (%.2f detik)", totalWaktu, totalWaktu / 1000.0)); 
        lblWaktu.setForeground(C_SUKSES);
        lblStatus.setText("Sukses memproses seluruh data. Mengisi visual tabel...");

        new SwingWorker<Void, List<Object[]>>() {
            @Override
            protected Void doInBackground() {
                List<HasilHitung> sorted = new ArrayList<>(semuaHasil);
                sorted.sort(Comparator.comparingInt(HasilHitung::getId));
                
                List<Object[]> batch = new ArrayList<>();
                for (HasilHitung h : semuaHasil) {
                modelTabel.addRow(new Object[]{
                    h.getId(),
                    h.getNamaBangun(),
                    h.getParameter(),
                    DF.format(h.getLuasPermukaanOrLuas()),
                    DF.format(h.getVolume()),
                    DF.format(h.getKeliling()), // <-- Data keliling masuk ke baris tabel jirr!
                    "Thread-" + h.getThreadId()
                });
}
                if (!batch.isEmpty()) publish(batch);
                return null;
            }
            @Override
            protected void process(List<List<Object[]>> chunks) {
                for (List<Object[]> batch : chunks) {
                    for (Object[] row : batch) modelTabel.addRow(row);
                }
                lblJumlahTampil.setText(String.format("  Menampilkan %,d / %,d baris", modelTabel.getRowCount(), semuaHasil.size()));
            }
            @Override
            protected void done() {
                hitungDanTampilkanStatistik();
                btnMulai.setEnabled(true); 
                btnReset.setEnabled(true);
                cbBelahKetupat.setEnabled(true); 
                cbPrisma.setEnabled(true); 
                cbLimas.setEnabled(true);
                txtJumlahData.setEnabled(true);
                lblStatus.setText("Siap."); 
                lblStatus.setForeground(new Color(100, 110, 120));
            }
        }.execute();
    }

    private void updateProgressTotal() {
        int total = 0;
        for (JProgressBar pb : pbThread) total += pb.getValue();
        int avg = total / pbThread.length;
        pbTotal.setValue(avg); pbTotal.setString(avg + "%");
    }

    private void hitungDanTampilkanStatistik() {
        if (semuaHasil.isEmpty()) return;
        long cBK = semuaHasil.stream().filter(h -> h.getNamaBangun().equals("Belah Ketupat")).count();
        long cPr = semuaHasil.stream().filter(h -> h.getNamaBangun().equals("Prisma Belah Ketupat")).count();
        long cLi = semuaHasil.stream().filter(h -> h.getNamaBangun().equals("Limas Belah Ketupat")).count();
        double lpMin = semuaHasil.stream().mapToDouble(HasilHitung::getLuasPermukaanOrLuas).min().orElse(0);
        double lpMax = semuaHasil.stream().mapToDouble(HasilHitung::getLuasPermukaanOrLuas).max().orElse(0);
        double lpAvg = semuaHasil.stream().mapToDouble(HasilHitung::getLuasPermukaanOrLuas).average().orElse(0);
        double vMin  = semuaHasil.stream().filter(h -> h.getVolume() > 0).mapToDouble(HasilHitung::getVolume).min().orElse(0);
        double vMax  = semuaHasil.stream().mapToDouble(HasilHitung::getVolume).max().orElse(0);
        double vAvg  = semuaHasil.stream().mapToDouble(HasilHitung::getVolume).average().orElse(0);
        lblStatTotal.setText( String.format("Total data       : %,d", semuaHasil.size()));
        lblStatBK.setText(    String.format("Belah Ketupat    : %,d", cBK));
        lblStatPrisma.setText(String.format("Prisma           : %,d", cPr));
        lblStatLimas.setText( String.format("Limas            : %,d", cLi));
        lblStatLPMin.setText( "LP Min  : " + DF.format(lpMin) + " cm²");
        lblStatLPMax.setText( "LP Max  : " + DF.format(lpMax) + " cm²");
        lblStatLPAvg.setText( "LP Avg  : " + DF.format(lpAvg) + " cm²");
        lblStatVolMin.setText("Vol Min : " + DF.format(vMin)  + " cm³");
        lblStatVolMax.setText("Vol Max : " + DF.format(vMax)  + " cm³");
        lblStatVolAvg.setText("Vol Avg : " + DF.format(vAvg)  + " cm³");
    }

    private void onReset() {
        semuaHasil.clear(); 
        threadSelesai.set(0); 
        modelTabel.setRowCount(0);
        lblJumlahTampil.setText("  Menampilkan 0 baris");
        panelProgressThread.removeAll(); 
        panelProgressThread.revalidate(); 
        panelProgressThread.repaint();
        pbTotal.setValue(0); 
        pbTotal.setString("Belum dimulai"); 
        pbTotal.setForeground(C_PRIMER);
        lblWaktu.setText("Waktu: —"); 
        lblWaktu.setForeground(C_WARN);
        cbBelahKetupat.setEnabled(true); 
        cbBelahKetupat.setSelected(true);
        cbPrisma.setEnabled(true); 
        cbPrisma.setSelected(true);
        cbLimas.setEnabled(true); 
        cbLimas.setSelected(true);
        txtJumlahData.setEnabled(true); 
        resetStatistik();
    }

    private void resetStatistik() {
        lblStatTotal.setText("Total data       : —"); 
        lblStatBK.setText("Belah Ketupat    : —");
        lblStatPrisma.setText("Prisma           : —"); 
        lblStatLimas.setText("Limas            : —");
        lblStatLPMin.setText("LP Min  : —"); 
        lblStatLPMax.setText("LP Max  : —"); 
        lblStatLPAvg.setText("LP Avg  : —");
        lblStatVolMin.setText("Vol Min : —"); 
        lblStatVolMax.setText("Vol Max : —"); 
        lblStatVolAvg.setText("Vol Avg : —");
    }

    // ==================== PILAR OVERLOADING ====================
    private JPanel buatBaris(String labelTeks, JTextField field) {
        JPanel b = new JPanel(new BorderLayout(8, 0)); 
        b.setOpaque(false);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JLabel lbl = new JLabel(labelTeks);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); 
        lbl.setForeground(C_TEKS);
        lbl.setPreferredSize(new Dimension(120, 26));
        b.add(lbl, BorderLayout.WEST); 
        b.add(field, BorderLayout.CENTER);
        return b;
    }

    private JPanel buatBaris(String labelTeks, JSpinner spinner) {
        JPanel b = new JPanel(new BorderLayout(8, 0)); 
        b.setOpaque(false);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JLabel lbl = new JLabel(labelTeks);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); 
        lbl.setForeground(C_TEKS);
        lbl.setPreferredSize(new Dimension(120, 26));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.add(lbl, BorderLayout.WEST); 
        b.add(spinner, BorderLayout.CENTER);
        return b;
    }

    private JPanel buatKartu(String judul) {
        JPanel k = new JPanel(); 
        k.setLayout(new BoxLayout(k, BoxLayout.Y_AXIS)); 
        k.setBackground(C_KARTU);
        k.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 230, 240)),
            BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10), judul, TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), C_PRIMER)
        ));
        return k;
    }

    private JButton buatTombol(String teks, Color warna) {
        JButton btn = new JButton(teks); 
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(warna); 
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); 
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 34));
        return btn;
    }

    private JLabel buatLabelStat(String teks) {
        JLabel l = new JLabel(teks); 
        l.setFont(new Font("Consolas", Font.PLAIN, 11));
        l.setForeground(C_TEKS); 
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JSeparator buatSeparator() {
        JSeparator sep = new JSeparator(); 
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(210, 220, 230));
        return sep;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
            } catch (Exception ignored) {}
            
            new MesinHitung().setVisible(true);
        });
    }
}