package com.android.ososassignment.adapter;

import android.content.Context;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.ososassignment.MainActivity;
import com.android.ososassignment.R;
import com.android.ososassignment.model.AllItems;
import com.android.ososassignment.model.AllTitles;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ItemViewholder> {

    private Context context;
    private ArrayList<AllItems> allItemsList;

    public ItemRecyclerAdapter(Context context, ArrayList<AllItems> allItemsList) {
        this.context = context;
        this.allItemsList = allItemsList;
    }

    @NonNull
    @Override
    public ItemRecyclerAdapter.ItemViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewholder(LayoutInflater.from(context).inflate(R.layout.item_recycler_layout,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRecyclerAdapter.ItemViewholder holder, int position) {

        Picasso.get().load(allItemsList.get(position).getImageUrl())
                .placeholder(R.drawable.pholder).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return allItemsList.size();
    }

    public final class ItemViewholder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ItemViewholder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.itemImage);
        }
    }
}
