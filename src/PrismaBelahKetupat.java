/**
 * INHERITANCE + POLYMORPHISM
 * Prisma dengan alas belah ketupat.
 */
public class PrismaBelahKetupat extends BelahKetupat {
    private double tinggi;

    public PrismaBelahKetupat(double diagonal1, double diagonal2, double tinggi) {
        super(diagonal1, diagonal2);
        this.tinggi = tinggi;
    }

    public double getTinggi() { return tinggi; }

    public double hitungLuasSelimut() {
        return 4 * hitungSisi() * tinggi;
    }

    @Override
    public double hitungLuasPermukaan() {
        return (2 * hitungLuas()) + hitungLuasSelimut();
    }

    @Override
    public double hitungVolume() {
        return hitungLuas() * tinggi;
    }

    @Override public String getNamaBangun() { return "Prisma Belah Ketupat"; }

    @Override
    public String getRingkasan() {
        return String.format("d1=%.1f d2=%.1f t=%.1f", getDiagonal1(), getDiagonal2(), tinggi);
    }
}
