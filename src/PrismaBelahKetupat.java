import java.util.Random;

public class PrismaBelahKetupat extends BelahKetupat {
    public double tinggi;
    public double luasPermukaan;
    public double volume;

    public PrismaBelahKetupat(int id, double d1, double d2, double tinggi, MesinHitung gui) {
        super(id, d1, d2, gui);
        this.tinggi = tinggi;
    }

    public PrismaBelahKetupat(int targetGenerate, MesinHitung gui) {
        super(targetGenerate, gui);
    }

    @Override
    public String getNamaBangun() {
        return "Prisma Belah Ketupat";
    }

    public double hitungLuasPermukaan() {
        this.luasPermukaan = (2 * this.luas) + (this.keliling * this.tinggi);
        return this.luasPermukaan;
    }

    public double hitungVolume() {
        this.volume = this.luas * this.tinggi;
        return this.volume;
    }

    @Override
    public void hitungSemua() {
        super.hitungSemua();
        hitungLuasPermukaan();
        hitungVolume();
    }

    @Override
    public void run() {
        if (targetGenerate > 0) {
            Random r = new Random();

            for (int i = 0; i < targetGenerate; i++) {
                PrismaBelahKetupat pr = new PrismaBelahKetupat(i + 1, 5 + r.nextDouble() * 95, 5 + r.nextDouble() * 95, 5 + r.nextDouble() * 95, getGui());
                pr.hitungSemua();
                getGui().tambahHasil(pr);

                try {
                    Thread.sleep(r.nextInt(3) + 1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            getGui().laporThreadSelesai();
        }
    }
}