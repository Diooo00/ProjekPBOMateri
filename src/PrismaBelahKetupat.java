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

    // --- OVERLOADING LUAS PERMUKAAN ---
    public double hitungLuasPermukaan(double luasAlas, double kelilingAlas, double t) throws IllegalArgumentException {
        if (luasAlas <= 0 || kelilingAlas <= 0 || t <= 0) {
            throw new IllegalArgumentException("Error: Luas, Keliling, atau Tinggi Prisma tidak boleh 0!");
        }
        this.luasPermukaan = (2 * luasAlas) + (kelilingAlas * t);
        return this.luasPermukaan; // Murni return atribut
    }

    public double hitungLuasPermukaan() throws IllegalArgumentException {
        hitungLuasPermukaan(super.luas, super.keliling, this.tinggi);
        return this.luasPermukaan; // Murni return atribut
    }

    // --- OVERLOADING VOLUME ---
    public double hitungVolume(double luasAlas, double t) throws IllegalArgumentException {
        if (luasAlas <= 0 || t <= 0) {
            throw new IllegalArgumentException("Error: Luas alas atau Tinggi tidak valid!");
        }
        this.volume = luasAlas * t;
        return this.volume; // Murni return atribut
    }

    public double hitungVolume() throws IllegalArgumentException {
        hitungVolume(super.luas, this.tinggi);
        return this.volume; // Murni return atribut
    }

    @Override
    public void hitungSemua() {
        super.hitungSemua(); 
        try {
            hitungLuasPermukaan();
            hitungVolume();
        } catch (IllegalArgumentException e) {
            System.err.println("Gagal menghitung Prisma: " + e.getMessage());
        }
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