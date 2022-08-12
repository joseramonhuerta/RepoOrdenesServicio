package com.eletronica.ordenesservicioapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapterPuntosVentas extends BaseAdapter
{
    Context context;

    List<PuntoVenta> TempPuntosVentasList;
    List<PuntoVenta> TempPuntosVentasListFilter;

    public ListViewAdapterPuntosVentas(List<PuntoVenta> listValue, Context context /*FragmentManager fm*/)
    {
        this.context = context;
        this.TempPuntosVentasList = listValue;

        this.TempPuntosVentasListFilter = new ArrayList<PuntoVenta>();
        this.TempPuntosVentasListFilter.addAll(TempPuntosVentasList);
    }

    @Override
    public int getCount()
    {
        return this.TempPuntosVentasList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.TempPuntosVentasList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewItemPuntoVenta viewItem = null;

        if(convertView == null)
        {
            viewItem = new ViewItemPuntoVenta();

            //LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            //convertView = layoutInfiater.inflate(R.layout.listview_items_lavadoras, parent, false);

            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.listview_items_puntoventas, null);

            viewItem.ivDescripcionPuntoVenta = (ImageView) convertView.findViewById(R.id.ivDescripcionPuntoVenta);
            viewItem.ivDescripcionPuntoVenta.setImageResource(R.drawable.adress);

            viewItem.txtDescripcionPuntoVenta = (TextView)convertView.findViewById(R.id.txtDescripcionPuntoVenta);



            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemPuntoVenta) convertView.getTag();
        }

        viewItem.txtDescripcionPuntoVenta.setText(TempPuntosVentasList.get(position).getDescripcion_puntodeventa());


        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        TempPuntosVentasList.clear();
        if (charText.length() == 0) {
            TempPuntosVentasList.addAll(TempPuntosVentasListFilter);
        }
        else
        {
            for (PuntoVenta wp : TempPuntosVentasListFilter) {
                if (wp.getDescripcion_puntodeventa().toLowerCase(Locale.getDefault()).contains(charText)){
                    TempPuntosVentasList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}

class ViewItemPuntoVenta
{
    TextView txtDescripcionPuntoVenta;

    ImageView ivDescripcionPuntoVenta;


}
