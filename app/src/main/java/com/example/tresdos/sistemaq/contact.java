package com.example.tresdos.sistemaq;

/**
 * Created by tresdos on 02-07-17.
 */

public class contact {
    private String nombre;
    private String numero;
    private String key;

    public contact(){

    }
    public contact(String nombre, String numero){
        this.nombre = nombre;
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
