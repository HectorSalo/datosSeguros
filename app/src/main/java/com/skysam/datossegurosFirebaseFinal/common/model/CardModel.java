package com.skysam.datossegurosFirebaseFinal.common.model;

public class CardModel {
    private String titular, tipo, numeroTarjeta, cedula, cvv, idTarjeta, banco, vencimiento, clave;
    private boolean isExpanded = false;


    public CardModel() {}

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getIdTarjeta() {
        return idTarjeta;
    }

    public String getTitular() {
        return titular;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public String getCedula() {
        return cedula;
    }

    public String getCvv() {
        return cvv;
    }

    public String getBanco() {
        return banco;
    }

    public String getVencimiento() {
        return vencimiento;
    }

    public String getClave() {
        return clave;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public void setIdTarjeta(String idTarjeta) {
        this.idTarjeta = idTarjeta;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public void setVencimiento(String vencimiento) {
        this.vencimiento = vencimiento;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
