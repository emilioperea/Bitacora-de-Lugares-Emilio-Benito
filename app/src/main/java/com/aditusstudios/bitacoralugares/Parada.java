package com.aditusstudios.bitacoralugares;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Parada {
    private long id;
    private String titulo;
    private String categoria;
    private String nota;
    private String foto;
    private double latitud;
    private double longitud;
    private long fecha;

    public Parada(String titulo, String categoria, String nota, String foto,
                  double latitud, double longitud, long fecha) {
        this.id = -1;
        this.titulo = titulo;
        this.categoria = categoria;
        this.nota = nota;
        this.foto = foto;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha = fecha;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getNota() {
        return nota;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public long getFecha() {
        return fecha;
    }

    public void setUbicacion(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public void editar(String titulo, String categoria, String nota) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.nota = nota;
    }

    public boolean tieneFoto() {
        return foto != null && !foto.isEmpty();
    }

    public String getFechaTexto() {
        SimpleDateFormat formato = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
        return formato.format(new Date(fecha));
    }

    public String getCoordenadas() {
        return String.format(Locale.US, "%.5f, %.5f", latitud, longitud);
    }

    public int getColorFondo() {
        if (categoria.equals("Comida"))
            return R.color.comida_fondo;
        else if (categoria.equals("Paisaje"))
            return R.color.paisaje_fondo;
        else if (categoria.equals("Escuela"))
            return R.color.escuela_fondo;
        else
            return R.color.otro_fondo;
    }

    public int getColorTexto() {
        if (categoria.equals("Comida"))
            return R.color.comida_texto;
        else if (categoria.equals("Paisaje"))
            return R.color.paisaje_texto;
        else if (categoria.equals("Escuela"))
            return R.color.escuela_texto;
        else
            return R.color.otro_texto;
    }

    @Override
    public String toString() {
        return titulo;
    }
}