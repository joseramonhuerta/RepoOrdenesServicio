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
import java.util.List;
import java.util.Locale;

public class ListViewAdapterCatalogoClientes extends BaseAdapter
{
    Context context;

    List<Cliente> TempClientesList;
    List<Cliente> TempClientesListFilter;
    Dialog customDialog = null;


    public ListViewAdapterCatalogoClientes(List<Cliente> listValue, Context context)
    {
        this.context = context;
        this.TempClientesList = listValue;

        this.TempClientesListFilter = new ArrayList<Cliente>();
        this.TempClientesListFilter.addAll(TempClientesList);


    }

    @Override
    public int getCount()
    {
        return this.TempClientesList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.TempClientesList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewItemCliente viewItem = null;





        if(convertView == null)
        {
            viewItem = new ViewItemCliente();

            LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.listview_items_cliente, parent, false);

            viewItem.txtNombreCliente = (TextView)convertView.findViewById(R.id.txtNombreClienteCatalogo);
            viewItem.txtCelular = (TextView)convertView.findViewById(R.id.txtCelularCatalogo);
            viewItem.txtDireccion = (TextView)convertView.findViewById(R.id.txtDireccionCatalogo);

            viewItem.ivNombreCliente = (ImageView) convertView.findViewById(R.id.ivNombreClienteCatalogo);
            viewItem.ivNombreCliente.setImageResource(R.drawable.client);
            viewItem.ivCelular= (ImageView) convertView.findViewById(R.id.ivCelularCatalogo);
            viewItem.ivCelular.setImageResource(R.drawable.smartphone);
            viewItem.ivDireccion = (ImageView) convertView.findViewById(R.id.ivDireccionCatalogo);
            viewItem.ivDireccion.setImageResource(R.drawable.adress);




            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemCliente) convertView.getTag();
        }


        viewItem.txtNombreCliente.setText(TempClientesList.get(position).getNombre_cliente());
        viewItem.txtCelular.setText(TempClientesList.get(position).getCelular());
        viewItem.txtDireccion.setText(TempClientesList.get(position).getDireccion());




        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        TempClientesList.clear();
        if (charText.length() == 0) {
            TempClientesList.addAll(TempClientesListFilter);
        }
        else
        {
            for (Cliente wp : TempClientesListFilter) {
                if (wp.getNombre_cliente().toLowerCase(Locale.getDefault()).contains(charText)){
                    TempClientesList.add(wp);
                }else if(wp.getCelular().toLowerCase(Locale.getDefault()).contains(charText)){
                    TempClientesList.add(wp);
                }else if(wp.getDireccion().toLowerCase(Locale.getDefault()).contains(charText)){
                    TempClientesList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}

class ViewItemCliente
{

    TextView txtNombreCliente;
    TextView txtCelular;
    TextView txtDireccion;



    ImageView ivNombreCliente;
    ImageView ivCelular;
    ImageView ivDireccion;


}
