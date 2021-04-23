package com.skysam.datossegurosFirebaseFinal.common.model;

public class NoteModel {
    private String titulo, contenido, idNota;
    private boolean isExpanded = false;

    public NoteModel() {}

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
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
