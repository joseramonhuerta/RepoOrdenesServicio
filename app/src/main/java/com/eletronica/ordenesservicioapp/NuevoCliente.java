package com.eletronica.ordenesservicioapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NuevoCliente extends AppCompatActivity {
    int id_cliente = 0;

    View mView;

    String HTTP_URL;
    String FinalJSonObject;
    StringRequest stringRequest;

    TextInputLayout txtIDCliente;
    TextInputLayout txtNombreCliente;
    TextInputLayout txtCelular;
    TextInputLayout txtDireccion;
    ImageView btnLimpiarCliente;
    Button btnGuardarCliente;
    ImageView btnAtras;

    Dialog dialogo = null;

    SweetAlertDialog pDialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_cliente);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mView = (View) findViewById(R.id.viewNuevoCliente);

        txtIDCliente = (TextInputLayout) findViewById(R.id.txtIDCliente);
        txtNombreCliente = (TextInputLayout) findViewById(R.id.txtNombreClienteNuevo);
        txtCelular = (TextInputLayout) findViewById(R.id.txtCelularNuevo);
        txtDireccion = (TextInputLayout) findViewById(R.id.txtDireccion);
        btnLimpiarCliente = (ImageView) findViewById(R.id.btnLimpiarCliente);
        btnGuardarCliente = (Button) findViewById(R.id.btnGuardarCliente);
        btnAtras = (ImageView) findViewById(R.id.btnAtrasNuevoCliente);

        Bundle extras = getIntent().getExtras();

        id_cliente = extras.getInt("ID");

        if(id_cliente > 0){
            loadCliente(mView);
        }else{
            setValuesDefault();
        }


        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnGuardarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validarNombre() | !validarCelular()){
                    return;
                }

                procesarGuardar(v);
            }
        });

        btnLimpiarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id_cliente > 0){
                    eliminarCliente(v);
                }else{
                    limpiarCampos(v);
                }
            }
        });


    }

    private boolean validarNombre(){
        boolean valido = false;

        String val = txtNombreCliente.getEditText().getText().toString();

        if(val.isEmpty()){
            txtNombreCliente.setError("Introduzca el Nombre");
            valido = false;
        }else{
            txtNombreCliente.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validarCelular(){
        boolean valido = false;

        String val = txtCelular.getEditText().getText().toString();

        if(val.isEmpty()){
            txtCelular.setError("Introduzca el Celular");
            valido = false;
        }else{
            txtCelular.setError(null);
            valido = true;
        }

        return valido;
    }

    public void loadCliente(View view){
        pDialogo = new SweetAlertDialog(NuevoCliente.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Cargando, espere....");
        pDialogo.setCancelable(false);
        pDialogo.show();


        GlobalVariables variablesGlobales = new GlobalVariables();
        String bd = variablesGlobales.bd;

        HTTP_URL = variablesGlobales.URLServicio + "obtenercliente.php?id_cliente="+id_cliente + "&basedatos=" + bd;
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new NuevoCliente.ParseJSonDataClass(mView.getContext()).execute();

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

    private void procesarGuardar(View view) {
        final View vista = view;
        //activarDesactivarBotones(btnGuardarCliente, 2);
        pDialogo = new SweetAlertDialog(NuevoCliente.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Guardando...");
        pDialogo.setCancelable(false);
        pDialogo.show();



        GlobalVariables variablesGlobales = new GlobalVariables();
        final String bd = variablesGlobales.bd;
        final int id_empresa = variablesGlobales.id_empresa;

        HTTP_URL =variablesGlobales.URLServicio + "guardarcliente.php?";
        // Creating StringRequest and set the JSON server URL in here.
        stringRequest = new StringRequest(Request.Method.POST, HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // After done Loading store JSON response in FinalJSonObject string variable.
                FinalJSonObject = response ;

                // Calling method to parse JSON object.
                new NuevoCliente.ParseJSonDataClassGuardar(vista).execute();
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
                String id_cte = String.valueOf(id_cliente);
                String nombre_cliente = txtNombreCliente.getEditText().getText().toString();
                String celular = txtCelular.getEditText().getText().toString();
                String direccion = txtDireccion.getEditText().getText().toString();

                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_cliente", id_cte);
                parametros.put("nombre_cliente", nombre_cliente);
                parametros.put("celular", celular);
                parametros.put("direccion", direccion);
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

    private void procesarEliminar(View view)    {
        final View vista = view;
        //activarDesactivarBotones(btnLimpiarCliente, 2);
        pDialogo = new SweetAlertDialog(NuevoCliente.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Eliminando...");
        pDialogo.setCancelable(false);
        pDialogo.show();

        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String bd = variablesGlobales.bd;

            HTTP_URL =variablesGlobales.URLServicio + "procesareliminarcliente.php?id_cliente=" + String.valueOf(id_cliente) + "&basedatos=" + bd;
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response ;

                            // Calling method to parse JSON object.
                            new NuevoCliente.ParseJSonDataClassEliminar(vista).execute();

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

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;

        // Creating List of Subject class.
        Cliente cliente;

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



                        cliente = new Cliente();

                        jsonObject = jsonArray.getJSONObject(0);


                        //Storing ID into subject list.
                        cliente.id_cliente = Integer.parseInt(jsonObject.getString("id_cliente"));
                        cliente.nombre_cliente = jsonObject.getString("nombre_cliente");
                        cliente.celular = jsonObject.getString("celular");
                        cliente.direccion = jsonObject.getString("direccion");




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

            setValues(cliente);
            pDialogo.dismiss();


        }
    }

    private class ParseJSonDataClassGuardar extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;

        // Creating List of Subject class.
        Cliente cliente;

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
                        this.msg = jsonObject.getString("msg");
                        jsonArray = jsonObject.getJSONArray("datos");

                        if(success){
                            cliente = new Cliente();
                            jsonObjectDatos = jsonArray.getJSONObject(0);

                            //Storing ID into subject list.
                            cliente.id_cliente = Integer.parseInt(jsonObjectDatos.getString("id_cliente"));



                        }

                        /*runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(NuevoCliente.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        });*/



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
            setValuesSafe(cliente);
            SweetAlertDialog sDialogo = new SweetAlertDialog(NuevoCliente.this);
            sDialogo.setTitleText(this.msg);
            sDialogo.show();
            //activarDesactivarBotones(btnGuardarCliente, 1);
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
                        this.msg = jsonObject.getString("msg");
                        jsonArray = jsonObject.getJSONArray("datos");

                        /*
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(NuevoCliente.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        });*/


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
            //activarDesactivarBotones(btnLimpiarCliente, 1);
            SweetAlertDialog sDialogo = new SweetAlertDialog(NuevoCliente.this);
            sDialogo.setTitleText(this.msg);
            sDialogo.show();

        }
    }

    public void eliminarCliente(View v){
        SweetAlertDialog sDialog = new SweetAlertDialog(NuevoCliente.this, SweetAlertDialog.WARNING_TYPE);
        sDialog.setTitleText("Desea eliminar el cliente?");
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

    public void setValues(Cliente cliente) {
        id_cliente = cliente.id_cliente;
        this.txtIDCliente.getEditText().setText(String.valueOf(id_cliente));
        this.txtNombreCliente.getEditText().setText(cliente.nombre_cliente);
        this.txtCelular.getEditText().setText(cliente.celular);
        this.txtDireccion.getEditText().setText(cliente.direccion);


        setIconoLimpiar();
    }

    public void setValuesDefault(){


        txtIDCliente.getEditText().setText(String.valueOf(id_cliente));


    }

    public void setValuesSafe(Cliente cliente) {
        if(cliente instanceof Cliente){
            id_cliente = cliente.id_cliente;
            this.txtIDCliente.getEditText().setText(String.valueOf(id_cliente));

            setIconoLimpiar();

        }


    }

    public void limpiarCampos(View v){
        id_cliente = 0;
        this.txtNombreCliente.getEditText().setText("");
        this.txtCelular.getEditText().setText("");
        this.txtDireccion.getEditText().setText("");

        txtIDCliente.getEditText().setText(String.valueOf(id_cliente));

        setIconoLimpiar();

    }

    public void setIconoLimpiar()
    {
        if(id_cliente > 0){
            btnLimpiarCliente.setImageResource(R.drawable.icono_delete);

        }else{
            btnLimpiarCliente.setImageResource(R.drawable.paint_brush);
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
}
