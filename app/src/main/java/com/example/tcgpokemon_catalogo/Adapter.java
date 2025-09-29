package com.example.tcgpokemon_catalogo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.tcgdex.sdk.models.CardResume;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<CardResume> list;
    private OnItemClickListener listener;

    @Override
    public int getItemCount() {
        return list.size();
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }
    public Adapter(List<CardResume> list){
        this.list = list;
    }
    public CardResume getCardAt(int position) {
        if (position >= 0 && position < list.size()) {
            return list.get(position);
        }
        return null;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View itemLista = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.layout_itens, parent, false);
        return new MyViewHolder(itemLista);

    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CardResume cardHolder = list.get(position);
        String imageUrl = cardHolder.getImage();
        String finalUrl;

        Log.d("TESTE", "Image URL: " + imageUrl);
        if (imageUrl != null && !imageUrl.isEmpty()) {

            finalUrl = imageUrl + "/high.webp";
        } else {
            finalUrl = null;
        }

        if (finalUrl == null || finalUrl.isEmpty()) {
            holder.img.setImageResource(R.drawable.ic_error_image);
            holder.name.setText(cardHolder.getName() );
            holder.localId.setText("ID: " + cardHolder.getId());
            return;
        }

        Glide.with(holder.itemView)
                .load(finalUrl)
                .error(R.drawable.ic_error_image)
                .into(holder.img);

        holder.name.setText(cardHolder.getName() + " (" + cardHolder.getLocalId() + ")");
        holder.localId.setText("ID: " + cardHolder.getId());
    }

    public void updateList(List<CardResume> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView img;
        TextView localId;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.pokemon_name_textview);
            img = itemView.findViewById(R.id.pokemon_image_view);
            localId = itemView.findViewById(R.id.pokemon_localId_textview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemLongClick(position);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }


    }

}
