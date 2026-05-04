/**
 * Abstract class yang menjadi fondasi dari semua bangun ruang.
 * Pilar OOP yang dicover: ABSTRACTION
 *
 * Abstraksi dilakukan dengan mendefinisikan method-method yang WAJIB
 * diimplementasikan oleh setiap subclass, tanpa menyebutkan detail cara
 * menghitungnya. Setiap bangun ruang punya cara hitung sendiri-sendiri.
 */
public abstract class BangunRuang {

    // ==================== ABSTRACTION ====================
    // Method abstract = harus diimplementasikan oleh subclass
    public abstract double hitungLuasPermukaan();
    public abstract double hitungVolume();
    public abstract String getNamaBangun();

    /**
     * Method template (non-abstract) yang bisa dipakai semua subclass.
     * Ini juga bagian dari abstraksi: kita menyediakan perilaku umum
     * tanpa subclass perlu tahu detail implementasinya.
     */
    public String tampilkanInfo() {
        return String.format(
            "=== %s ===\n" +
            "Luas Permukaan : %.2f cm²\n" +
            "Volume         : %.2f cm³",
            getNamaBangun(),
            hitungLuasPermukaan(),
            hitungVolume()
        );
    }
}
