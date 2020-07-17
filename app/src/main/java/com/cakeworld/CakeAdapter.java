package com.cakeworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cakeworld.Models.CakeModelClass;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class CakeAdapter extends RecyclerView.Adapter<CakeAdapter.CakeViewHolder>{

    Context context;
    List<CakeModelClass> cakeList;

    public CakeAdapter(Context context, List<CakeModelClass> cakeList) {
        this.context = context;
        this.cakeList = cakeList;
    }

    @NonNull
    @Override
    public CakeAdapter.CakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CakeViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_cake_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CakeAdapter.CakeViewHolder holder, int position) {
        holder.tvCakeName.setText(cakeList.get(position).getCakeName());
        holder.tvCakePrice.setText(cakeList.get(position).getCakePrice());
        holder.tvCakeDescription.setText(cakeList.get(position).getCakeDescription());
        if(cakeList.get(position).getCakePhoto().isEmpty()){
            Picasso.with(context).load("https://w7.pngwing.com/pngs/174/558/png-transparent-black-sad-emoji-illustration-face-sadness-smiley-computer-icons-sad-child-people-emoticon.png")
                    .into(holder.ivCakePhoto);
        }
        else{
            Picasso.with(context).load(cakeList.get(position).getCakePhoto()).into(holder.ivCakePhoto);
        }
    }

    @Override
    public int getItemCount() {
        return cakeList.size();
    }

    class CakeViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCakePhoto;
        TextView tvCakeName, tvCakePrice, tvCakeDescription;

        CakeViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCakePhoto = itemView.findViewById(R.id.cake_photo_iv);
            tvCakeName = itemView.findViewById(R.id.cake_name_tv);
            tvCakePrice = itemView.findViewById(R.id.cake_price_tv);
            tvCakeDescription = itemView.findViewById(R.id.cake_description_tv);
        }
    }

}