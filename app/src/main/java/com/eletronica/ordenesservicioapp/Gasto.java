package com.eletronica.ordenesservicioapp;

public class Gasto {
    int id_gasto;
    String concepto;
    String fecha;
    double importe;
    int tipo;
    String tipo_descripcion;
    int status;
    String status_descripcion;
    int id_orden_servicio;
    String orden_servicio_descripcion;

    public int getId_gasto() {
        return id_gasto;
    }

    public void setId_gasto(int id_gasto) {
        this.id_gasto = id_gasto;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getTipo_descripcion() {
        return tipo_descripcion;
    }

    public void setTipo_descripcion(String tipo_descripcion) {
        this.tipo_descripcion = tipo_descripcion;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatus_descripcion() {
        return status_descripcion;
    }

    public void setStatus_descripcion(String status_descripcion) {
        this.status_descripcion = status_descripcion;
    }

    public int getId_orden_servicio() {
        return id_orden_servicio;
    }

    public void setId_orden_servicio(int id_orden_servicio) {
        this.id_orden_servicio = id_orden_servicio;
    }

    public String getOrden_servicio_descripcion() {
        return orden_servicio_descripcion;
    }

    public void setOrden_servicio_descripcion(String orden_servicio_descripcion) {
        this.orden_servicio_descripcion = orden_servicio_descripcion;
    }
}
