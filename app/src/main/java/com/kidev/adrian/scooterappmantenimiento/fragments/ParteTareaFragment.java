package com.kidev.adrian.scooterappmantenimiento.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.kidev.adrian.scooterappmantenimiento.R;
import com.kidev.adrian.scooterappmantenimiento.activities.MenuActivity;
import com.kidev.adrian.scooterappmantenimiento.entities.Tarea;
import com.kidev.adrian.scooterappmantenimiento.interfaces.CallbackRespuesta;
import com.kidev.adrian.scooterappmantenimiento.model.ParteViewModel;
import com.kidev.adrian.scooterappmantenimiento.util.AndroidUtil;
import com.kidev.adrian.scooterappmantenimiento.util.ConectorTCP;
import com.kidev.adrian.scooterappmantenimiento.util.Util;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ParteTareaFragment extends Fragment {

    private MenuActivity menuActivity;
    private ParteViewModel parteViewModel;

    private Tarea initTarea;

    private TextView textoTipoTarea;
    private TextView textoModelo;
    private TextView textoCodigo;
    private TextView textoDireccion;
    private EditText editText;

    private Button boton1;
    private Button boton2;

    private boolean creado = false;

    public ParteTareaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_parte_tarea, container, false);

        menuActivity = (MenuActivity) getActivity();
        parteViewModel = ViewModelProviders.of(getActivity()).get(ParteViewModel.class);

        textoTipoTarea = root.findViewById(R.id.tareaTipo);
        textoModelo = root.findViewById(R.id.tareaMatricula);
        textoCodigo = root.findViewById(R.id.tareaCodigo);
        textoDireccion = root.findViewById(R.id.tareaCalle);
        editText = root.findViewById(R.id.textoDescripcion);
        boton1 = root.findViewById(R.id.botonEnviarSi);
        boton2 = root.findViewById(R.id.botonEnviarNo);

        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarParte(true);
            }
        });
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarParte(false);
            }
        });

        creado=true;
        if(initTarea!=null)
            generarParte(initTarea);
        else
            generarParte(parteViewModel.getTareaSeleccionada());

        initTarea=null;
        return root;
    }

    private void generarParte(Tarea tarea) {
        parteViewModel.setTareaSeleccionada(tarea);
        if (tarea==null) {
            AndroidUtil.crearErrorDialog(getActivity(), "No se ha podido generar el informe");
            cerrarParte();
            return;
        }

        textoTipoTarea.setText(tarea.getTipotarea());
        textoModelo.setText(tarea.getMatricula() + " - " + tarea.getModelo());
        textoCodigo.setText("ID Scooter: " + tarea.getCodigo());
        textoDireccion.setText(tarea.getDireccion());
        editText.setText("");

        boton2.setVisibility(View.VISIBLE);

        int tipoTarea = tarea.getTipoTareaId();
        switch (tipoTarea){
            case 1:
                boton1.setText("Scooter recogida");
                boton2.setVisibility(View.GONE);
                break;
            case 2:
                boton1.setText("Scooter devuelta");
                boton2.setVisibility(View.GONE);
                break;
            case 3:
                boton1.setText("Baterias cambiadas");
                boton2.setVisibility(View.GONE);
                break;
            case 4:
                boton1.setText("Scooter aparcada");
                boton2.setVisibility(View.GONE);
                break;
            case 5:
                boton1.setText("Scooter funciona correctamente");
                boton2.setText("Scooter no funciona");
                break;
            case 6:
                boton1.setText("La scooter ya es funcional");
                boton2.setText("La scooter sigue sin funcionar");
                break;
            case 7:
                boton1.setText("La scooter es rentable repararla");
                boton2.setText("La scooter no es rentable repararla");
                break;
        }
    }

    public void configurarParte (Tarea tarea) {
        if (creado)
            generarParte(tarea);
        else
            initTarea = tarea;
    }

    public void cerrarParte () {
        // Cerramos este fragment y volvemos al fragment de incidencias
        parteViewModel.setTareaSeleccionada(null);
        menuActivity.closeParteIncidencia();
    }

    private void enviarParte (boolean conseguido) {
        final Map<String,String> parametros = new HashMap<>();
        Tarea tarea = parteViewModel.getTareaSeleccionada();
        if (tarea==null) {
            AndroidUtil.crearErrorDialog(getActivity(), "No hay tarea disponible para enviar");
            return;
        }

        parametros.put("id", tarea.getId().toString());
        parametros.put("observaciones", editText.getText().toString());
        parametros.put("opcion", conseguido?"si":"no");

        AndroidUtil.crearAcceptDialog(getActivity(), "Confirmación", "¿Quieres enviar el parte de tarea?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConectorTCP.getInstance().realizarConexion(getActivity(),"enviarParte", parametros, new CallbackRespuesta() {
                    @Override
                    public void success(Map<String, String> contenido) {
                        AndroidUtil.crearDialog(getActivity(), "Confirmacion", "Se ha enviado el parte de incidencia correctamente", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        menuActivity.actualizarListaTareas();
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
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

}
