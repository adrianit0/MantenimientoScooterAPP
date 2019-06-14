package com.kidev.adrian.scooterappmantenimiento.fragments;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kidev.adrian.scooterappmantenimiento.R;
import com.kidev.adrian.scooterappmantenimiento.activities.MenuActivity;
import com.kidev.adrian.scooterappmantenimiento.entities.Tarea;
import com.kidev.adrian.scooterappmantenimiento.interfaces.CallbackRespuesta;
import com.kidev.adrian.scooterappmantenimiento.interfaces.IOnRequestPermission;
import com.kidev.adrian.scooterappmantenimiento.model.ParteViewModel;
import com.kidev.adrian.scooterappmantenimiento.util.AndroidUtil;
import com.kidev.adrian.scooterappmantenimiento.util.ConectorTCP;
import com.kidev.adrian.scooterappmantenimiento.util.Util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private final String TAG = this.getTag();

    private MapView mMapView;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ParteViewModel parteViewModel;

    private MenuActivity menuActivity;

    private LinearLayout surface;

    private Tarea tareaSeleccionada = null;
    private int position_permission_code = 1;
    private boolean permisoLocalizacion=false;
    private LatLng myLastPosition = null;

    private Button botonActualizar;

    private List<Marker> markers;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        markers = new ArrayList<>();

        menuActivity = (MenuActivity) getActivity();
        parteViewModel = ViewModelProviders.of(getActivity()).get(ParteViewModel.class);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Surfaces
        surface = view.findViewById(R.id.include);


        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstance);

        surface.setVisibility(View.GONE);

        Button botonCerrarSurface = surface.findViewById(R.id.botonCerrarSurface);
        botonCerrarSurface.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    surface.setVisibility(View.GONE);
                    tareaSeleccionada = null;
                }
            }
        );


        botonActualizar = view.findViewById(R.id.botonActualizar);


        botonActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarTareas(true);
            }
        });

        parteViewModel.getTuPosicion().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(@Nullable LatLng latLng) {
                Tarea tarea = tareaSeleccionada;
                if (tarea!=null) {
                    int distancia = AndroidUtil.getDistanceBetweenTwoPoints(tarea.getPosicion(), latLng);
                    TextView text = surface.findViewById(R.id.textoDistancia);
                    text.setText(distancia+"m");
                }
            }
        });

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surface = getActivity().findViewById(R.id.scooter_surface);
        if (surface!=null)
            surface.setVisibility(View.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);


        menuActivity.pedirPermiso(Manifest.permission.ACCESS_FINE_LOCATION, position_permission_code, new IOnRequestPermission() {
            @Override
            public void onPermissionAccepted(String permiso) {
                realizarConexion();
            }

            @Override
            public void onPermissionDenied(String permiso) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Permiso denegado")
                        .setMessage("No tienes permisos para acceder al GPS. No se mostrará tu posición actual.")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (permisoLocalizacion) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                myLastPosition = null;
                // getLocationPermission();
                Toast.makeText(getContext(), "No tienes permisos para coger tu ubicación", Toast.LENGTH_LONG).show();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (permisoLocalizacion) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                if (locationResult!=null) {
                    locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                // Set the map's camera position to the current location of the device.
                                Location location = (Location) task.getResult();
                                myLastPosition = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(myLastPosition.latitude, myLastPosition.longitude), 40));
                                parteViewModel.changePosition(myLastPosition);
                            } else {
                                Log.d(TAG, "Current location is null. Using defaults.");
                                Log.e(TAG, "Exception: %s", task.getException());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLastPosition, 1));
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        }
                    });
                } else {
                    Log.e("MapFragment::getDevice", "No se ha encontrado la posición actual");
                }
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void realizarConexion () {
        permisoLocalizacion=true;

        actualizarTareas(false);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14), 1500, null);

        Tarea tarea = (Tarea) marker.getTag();
        tareaSeleccionada = tarea;
        if (surface!=null) {
            surface.setVisibility(View.VISIBLE);

            TextView matriculaText = surface.findViewById(R.id.textoModelo);
            TextView estadoText = surface.findViewById(R.id.textoTipoTarea);
            TextView distanciaText = surface.findViewById(R.id.textoDistancia);
            TextView calleText = surface.findViewById(R.id.textoCalle);

            estadoText.setText(tarea.getTipotarea());

            matriculaText.setText(tarea.getMatricula() + " - " + tarea.getModelo());

            LatLng scooterPos = tarea.getPosicion();
            if (myLastPosition!=null && scooterPos!=null) {
                int distancia = AndroidUtil.getDistanceBetweenTwoPoints(myLastPosition, scooterPos);
                distanciaText.setText(distancia + "m");
            } else {
                distanciaText.setText("?m");
            }

            calleText.setText(tarea.getDireccion());
        } else {
            Log.e("ERROR map", "El surface no es visible");
        }

        return true;
    }

    private void actualizarTareas (boolean forzar) {
        updateLocationUI();
        getDeviceLocation();

        double latitude = 1;
        double longitude = 1;

        // Eliminamos los anteriores markers si los hubiera
        for (Marker m : markers)
            m.remove();
        markers.clear();

        parteViewModel.getTareas(getActivity(), forzar).observe(getActivity(), new Observer<List<Tarea>>() {
            @Override
            public void onChanged(@Nullable List<Tarea> tareas) {
                LatLng pos = new LatLng(0,0);


                int i = 0;
                for (Tarea tarea : tareas) {
                    MarkerOptions markerOptions = new MarkerOptions().position(tarea.getPosicion());
                    Marker marker = mMap.addMarker(markerOptions);
                    marker.setTag(tarea);

                    markers.add(marker);

                    if (0==i++)
                        pos = tarea.getPosicion();
                }

                // Al actualizar cogemos siempre nuestra posición
                if(parteViewModel.getLastPosition()!=null)
                    pos = parteViewModel.getLastPosition();


                mMap.moveCamera((CameraUpdateFactory.newLatLng(pos)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 14), 1500, null);
            }
        });
    }

    private void cambiarDistanciaScooter (Tarea tarea, TextView distanciaText) {
        if (myLastPosition!=null && tarea!=null) {
            LatLng scooterPos = tarea.getPosicion();
            int distancia = AndroidUtil.getDistanceBetweenTwoPoints(myLastPosition, scooterPos);
            distanciaText.setText(distancia + "m");
        } else {
            distanciaText.setText("?m");
        }
    }
}