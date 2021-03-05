package com.eletronica.ordenesservicioapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Calendar;

public class CuadroDialogoFechas extends DialogFragment {
    Context mContext;
    FragmentManager fm;
    Dialog dialogo = null;
    View mView;

    TextView txtFechaInicio;
    TextView txtFechaFin;



    public interface Actualizar {

        public void actualizaActividad(View view,String fechaInicio,String fechaFin);
    }

    Actualizar listener;
    Activity activity;

    public CuadroDialogoFechas(Context context, FragmentManager fm) {
        this.mContext = context;
        this.fm = fm;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (Actualizar) getActivity();
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString()
                    + " must implement Actualizar");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.cuadro_dialogo_fechas, null);



        dialogo = getDialog();
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setCancelable(false);
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        txtFechaInicio = (TextView) v.findViewById(R.id.txtFechaInicio);
        txtFechaFin = (TextView) v.findViewById(R.id.txtFechaFin);

        final Button btnAceptar = (Button) v.findViewById(R.id.btnAceptar);
        final Button btnSalir = (Button) v.findViewById(R.id.btnSalir);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;

        txtFechaInicio.setText(selectedDate);
        txtFechaFin.setText(selectedDate);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
                listener.actualizaActividad(mView,txtFechaInicio.getText().toString(), txtFechaFin.getText().toString());
            }
        });



        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();;
            }
        });

        txtFechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDatePickerDialog(v);

            }
        });

        txtFechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDatePickerDialog(v);

            }
        });

        return v;

    }

    private void showDatePickerDialog(View v) {
        final View view = v;
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
                //txtFecha.setText(selectedDate);
                switch (view.getId()) {
                    case R.id.txtFechaInicio:
                        txtFechaInicio.setText(selectedDate);
                        break;
                    case R.id.txtFechaFin:
                        txtFechaFin.setText(selectedDate);
                        break;
                }
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }




    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }
}
