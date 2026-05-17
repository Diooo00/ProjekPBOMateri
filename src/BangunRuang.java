/**
 * ABSTRACTION - Abstract class fondasi semua bangun ruang.
 * Mendefinisikan kontrak yang WAJIB diimplementasi semua subclass.
 */
public abstract class BangunRuang extends BangunGeometri{
    public abstract double hitungLuasPermukaan();
    public abstract double hitungVolume();
    public abstract String getNamaBangun();
    public abstract String getRingkasan();
}
