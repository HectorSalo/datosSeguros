package com.skysam.datossegurosFirebaseFinal.Constructores;

public class NotaConstructor {
    private String titulo, contenido, idNota;

    public NotaConstructor() {}

    public NotaConstructor(String titulo, String contenido) {
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public String getIdNota() {
        return idNota;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public void setIdNota(String idNota) {
        this.idNota = idNota;
    }
}
