package com.example.datossegurosFirebaseFinal.Constructores;

public class ContrasenaConstructor {
    private String servicio, usuario, contrasena;
    private int vencimiento;

    public ContrasenaConstructor() {}

    public ContrasenaConstructor(String servicio, String usuario, String contrasena, int vencimiento) {
        this.servicio = servicio;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.vencimiento = vencimiento;
    }

    public String getServicio() {
        return servicio;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public int getVencimiento() {
        return vencimiento;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setVencimiento(int vencimiento) {
        this.vencimiento = vencimiento;
    }
}
