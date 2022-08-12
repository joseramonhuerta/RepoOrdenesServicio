package com.eletronica.ordenesservicioapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NuevaConfiguracion extends AppCompatActivity {
    int id_configuracion = 0;
    int imprimeticket = 0;
    int enviamensaje = 0;
    View mView;
    String HTTP_URL;
    String FinalJSonObject;

    StringRequest stringRequest;
    TextInputLayout txtNombreEmpresa;
    TextInputLayout txtDireccion;
    TextInputLayout txtTelefono;
    TextInputLayout txtLeyenda1;
    TextInputLayout txtLeyenda2;
    ToggleButton tgbImprimeTicket;
    ToggleButton tgbEnviaMensaje;

    Button btnGuardarConfig;
    ImageView btnAtras;

    Dialog dialogo = null;

    SweetAlertDialog pDialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_configuracion);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mView = (View) findViewById(R.id.viewNuevaConfig);
        txtNombreEmpresa = (TextInputLayout) findViewById(R.id.txtNombreEmpresaConfig);
        txtDireccion = (TextInputLayout) findViewById(R.id.txtDireccionConfig);
        txtTelefono = (TextInputLayout) findViewById(R.id.txtTelefonoConfig);
        txtLeyenda1 = (TextInputLayout) findViewById(R.id.txtLeyenda1Config);
        txtLeyenda2 = (TextInputLayout) findViewById(R.id.txtLeyenda2Config);
        tgbImprimeTicket = (ToggleButton) findViewById(R.id.tgbImprimeTicket);
        tgbEnviaMensaje = (ToggleButton) findViewById(R.id.tgbEnviaMensaje);
        btnGuardarConfig = (Button) findViewById(R.id.btnGuardarConfig);
        btnAtras = (ImageView) findViewById(R.id.btnAtrasNuevaConfig);

        btnGuardarConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procesarGuardar(v);
            }
        });

        tgbImprimeTicket.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    imprimeticket = 1;
                else
                    imprimeticket = 0;
            }
        });

        tgbEnviaMensaje.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    enviamensaje = 1;
                else
                    enviamensaje = 0;
            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.tgbImprimeTicket.setChecked(false);
        this.tgbEnviaMensaje.setChecked(false);

        loadConfiguracion(mView);

    }

    private void loadConfiguracion(View view){
        pDialogo = new SweetAlertDialog(NuevaConfiguracion.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Cargando, espere....");
        pDialogo.setCancelable(false);
        pDialogo.show();


        GlobalVariables variablesGlobales = new GlobalVariables();
        String bd = variablesGlobales.bd;

        HTTP_URL = variablesGlobales.URLServicio + "obtenerconfiguracion.php?basedatos=" + bd;
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new NuevaConfiguracion.ParseJSonDataClass(mView.getContext()).execute();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Showing error message if something goes wrong.
                        //Toast.makeText(mView.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });

        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(mView.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);
    }

    private void procesarGuardar(View view){
        final View vista = view;
        pDialogo = new SweetAlertDialog(NuevaConfiguracion.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Guardando...");
        pDialogo.setCancelable(false);
        pDialogo.show();



        GlobalVariables variablesGlobales = new GlobalVariables();
        final String bd = variablesGlobales.bd;

        HTTP_URL =variablesGlobales.URLServicio + "guardarconfiguracion.php?";
        // Creating StringRequest and set the JSON server URL in here.
        stringRequest = new StringRequest(Request.Method.POST, HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // After done Loading store JSON response in FinalJSonObject string variable.
                FinalJSonObject = response ;

                // Calling method to parse JSON object.
                new NuevaConfiguracion.ParseJSonDataClassGuardar(vista).execute();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Showing error message if something goes wrong.
                final String error_volley = error.getMessage();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(NuevaConfiguracion.this,error_volley,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id_config = String.valueOf(id_configuracion);
                String nombre_empresa = txtNombreEmpresa.getEditText().getText().toString();
                String direccion = txtDireccion.getEditText().getText().toString();
                String telefono = txtTelefono.getEditText().getText().toString();
                String leyenda1 = txtLeyenda1.getEditText().getText().toString();
                String leyenda2 = txtLeyenda2.getEditText().getText().toString();
                int imprime_ticket = imprimeticket;
                int envia_mensaje = enviamensaje;

                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_configuracion", id_config);
                parametros.put("nombre_empresa", nombre_empresa);
                parametros.put("direccion", direccion);
                parametros.put("telefono", telefono);
                parametros.put("leyenda1", leyenda1);
                parametros.put("leyenda2", leyenda2);
                parametros.put("imprimeticket", String.valueOf(imprime_ticket));
                parametros.put("enviamensaje", String.valueOf(envia_mensaje));
                parametros.put("basedatos", bd);
                return parametros;
            }
        };


        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);

    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;

        // Creating List of Subject class.
        Configuracion configuracion;

        public ParseJSonDataClass(Context context) {

            this.context = context;

        }

        //@Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        //@Override
        protected Void doInBackground(Void... arg0) {

            try {

                // Checking whether FinalJSonObject is not equals to null.
                if (FinalJSonObject != null) {

                    // Creating and setting up JSON array as null.
                    JSONArray jsonArray = null;
                    try {

                        // Adding JSON response object into JSON array.
                        jsonArray = new JSONArray(FinalJSonObject);

                        // Creating JSON Object.
                        JSONObject jsonObject;



                        configuracion = new Configuracion();

                        jsonObject = jsonArray.getJSONObject(0);


                        //Storing ID into subject list.
                        configuracion.setId_configuracion(Integer.parseInt(jsonObject.getString("id_configuracion")));
                        configuracion.setNombre_empresa(jsonObject.getString("nombre_empresa"));
                        configuracion.setDireccion(jsonObject.getString("direccion"));
                        configuracion.setTelefono(jsonObject.getString("telefono"));
                        configuracion.setLeyenda1(jsonObject.getString("leyenda1"));
                        configuracion.setLeyenda2(jsonObject.getString("leyenda2"));
                        configuracion.setImprimeticket(Integer.parseInt(jsonObject.getString("imprimeticket")));
                        configuracion.setEnviamensaje(Integer.parseInt(jsonObject.getString("enviamensaje")));

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)

        {

            setValues(configuracion);
            pDialogo.dismiss();


        }
    }

    private class ParseJSonDataClassGuardar extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;

        // Creating List of Subject class.
        Configuracion config;

        public ParseJSonDataClassGuardar(View view) {
            this.view = view;
            this.context = view.getContext();
        }

        //@Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        //@Override
        protected Void doInBackground(Void... arg0) {

            try {

                // Checking whether FinalJSonObject is not equals to null.
                if (FinalJSonObject != null) {

                    // Creating and setting up JSON array as null.
                    JSONArray jsonArray = null,jsonArrayDatos = null;
                    JSONObject jsonObject,jsonObjectDatos;
                    try {

                        jsonObject = new JSONObject(FinalJSonObject);
                        boolean success = jsonObject.getBoolean("success");
                        msg = jsonObject.getString("msg");
                        jsonArray = jsonObject.getJSONArray("datos");

                        if(success){
                            config = new Configuracion();

                            jsonObjectDatos = jsonArray.getJSONObject(0);

                            //Storing ID into subject list.
                            config.setId_configuracion(Integer.parseInt(jsonObjectDatos.getString("id_configuracion")));


                        }


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)

        {
            super.onPostExecute(result);


            pDialogo.dismiss();
            setValuesSafe(config);
            SweetAlertDialog sDialogo = new SweetAlertDialog(NuevaConfiguracion.this);
            sDialogo.setTitleText(this.msg);
            sDialogo.show();
        }
    }

    private void setValues(Configuracion config){
        id_configuracion = config.getId_configuracion();
        this.txtNombreEmpresa.getEditText().setText(config.getNombre_empresa());
        this.txtDireccion.getEditText().setText(config.getDireccion());
        this.txtTelefono.getEditText().setText(config.getTelefono());
        this.txtLeyenda1.getEditText().setText(config.getLeyenda1());
        this.txtLeyenda2.getEditText().setText(config.getLeyenda2());

        if(config.getImprimeticket() == 1)
            this.tgbImprimeTicket.setChecked(true);
        else
            this.tgbImprimeTicket.setChecked(false);

        if(config.getEnviamensaje() == 1)
            this.tgbEnviaMensaje.setChecked(true);
        else
            this.tgbEnviaMensaje.setChecked(false);

        imprimeticket = config.getImprimeticket();
        enviamensaje = config.getEnviamensaje();

    }

    public void setValuesSafe(Configuracion configuracion) {

        id_configuracion = configuracion.getId_configuracion();

    }
}