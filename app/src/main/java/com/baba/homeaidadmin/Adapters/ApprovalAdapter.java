package com.baba.homeaidadmin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baba.homeaidadmin.Activities.RequestApprovalActivity;
import com.baba.homeaidadmin.Modals.WorkerDetails;
import com.baba.homeaidadmin.R;

import java.util.ArrayList;

public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalAdapter.approvalViewHolder> {
    private ArrayList<WorkerDetails> workerDetails;
    private RequestApprovalActivity requestApproval;
    private Context ctx;

    public ApprovalAdapter(ArrayList<WorkerDetails> workerDetails, Context ctx) {
        this.workerDetails = workerDetails;
        this.ctx = ctx;
        requestApproval = (RequestApprovalActivity) ctx;
    }

    @NonNull
    @Override
    public approvalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_approval_layout, parent, false);
        return new ApprovalAdapter.approvalViewHolder(view, requestApproval);
    }

    @Override
    public void onBindViewHolder(@NonNull approvalViewHolder holder, int position) {
        holder.wname.setText(workerDetails.get(position).getwName());
        holder.wphone.setText(workerDetails.get(position).getwPhone());
        holder.waddress.setText(workerDetails.get(position).getwAddress());
        holder.wtype.setText((workerDetails).get(position).getwType());

    }

    @Override
    public int getItemCount() {
        return workerDetails.size();
    }

    public static class approvalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView wname, wphone, waddress, wtype;
        private Button reqAccept, reqCancel;
        RequestApprovalActivity requestApproval;

        public approvalViewHolder(@NonNull View itemView, RequestApprovalActivity requestApproval) {
            super(itemView);
            wname = itemView.findViewById(R.id.textView_wname);
            wphone = itemView.findViewById(R.id.textView_wphone);
            waddress = itemView.findViewById(R.id.textView_wAddress);
            wtype = itemView.findViewById(R.id.textView_wType);

            reqAccept = itemView.findViewById(R.id.buttonReqAccept);
            reqCancel = itemView.findViewById(R.id.buttonReqCancel);

            this.requestApproval = requestApproval;
            reqAccept.setOnClickListener(this);
            reqCancel.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (reqAccept.isPressed()) {
                requestApproval.approveRequest(getAdapterPosition());
            }

            if (reqCancel.isPressed()) {
                requestApproval.cancelRequest(getAdapterPosition());
            }
        }
    }
}

