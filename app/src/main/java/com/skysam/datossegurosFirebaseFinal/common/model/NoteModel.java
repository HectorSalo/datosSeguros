package com.skysam.datossegurosFirebaseFinal.common.model;

public class NoteModel {
    private String titulo, contenido, idNota;

    public NoteModel() {}

    public NoteModel(String titulo, String contenido) {
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
