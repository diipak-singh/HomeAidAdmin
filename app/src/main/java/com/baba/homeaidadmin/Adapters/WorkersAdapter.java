package com.baba.homeaidadmin.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baba.homeaidadmin.Modals.WorkerDetails;
import com.baba.homeaidadmin.R;

import java.util.ArrayList;

public class WorkersAdapter extends RecyclerView.Adapter<WorkersAdapter.approvalViewHolder> {
    private ArrayList<WorkerDetails> workerDetails;

    public WorkersAdapter(ArrayList<WorkerDetails> workerDetails) {
        this.workerDetails = workerDetails;

    }

    @NonNull
    @Override
    public approvalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.worker_layout, parent, false);
        return new WorkersAdapter.approvalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull approvalViewHolder holder, int position) {
        holder.wname.setText(workerDetails.get(position).getwName());
        holder.wphone.setText(workerDetails.get(position).getwPhone());
        holder.waddress.setText(workerDetails.get(position).getwAddress());
        holder.wtype.setText(workerDetails.get(position).getwType());

    }

    @Override
    public int getItemCount() {
        return workerDetails.size();
    }


    public static class approvalViewHolder extends RecyclerView.ViewHolder{
        private TextView wname, wphone, waddress,wtype;

        public approvalViewHolder(@NonNull View itemView) {
            super(itemView);
            wname = itemView.findViewById(R.id.textView_wname);
            wphone = itemView.findViewById(R.id.textView_wphone);
            waddress = itemView.findViewById(R.id.textView_wAddress);
            wtype=itemView.findViewById(R.id.textView_wType);
        }
    }
}
