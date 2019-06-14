package com.kidev.adrian.scooterappmantenimiento.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidev.adrian.scooterappmantenimiento.R;
import com.kidev.adrian.scooterappmantenimiento.activities.MenuActivity;
import com.kidev.adrian.scooterappmantenimiento.entities.Empleado;

public class UserFragment extends Fragment {

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_user, container, false);

        MenuActivity menu = (MenuActivity) getActivity();

        final Empleado empleado = menu.getEmpleado();

        TextView textoNombre = root.findViewById(R.id.textoNombre);
        TextView textoApellido1 = root.findViewById(R.id.textoApellido1);
        TextView textoApellido2 = root.findViewById(R.id.textoApellido2);
        TextView textoDNI = root.findViewById(R.id.textoDNI);
        TextView textoEmail = root.findViewById(R.id.textoEmail);
        TextView textoFechaAlta = root.findViewById(R.id.textoFechaAlta);
        TextView textoCiudad = root.findViewById(R.id.textoCiudad);
        TextView textoSede = root.findViewById(R.id.textoSede);
        TextView textoPuesto = root.findViewById(R.id.textoPuesto);
        TextView textoSalario = root.findViewById(R.id.textoSalario);

        textoNombre.setText(empleado.getNombre());
        textoApellido1.setText(empleado.getApellido1());
        textoApellido2.setText(empleado.getApellido2());
        textoDNI.setText(empleado.getDni());
        textoEmail.setText(empleado.getEmail());
        textoFechaAlta.setText(empleado.getFechaAlta());
        textoCiudad.setText(empleado.getCiudad());
        textoSede.setText(empleado.getSede());
        textoPuesto.setText(empleado.getPuesto());
        textoSalario.setText((empleado.getSueldo()*14) + "€");

        return root;
    }
}