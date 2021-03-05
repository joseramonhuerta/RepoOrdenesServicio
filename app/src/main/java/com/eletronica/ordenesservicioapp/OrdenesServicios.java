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

public class OrdenesServicios extends AppCompatActivity implements CuadroDialogoFechas.Actualizar, SwipeRefreshLayout.OnRefreshListener{

    ListView listView;
    static View mView;
     androidx.appcompat.widget.Toolbar myToolbar;
    EditText txtSearch;
    TextView txtFiltros;
    // Server Http URL
    String HTTP_URL;
    int limite = 5;
    int filtro = 0;
    int filtro_tecnico = 0;
    ImageView btnFiltro;
    ImageView btnNueva;
    ImageView btnAtras;
    ImageView btnClear;
    ImageView btnCalendario;
    ImageView btnBusqueda;
    FragmentManager fm = getSupportFragmentManager();
    ProgressDialog pDialogo = null;
    RecyclerView listViewTecnicos;
    public static final int DIALOGO_FRAGMENT = 1;
    // String to hold complete JSON response object.
    String FinalJSonObject ;
    String FinalJSonObjectTecnicos;
    SwipeRefreshLayout swipeContainer;
    String FechaInicio;
    String FechaFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordenes_servicios);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        myToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarOrdenesServicio);
        setSupportActionBar(myToolbar);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.srlContainer);
        txtSearch = (EditText) findViewById(R.id.txtSearch);

        //txtSearch.addTextChangedListener(searchTextWatcher);



        listView = (ListView) findViewById(R.id.listViewOrdenes);
        listViewTecnicos = (RecyclerView) findViewById(R.id.listViewTecnicos);

         // btnActualizarEntregar = (Button) view.findViewById(R.id.btnActualizarEntregar);

        mView = (View) findViewById(R.id.viewOrdenServicio);
        loadTecnicos(mView);
        btnNueva = (ImageView) findViewById(R.id.btnNuevaOrden);
        btnAtras = (ImageView) findViewById(R.id.btnAtrasOrdenes);
        btnClear = (ImageView) findViewById(R.id.btnClearSearch);
        btnFiltro = (ImageView) findViewById(R.id.btnFiltroSearch);
        btnCalendario = (ImageView) findViewById(R.id.btnCalendario);
        btnBusqueda = (ImageView) findViewById(R.id.btnBusquedaOrdenes);
        txtFiltros = (TextView) findViewById(R.id.txtFiltros);

        btnNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(getApplicationContext(), NuevaOrdenServicio.class);
                intencion.putExtra("ID", 0);
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);

            }
        });


        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               txtSearch.setText("");
               loadPedidos(mView);
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
        swipeContainer.setOnRefreshListener(this);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                OrdenServicio orden = (OrdenServicio) listView.getItemAtPosition(position);
                // Realiza lo que deseas, al recibir clic en el elemento de tu listView determinado por su posicion.
                //Toast.makeText(getApplicationContext(),orden.nombre_cliente,Toast.LENGTH_LONG).show();
                Intent intencion = new Intent(getApplicationContext(), NuevaOrdenServicio.class);
                intencion.putExtra("ID", orden.id_orden_servicio);
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);
            }
        });

        btnBusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPedidos(mView);
            }
        });

        GlobalVariables vg = new GlobalVariables();

        if(vg.rol == 4) {
            btnNueva.setVisibility(View.GONE);
            listViewTecnicos.setVisibility(View.GONE);
        }

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;

        FechaInicio = selectedDate;
        FechaFin = selectedDate;

        actualizarFiltros();

        loadPedidos(mView);

    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    @Override
    public void onRefresh() {
        this.limite += 5;
        loadPedidos(mView);
    }

    @Override
    public void actualizaActividad(View view,String fInicio,String fFin) {
        FechaInicio = fInicio;
        FechaFin = fFin;
        //progressBar.setVisibility(mView.VISIBLE);
        loadPedidos(mView);


    }
    @Override
    public void onResume(){
        super.onResume();
        loadPedidos(mView);
    }

    private void seleccionarFiltro(View view){
        String[] opciones = {"Todos", "Recibido", "En Revisión", "Cotizado", "En Reparación","Reparado","Entregado","Devolución"};

        //0=Recibido,1=Revision,2=Cotizacion,3=Reparacion,4=Reparado,5=Entregado,6=Devolucion
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle("Filtro");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filtro = which;

                actualizarFiltros();
                loadPedidos(mView);
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
        List<OrdenServicio> ordenesList;

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
                        OrdenServicio ordenservicio;

                        // Defining CustomSubjectNamesList AS Array List.
                        ordenesList = new ArrayList<OrdenServicio>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            ordenservicio = new OrdenServicio();

                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.
                            ordenservicio.id_orden_servicio = Integer.parseInt(jsonObject.getString("id_orden_servicio"));
                            ordenservicio.nombre_cliente = jsonObject.getString("nombre_cliente");
                            ordenservicio.fecha = jsonObject.getString("fecha");
                            ordenservicio.descripcion_falla = jsonObject.getString("descripcion_falla");
                            ordenservicio.status_servicio = Integer.parseInt(jsonObject.getString("status_servicio"));
                            ordenservicio.status_servicio_descripcion = jsonObject.getString("status_servicio_descripcion");
                            ordenservicio.nombre_equipo = jsonObject.getString("nombre_equipo");

                            // Adding subject list object into CustomSubjectNamesList.
                            ordenesList.add(ordenservicio);
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
            final ListViewAdapterOrdenes adapter = new ListViewAdapterOrdenes(ordenesList, context);

            // Setting up all data into ListView.
            listView.setAdapter(adapter);
            swipeContainer.setRefreshing(false);
            /*
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
            */
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
    public void loadPedidos(View view){
        actualizarFiltros();
        final View vista = view;

        String fechaini = parseFecha(FechaInicio);
        String fechafin = parseFecha(FechaFin);


        GlobalVariables variablesGlobales = new GlobalVariables();
        int id_user = variablesGlobales.id_usuario;
        int rol = variablesGlobales.rol;
        String bd = variablesGlobales.bd;

        HTTP_URL = variablesGlobales.URLServicio + "obtenerordenesservicio.php?filtro="+filtro+"&id_usuario="+String.valueOf(id_user)+"&rol="+String.valueOf(rol)+"&filtro_tecnico="+filtro_tecnico+"&fechainicio="+fechaini+"&fechafin="+fechafin+"&limite="+String.valueOf(this.limite)+"&busqueda="+txtSearch.getText().toString()+"&basedatos="+bd;
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

        //this.limite += 5;
    }

    private class ParseJSonDataClassTecnicos extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public Fragment fragment;
        // Creating List of Subject class.
        List<Tecnico> tecnicosList;

        public ParseJSonDataClassTecnicos(View view) {
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
                if (FinalJSonObjectTecnicos != null) {

                    // Creating and setting up JSON array as null.
                    JSONArray jsonArray = null;
                    try {

                        // Adding JSON response object into JSON array.
                        jsonArray = new JSONArray(FinalJSonObjectTecnicos);

                        // Creating JSON Object.
                        JSONObject jsonObject;

                        // Creating Subject class object.
                        Tecnico tecnico;

                        // Defining CustomSubjectNamesList AS Array List.
                        tecnicosList = new ArrayList<Tecnico>();

                        tecnico = new Tecnico();

                        tecnico.setId_tecnico(0);
                        tecnico.setNombre_tecnico("TODOS");

                        tecnicosList.add(tecnico);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            tecnico = new Tecnico();

                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.
                            tecnico.setId_tecnico(Integer.parseInt(jsonObject.getString("id_usuario")));
                            tecnico.setNombre_tecnico(jsonObject.getString("nombre_usuario"));


                            // Adding subject list object into CustomSubjectNamesList.
                            tecnicosList.add(tecnico);
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

            //listViewTecnicos.setAdapter(adapter);

            listViewTecnicos.setAdapter(new MyRecyclerViewAdapter(tecnicosList, new RecyclerViewOnItemClickListener() {

                @Override
                public void onClick(View v, int position) {
                    //Toast.makeText(OrdenesServicios.this, tecnicosList.get(position).getNombre_tecnico(), Toast.LENGTH_SHORT).show();
                    filtro_tecnico = tecnicosList.get(position).getId_tecnico();

                    //ImageView ivImagentecnico = (ImageView)v.findViewById(R.id.ivImagenTecnico);
                    //ivImagentecnico.setImageResource(R.drawable.filtro_tecnico_selected);
                    loadPedidos(mView);



                }
            }));

            //Horizontal orientation.
            listViewTecnicos.setLayoutManager(new LinearLayoutManager(OrdenesServicios.this, LinearLayoutManager.HORIZONTAL, false));




        }
    }

    public void loadTecnicos(View view){
        final View vista = view;

        GlobalVariables variablesGlobales = new GlobalVariables();
        String bd = variablesGlobales.bd;

        HTTP_URL = variablesGlobales.URLServicio + "obtenertecnicos.php?basedatos="+bd;
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObjectTecnicos = response ;

                        // Calling method to parse JSON object.
                        new ParseJSonDataClassTecnicos(vista).execute();

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
        String status="";
        switch (filtro) {
            case 0:
                status = "Todos";
                break;
            case 1:
                status = "Recibido";
                break;
            case 2:
                status = "En Revisión";
                break;
            case 3:
                status = "Cotizado";
                break;
            case 4:
                status = "En Reparación";
                break;
            case 5:
                status = "Reparado";
                break;
            case 6:
                status = "Entregado";
                break;
            case 7:
                status = "Devolución";
                break;
        }

        String filtros = "Periodo: " + FechaInicio + " - " + FechaFin + "  Status: " + status;
        txtFiltros.setText(filtros);
    }

}
