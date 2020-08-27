package com.skysam.datossegurosFirebaseFinal.ui.interfaces;

public interface InicSesion {
    void showProgress();
    void hideProgress();
    void showErrorEmailEmpty();
    void showErrorPassEmpty();
    void showErrorEmailFormatIncorrect();
    void showErrorPassEmptyLongIncorrect();
    void initSession();
    void errorSession();
}
