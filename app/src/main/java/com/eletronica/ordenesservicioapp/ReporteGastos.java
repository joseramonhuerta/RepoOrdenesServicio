package com.eletronica.ordenesservicioapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReporteGastos extends AppCompatActivity {
    static View mView;
    PieChart pieChart;
    ImageView btnAtras;
    androidx.appcompat.widget.Toolbar myToolbar;

    String FechaInicio;
    String FechaFin;
    String HTTP_URL;
    String FinalJSonObject ;
    int filtro = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_gastos);
        myToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarReporteGastos);
        btnAtras = (ImageView) findViewById(R.id.btnAtrasReporteGastos);
        pieChart = findViewById(R.id.graficaPastel);
        mView = (View) findViewById(R.id.viewReporteGastos);

        Bundle extras = getIntent().getExtras();

        FechaInicio = extras.getString("FechaInicio");
        FechaFin = extras.getString("FechaFin");
        filtro = extras.getInt("Tipo");

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        crearGrafico(mView);

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

    public void crearGrafico(View view){

        final View vista = view;

        String fechaini = parseFecha(FechaInicio);
        String fechafin = parseFecha(FechaFin);

        GlobalVariables variablesGlobales = new GlobalVariables();
        int id_user = variablesGlobales.id_usuario;
        int rol = variablesGlobales.rol;
        String bd = variablesGlobales.bd;

        HTTP_URL = variablesGlobales.URLServicio + "obtenerreportegastos.php?filtro="+filtro+"&fechainicio="+fechaini+"&fechafin="+fechafin+"&basedatos="+bd;
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new ReporteGastos.ParseJSonDataClass(vista).execute();

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

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public Fragment fragment;
        // Creating List of Subject class.
        List<PieEntry> pieEntries;

        //pieEntries = new ArrayList<>();

        //pieEntries.add(new PieEntry(33,"ventas 1", Color.blue(1)));
        //pieEntries.add(new PieEntry(33,"ventas 2", Color.red(2)));
        //pieEntries.add(new PieEntry(66,"ventas 3", Color.green(3)));

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
                       // OrdenServicio ordenservicio;

                        // Defining CustomSubjectNamesList AS Array List.
                        pieEntries = new ArrayList<PieEntry>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            //ordenservicio = new OrdenServicio();

                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.
                            //ordenservicio.id_orden_servicio = Integer.parseInt(jsonObject.getString("id_orden_servicio"));
                            //ordenservicio.nombre_cliente = jsonObject.getString("nombre_cliente");
                            //ordenservicio.fecha = jsonObject.getString("fecha");
                            //ordenservicio.descripcion_falla = jsonObject.getString("descripcion_falla");
                            //ordenservicio.status_servicio = Integer.parseInt(jsonObject.getString("status_servicio"));
                            //ordenservicio.status_servicio_descripcion = jsonObject.getString("status_servicio_descripcion");
                            //ordenservicio.nombre_equipo = jsonObject.getString("nombre_equipo");

                            String label = jsonObject.getString("tipo");
                            float total = Float.parseFloat(jsonObject.getString("total"));

                            pieEntries.add(new PieEntry(total,label));

                            // Adding subject list object into CustomSubjectNamesList.
                            //ordenesList.add(ordenservicio);
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
            Description descripcion =  new Description();
            String titulo = "Reporte de gastos del " + FechaInicio + " al " + FechaFin;
            descripcion.setText(titulo);
            descripcion.setTextSize(14);
            //pieChart.setNoDataText("No hay datos para mostrar");

            pieChart.setDescription(descripcion);
            PieDataSet pieDataSet = new PieDataSet(pieEntries, "Leyendas");

            pieDataSet.setColors(Color.BLUE,Color.RED);
            pieDataSet.setValueTextSize(12);
            pieDataSet.setValueTextColor(Color.WHITE);
            PieData pieData = new PieData(pieDataSet);

            pieChart.setData(pieData);
            pieChart.animateY(2000);
            pieChart.invalidate();

        }
    }

}
