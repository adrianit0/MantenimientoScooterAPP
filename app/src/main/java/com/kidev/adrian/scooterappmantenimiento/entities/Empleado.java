package com.kidev.adrian.scooterappmantenimiento.entities;

import com.google.android.gms.common.api.Api;

import java.sql.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Empleado  implements java.io.Serializable {

    private Integer id;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String dni;
    private String direccion;
    private String email;
    private double sueldo;
    private String fechaAlta;
    private String fechaBaja;

    private String ciudad;
    private String sede;
    private String puesto;

    public Empleado() {
    }


    public Empleado(String nombre, String apellido1, String dni, String direccion, String email, String pass, double sueldo) {
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.dni = dni;
        this.direccion = direccion;
        this.email = email;
        this.sueldo = sueldo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(String fechaBaja) {
        this.fechaBaja = fechaBaja;
    }
    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido1() {
        return this.apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }
    public String getApellido2() {
        return this.apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }
    public String getDni() {
        return this.dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
    public String getDireccion() {
        return this.direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public double getSueldo() {
        return this.sueldo;
    }

    public void setSueldo(double sueldo) {
        this.sueldo = sueldo;
    }

    public String getNombreCompleto () {
        return nombre + " " + apellido1 + " " + (apellido2==null?"":apellido2);
    }
}