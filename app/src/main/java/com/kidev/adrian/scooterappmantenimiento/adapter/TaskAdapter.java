package com.kidev.adrian.scooterappmantenimiento.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidev.adrian.scooterappmantenimiento.R;
import com.kidev.adrian.scooterappmantenimiento.entities.Tarea;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<Tarea> tareas;
    private int layout;
    private Activity activity;
    private OnItemClickListener listener;

    public TaskAdapter(List<Tarea> tareas, int layout, Activity activity, OnItemClickListener listener) {
        this.tareas = tareas;
        this.layout = layout;
        this.activity = activity;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(activity).inflate(layout,viewGroup, false);
        TaskAdapter.ViewHolder viewHolder= new TaskAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder viewHolder, int i) {
        final Tarea tarea = tareas.get(i);

        viewHolder.textoNombre.setText(tarea.getTipotarea());
        viewHolder.textoDescripcion.setText(tarea.getNombre());

        viewHolder.linear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, tarea);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linear;
        private TextView textoNombre;
        private TextView textoDescripcion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textoNombre= itemView.findViewById(R.id.tareaTitulo);
            textoDescripcion = itemView.findViewById(R.id.tareaDescripcion);

            linear = itemView.findViewById(R.id.linear);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View vista, Tarea tarea);
    }
}