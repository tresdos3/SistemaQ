package com.example.tresdos.sistemaq;

/**
 * Created by tresdos on 28-06-17.
 */

public class hijos {
    private String nombre;
    private String token;
    private String estado;
    private String key;
    private String alerta;
    private String fecha;
    private int SDK;
    public  hijos(){

    }
    public  hijos(String token, String estado){
        this.token = token;
        this.estado = estado;
    }
    public  hijos(String nombre, String token, String estado, String alerta){
        this.nombre = nombre;
        this.token = token;
        this.estado = estado;
        this.alerta = alerta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAlerta() {
        return alerta;
    }

    public void setAlerta(String alerta) {
        this.alerta = alerta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getSdk() {
        return SDK;
    }

    public void setSdk(int sdk) {
        SDK = sdk;
    }
}
