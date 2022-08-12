package com.eletronica.ordenesservicioapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Menu extends AppCompatActivity {
    static View mView;
    int REQUEST_CODE_SENDMSG = 100;
    int REQUEST_CODE_CAMARA = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mView = (View) findViewById(R.id.menuView);
        CardView btnOrdenes = (CardView) findViewById(R.id.btnOrdenesServicio);
        CardView btnClientes = (CardView) findViewById(R.id.btnClientes);
        CardView btnUsuarios = (CardView) findViewById(R.id.btnUsuarios);
        CardView btnPuntosDeVentas = (CardView) findViewById(R.id.btnPuntosDeVentas);
        CardView btnConfiguracionMenu = (CardView) findViewById(R.id.btnConfiguracionMenu);
        ImageView btnSalir = (ImageView) findViewById(R.id.btnCerrarSesion);
        CardView btnGastos = (CardView) findViewById(R.id.btnGastos);
        TextView txtEmpresa = (TextView) findViewById(R.id.txtNombreEmpresa);

        GlobalVariables vg = new GlobalVariables();

        txtEmpresa.setText(vg.nombre_empresa);

        if(vg.rol != 5) {
            btnUsuarios.setVisibility(View.GONE);
            btnGastos.setVisibility(View.GONE);
        }

        btnOrdenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(mView.getContext(), OrdenesServicios.class);
                intencion.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intencion);

            }
        });

        btnClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(mView.getContext(), Clientes.class);
                intencion.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intencion);

            }
        });

        btnUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(mView.getContext(), Usuarios.class);
                intencion.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intencion);

            }
        });

        btnGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(mView.getContext(), Gastos.class);
                intencion.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intencion);

            }
        });

        btnPuntosDeVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(mView.getContext(), PuntosVentas.class);
                intencion.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intencion);

            }
        });

        btnConfiguracionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(mView.getContext(), NuevaConfiguracion.class);
                intencion.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intencion);

            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        veridicarPermisos();
    }

    private void veridicarPermisos(){
        int PermisoSms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int PermisoCamara = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int PermisoAlm = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(PermisoSms == PackageManager.PERMISSION_GRANTED && PermisoCamara == PackageManager.PERMISSION_GRANTED && PermisoAlm == PackageManager.PERMISSION_GRANTED){

        }else{
            requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_SENDMSG);

        }




    }

    @Override public void onBackPressed() {

    }
}
