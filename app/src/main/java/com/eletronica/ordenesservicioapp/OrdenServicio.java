 package com.eletronica.ordenesservicioapp;

import java.io.Serializable;

public class OrdenServicio implements Serializable {
    public int id_orden_servicio;
    public String nombre_cliente;
    public String observaciones;
    public String fecha;
    public int status_servicio;
    public String status_servicio_descripcion;
    public String fecha_entrega;

    public String nombre_equipo;
    public String modelo_equipo;
    public String serie_equipo;
    public String nombre_tecnico;
    public String descripcion_falla;
    public String descripcion_diagnostico;
    public String descripcion_reparacion;
    public double importe_presupuesto;
    public String ruta_imagen;
    public String nombre_imagen;
    public byte[] imagen;

    public String nombre_imagen_back;
    public byte[] imagen_back;

    public String nombre_imagen_tecnico;
    public byte[] imagen_tecnico;

    public String token;
    public String celular;

    public int id_tecnico;
    public int id_cliente;

    public int tipo_servicio;
    public int id_cliente_venta;
    public int id_puntodeventa;
    public double precio_venta;
    public String nombre_cliente_venta;


}
