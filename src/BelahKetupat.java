import java.util.Random;

public class BelahKetupat extends BangunGeometri {
    protected double d1;
    protected double d2;
    protected double sisi;
    protected double luas;
    protected double keliling;

    public BelahKetupat(int id, double d1, double d2, MesinHitung gui) {
        super(id, "Belah Ketupat", gui);
        this.d1 = d1;
        this.d2 = d2;
    }

    public BelahKetupat(int targetGenerate, MesinHitung gui) {
        super("Belah Ketupat", targetGenerate, gui);
    }

    public double hitungSisi() {
        this.sisi = Math.sqrt(Math.pow(this.d1 / 2, 2) + Math.pow(this.d2 / 2, 2));
        return this.sisi;
    }

    public double hitungLuas() {
        this.luas = (this.d1 * this.d2) / 2.0;
        return this.luas;
    }

    public double hitungKeliling() {
        this.keliling = 4 * hitungSisi();
        return this.keliling;
    }

    @Override
    public void hitungSemua() {
        hitungSisi();
        hitungLuas();
        hitungKeliling();
    }

    @Override
    public void run() {
        if (targetGenerate > 0) {
            Random r = new Random();

            for (int i = 0; i < targetGenerate; i++) {
                BelahKetupat bk = new BelahKetupat(i + 1, 5 + r.nextDouble() * 95, 5 + r.nextDouble() * 95, getGui());
                bk.hitungSemua();
                getGui().tambahHasil(bk);

                try {
                    // Jeda acak tetap ada agar progress tidak instan
                    Thread.sleep(r.nextInt(3) + 1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            // Lapor jika Thread ini sudah menyelesaikan seluruh kuotanya
            getGui().laporThreadSelesai();
        }
    }
}