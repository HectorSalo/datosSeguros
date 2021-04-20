package com.skysam.datossegurosFirebaseFinal.common.model;

public class PasswordsModel {
    private String servicio, usuario, contrasena, idContrasena;
    private int vencimiento;
    private boolean isExpanded = false;

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public PasswordsModel() {}

    public String getIdContrasena() {
        return idContrasena;
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

    public void setIdContrasena(String idContrasena) {
        this.idContrasena = idContrasena;
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
