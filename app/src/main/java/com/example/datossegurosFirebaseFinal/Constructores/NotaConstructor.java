package com.example.datossegurosFirebaseFinal.Constructores;

public class NotaConstructor {
    private String titulo, contenido;

    public NotaConstructor() {}

    public NotaConstructor(String titulo, String contenido) {
        this.titulo = titulo;
        this.contenido = contenido;
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
}