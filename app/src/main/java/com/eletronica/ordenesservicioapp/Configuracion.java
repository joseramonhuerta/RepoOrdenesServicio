package com.eletronica.ordenesservicioapp;

public class Configuracion {
    int id_configuracion;
    String nombre_empresa;
    String direccion;
    String telefono;
    String leyenda1;
    String leyenda2;
    int imprimeticket;
    int enviamensaje;

    public int getId_configuracion() {
        return id_configuracion;
    }

    public void setId_configuracion(int id_configuracion) {
        this.id_configuracion = id_configuracion;
    }

    public String getNombre_empresa() {
        return nombre_empresa;
    }

    public void setNombre_empresa(String nombre_empresa) {
        this.nombre_empresa = nombre_empresa;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLeyenda1() {
        return leyenda1;
    }

    public void setLeyenda1(String leyenda1) {
        this.leyenda1 = leyenda1;
    }

    public String getLeyenda2() {
        return leyenda2;
    }

    public void setLeyenda2(String leyenda2) {
        this.leyenda2 = leyenda2;
    }

    public int getImprimeticket() {
        return imprimeticket;
    }

    public void setImprimeticket(int imprimeticket) {
        this.imprimeticket = imprimeticket;
    }

    public int getEnviamensaje() {
        return enviamensaje;
    }

    public void setEnviamensaje(int enviamensaje) {
        this.enviamensaje = enviamensaje;
    }
}
/*
    CREATE TABLE `cat_configuracion` (
        `id_configuracion` BIGINT(20) NOT NULL AUTO_INCREMENT,
        `nombre_empresa` VARCHAR(200) DEFAULT NULL,
        `direccion` VARCHAR(200) DEFAULT NULL,
        `telefono` VARCHAR(50) DEFAULT NULL,
        `leyenda1` VARCHAR(200) DEFAULT NULL,
        `leyenda2` VARCHAR(200) DEFAULT NULL,
        `status` CHAR(1) DEFAULT 'A' COMMENT 'A=Activo, I=Inactivo',
        PRIMARY KEY (`id_configuracion`)
        ) ENGINE=MYISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


 */