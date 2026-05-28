import java.util.List;

public abstract class BangunGeometri extends Thread {
    protected int targetGenerate = 0;
    protected List<BangunGeometri> peerThreads;
    private final int id;
    private final String namaBangun;
    private final MesinHitung gui;

    public BangunGeometri(int id, String namaBangun, MesinHitung gui) {
        this.id = id;
        this.namaBangun = namaBangun;
        this.gui = gui;
    }

    public BangunGeometri(String namaBangun, int targetGenerate, MesinHitung gui) {
        this.id = 0;
        this.namaBangun = namaBangun;
        this.targetGenerate = targetGenerate;
        this.gui = gui;
    }

    public int getIdBangun() {
        return this.id;
    }

    public String getNamaBangun() {
        return this.namaBangun;
    }

    public MesinHitung getGui() {
        return this.gui;
    }

    public void setPeerThreads(List<BangunGeometri> peers) {
        this.peerThreads = peers;
    }

    public abstract void hitungSemua();
}