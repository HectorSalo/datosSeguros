package com.example.datossegurosFirebaseFinal.Constructores;

public class TarjetaConstructor {
    private String titular, tipo;
    private int numeroTarjeta, cedula, cvv;

    public TarjetaConstructor() {}

    public TarjetaConstructor (String titular, int numeroTarjeta, int cvv, int cedula, String tipo) {
        this.titular = titular;
        this.numeroTarjeta = numeroTarjeta;
        this.cvv = cvv;
        this.cedula = cedula;
        this.tipo = tipo;
    }

    public String getTitular() {
        return titular;
    }

    public String getTipo() {
        return tipo;
    }

    public int getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public int getCedula() {
        return cedula;
    }

    public int getCvv() {
        return cvv;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setNumeroTarjeta(int numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public void setCedula(int cedula) {
        this.cedula = cedula;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }
}
