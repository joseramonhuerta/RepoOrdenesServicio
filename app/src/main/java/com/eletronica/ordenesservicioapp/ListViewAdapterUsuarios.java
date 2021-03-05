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

public class ListViewAdapterUsuarios extends BaseAdapter
{
    Context context;

    List<User> TempUsuariosList;
    List<User> TempUsuariosListFilter;
    Dialog customDialog = null;


    public ListViewAdapterUsuarios(List<User> listValue, Context context)
    {
        this.context = context;
        this.TempUsuariosList = listValue;

        this.TempUsuariosListFilter = new ArrayList<User>();
        this.TempUsuariosListFilter.addAll(TempUsuariosList);


    }

    @Override
    public int getCount()
    {
        return this.TempUsuariosList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.TempUsuariosList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewItemUsuario viewItem = null;





        if(convertView == null)
        {
            viewItem = new ViewItemUsuario();

            LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.listview_items_usuario, parent, false);

            viewItem.txtNombreUsuario = (TextView)convertView.findViewById(R.id.txtNombreUsuarioCatalogo);
            viewItem.txtUsuario = (TextView)convertView.findViewById(R.id.txtUsuarioCatalogo);
            viewItem.txtRol = (TextView)convertView.findViewById(R.id.txtRolCatalogo);

            viewItem.ivNombreUsuario = (ImageView) convertView.findViewById(R.id.ivNombreUsuarioCatalogo);
            viewItem.ivNombreUsuario.setImageResource(R.drawable.client);
            viewItem.ivUsuario= (ImageView) convertView.findViewById(R.id.ivUsuarioCatalogo);
            viewItem.ivUsuario.setImageResource(R.drawable.userr);
            viewItem.ivRol = (ImageView) convertView.findViewById(R.id.ivRolCatalogo);
            viewItem.ivRol.setImageResource(R.drawable.role);

            viewItem.ivStatusUsuario = (ImageView) convertView.findViewById(R.id.ivStatusUsuario);




            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemUsuario) convertView.getTag();
        }


        viewItem.txtNombreUsuario.setText(TempUsuariosList.get(position).getNombre());
        viewItem.txtUsuario.setText(TempUsuariosList.get(position).getUsuario());
        viewItem.txtRol.setText(TempUsuariosList.get(position).getRol_descripcion());

        if(TempUsuariosList.get(position).getStatus() == 1)
            viewItem.ivStatusUsuario.setImageResource(R.drawable.toggle_on);
        else
            viewItem.ivStatusUsuario.setImageResource(R.drawable.toggle_off);


        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        TempUsuariosList.clear();
        if (charText.length() == 0) {
            TempUsuariosList.addAll(TempUsuariosListFilter);
        }
        else
        {
            for (User wp : TempUsuariosListFilter) {
                if (wp.getNombre().toLowerCase(Locale.getDefault()).contains(charText)){
                    TempUsuariosList.add(wp);
                }else if(wp.getUsuario().toLowerCase(Locale.getDefault()).contains(charText)){
                    TempUsuariosList.add(wp);
                }else if(wp.getRol_descripcion().toLowerCase(Locale.getDefault()).contains(charText)){
                    TempUsuariosList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}

class ViewItemUsuario
{

    TextView txtNombreUsuario;
    TextView txtUsuario;
    TextView txtRol;



    ImageView ivNombreUsuario;
    ImageView ivUsuario;
    ImageView ivRol;
    ImageView ivStatusUsuario;


}
