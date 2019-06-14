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

public class IncidenciaFragment extends Fragment {

    public IncidenciaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_incidencia, container, false);

        return root;
    }
}
