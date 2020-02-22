package com.baba.homeaidadmin.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baba.homeaidadmin.Modals.CategoryModal;
import com.baba.homeaidadmin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.categoryViewHolder> {
    ArrayList<CategoryModal> catList;

    public CategoryAdapter(ArrayList<CategoryModal> catList) {
        this.catList = catList;
    }

    @NonNull
    @Override
    public categoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_layout, parent, false);
        return new categoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull categoryViewHolder holder, int position) {
        String name = catList.get(position).getCatName();
        String img = catList.get(position).getCatImg();
        holder.setName(name);
        holder.setcatImg(img);

    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    public static class categoryViewHolder extends RecyclerView.ViewHolder {
        private TextView cat_name;
        private ImageView catImg;

        public categoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cat_name = itemView.findViewById(R.id.textView_catName);
            catImg=itemView.findViewById(R.id.imageView4);
        }

        private void setName(String name) {
            cat_name.setText(name);
        }
        private void setcatImg(String img){
            Picasso.get().load(img).into(catImg);
        }
    }
}

