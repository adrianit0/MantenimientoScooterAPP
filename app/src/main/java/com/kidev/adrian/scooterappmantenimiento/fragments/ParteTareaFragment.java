package com.kidev.adrian.scooterappmantenimiento.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.kidev.adrian.scooterappmantenimiento.R;
import com.kidev.adrian.scooterappmantenimiento.activities.MenuActivity;
import com.kidev.adrian.scooterappmantenimiento.interfaces.CallbackRespuesta;
import com.kidev.adrian.scooterappmantenimiento.util.AndroidUtil;
import com.kidev.adrian.scooterappmantenimiento.util.ConectorTCP;
import com.kidev.adrian.scooterappmantenimiento.util.Util;

import java.util.HashMap;
import java.util.Map;

public class ParteTareaFragment extends Fragment {


    //TODO: Meter en un viewModel
    private int tipoIncidencia;
    private Integer codigoScooter;
    private LatLng clientePosicion;

    private MenuActivity menuActivity;

    private boolean creado = false;

    public ParteTareaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_parte_tarea, container, false);

        menuActivity = (MenuActivity) getActivity();


        creado=true;
        generarParte();

        return root;
    }

    private void generarParte() {
        // TODO: Rellenar
    }

    public void configurarParte (int codigoParte, Integer codigoScooter) {
        this.tipoIncidencia = codigoParte;
        this.codigoScooter = codigoScooter;

        if (creado)
            generarParte();
    }

    public void configurarParteWithPosition (int codigoParte, LatLng clientePosicion) {
        configurarParte(codigoParte, null);
        this.clientePosicion = clientePosicion;
    }

    public void cerrarParte () {
        // Cerramos este fragment y volvemos al fragment de incidencias
        menuActivity.mostrarFragmentByTag("incidencia", false);
    }

    private void enviarParte () {
        Map<String,String> parametros = new HashMap<>();


        ConectorTCP.getInstance().realizarConexion(getActivity(),"enviarParteTarea", parametros, new CallbackRespuesta() {
            @Override
            public void success(Map<String, String> contenido) {
                AndroidUtil.crearDialog(getActivity(), "Confirmacion", "Se ha enviado el parte de incidencia correctamente", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cerrarParte();
                            }
                        });
                    }
                });
            }

            @Override
            public void error(Map<String, String> contenido, Util.CODIGO codigoError) {
                AndroidUtil.crearDialog(getActivity(), "Error", "Ha habido un error al enviar los datos. " + contenido.get("error"), null);
            }
        });

    }

    // TODO: Cambiar
    private String getTipoIncidencia (int codigo) {
        switch (codigo) {
            case 1:
                return "Scooter mal aparcada";
            case 2:
                return "Scooter dañada";
            case 3:
                return "Scooter no arranca";
            case 4:
            default:
                return "Otros problemas";
        }
    }
}
