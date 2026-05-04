import javax.swing.SwingWorker;
import java.util.List;

/**
 * KalkulatorWorker - menjalankan perhitungan di background thread.
 *
 * Pilar OOP yang dicover: MULTI-THREADING
 *
 * SwingWorker adalah implementasi Thread di Java Swing.
 * Tujuannya: agar proses perhitungan tidak memblokir UI thread (EDT).
 * Ini penting untuk menjaga responsivitas aplikasi.
 *
 * Cara kerja:
 * 1. doInBackground() → dijalankan di background thread (bukan EDT)
 * 2. process()        → dipanggil di EDT untuk update progress
 * 3. done()           → dipanggil di EDT setelah background selesai
 */
public class KalkulatorWorker extends SwingWorker<String, String> {

    // Tipe bangun yang akan dihitung
    public enum TipeBangun {
        BELAH_KETUPAT, PRISMA, LIMAS
    }

    private final TipeBangun tipe;
    private final double[] params;   // parameter input dari user
    private final KalkulatorCallback callback;

    /**
     * Interface callback untuk mengirim hasil balik ke GUI.
     * Ini pattern yang umum digunakan bersama multi-threading.
     */
    public interface KalkulatorCallback {
        void onProgress(String message);
        void onSelesai(String hasil);
        void onError(String pesan);
    }

    public KalkulatorWorker(TipeBangun tipe, double[] params, KalkulatorCallback callback) {
        this.tipe = tipe;
        this.params = params;
        this.callback = callback;
    }

    /**
     * Dijalankan di BACKGROUND THREAD - bukan UI thread.
     * Ini yang dimaksud multi-threading: perhitungan berat tidak
     * mengganggu responsivitas tampilan.
     */
    @Override
    protected String doInBackground() throws Exception {
        // Simulasi proses bertahap (publish progress ke UI)
        publish("Memvalidasi input...");
        Thread.sleep(300); // simulasi proses

        publish("Menghitung...");
        Thread.sleep(300);

        String hasil;
        switch (tipe) {
            case BELAH_KETUPAT:
                BelahKetupat bk = new BelahKetupat(params[0], params[1]);
                hasil = bk.tampilkanInfo();
                break;
            case PRISMA:
                PrismaBelahKetupat prisma = new PrismaBelahKetupat(params[0], params[1], params[2]);
                hasil = prisma.tampilkanInfo();
                break;
            case LIMAS:
                LimasBelahKetupat limas = new LimasBelahKetupat(params[0], params[1], params[2]);
                hasil = limas.tampilkanInfo();
                break;
            default:
                throw new IllegalArgumentException("Tipe bangun tidak dikenal");
        }

        publish("Selesai!");
        Thread.sleep(200);
        return hasil;
    }

    /**
     * Dipanggil di EDT (UI thread) saat ada publish() dari background.
     * Aman untuk update komponen Swing dari sini.
     */
    @Override
    protected void process(List<String> chunks) {
        // Ambil pesan progress terbaru
        String lastMessage = chunks.get(chunks.size() - 1);
        callback.onProgress(lastMessage);
    }

    /**
     * Dipanggil di EDT setelah doInBackground() selesai.
     */
    @Override
    protected void done() {
        try {
            String hasil = get(); // ambil return value dari doInBackground()
            callback.onSelesai(hasil);
        } catch (Exception e) {
            callback.onError("Terjadi kesalahan: " + e.getMessage());
        }
    }
}
