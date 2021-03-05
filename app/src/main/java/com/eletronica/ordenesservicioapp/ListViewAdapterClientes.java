package com.eletronica.ordenesservicioapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapterClientes extends BaseAdapter
{
    Context context;

    List<Cliente> TempClientesList;
    List<Cliente> TempClientesListFilter;

    public ListViewAdapterClientes(List<Cliente> listValue, Context context /*FragmentManager fm*/)
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
        ViewItemClientes viewItem = null;

        if(convertView == null)
        {
            viewItem = new ViewItemClientes();

            //LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            //convertView = layoutInfiater.inflate(R.layout.listview_items_lavadoras, parent, false);

            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.listview_items_clientes, null);

            viewItem.ivNombreClienteBusqueda = (ImageView) convertView.findViewById(R.id.ivNombreClienteBusqueda);
            viewItem.ivNombreClienteBusqueda.setImageResource(R.drawable.client);

            viewItem.ivCelularBusqueda = (ImageView) convertView.findViewById(R.id.ivCelularBusqueda);
            viewItem.ivCelularBusqueda.setImageResource(R.drawable.smartphone);

            viewItem.txtNombreClienteBusqueda = (TextView)convertView.findViewById(R.id.txtNombreClienteBusqueda);
            viewItem.txtCelularBusqueda = (TextView)convertView.findViewById(R.id.txtCelularBusqueda);


            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemClientes) convertView.getTag();
        }

        viewItem.txtNombreClienteBusqueda.setText(TempClientesList.get(position).getNombre_cliente());
        viewItem.txtCelularBusqueda.setText(TempClientesList.get(position).getCelular());

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
                }
            }
        }
        notifyDataSetChanged();
    }
}

class ViewItemClientes
{
    TextView txtNombreClienteBusqueda;
    TextView txtCelularBusqueda;

    ImageView ivNombreClienteBusqueda;
    ImageView ivCelularBusqueda;

}
