package com.android.ososassignment.adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import java.util.ArrayList;
import java.util.List;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MainViewHolder> {

    private Context context;
    private ArrayList<AllTitles> allTitlesList;

    public MainRecyclerAdapter(Context context, ArrayList<AllTitles> allTitles) {
        this.context = context;
        this.allTitlesList = allTitles;
    }

    @NonNull
    @Override
    public MainRecyclerAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(context).inflate(R.layout.main_recycler_layout,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainRecyclerAdapter.MainViewHolder holder, int position) {

        holder.titles.setText(allTitlesList.get(position).getTitle());
        holder.titleStr = allTitlesList.get(position).getTitle();

        holder.bind(allTitlesList.get(position), holder.titleStr);

        holder.databaseReference.child("Titles")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.allItems.clear();

                        for (DataSnapshot dataSnapshot: snapshot.child(holder.titleStr).child("Images").getChildren()) {
                            AllItems items = dataSnapshot.getValue(AllItems.class);
                            holder.allItems.add(items);
                        }
                        holder.adapter = new ItemRecyclerAdapter(context, holder.allItems);
                        holder.recyclerView.setAdapter(holder.adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return allTitlesList.size();
    }

    public final class MainViewHolder extends RecyclerView.ViewHolder {

        TextView titles;
        String titleStr, ID;
        Button button;
        ArrayList<AllItems> allItems;
        RecyclerView recyclerView;
        ItemRecyclerAdapter adapter;
        private DatabaseReference databaseReference;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            titles = itemView.findViewById(R.id.titlestxt);
            button = itemView.findViewById(R.id.chooseImage);
            recyclerView = itemView.findViewById(R.id.itemRecyclerMain);

            allItems = new ArrayList<AllItems>();

            ID = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(ID);


            recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);
        }

        private void bind(AllTitles titles, String title) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context,
                            MainActivity.class).putExtra("title", titleStr));
                }
            });
        }
    }
}
