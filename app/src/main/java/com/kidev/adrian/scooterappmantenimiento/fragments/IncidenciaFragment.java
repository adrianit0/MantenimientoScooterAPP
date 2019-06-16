package com.kidev.adrian.scooterappmantenimiento.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidev.adrian.scooterappmantenimiento.R;
import com.kidev.adrian.scooterappmantenimiento.activities.MenuActivity;
import com.kidev.adrian.scooterappmantenimiento.adapter.TaskAdapter;
import com.kidev.adrian.scooterappmantenimiento.entities.Empleado;
import com.kidev.adrian.scooterappmantenimiento.entities.Tarea;
import com.kidev.adrian.scooterappmantenimiento.model.ParteViewModel;
import com.kidev.adrian.scooterappmantenimiento.util.AndroidUtil;

import java.util.ArrayList;
import java.util.List;

public class IncidenciaFragment extends Fragment {

    private TaskAdapter mAdapter;
    private ArrayList<Tarea> tareas;
    private RecyclerView recyclerView;

    private MenuActivity menuActivity;

    public IncidenciaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_incidencia, container, false);

        tareas = new ArrayList<>();

        recyclerView= root.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        menuActivity = (MenuActivity) getActivity();

        ViewModelProviders.of(getActivity()).get(ParteViewModel.class).getTareas(getActivity(), false).observe(getActivity(), new Observer<List<Tarea>>() {
            @Override
            public void onChanged(@Nullable List<Tarea> tareas) {
                mAdapter = new TaskAdapter(tareas, R.layout.tarea_row, getActivity(), new TaskAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View vista, final Tarea tarea) {
                        menuActivity.openParteIncidencia(tarea);
                    }
                });

                recyclerView.setAdapter(mAdapter);
            }
        });

        return root;
    }
}
