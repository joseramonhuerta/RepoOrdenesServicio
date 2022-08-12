   package com.eletronica.ordenesservicioapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ListViewAdapterOrdenes extends BaseAdapter
{
    Context context;

    List<OrdenServicio> TempOrdenesList;
    List<OrdenServicio> TempOrdenesListFilter;
    Dialog customDialog = null;


    public ListViewAdapterOrdenes(List<OrdenServicio> listValue, Context context)
    {
        this.context = context;
        this.TempOrdenesList = listValue;

        this.TempOrdenesListFilter = new ArrayList<OrdenServicio>();
        this.TempOrdenesListFilter.addAll(TempOrdenesList);


    }

    @Override
    public int getCount()
    {
        return this.TempOrdenesList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.TempOrdenesList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewItemOrden viewItem = null;





        if(convertView == null)
        {
            viewItem = new ViewItemOrden();

            LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.listview_items_ordenes, parent, false);

            viewItem.txtFolio = (TextView)convertView.findViewById(R.id.txtFolio);
            viewItem.txtNombreCliente = (TextView)convertView.findViewById(R.id.txtNombreCliente);
            viewItem.txtFecha = (TextView)convertView.findViewById(R.id.txtFecha);
            viewItem.txtObservaciones = (TextView)convertView.findViewById(R.id.txtObservaciones);
            viewItem.txtStatus = (TextView)convertView.findViewById(R.id.txtStatus);
            viewItem.txtEquipo = (TextView)convertView.findViewById(R.id.txtEquipo);

            viewItem.ivStatus = (ImageView) convertView.findViewById(R.id.ivStatus);
            viewItem.ivFolio = (ImageView) convertView.findViewById(R.id.ivFolio);
            viewItem.ivFolio.setImageResource(R.drawable.icono_ordenes);
            viewItem.ivNombreCliente = (ImageView) convertView.findViewById(R.id.ivNombreCliente);
            viewItem.ivNombreCliente.setImageResource(R.drawable.client);
            viewItem.ivFecha= (ImageView) convertView.findViewById(R.id.ivFecha);
            viewItem.ivFecha.setImageResource(R.drawable.calendar);
            viewItem.ivObservaciones = (ImageView) convertView.findViewById(R.id.ivObservaciones);
            viewItem.ivObservaciones.setImageResource(R.drawable.message);
            viewItem.ivEquipo = (ImageView) convertView.findViewById(R.id.ivEquipo);
            viewItem.ivEquipo.setImageResource(R.drawable.washer);



            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemOrden) convertView.getTag();
        }

        if(TempOrdenesList.get(position).status_servicio == 1)
            viewItem.ivStatus.setImageResource(R.drawable.icono_status_1);
        if(TempOrdenesList.get(position).status_servicio == 2)
            viewItem.ivStatus.setImageResource(R.drawable.icono_status_2);
        if(TempOrdenesList.get(position).status_servicio == 3)
            viewItem.ivStatus.setImageResource(R.drawable.icono_status_3);
        if(TempOrdenesList.get(position).status_servicio == 4)
            viewItem.ivStatus.setImageResource(R.drawable.icono_status_4);
        if(TempOrdenesList.get(position).status_servicio == 5)
            viewItem.ivStatus.setImageResource(R.drawable.icono_status_5);
        if(TempOrdenesList.get(position).status_servicio == 6)
            viewItem.ivStatus.setImageResource(R.drawable.icono_status_6);
        if(TempOrdenesList.get(position).status_servicio == 7)
            viewItem.ivStatus.setImageResource(R.drawable.icono_status_7);
        if(TempOrdenesList.get(position).status_servicio == 8)
            viewItem.ivStatus.setImageResource(R.drawable.icono_status_6);
        if(TempOrdenesList.get(position).status_servicio == 9)
            viewItem.ivStatus.setImageResource(R.drawable.icono_status_6);
        if(TempOrdenesList.get(position).status_servicio == 10)
            viewItem.ivStatus.setImageResource(R.drawable.icono_status_6);


        if(TempOrdenesList.get(position).status_servicio == 1)
            viewItem.txtStatus.setTextColor(Color.parseColor("#020202"));
        if(TempOrdenesList.get(position).status_servicio == 2)
            viewItem.txtStatus.setTextColor(Color.parseColor("#DCDC35"));
        if(TempOrdenesList.get(position).status_servicio == 3)
            viewItem.txtStatus.setTextColor(Color.parseColor("#2ECCFA"));
        if(TempOrdenesList.get(position).status_servicio == 4)
            viewItem.txtStatus.setTextColor(Color.parseColor("#E78902"));
        if(TempOrdenesList.get(position).status_servicio == 5)
            viewItem.txtStatus.setTextColor(Color.parseColor("#088A68"));
        if(TempOrdenesList.get(position).status_servicio == 6)
            viewItem.txtStatus.setTextColor(Color.parseColor("#0431B4"));
        if(TempOrdenesList.get(position).status_servicio == 7)
            viewItem.txtStatus.setTextColor(Color.parseColor("#DF0101" +
                    ""));
        if(TempOrdenesList.get(position).status_servicio == 8)
            viewItem.txtStatus.setTextColor(Color.parseColor("#0431B4"));
        if(TempOrdenesList.get(position).status_servicio == 9)
            viewItem.txtStatus.setTextColor(Color.parseColor("#0431B4"));
        if(TempOrdenesList.get(position).status_servicio == 10)
            viewItem.txtStatus.setTextColor(Color.parseColor("#0431B4"));

        viewItem.txtFolio.setText(String.valueOf(TempOrdenesList.get(position).id_orden_servicio));
        viewItem.txtNombreCliente.setText(TempOrdenesList.get(position).nombre_cliente);
        viewItem.txtFecha.setText(TempOrdenesList.get(position).fecha);
        viewItem.txtObservaciones.setText(TempOrdenesList.get(position).descripcion_falla);
        viewItem.txtStatus.setText(TempOrdenesList.get(position).status_servicio_descripcion);
        viewItem.txtEquipo.setText(TempOrdenesList.get(position).nombre_equipo);


        return convertView;
    }
    /*
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        TempOrdenesList.clear();
        if (charText.length() == 0) {
            TempOrdenesList.addAll(TempOrdenesListFilter);
        }
        else
        {
            for (OrdenServicio wp : TempOrdenesListFilter) {
                if (wp.nombre_cliente.toLowerCase(Locale.getDefault()).contains(charText)){
                    TempOrdenesList.add(wp);
                }else if(String.valueOf(wp.id_orden_servicio).toLowerCase(Locale.getDefault()).contains(charText)){
                    TempOrdenesList.add(wp);
                }else if(wp.descripcion_falla.toLowerCase(Locale.getDefault()).contains(charText)){
                    TempOrdenesList.add(wp);
                }else if(wp.status_servicio_descripcion.toLowerCase(Locale.getDefault()).contains(charText)){
                    TempOrdenesList.add(wp);
                }else if(wp.nombre_equipo.toLowerCase(Locale.getDefault()).contains(charText)){
                    TempOrdenesList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
    */
}

class ViewItemOrden
{
    TextView txtFolio;
    TextView txtNombreCliente;
    TextView txtFecha;
    TextView txtObservaciones;
    TextView txtStatus;
    TextView txtEquipo;

    ImageView ivStatus;
    ImageView ivFolio;
    ImageView ivNombreCliente;
    ImageView ivFecha;
    ImageView ivObservaciones;
    ImageView ivEquipo;

}
