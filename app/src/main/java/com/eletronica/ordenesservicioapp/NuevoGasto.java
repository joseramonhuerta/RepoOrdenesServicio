package com.eletronica.ordenesservicioapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.eletronica.ordenesservicioapp.R;
import com.google.android.material.textfield.TextInputLayout;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NuevoGasto extends AppCompatActivity {

    int id_gasto = 0;
    int id_orden_servicio = 0;
    View mView;
    String HTTP_URL;
    String FinalJSonObject;
    String nombre_cliente_orden;
    Double importe_orden;
    String nombre_equipo;
    
    StringRequest stringRequest;

    TextInputLayout txtFolio;
    TextInputLayout txtConcepto;
    TextInputLayout txtFecha;
    TextInputLayout txtImporte;
    TextInputLayout txtNumOrdenServicio;
    
    LinearLayout groupGenerales;
    LinearLayout layOrdenes;
    
    ImageView btnAtras;
    ImageView btnBuscarOrden;

    TextView txtNombreClienteOrden;

    Button btnGuardar;
    ImageView btnLimpiar;
    
    Spinner spnTipo;
    
    public int tipoSelected=-1;
    
    Dialog dialogo = null;

    SweetAlertDialog pDialogo;
    
    String[] spinnerArray;
    HashMap<Integer,Integer> spinnerMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_gasto);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mView = (View) findViewById(R.id.viewNuevoGasto);

        txtFolio = (TextInputLayout) findViewById(R.id.txtFolioGasto);
        txtFecha = (TextInputLayout) findViewById(R.id.txtFechaNuevoGasto);
        txtConcepto = (TextInputLayout) findViewById(R.id.txtConceptoNuevoGasto);
        txtImporte = (TextInputLayout) findViewById(R.id.txtImporteNuevoGasto);
        txtNumOrdenServicio = (TextInputLayout) findViewById(R.id.txtNumOrdenServicio);

        groupGenerales = (LinearLayout) findViewById(R.id.groupGeneralesGasto);
        
        btnGuardar = (Button) findViewById(R.id.btnGuardarGasto);
        btnLimpiar = (ImageView) findViewById(R.id.btnLimpiarGasto);
        btnAtras = (ImageView) findViewById(R.id.btnAtrasNuevoGasto);
        btnBuscarOrden = (ImageView) findViewById(R.id.btnBuscarOrden);
        txtNombreClienteOrden = (TextView) findViewById(R.id.txtNombreClienteOrden);
        
        spnTipo = (Spinner) findViewById(R.id.spnTipo);

        layOrdenes = (LinearLayout) findViewById(R.id.layOrdenServicio);

        String[] opciones = {"Ingreso", "Egreso"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        spnTipo.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();

        id_gasto = extras.getInt("ID");
        

        if(id_gasto > 0){
            loadGasto(mView);
            setIconoLimpiar();

        }else{
            setValuesDefault();
        }

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        txtFecha.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();

            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validarConcepto() | !validarImporte() | !validarOrdenServicio()){
                    return;
                }
                
                procesarGuardar(v);
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id_gasto > 0){
                    eliminarGasto(v);
                }else{
                    limpiarCampos(v);
                }


                //Toast.makeText(v.getContext(),"Limpiando campos",Toast.LENGTH_LONG).show();

            }
        });

        btnBuscarOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                procesarBuscarOrden(v);

                //Toast.makeText(v.getContext(),"Limpiando campos",Toast.LENGTH_LONG).show();

            }
        });

        spnTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoSelected = position;

                validarSeleccionTipo(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    private void validarSeleccionTipo(int position){
        if(position == 0){
           layOrdenes.setVisibility(View.VISIBLE);
        }else{
            layOrdenes.setVisibility(View.GONE);
        }
    }


    private boolean validarConcepto(){
        boolean valido = false;

        String val = txtConcepto.getEditText().getText().toString();

        if(val.isEmpty()){
            txtConcepto.setError("Introduzca el concepto");
            valido = false;
        }else{
            txtConcepto.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validarImporte(){
        boolean valido = false;

        String val = txtImporte.getEditText().getText().toString();

        if(val.isEmpty()){
            txtImporte.setError("Introduzca el Importe");
            valido = false;
        }else{
            txtImporte.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validarOrdenServicio(){
        boolean valido = false;

        String val = txtNumOrdenServicio.getEditText().getText().toString();

        if(tipoSelected == 0) {
            if (val.isEmpty()) {
                txtNumOrdenServicio.setError("Introduzca la Orden de Servicio");
                valido = false;
            } else if (this.id_orden_servicio == 0) {
                txtNumOrdenServicio.setError("Seleccione una Orden de Servicio");
                valido = false;
            } else {
                txtNumOrdenServicio.setError(null);
                valido = true;
            }
        }else{
            valido = true;
        }
        return valido;
    }


    public void setIconoLimpiar()
    {
        if(id_gasto > 0){
            btnLimpiar.setImageResource(R.drawable.icono_delete);

        }else{
            btnLimpiar.setImageResource(R.drawable.paint_brush);
        }
    }

    public void activarDesactivarBotones(ImageButton boton, int opt){
        switch(opt) {
            case 1 :
                boton.setEnabled(true);
                break;
            case 2 :
                boton.setEnabled(false);
                break;
        }
    }

    private void procesarGuardar(View view) {
        final View vista = view;
        pDialogo = new SweetAlertDialog(NuevoGasto.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Guardando...");
        pDialogo.setCancelable(false);
        pDialogo.show();



        GlobalVariables variablesGlobales = new GlobalVariables();
        final String bd = variablesGlobales.bd;
        final int id_empresa = variablesGlobales.id_empresa;

        HTTP_URL =variablesGlobales.URLServicio + "guardargasto.php?";
        // Creating StringRequest and set the JSON server URL in here.
        stringRequest = new StringRequest(Request.Method.POST, HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // After done Loading store JSON response in FinalJSonObject string variable.
                FinalJSonObject = response ;

                // Calling method to parse JSON object.
                new NuevoGasto.ParseJSonDataClassGuardar(vista).execute();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Showing error message if something goes wrong.
                Toast.makeText(mView.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String parsedDate = "";


                String newFecha = txtFecha.getEditText().getText().toString();

                try {
                    Date initDate = new SimpleDateFormat("dd/mm/yyyy").parse(newFecha);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
                    parsedDate = formatter.format(initDate).toString();
                } catch (ParseException ex) {

                }

                GlobalVariables variablesGlobales = new GlobalVariables();
                int id_user = variablesGlobales.id_usuario;

                String id = String.valueOf(id_gasto);
                String id_usuario = String.valueOf(id_user);
                String id_orden = String.valueOf(id_orden_servicio);
                String concepto = txtConcepto.getEditText().getText().toString();
                String fecha = parsedDate;


                String importe = "0";
                if(!txtImporte.getEditText().getText().toString().isEmpty())
                    importe = txtImporte.getEditText().getText().toString();

                String tipo = String.valueOf(tipoSelected + 1);


                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_gasto", id);
                parametros.put("id_usuario", id_usuario);
                parametros.put("id_orden_servicio", id_orden);
                parametros.put("concepto", concepto);
                parametros.put("fecha", fecha);
                parametros.put("importe", importe);
                parametros.put("tipo", tipo);
                parametros.put("basedatos", bd);
                parametros.put("id_empresa", String.valueOf(id_empresa));

                return parametros;
            }
        };


        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);


    }

    private void procesarEliminar(View view) {
        final View vista = view;
        pDialogo = new SweetAlertDialog(NuevoGasto.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Eliminando...");
        pDialogo.setCancelable(false);
        pDialogo.show();

        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String bd = variablesGlobales.bd;

            HTTP_URL =variablesGlobales.URLServicio + "procesareliminargasto.php?id_gasto=" + String.valueOf(id_gasto) + "&basedatos=" + bd;
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response ;

                            // Calling method to parse JSON object.
                            new NuevoGasto.ParseJSonDataClassEliminar(vista).execute();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            // Showing error message if something goes wrong.
                            Toast.makeText(mView.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    });

            // Creating String Request Object.
            RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

            // Passing String request into RequestQueue.
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void procesarBuscarOrden(View view) {
        id_orden_servicio = 0;
        txtNombreClienteOrden.setText("");
        final View vista = view;
        pDialogo = new SweetAlertDialog(NuevoGasto.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Buscando Orden de Servicio...");
        pDialogo.setCancelable(false);
        pDialogo.show();

        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String bd = variablesGlobales.bd;
            String id_orden = txtNumOrdenServicio.getEditText().getText().toString();

            HTTP_URL =variablesGlobales.URLServicio + "buscarordengastos.php?id_orden=" + id_orden + "&basedatos=" + bd;
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response ;

                            // Calling method to parse JSON object.
                            new NuevoGasto.ParseJSonDataClassBuscarOrden(vista).execute();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            // Showing error message if something goes wrong.
                            Toast.makeText(mView.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    });

            // Creating String Request Object.
            RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

            // Passing String request into RequestQueue.
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ParseJSonDataClassGuardar extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;

        // Creating List of Subject class.
        Gasto gasto;

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
                            gasto = new Gasto();

                            jsonObjectDatos = jsonArray.getJSONObject(0);


                            //Storing ID into subject list.
                            gasto.setId_gasto(Integer.parseInt(jsonObjectDatos.getString("id_gasto")));



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
            setValuesSafe(gasto);
            SweetAlertDialog sDialogo = new SweetAlertDialog(NuevoGasto.this);
            sDialogo.setTitleText(msg);
            sDialogo.show();




        }
    }

    private class ParseJSonDataClassEliminar extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;
        // Creating List of Subject class.


        public ParseJSonDataClassEliminar(View view) {
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

            limpiarCampos(view);
            pDialogo.dismiss();
            SweetAlertDialog sDialogo = new SweetAlertDialog(NuevoGasto.this);
            sDialogo.setTitleText(this.msg);
            sDialogo.show();

        }
    }

    private class ParseJSonDataClassBuscarOrden extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;
        // Creating List of Subject class.


        public ParseJSonDataClassBuscarOrden(View view) {
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
                            jsonObjectDatos = jsonArray.getJSONObject(0);
                            id_orden_servicio = Integer.parseInt(jsonObjectDatos.getString("id_orden_servicio"));
                            nombre_cliente_orden = jsonObjectDatos.getString("nombre_cliente");
                            importe_orden = Double.parseDouble(jsonObjectDatos.getString("importe_presupuesto"));
                            nombre_equipo = jsonObjectDatos.getString("nombre_equipo");

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

            //limpiarCampos(view);
            pDialogo.dismiss();

            if(id_orden_servicio > 0){
                txtNombreClienteOrden.setText(nombre_cliente_orden);
                txtImporte.getEditText().setText(String.valueOf(importe_orden));
                txtConcepto.getEditText().setText(nombre_equipo);
            }else{
                SweetAlertDialog sDialogo = new SweetAlertDialog(NuevoGasto.this);
                sDialogo.setTitleText(this.msg);
                sDialogo.show();
            }




        }
    }


    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
                txtFecha.getEditText().setText(selectedDate);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    public void setValues(Gasto gasto) {
        id_gasto = gasto.getId_gasto();
        id_orden_servicio = gasto.getId_orden_servicio();

        this.txtFolio.getEditText().setText(String.valueOf(id_gasto));
        this.txtConcepto.getEditText().setText(gasto.getConcepto());
        this.txtFecha.getEditText().setText(gasto.getFecha());
        this.txtImporte.getEditText().setText(String.valueOf(gasto.getImporte()));
        this.txtNumOrdenServicio.getEditText().setText(String.valueOf(gasto.getId_orden_servicio()));
        this.txtNombreClienteOrden.setText(gasto.getOrden_servicio_descripcion());
        spnTipo.setSelection(gasto.getTipo() - 1);


        tipoSelected = gasto.getTipo() - 1;

        validarSeleccionTipo(tipoSelected);
        setIconoLimpiar();
    }


    public void setValuesSafe(Gasto gasto) {
        id_gasto = gasto.getId_gasto();
        //id_cliente = orden.id_cliente;
        this.txtFolio.getEditText().setText(String.valueOf(id_gasto));

        setIconoLimpiar();

        validarSeleccionTipo(tipoSelected);

    }

    public void limpiarCampos(View v){
        id_gasto = 0;

        this.txtConcepto.getEditText().setText("");
        this.txtImporte.getEditText().setText("");

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
        txtFecha.getEditText().setText(selectedDate);

        txtFolio.getEditText().setText(String.valueOf(id_gasto));

        spnTipo.setSelection(0);
        tipoSelected = 0;

        validarSeleccionTipo(tipoSelected);

        setIconoLimpiar();

        id_orden_servicio = 0;
        txtNombreClienteOrden.setText("");

        nombre_cliente_orden = "";
        nombre_equipo = "";

    }

    public void eliminarGasto(View v){
        SweetAlertDialog sDialog = new SweetAlertDialog(NuevoGasto.this, SweetAlertDialog.WARNING_TYPE);
        sDialog.setTitleText("Desea eliminar el gasto?");
        sDialog.setContentText("No se podra recuperar si se elimina");
        sDialog.setConfirmText("SI");
        sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                procesarEliminar(mView);
            }
        })
                .setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                });
        sDialog.show();

    }

    public void setValuesDefault(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
        txtFecha.getEditText().setText(selectedDate);

        txtFolio.getEditText().setText(String.valueOf(id_gasto));

        spnTipo.setSelection(0);

        tipoSelected = 0;

        id_orden_servicio = 0;
        txtNombreClienteOrden.setText("");

        validarSeleccionTipo(tipoSelected);



    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;

        // Creating List of Subject class.
        Gasto gasto;

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



                        gasto = new Gasto();

                        jsonObject = jsonArray.getJSONObject(0);


                        //Storing ID into subject list.
                        gasto.setId_gasto(Integer.parseInt(jsonObject.getString("id_gasto")));

                        gasto.setConcepto(jsonObject.getString("concepto"));

                        gasto.setFecha(jsonObject.getString("fecha"));
                        gasto.setTipo(Integer.parseInt(jsonObject.getString("tipo")));
                        gasto.setTipo_descripcion(jsonObject.getString("tipo_descripcion"));
                        gasto.setImporte(Double.parseDouble(jsonObject.getString("importe")));
                        gasto.setId_orden_servicio(Integer.parseInt(jsonObject.getString("id_orden_servicio")));
                        gasto.setOrden_servicio_descripcion(jsonObject.getString("nombre_cliente"));



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

            setValues(gasto);
            pDialogo.dismiss();


        }
    }


    public void loadGasto(View view){


        pDialogo = new SweetAlertDialog(NuevoGasto.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Cargando, espere....");
        pDialogo.setCancelable(false);
        pDialogo.show();


        GlobalVariables variablesGlobales = new GlobalVariables();
        String bd = variablesGlobales.bd;

        HTTP_URL = variablesGlobales.URLServicio + "obtenergasto.php?id_gasto="+id_gasto + "&basedatos=" + bd;
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new NuevoGasto.ParseJSonDataClass(mView.getContext()).execute();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Showing error message if something goes wrong.
                        Toast.makeText(mView.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });

        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(mView.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);
    }

}
