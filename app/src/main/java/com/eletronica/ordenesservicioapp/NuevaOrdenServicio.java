package com.eletronica.ordenesservicioapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.eletronica.ordenesservicioapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.mazenrashed.printooth.utilities.Printing;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static java.lang.Integer.parseInt;


public class NuevaOrdenServicio extends AppCompatActivity implements CuadroDialogoClientes.ActualizarCliente {

    int id_orden_servicio = 0;
    int id_cliente = 0;
    int id_cliente_venta = 0;
    View mView;
    String HTTP_URL;
    String FinalJSonObject;
    String FinalJSonObjectConfiguracion;
    String FinalJSonObjectTecnicos;
    String FinalJSonObjectPuntosVentas;
    String foto;
    String nombreImagen;
    String fotoBack;
    String nombreImagenBack;
    String fotoTecnico;
    String nombreImagenTecnico;
    File file;
    Uri output;
    StringRequest stringRequest;

    TextInputLayout txtFolio;
    TextInputLayout txtNombreCliente;
    TextInputLayout txtFecha;
    TextInputLayout txtFechaEntrega;
    TextInputLayout txtEquipo;
    TextInputLayout txtModelo;
    TextInputLayout txtSerie;
    TextInputLayout txtFalla;

    TextInputLayout txtDiagnostico;
    TextInputLayout txtReparacion;
    TextInputLayout txtPresupuesto;
    TextInputLayout txtCelular;
    TextInputLayout txtNombreClienteVenta;
    TextInputLayout txtPrecioVenta;

    LinearLayout groupGenerales;
    LinearLayout groupTecnicos;
    LinearLayout groupAdministracion;
    LinearLayout groupVenta;
    LinearLayout layFechaEntrega;
    LinearLayout layPuntoVenta;

    ImageView btnAtras;
    ImageView ivImagen;
    ImageView ivImagenBack;
    ImageView ivImagenTecnico;
    ImageView ivImagenQr;

    Button btnGuardar;
    ImageView btnLimpiar;
    ImageView btnCamara;
    ImageView btnCamaraBack;
    ImageView btnCamaraTecnico;
    ImageView btnNuevoCliente;
    ImageView btnImprimirRecibo;
    ImageView btnNuevoClienteVenta;

    Spinner spnStatus;
    Spinner txtTecnico;
    Spinner spnTipoOrden;
    Spinner txtPuntoVenta;

    Bitmap photobmp = null;
    Bitmap photobmpBack = null;
    Bitmap photobmpTecnico = null;

    TabHost tabs;
    TabHost.TabSpec ts1,ts2,ts3,ts4;

    public int statusSelected=-1;
    public int tecnicoSelected=-1;
    public int tipoSelected=-1;
    public int puntoventaSelected=-1;

    Dialog dialogo = null;

    //ProgressDialog pDialogo = null;
    //ProgressBar progressBar;
    SweetAlertDialog pDialogo;

    ArrayList<Tecnico> tecnicosList;
    ArrayList<PuntoVenta> puntosventasList;
    String[] spinnerArray;
    String[] spinnerArrayPuntoVenta;
    HashMap<Integer,Integer> spinnerMap;
    HashMap<Integer,Integer> spinnerMapPuntoVenta;

    int enviarMSMCliente = 0;
    int reenviarImagen = 0;
    int reenviarImagenBack = 0;
    int reenviarImagenTecnico = 0;

    QRGEncoder qrgEncoder;

    private static final int REQUEST_DISPOSITIVO = 425;
    private static final int LIMITE_CARACTERES_POR_LINEA = 32;
    private static final String TAG_DEBUG = "tag_debug";
    private static final int IR_A_DIBUJAR = 632;
    private static final int COD_PERMISOS = 872;
    private static final int INTENT_CAMARA = 123;
    private static final int INTENT_GALERIA = 321;
    private final int ANCHO_IMG_58_MM = 384;
    private static final int MODE_PRINT_IMG = 0;

    public static final byte[] ESC_ALIGN_LEFT = new byte[] { 0x1b, 'a', 0x00 };
    public static final byte[] ESC_ALIGN_RIGHT = new byte[] { 0x1b, 'a', 0x02 };
    public static final byte[] ESC_ALIGN_CENTER = new byte[] { 0x1b, 'a', 0x01 };
    public static final byte[] ESC_CANCEL_BOLD = new byte[] { 0x1B, 0x45, 0 };

    private volatile boolean pararLectura;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice dispositivoBluetooth;
    private BluetoothSocket bluetoothSocket;

    private UUID aplicacionUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    // Para el flujo de datos de entrada y salida del socket bluetooth
    private OutputStream outputStream;
    private InputStream inputStream;

    Printing printing;
    boolean conectada = false;

    OrdenServicio ordenImpresion;

    @Override
    public void actualizaActividadCliente(View view, int id_cliente, String nombre_cliente, String celular, int tipo) {
        if(tipo == 1) {
            this.id_cliente = id_cliente;
            txtNombreCliente.getEditText().setText(nombre_cliente);
            txtCelular.getEditText().setText(celular);
        }

        if(tipo == 2){
            this.id_cliente_venta = id_cliente;
            txtNombreClienteVenta.getEditText().setText(nombre_cliente);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_orden_servicio);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mView = (View) findViewById(R.id.viewOrdenServicio);

        //inicializamos el BlueTooth Adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        tabs = (TabHost) findViewById(R.id.tabOrdenes);
        tabs.setup();

        ts1 = tabs.newTabSpec("tabGenerales");
        Resources res = getResources();
        Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.info, null);
        ts1.setIndicator("General", drawable);
        ts1.setContent(R.id.tabGenerales);
        tabs.addTab(ts1);

        ts2 = tabs.newTabSpec("tabTecnicos");
        drawable = ResourcesCompat.getDrawable(res, R.drawable.manual, null);
        ts2.setIndicator("Técnico", drawable);
        ts2.setContent(R.id.tabTecnicos);
        tabs.addTab(ts2);


        ts3 = tabs.newTabSpec("tabAdminitracion");
        drawable = ResourcesCompat.getDrawable(res, R.drawable.working, null);
        ts3.setIndicator("Admin", drawable);
        ts3.setContent(R.id.tabAdmin);
        tabs.addTab(ts3);

        ts4 = tabs.newTabSpec("tabVenta");
        drawable = ResourcesCompat.getDrawable(res, R.drawable.working, null);
        ts4.setIndicator("Venta", drawable);
        ts4.setContent(R.id.tabVenta);
        tabs.addTab(ts4);

        txtFolio = (TextInputLayout) findViewById(R.id.txtFolio);
        txtNombreCliente = (TextInputLayout) findViewById(R.id.txtNombreCliente);
        txtFecha = (TextInputLayout) findViewById(R.id.txtFecha);
        txtFechaEntrega = (TextInputLayout) findViewById(R.id.txtFechaEntrega);
        txtEquipo = (TextInputLayout) findViewById(R.id.txtEquipo);
        txtModelo = (TextInputLayout) findViewById(R.id.txtModelo);
        txtSerie = (TextInputLayout) findViewById(R.id.txtSerie);
        txtFalla = (TextInputLayout) findViewById(R.id.txtFalla);
        txtTecnico = (Spinner) findViewById(R.id.txtTecnico);
        txtDiagnostico = (TextInputLayout) findViewById(R.id.txtDiagnostico);
        txtReparacion = (TextInputLayout) findViewById(R.id.txtReparacion);
        txtPresupuesto = (TextInputLayout) findViewById(R.id.txtPresupuesto);
        txtCelular = (TextInputLayout) findViewById(R.id.txtCelular);
        txtPrecioVenta = (TextInputLayout) findViewById(R.id.txtPrecioVenta);
        txtNombreClienteVenta = (TextInputLayout) findViewById(R.id.txtNombreClienteVenta);
        txtPuntoVenta = (Spinner) findViewById(R.id.txtPuntoVenta);

        groupGenerales = (LinearLayout) findViewById(R.id.groupGenerales);
        groupTecnicos = (LinearLayout) findViewById(R.id.groupTecnicos);
        groupAdministracion = (LinearLayout) findViewById(R.id.groupAdministracion);

        layFechaEntrega = (LinearLayout) findViewById(R.id.layFechaEntrega);
        groupVenta = (LinearLayout) findViewById(R.id.groupVentas);
        layPuntoVenta = (LinearLayout) findViewById(R.id.layPuntoVenta);

        btnGuardar = (Button) findViewById(R.id.btnGuardarOrden);
        btnLimpiar = (ImageView) findViewById(R.id.btnLimpiarOrden);
        btnAtras = (ImageView) findViewById(R.id.btnAtrasNuevaOrden);
        btnCamara = (ImageView) findViewById(R.id.btnCamara);
        btnCamaraBack = (ImageView) findViewById(R.id.btnCamaraBack);
        btnCamaraTecnico = (ImageView) findViewById(R.id.btnCamaraTecnico);
        btnNuevoCliente = (ImageView) findViewById(R.id.btnNuevoCliente);
        btnImprimirRecibo = (ImageView) findViewById(R.id.btnImprimirRecibo);
        btnNuevoCliente = (ImageView) findViewById(R.id.btnNuevoCliente);
        btnNuevoClienteVenta = (ImageView) findViewById(R.id.btnNuevoClienteVenta);

        ivImagen = (ImageView) findViewById(R.id.ivImagen);
        ivImagenBack = (ImageView) findViewById(R.id.ivImagenBack);
        ivImagenTecnico = (ImageView) findViewById(R.id.ivImagenTecnico);
        ivImagenQr = (ImageView) findViewById(R.id.ivImagenQr);

        spnStatus = (Spinner) findViewById(R.id.spnStatus);
        spnTipoOrden = (Spinner) findViewById(R.id.spnTipoOrden);

        String[] opciones = {"Recibido", "En Revision", "Cotizado", "En Reparacion","Reparado","Entregado","Devolucion","Bodega", "En Tienda", "Vendida"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        spnStatus.setAdapter(adapter);

        String[] opciones_tipo = {"Cargo", "Propia"};
        ArrayAdapter adapter_tipo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, opciones_tipo);
        spnTipoOrden.setAdapter(adapter_tipo);

        Bundle extras = getIntent().getExtras();

        id_orden_servicio = extras.getInt("ID");
        id_cliente = 0;
        id_cliente_venta = 0;



        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        txtFecha.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        txtNombreCliente.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                CuadroDialogoClientes dialogoFragment = new CuadroDialogoClientes(mView.getContext(), fm, mView, 1);

                CuadroDialogoClientes tPrev =  (CuadroDialogoClientes) fm.findFragmentByTag("dialogo_clientes");

                if(tPrev!=null)
                    ft.remove(tPrev);

                //dialogoFragment.setTargetFragment(mainFrag, DIALOGO_FRAGMENT);
                dialogoFragment.show(fm, "dialogo_clientes");
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validarCliente() | !validarEquipo() | !validarFalla() | !validarStatus() | !validarClienteVenta()){
                    return;
                }
                procesarGuardar(v);
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id_orden_servicio > 0){
                    eliminarOrden(v);
                }else{
                    limpiarCampos(v);
                }


                //Toast.makeText(v.getContext(),"Limpiando campos",Toast.LENGTH_LONG).show();

            }
        });

        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statusSelected = position;
                //{"Recibido", "Revisión", "Cotización", "Reparación","Reparado","Entregado","Devolución"};
                if(position == 1 || position == 2 || position == 3 || position == 4)
                    enviarMSMCliente = 1;
                else
                    enviarMSMCliente = 0;

                validarSeleccionStatus(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnTipoOrden.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoSelected = position;
                //{"Recibido", "Revisión", "Cotización", "Reparación","Reparado","Entregado","Devolución"};
                //if(position == 1 || position == 2 || position == 3 || position == 4)
                //    enviarMSMCliente = 1;
                //else
                 //   enviarMSMCliente = 0;

                validarSeleccionTipoOrden(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtTecnico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tecnicoSelected = position;




                //tecnicoSelected = spinnerMap.get(txtTecnico.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCamara(1);
            }
        });

        btnCamaraBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCamara(2);
            }
        });

        btnCamaraTecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCamara(3);
            }
        });

        btnNuevoCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(getApplicationContext(), NuevoCliente.class);
                intencion.putExtra("ID", 0);
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);

            }
        });

        btnImprimirRecibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    generarCodigoQr();
                }catch (Exception ex){

                }

            }
        });

        btnNuevoClienteVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(getApplicationContext(), NuevoCliente.class);
                intencion.putExtra("ID", 0);
                intencion.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intencion);

            }
        });

        txtNombreClienteVenta.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                CuadroDialogoClientes dialogoFragment = new CuadroDialogoClientes(mView.getContext(), fm, mView, 2);

                CuadroDialogoClientes tPrev =  (CuadroDialogoClientes) fm.findFragmentByTag("dialogo_clientes");

                if(tPrev!=null)
                    ft.remove(tPrev);

                //dialogoFragment.setTargetFragment(mainFrag, DIALOGO_FRAGMENT);
                dialogoFragment.show(fm, "dialogo_clientes");
            }
        });



        getConfiguracion(mView);



    }

    private void getConfiguracion(View mView){
        View vista = mView;
        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String bd = variablesGlobales.bd;

            HTTP_URL =variablesGlobales.URLServicio + "getconfiguracion.php?basedatos=" + bd;
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObjectConfiguracion = response ;

                            // Calling method to parse JSON object.
                            new NuevaOrdenServicio.ParseJSonDataClassGetConfiguracion(vista).execute();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            // Showing error message if something goes wrong.
                            Toast.makeText(vista.getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    });

            // Creating String Request Object.
            RequestQueue requestQueue = Volley.newRequestQueue(vista.getContext());

            // Passing String request into RequestQueue.
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generarCodigoQr(){

        if(id_orden_servicio > 0){

            try{
                GlobalVariables vg = new GlobalVariables();
                if(vg.config_imprimeticket == 1)
                    imprimir();
            }catch (Exception ex){
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

    }

    private Bitmap getCodigoQr(){
        Bitmap bitmap = null;
        GlobalVariables variablesGlobales = new GlobalVariables();

        String bd = variablesGlobales.bd;

        String url = variablesGlobales.URLServicio + "getstatusorden.php?basedatos=" + bd + "&id_orden=" + String.valueOf(id_orden_servicio);

        if(id_orden_servicio > 0){
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smd = width<height ? width:height;
            smd = smd * 3/4;
            qrgEncoder = new QRGEncoder(url, null, QRGContents.Type.TEXT,smd);

            try{
                Bitmap bitmap2 = qrgEncoder.encodeAsBitmap();

                bitmap = Bitmap.createScaledBitmap(bitmap2, 250 /*Ancho*/, 250 /*Alto*/, true /* filter*/);
                //ivImagenQr.setImageBitmap(bitmap);
                //imprimir();
            }catch (Exception ex){

            }

        }

        return bitmap;

    }

    private void validarSeleccionStatus(int position){
        if(position == 5 | position == 6){
            layFechaEntrega.setVisibility(View.VISIBLE);
        }else{
            layFechaEntrega.setVisibility(View.GONE);
        }

        if(position == 8 | position == 9){
            layPuntoVenta.setVisibility(View.VISIBLE);
        }else{
            layPuntoVenta.setVisibility(View.GONE);
        }

    }

    private void validarSeleccionTipoOrden(int position){
        if(position == 0){
            this.id_cliente_venta = 0;
            this.txtNombreClienteVenta.setEnabled(false);
            this.txtPrecioVenta.setEnabled(false);
            this.btnNuevoClienteVenta.setEnabled(false);
            this.txtNombreClienteVenta.getEditText().setText("");
            this.txtPrecioVenta.getEditText().setText("");

            this.txtNombreCliente.setEnabled(true);
            this.btnNuevoCliente.setEnabled(true);
        }else{
            this.txtNombreClienteVenta.setEnabled(true);
            this.txtPrecioVenta.setEnabled(true);
            this.btnNuevoClienteVenta.setEnabled(true);

            this.id_cliente = 0;
            this.txtNombreCliente.setEnabled(false);
            this.btnNuevoCliente.setEnabled(false);
            this.txtNombreCliente.getEditText().setText("");
            this.txtCelular.getEditText().setText("");
        }
    }

    private boolean validarCliente(){
        boolean valido = false;

        String val = txtNombreCliente.getEditText().getText().toString();

        if (val.isEmpty() & this.tipoSelected == 0) {
            txtNombreCliente.setError("Seleccione el Cliente");
            valido = false;
        } else {
            txtNombreCliente.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validarClienteVenta(){
        boolean valido = false;

        String val = txtNombreClienteVenta.getEditText().getText().toString();

        if(val.isEmpty() & this.tipoSelected == 1 & this.statusSelected == 9){
            txtNombreClienteVenta.setError("Seleccione el Cliente Venta");
            valido = false;
        }else{
            txtNombreClienteVenta.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validarEquipo(){
        boolean valido = false;

        String val = txtEquipo.getEditText().getText().toString();

        if(val.isEmpty()){
            txtEquipo.setError("Introduzca el Equipo");
            valido = false;
        }else{
            txtEquipo.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validarFalla(){
        boolean valido = false;

        String val = txtFalla.getEditText().getText().toString();

        if(val.isEmpty()){
            txtFalla.setError("Introduzca la Falla");
            valido = false;
        }else{
            txtFalla.setError(null);
            valido = true;
        }

        return valido;
    }

    private boolean validarStatus(){
        boolean valido = false;

        int status = statusSelected + 1;
        GlobalVariables vg = new GlobalVariables();

        if(vg.rol == 4 && (status == 6 | status == 7)){
            pDialogo = new SweetAlertDialog(NuevaOrdenServicio.this, SweetAlertDialog.ERROR_TYPE);
            pDialogo.setTitleText("Error");
            pDialogo.setContentText("No puede cambiar el estatus a Recibido o Devolución");
            pDialogo.show();
            valido = false;
        }else{
            valido = true;
        }

        return valido;
    }

    public void loadTecnicos(View view){

        Log.i("CargarTecnicos", "Se cargaran los tecnicos");
        GlobalVariables variablesGlobales = new GlobalVariables();
        String bd = variablesGlobales.bd;

        HTTP_URL = variablesGlobales.URLServicio + "obtenertecnicos.php?basedatos=" + bd;
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObjectTecnicos = response ;

                        // Calling method to parse JSON object.
                        new NuevaOrdenServicio.ParseJSonDataClassTecnicos(mView.getContext()).execute();

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

    public void loadPuntosVenta(View view){

        Log.i("CargarPuntosVentas", "Se cargaran los puntos de ventas");
        GlobalVariables variablesGlobales = new GlobalVariables();
        String bd = variablesGlobales.bd;

        HTTP_URL = variablesGlobales.URLServicio + "obtenerpuntosventa.php?basedatos=" + bd;
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObjectPuntosVentas = response ;

                        // Calling method to parse JSON object.
                        new NuevaOrdenServicio.ParseJSonDataClassPuntosVenta(mView.getContext()).execute();

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

    private void sendMessage(){
        checkSMSStatePermission();

        String estado =  "";

        if(statusSelected == 2){
            estado =  "Estimado "+ txtNombreCliente.getEditText().getText().toString() +" se le informa que el equipo: "+ txtEquipo.getEditText().getText().toString() + " modelo: "+ txtModelo.getEditText().getText().toString() + " folio: "+ txtFolio.getEditText().getText().toString() + " se encuentra " + spnStatus.getSelectedItem().toString() + " y el total de la reparación seria: $ " + txtPresupuesto.getEditText().getText().toString() + ", favor de confirmar por este medio.";
        }else if(statusSelected == 4) {
            estado = "Estimado " + txtNombreCliente.getEditText().getText().toString() + " se le informa que el equipo: " + txtEquipo.getEditText().getText().toString() + " modelo: " + txtModelo.getEditText().getText().toString() + " folio: "+ txtFolio.getEditText().getText().toString() + " se encuentra " + spnStatus.getSelectedItem().toString() + " puede pasar por el equipo en un horario de 9:00 am a 5:30 pm de Lunes a Viernes y 9:00 am a 2:00 pm los Sabados, despues de 30 dias no nos hacemos responsables de los equipos.";
        }else{
            estado =  "Estimado "+ txtNombreCliente.getEditText().getText().toString() +" se le informa que el equipo: "+ txtEquipo.getEditText().getText().toString() + " modelo: "+ txtModelo.getEditText().getText().toString() + " folio: "+ txtFolio.getEditText().getText().toString() + " se encuentra " + spnStatus.getSelectedItem().toString();
        }

        String phone = txtCelular.getEditText().getText().toString();
        if(phone != null || phone != ""){
            try{
                SmsManager sms = SmsManager.getDefault();
                ArrayList messageParts = sms.divideMessage(estado);
                sms.sendMultipartTextMessage(phone, null, messageParts , null, null);
                enviarMSMCliente = 0;
            }catch(Exception ex){

            }



        }
    }

    private void checkSMSStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para enviar SMS.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso para enviar SMS!");
        }
    }

    private void getCamara(int requestCode){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                if(requestCode == 1)
                    nombreImagen = photoFile.getName();
                if(requestCode == 2)
                    nombreImagenBack = photoFile.getName();
                if(requestCode == 3)
                    nombreImagenTecnico = photoFile.getName();
            } catch(Exception ex) {

            }
            if (photoFile != null) {
                output = FileProvider.getUriForFile(NuevaOrdenServicio.this,"com.eletronica.ordenesservicioapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                startActivityForResult(takePictureIntent, requestCode);
            }
        }

    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombre = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                nombre,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        //nombreImagen = image.getName();
        return image;
    }


    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1 || requestCode == 2 || requestCode == 3) && resultCode == RESULT_OK) {

            ContentResolver cr = this.getContentResolver();
            Bitmap bit = null;
            try {
                bit = android.provider.MediaStore.Images.Media.getBitmap(cr, output);
                int rotate = 0;
                ExifInterface exif = new ExifInterface(
                        file.getAbsolutePath());
                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }

                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bit = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(), bit.getHeight(), matrix, true);


            } catch (Exception e) {
                e.printStackTrace();
            }

            if(requestCode == 1){
                photobmp = bit;
                ivImagen.setImageBitmap(bit);
                reenviarImagen = 1;

            }

            if(requestCode == 2){
                photobmpBack = bit;
                ivImagenBack.setImageBitmap(bit);
                reenviarImagenBack = 1;

            }

            if(requestCode == 3){
                photobmpTecnico = bit;
                ivImagenTecnico.setImageBitmap(bit);
                reenviarImagenTecnico = 1;

            }


        }else{
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case REQUEST_DISPOSITIVO:


                        final String direccionDispositivo = data.getExtras().getString("DireccionDispositivo");
                        final String nombreDispositivo = data.getExtras().getString("NombreDispositivo");

                        // Obtenemos el dispositivo con la direccion seleccionada en la lista
                        dispositivoBluetooth = bluetoothAdapter.getRemoteDevice(direccionDispositivo);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // Conectamos los dispositivos

                                    // Creamos un socket
                                    bluetoothSocket = dispositivoBluetooth.createRfcommSocketToServiceRecord(aplicacionUUID);
                                    bluetoothSocket.connect();// conectamos el socket
                                    outputStream = bluetoothSocket.getOutputStream();
                                    inputStream = bluetoothSocket.getInputStream();

                                    //empezarEscucharDatos();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            conectada = true;
                                            Toast.makeText(getApplicationContext(), "Dispositivo Conectado", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } catch (IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            conectada = false;
                                            Toast.makeText(getApplicationContext(), "No se pudo conectar el dispositivo", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //Log.e(TAG_DEBUG, "Error al conectar el dispositivo bluetooth");
                                    conectada = false;
                                    //e.printStackTrace();
                                }
                            }
                        }).start();

                        break;

                }


            }
        }


    }

    public void setIconoLimpiar()
    {
        if(id_orden_servicio > 0){
            btnLimpiar.setImageResource(R.drawable.icono_delete);
            btnImprimirRecibo.setVisibility(View.VISIBLE);

        }else{
            btnLimpiar.setImageResource(R.drawable.paint_brush);
            btnImprimirRecibo.setVisibility(View.INVISIBLE);
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

    private void procesarGuardar(View view) {
        final View vista = view;
        pDialogo = new SweetAlertDialog(NuevaOrdenServicio.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Guardando...");
        pDialogo.setCancelable(false);
        pDialogo.show();



        GlobalVariables variablesGlobales = new GlobalVariables();
        final String bd = variablesGlobales.bd;
        final int id_empresa = variablesGlobales.id_empresa;
        final int id_usuario = variablesGlobales.id_usuario;

        HTTP_URL =variablesGlobales.URLServicio + "guardarordenservicio.php?";
        // Creating StringRequest and set the JSON server URL in here.
        stringRequest = new StringRequest(Request.Method.POST, HTTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // After done Loading store JSON response in FinalJSonObject string variable.
                FinalJSonObject = response ;

                // Calling method to parse JSON object.
                new NuevaOrdenServicio.ParseJSonDataClassGuardar(vista).execute();
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
                String parsedDate = "";
                String parsedDateEntrega = "";

                String newFecha = txtFecha.getEditText().getText().toString();
                String newFechaEntrega = txtFechaEntrega.getEditText().getText().toString();

                try {
                    Date initDate = new SimpleDateFormat("dd/mm/yyyy").parse(newFecha);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
                    parsedDate = formatter.format(initDate).toString();

                    Date initDateEntrega = new SimpleDateFormat("dd/mm/yyyy").parse(newFechaEntrega);
                    SimpleDateFormat formatterEntrega = new SimpleDateFormat("yyyy-mm-dd");
                    parsedDateEntrega = formatterEntrega.format(initDateEntrega).toString();

                } catch (ParseException ex) {

                }

                String id_orden = String.valueOf(id_orden_servicio);
                String id_cte = String.valueOf(id_cliente);
                String id_cte_vta = String.valueOf(id_cliente_venta);

                String nombre_cliente = txtNombreCliente.getEditText().getText().toString();
                String celular = txtCelular.getEditText().getText().toString();
                String fecha = parsedDate;
                String fechaEntrega = parsedDateEntrega;
                String nombre_equipo = txtEquipo.getEditText().getText().toString();
                String modelo_equipo = txtModelo.getEditText().getText().toString();
                String serie_equipo = txtSerie.getEditText().getText().toString();
                String descripcion_falla = txtFalla.getEditText().getText().toString();
                String nombre_cliente_venta = txtNombreClienteVenta.getEditText().getText().toString();

                String id_tecnico = String.valueOf(spinnerMap.get(txtTecnico.getSelectedItemPosition()));
                String descripcion_diagnostico = txtDiagnostico.getEditText().getText().toString();
                String descripcion_reparacion = txtReparacion.getEditText().getText().toString();
                String id_puntoventa = String.valueOf(spinnerMapPuntoVenta.get(txtPuntoVenta.getSelectedItemPosition()));
                String importe_presupuesto = "0";
                String precio_venta = "0";

                if(!txtPresupuesto.getEditText().getText().toString().isEmpty())
                    importe_presupuesto = txtPresupuesto.getEditText().getText().toString();

                if(!txtPrecioVenta.getEditText().getText().toString().isEmpty())
                    precio_venta = txtPrecioVenta.getEditText().getText().toString();

                String status_servicio = String.valueOf(statusSelected + 1);

                String tipo_servicio = String.valueOf(tipoSelected + 1);

                //ContentBody foto = new FileBody(file, "image/jpeg");

                String imagen = null;


                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_orden_servicio", id_orden);
                parametros.put("id_cliente", id_cte);
                parametros.put("nombre_cliente", nombre_cliente);
                parametros.put("celular", celular);
                parametros.put("fecha", fecha);
                parametros.put("fecha_entrega", fechaEntrega);
                parametros.put("nombre_equipo", nombre_equipo);
                parametros.put("modelo_equipo", modelo_equipo);
                parametros.put("serie_equipo", serie_equipo);
                parametros.put("descripcion_falla", descripcion_falla);
                parametros.put("id_tecnico", id_tecnico);
                parametros.put("descripcion_diagnostico", descripcion_diagnostico);
                parametros.put("descripcion_reparacion", descripcion_reparacion);
                parametros.put("importe_presupuesto", importe_presupuesto);
                parametros.put("status_servicio", status_servicio);

                parametros.put("tipo_servicio", tipo_servicio);
                parametros.put("id_cliente_venta", id_cte_vta);
                parametros.put("id_puntodeventa", id_puntoventa);
                parametros.put("precio_venta", precio_venta);

                if(reenviarImagen == 1 && photobmp != null){
                    imagen =  convertirImgString(photobmp);
                    parametros.put("nombre_imagen", nombreImagen);
                    parametros.put("imagen", imagen);
                }

                if(reenviarImagenBack == 1 && photobmpBack != null){
                    imagen =  convertirImgString(photobmpBack);
                    parametros.put("nombre_imagen_back", nombreImagenBack);
                    parametros.put("imagen_back", imagen);
                }

                if(reenviarImagenTecnico == 1 && photobmpTecnico != null){
                    imagen =  convertirImgString(photobmpTecnico);
                    parametros.put("nombre_imagen_tecnico", nombreImagenTecnico);
                    parametros.put("imagen_tecnico", imagen);
                }

                parametros.put("basedatos", bd);
                parametros.put("id_empresa", String.valueOf(id_empresa));
                parametros.put("id_usuario", String.valueOf(id_usuario));

                return parametros;
            }
        };


        // Creating String Request Object.
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

        // Passing String request into RequestQueue.
        requestQueue.add(stringRequest);


    }

    private String convertirImgString(Bitmap bitmap){
        String encodedImage = null;
        if(bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
        return encodedImage;
    }

    private void procesarEliminar(View view) {
        final View vista = view;
        pDialogo = new SweetAlertDialog(NuevaOrdenServicio.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Eliminando...");
        pDialogo.setCancelable(false);
        pDialogo.show();


        try {

            GlobalVariables variablesGlobales = new GlobalVariables();
            String bd = variablesGlobales.bd;

            HTTP_URL =variablesGlobales.URLServicio + "procesareliminar.php?id_orden_servicio=" + String.valueOf(id_orden_servicio)+"&basedatos=" + bd;
            // Creating StringRequest and set the JSON server URL in here.
            StringRequest stringRequest = new StringRequest(HTTP_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // After done Loading store JSON response in FinalJSonObject string variable.
                            FinalJSonObject = response ;

                            // Calling method to parse JSON object.
                            new NuevaOrdenServicio.ParseJSonDataClassEliminar(vista).execute();

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

    private class ParseJSonDataClassGuardar extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;

        // Creating List of Subject class.
        OrdenServicio orden;

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
                            orden = new OrdenServicio();

                            jsonObjectDatos = jsonArray.getJSONObject(0);


                            //Storing ID into subject list.
                            orden.id_orden_servicio = Integer.parseInt(jsonObjectDatos.getString("id_orden_servicio"));
                            orden.nombre_cliente = jsonObjectDatos.getString("nombre_cliente");
                            orden.fecha = jsonObjectDatos.getString("fecha");
                            orden.descripcion_falla = jsonObjectDatos.getString("descripcion_falla");
                            orden.nombre_equipo = jsonObjectDatos.getString("nombre_equipo");
                            orden.modelo_equipo = jsonObjectDatos.getString("modelo_equipo");
                            orden.serie_equipo = jsonObjectDatos.getString("serie_equipo");



                        }

                        /*runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(NuevaOrdenServicio.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                        */


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
            setValuesSafe(orden);
            //activarDesactivarBotones(btnGuardar, 1);

            SweetAlertDialog sDialogo = new SweetAlertDialog(NuevaOrdenServicio.this);
            sDialogo.setTitleText(this.msg);
            sDialogo.show();

            GlobalVariables vg = new GlobalVariables();

            if(vg.config_enviamensaje == 1 & enviarMSMCliente == 1)
                sendMessage();


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

                        /*runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(NuevaOrdenServicio.this,msg,Toast.LENGTH_SHORT).show();
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
            //activarDesactivarBotones(btnLimpiar, 1);
            SweetAlertDialog sDialogo = new SweetAlertDialog(NuevaOrdenServicio.this);
            sDialogo.setTitleText(this.msg);
            sDialogo.show();

        }
    }

    private class ParseJSonDataClassGetConfiguracion extends AsyncTask<Void, Void, Void> {

        public Context context;
        public View view;
        public String msg;
        public JSONObject jsonObject = null;
        // Creating List of Subject class.


        public ParseJSonDataClassGetConfiguracion(View view) {
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
                if (FinalJSonObjectConfiguracion != null) {

                    // Creating and setting up JSON array as null.
                    JSONArray jsonArray = null,jsonArrayDatos = null;
                    JSONObject jsonObjectDatos;
                    try {

                        jsonObject = new JSONObject(FinalJSonObjectConfiguracion);
                        jsonArray = jsonObject.getJSONArray("datos");
                        jsonObject = jsonArray.getJSONObject(0);

                        /*runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(NuevaOrdenServicio.this,msg,Toast.LENGTH_SHORT).show();
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
            Configuracion config = new Configuracion();
            config.setNombre_empresa(jsonObject.optString("nombre_empresa"));
            config.setDireccion(jsonObject.optString("direccion"));
            config.setTelefono(jsonObject.optString("telefono"));
            config.setLeyenda1(jsonObject.optString("leyenda1"));
            config.setLeyenda2(jsonObject.optString("leyenda2"));
            config.setImprimeticket(parseInt(jsonObject.optString("imprimeticket")));
            config.setEnviamensaje(parseInt(jsonObject.optString("enviamensaje")));

            GlobalVariables variablesGlobales = new GlobalVariables();

            variablesGlobales.config_nombre_empresa = config.getNombre_empresa();
            variablesGlobales.config_direccion = config.getDireccion();
            variablesGlobales.config_telefono = config.getTelefono();
            variablesGlobales.config_leyenda1 = config.getLeyenda1();
            variablesGlobales.config_leyenda2 = config.getLeyenda2();
            variablesGlobales.config_imprimeticket = config.getImprimeticket();
            variablesGlobales.config_enviamensaje = config.getEnviamensaje();

            Log.i("Configuracion", "Se cargo la configuracion");

            loadTecnicos(mView);

        }
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
                txtFecha.getEditText().setText(selectedDate);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    public void setValues(OrdenServicio orden) {
        ordenImpresion = orden;

        id_orden_servicio = orden.id_orden_servicio;
        id_cliente = orden.id_cliente;

        this.txtFolio.getEditText().setText(String.valueOf(orden.id_orden_servicio));
        this.txtNombreCliente.getEditText().setText(orden.nombre_cliente);
        this.txtCelular.getEditText().setText(orden.celular);
        this.txtFecha.getEditText().setText(orden.fecha);

        this.txtEquipo.getEditText().setText(orden.nombre_equipo);
        this.txtModelo.getEditText().setText(orden.modelo_equipo);
        this.txtSerie.getEditText().setText(orden.serie_equipo);
        this.txtFalla.getEditText().setText(orden.descripcion_falla);

        this.txtDiagnostico.getEditText().setText(orden.descripcion_diagnostico);
        this.txtReparacion.getEditText().setText(orden.descripcion_reparacion);
        this.txtPresupuesto.getEditText().setText(String.valueOf(orden.importe_presupuesto));

        if(orden.fecha_entrega != "null"){
            this.txtFechaEntrega.getEditText().setText(orden.fecha_entrega);
        }else{
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
            txtFechaEntrega.getEditText().setText(selectedDate);
        }


        if (orden.imagen != null) {
            byte[] encodeByte = (byte[]) orden.imagen;
            if(encodeByte.length > 0){
                photobmp = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                ivImagen.setImageBitmap(photobmp);
                nombreImagen = orden.nombre_imagen;
            }
        }

        if (orden.imagen_back != null) {
            byte[] encodeByteBack = (byte[]) orden.imagen_back;
            if(encodeByteBack.length > 0) {
                photobmpBack = BitmapFactory.decodeByteArray(encodeByteBack, 0, encodeByteBack.length);
                ivImagenBack.setImageBitmap(photobmpBack);
                nombreImagenBack = orden.nombre_imagen_back;
            }
        }

        if (orden.imagen_tecnico != null){
            byte[] encodeByteTecnico = (byte[]) orden.imagen_tecnico;
            if(encodeByteTecnico.length > 0) {
                photobmpTecnico = BitmapFactory.decodeByteArray(encodeByteTecnico, 0, encodeByteTecnico.length);
                ivImagenTecnico.setImageBitmap(photobmpTecnico);
                nombreImagenTecnico = orden.nombre_imagen_tecnico;
            }
        }

        spnStatus.setSelection(orden.status_servicio - 1);
        txtTecnico.setSelection(getIndexTecnico(orden.id_tecnico));
        statusSelected = orden.status_servicio - 1;
        tecnicoSelected = getIndexTecnico(orden.id_tecnico);

        id_cliente_venta = orden.id_cliente_venta;
        spnTipoOrden.setSelection(orden.tipo_servicio - 1);
        txtPuntoVenta.setSelection(getIndexPuntoVenta(orden.id_puntodeventa));
        tipoSelected = orden.tipo_servicio - 1;
        puntoventaSelected = getIndexPuntoVenta(orden.id_puntodeventa);
        this.txtPrecioVenta.getEditText().setText(String.valueOf(orden.precio_venta));
        this.txtNombreClienteVenta.getEditText().setText(orden.nombre_cliente_venta);

        validarSeleccionStatus(statusSelected);

        setIconoLimpiar();
    }

    private int getIndexTecnico(int value){
        int index = -1;
        for (int x = 0; x < tecnicosList.size(); x++) {
            Tecnico t = tecnicosList.get(x);
            if (t.getId_tecnico() == value) {
                index = x;
                break; // Terminar ciclo, pues ya lo encontramos
            }
        }
        return index;
    }

    private int getIndexPuntoVenta(int value){
        int index = -1;
        for (int x = 0; x < puntosventasList.size(); x++) {
            PuntoVenta t = puntosventasList.get(x);
            if (t.getId_puntodeventa() == value) {
                index = x;
                break; // Terminar ciclo, pues ya lo encontramos
            }
        }
        return index;
    }

    public void setValuesSafe(OrdenServicio orden) {
        ordenImpresion = orden;
        id_orden_servicio = orden.id_orden_servicio;
        //id_cliente = orden.id_cliente;
        this.txtFolio.getEditText().setText(String.valueOf(orden.id_orden_servicio));

        setIconoLimpiar();
        reenviarImagen = 0;
        reenviarImagenBack = 0;
        reenviarImagenTecnico = 0;
    }

    public void limpiarCampos(View v){
        id_orden_servicio = 0;
        id_cliente = 0;
        this.txtNombreCliente.getEditText().setText("");
        this.txtCelular.getEditText().setText("");
        this.txtEquipo.getEditText().setText("");
        this.txtModelo.getEditText().setText("");
        this.txtSerie.getEditText().setText("");
        this.txtFalla.getEditText().setText("");
        //this.txtTecnico.getEditText().setText("");
        this.txtDiagnostico.getEditText().setText("");
        this.txtReparacion.getEditText().setText("");
        this.txtPresupuesto.getEditText().setText("");

        this.txtNombreClienteVenta.getEditText().setText("");
        this.txtPrecioVenta.getEditText().setText("");

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
        txtFecha.getEditText().setText(selectedDate);
        txtFechaEntrega.getEditText().setText(selectedDate);

        txtFolio.getEditText().setText(String.valueOf(id_orden_servicio));

        spnStatus.setSelection(0);
        txtTecnico.setSelection(0);
        spnTipoOrden.setSelection(0);

        statusSelected = 0;
        tecnicoSelected = 0;
        tipoSelected = 0;

        nombreImagen = null;
        photobmp = null;
        ivImagen.setImageDrawable(null);
        reenviarImagen = 0;

        nombreImagenBack = null;
        photobmpBack = null;
        ivImagenBack.setImageDrawable(null);
        reenviarImagenBack = 0;

        nombreImagenTecnico = null;
        photobmpTecnico = null;
        ivImagenTecnico.setImageDrawable(null);
        reenviarImagenTecnico = 0;

        setIconoLimpiar();

        enviarMSMCliente = 0;



    }

    public void eliminarOrden(View v){
        SweetAlertDialog sDialog = new SweetAlertDialog(NuevaOrdenServicio.this, SweetAlertDialog.WARNING_TYPE);
        sDialog.setTitleText("Desea Eliminar la Orden?");
        sDialog.setContentText("No se podra recuperar si es eliminada");
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

    public void setValuesDefault(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
        txtFecha.getEditText().setText(selectedDate);
        txtFechaEntrega.getEditText().setText(selectedDate);
        txtFolio.getEditText().setText(String.valueOf(id_orden_servicio));

        spnStatus.setSelection(0);
        txtTecnico.setSelection(0);

        spnTipoOrden.setSelection(0);
        txtPuntoVenta.setSelection(0);


        statusSelected = 0;
        tecnicoSelected = 0;
        reenviarImagen = 0;
        tipoSelected = 0;

        setIconoLimpiar();

        validarSeleccionStatus(statusSelected);
    }

    private void changeViewRol(){
       GlobalVariables variables = new GlobalVariables();

        this.txtFecha.setEnabled(false);
        this.txtNombreCliente.setEnabled(false);
        //this.txtCelular.setEnabled(false);
        this.txtEquipo.setEnabled(false);
        this.txtModelo.setEnabled(false);
        this.txtSerie.setEnabled(false);
        this.txtFalla.setEnabled(false);
        this.btnCamara.setEnabled(false);
        this.btnCamaraBack.setEnabled(false);
        this.btnCamaraTecnico.setEnabled(false);
        this.btnNuevoCliente.setEnabled(false);
        this.txtTecnico.setEnabled(false);
        this.txtDiagnostico.setEnabled(false);
        this.txtReparacion.setEnabled(false);

        this.txtPresupuesto.setEnabled(false);
        this.spnStatus.setEnabled(false);

        this.spnTipoOrden.setEnabled(false);


       if(variables.rol == 1 /*ADMINISTRADOR*/) {
           this.txtPresupuesto.setEnabled(true);
           this.spnStatus.setEnabled(true);
       }
       if(variables.rol == 3 /*VENDEDOR*/)
       {
           this.txtFecha.setEnabled(true);
           this.txtNombreCliente.setEnabled(true);
           //this.txtCelular.setEnabled(true);
           this.txtEquipo.setEnabled(true);
           this.txtModelo.setEnabled(true);
           this.txtSerie.setEnabled(true);
           this.txtFalla.setEnabled(true);
           this.btnCamara.setEnabled(true);
           this.btnCamaraBack.setEnabled(true);
           this.btnCamaraTecnico.setEnabled(true);
           this.btnNuevoCliente.setEnabled(true);
           this.spnTipoOrden.setEnabled(true);

       }
       if(variables.rol == 4 /*TECNICO*/)
       {
           this.txtTecnico.setEnabled(false);
           this.txtDiagnostico.setEnabled(true);
           this.txtReparacion.setEnabled(true);
           this.spnStatus.setEnabled(true);
           this.btnCamaraTecnico.setEnabled(true);
       }

       if(variables.rol == 2 /*SUPERVISOR*/ || variables.rol == 5 /*SUPER*/){
           this.txtFecha.setEnabled(true);
           this.txtNombreCliente.setEnabled(true);
           //this.txtCelular.setEnabled(true);
           this.txtEquipo.setEnabled(true);
           this.txtModelo.setEnabled(true);
           this.txtSerie.setEnabled(true);
           this.txtFalla.setEnabled(true);
           this.btnCamara.setEnabled(true);
           this.btnCamaraBack.setEnabled(true);
           this.btnCamaraTecnico.setEnabled(true);

           this.txtTecnico.setEnabled(true);
           this.txtDiagnostico.setEnabled(true);
           this.txtReparacion.setEnabled(true);

           this.txtPresupuesto.setEnabled(true);
           this.spnStatus.setEnabled(true);

           this.btnNuevoCliente.setEnabled(true);

           this.spnTipoOrden.setEnabled(true);
           this.txtNombreClienteVenta.setEnabled(true);
           this.txtPrecioVenta.setEnabled(true);
           this.btnNuevoClienteVenta.setEnabled(true);
       }

    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {

        public Context context;

        // Creating List of Subject class.
        OrdenServicio orden;

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



                            orden = new OrdenServicio();

                            jsonObject = jsonArray.getJSONObject(0);


                            //Storing ID into subject list.
                            orden.id_orden_servicio = Integer.parseInt(jsonObject.getString("id_orden_servicio"));
                            orden.id_cliente = Integer.parseInt(jsonObject.getString("id_cliente"));
                            orden.nombre_cliente = jsonObject.getString("nombre_cliente");
                            orden.celular = jsonObject.getString("celular");
                            orden.fecha = jsonObject.getString("fecha");
                            orden.fecha_entrega = jsonObject.getString("fecha_entrega");
                            orden.status_servicio = Integer.parseInt(jsonObject.getString("status_servicio"));
                            orden.status_servicio_descripcion = jsonObject.getString("status_servicio_descripcion");

                            orden.nombre_equipo = jsonObject.getString("nombre_equipo");
                            orden.modelo_equipo = jsonObject.getString("modelo_equipo");
                            orden.serie_equipo = jsonObject.getString("serie_equipo");
                            orden.descripcion_falla = jsonObject.getString("descripcion_falla");
                            orden.descripcion_diagnostico = jsonObject.getString("descripcion_diagnostico");
                            orden.ruta_imagen = jsonObject.getString("ruta_imagen");
                            orden.nombre_imagen = jsonObject.getString("nombre_imagen");
                            orden.imagen = Base64.decode(jsonObject.getString("imagen"), Base64.DEFAULT);
                            orden.nombre_imagen_back = jsonObject.getString("nombre_imagen_back");
                            orden.imagen_back = Base64.decode(jsonObject.getString("imagen_back"), Base64.DEFAULT);
                            orden.nombre_imagen_tecnico = jsonObject.getString("nombre_imagen_tecnico");
                            orden.imagen_tecnico = Base64.decode(jsonObject.getString("imagen_tecnico"), Base64.DEFAULT);
                            orden.nombre_tecnico = jsonObject.getString("nombre_tecnico");
                            orden.descripcion_reparacion = jsonObject.getString("descripcion_reparacion");
                            orden.importe_presupuesto = Double.parseDouble(jsonObject.getString("importe_presupuesto"));
                            orden.id_tecnico = Integer.parseInt(jsonObject.getString("id_tecnico"));

                            orden.id_cliente_venta = Integer.parseInt(jsonObject.getString("id_cliente_venta"));
                            orden.tipo_servicio = Integer.parseInt(jsonObject.getString("tipo_servicio"));
                            orden.id_puntodeventa = Integer.parseInt(jsonObject.getString("id_puntodeventa"));
                            orden.precio_venta = Double.parseDouble(jsonObject.getString("precio_venta"));
                            orden.nombre_cliente_venta = jsonObject.getString("nombre_cliente_venta");


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

          setValues(orden);

          pDialogo.dismiss();



        }
    }

    private class ParseJSonDataClassTecnicos extends AsyncTask<Void, Void, Void> {

        public Context context;

        // Creating List of Subject class.
        Tecnico tecnico;

        public ParseJSonDataClassTecnicos(Context context) {

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

                        for (int i = 0; i < jsonArray.length(); i++) {

                            tecnico = new Tecnico();

                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.
                            tecnico.setId_tecnico(Integer.parseInt(jsonObject.getString("id_usuario")));
                            tecnico.setNombre_tecnico(jsonObject.getString("nombre_usuario"));


                            // Adding subject list object into CustomSubjectNamesList.
                            tecnicosList.add(tecnico);
                        }

                        Log.v("DatosTecnicos",FinalJSonObjectTecnicos);

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
            populateSpinner();

            Log.i("TecnicosCargados","Se cargaron los tecnicos");

            loadPuntosVenta(mView);

        }
    }

    private class ParseJSonDataClassPuntosVenta extends AsyncTask<Void, Void, Void> {

        public Context context;

        // Creating List of Subject class.
        PuntoVenta puntoVenta;

        public ParseJSonDataClassPuntosVenta(Context context) {

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
                if (FinalJSonObjectPuntosVentas != null) {

                    // Creating and setting up JSON array as null.
                    JSONArray jsonArray = null;
                    try {

                        // Adding JSON response object into JSON array.
                        jsonArray = new JSONArray(FinalJSonObjectPuntosVentas);

                        // Creating JSON Object.
                        JSONObject jsonObject;

                        // Creating Subject class object.
                        PuntoVenta puntoVenta;

                        // Defining CustomSubjectNamesList AS Array List.
                        puntosventasList = new ArrayList<PuntoVenta>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            puntoVenta = new PuntoVenta();

                            jsonObject = jsonArray.getJSONObject(i);

                            //Storing ID into subject list.
                            puntoVenta.setId_puntodeventa(Integer.parseInt(jsonObject.getString("id_puntodeventa")));
                            puntoVenta.setDescripcion_puntodeventa(jsonObject.getString("descripcion_puntodeventa"));


                            // Adding subject list object into CustomSubjectNamesList.
                            puntosventasList.add(puntoVenta);
                        }

                        Log.v("DatosPuntosVentas", FinalJSonObjectPuntosVentas);

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

            populateSpinnerPuntosVenta ();
            Log.i("PuntosVentasCargados", "Se cargaron los puntos de ventas");

            if(id_orden_servicio > 0){
                loadOrden(mView);
                setIconoLimpiar();

            }else{
                setValuesDefault();
            }

            changeViewRol();





        }
    }

    private void populateSpinner() {
        //tecnico.setId_tecnico(Integer.parseInt(jsonObject.getString("id_usuario")));
        spinnerArray = new String[tecnicosList.size()];
        spinnerMap = new HashMap<Integer, Integer>();
        Tecnico tecnico;
        for (int i = 0; i < tecnicosList.size(); i++)
        {
            tecnico = new Tecnico();
            tecnico = tecnicosList.get(i);
            spinnerMap.put(i,tecnico.getId_tecnico());
            spinnerArray[i] = tecnico.getNombre_tecnico();
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(NuevaOrdenServicio.this,android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        txtTecnico.setAdapter(spinnerAdapter);
    }

    private void populateSpinnerPuntosVenta() {
        //tecnico.setId_tecnico(Integer.parseInt(jsonObject.getString("id_usuario")));
        spinnerArrayPuntoVenta = new String[puntosventasList.size()];
        spinnerMapPuntoVenta = new HashMap<Integer, Integer>();
        PuntoVenta puntoVenta;
        for (int i = 0; i < puntosventasList.size(); i++)
        {
            puntoVenta = new PuntoVenta();
            puntoVenta = puntosventasList.get(i);
            spinnerMapPuntoVenta.put(i,puntoVenta.getId_puntodeventa());
            spinnerArrayPuntoVenta[i] = puntoVenta.getDescripcion_puntodeventa();
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(NuevaOrdenServicio.this,android.R.layout.simple_spinner_dropdown_item, spinnerArrayPuntoVenta);
        txtPuntoVenta.setAdapter(spinnerAdapter);
    }

    public void loadOrden(View view){
        pDialogo = new SweetAlertDialog(NuevaOrdenServicio.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogo.setTitleText("Cargando, espere....");
        pDialogo.setCancelable(false);
        pDialogo.show();


        GlobalVariables variablesGlobales = new GlobalVariables();
        String bd = variablesGlobales.bd;

        HTTP_URL = variablesGlobales.URLServicio + "obtenerorden.php?id_orden_servicio="+id_orden_servicio+ "&basedatos=" + bd;
        // Creating StringRequest and set the JSON server URL in here.
        StringRequest stringRequest = new StringRequest(HTTP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // After done Loading store JSON response in FinalJSonObject string variable.
                        FinalJSonObject = response ;

                        // Calling method to parse JSON object.
                        new NuevaOrdenServicio.ParseJSonDataClass(mView.getContext()).execute();

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

    private void cerrarConexion() {
        try {
            if (bluetoothSocket != null) {
                if (outputStream != null) outputStream.close();
                pararLectura = true;
                if (inputStream != null) inputStream.close();
                bluetoothSocket.close();
                Toast.makeText(getApplicationContext(), "Conexion a impresora finalizada", Toast.LENGTH_SHORT).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void clickBuscarDispositivosSync() {
        // Cerramos la conexion antes de establecer otra
        cerrarConexion();

        Intent intentLista = new Intent(getApplicationContext(), ListaBluetoohtActivity.class);
        startActivityForResult(intentLista, REQUEST_DISPOSITIVO);
        //getParentFragment().startActivityForResult(new Intent(getActivity().getApplicationContext(), ListaBluetoohtActivity.class), REQUEST_DISPOSITIVO);

    }

    public static byte[] getByteString(String str, int bold, int font, int widthsize, int heigthsize) {

        if (str.length() == 0 | widthsize < 0 | widthsize > 3 | heigthsize < 0 | heigthsize > 3
                | font < 0 | font > 1)
            return null;

        byte[] strData = null;
        try {
            strData = str.getBytes("iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        byte[] command = new byte[strData.length + 9];

        byte[] intToWidth = {0x00, 0x10, 0x20, 0x30};//
        byte[] intToHeight = {0x00, 0x01, 0x02, 0x03};//

        command[0] = 27;// caracter ESC para darle comandos a la impresora
        command[1] = 69;
        command[2] = ((byte) bold);
        command[3] = 27;
        command[4] = 77;
        command[5] = ((byte) font);
        command[6] = 29;
        command[7] = 33;
        command[8] = (byte) (intToWidth[widthsize] + intToHeight[heigthsize]);

        System.arraycopy(strData, 0, command, 9, strData.length);
        return command;
    }

    private void imprimir() {
        //imprimir();
        if(!conectada) {
            clickBuscarDispositivosSync();
        }else {
            if (bluetoothSocket != null) {
                try {

                    outputStream.write(0x1C);
                    outputStream.write(0x2E); // Cancelamos el modo de caracteres chino (FS .)
                    outputStream.write(0x1B);
                    outputStream.write(0x74);
                    outputStream.write(0x10); // Seleccionamos los caracteres escape (ESC t n) - n = 16(0x10) para WPC1252

                    // Para que acepte caracteres espciales
                    //outputStream.write(getByteString(texto, negrita, fuente, ancho, alto));

                    GlobalVariables vg = new GlobalVariables();
                    String config_nombre_empresa = vg.config_nombre_empresa;
                    String config_direccion = vg.config_direccion;
                    String config_telefono = vg.config_telefono;
                    String config_leyenda1 = vg.config_leyenda1;
                    String config_leyenda2 = vg.config_leyenda2;

                    String nombre_cliente = ordenImpresion.nombre_cliente;
                    String fecha = ordenImpresion.fecha;
                    String falla = ordenImpresion.descripcion_falla;
                    String equipo = ordenImpresion.nombre_equipo;

                    String texto = "\n";
                    outputStream.write(ESC_ALIGN_CENTER);
                    texto += config_nombre_empresa + " \n";
                    texto += "Tel: " + config_telefono + " \n\n";
                    texto += "Recibo \n\n";
                    outputStream.write(getByteString(texto, 1, 1, 1, 1));
                    outputStream.write(ESC_ALIGN_LEFT);
                    texto = "Orden de servicio: " + String.valueOf(id_orden_servicio) + "\n";
                    texto += "Fecha: " + fecha + "\n";
                    texto += "Cliente: " + nombre_cliente + "\n";
                    texto += "Equipo: " + equipo + "\n";
                    texto += "Falla: " + falla + "\n\n";
                    outputStream.write(getByteString(texto, 0, 1, 0, 0));

                    Bitmap b = getCodigoQr();

                    try {

                        if(b!=null){
                            byte[] command = Utils.decodeBitmap(b);
                            outputStream.write(ESC_ALIGN_CENTER);
                            outputStream.write(command);
                            texto = "\n\n";
                            texto += config_leyenda1 + " \n\n";
                            texto += config_leyenda2 + " \n\n";
                            texto += "\n\n";
                            outputStream.write(getByteString(texto, 0, 1, 0, 0));
                            outputStream.write(ESC_ALIGN_LEFT);
                        }else{
                            Log.e("Print Photo error", "the file isn't exists");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("PrintTools", "the file isn't exists");
                    }






                } catch (IOException e) {


                    Toast.makeText(getApplicationContext(), "Error al interntar imprimir texto", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    conectada = false;
                    clickBuscarDispositivosSync();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Impresora no conectada", Toast.LENGTH_SHORT).show();


            }
        }

    }

}
