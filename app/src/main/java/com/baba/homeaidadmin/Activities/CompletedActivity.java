package com.baba.homeaidadmin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.baba.homeaidadmin.Adapters.ServiceAdapter;
import com.baba.homeaidadmin.Adapters.ServiceCompletedAdapter;
import com.baba.homeaidadmin.Modals.ServiceDetails;
import com.baba.homeaidadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompletedActivity extends AppCompatActivity {
    Toolbar toolbar;
    private TextView counter_text_view;
    private RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    final List<ServiceDetails> serviceDetails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);
        recyclerView = findViewById(R.id.recyclerview_completed);

        toolbar = findViewById(R.id.include);
        setSupportActionBar(toolbar);
        counter_text_view = findViewById(R.id.counter_text);
        counter_text_view.setText("Completed Services");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        getCompletedServices();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCompletedServices() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("service_requests");

        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    serviceDetails.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ServiceDetails sd = ds.getValue(ServiceDetails.class);
                        if (sd.getIsCompleted().matches("Completed")) {
                            serviceDetails.add(new ServiceDetails(sd.getUserImg(), sd.getUserName(),
                                    sd.getUserPhone(), sd.getUserCity(), sd.getUserAddress(), sd.getItemImg(),
                                    sd.getItemName(), sd.getItemCatg(), sd.getItemPrice(), sd.getServiceDay(),
                                    sd.getServiceTime(), sd.getCurrentDay(), sd.getCurrentTime(), sd.getOtherDetail(),
                                    sd.getServiceId(), sd.getUserId(), sd.getPaymentStatus(), sd.getIsCompleted(), sd.getRating()));
                        }
                    }

                    Collections.reverse(serviceDetails);
                    adapter = new ServiceCompletedAdapter((ArrayList<ServiceDetails>) serviceDetails, CompletedActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(CompletedActivity.this, "No Services booked yet", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
