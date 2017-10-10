package com.example.tresdos.sistemaq;

/**
 * Created by tresdos on 29-06-17.
 */

public class maps {
    private Double latitud;
    private Double longitud;
    private String IDuid;
    public  maps(){

    }
    public  maps(String Iduid, Double latitud, Double longitud){
        this.IDuid = Iduid;
        this.latitud = latitud;
        this.longitud= longitud;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getIDuid() {
        return IDuid;
    }

    public void setIDuid(String IDuid) {
        this.IDuid = IDuid;
    }
}
