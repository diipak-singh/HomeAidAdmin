package com.baba.homeaidadmin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baba.homeaidadmin.Adapters.WorkersAdapter;
import com.baba.homeaidadmin.Modals.ServiceDetails;
import com.baba.homeaidadmin.Modals.WorkerDetails;
import com.baba.homeaidadmin.R;
import com.baba.homeaidadmin.Utils.RecyclerItemClickListener;
import com.baba.homeaidadmin.Utils.SendSms;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WorkersActivity extends AppCompatActivity {

    Toolbar toolbar;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    DatabaseReference dref, databaseReference;
    ArrayList<WorkerDetails> workerDetails = new ArrayList<>();
    final List<ServiceDetails> serviceDetails = new ArrayList<>();
    ProgressDialog pd;
    FirebaseUser user;
    DatabaseReference dref2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workers);
        recyclerView = findViewById(R.id.recyclerView_workers);

        toolbar = findViewById(R.id.include);
        setSupportActionBar(toolbar);
        TextView toolbarText = findViewById(R.id.counter_text);
        toolbarText.setText("All Workers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dref = FirebaseDatabase.getInstance().getReference().child("worker_detail");
        user = FirebaseAuth.getInstance().getCurrentUser();

        pd = new ProgressDialog(WorkersActivity.this);
        pd.setMessage("Loading All Workers");
        pd.setCancelable(false);
        pd.show();

        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        getWorkers();

        //TODo: AsyncTask ka use karle bhai, ye do line ek problem solve karke do khadi kar dengi.
        //TODo: Just fot Testing
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, final int position) {
                        String wid = workerDetails.get(position).getwId();
                        DatabaseReference dref1 = FirebaseDatabase.getInstance().getReference().child("myBookings");
                        dref2 = FirebaseDatabase.getInstance().getReference().child("admin2workers/" + wid);
                        for (final ServiceDetails sd : MainActivity.selection_list) {
                            //String id = dref2.push().getKey();
                            String sId = sd.getServiceId();
                            dref1.child(sId).child("assignedWid").setValue(wid);
                            ServiceDetails svd = new ServiceDetails(sd.getUserImg(), sd.getUserName(), sd.getUserPhone(),
                                    sd.getUserCity(), sd.getUserAddress(), sd.getItemImg(), sd.getItemName(), sd.getItemCatg(),
                                    sd.getItemPrice(), sd.getServiceDay(), sd.getServiceTime(), sd.getCurrentDay(), sd.getCurrentTime(),
                                    sd.getOtherDetail(), sd.getServiceId(), sd.getUserId(), sd.getPaymentStatus(), sd.getIsCompleted(),
                                    sd.getRating());

                            dref2.child(sId).setValue(svd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isComplete()) {
                                        Toast.makeText(WorkersActivity.this, "Sent Sucessfully", Toast.LENGTH_SHORT).show();
                                        String userP = sd.getUserPhone().substring(1);
                                        String workerP = workerDetails.get(position).getwPhone().substring(1);
                                        String usrMsg = "We received your request regarding " + sd.getItemName() + ".Our Agent would be reaching you soon. Thank you for using Helper At Home.";
                                        String workerMsg = "Reach " + sd.getUserAddress() + " regarding " + sd.getItemName() + ". Check your app for more details.";

                                        try {
                                            SendSMStoUser(usrMsg, userP, sd.getUserName());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            SendSMStoWorker(workerMsg, workerP, workerDetails.get(position).getwName());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });

                        }

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finishAffinity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getWorkers() {
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workerDetails.clear();
                if (dataSnapshot.exists()) {
                    pd.cancel();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        WorkerDetails wd = ds.getValue(WorkerDetails.class);
                        if (wd.getIsApproved().matches("YES")) {
                            workerDetails.add(new WorkerDetails(wd.getwName(), wd.getwPhone(), wd.getwType(), wd.getwAddress(), wd.getwId(), wd.getIsApproved()));
                        }

                    }
                    adapter = new WorkersAdapter((ArrayList<WorkerDetails>) workerDetails);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                pd.cancel();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendSMStoUser(String msg, String number, String name) throws JSONException {

        SendSms sms = new SendSms();
        String res = sms.SendSms(msg, number);

        JSONObject obj = new JSONObject(res);
        String status = obj.getString("status");

        if (status.matches("success")) {
            Toast.makeText(getApplicationContext(), "Text sent succesfully to " + name, Toast.LENGTH_SHORT).show();
        } else if (status.matches("failure")) {

            JSONArray arr = obj.getJSONArray("errors");
            for (int i = 0; i < arr.length(); i++) {
                String msg1 = arr.getJSONObject(i).getString("message");
                Toast.makeText(getApplicationContext(), msg1, Toast.LENGTH_LONG).show();
            }
        }


    }

    private void SendSMStoWorker(String msg, String number, String name) throws JSONException {

        SendSms sms = new SendSms();
        String res = sms.SendSms(msg, number);

        JSONObject obj = new JSONObject(res);
        String status = obj.getString("status");

        if (status.matches("success")) {
            Toast.makeText(getApplicationContext(), "Text sent succesfuly to " + name, Toast.LENGTH_SHORT).show();
        } else if (status.matches("failure")) {

            JSONArray arr = obj.getJSONArray("errors");
            for (int i = 0; i < arr.length(); i++) {
                String msg1 = arr.getJSONObject(i).getString("message");
                Toast.makeText(getApplicationContext(), msg1, Toast.LENGTH_LONG).show();
            }
        }


    }
}
