package com.eletronica.ordenesservicioapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Application;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.eletronica.ordenesservicioapp.R;

import static java.lang.Integer.parseInt;

public class InicioSesion extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener  {
    RequestQueue rq;
    JsonRequest jrq;
    EditText cajausuario, cajaclave;
    Button btnConsultar;
    private Context mContext;
    private SharedPreferences prefs;
    View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        ///View vista =inflater.inflate(R.layout.activity_inicio_sesion, container, false);
        //this.mContext = InicioSesion.this;
        mView = (View) findViewById(R.id.viewInicioSesion);
        cajausuario = (EditText) findViewById(R.id.edit_user);
        cajaclave = (EditText) findViewById(R.id.edit_contra);
        btnConsultar = (Button) findViewById(R.id.bt_iniciar);

        prefs = mView.getContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        String user = prefs.getString("user", "");
        String pass = prefs.getString("pass", "");
        cajausuario.setText(user);
        cajaclave.setText(pass);



        rq = Volley.newRequestQueue(mView.getContext());

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();

            }
        });



    }



    private void iniciarSesion(){

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user", cajausuario.getText().toString());
        editor.putString("pass", cajaclave.getText().toString());

        editor.commit();

        GlobalVariables variablesGlobales = new GlobalVariables();
        String url = variablesGlobales.URLServicio + "sesion.php?usuario="+cajausuario.getText().toString()+
                "&clave="+cajaclave.getText().toString();
        jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        rq.add(jrq);

    }

    public void onErrorResponse(VolleyError error) {
        Toast.makeText(mView.getContext()," no se  escontrado el usuario " + error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {


        User usuario = new User();
        JSONArray jsonArray = response.optJSONArray("datos");
        JSONObject jsonObject = null;

        try {
            jsonObject = jsonArray.getJSONObject(0);
            usuario.setUsuario(jsonObject.optString("usuario"));
            usuario.setNombre(jsonObject.optString("nombre"));
            usuario.setId_usuario(parseInt(jsonObject.optString("id_usuario")));
            usuario.setRol(parseInt(jsonObject.optString("rol")));

            GlobalVariables variablesGlobales = new GlobalVariables();

            variablesGlobales.id_usuario = usuario.getId_usuario();
            variablesGlobales.usuario = usuario.getUsuario();
            variablesGlobales.nombre_Usuario = usuario.getNombre();
            variablesGlobales.rol = usuario.getRol();
            variablesGlobales.bd = jsonObject.optString("basedatos");
            variablesGlobales.id_empresa =  parseInt(jsonObject.optString("id_empresa"));
            variablesGlobales.nombre_empresa = jsonObject.optString("nombre_empresa");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Toast.makeText(getContext(),"se ha escontrado el usuario: " + usuario.getUsuario().toString(), Toast.LENGTH_SHORT).show();
        Intent intencion = new Intent(mView.getContext(), Menu.class);
        intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intencion);

        //Intent intencion = new Intent(getContext(), OrdenesServicio.class);
        //startActivity(intencion);
    }

    @Override public void onBackPressed() {

    }
}
