import java.util.Random;

public class LimasBelahKetupat extends BelahKetupat {
    public double tinggi;
    public double apotema1;
    public double apotema2;
    public double luasPermukaan;
    public double volume;

    public LimasBelahKetupat(int id, double d1, double d2, double tinggi, MesinHitung gui) {
        super(id, d1, d2, gui);
        this.tinggi = tinggi;
    }

    public LimasBelahKetupat(int targetGenerate, MesinHitung gui) {
        super(targetGenerate, gui);
    }

    @Override
    public String getNamaBangun() {
        return "Limas Belah Ketupat";
    }

    public double hitungApotema1() {
        this.apotema1 = Math.sqrt(Math.pow(this.tinggi, 2) + Math.pow(this.d2 / 2, 2));
        return this.apotema1;
    }

    public double hitungApotema2() {
        this.apotema2 = Math.sqrt(Math.pow(this.tinggi, 2) + Math.pow(this.d1 / 2, 2));
        return this.apotema2;
    }
    
    public double hitungLuasSelimut() {
        return (this.d1 * hitungApotema2()) + (this.d2 * hitungApotema1());
    }

    public double hitungLuasPermukaan() {
        this.luasPermukaan = this.luas + hitungLuasSelimut();
        return this.luasPermukaan;
    }

    public double hitungVolume() {
        this.volume = (1.0 / 3.0) * this.luas * this.tinggi;
        return this.volume;
    }

    @Override
    public void hitungSemua() {
        super.hitungSemua();
        hitungApotema1();
        hitungApotema2();
        hitungLuasPermukaan();
        hitungVolume();
    }

    @Override
    public void run() {
        if (targetGenerate > 0) {
            Random r = new Random();

            for (int i = 0; i < targetGenerate; i++) {
                LimasBelahKetupat li = new LimasBelahKetupat(i + 1, 5 + r.nextDouble() * 95, 5 + r.nextDouble() * 95, 5 + r.nextDouble() * 95, getGui());
                li.hitungSemua();
                getGui().tambahHasil(li);

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