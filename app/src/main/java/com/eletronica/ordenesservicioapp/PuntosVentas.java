package com.eletronica.ordenesservicioapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PuntosVentas extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
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
        setContentView(R.layout.activity_puntos_ventas);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        myToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarCatalogoPuntosVentas);
        setSupportActionBar(myToolbar);

        txtSearch = (EditText) findViewById(R.id.txtSearchCatalogoPuntosVentas);

        //txtSearch.addTextChangedListener(searchTextWatcher);

        listView = (ListView) findViewById(R.id.listViewCatalogoPuntosVentas);

        // btnActualizarEntregar = (Button) view.findViewById(R.id.btnActualizarEntregar);

        mView = (View) findViewById(R.id.viewCatalogoPuntosVentas);

        btnNueva = (ImageView) findViewById(R.id.btnNuevoCatalogoPuntosVentas);
        btnAtras = (ImageView) findViewById(R.id.btnAtrasCatalogoPuntosVentas);
        btnClear = (ImageView) findViewById(R.id.btnClearSearchCatalogoPuntosVentas);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.srlContainerPuntosVentas);

        btnNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(getApplicationContext(), NuevoPuntoVenta.class);
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

                PuntoVenta pva = (PuntoVenta) listView.getItemAtPosition(position);

                Intent intencion = new Intent(getApplicationContext(), NuevoPuntoVenta.class);
                intencion.putExtra("ID", pva.getId_puntodeventa());
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);
            }
        });

        GlobalVariables vg = new GlobalVariables();

        loadPuntosVentas(mView);
    }

    @Override
    public void onRefresh() {
        loadPuntosVentas(mView);
    }

    @Override
    public void onResume(){
        super.onResume();
        loadPuntosVentas(mView);
    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public Fragment fragment;
        // Creating List of Subject class.
        List<PuntoVenta> puntosdeventasList;

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
                        PuntoVenta puntoVenta;

                        // Defining CustomSubjectNamesList AS Array List.
                        puntosdeventasList = new ArrayList<PuntoVenta>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            puntoVenta = new PuntoVenta();

                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.
                            puntoVenta.setId_puntodeventa(Integer.parseInt(jsonObject.getString("id_puntodeventa")));
                            puntoVenta.setDescripcion_puntodeventa(jsonObject.getString("descripcion_puntodeventa"));



                            // Adding subject list object into CustomSubjectNamesList.
                            puntosdeventasList.add(puntoVenta);
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
            final ListViewAdapterPuntosVentas adapter = new ListViewAdapterPuntosVentas(puntosdeventasList, context);

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

    public void loadPuntosVentas(View view){

        final View vista = view;

        GlobalVariables variablesGlobales = new GlobalVariables();
        String bd = variablesGlobales.bd;


        HTTP_URL = variablesGlobales.URLServicio + "obtenerpuntosdeventas.php?basedatos=" + bd;
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new PuntosVentas.ParseJSonDataClass(vista).execute();

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