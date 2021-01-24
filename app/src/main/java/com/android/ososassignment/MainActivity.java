package com.android.ososassignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.ososassignment.adapter.*;
import com.android.ososassignment.model.*;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    public ArrayList<Uri> imageList = new ArrayList<Uri>();
    public Uri imageUri;
    public int uploadCount = 0;
    public String titleName;

    RecyclerView recyclerView;
    TextView noTitlestxt;
    DatabaseReference databaseReference;
    MainRecyclerAdapter mainRecyclerAdapter;
    FloatingActionButton floatingActionButton;
    ArrayList<AllTitles> titlesArrayList;
    ProgressBar progressBar;
    String ID;
    List<AllTitles> titlesList;
    ArrayList<AllItems> itemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkConnection();

        titleName = getIntent().getStringExtra("title");
        if (titleName!=null) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_IMAGE);
        }

        recyclerView = findViewById(R.id.mainRecycler);
        floatingActionButton = findViewById(R.id.floatTitle);
        progressBar = findViewById(R.id.mainProgress);
        noTitlestxt = findViewById(R.id.noTitlestxt);

        titlesArrayList = new ArrayList<AllTitles>();

        ID = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(ID).child("id").setValue(ID);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTitle();
            }
        });

        titleRecycler();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void createTitle() {

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
                    Toast.makeText(MainActivity.this, "Please enter title name",
                            Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.child(ID).child("Titles").child(title.getText().toString())
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

    public void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (null == activeNetwork) {
            Toast.makeText(MainActivity.this, "No Internet Connection!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void titleRecycler() {
            databaseReference.child(ID).child("Titles").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    titlesArrayList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        AllTitles titles = dataSnapshot.getValue(AllTitles.class);
                        titlesArrayList.add(titles);
                    }
                    progressBar.setVisibility(View.GONE);
                    mainRecyclerAdapter = new MainRecyclerAdapter(MainActivity.this, titlesArrayList);
                    recyclerView.setAdapter(mainRecyclerAdapter);

                    if(titlesArrayList.isEmpty()){
                        noTitlestxt.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    else{
                        recyclerView.setVisibility(View.VISIBLE);
                        noTitlestxt.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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
                                //progressDialog.setMessage("Please wait...("+(uploadCount+1)+"/"+imageList.size()+")");
                                Uri individualImage = imageList.get(uploadCount);
                                StorageReference imageName = storageReference.child(titleName).child("Image"
                                        + individualImage.getLastPathSegment());

                                imageName.putFile(individualImage).addOnSuccessListener(
                                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String url = String.valueOf(uri);
                                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                                                .child(ID).child("Titles").child(titleName).child("Images");

                                                        HashMap<String, Object> saveMap = new HashMap<>();
                                                        saveMap.put("imglink", url);

                                                        databaseReference.push().setValue(saveMap);
                                                        progressDialog.dismiss();
                                                        Toast.makeText(MainActivity.this, "Images added to " + titleName + "!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}