package com.kidev.adrian.scooterappmantenimiento.model;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.kidev.adrian.scooterappmantenimiento.entities.Tarea;
import com.kidev.adrian.scooterappmantenimiento.interfaces.CallbackRespuesta;
import com.kidev.adrian.scooterappmantenimiento.util.AndroidUtil;
import com.kidev.adrian.scooterappmantenimiento.util.ConectorTCP;
import com.kidev.adrian.scooterappmantenimiento.util.Util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParteViewModel extends AndroidViewModel {

    private MutableLiveData<List<Tarea>> tareas;
    private MutableLiveData<LatLng> tuPosicion;

    private String actualFragment;

    private long timeRemain=0;
    private Integer scooterID=null;

    public ParteViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<List<Tarea>> getTareas (Activity activity, boolean forzar) {
        if (forzar || tareas==null) {
            tareas = new MutableLiveData<>();
            traerTareas(activity);
        }

        return tareas;
    }

    public LiveData<LatLng> getTuPosicion () {
        if (tuPosicion==null) {
            tuPosicion = new MutableLiveData<>();
        }

        return tuPosicion;
    }

    public LatLng getLastPosition() {
        if (tuPosicion==null)
            return null;

        return tuPosicion.getValue();
    }

    public void changePosition (LatLng posicion) {
        tuPosicion.setValue(posicion);
    }

    public Integer getScooterID() {
        return scooterID;
    }

    public void setScooterID(Integer scooterID) {
        this.scooterID = scooterID;
    }

    public long getTimeRemain() {
        return timeRemain;
    }

    public void setTimeRemain(long timeRemain) {
        this.timeRemain = timeRemain;
    }

    public String getActualFragment() {
        return actualFragment;
    }

    public void setActualFragment(String actualFragment) {
        this.actualFragment = actualFragment;
    }


    private void traerTareas (final Activity activity) {
        Map<String,String> parametros = new HashMap<>();
        ConectorTCP.getInstance().realizarConexion(activity,"getTareas", parametros, new CallbackRespuesta() {
            @Override
            public void success(Map<String, String> contenido) {
                int length = Integer.parseInt(contenido.get("length"));
                Log.i("Conexión exitosa", "Se han recuperado " + length + " tareas");

                List<Tarea> tareaList = new ArrayList<>();

                for (int i = 0; i < length; i++) {
                    int id = Integer.parseInt(contenido.get("id[" + i + "]"));
                    String nombre = contenido.get("nombre[" + i + "]");
                    Date fechaAsignacion = new Date (Long.parseLong(contenido.get("fechaAsignacion[" + i + "]")));
                    int estimacion = Integer.parseInt(contenido.get("estimacion[" + i + "]"));
                    int estadoTarea = Integer.parseInt(contenido.get("estadoTarea[" + i + "]"));
                    String tipoTarea = contenido.get("tipoTarea[" + i + "]");

                    String matricula = contenido.get("matricula[" + i + "]");
                    String noSerie = contenido.get("noSerie[" + i + "]");
                    float lat = Float.parseFloat(contenido.get("posicionLat[" + i + "]"));
                    float lon = Float.parseFloat(contenido.get("posicionLon[" + i + "]"));
                    String modelo = contenido.get("modelo[" + i + "]");


                    Tarea tarea = new Tarea();
                    tarea.setId(id);
                    tarea.setNombre(nombre);
                    tarea.setFechaAsignacion(fechaAsignacion);
                    tarea.setEstimacion(estimacion);
                    tarea.setEstadotarea(estadoTarea);
                    tarea.setTipotarea(tipoTarea);
                    tarea.setMatricula(matricula);
                    tarea.setNoSerie(noSerie);
                    tarea.setPosicion(new LatLng(lat, lon));
                    tarea.setModelo(modelo);

                    String direccion = AndroidUtil.getStreetName(activity, lat, lon);
                    tarea.setDireccion(direccion);

                    tareaList.add(tarea);
                }

                tareas.setValue(tareaList);
            }

            @Override
            public void error(Map<String, String> contenido, Util.CODIGO codigoError) {
                Log.e("Error de conexión", "No se han cargado las tareas " + codigoError.toString());
                AndroidUtil.crearToast(activity, "No se han podido cargar las tareas");
            }
        });
    }
}
