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

    /** Enum tipe bangun yang bisa dipilih user */
    public enum TipeBangun { BELAH_KETUPAT, PRISMA, LIMAS }

    /** Callback interface untuk melaporkan progress ke GUI */
    public interface ProgressListener {
        void onProgress(int threadId, int selesai, int total, String status);
        void onSelesai(int threadId, List<HasilHitung> hasil, long waktuMs);
    }

    // ==================== ENCAPSULATION ====================
    private final int               threadId;
    private final int               startIdx;
    private final int               endIdx;
    private final ProgressListener  listener;
    private final List<HasilHitung> hasilList = new ArrayList<>();
    private final List<TipeBangun>  tipeDiizinkan;

    private final Random random;

    public ProsesThread(int threadId, int startIdx, int endIdx,
                        List<TipeBangun> tipeDiizinkan, ProgressListener listener) {
        super("ProsesThread-" + threadId);
        this.threadId      = threadId;
        this.startIdx      = startIdx;
        this.endIdx        = endIdx;
        this.tipeDiizinkan = tipeDiizinkan;
        this.listener      = listener;
        this.random        = new Random(threadId * 12345L);
    }

    @Override
    public void run() {
        long mulai   = System.currentTimeMillis();
        int  total   = endIdx - startIdx;
        int  laporan = Math.max(1, total / 100);

        listener.onProgress(threadId, 0, total, "Memulai...");

        for (int i = 0; i < total; i++) {
            int globalId = startIdx + i;

            // Generate hanya dari tipe yang diizinkan (pilihan user)
            BangunRuang bangun = generateDariPilihan(globalId);

            double lp = bangun.hitungLuasPermukaan();
            double v  = bangun.hitungVolume();

            // Beban CPU agar threading terasa
            for (int k = 0; k < 500; k++) Math.sqrt(lp * k + v);

            hasilList.add(new HasilHitung(
                globalId + 1,
                bangun.getNamaBangun(),
                bangun.getRingkasan(),
                lp, v,
                threadId
            ));

            if ((i + 1) % laporan == 0 || i == total - 1) {
                listener.onProgress(threadId, i + 1, total,
                    String.format("Thread %d: %d / %d", threadId, i + 1, total));
            }
        }

        long selesai = System.currentTimeMillis();
        listener.onSelesai(threadId, hasilList, selesai - mulai);
    }

    /**
     * POLYMORPHISM: return type BangunRuang, tapi tipe aktual
     * dipilih secara random HANYA dari tipeDiizinkan.
     */
    private BangunRuang generateDariPilihan(int seed) {
        // Pilih tipe random dari daftar yang diizinkan
        TipeBangun tipe = tipeDiizinkan.get(random.nextInt(tipeDiizinkan.size()));

        double d1 = 5 + random.nextDouble() * 95;
        double d2 = 5 + random.nextDouble() * 95;
        double t  = 5 + random.nextDouble() * 95;

        switch (tipe) {
            case BELAH_KETUPAT: return new BelahKetupat(d1, d2);
            case PRISMA:        return new PrismaBelahKetupat(d1, d2, t);
            default:            return new LimasBelahKetupat(d1, d2, t);
        }
    }

    public List<HasilHitung> getHasilList() { return hasilList; }
}
