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

public class ListViewAdapterTecnicos extends BaseAdapter
{
    Context context;

    List<Tecnico> TempTecnicosList;



    public ListViewAdapterTecnicos(List<Tecnico> listValue, Context context)
    {
        this.context = context;
        this.TempTecnicosList = listValue;




    }

    @Override
    public int getCount()
    {
        return this.TempTecnicosList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.TempTecnicosList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewItemTecnico viewItem = null;





        if(convertView == null)
        {
            viewItem = new ViewItemTecnico();

            LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.listview_items_tecnicos, parent, false);


            viewItem.txtNombreTecnico = (TextView)convertView.findViewById(R.id.txtNombreTecnico);

            viewItem.ivImagenTecnico = (ImageView) convertView.findViewById(R.id.ivImagenTecnico);
            viewItem.ivImagenTecnico.setImageResource(R.drawable.filtro_tecnico);

            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemTecnico) convertView.getTag();
        }


        viewItem.txtNombreTecnico.setText(TempTecnicosList.get(position).getNombre_tecnico());





        return convertView;
    }



}

class ViewItemTecnico
{

    TextView txtNombreTecnico;


    ImageView ivImagenTecnico;


}
