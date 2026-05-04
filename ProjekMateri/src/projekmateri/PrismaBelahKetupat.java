/**
 * Class PrismaBelahKetupat - Prisma dengan alas belah ketupat.
 *
 * Pilar OOP yang dicover:
 * - INHERITANCE: extends BelahKetupat, mewarisi semua properti alas
 * - ENCAPSULATION: atribut tinggi dibuat private
 * - POLYMORPHISM: override method dari parent class
 *
 * Rumus Prisma Belah Ketupat:
 * - Luas Alas     = (d1 * d2) / 2
 * - Luas Selimut  = Keliling Alas * Tinggi = 4 * sisi * tinggi
 * - Luas Permukaan = 2 * Luas Alas + Luas Selimut
 * - Volume        = Luas Alas * Tinggi
 *
 *        /\
 *       /  \
 *      / BK \   <- Alas Belah Ketupat
 *     /______\
 *     |      |
 *     |      |  <- tinggi prisma
 *     |______|
 */
public class PrismaBelahKetupat extends BelahKetupat {

    // ==================== ENCAPSULATION ====================
    private double tinggi;

    // ==================== CONSTRUCTOR ====================
    public PrismaBelahKetupat(double diagonal1, double diagonal2, double tinggi) {
        // Panggil constructor parent (BelahKetupat) untuk inisialisasi diagonal
        super(diagonal1, diagonal2);
        this.tinggi = tinggi;
    }

    // ==================== GETTER & SETTER ====================
    public double getTinggi() { return tinggi; }

    public void setTinggi(double tinggi) {
        if (tinggi <= 0) throw new IllegalArgumentException("Tinggi harus > 0");
        this.tinggi = tinggi;
    }

    // ==================== METHOD KALKULASI ====================

    /**
     * Luas selimut = keliling alas * tinggi
     * Keliling alas diperoleh dari method parent hitungKeliling()
     */
    public double hitungLuasSelimut() {
        return hitungKeliling() * tinggi;
    }

    // ==================== POLYMORPHISM (Override) ====================

    /**
     * Luas Permukaan = 2 * Luas Alas + Luas Selimut
     * Override dari BangunRuang (lewat BelahKetupat)
     */
    @Override
    public double hitungLuasPermukaan() {
        return (2 * hitungLuas()) + hitungLuasSelimut();
    }

    /**
     * Volume = Luas Alas * Tinggi
     * Override dari BangunRuang
     */
    @Override
    public double hitungVolume() {
        return hitungLuas() * tinggi;
    }

    @Override
    public String getNamaBangun() {
        return "Prisma Belah Ketupat";
    }

    @Override
    public String tampilkanInfo() {
        return String.format(
            "=== %s ===\n" +
            "Diagonal 1     : %.2f cm\n" +
            "Diagonal 2     : %.2f cm\n" +
            "Sisi Alas      : %.2f cm\n" +
            "Tinggi         : %.2f cm\n" +
            "Luas Alas      : %.2f cm²\n" +
            "Luas Selimut   : %.2f cm²\n" +
            "Luas Permukaan : %.2f cm²\n" +
            "Volume         : %.2f cm³",
            getNamaBangun(),
            getDiagonal1(), getDiagonal2(),
            hitungSisi(), tinggi,
            hitungLuas(), hitungLuasSelimut(),
            hitungLuasPermukaan(), hitungVolume()
        );
    }
}
