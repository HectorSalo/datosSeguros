package com.skysam.datossegurosFirebaseFinal.common.model;

public class CardModel {
    private String titular, tipo, numeroTarjeta, cedula, cvv, idTarjeta, banco, vencimiento, clave;


    public CardModel() {}

    public CardModel(String titular, String numeroTarjeta, String cvv, String cedula, String tipo, String banco, String vencimiento, String clave) {
        this.titular = titular;
        this.numeroTarjeta = numeroTarjeta;
        this.cvv = cvv;
        this.cedula = cedula;
        this.tipo = tipo;
        this.banco = banco;
        this.clave = clave;
        this.vencimiento = vencimiento;
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
