package com.eletronica.ordenesservicioapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapterGastos extends BaseAdapter
{
    Context context;

    List<Gasto> TempGastosList;
    List<Gasto> TempGastosListFilter;
    Dialog customDialog = null;


    public ListViewAdapterGastos(List<Gasto> listValue, Context context)
    {
        this.context = context;
        this.TempGastosList = listValue;

        this.TempGastosListFilter = new ArrayList<Gasto>();
        this.TempGastosListFilter.addAll(TempGastosList);


    }

    @Override
    public int getCount()
    {
        return this.TempGastosList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.TempGastosList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewItemGasto viewItem = null;





        if(convertView == null)
        {
            viewItem = new ViewItemGasto();

            LayoutInflater layoutInfiater = LayoutInflater.from(context);//(LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInfiater.inflate(R.layout.listview_items_gastos, parent, false);

            viewItem.txtFolio = (TextView)convertView.findViewById(R.id.txtFolioGasto);
            viewItem.txtConcepto = (TextView)convertView.findViewById(R.id.txtConceptoGasto);
            viewItem.txtFecha = (TextView)convertView.findViewById(R.id.txtFechaGasto);
            viewItem.txtImporte = (TextView)convertView.findViewById(R.id.txtImporteGasto);
            viewItem.txtTipo = (TextView)convertView.findViewById(R.id.txtTipoGasto);
            viewItem.txtOrdenDescripcion = (TextView)convertView.findViewById(R.id.txtOrdenServicio);

            viewItem.layOrden = (LinearLayout)convertView.findViewById(R.id.layOrden);

            viewItem.ivFolio = (ImageView) convertView.findViewById(R.id.ivFolioGasto);
            viewItem.ivFolio.setImageResource(R.drawable.icono_ordenes);
            viewItem.ivConcepto = (ImageView) convertView.findViewById(R.id.ivConceptoGasto);
            viewItem.ivConcepto.setImageResource(R.drawable.message);
            viewItem.ivFecha= (ImageView) convertView.findViewById(R.id.ivFechaGasto);
            viewItem.ivFecha.setImageResource(R.drawable.calendar);
            viewItem.ivImporte = (ImageView) convertView.findViewById(R.id.ivImporteGasto);
            viewItem.ivImporte.setImageResource(R.drawable.dollar);
            viewItem.ivOrdenServicio = (ImageView) convertView.findViewById(R.id.ivOrdenServicio);
            viewItem.ivOrdenServicio.setImageResource(R.drawable.filtro_tecnico);

            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItemGasto) convertView.getTag();
        }

        viewItem.txtFolio.setText(String.valueOf(TempGastosList.get(position).getId_gasto()));
        viewItem.txtConcepto.setText(TempGastosList.get(position).getConcepto());
        viewItem.txtFecha.setText(TempGastosList.get(position).getFecha());
        viewItem.txtImporte.setText(String.valueOf(TempGastosList.get(position).getImporte()));
        viewItem.txtTipo.setText(TempGastosList.get(position).getTipo_descripcion());
        String orden = String.valueOf(TempGastosList.get(position).getId_orden_servicio()) + " (" + TempGastosList.get(position).getOrden_servicio_descripcion() + ")";
        viewItem.txtOrdenDescripcion.setText(orden);

        viewItem.layOrden.setVisibility(View.GONE);

        if(TempGastosList.get(position).getTipo() == 1){
            viewItem.txtTipo.setTextColor(Color.parseColor("#2ECCFA"));
            viewItem.layOrden.setVisibility(View.VISIBLE);
        }

        if(TempGastosList.get(position).getTipo() == 2){
            viewItem.txtTipo.setTextColor(Color.parseColor("#DF0101"));
        }

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        TempGastosList.clear();
        if (charText.length() == 0) {
            TempGastosList.addAll(TempGastosListFilter);
        }
        else
        {
            for (Gasto wp : TempGastosListFilter) {
                if (wp.getConcepto().toLowerCase(Locale.getDefault()).contains(charText)){
                    TempGastosList.add(wp);
                }else if(String.valueOf(wp.getId_gasto()).toLowerCase(Locale.getDefault()).contains(charText)){
                    TempGastosList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}

class ViewItemGasto
{
    TextView txtFolio;
    TextView txtConcepto;
    TextView txtFecha;
    TextView txtImporte;
    TextView txtTipo;
    TextView txtOrdenDescripcion;

    ImageView ivFolio;
    ImageView ivConcepto;
    ImageView ivFecha;
    ImageView ivImporte;
    ImageView ivOrdenServicio;

    LinearLayout layOrden;


}
