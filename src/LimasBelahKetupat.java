import java.util.Random;

public class LimasBelahKetupat extends BelahKetupat {
    public double tinggi;
    public double apotema1;
    public double apotema2;
    public double luasSelimut; 
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

    // --- OVERLOADING APOTEMA 1 ---
    public double hitungApotema1(double t, double diagonal2) throws IllegalArgumentException {
        if (t <= 0 || diagonal2 <= 0) throw new IllegalArgumentException("Error: Tinggi atau Diagonal tidak valid!");
        this.apotema1 = Math.sqrt(Math.pow(t, 2) + Math.pow(diagonal2 / 2, 2));
        return this.apotema1; // Murni return atribut
    }

    public double hitungApotema1() throws IllegalArgumentException {
        hitungApotema1(this.tinggi, super.d2);
        return this.apotema1; // Murni return atribut
    }

    // --- OVERLOADING APOTEMA 2 ---
    public double hitungApotema2(double t, double diagonal1) throws IllegalArgumentException {
        if (t <= 0 || diagonal1 <= 0) throw new IllegalArgumentException("Error: Tinggi atau Diagonal tidak valid!");
        this.apotema2 = Math.sqrt(Math.pow(t, 2) + Math.pow(diagonal1 / 2, 2));
        return this.apotema2; // Murni return atribut
    }

    public double hitungApotema2() throws IllegalArgumentException {
        hitungApotema2(this.tinggi, super.d1);
        return this.apotema2; // Murni return atribut
    }
    
    // --- OVERLOADING LUAS SELIMUT ---
    public double hitungLuasSelimut(double diag1, double diag2, double a1, double a2) throws IllegalArgumentException {
        if (diag1 <= 0 || diag2 <= 0 || a1 <= 0 || a2 <= 0) {
            throw new IllegalArgumentException("Error: Parameter komponen selimut tidak valid!");
        }
        this.luasSelimut = (diag1 * a2) + (diag2 * a1);
        return this.luasSelimut; // Murni return atribut
    }

    public double hitungLuasSelimut() throws IllegalArgumentException {
        hitungLuasSelimut(super.d1, super.d2, this.apotema1, this.apotema2);
        return this.luasSelimut; // Murni return atribut
    }

    // --- OVERLOADING LUAS PERMUKAAN ---
    public double hitungLuasPermukaan(double luasAlas, double selimut) throws IllegalArgumentException {
        if (luasAlas <= 0 || selimut <= 0) throw new IllegalArgumentException("Error: Parameter tidak valid!");
        this.luasPermukaan = luasAlas + selimut;
        return this.luasPermukaan; // Murni return atribut
    }

    public double hitungLuasPermukaan() throws IllegalArgumentException {
        hitungLuasPermukaan(super.luas, this.luasSelimut);
        return this.luasPermukaan; // Murni return atribut
    }

    // --- OVERLOADING VOLUME ---
    public double hitungVolume(double luasAlas, double t) throws IllegalArgumentException {
        if (luasAlas <= 0 || t <= 0) throw new IllegalArgumentException("Error: Parameter tidak valid!");
        this.volume = (1.0 / 3.0) * luasAlas * t;
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
            hitungApotema1();
            hitungApotema2();
            hitungLuasSelimut(); 
            hitungLuasPermukaan();
            hitungVolume();
        } catch (IllegalArgumentException e) {
            System.err.println("Gagal menghitung Limas: " + e.getMessage());
        }
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