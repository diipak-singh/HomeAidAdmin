package com.baba.homeaidadmin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.baba.homeaidadmin.Adapters.ServiceAdapter;
import com.baba.homeaidadmin.Modals.ServiceDetails;
import com.baba.homeaidadmin.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnLongClickListener {

    Toolbar toolbar;
    private TextView counter_text_view;
    private RecyclerView recyclerView;
    public boolean is_in_action_mode = false;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    DatabaseReference dref;
    public static ArrayList<ServiceDetails> selection_list = new ArrayList<>();
    final List<ServiceDetails> serviceDetails = new ArrayList<>();
    int counter = 0;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView_requests);

        toolbar = findViewById(R.id.include);
        setSupportActionBar(toolbar);
        counter_text_view = findViewById(R.id.counter_text);
        counter_text_view.setText("All Requests");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bot_home);

        dref = FirebaseDatabase.getInstance().getReference().child("service_requests");

        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Loading Service Details");
        pd.setCancelable(false);
        pd.show();

        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        getAllServices();



        /*try {
            Log.i("brown1","here");
            testSMS();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.bot_home:

                return true;

            case R.id.bot_allServices:
                Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.bot_allWorkers:
                Intent intent2 = new Intent(getApplicationContext(), WorkersActivity.class);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_send) {
            Intent intent = new Intent(getApplicationContext(), WorkersActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (item.getItemId() == android.R.id.home) {
            clearActionMode();
            adapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.item_admins) {
            Intent intent = new Intent(getApplicationContext(), RequestApprovalActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (item.getItemId() == R.id.item_completed) {
            Intent in2 = new Intent(getApplicationContext(), CompletedActivity.class);
            startActivity(in2);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onLongClick(View v) {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_action_mode);
        counter_text_view.setVisibility(View.VISIBLE);
        is_in_action_mode = true;
        adapter.notifyDataSetChanged();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

//    public void prepareSelection(View view, int position) {
//
//        if (((CheckBox) view).isChecked()) {
//            selection_list.add(serviceDetails.get(position));
//            counter = counter + 1;
//            updateCounter(counter);
//        } else {
//            selection_list.remove(serviceDetails.get(position));
//            counter = counter - 1;
//            updateCounter(counter);
//        }
//    }

    public void testing(int flag, int position) {
        if (flag == 1) {
            selection_list.add(serviceDetails.get(position));
            counter = counter + 1;
            updateCounter(counter);
        } else {
            selection_list.remove(serviceDetails.get(position));
            counter = counter - 1;
            updateCounter(counter);
        }
    }

    public void updateCounter(int counter) {
        if (counter == 0) {
            counter_text_view.setText("0 Selected");
        } else {
            counter_text_view.setText(counter + " Selected");
        }
    }

    public void clearActionMode() {
        is_in_action_mode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.main_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        counter_text_view.setText("All Requests");
        counter = 0;
        selection_list.clear();
        ServiceAdapter.itemStateArray.clear();
    }

    @Override
    public void onBackPressed() {

        if (is_in_action_mode) {
            clearActionMode();
            adapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }

    }

    private void getAllServices() {

        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pd.cancel();
                if (dataSnapshot.exists()) {
                    serviceDetails.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ServiceDetails sd = ds.getValue(ServiceDetails.class);
                        if (sd.getIsCompleted().matches("NO")) {
                            serviceDetails.add(new ServiceDetails(sd.getUserImg(), sd.getUserName(), sd.getUserPhone(),
                                    sd.getUserCity(), sd.getUserAddress(), sd.getItemImg(), sd.getItemName(),
                                    sd.getItemCatg(), sd.getItemPrice(), sd.getServiceDay(), sd.getServiceTime(),
                                    sd.getCurrentDay(), sd.getCurrentTime(), sd.getOtherDetail(), sd.getServiceId(),
                                    sd.getUserId(), sd.getPaymentStatus(), sd.getIsCompleted(), sd.getRating()));
                        }
                    }

                    Collections.reverse(serviceDetails);
                    adapter = new ServiceAdapter((ArrayList<ServiceDetails>) serviceDetails, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(MainActivity.this, "No Services booked yet", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pd.cancel();

            }
        });

    }
}
