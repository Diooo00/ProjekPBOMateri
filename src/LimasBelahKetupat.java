/**
 * INHERITANCE + POLYMORPHISM
 * Limas dengan alas belah ketupat.
 */
public class LimasBelahKetupat extends BelahKetupat {
    private double tinggi;

    public LimasBelahKetupat(double diagonal1, double diagonal2, double tinggi) {
        super(diagonal1, diagonal2);
        this.tinggi = tinggi;
    }

    public double getTinggi() { return tinggi; }

    public double hitungApotema1() {
        return Math.sqrt(Math.pow(tinggi, 2) + Math.pow(getDiagonal2() / 2, 2));
    }

    public double hitungApotema2() {
        return Math.sqrt(Math.pow(tinggi, 2) + Math.pow(getDiagonal1() / 2, 2));
    }

    public double hitungLuasSelimut() {
        return (getDiagonal1() * hitungApotema2()) + (getDiagonal2() * hitungApotema1());
    }

    @Override
    public double hitungLuasPermukaan() {
        return hitungLuas() + hitungLuasSelimut();
    }

    @Override
    public double hitungVolume() {
        return (1.0 / 3.0) * hitungLuas() * tinggi;
    }

    @Override public String getNamaBangun() { return "Limas Belah Ketupat"; }

    @Override
    public String getRingkasan() {
        return String.format("d1=%.1f d2=%.1f t=%.1f", getDiagonal1(), getDiagonal2(), tinggi);
    }
}
