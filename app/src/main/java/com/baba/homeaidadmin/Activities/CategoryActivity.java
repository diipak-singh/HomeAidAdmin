package com.baba.homeaidadmin.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baba.homeaidadmin.Adapters.CategoryAdapter;
import com.baba.homeaidadmin.Modals.CategoryModal;
import com.baba.homeaidadmin.R;
import com.baba.homeaidadmin.Utils.CircleTransform;
import com.baba.homeaidadmin.Utils.RecyclerItemClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    DatabaseReference dref;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<CategoryModal> catList = new ArrayList<>();

    private Dialog addCategoryDialog;
    private EditText catName;

    private ImageView categoryImage;

    private static final int CHOOSE_IMAGE = 1;
    private Uri imageUrl;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    String ImageUrl = null;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        FloatingActionButton fab = findViewById(R.id.fab);

        toolbar = findViewById(R.id.include);
        setSupportActionBar(toolbar);
        TextView toolbarText = findViewById(R.id.counter_text);
        toolbarText.setText("All Services");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStorageRef = FirebaseStorage.getInstance().getReference("cat_images");

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        getCategories();

        addCategoryDialog = new Dialog(CategoryActivity.this);
        addCategoryDialog.setContentView(R.layout.add_cat_dialog);
        addCategoryDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        catName = addCategoryDialog.findViewById(R.id.editText_catName);
        Button addCat = addCategoryDialog.findViewById(R.id.button_addCat);
        categoryImage = addCategoryDialog.findViewById(R.id.imageView_catImage);


        addCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategoryDialog.show();
            }
        });

        categoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChoose();
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(CategoryActivity.this, SubCategoryActivity.class);
                        intent.putExtra("category", catList.get(position).getCatName());
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        String id=catList.get(position).getCatId();
                        String name=catList.get(position).getCatName();

                        deleteCategory(id,name);

                    }
                }));


    }

    private void getCategories() {
        dref = FirebaseDatabase.getInstance().getReference().child("categories");

        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    catList.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        CategoryModal cm = ds.getValue(CategoryModal.class);
                        catList.add(new CategoryModal(cm.getCatName(),cm.getCatImg(),cm.getCatId()));
                    }
                    adapter = new CategoryAdapter((ArrayList<CategoryModal>) catList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "No Categories Added, Add some by pressing + button.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showFileChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUrl = data.getData();

            Picasso.get().load(imageUrl).transform(new CircleTransform()).into(categoryImage);

        }
    }

    private void saveImage() {

        if (imageUrl != null) {

            String name = catName.getText().toString().trim();
            final StorageReference fileRefrence = mStorageRef.child(name + "." + getFileExtension(imageUrl));

            mUploadTask = fileRefrence.putFile(imageUrl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // uploadProgress.setProgress(0);
                                }
                            }, 500);

                            fileRefrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    ImageUrl = uri.toString();
                                    addNewCategory();
                                }
                            });


                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            // uploadProgress.setProgress((int) progress);
                        }
                    });

        } else {

        }

    }

    private void addNewCategory() {
        dref = FirebaseDatabase.getInstance().getReference().child("categories");
        String cat_name = catName.getText().toString().trim();
        String id = dref.push().getKey();

        CategoryModal cm = new CategoryModal(cat_name,ImageUrl,id);
        dref.child(id).setValue(cm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {

                    addCategoryDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Category Added Successfully", Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finishAffinity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteCategory(final String catId, String catName){
        dref = FirebaseDatabase.getInstance().getReference().child("categories");

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_delete_forever)
                .setTitle("Deleting "+catName)
                .setMessage("Are you sure, you want to delete this category?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dref.child(catId).removeValue();
                        Toast.makeText(CategoryActivity.this, "Category Deleted Successfully", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }
}
