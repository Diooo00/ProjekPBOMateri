/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author user
 */
public abstract class BangunDatar extends BangunGeometri {
    
    // Kontrak wajib khusus bangun datar
    public abstract double hitungLuas();

    @Override
    public final double hitungLuasPermukaan() {
        return hitungLuas(); // Luas permukaan bangun datar adalah luasnya sendiri
    }

    @Override
    public final double hitungVolume() {
        return 0.0; // Bangun datar secara matematis mustahil memiliki volume
    }
}
