package com.android.ososassignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososassignment.model.AllItems;
import com.android.ososassignment.model.AllTitles;
import com.android.ososassignment.viewholder.CategoryTwoViewHolder;
import com.android.ososassignment.viewholder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainPage extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase database;
    DatabaseReference reference;
    String ID;
    public static final int PICK_IMAGE = 1;
    String titleName;
    public ArrayList<Uri> imageList;
    public Uri imageUri;
    public int uploadCount = 0;

    FirebaseRecyclerAdapter<AllTitles, CategoryViewHolder> adapter;
    FirebaseRecyclerAdapter<AllItems, CategoryTwoViewHolder> adapter2;
    RecyclerView.LayoutManager manager;

    ProgressBar progressBar;
    TextView noTitlestxt;
    FloatingActionButton btn_add_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        progressBar = findViewById(R.id.mainProgress);
        btn_add_item = findViewById(R.id.floatTitle);
        noTitlestxt = findViewById(R.id.noTitlestxt);
        imageList = new ArrayList<Uri>();
        
        btn_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTitle();
            }
        });

        checkConnection();

        ID = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        //firebase init
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(ID);

        manager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(manager);

        FirebaseRecyclerOptions<AllTitles> options = new FirebaseRecyclerOptions.Builder<AllTitles>()
                .setQuery(reference.child("Titles"), AllTitles.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<AllTitles, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull AllTitles titles) {

                 holder.titleName.setText(titles.getTitle());
                 titleName = titles.getTitle();

                holder.addItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        titleName = titles.getTitle();
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(intent, PICK_IMAGE);
                    }
                });

                FirebaseRecyclerOptions<AllItems> options2 = new FirebaseRecyclerOptions.Builder<AllItems>()
                        .setQuery(reference.child("Titles").child(titles.getTitle()).child("Images"), AllItems.class)
                        .build();

                adapter2 = new FirebaseRecyclerAdapter<AllItems, CategoryTwoViewHolder>(options2) {
                    @Override
                    protected void onBindViewHolder(@NonNull CategoryTwoViewHolder holder, int position, @NonNull AllItems items) {

                        Picasso.get().load(items.getImglink()).placeholder(R.drawable.pholder)
                                .into(holder.item);

                       // Toast.makeText(MainPage.this, items.getImglink(), Toast.LENGTH_SHORT).show();
                    }

                    @NonNull
                    @Override
                    public CategoryTwoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return new CategoryTwoViewHolder(LayoutInflater.from(getApplicationContext())
                                .inflate(R.layout.item_recycler_layout, parent, false));
                    }
                };
                adapter2.startListening();
                adapter2.notifyDataSetChanged();
                holder.category_recyclerView.setAdapter(adapter2);

                progressBar.setVisibility(View.GONE);
            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CategoryViewHolder(LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.main_recycler_layout, parent, false));
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    public void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (null == activeNetwork) {
            Toast.makeText(MainPage.this, "No Internet Connection!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void createTitle() {

        progressBar.setVisibility(View.GONE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Title");

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating title");
        progressDialog.setMessage("Please wait");

        LinearLayout linearLayout = new LinearLayout(this);
        final EditText title = new EditText(this);
        title.setHint("Enter Title name");
        title.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        title.setMinEms(15);

        linearLayout.addView(title);
        linearLayout.setPadding(10, 10, 10,10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String titletxt = title.getText().toString();

                if (titletxt.isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(MainPage.this, "Please enter title name",
                            Toast.LENGTH_SHORT).show();
                } else {
                    reference.child("Titles").child(title.getText().toString())
                            .child("title")
                            .setValue(title.getText().toString());
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                progressDialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {

                    int countClipData = data.getClipData().getItemCount();
                    int currentSelectImage = 0;
                    while (currentSelectImage < countClipData) {
                        imageUri = data.getClipData().getItemAt(currentSelectImage).getUri();
                        imageList.add(imageUri);

                        currentSelectImage = currentSelectImage +1;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("You have selected "+imageList.size()+ " images");
                    builder.setMessage("Please confirm to save");

                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Saving images");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);

                    LinearLayout linearLayout = new LinearLayout(this);
                    linearLayout.setPadding(10, 10, 10,10);
                    builder.setView(linearLayout);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.show();

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Images");

                            for (uploadCount=0; uploadCount<imageList.size(); uploadCount++) {
                               // progressDialog.setMessage("Please wait...("+(uploadCount+1)+"/"+imageList.size()+")");
                                Uri individualImage = imageList.get(uploadCount);

                                StorageReference imageName = storageReference.child(titleName).child("image-" +individualImage.getLastPathSegment());

                                imageName.putFile(individualImage).addOnSuccessListener(
                                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String url = String.valueOf(uri);

                                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssms");
                                                        String name = sdf.format(new Date());
                                                        HashMap<String, Object> saveMap = new HashMap<>();
                                                        saveMap.put("imgname", name);
                                                        saveMap.put("imglink", url);

                                                        reference.child("Titles").child(titleName).child("Images").child(name).setValue(saveMap);
                                                        Toast.makeText(MainPage.this, "Images added to " + titleName + "!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                progressDialog.dismiss();
                                            }
                                        }
                                );
                            }
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.dismiss();
                        }
                    });
                    builder.create().show();

                } else {
                    Toast.makeText(this, "Please select at least two images!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}