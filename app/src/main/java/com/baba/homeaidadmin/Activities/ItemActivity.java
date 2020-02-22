package com.baba.homeaidadmin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baba.homeaidadmin.Adapters.ItemAdapter;
import com.baba.homeaidadmin.Modals.ItemModel;
import com.baba.homeaidadmin.R;
import com.baba.homeaidadmin.Utils.RecyclerItemClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ItemActivity extends AppCompatActivity {

    DatabaseReference dref;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ItemModel> itemList = new ArrayList<>();

    private Dialog addItemDialog;
    private EditText itmname, itmprice;
    private Button addItem,updtItem;
    private TextView DialogHeader;
    Toolbar toolbar;
    String addr, itemIdToBeUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        recyclerView = findViewById(R.id.recyclerView3);
        FloatingActionButton fab = findViewById(R.id.fab3);

        Intent intent = getIntent();
        addr = intent.getStringExtra("address");

        toolbar = findViewById(R.id.include);
        setSupportActionBar(toolbar);

        TextView toolbarText = findViewById(R.id.counter_text);
        toolbarText.setText(addr);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        getItems();

        addItemDialog = new Dialog(ItemActivity.this);
        addItemDialog.setContentView(R.layout.add_item_layout);
        addItemDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itmname = addItemDialog.findViewById(R.id.editText_serviceName);
        itmprice = addItemDialog.findViewById(R.id.editText_servicePrice);
        addItem = addItemDialog.findViewById(R.id.button_addService);
        updtItem=addItemDialog.findViewById(R.id.button_updateService);

        DialogHeader=addItemDialog.findViewById(R.id.textView6_dialogHeader);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemDialog.show();
                updtItem.setVisibility(View.GONE);
                addItem.setVisibility(View.VISIBLE);
            }
        });

        updtItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateValueFinally();
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        String itemname=itemList.get(position).getItemname();
                        String itemprice=itemList.get(position).getItemprice();
                        String itemid=itemList.get(position).getItemid();

                        updateValue(itemid,itemname,itemprice);
                    }
                }));


    }

    private void getItems() {
        dref = FirebaseDatabase.getInstance().getReference().child("items/" + addr);

        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    itemList.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ItemModel im = ds.getValue(ItemModel.class);
                        itemList.add(new ItemModel(im.getItemname(), im.getItemprice(), im.getItemid()));
                    }
                    adapter = new ItemAdapter((ArrayList<ItemModel>) itemList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "No Items Added, Add some by pressing + button.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addNewItem() {
        DialogHeader.setText("Add a new Service.");

        dref = FirebaseDatabase.getInstance().getReference().child("items/" + addr);

        String itemname = itmname.getText().toString().trim();
        String itemprice = itmprice.getText().toString().trim();
        String itemid = dref.push().getKey();

        ItemModel im = new ItemModel(itemname, itemprice, itemid);
        dref.child(itemid).setValue(im).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    Toast.makeText(getApplicationContext(), "Service added successfully.", Toast.LENGTH_SHORT).show();
                    addItemDialog.dismiss();
                } else
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateValue(String item_id, String item_name, String item_price){
        DialogHeader.setText("Update a Service.");
        updtItem.setVisibility(View.VISIBLE);
        addItem.setVisibility(View.GONE);
        itmname.setText(item_name);
        itmprice.setText(item_price);
        itemIdToBeUpdated=item_id;
        addItemDialog.show();



    }

    private void updateValueFinally(){
        dref = FirebaseDatabase.getInstance().getReference().child("items/" + addr);
        String itemname = itmname.getText().toString().trim();
        String itemprice = itmprice.getText().toString().trim();
        String itemid = itemIdToBeUpdated;

        ItemModel im = new ItemModel(itemname, itemprice, itemid);
        dref.child(itemid).setValue(im).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    Toast.makeText(getApplicationContext(), "Service updated successfully.", Toast.LENGTH_SHORT).show();
                    addItemDialog.dismiss();
                } else
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                //finishAffinity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
