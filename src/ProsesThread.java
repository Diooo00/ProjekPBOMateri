import java.util.ArrayList;
import java.util.List;

public class ProsesThread extends Thread {

    public enum TipeBangun { BELAH_KETUPAT, PRISMA, LIMAS }

    public interface ProgressListener {
        void onProgress(int threadId, int selesai, int total, String status);
        void onSelesai(int threadId, List<HasilHitung> hasil, long waktuMs);
    }

    // ==================== ENCAPSULATION ====================
    private final int                 threadId;
    private final List<BangunGeometri> daftarBangun; // Menggunakan Polimorfisme Tipe Induk Tertinggi
    private final int                 globalOffset; 
    private final ProgressListener    listener;
    private final List<HasilHitung>   hasilList = new ArrayList<>();

    public ProsesThread(int threadId, List<BangunGeometri> daftarBangun, int globalOffset, ProgressListener listener) {
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
            BangunGeometri bangun = daftarBangun.get(i); // Pemanggilan runtime polimorfisme murni
            
            double lp = bangun.hitungLuasPermukaan();
            double v  = bangun.hitungVolume();

            // Kebijakan simulasi beban komputasi bawaan proyek
            for (int k = 0; k < 100; k++) Math.sqrt(lp * k + v);

            if (i % 1000 == 0) {
                try { Thread.sleep(1); }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            hasilList.add(new HasilHitung(
                globalOffset + i + 1,
                bangun.getNamaBangun(),
                bangun.getRingkasan(),
                lp, v,
                threadId
            ));

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