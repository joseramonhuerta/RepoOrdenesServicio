package com.eletronica.ordenesservicioapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CuadroDialogoClientes extends DialogFragment {
    Context mContext;
    FragmentManager fm;
    Dialog dialogo = null;
    View mView;

    TextView txtSearchCliente;
    ImageView btnClearSearchCliente;
    ListView listViewClientes;
    Button btnAceptarCliente;
    Button btnSalirCliente;

    ProgressBar progressBar;

    String HTTP_URL = "";
    String FinalJSonObject ;

    int position_selected=-1;
    int id_cliente=0;
    String nombre_cliente="";
    String celular="";

    public interface ActualizarCliente {

        public void actualizaActividadCliente(View view,int id_cliente,String nombre_cliente, String celular);
    }

    CuadroDialogoClientes.ActualizarCliente listener;
    Activity activity;

    public CuadroDialogoClientes(Context context, FragmentManager fm, View view) {
        this.mContext = context;
        this.fm = fm;
        this.mView = view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CuadroDialogoClientes.ActualizarCliente) getActivity();
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString()
                    + " must implement Actualizar");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.cuadro_dialogo_clientes, null);

        dialogo = getDialog();
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setCancelable(false);
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        txtSearchCliente = (TextView) v.findViewById(R.id.txtSearchCliente);
        btnClearSearchCliente = (ImageView) v.findViewById(R.id.btnClearSearchCliente);

        btnAceptarCliente = (Button) v.findViewById(R.id.btnAceptarCliente);
        btnSalirCliente = (Button) v.findViewById(R.id.btnSalirCliente);

        listViewClientes = (ListView) v.findViewById(R.id.listViewClientes);

        progressBar = (ProgressBar) v.findViewById(R.id.ProgressBarClientes);

        btnAceptarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
                listener.actualizaActividadCliente(mView,id_cliente,nombre_cliente, celular);
            }
        });



        btnSalirCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();;
            }
        });

        btnClearSearchCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearchCliente.setText("");

            }
        });

        listViewClientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                position_selected = position;
                Cliente selItem = (Cliente) listViewClientes.getAdapter().getItem(position);

                id_cliente =  selItem.getId_cliente();
                nombre_cliente = selItem.getNombre_cliente();
                celular = selItem.getCelular();
                //Toast.makeText(view.getContext(), desc+" ID: " + String.valueOf(idAsig)+ " IDPRo: "+String.valueOf(idPro), Toast.LENGTH_LONG).show();

                //Object lav = listView.getAdapter().getItem(position);



            }
        });

        loadClientes(v);



        return v;

    }

    private void loadClientes(final View view){
        progressBar.setVisibility(View.VISIBLE);

        GlobalVariables variablesGlobales = new GlobalVariables();
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
                        new CuadroDialogoClientes.ParseJSonDataClass(view.getContext()).execute();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Showing error message if something goes wrong.
                        Toast.makeText(view.getContext(),error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);

    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;

        // Creating List of Subject class.
        List<Cliente> clientesList;

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

                            //Storing Subject name in subject list.


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
            //FragmentManager fm = getFragmentManager();
            final ListViewAdapterClientes adapter = new ListViewAdapterClientes(clientesList, context);

            // Setting up all data into ListView.
            listViewClientes.setAdapter(adapter);

            txtSearchCliente.addTextChangedListener(new TextWatcher() {
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
                    String text = txtSearchCliente.getText().toString().toLowerCase(Locale.getDefault());
                    adapter.filter(text);
                    // listView.getAdapter().getFilter().filter(s.toString());
                }
            });

            // Hiding progress bar after all JSON loading done.
            progressBar.setVisibility(View.GONE);

        }
    }



}
