package com.rth.timer.dbops;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataRiwayat {

    private String namaPasien;

    private String mode;
    // diagnosa_konsultasi atau
    // therapy

    private String therapy;
    // hijamah, gurah, elektrik, moxa, ruqyah

    private String tanggal;
    // yyyy-mm-dd HH:mm

    private String durasi;
    // angka Satuan:
    // 10 menit
    // 1 jam

    public DataRiwayat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm");
        String date = dateFormat.format(new Date());
        this.setTanggal(date);
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTherapy() {
        return therapy;
    }

    public void setTherapy(String therapy) {
        this.therapy = therapy;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getDurasi() {
        return durasi;
    }

    public void setDurasi(String durasi) {
        this.durasi = durasi;
    }

    public String getNamaPasien() {
        return namaPasien;
    }

    public void setNamaPasien(String namaPasien) {
        this.namaPasien = namaPasien;
    }
}
