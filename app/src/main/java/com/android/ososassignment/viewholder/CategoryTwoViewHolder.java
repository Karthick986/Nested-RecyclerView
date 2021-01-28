package com.android.ososassignment.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.ososassignment.R;

public class CategoryTwoViewHolder extends RecyclerView.ViewHolder {

    public ImageView item;

    public CategoryTwoViewHolder(@NonNull final View itemView) {
        super(itemView);
        item = itemView.findViewById(R.id.itemImage);
       // itemView.setOnClickListener(this);
    }
}
