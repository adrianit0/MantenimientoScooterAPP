package com.kidev.adrian.scooterappmantenimiento.entities;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Tarea {

    private Integer id;
    private String nombre;
    private Date fechaAsignacion;
    private int estimacion;

    private int estadotarea;
    private String tipotarea;
    private int tipoTareaId;

    // Info de la scooter
    private LatLng posicion;
    private String noSerie;
    private String modelo;
    private String matricula;
    private String direccion;
    private String codigo;

    public Tarea() {
    }

    public Tarea(String nombre, Date fechaAsignacion, int estimacion) {
        this.nombre = nombre;
        this.fechaAsignacion = fechaAsignacion;
        this.estimacion = estimacion;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public int getEstadotarea() {
        return this.estadotarea;
    }

    public void setEstadotarea(int estadotarea) {
        this.estadotarea = estadotarea;
    }
    public String getTipotarea() {
        return this.tipotarea;
    }

    public void setTipotarea(String tipotarea) {
        this.tipotarea = tipotarea;
    }
    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public Date getFechaAsignacion() {
        return this.fechaAsignacion;
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
    public int getEstimacion() {
        return this.estimacion;
    }

    public void setEstimacion(int estimacion) {
        this.estimacion = estimacion;
    }

    public LatLng getPosicion() {
        return posicion;
    }

    public void setPosicion(LatLng posicion) {
        this.posicion = posicion;
    }

    public String getNoSerie() {
        return noSerie;
    }

    public void setNoSerie(String noSerie) {
        this.noSerie = noSerie;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getTipoTareaId() {
        return tipoTareaId;
    }

    public void setTipoTareaId(int tipoTareaId) {
        this.tipoTareaId = tipoTareaId;
    }
}