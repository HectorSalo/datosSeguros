package com.skysam.datossegurosFirebaseFinal.common.model;

public class AccountModel {

    private String titular, banco, tipo, numeroCuenta, cedula, telefono, idCuenta, correo, tipoDocumento;


    public AccountModel() {}

    public AccountModel(String titular, String banco, String tipo, String numeroCuenta, String cedula, String telefono, String correo, String tipoDocumento) {
        this.titular = titular;
        this.banco = banco;
        this.tipo = tipo;
        this.numeroCuenta = numeroCuenta;
        this.cedula = cedula;
        this.telefono = telefono;
        this.correo = correo;
        this.tipoDocumento = tipoDocumento;
    }

    public String getIdCuenta() {
        return idCuenta;
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

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public String getCedula() {
        return cedula;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
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

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
}
