/**
 * INHERITANCE + POLYMORPHISM
 * Prisma dengan alas belah ketupat.
 */
public class PrismaBelahKetupat extends BangunRuang {
    private final BelahKetupat alas; // Pilar Komposisi Objek
    private final double       tinggi;

    public PrismaBelahKetupat(double diagonal1, double diagonal2, double tinggi) {
        this.alas   = new BelahKetupat(diagonal1, diagonal2); // Instansiasi anatomi bagian komponen
        this.tinggi = tinggi;
    }

    public double getTinggi() { return tinggi; }
    public BelahKetupat getAlas() { return alas; }

    public double hitungLuasSelimut() {
        return 4 * alas.hitungSisi() * tinggi;
    }

    @Override
    public double hitungLuasPermukaan() {
        return (2 * alas.hitungLuas()) + hitungLuasSelimut();
    }

    @Override
    public double hitungVolume() {
        return alas.hitungLuas() * tinggi;
    }

    @Override public String getNamaBangun() { return "Prisma Belah Ketupat"; }
    @Override public String getRingkasan()  { return String.format("d1=%.1f d2=%.1f t=%.1f", alas.getDiagonal1(), alas.getDiagonal2(), tinggi); }
}
