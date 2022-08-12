package com.eletronica.ordenesservicioapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NuevoPuntoVenta extends AppCompatActivity {
    int id_puntodeventa = 0;

    String HTTP_URL;
    String FinalJSonObject;
    StringRequest stringRequest;

    View mView;
    TextInputLayout txtIDPuntoVenta;
    TextInputLayout txtDescripcionPuntoVenta;
    ImageView btnLimpiarPuntoVenta;
    Button btnGuardarPuntoVenta;
    ImageView btnAtrasPuntoVenta;

    SweetAlertDialog pDialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_punto_venta);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mView = (View) findViewById(R.id.viewNuevoPuntoVenta);

        txtIDPuntoVenta = (TextInputLayout) findViewById(R.id.txtIDPuntoVenta);
        txtDescripcionPuntoVenta = (TextInputLayout) findViewById(R.id.txtDescripcionPuntoVentaNuevo);
        btnLimpiarPuntoVenta = (ImageView) findViewById(R.id.btnLimpiarPuntoVenta);
        btnGuardarPuntoVenta = (Button) findViewById(R.id.btnGuardarPuntoVenta);
        btnAtrasPuntoVenta = (ImageView) findViewById(R.id.btnAtrasNuevoPuntoVenta);

        Bundle extras = getIntent().getExtras();

        id_puntodeventa = extras.getInt("ID");

        if(id_puntodeventa > 0){
            loadPuntoVenta(mView);
        }else{
            setValuesDefault();
        }


        btnAtrasPuntoVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnGuardarPuntoVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validarNombre()){
                    return;
                }

                procesarGuardar(v);
            }
        });

        btnLimpiarPuntoVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id_puntodeventa > 0){
                    eliminarPuntoVenta(v);
                }else{
                    limpiarCampos(v);
                }
            }
        });
    }


    private boolean validarNombre(){
        boolean valido = false;

        String val = txtDescripcionPuntoVenta.getEditText().getText().toString();

        if(val.isEmpty()){
            txtDescripcionPuntoVenta.setError("Introduzca la Descripción");
            valido = false;
        }else{
            txtDescripcionPuntoVenta.setError(null);
            valido = true;
        }

        return valido;
    }

    public void loadPuntoVenta(View view){
        pDialogo = new SweetAlertDialog(NuevoPuntoVenta.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Cargando, espere....");
        pDialogo.setCancelable(false);
        pDialogo.show();


        GlobalVariables variablesGlobales = new GlobalVariables();
        String bd = variablesGlobales.bd;

        HTTP_URL = variablesGlobales.URLServicio + "obtenerpuntoventa.php?id_puntodeventa="+id_puntodeventa + "&basedatos=" + bd;
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new NuevoPuntoVenta.ParseJSonDataClass(mView.getContext()).execute();

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
        pDialogo = new SweetAlertDialog(NuevoPuntoVenta.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Guardando...");
        pDialogo.setCancelable(false);
        pDialogo.show();



        GlobalVariables variablesGlobales = new GlobalVariables();
        final String bd = variablesGlobales.bd;
        final int id_empresa = variablesGlobales.id_empresa;

        HTTP_URL =variablesGlobales.URLServicio + "guardarpuntoventa.php?";
        // Creating StringRequest and set the JSON server URL in here.
        stringRequest = new StringRequest(Request.Method.POST, HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // After done Loading store JSON response in FinalJSonObject string variable.
                FinalJSonObject = response ;

                // Calling method to parse JSON object.
                new NuevoPuntoVenta.ParseJSonDataClassGuardar(vista).execute();
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
                String id_pvta = String.valueOf(id_puntodeventa);
                String descrip = txtDescripcionPuntoVenta.getEditText().getText().toString();

                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_puntodeventa", id_pvta);
                parametros.put("descripcion_puntoventa", descrip);
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
        pDialogo = new SweetAlertDialog(NuevoPuntoVenta.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Eliminando...");
        pDialogo.setCancelable(false);
        pDialogo.show();

        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String bd = variablesGlobales.bd;

            HTTP_URL =variablesGlobales.URLServicio + "procesareliminarpuntoventa.php?id_puntodeventa=" + String.valueOf(id_puntodeventa) + "&basedatos=" + bd;
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response ;

                            // Calling method to parse JSON object.
                            new NuevoPuntoVenta.ParseJSonDataClassEliminar(vista).execute();

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
        PuntoVenta puntoVenta;

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



                        puntoVenta = new PuntoVenta();

                        jsonObject = jsonArray.getJSONObject(0);


                        //Storing ID into subject list.
                        puntoVenta.id_puntodeventa = Integer.parseInt(jsonObject.getString("id_puntodeventa"));
                        puntoVenta.descripcion_puntodeventa = jsonObject.getString("descripcion_puntodeventa");

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

            setValues(puntoVenta);
            pDialogo.dismiss();


        }
    }

    private class ParseJSonDataClassGuardar extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;

        // Creating List of Subject class.
        PuntoVenta puntoVenta;

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
                            puntoVenta = new PuntoVenta();
                            jsonObjectDatos = jsonArray.getJSONObject(0);

                            //Storing ID into subject list.
                            puntoVenta.id_puntodeventa = Integer.parseInt(jsonObjectDatos.getString("id_puntodeventa"));



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
            setValuesSafe(puntoVenta);
            SweetAlertDialog sDialogo = new SweetAlertDialog(NuevoPuntoVenta.this);
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
            SweetAlertDialog sDialogo = new SweetAlertDialog(NuevoPuntoVenta.this);
            sDialogo.setTitleText(this.msg);
            sDialogo.show();

        }
    }

    public void eliminarPuntoVenta(View v){
        SweetAlertDialog sDialog = new SweetAlertDialog(NuevoPuntoVenta.this, SweetAlertDialog.WARNING_TYPE);
        sDialog.setTitleText("Desea eliminar el punto de venta?");
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

    public void setValues(PuntoVenta puntoVenta) {
        id_puntodeventa = puntoVenta.id_puntodeventa;
        this.txtIDPuntoVenta.getEditText().setText(String.valueOf(id_puntodeventa));
        this.txtDescripcionPuntoVenta.getEditText().setText(puntoVenta.getDescripcion_puntodeventa());

        setIconoLimpiar();
    }

    public void setValuesDefault(){


        txtIDPuntoVenta.getEditText().setText(String.valueOf(id_puntodeventa));


    }

    public void setValuesSafe(PuntoVenta puntoVenta) {
        if(puntoVenta instanceof PuntoVenta){
            id_puntodeventa = puntoVenta.id_puntodeventa;
            this.txtIDPuntoVenta.getEditText().setText(String.valueOf(id_puntodeventa));

            setIconoLimpiar();

        }


    }

    public void limpiarCampos(View v){
        id_puntodeventa = 0;
        this.txtDescripcionPuntoVenta.getEditText().setText("");

        txtIDPuntoVenta.getEditText().setText(String.valueOf(id_puntodeventa));

        setIconoLimpiar();

    }

    public void setIconoLimpiar()
    {
        if(id_puntodeventa > 0){
            btnLimpiarPuntoVenta.setImageResource(R.drawable.icono_delete);

        }else{
            btnLimpiarPuntoVenta.setImageResource(R.drawable.paint_brush);
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