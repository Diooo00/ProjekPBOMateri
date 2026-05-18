/**
 * Data class untuk menyimpan hasil perhitungan satu objek bangun ruang.
 * Digunakan sebagai row data pada tabel hasil.
 */
public class HasilHitung {
    private final int    id;
    private final String namaBangun;
    private final String parameter;
private final double luasPermukaanOrLuas; 
    private final double volume;
    private final double keliling;            
    private final int    threadId;

    public HasilHitung(int id, String namaBangun, String parameter,
                       double luasPermukaanOrLuas, double volume, double keliling, int threadId) {
        this.id                  = id;
        this.namaBangun          = namaBangun;
        this.parameter           = parameter;
        this.luasPermukaanOrLuas = luasPermukaanOrLuas;
        this.volume              = volume;
        this.keliling            = keliling;  
        this.threadId            = threadId;
    }

    public int    getId()                  { return id; }
    public String getNamaBangun()          { return namaBangun; }
    public String getParameter()           { return parameter; }
    public double getLuasPermukaanOrLuas() { return luasPermukaanOrLuas; }
    public double getVolume()              { return volume; }
    public double getKeliling()            { return keliling; }
    public int    getThreadId()            { return threadId; }
}
