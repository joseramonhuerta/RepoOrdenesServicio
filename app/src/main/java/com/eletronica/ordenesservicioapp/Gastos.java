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

public class Gastos extends AppCompatActivity implements CuadroDialogoFechas.Actualizar, SwipeRefreshLayout.OnRefreshListener{

    ListView listView;
    static View mView;
    androidx.appcompat.widget.Toolbar myToolbar;
    EditText txtSearch;
    TextView txtFiltros;
    // Server Http URL
    String HTTP_URL;
    int filtro = 0;

    ImageView btnFiltro;
    ImageView btnNueva;
    ImageView btnAtras;
    ImageView btnClear;
    ImageView btnCalendario;
    ImageView btnReporteGastos;
    FragmentManager fm = getSupportFragmentManager();
    ProgressDialog pDialogo = null;

    public static final int DIALOGO_FRAGMENT = 1;
    // String to hold complete JSON response object.
    String FinalJSonObject ;

    SwipeRefreshLayout swipeContainer;
    String FechaInicio;
    String FechaFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gastos);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        myToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarGastos);
        setSupportActionBar(myToolbar);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.srlContainerGastos);
        txtSearch = (EditText) findViewById(R.id.txtSearchGastos);

        //txtSearch.addTextChangedListener(searchTextWatcher);



        listView = (ListView) findViewById(R.id.listViewGastos);

        mView = (View) findViewById(R.id.viewGastos);

        btnNueva = (ImageView) findViewById(R.id.btnNuevoGasto);
        btnAtras = (ImageView) findViewById(R.id.btnAtrasGastos);
        btnClear = (ImageView) findViewById(R.id.btnClearSearchGastos);
        btnFiltro = (ImageView) findViewById(R.id.btnFiltroSearchGastos);
        btnCalendario = (ImageView) findViewById(R.id.btnCalendarioGastos);
        txtFiltros = (TextView) findViewById(R.id.txtFiltrosGastos);
        btnReporteGastos  = (ImageView) findViewById(R.id.btnReporteGastos);

        btnNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(getApplicationContext(), NuevoGasto.class);
                intencion.putExtra("ID", 0);
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);

            }
        });


        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearch.setText("");

            }
        });

        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFiltro(v);

            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        btnCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fm.beginTransaction();
                CuadroDialogoFechas dialogoFragment = new CuadroDialogoFechas(v.getContext(), fm);

                CuadroDialogoFechas tPrev =  (CuadroDialogoFechas) fm.findFragmentByTag("dialogo");

                if(tPrev!=null)
                    ft.remove(tPrev);

                dialogoFragment.setTargetFragment(null, DIALOGO_FRAGMENT);
                dialogoFragment.show(fm, "dialogo");

            }
        });

        btnReporteGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(getApplicationContext(), ReporteGastos.class);

                String fechaini = parseFecha(FechaInicio);
                String fechafin = parseFecha(FechaFin);

                intencion.putExtra("FechaInicio", FechaInicio);
                intencion.putExtra("FechaFin", FechaFin);
                intencion.putExtra("Tipo", filtro);
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);

            }
        });

        swipeContainer.setOnRefreshListener(this);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Gasto gasto = (Gasto) listView.getItemAtPosition(position);
                // Realiza lo que deseas, al recibir clic en el elemento de tu listView determinado por su posicion.
                //Toast.makeText(getApplicationContext(),orden.nombre_cliente,Toast.LENGTH_LONG).show();
                Intent intencion = new Intent(getApplicationContext(), NuevoGasto.class);
                intencion.putExtra("ID", gasto.getId_gasto());
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);
            }
        });

        GlobalVariables vg = new GlobalVariables();

        if(vg.rol == 4) {
            btnNueva.setVisibility(View.GONE);
        }

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;

        FechaInicio = selectedDate;
        FechaFin = selectedDate;

        actualizarFiltros();

        loadGastos(mView);

    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    @Override
    public void onRefresh() {
        loadGastos(mView);
    }

    @Override
    public void actualizaActividad(View view,String fInicio,String fFin) {
        FechaInicio = fInicio;
        FechaFin = fFin;
        //progressBar.setVisibility(mView.VISIBLE);
        loadGastos(mView);


    }
    @Override
    public void onResume(){
        super.onResume();
        loadGastos(mView);
    }

    private void seleccionarFiltro(View view){
        String[] opciones = {"Todos", "Ingresos", "Egresos"};

        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle("Filtro");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filtro = which;

                actualizarFiltros();
                loadGastos(mView);
            }
        });

        Dialog dialogo =  builder.create();
        dialogo.show();
    }


    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public Fragment fragment;
        // Creating List of Subject class.
        List<Gasto> gastosList;

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
                        Gasto gasto;

                        // Defining CustomSubjectNamesList AS Array List.
                        gastosList = new ArrayList<Gasto>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            gasto = new Gasto();

                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.

                            gasto.setId_gasto(Integer.parseInt(jsonObject.getString("id_gasto")));
                            gasto.setFecha(jsonObject.getString("fecha"));
                            gasto.setConcepto(jsonObject.getString("concepto"));
                            gasto.setImporte(Double.parseDouble(jsonObject.getString("importe")));
                            gasto.setTipo(Integer.parseInt(jsonObject.getString("tipo")));
                            gasto.setTipo_descripcion(jsonObject.getString("tipo_descripcion"));
                            gasto.setId_orden_servicio(Integer.parseInt(jsonObject.getString("id_orden_servicio")));
                            gasto.setOrden_servicio_descripcion(jsonObject.getString("orden_servicio_descripcion"));

                            // Adding subject list object into CustomSubjectNamesList.
                            gastosList.add(gasto);
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
            final ListViewAdapterGastos adapter = new ListViewAdapterGastos(gastosList, context);

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

    public String parseFecha(String fecha){
        String parsedDate = "";


        String newFecha = fecha;

        try {
            Date initDate = new SimpleDateFormat("dd/mm/yyyy").parse(newFecha);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
            parsedDate = formatter.format(initDate).toString();
        } catch (ParseException ex) {

        }

        return parsedDate;

    }
    public void loadGastos(View view){
        actualizarFiltros();
        final View vista = view;

        String fechaini = parseFecha(FechaInicio);
        String fechafin = parseFecha(FechaFin);


        GlobalVariables variablesGlobales = new GlobalVariables();
        int id_user = variablesGlobales.id_usuario;
        String bd = variablesGlobales.bd;


        HTTP_URL = variablesGlobales.URLServicio + "obtenergastos.php?filtro="+filtro+"&id_usuario="+String.valueOf(id_user)+"&fechainicio="+fechaini+"&fechafin="+fechafin+"&basedatos="+bd;
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


    public void actualizarFiltros(){
        String tipo="";
        switch (filtro) {
            case 0:
                tipo = "Todos";
                break;
            case 1:
                tipo = "Ingresos";
                break;
            case 2:
                tipo = "Egresos";
                break;
        }

        String filtros = "Periodo: " + FechaInicio + " - " + FechaFin + "  Tipo: " + tipo;
        txtFiltros.setText(filtros);
    }

}
