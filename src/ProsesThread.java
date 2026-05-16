import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MULTI-THREADING - Thread yang memproses sebagian (chunk) dari total data.
 *
 * Perubahan: sekarang menerima tipeDiizinkan → hanya generate bangun
 * yang dipilih user (bisa BK saja, Prisma+Limas, atau semua, dll).
 *
 * POLYMORPHISM: generateRandom() tetap return BangunRuang (tipe abstrak),
 * tapi objek aktualnya dipilih dari tipeDiizinkan saja.
 */
public class ProsesThread extends Thread {

    /** Enum tetap dipertahankan di sini agar tidak merusak dependensi lain */
    public enum TipeBangun { BELAH_KETUPAT, PRISMA, LIMAS }

    /** Callback interface untuk melaporkan progress ke GUI */
    public interface ProgressListener {
        void onProgress(int threadId, int selesai, int total, String status);
        void onSelesai(int threadId, List<HasilHitung> hasil, long waktuMs);
    }

    // ==================== ENCAPSULATION ====================
    private final int               threadId;
    private final List<BangunRuang> daftarBangun; // Polimorfisme: List menggunakan kelas induk
    private final int               globalOffset; // Untuk penomoran ID baris di tabel
    private final ProgressListener  listener;
    private final List<HasilHitung> hasilList = new ArrayList<>();

    // Constructor baru yang menerima sublist BangunRuang yang sudah di-generate di Main
    public ProsesThread(int threadId, List<BangunRuang> daftarBangun, int globalOffset, ProgressListener listener) {
        this.threadId     = threadId;
        this.daftarBangun = daftarBangun;
        this.globalOffset = globalOffset;
        this.listener     = listener;
    }

    @Override
    public void run() {
        long mulai = System.currentTimeMillis();
        int total = daftarBangun.size();

        for (int i = 0; i < total; i++) {
            // MENGGUNAKAN POLYMORPHISM MURNI DI DALAM THREAD
            BangunRuang bangun = daftarBangun.get(i);
            
            // Thread langsung panggil tanpa peduli tipe asli objeknya
            double lp = bangun.hitungLuasPermukaan();
            double v  = bangun.hitungVolume();

            // Simulasi beban komputasi (bawaan dari kode aslimu)
            for (int k = 0; k < 500; k++) Math.sqrt(lp * k + v);

            // Menyimpan hasil hitung ke list
            hasilList.add(new HasilHitung(
                globalOffset + i + 1, // ID baris yang kontinu
                bangun.getNamaBangun(),
                bangun.getRingkasan(),
                lp, v,
                threadId
            ));

            // Laporan progress ke GUI
            int laporan = Math.max(1, total / 10);
            if ((i + 1) % laporan == 0 || i == total - 1) {
                listener.onProgress(threadId, i + 1, total,
                    String.format("Thread %d: %d / %d", threadId, i + 1, total));
            }
        }

        long selesai = System.currentTimeMillis();
        listener.onSelesai(threadId, hasilList, selesai - mulai);
    }
}