package com.example.datossegurosFirebaseFinal.Constructores;

public class TarjetaConstructor {
    private String titular, tipo,numeroTarjeta, cedula, cvv, idTarjeta;


    public TarjetaConstructor() {}

    public TarjetaConstructor (String titular, String numeroTarjeta, String cvv, String cedula, String tipo) {
        this.titular = titular;
        this.numeroTarjeta = numeroTarjeta;
        this.cvv = cvv;
        this.cedula = cedula;
        this.tipo = tipo;
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
}
