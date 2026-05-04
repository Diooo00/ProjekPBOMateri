/**
 * Class LimasBelahKetupat - Limas dengan alas belah ketupat.
 *
 * Pilar OOP yang dicover:
 * - INHERITANCE: extends BelahKetupat
 * - ENCAPSULATION: atribut tinggi private
 * - POLYMORPHISM: override method hitungLuasPermukaan dan hitungVolume
 *
 * Rumus Limas Belah Ketupat:
 * - Tinggi Segitiga (apotema) = sqrt(tinggi^2 + (d/2)^2)
 *   dimana d adalah diagonal yang tegak lurus dengan sisi segitiga tsb
 * - Luas 1 Segitiga Sisi = (sisi_alas * tinggi_segitiga) / 2
 * - Luas Selimut = 4 * Luas 1 Segitiga Sisi
 * - Luas Permukaan = Luas Alas + Luas Selimut
 * - Volume = (1/3) * Luas Alas * Tinggi
 *
 *         /\
 *        /  \
 *       / BK \   <- puncak limas
 *      /      \
 *     /________\  <- alas belah ketupat
 */
public class LimasBelahKetupat extends BelahKetupat {

    // ==================== ENCAPSULATION ====================
    private double tinggi;

    // ==================== CONSTRUCTOR ====================
    public LimasBelahKetupat(double diagonal1, double diagonal2, double tinggi) {
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
     * Tinggi segitiga sisi 1 (berdasarkan diagonal1)
     * apotema1 = sqrt(tinggi^2 + (d2/2)^2)
     */
    public double hitungTinggiSegitigaSisi1() {
        return Math.sqrt(Math.pow(tinggi, 2) + Math.pow(getDiagonal2() / 2, 2));
    }

    /**
     * Tinggi segitiga sisi 2 (berdasarkan diagonal2)
     * apotema2 = sqrt(tinggi^2 + (d1/2)^2)
     */
    public double hitungTinggiSegitigaSisi2() {
        return Math.sqrt(Math.pow(tinggi, 2) + Math.pow(getDiagonal1() / 2, 2));
    }

    /**
     * Luas selimut = 2 sisi menggunakan apotema1 + 2 sisi menggunakan apotema2
     * Karena belah ketupat memiliki 4 sisi sama panjang namun 2 pasang segitiga:
     * - 2 segitiga dengan alas d1/2 dan tinggi apotema1 (per setengah)
     * - 2 segitiga dengan alas d2/2 dan tinggi apotema2 (per setengah)
     *
     * Lebih mudah: Luas selimut = (d1 * apotema2) + (d2 * apotema1)
     * Karena: 2*(0.5 * d1/2 * apotema2) * 2 sisi = d1 * apotema2 (begitu juga d2)
     */
    public double hitungLuasSelimut() {
        double a1 = hitungTinggiSegitigaSisi1();
        double a2 = hitungTinggiSegitigaSisi2();
        return (getDiagonal1() * a2) + (getDiagonal2() * a1);
    }

    // ==================== POLYMORPHISM (Override) ====================

    /**
     * Luas Permukaan = Luas Alas + Luas Selimut
     */
    @Override
    public double hitungLuasPermukaan() {
        return hitungLuas() + hitungLuasSelimut();
    }

    /**
     * Volume = (1/3) * Luas Alas * Tinggi
     */
    @Override
    public double hitungVolume() {
        return (1.0 / 3.0) * hitungLuas() * tinggi;
    }

    @Override
    public String getNamaBangun() {
        return "Limas Belah Ketupat";
    }

    @Override
    public String tampilkanInfo() {
        return String.format(
            "=== %s ===\n" +
            "Diagonal 1         : %.2f cm\n" +
            "Diagonal 2         : %.2f cm\n" +
            "Sisi Alas          : %.2f cm\n" +
            "Tinggi Limas       : %.2f cm\n" +
            "Tinggi Segitiga 1  : %.2f cm\n" +
            "Tinggi Segitiga 2  : %.2f cm\n" +
            "Luas Alas          : %.2f cm²\n" +
            "Luas Selimut       : %.2f cm²\n" +
            "Luas Permukaan     : %.2f cm²\n" +
            "Volume             : %.2f cm³",
            getNamaBangun(),
            getDiagonal1(), getDiagonal2(),
            hitungSisi(), tinggi,
            hitungTinggiSegitigaSisi1(), hitungTinggiSegitigaSisi2(),
            hitungLuas(), hitungLuasSelimut(),
            hitungLuasPermukaan(), hitungVolume()
        );
    }
}
