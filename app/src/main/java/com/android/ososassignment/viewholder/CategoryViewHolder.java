package com.android.ososassignment.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.ososassignment.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    public TextView titleName;
    public Button addItem;
    public RecyclerView category_recyclerView;
    public RecyclerView.LayoutManager manager;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);

        manager = new LinearLayoutManager(itemView.getContext(),LinearLayoutManager.HORIZONTAL,false);
        titleName = itemView.findViewById(R.id.titlestxt);
        addItem = itemView.findViewById(R.id.chooseImage);
        category_recyclerView = itemView.findViewById(R.id.itemRecyclerMain);
        category_recyclerView.setLayoutManager(manager);
    }
}
