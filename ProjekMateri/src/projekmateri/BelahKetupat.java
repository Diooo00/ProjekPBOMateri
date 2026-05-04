/**
 * Class BelahKetupat - Bangun datar dasar.
 *
 * Pilar OOP yang dicover:
 * - ENCAPSULATION: atribut private, akses lewat getter/setter
 * - INHERITANCE: extends BangunRuang (mewarisi struktur abstract)
 * - ABSTRACTION: mengimplementasikan method abstract dari BangunRuang
 *
 * Rumus Belah Ketupat:
 * - Sisi (s) dihitung dari diagonal: s = sqrt((d1/2)^2 + (d2/2)^2)
 * - Keliling = 4 * s
 * - Luas = (d1 * d2) / 2
 *
 *      d2
 *   /------\
 *  /        \  d1
 *  \        /
 *   \------/
 */
public class BelahKetupat extends BangunRuang {

    // ==================== ENCAPSULATION ====================
    // Atribut dibuat private agar tidak bisa diakses langsung dari luar
    private double diagonal1; // d1 = diagonal horizontal
    private double diagonal2; // d2 = diagonal vertikal

    // ==================== CONSTRUCTOR ====================
    public BelahKetupat(double diagonal1, double diagonal2) {
        this.diagonal1 = diagonal1;
        this.diagonal2 = diagonal2;
    }

    // ==================== GETTER & SETTER (Encapsulation) ====================
    public double getDiagonal1() { return diagonal1; }
    public double getDiagonal2() { return diagonal2; }

    public void setDiagonal1(double diagonal1) {
        if (diagonal1 <= 0) throw new IllegalArgumentException("Diagonal harus > 0");
        this.diagonal1 = diagonal1;
    }

    public void setDiagonal2(double diagonal2) {
        if (diagonal2 <= 0) throw new IllegalArgumentException("Diagonal harus > 0");
        this.diagonal2 = diagonal2;
    }

    // ==================== METHOD KALKULASI ====================

    /**
     * Hitung sisi belah ketupat dari diagonal menggunakan Pythagoras.
     * Sisi = sqrt((d1/2)^2 + (d2/2)^2)
     */
    public double hitungSisi() {
        return Math.sqrt(Math.pow(diagonal1 / 2, 2) + Math.pow(diagonal2 / 2, 2));
    }

    /**
     * Keliling = 4 * sisi
     */
    public double hitungKeliling() {
        return 4 * hitungSisi();
    }

    /**
     * Luas = (d1 * d2) / 2
     */
    public double hitungLuas() {
        return (diagonal1 * diagonal2) / 2;
    }

    // ==================== IMPLEMENTASI ABSTRACT METHOD ====================

    @Override
    public double hitungLuasPermukaan() {
        // Untuk bangun datar, luas permukaan = luas bidangnya
        return hitungLuas();
    }

    @Override
    public double hitungVolume() {
        // Bangun datar tidak punya volume
        return 0;
    }

    @Override
    public String getNamaBangun() {
        return "Belah Ketupat";
    }

    @Override
    public String tampilkanInfo() {
        return String.format(
            "=== %s ===\n" +
            "Diagonal 1     : %.2f cm\n" +
            "Diagonal 2     : %.2f cm\n" +
            "Sisi           : %.2f cm\n" +
            "Keliling       : %.2f cm\n" +
            "Luas           : %.2f cm²",
            getNamaBangun(), diagonal1, diagonal2,
            hitungSisi(), hitungKeliling(), hitungLuas()
        );
    }
}
