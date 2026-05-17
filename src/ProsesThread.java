import java.util.ArrayList;
import java.util.List;

/**
 * MULTI-THREADING - Thread jembatan eksekusi komputasi secara paralel.
 * * PILAR POLYMORPHISM (Behavioral Overriding):
 * Thread ini sama sekali tidak tahu bentuk asli objeknya (apakah Prisma atau Limas),
 * ia murni mengeksekusi method kontraktual dari kelas induk (BangunRuang).
 */
public class ProsesThread extends Thread {

    /** Enum dipertahankan di sini agar menjaga kompatibilitas tipe pilihan */
    public enum TipeBangun { BELAH_KETUPAT, PRISMA, LIMAS }

    /** Callback interface untuk melaporkan progress ke GUI (EDT) */
    public interface ProgressListener {
        void onProgress(int threadId, int selesai, int total, String status);
        void onSelesai(int threadId, List<HasilHitung> hasil, long waktuMs);
    }

    // ==================== ENCAPSULATION ====================
    private final int               threadId;
    private final List<BangunRuang> daftarBangun; // Menerima jatah list polimorfis dari Main Flow
    private final int               globalOffset; // Penanda indeks baris kontinu di tabel
    private final ProgressListener  listener;
    private final List<HasilHitung> hasilList = new ArrayList<>();

    // Constructor menerima potongan data (chunk) yang sudah jadi dari fungsi Main
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
            // EKSEKUSI POLYMORPHISM MURNI DI DALAM RUNTIME THREAD
            BangunRuang bangun = daftarBangun.get(i);
            
            // Mengubah wujud perilaku dinamis sesuai tipe asli objek tanpa manual checking
            double lp = bangun.hitungLuasPermukaan();
            double v  = bangun.hitungVolume();

            // Kebijakan simulasi beban komputasi bawaan proyek
            for (int k = 0; k < 100; k++) Math.sqrt(lp * k + v);

            //Throttling CPU, membiarkan thread beristirahat setiap memproses50 data
            if (i % 1000 == 0) {
                try {
                    Thread.sleep(1);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt() ;
                    break ;
                }
            }

            // Bungkus hasil kalkulasi ke data-class
            hasilList.add(new HasilHitung(
                globalOffset + i + 1,
                bangun.getNamaBangun(),
                bangun.getRingkasan(),
                lp, v,
                threadId
            ));

            // Kirim laporan kemajuan setiap kelipatan 10%
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
