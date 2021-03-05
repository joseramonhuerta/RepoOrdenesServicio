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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.eletronica.ordenesservicioapp.User;
import com.eletronica.ordenesservicioapp.GlobalVariables;
import com.eletronica.ordenesservicioapp.R;
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

public class NuevoUsuario extends AppCompatActivity {
    int id_usuario = 0;
    int status_usuario = 1;
    View mView;
    String HTTP_URL;
    String FinalJSonObject;

    StringRequest stringRequest;
    LinearLayout layStatus;
    TextInputLayout txtIDUsuario;
    TextInputLayout txtNombreUsuario;
    TextInputLayout txtUsuario;
    TextInputLayout txtPass;
    ToggleButton tgbStatus;
    Spinner spnTipoUsuario;
    ImageView btnLimpiarUsuario;
    Button btnGuardarUsuario;
    ImageView btnAtras;
    public int rolSelected=-1;
    Dialog dialogo = null;

    SweetAlertDialog pDialogo;

    HashMap<Integer,Integer> spinnerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usuario);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mView = (View) findViewById(R.id.viewNuevoUsuario);

        txtIDUsuario = (TextInputLayout) findViewById(R.id.txtIDUsuario);
        txtNombreUsuario = (TextInputLayout) findViewById(R.id.txtNombreUsuarioNuevo);
        txtUsuario = (TextInputLayout) findViewById(R.id.txtUsuarioNuevo);
        txtPass = (TextInputLayout) findViewById(R.id.txtPassNuevo);
        btnLimpiarUsuario = (ImageView) findViewById(R.id.btnLimpiarUsuario);
        btnGuardarUsuario = (Button) findViewById(R.id.btnGuardarUsuario);
        btnAtras = (ImageView) findViewById(R.id.btnAtrasNuevoUsuario);
        spnTipoUsuario = (Spinner) findViewById(R.id.spnTipoUsuario);
        tgbStatus = (ToggleButton) findViewById(R.id.tgbStatus);
        layStatus = (LinearLayout) findViewById(R.id.layStatus);

        //1=Administrador,2=Supervisor,3=Vendedor,4=Tecnico,5=Super
        String[] opciones = {"Administrador", "Supervisor", "Vendedor", "Tecnico"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        spnTipoUsuario.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();

        id_usuario = extras.getInt("ID");



        if(id_usuario > 0){
            loadUsuario(mView);
        }else{
            setValuesDefault();
        }


        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnGuardarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validarNombre() | !validarUsuario() | !validarPassword()){
                    return;
                }
                procesarGuardar(v);
            }
        });

        btnLimpiarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id_usuario > 0){
                    eliminarUsuario(v);
                }else{
                    limpiarCampos(v);
                }
            }
        });

        spnTipoUsuario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rolSelected = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tgbStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    status_usuario = 1;
                else
                    status_usuario = 0;
            }
        });

        validarStatus(id_usuario);
    }

    private Boolean validarNombre(){
        String val = txtNombreUsuario.getEditText().getText().toString();

        if(val.isEmpty()){
            txtNombreUsuario.setError("Introduzca el Nombre");
            return false;
        }else{
            txtNombreUsuario.setError(null);
            return true;
        }
    }

    private Boolean validarUsuario(){
        String val = txtUsuario.getEditText().getText().toString();
        String pattern = "[a-zA-Z0-9]$";

        if(val.isEmpty()){
            txtUsuario.setError("Introduzca el Usuario");
            return false;
        }else if(val.length() > 20){
            txtUsuario.setError("Tamaño no válido");
            return false;
        }else{
            txtUsuario.setError(null);
            return true;
        }
    }

    private Boolean validarPassword(){
        String val = txtPass.getEditText().getText().toString();
        String patternPassword = "[a-zA-Z0-9]{20}$";

        if(val.isEmpty()){
            txtPass.setError("Introduzca la Contraseña");
            return false;
        }else if(val.length() > 20){
            txtPass.setError("Tamaño no válido");
            return false;
        }else{
            txtPass.setError(null);
            return true;
        }
    }

    private void validarStatus(int id){
        if(id > 0){
            layStatus.setVisibility(View.VISIBLE);
        }else{
            layStatus.setVisibility(View.GONE);
        }
    }

    public void loadUsuario(View view){
        pDialogo = new SweetAlertDialog(NuevoUsuario.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Cargando, espere....");
        pDialogo.setCancelable(false);
        pDialogo.show();


        GlobalVariables variablesGlobales = new GlobalVariables();
        String bd = variablesGlobales.bd;

        HTTP_URL = variablesGlobales.URLServicio + "obtenerusuario.php?id_usuario="+id_usuario + "&basedatos=" + bd;
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new com.eletronica.ordenesservicioapp.NuevoUsuario.ParseJSonDataClass(mView.getContext()).execute();

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

    private void procesarGuardar(View view) {
        final View vista = view;
        pDialogo = new SweetAlertDialog(NuevoUsuario.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Guardando...");
        pDialogo.setCancelable(false);
        pDialogo.show();



        GlobalVariables variablesGlobales = new GlobalVariables();
        final String bd = variablesGlobales.bd;
        final int id_empresa = variablesGlobales.id_empresa;

        HTTP_URL =variablesGlobales.URLServicio + "guardarusuario.php?";
        // Creating StringRequest and set the JSON server URL in here.
        stringRequest = new StringRequest(Request.Method.POST, HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // After done Loading store JSON response in FinalJSonObject string variable.
                FinalJSonObject = response ;

                // Calling method to parse JSON object.
                new com.eletronica.ordenesservicioapp.NuevoUsuario.ParseJSonDataClassGuardar(vista).execute();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Showing error message if something goes wrong.
                final String error_volley = error.getMessage();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(NuevoUsuario.this,error_volley,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id_usr = String.valueOf(id_usuario);
                String nombre_usuario = txtNombreUsuario.getEditText().getText().toString();
                String usuario = txtUsuario.getEditText().getText().toString();
                String pass = txtPass.getEditText().getText().toString();
                String rol = String.valueOf(rolSelected + 1);

                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_usuario", id_usr);
                parametros.put("nombre_usuario", nombre_usuario);
                parametros.put("usuario", usuario);
                parametros.put("pass", pass);
                parametros.put("rol", rol);
                parametros.put("basedatos", bd);
                parametros.put("id_empresa", String.valueOf(id_empresa));
                parametros.put("status", String.valueOf(status_usuario));
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
        pDialogo = new SweetAlertDialog(NuevoUsuario.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Eliminando...");
        pDialogo.setCancelable(false);
        pDialogo.show();

        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String bd = variablesGlobales.bd;
            int id_empresa = variablesGlobales.id_empresa;
            HTTP_URL =variablesGlobales.URLServicio + "procesareliminarusuario.php?id_usuario=" + String.valueOf(id_usuario) + "&basedatos=" + bd + "&id_empresa=" + String.valueOf(id_empresa);
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response ;

                            // Calling method to parse JSON object.
                            new com.eletronica.ordenesservicioapp.NuevoUsuario.ParseJSonDataClassEliminar(vista).execute();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            final String error_volley =  error.getMessage();
                            // Showing error message if something goes wrong.
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(NuevoUsuario.this,error_volley,Toast.LENGTH_SHORT).show();
                                }
                            });

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
        User usuario;

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



                        usuario = new User();

                        jsonObject = jsonArray.getJSONObject(0);


                        //Storing ID into subject list.
                        usuario.setId_usuario(Integer.parseInt(jsonObject.getString("id_usuario")));
                        usuario.setNombre(jsonObject.getString("nombre_usuario"));
                        usuario.setUsuario(jsonObject.getString("usuario"));
                        usuario.setClave(jsonObject.getString("pass"));
                        usuario.setRol(Integer.parseInt(jsonObject.getString("rol")));
                        usuario.setRol_descripcion(jsonObject.getString("rol_descripcion"));
                        usuario.setStatus(Integer.parseInt(jsonObject.getString("status")));




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

            setValues(usuario);
            pDialogo.dismiss();


        }
    }

    private class ParseJSonDataClassGuardar extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;

        // Creating List of Subject class.
        User usuario;

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
                            usuario = new User();

                            jsonObjectDatos = jsonArray.getJSONObject(0);


                            //Storing ID into subject list.
                            usuario.setId_usuario(Integer.parseInt(jsonObjectDatos.getString("id_usuario")));


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
            setValuesSafe(usuario);
            SweetAlertDialog sDialogo = new SweetAlertDialog(NuevoUsuario.this);
            sDialogo.setTitleText(this.msg);
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

            SweetAlertDialog sDialogo = new SweetAlertDialog(NuevoUsuario.this);
            sDialogo.setTitleText(this.msg);
            sDialogo.show();

        }
    }

    public void eliminarUsuario(View v){

        SweetAlertDialog sDialog = new SweetAlertDialog(NuevoUsuario.this, SweetAlertDialog.WARNING_TYPE);
        sDialog.setTitleText("Desea eliminar el usuario?");
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

    public void setValues(User usuario) {
        id_usuario = usuario.getId_usuario();
        this.txtIDUsuario.getEditText().setText(String.valueOf(id_usuario));
        this.txtNombreUsuario.getEditText().setText(usuario.getNombre());
        this.txtUsuario.getEditText().setText(usuario.getUsuario());
        this.txtPass.getEditText().setText(usuario.getClave());
        this.spnTipoUsuario.setSelection(usuario.getRol() - 1);
        if(usuario.getStatus() == 1)
            this.tgbStatus.setChecked(true);
        else
            this.tgbStatus.setChecked(false);

        status_usuario = usuario.getStatus();
        rolSelected = usuario.getRol() - 1;

        setIconoLimpiar();

        validarStatus(id_usuario);
    }

    public void setValuesDefault(){


        txtIDUsuario.getEditText().setText(String.valueOf(id_usuario));
        status_usuario = 1;
        tgbStatus.setChecked(true);

        validarStatus(id_usuario);


    }

    public void setValuesSafe(User usuario) {
        if(usuario instanceof User){
            id_usuario = usuario.getId_usuario();
            this.txtIDUsuario.getEditText().setText(String.valueOf(id_usuario));

            setIconoLimpiar();

        }

        validarStatus(id_usuario);

    }

    public void limpiarCampos(View v){
        id_usuario = 0;
        this.txtNombreUsuario.getEditText().setText("");
        this.txtUsuario.getEditText().setText("");
        this.txtPass.getEditText().setText("");
        this.spnTipoUsuario.setSelection(0);
        txtIDUsuario.getEditText().setText(String.valueOf(id_usuario));
        rolSelected = 0;
        setIconoLimpiar();
        status_usuario = 0;

        validarStatus(id_usuario);

    }

    public void setIconoLimpiar()
    {
        if(id_usuario > 0){
            btnLimpiarUsuario.setImageResource(R.drawable.icono_delete);

        }else{
            btnLimpiarUsuario.setImageResource(R.drawable.paint_brush);
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
