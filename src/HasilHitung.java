/**
 * Data class untuk menyimpan hasil perhitungan satu objek bangun ruang.
 * Digunakan sebagai row data pada tabel hasil.
 */
public class HasilHitung {
    private final int    id;
    private final String namaBangun;
    private final String parameter;
    private final double luasPermukaan;
    private final double volume;
    private final int    threadId;

    public HasilHitung(int id, String namaBangun, String parameter,
                       double luasPermukaan, double volume, int threadId) {
        this.id            = id;
        this.namaBangun    = namaBangun;
        this.parameter     = parameter;
        this.luasPermukaan = luasPermukaan;
        this.volume        = volume;
        this.threadId      = threadId;
    }

    public int getId(){ 
        return id; 
    }
    public String getNamaBangun(){
        return namaBangun; 
    }
    public String getParameter(){
        return parameter; 
    }
    public double getLuasPermukaan() { 
        return luasPermukaan; 
    }
    public double getVolume(){
        return volume; 
    }
    public int getThreadId(){ 
        return threadId; 
    }
}
