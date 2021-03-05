package com.eletronica.ordenesservicioapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Menu extends AppCompatActivity {
    static View mView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mView = (View) findViewById(R.id.menuView);
        CardView btnOrdenes = (CardView) findViewById(R.id.btnOrdenesServicio);
        CardView btnClientes = (CardView) findViewById(R.id.btnClientes);
        CardView btnUsuarios = (CardView) findViewById(R.id.btnUsuarios);
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

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

    @Override public void onBackPressed() {

    }
}
