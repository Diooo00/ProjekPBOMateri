/**
 * INHERITANCE + POLYMORPHISM
 * Limas dengan alas belah ketupat.
 */
public class LimasBelahKetupat extends BangunRuang {
    private final BelahKetupat alas; // Pilar Komposisi Objek
    private final double       tinggi;

    public LimasBelahKetupat(double diagonal1, double diagonal2, double tinggi) {
        this.alas   = new BelahKetupat(diagonal1, diagonal2);
        this.tinggi = tinggi;
    }

    public double getTinggi(){ 
        return tinggi; 
    }
    public BelahKetupat getAlas(){ 
        return alas; 
    }

    public double hitungApotema1(){
        return Math.sqrt(Math.pow(tinggi, 2) + Math.pow(alas.getDiagonal2() / 2, 2));
    }

    public double hitungApotema2() {
        return Math.sqrt(Math.pow(tinggi, 2) + Math.pow(alas.getDiagonal1() / 2, 2));
    }

    public double hitungLuasSelimut() {
        return (alas.getDiagonal1() * hitungApotema2()) + (alas.getDiagonal2() * hitungApotema1());
    }

    @Override
    public double hitungLuasPermukaan() {
        return alas.hitungLuas() + hitungLuasSelimut();
    }

    @Override
    public double hitungVolume() {
        return (1.0 / 3.0) * alas.hitungLuas() * tinggi;
    }

    @Override public String getNamaBangun(){ 
        return "Limas Belah Ketupat"; 
    }
    @Override public String getRingkasan(){ 
        return String.format("d1=%.1f d2=%.1f t=%.1f", alas.getDiagonal1(), alas.getDiagonal2(), tinggi); 
    }
}
