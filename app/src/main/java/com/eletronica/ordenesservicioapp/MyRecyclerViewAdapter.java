package com.eletronica.ordenesservicioapp;


import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {
    private List<Tecnico> data;
    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

    public MyRecyclerViewAdapter(@NonNull List<Tecnico> data,
                                 @NonNull RecyclerViewOnItemClickListener
                                         recyclerViewOnItemClickListener) {
        this.data = data;
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_items_tecnicos, parent, false);
        return new MyViewHolder(row);
    }
    private final ArrayList selected = new ArrayList<>();
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Tecnico person = data.get(position);
        holder.getNameTextView().setText(person.getNombre_tecnico());
        //holder.getPersonImageView().setImageResource(R.drawable.selector_tecnicos);
        //set image with picasso.
        //permission required : android.permission.INTERNET
        //Picasso.get()
         //       .load(person.getUrlImage())
         //       .into(holder.getPersonImageView());

        if (!selected.contains(position)){
            holder.getPersonImageView().setImageResource(R.drawable.tecnic);
        } else
            holder.getPersonImageView().setImageResource(R.drawable.tecnic_selected);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {
        private TextView txtNombreTecnico;
        private ImageView ivImagenTecnico;


        public MyViewHolder(View itemView) {
            super(itemView);
            txtNombreTecnico = (TextView) itemView.findViewById(R.id.txtNombreTecnico);
            ivImagenTecnico = (ImageView) itemView.findViewById(R.id.ivImagenTecnico);
            itemView.setOnClickListener(this);
        }

        public TextView getNameTextView() {
            return txtNombreTecnico;
        }

        public ImageView getPersonImageView() {
            return ivImagenTecnico;
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            ivImagenTecnico = (ImageView) itemView.findViewById(R.id.ivImagenTecnico);
            ivImagenTecnico.setImageResource(R.drawable.tecnic_selected);
            recyclerViewOnItemClickListener.onClick(v, getAdapterPosition());

            if (selected.isEmpty()) {
                selected.add(position);
            } else {
                int oldSelected = (int) selected.get(0);
                selected.clear();
                selected.add(position);
                notifyItemChanged(oldSelected);
            }
            // we do not notify that an item has been selected
            // because that work is done here. we instead send
            // notifications for items to be deselected


        }

    }

}