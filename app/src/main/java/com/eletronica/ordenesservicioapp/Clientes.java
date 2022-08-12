package com.eletronica.ordenesservicioapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Clientes extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ListView listView;
    static View mView;
    androidx.appcompat.widget.Toolbar myToolbar;
    EditText txtSearch;
    // Server Http URL
    String HTTP_URL;

    ImageView btnNueva;
    ImageView btnAtras;
    ImageView btnClear;
    SwipeRefreshLayout swipeContainer;

    String FinalJSonObject ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        myToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarCatalogoClientes);
        setSupportActionBar(myToolbar);

        txtSearch = (EditText) findViewById(R.id.txtSearchCatalogoClientes);

        //txtSearch.addTextChangedListener(searchTextWatcher);

        listView = (ListView) findViewById(R.id.listViewCatalogoClientes);

        // btnActualizarEntregar = (Button) view.findViewById(R.id.btnActualizarEntregar);

        mView = (View) findViewById(R.id.viewCatalogoClientes);

        btnNueva = (ImageView) findViewById(R.id.btnNuevoCatalogoClientes);
        btnAtras = (ImageView) findViewById(R.id.btnAtrasCatalogoClientes);
        btnClear = (ImageView) findViewById(R.id.btnClearSearchCatalogoClientes);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.srlContainerClientes);

        btnNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(getApplicationContext(), NuevoCliente.class);
                intencion.putExtra("ID", 0);
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);

            }
        });

        swipeContainer.setOnRefreshListener(this);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearch.setText("");

            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Cliente cte = (Cliente) listView.getItemAtPosition(position);

                Intent intencion = new Intent(getApplicationContext(), NuevoCliente.class);
                intencion.putExtra("ID", cte.getId_cliente());
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);
            }
        });

        GlobalVariables vg = new GlobalVariables();

        loadClientes(mView);

    }

    @Override
    public void onRefresh() {
       loadClientes(mView);
    }


    @Override
    public void onResume(){
        super.onResume();
        loadClientes(mView);
    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public Fragment fragment;
        // Creating List of Subject class.
        List<Cliente> clientesList;

        public ParseJSonDataClass(View view) {
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
                    JSONArray jsonArray = null;
                    try {

                        // Adding JSON response object into JSON array.
                        jsonArray = new JSONArray(FinalJSonObject);

                        // Creating JSON Object.
                        JSONObject jsonObject;

                        // Creating Subject class object.
                        Cliente cliente;

                        // Defining CustomSubjectNamesList AS Array List.
                        clientesList = new ArrayList<Cliente>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            cliente = new Cliente();

                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.
                            cliente.setId_cliente(Integer.parseInt(jsonObject.getString("id_cliente")));
                            cliente.setNombre_cliente(jsonObject.getString("nombre_cliente"));
                            cliente.setCelular(jsonObject.getString("celular"));
                            cliente.setDireccion(jsonObject.getString("direccion"));


                            // Adding subject list object into CustomSubjectNamesList.
                            clientesList.add(cliente);
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
            // After all done loading set complete CustomSubjectNamesList with application context to ListView adapter.

            //Fragment ff = Entregar.this;
            //fm.beginTransaction().set
            final ListViewAdapterCatalogoClientes adapter = new ListViewAdapterCatalogoClientes(clientesList, context);

            // Setting up all data into ListView.
            listView.setAdapter(adapter);
            swipeContainer.setRefreshing(false);
            txtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //listView.getAdapter().getFilter().filter(s.toString());


                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // ignore
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //listView.getAdapter().
                    String text = txtSearch.getText().toString().toLowerCase(Locale.getDefault());
                    adapter.filter(text);
                    // listView.getAdapter().getFilter().filter(s.toString());
                }
            });

            // Hiding progress bar after all JSON loading done.


        }
    }

    public void loadClientes(View view){

        final View vista = view;

        GlobalVariables variablesGlobales = new GlobalVariables();
        int id_user = variablesGlobales.id_usuario;
        int rol = variablesGlobales.rol;
        String bd = variablesGlobales.bd;


        HTTP_URL = variablesGlobales.URLServicio + "obtenerclientes.php?basedatos=" + bd;
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new ParseJSonDataClass(vista).execute();

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
    }



}
