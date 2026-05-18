/**
 * ENCAPSULATION + INHERITANCE
 * Bangun dasar belah ketupat.
 */
public class BelahKetupat extends BangunDatar {
    private double diagonal1;
    private double diagonal2;

    public BelahKetupat(double diagonal1, double diagonal2) {
        this.diagonal1 = diagonal1;
        this.diagonal2 = diagonal2;
    }

    public double getDiagonal1() { 
        return diagonal1; 
    }
    public double getDiagonal2() { 
        return diagonal2; 
    }

    public double hitungSisi() {
        return Math.sqrt(Math.pow(diagonal1 / 2, 2) + Math.pow(diagonal2 / 2, 2));
    }
    
    @Override
    public double hitungLuas() {
        return (diagonal1 * diagonal2) / 2;
    }

    @Override
    public double hitungKeliling() {
        return 4 * hitungSisi(); // <-- Implementasi perhitungan keliling
    }

    @Override public String getNamaBangun() {
        return "Belah Ketupat"; 
    }
    @Override public String getRingkasan()  { 
        return String.format("d1=%.1f d2=%.1f", diagonal1, diagonal2); 
    }
}