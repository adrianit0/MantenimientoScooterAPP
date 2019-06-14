package com.kidev.adrian.scooterappmantenimiento.activities;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.kidev.adrian.scooterappmantenimiento.R;
import com.kidev.adrian.scooterappmantenimiento.entities.Empleado;
import com.kidev.adrian.scooterappmantenimiento.fragments.IncidenciaFragment;
import com.kidev.adrian.scooterappmantenimiento.fragments.MapFragment;
import com.kidev.adrian.scooterappmantenimiento.fragments.ParteTareaFragment;
import com.kidev.adrian.scooterappmantenimiento.fragments.UserFragment;
import com.kidev.adrian.scooterappmantenimiento.interfaces.CallbackRespuesta;
import com.kidev.adrian.scooterappmantenimiento.interfaces.IOnRequestPermission;
import com.kidev.adrian.scooterappmantenimiento.model.ParteViewModel;
import com.kidev.adrian.scooterappmantenimiento.util.AndroidUtil;
import com.kidev.adrian.scooterappmantenimiento.util.ConectorTCP;
import com.kidev.adrian.scooterappmantenimiento.util.Util;

import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private Empleado empleado;
    private IOnRequestPermission mCallback;
    private ParteViewModel parteViewModel;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    // Fragments:
    private MapFragment mapFragment;
    private UserFragment userFragment;
    private IncidenciaFragment incidenciaFragment;
    private ParteTareaFragment parteIncidenciaFragment;

    private String lastTag;
    private String preCameraTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        parteViewModel = ViewModelProviders.of(this).get(ParteViewModel.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        String token = i.getStringExtra("token");

        mapFragment = new MapFragment();
        userFragment = new UserFragment();
        incidenciaFragment = new IncidenciaFragment();
        parteIncidenciaFragment = new ParteTareaFragment();

        empleado = new Empleado();
        empleado.setNombre(i.getStringExtra("nombre"));
        empleado.setApellido1(i.getStringExtra("apellido1"));
        empleado.setApellido2(i.getStringExtra("apellido2"));
        empleado.setEmail(i.getStringExtra("email"));
        empleado.setId(Integer.parseInt(i.getStringExtra("id")));
        empleado.setDni(i.getStringExtra("dni"));
        empleado.setFechaAlta(i.getStringExtra("fechaCreacion"));
        empleado.setCiudad(i.getStringExtra("ciudad"));
        empleado.setSede(i.getStringExtra("sede"));
        empleado.setPuesto(i.getStringExtra("puesto"));
        empleado.setSueldo(Double.parseDouble(i.getStringExtra("sueldo")));


        ConectorTCP conector = ConectorTCP.getInstance();
        conector.setNick(empleado.getEmail());
        conector.setToken(token);

        // Deslizador
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Navigation View
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        actualizarDatosNavigationView();

        // FragmentManager
        String tag = parteViewModel.getActualFragment();
        if (tag==null) {
            mostrarFragment(R.id.contenedor, mapFragment, getApplicationContext().getString(R.string.fragment_incidencia), false);
        } else {
            mostrarFragmentByTag(tag, false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(lastTag.equals(getApplicationContext().getString(R.string.fragment_parte))) {
            mostrarFragmentByTag(getApplicationContext().getString(R.string.fragment_incidencia), false);
        }else {
            preguntarDesconectar();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mapa) {
            mostrarFragment(R.id.contenedor, mapFragment, getApplicationContext().getString(R.string.fragment_map), false);
        } else if (id == R.id.nav_perfil) {
            mostrarFragment(R.id.contenedor, userFragment, getApplicationContext().getString(R.string.fragment_perfil), false);
        } else if (id == R.id.nav_incidencia) {
            mostrarFragment(R.id.contenedor, incidenciaFragment, getApplicationContext().getString(R.string.fragment_incidencia), false);
        } else if (id == R.id.nav_disconnect) {
            preguntarDesconectar();
        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void preguntarDesconectar() {
        AndroidUtil.crearAcceptDialog(this, "Desconectar", "¿Quieres desconectarte del servidor?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                desconectar();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void desconectar () {
        final Activity activity = this;
        Map<String,String> parametros = new HashMap<>();
        ConectorTCP.getInstance().realizarConexion(this,"desconectarEmpleado", parametros, new CallbackRespuesta() {
            @Override
            public void success(Map<String, String> contenido) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void error(Map<String, String> contenido, Util.CODIGO codigoError) {
                AndroidUtil.crearToast(activity,"No se ha podido desconectar del servidor. " + contenido.get("error"));
            }
        });
    }

    public void openParteIncidencia (int codigoParte, Integer codigoScooter) {
        mostrarFragment(R.id.contenedor, parteIncidenciaFragment, getApplicationContext().getString(R.string.fragment_parte), true);
        parteIncidenciaFragment.configurarParte(codigoParte, codigoScooter);
    }

    private void mostrarFragment (int resId, Fragment fragment, String tag, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if ( lastTag != null ) {
            Fragment lastFragment = fragmentManager.findFragmentByTag( lastTag );
            if ( lastFragment != null ) {
                transaction.hide( lastFragment );
            }
        }

        if ( fragment.isAdded() ) {
            transaction.show( fragment );
        }
        else {
            transaction.add( resId, fragment, tag ).setBreadCrumbShortTitle( tag );
        }

        if ( addToBackStack ) {
            transaction.addToBackStack( tag );
        }

        transaction.commit();
        lastTag = tag;

        ViewModelProviders.of(this).get(ParteViewModel.class).setActualFragment(tag);
    }

    public void mostrarFragmentByTag (String tag, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (lastTag != null) {
            Fragment lastFragment = fragmentManager.findFragmentByTag( lastTag );
            if ( lastFragment != null ) {
                transaction.hide( lastFragment );
            }
        }

        Fragment actualFragment = fragmentManager.findFragmentByTag(tag);

        if (actualFragment!=null) {
            transaction.show( actualFragment );

            if (addToBackStack){
                transaction.addToBackStack(tag);
            }

            transaction.commit();
            lastTag = tag;
        }

        ViewModelProviders.of(this).get(ParteViewModel.class).setActualFragment(tag);
    }

    /**
     * Pide permiso al activity
     * */
    public void pedirPermiso (final String permiso, final int REQUEST_CODE, final IOnRequestPermission callback) {
        mCallback = callback;
        final Activity activity = this;
        if (ContextCompat.checkSelfPermission(activity, permiso) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(activity, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
            callback.onPermissionAccepted(permiso);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(activity.getApplicationContext())
                        .setTitle("Se necesita permiso")
                        .setMessage("Es requerido para el correcto funcionamiento de la aplicación")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, new String[]{permiso}, REQUEST_CODE);
                            }
                        })
                        .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();

            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permiso}, REQUEST_CODE);
            }
        }
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mCallback==null)
            return;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            mCallback.onPermissionAccepted(permissions[0]);
        } else {
            Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            mCallback.onPermissionDenied(permissions[0]);
        }
        // Despues de usar el callback lo eliminamos
        mCallback=null;
    }

    public void actualizarDatosNavigationView() {
        TextView textoNombre = navigationView.getHeaderView(0).findViewById(R.id.textoNombre);
        TextView textoEmail = navigationView.getHeaderView(0).findViewById(R.id.textoEmail);

        textoNombre.setText(empleado.getNombreCompleto());
        textoEmail.setText(empleado.getEmail());
    }
}