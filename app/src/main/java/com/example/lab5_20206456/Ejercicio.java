package com.example.lab5_20206456;

public class Ejercicio {
    private String nombre;
    private int tiempo;
    private int calorias;

    // Constructor
    public Ejercicio(String nombre, int tiempo, int calorias) {
        this.nombre = nombre;
        this.tiempo = tiempo;
        this.calorias = calorias;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public int getCalorias() {
        return calorias;
    }

    public void setCalorias(int calorias) {
        this.calorias = calorias;
    }
}
