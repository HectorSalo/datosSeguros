package com.example.datossegurosFirebase.Constructores;

public class BancoConstructor {

    private String titular, banco, tipo;
    private int numeroCuenta, cedula, telefono;

    public BancoConstructor() {}

    public BancoConstructor (String titular, String banco, String tipo, int numeroCuenta, int cedula, int telefono) {
        this.titular = titular;
        this.banco = banco;
        this.tipo = tipo;
        this.numeroCuenta = numeroCuenta;
        this.cedula = cedula;
        this.telefono = telefono;
    }

    public String getTitular() {
        return titular;
    }

    public String getBanco() {
        return banco;
    }

    public String getTipo() {
        return tipo;
    }

    public int getNumeroCuenta() {
        return numeroCuenta;
    }

    public int getCedula() {
        return cedula;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setNumeroCuenta(int numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public void setCedula(int cedula) {
        this.cedula = cedula;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }
}
