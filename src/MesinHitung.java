import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.*; // Tambahan import untuk DocumentFilter
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MesinHitung extends JFrame {

    private JCheckBox cbBelahKetupat;
    private JCheckBox cbPrisma;
    private JCheckBox cbLimas;
    private JTextField txtJumlahData;
    private JButton btnMulai;
    private JButton btnReset;
    private JLabel lblStatus;
    private JLabel lblWaktu;
    private JLabel lblJumlahTampil;
    private JProgressBar pbTotal;

    private JPanel panelProgressThread;
    
    private final Map<String, JProgressBar> mapPb = new HashMap<>();
    private final Map<String, JLabel> mapLbl = new HashMap<>();
    
    private final Map<String, Integer> counterPerThread = new HashMap<>();

    private JTable tabelHasil;
    private DefaultTableModel modelTabel;

    private JLabel lblStatTotal;
    private JLabel lblStatBK;
    private JLabel lblStatPrisma;
    private JLabel lblStatLimas;
    private JLabel lblStatLPMin;
    private JLabel lblStatLPMax;
    private JLabel lblStatLPAvg;
    private JLabel lblStatVolMin;
    private JLabel lblStatVolMax;
    private JLabel lblStatVolAvg;

    private final List<BangunGeometri> semuaHasil = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger threadSelesai = new AtomicInteger(0);
    
    private int totalDataTarget = 0;
    private long waktuMulaiGlobal = 0;
    private int jumlahThreadAktif = 0;

    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");
    private static final Color C_PRIMER = new Color(41, 128, 185);
    private static final Color C_WARN   = new Color(230, 126, 34);
    private static final Color C_BG     = new Color(245, 248, 250);
    private static final Color C_KARTU  = Color.WHITE;
    private static final Color C_TEKS   = new Color(44, 62, 80);

    private static final Color[] WARNA_THREAD = {
        new Color(41, 128, 185), 
        new Color(39, 174, 96),  
        new Color(155, 89, 182)  
    };

    public MesinHitung() {
        initUI();
    }

    private void initUI() {
        setTitle("Mesin Hitung (Gen-Spec OOP & Multi-Threading)");
        setSize(1100, 730); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(C_BG);
        setLayout(new BorderLayout());

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
        
        kiri.add(Box.createVerticalGlue()); 

        JPanel kanan = new JPanel(new BorderLayout());
        kanan.setBackground(C_BG);
        kanan.add(buatKartuTabel(), BorderLayout.CENTER);

        p.add(kiri, BorderLayout.WEST);
        p.add(kanan, BorderLayout.CENTER);
        
        add(p, BorderLayout.CENTER);
        add(buatStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buatKartuPilihBangun() {
        JPanel k = buatKartu("Pilih Bangun (Master Thread)");
        
        cbBelahKetupat = buatCheckbox("Thread 1: Belah Ketupat", WARNA_THREAD[0]);
        cbPrisma       = buatCheckbox("Thread 2: Prisma BK", WARNA_THREAD[1]);
        cbLimas        = buatCheckbox("Thread 3: Limas BK", WARNA_THREAD[2]);

        k.add(cbBelahKetupat);
        k.add(Box.createVerticalStrut(4));
        k.add(cbPrisma);
        k.add(Box.createVerticalStrut(4));
        k.add(cbLimas);
        
        return k;
    }

    private JCheckBox buatCheckbox(String teks, Color warna) {
        JCheckBox cb = new JCheckBox(teks, true);
        cb.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cb.setForeground(warna); 
        cb.setBackground(C_KARTU);
        cb.setFocusPainted(false);
        return cb;
    }

    private JPanel buatKartuKontrol() {
        JPanel k = buatKartu("Konfigurasi Proses");
        
        txtJumlahData = new JTextField("3000");
        txtJumlahData.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtJumlahData.setPreferredSize(new Dimension(120, 26)); 
        
        // --- PENGAMAN 1: BLOKIR FISIK INPUT MINUS DAN HURUF ---
        ((AbstractDocument) txtJumlahData.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                // Regex \d* memastikan hanya angka (0-9) yang bisa masuk
                if (text != null && text.matches("\\d*")) { 
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    Toolkit.getDefaultToolkit().beep(); // Bunyi 'beep' kalau user maksa ngetik huruf/minus
                }
            }
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string != null && string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        // --------------------------------------------------------

        JPanel wrapInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapInput.setOpaque(false);
        wrapInput.add(txtJumlahData);

        k.add(buatBaris("Total Kuota Data:", wrapInput));
        k.add(Box.createVerticalStrut(10));

        pbTotal = new JProgressBar(0, 100);
        pbTotal.setStringPainted(true);
        pbTotal.setString("Belum dimulai");
        pbTotal.setForeground(C_PRIMER);
        pbTotal.setPreferredSize(new Dimension(280, 24)); 
        pbTotal.setMaximumSize(new Dimension(280, 24)); 
        pbTotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        k.add(pbTotal); 
        k.add(Box.createVerticalStrut(4));

        lblWaktu = new JLabel("Waktu total: —");
        lblWaktu.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblWaktu.setForeground(C_WARN);
        lblWaktu.setAlignmentX(Component.CENTER_ALIGNMENT);
        k.add(lblWaktu);
        k.add(Box.createVerticalStrut(10));

        JPanel tp = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        tp.setOpaque(false);
        
        btnMulai = buatTombol("Mulai Komputasi", C_PRIMER);
        btnReset = buatTombol("Reset", new Color(149, 165, 166));
        
        btnMulai.addActionListener(e -> onMulai());
        btnReset.addActionListener(e -> onReset());
        
        tp.add(btnMulai);
        tp.add(btnReset);
        k.add(tp);
        
        return k;
    }

    private JPanel buatKartuProgress() {
        JPanel k = buatKartu("Progress Komputasi Thread");
        k.setLayout(new BorderLayout());
        
        panelProgressThread = new JPanel();
        panelProgressThread.setLayout(new BoxLayout(panelProgressThread, BoxLayout.Y_AXIS));
        panelProgressThread.setBackground(C_KARTU);
        
        JScrollPane sp = new JScrollPane(panelProgressThread);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setPreferredSize(new Dimension(0, 130)); 
        k.add(sp, BorderLayout.CENTER);
        
        return k;
    }

    private JPanel buatKartuStatistik() {
        JPanel k = buatKartu("Statistik (Membaca Atribut Langsung)");
        
        lblStatTotal  = buatLabelStat("Total Data : —");
        lblStatBK     = buatLabelStat("BK         : —");
        lblStatPrisma = buatLabelStat("Prisma     : —");
        lblStatLimas  = buatLabelStat("Limas      : —");
        
        k.add(lblStatTotal);
        k.add(Box.createVerticalStrut(2));
        k.add(lblStatBK);
        k.add(Box.createVerticalStrut(2));
        k.add(lblStatPrisma);
        k.add(Box.createVerticalStrut(2));
        k.add(lblStatLimas);
        k.add(Box.createVerticalStrut(6));
        
        JSeparator sep = new JSeparator(); 
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        k.add(sep);
        k.add(Box.createVerticalStrut(4));

        lblStatLPMin  = buatLabelStat("LP Min     : —");
        lblStatLPMax  = buatLabelStat("LP Max     : —");
        lblStatLPAvg  = buatLabelStat("LP Avg     : —");
        lblStatVolMin = buatLabelStat("Vol Min    : —");
        lblStatVolMax = buatLabelStat("Vol Max    : —");
        lblStatVolAvg = buatLabelStat("Vol Avg    : —");
        
        k.add(lblStatLPMin);
        k.add(lblStatLPMax);
        k.add(lblStatLPAvg);
        k.add(Box.createVerticalStrut(4));
        k.add(lblStatVolMin);
        k.add(lblStatVolMax);
        k.add(lblStatVolAvg);
        
        return k;
    }

    private JPanel buatKartuTabel() {
        JPanel k = buatKartu("Hasil Objek Thread");
        k.setLayout(new BorderLayout());
        
        String[] kolom = {"ID", "Nama Bangun", "Parameter", "Luas/LP", "Volume", "Keliling"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        
        tabelHasil = new JTable(modelTabel);
        tabelHasil.setFont(new Font("Consolas", Font.PLAIN, 11)); 
        tabelHasil.setRowHeight(30);
        tabelHasil.setGridColor(Color.BLACK); 

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(230, 230, 230)); 
        headerRenderer.setForeground(Color.BLACK);              
        headerRenderer.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);   
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        for (int i = 0; i < modelTabel.getColumnCount(); i++) {
            tabelHasil.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        tabelHasil.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(table, val, sel, foc, row, col);
                
                if (!sel) {
                    try {
                        String nama = table.getModel().getValueAt(row, 1).toString();
                        Color base;
                        
                        if (nama.equals("Belah Ketupat")) {
                            base = WARNA_THREAD[0];
                        } else if (nama.equals("Prisma Belah Ketupat")) {
                            base = WARNA_THREAD[1];
                        } else {
                            base = WARNA_THREAD[2];
                        }
                        
                        setBackground(new Color(base.getRed(), base.getGreen(), base.getBlue(), 22));
                    } catch (Exception ex) {
                        setBackground(Color.WHITE);
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(tabelHasil);
        sp.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
        k.add(sp, BorderLayout.CENTER);
        
        lblJumlahTampil = new JLabel("  0 baris data");
        lblJumlahTampil.setFont(new Font("Segoe UI", Font.ITALIC, 10)); 
        k.add(lblJumlahTampil, BorderLayout.SOUTH);
        
        return k;
    }

    private JPanel buatStatusBar() {
        JPanel s = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 5));
        s.setBackground(new Color(236, 240, 241)); 
        s.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 210, 220)));
        
        lblStatus = new JLabel("Siap memulai komputasi paralel.");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11)); 
        lblStatus.setForeground(C_WARN);
        s.add(lblStatus); 
        
        return s;
    }

    public void logStatus(String pesan) {
        SwingUtilities.invokeLater(() -> {
            lblStatus.setText(pesan);
        });
    }

    public void laporThreadSelesai() {
        int selesai = threadSelesai.incrementAndGet();
        if (selesai == jumlahThreadAktif) {
            SwingUtilities.invokeLater(() -> {
                onSemuaSelesai();
            });
        }
    }

    private void tambahProgressUI(String nama, Color warna, int maxData, int index) {
        JLabel lbl = new JLabel(String.format("Thread %d: %s — bersiap...", index, nama));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10)); 
        lbl.setForeground(warna);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT); 
        
        JProgressBar pb = new JProgressBar(0, maxData);
        pb.setStringPainted(true);
        pb.setForeground(warna);
        
        pb.setPreferredSize(new Dimension(180, 16)); 
        pb.setMaximumSize(new Dimension(180, 16));   
        pb.setAlignmentX(Component.LEFT_ALIGNMENT);  
        
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBackground(C_KARTU);
        row.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8)); 
        
        row.add(lbl);
        row.add(Box.createVerticalStrut(2));
        row.add(pb);
        
        JPanel wrapRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapRow.setBackground(C_KARTU);
        wrapRow.add(row);
        
        panelProgressThread.add(wrapRow);
        mapPb.put(nama, pb);
        mapLbl.put(nama, lbl);
    }

    private void onMulai() {
        semuaHasil.clear(); 
        modelTabel.setRowCount(0); 
        threadSelesai.set(0);
        pbTotal.setValue(0); 
        pbTotal.setString("0%");
        
        int terpilih = 0;
        if (cbBelahKetupat.isSelected()) terpilih++;
        if (cbPrisma.isSelected()) terpilih++;
        if (cbLimas.isSelected()) terpilih++;

        if (terpilih == 0) {
            logStatus("Pilih minimal 1 bangun!"); 
            return;
        }

        int jatahPerThread = 0;
        try {
            int inputTotal = Integer.parseInt(txtJumlahData.getText().trim());
            
            // --- PENGAMAN 2: BLOKIR LOGIKAL UNTUK NILAI 0 ATAU KOSONG ---
            if (inputTotal <= 0) {
                logStatus("Validasi Gagal: Kuota data harus lebih besar dari 0!"); 
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            
            jatahPerThread = inputTotal / terpilih;
            totalDataTarget = jatahPerThread * terpilih; 
        } catch (Exception e) {
            logStatus("Validasi Gagal: Masukkan angka yang valid!"); 
            return;
        }

        mapPb.clear(); 
        mapLbl.clear();
        counterPerThread.clear(); 
        panelProgressThread.removeAll();

        List<BangunGeometri> masterThreads = new ArrayList<>();
        int indexThread = 1;
        
        if (cbBelahKetupat.isSelected()) {
            masterThreads.add(new BelahKetupat(jatahPerThread, this));
            tambahProgressUI("Belah Ketupat", WARNA_THREAD[0], jatahPerThread, indexThread++);
        }
        if (cbPrisma.isSelected()) {
            masterThreads.add(new PrismaBelahKetupat(jatahPerThread, this));
            tambahProgressUI("Prisma Belah Ketupat", WARNA_THREAD[1], jatahPerThread, indexThread++);
        }
        if (cbLimas.isSelected()) {
            masterThreads.add(new LimasBelahKetupat(jatahPerThread, this));
            tambahProgressUI("Limas Belah Ketupat", WARNA_THREAD[2], jatahPerThread, indexThread++);
        }

        panelProgressThread.revalidate();
        panelProgressThread.repaint();

        jumlahThreadAktif = masterThreads.size();

        for (BangunGeometri master : masterThreads) {
            master.setPeerThreads(masterThreads);
        }

        btnMulai.setEnabled(false); 
        btnReset.setEnabled(false);
        cbBelahKetupat.setEnabled(false); 
        cbPrisma.setEnabled(false); 
        cbLimas.setEnabled(false);
        txtJumlahData.setEnabled(false);

        logStatus("Komputasi dimulai. Membangkitkan data...");
        lblWaktu.setText("Waktu total: berjalan...");
        waktuMulaiGlobal = System.currentTimeMillis();
        
        for (BangunGeometri master : masterThreads) {
            master.start();
        }
    }

    public synchronized void tambahHasil(BangunGeometri bangun) {
        semuaHasil.add(bangun);
        int selesai = semuaHasil.size();
        String nama = bangun.getNamaBangun();
        
        int hitunganThreadIni = counterPerThread.getOrDefault(nama, 0) + 1;
        counterPerThread.put(nama, hitunganThreadIni);
        
        int targetThreadIni = mapPb.get(nama).getMaximum();
        
        int laporanBatchIndividual = Math.max(1, targetThreadIni / 10);
        if (hitunganThreadIni % laporanBatchIndividual == 0 || hitunganThreadIni == targetThreadIni) {
            SwingUtilities.invokeLater(() -> {
                JProgressBar pb = mapPb.get(nama);
                JLabel lbl = mapLbl.get(nama);
                
                pb.setValue(hitunganThreadIni);
                int pct = (int) (((double) hitunganThreadIni / targetThreadIni) * 100);
                pb.setString(pct + "%");
                lbl.setText(nama + " — memproses batch data ke-" + hitunganThreadIni);
            });
        }
        
        int laporanBatchGlobal = Math.max(1, totalDataTarget / 10);
        if (selesai % laporanBatchGlobal == 0 || selesai == totalDataTarget) {
            SwingUtilities.invokeLater(() -> {
                int persentase = (int) (((double) selesai / totalDataTarget) * 100);
                pbTotal.setValue(persentase);
                pbTotal.setString(persentase + "%");
            });
        }
    }

    private void onSemuaSelesai() {
        long waktu = System.currentTimeMillis() - waktuMulaiGlobal;
        lblWaktu.setText("Waktu total: " + waktu + " ms"); 
        
        SwingUtilities.invokeLater(() -> {
            pbTotal.setString("Selesai diproses");
        });
        
        new SwingWorker<Void, Object[]>() {
            @Override
            protected Void doInBackground() {
                List<BangunGeometri> sorted = new ArrayList<>(semuaHasil);
                sorted.sort(Comparator.comparingInt(BangunGeometri::getIdBangun));
                
                List<Object[]> batch = new ArrayList<>();
                for (BangunGeometri b : sorted) {
                    double lp = 0;
                    double vol = 0;
                    double kel = 0;
                    String param = "";

                    if (b instanceof PrismaBelahKetupat) {
                        PrismaBelahKetupat pr = (PrismaBelahKetupat) b;
                        lp = pr.hitungLuasPermukaan(); 
                        vol = pr.hitungVolume(); 
                        param = String.format("d1=%.1f d2=%.1f t=%.1f", pr.d1, pr.d2, pr.tinggi);
                    } else if (b instanceof LimasBelahKetupat) {
                        LimasBelahKetupat li = (LimasBelahKetupat) b;
                        lp = li.hitungLuasPermukaan(); 
                        vol = li.hitungVolume();
                        param = String.format("d1=%.1f d2=%.1f t=%.1f", li.d1, li.d2, li.tinggi);
                    } else if (b instanceof BelahKetupat) {
                        BelahKetupat bk = (BelahKetupat) b;
                        lp = bk.hitungLuas(); 
                        kel = bk.hitungKeliling();
                        param = String.format("d1=%.1f d2=%.1f", bk.d1, bk.d2);
                    }

                    batch.add(new Object[]{ 
                        b.getIdBangun(), 
                        b.getNamaBangun(), 
                        param, 
                        DF.format(lp), 
                        DF.format(vol), 
                        DF.format(kel) 
                    });
                    
                    if (batch.size() >= 500) {
                        publish(batch.toArray(new Object[0][]));
                        batch.clear();
                    }
                }
                
                if (!batch.isEmpty()) {
                    publish(batch.toArray(new Object[0][]));
                }
                return null;
            }
            
            @Override
            protected void process(List<Object[]> chunks) {
                for (Object[] row : chunks) {
                    modelTabel.addRow(row);
                }
                lblJumlahTampil.setText("  " + semuaHasil.size() + " baris data terproses");
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
                
                logStatus("Komputasi selesai.");
            }
        }.execute();
    }

    private void hitungDanTampilkanStatistik() {
        lblStatTotal.setText("Total Data Akhir : " + semuaHasil.size()); 
        if (semuaHasil.isEmpty()) {
            return;
        }

        long cBK = semuaHasil.stream()
            .filter(h -> h instanceof BelahKetupat && !(h instanceof PrismaBelahKetupat) && !(h instanceof LimasBelahKetupat))
            .count();
            
        long cPr = semuaHasil.stream()
            .filter(h -> h instanceof PrismaBelahKetupat)
            .count();
            
        long cLi = semuaHasil.stream()
            .filter(h -> h instanceof LimasBelahKetupat)
            .count();

        double lpMin = semuaHasil.stream().mapToDouble(this::getLpOrLuas).min().orElse(0);
        double lpMax = semuaHasil.stream().mapToDouble(this::getLpOrLuas).max().orElse(0);
        double lpAvg = semuaHasil.stream().mapToDouble(this::getLpOrLuas).average().orElse(0);

        double vMin = semuaHasil.stream()
            .filter(b -> b instanceof PrismaBelahKetupat || b instanceof LimasBelahKetupat)
            .mapToDouble(this::getVol).min().orElse(0);
            
        double vMax = semuaHasil.stream()
            .filter(b -> b instanceof PrismaBelahKetupat || b instanceof LimasBelahKetupat)
            .mapToDouble(this::getVol).max().orElse(0);
            
        double vAvg = semuaHasil.stream()
            .filter(b -> b instanceof PrismaBelahKetupat || b instanceof LimasBelahKetupat)
            .mapToDouble(this::getVol).average().orElse(0);

        lblStatBK.setText("BK         : " + cBK);
        lblStatPrisma.setText("Prisma     : " + cPr);
        lblStatLimas.setText("Limas      : " + cLi);
        lblStatLPMin.setText("LP Min     : " + DF.format(lpMin));
        lblStatLPMax.setText("LP Max     : " + DF.format(lpMax));
        lblStatLPAvg.setText("LP Avg     : " + DF.format(lpAvg));
        lblStatVolMin.setText("Vol Min    : " + DF.format(vMin));
        lblStatVolMax.setText("Vol Max    : " + DF.format(vMax));
        lblStatVolAvg.setText("Vol Avg    : " + DF.format(vAvg));
    }

    private double getLpOrLuas(BangunGeometri b) {
        if (b instanceof PrismaBelahKetupat) {
            return ((PrismaBelahKetupat) b).hitungLuasPermukaan();
        }
        if (b instanceof LimasBelahKetupat) {
            return ((LimasBelahKetupat) b).hitungLuasPermukaan();
        }
        return ((BelahKetupat) b).hitungLuas();
    }

    private double getVol(BangunGeometri b) {
        if (b instanceof PrismaBelahKetupat) {
            return ((PrismaBelahKetupat) b).hitungVolume();
        }
        if (b instanceof LimasBelahKetupat) {
            return ((LimasBelahKetupat) b).hitungVolume();
        }
        return 0;
    }

    private void onReset() {
        semuaHasil.clear(); 
        modelTabel.setRowCount(0); 
        pbTotal.setValue(0); 
        
        lblStatus.setText("Reset!"); 
        lblWaktu.setText("Waktu total: —");
        lblJumlahTampil.setText("  0 baris data");
        
        panelProgressThread.removeAll(); 
        panelProgressThread.revalidate(); 
        panelProgressThread.repaint();
        
        pbTotal.setString("Belum dimulai");
        
        lblStatTotal.setText("Total Data : —"); 
        lblStatBK.setText("BK         : —"); 
        lblStatPrisma.setText("Prisma     : —"); 
        lblStatLimas.setText("Limas      : —");
        lblStatLPMin.setText("LP Min     : —"); 
        lblStatLPMax.setText("LP Max     : —"); 
        lblStatLPAvg.setText("LP Avg     : —");
        lblStatVolMin.setText("Vol Min    : —"); 
        lblStatVolMax.setText("Vol Max    : —"); 
        lblStatVolAvg.setText("Vol Avg    : —");
    }

    private JPanel buatBaris(String labelTeks, JComponent field) {
        JPanel b = new JPanel(new BorderLayout(8, 0)); 
        b.setOpaque(false);
        
        JLabel lbl = new JLabel(labelTeks); 
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); 
        lbl.setPreferredSize(new Dimension(140, 26));
        
        b.add(lbl, BorderLayout.WEST); 
        b.add(field, BorderLayout.CENTER);
        
        return b;
    }

    private JPanel buatKartu(String judul) {
        JPanel k = new JPanel() {
            @Override
            public Dimension getMaximumSize() {
                Dimension pref = getPreferredSize();
                return new Dimension(Integer.MAX_VALUE, pref.height);
            }
        }; 
        k.setLayout(new BoxLayout(k, BoxLayout.Y_AXIS)); 
        k.setBackground(C_KARTU);
        
        k.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 230, 240)), 
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(4, 10, 10, 10), 
                judul, 
                TitledBorder.LEFT, 
                TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 12), 
                C_PRIMER
            )
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
        btn.setPreferredSize(new Dimension(130, 34)); 
        
        return btn;
    }

    private JLabel buatLabelStat(String teks) {
        JLabel l = new JLabel(teks); 
        l.setFont(new Font("Consolas", Font.PLAIN, 11)); 
        l.setForeground(C_TEKS); 
        
        return l;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { 
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
            } catch (Exception ignored) {
            }
            
            new MesinHitung().setVisible(true);
        });
    }
}