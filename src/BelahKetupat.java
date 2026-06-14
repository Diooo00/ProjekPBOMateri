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

    // --- OVERLOADING SISI ---
    public double hitungSisi(double diagonal1, double diagonal2) throws IllegalArgumentException {
        if (diagonal1 <= 0 || diagonal2 <= 0) {
            throw new IllegalArgumentException("Error: Diagonal tidak boleh kurang dari atau sama dengan 0!");
        }
        this.sisi = Math.sqrt(Math.pow(diagonal1 / 2, 2) + Math.pow(diagonal2 / 2, 2));
        return this.sisi; // Murni return atribut
    }

    public double hitungSisi() throws IllegalArgumentException {
        hitungSisi(this.d1, this.d2); 
        return this.sisi; // Murni return atribut
    }

    // --- OVERLOADING LUAS ---
    public double hitungLuas(double diagonal1, double diagonal2) throws IllegalArgumentException {
        if (diagonal1 <= 0 || diagonal2 <= 0) {
            throw new IllegalArgumentException("Error: Diagonal tidak boleh kurang dari atau sama dengan 0!");
        }
        this.luas = (diagonal1 * diagonal2) / 2.0;
        return this.luas; // Murni return atribut
    }

    public double hitungLuas() throws IllegalArgumentException {
        hitungLuas(this.d1, this.d2);
        return this.luas; // Murni return atribut
    }

    // --- OVERLOADING KELILING ---
    public double hitungKeliling(double nilaiSisi) throws IllegalArgumentException {
        if (nilaiSisi <= 0) {
            throw new IllegalArgumentException("Error: Sisi tidak valid!");
        }
        this.keliling = 4 * nilaiSisi;
        return this.keliling; // Murni return atribut
    }

    public double hitungKeliling() throws IllegalArgumentException {
        hitungKeliling(this.sisi);
        return this.keliling; // Murni return atribut
    }

    @Override
    public void hitungSemua() {
        try {
            hitungSisi();
            hitungLuas();
            hitungKeliling();
        } catch (IllegalArgumentException e) {
            System.err.println("Gagal menghitung Belah Ketupat: " + e.getMessage());
        }
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
                    Thread.sleep(r.nextInt(3) + 1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            getGui().laporThreadSelesai();
        }
    }
}
