package com.baba.homeaidadmin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.baba.homeaidadmin.Adapters.ApprovalAdapter;
import com.baba.homeaidadmin.Modals.ServiceDetails;
import com.baba.homeaidadmin.Modals.WorkerDetails;
import com.baba.homeaidadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestApprovalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    DatabaseReference dref, databaseReference;
    ArrayList<WorkerDetails> workerDetails = new ArrayList<>();
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_approval);
        recyclerView = findViewById(R.id.recyclerView_approval);

        Toolbar toolbar = findViewById(R.id.include);
        setSupportActionBar(toolbar);
        TextView counter_text_view = findViewById(R.id.counter_text);
        counter_text_view.setText("Worker Approval");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dref = FirebaseDatabase.getInstance().getReference().child("approval_requests");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("worker_detail");
        user = FirebaseAuth.getInstance().getCurrentUser();

        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        getAllRequests();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

        }


        return super.onOptionsItemSelected(item);
    }

    private void getAllRequests() {
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workerDetails.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        WorkerDetails wd = ds.getValue(WorkerDetails.class);

                        workerDetails.add(new WorkerDetails(wd.getwName(), wd.getwPhone(), wd.getwType(), wd.getwAddress(), wd.getwId(), wd.getIsApproved()));

                    }
                    adapter = new ApprovalAdapter((ArrayList<WorkerDetails>) workerDetails, RequestApprovalActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void approveRequest(int position) {
        final String wId = workerDetails.get(position).getwId();

        dref.child(wId).child("isApproved").setValue("YES");
        databaseReference.child(wId).child("isApproved").setValue("YES").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    Toast.makeText(RequestApprovalActivity.this, "Worker Approved Successfully", Toast.LENGTH_SHORT).show();
                    dref.child(wId).removeValue();
                    adapter.notifyDataSetChanged();
                }
            }
        });


    }

    public void cancelRequest(int position) {
        final String wID = workerDetails.get(position).getwId();

        dref.child(wID).removeValue();
        databaseReference.child(wID).child("isApproved").setValue("NO").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    Toast.makeText(getApplicationContext(), "Request Cancelled Successfully.", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }

            }
        });

    }
}
