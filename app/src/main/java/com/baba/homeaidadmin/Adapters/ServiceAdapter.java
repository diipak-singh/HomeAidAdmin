package com.baba.homeaidadmin.Adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.baba.homeaidadmin.Activities.MainActivity;
import com.baba.homeaidadmin.Modals.ServiceDetails;
import com.baba.homeaidadmin.R;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.serviceViewHolder> {
    private ArrayList<ServiceDetails> serviceList;
    private MainActivity mainActivity;
    private Context ctx;
    public static SparseBooleanArray itemStateArray = new SparseBooleanArray();

    public ServiceAdapter(ArrayList<ServiceDetails> serviceList, Context ctx) {
        this.serviceList = serviceList;
        this.ctx = ctx;
        mainActivity = (MainActivity) ctx;
    }

    @NonNull
    @Override
    public serviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sevice_layout, parent, false);
        serviceViewHolder serviceViewHolder = new serviceViewHolder(view, mainActivity);

        return serviceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final serviceViewHolder holder, int position) {


        holder.sCategory.setText(serviceList.get(position).getItemCatg());
        holder.sName.setText(serviceList.get(position).getItemName());
        holder.sPrice.setText("Total Price: ₹" + serviceList.get(position).getItemPrice());
        holder.sOtherDetail.setText("Other Details: " + serviceList.get(position).getOtherDetail());
        holder.sExpectedService.setText("Expected on: " + serviceList.get(position).getServiceDay() + " at " + serviceList.get(position).getServiceTime());
        holder.sBookedOn.setText("Booked on: " + serviceList.get(position).getCurrentDay() + " at " + serviceList.get(position).getCurrentTime());
        holder.uName.setText(serviceList.get(position).getUserName());
        holder.uCity.setText(serviceList.get(position).getUserCity());
        holder.uPhone.setText(serviceList.get(position).getUserPhone());
        holder.uAddress.setText(serviceList.get(position).getUserAddress());

        if (!mainActivity.is_in_action_mode) {
            holder.checkBox.setVisibility(View.GONE);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
            //holder.checkBox.setChecked(false);
            if (!itemStateArray.get(position, false)) {
                holder.checkBox.setChecked(false);
            } else {
                holder.checkBox.setChecked(true);
            }
        }


    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class serviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView sCategory, sName, sPrice, sOtherDetail, sExpectedService, sBookedOn, uName, uCity, uPhone, uAddress;
        private CheckBox checkBox;
        MainActivity mainActivity;
        ConstraintLayout claout;

        public serviceViewHolder(@NonNull View itemView, MainActivity mainActivity) {
            super(itemView);

            sCategory = itemView.findViewById(R.id.textView_serviceCatg);
            sName = itemView.findViewById(R.id.textView_serviceItemname);
            sPrice = itemView.findViewById(R.id.textView_servicePrice);
            sOtherDetail = itemView.findViewById(R.id.textView_serviceAnyOtherDetail);
            sExpectedService = itemView.findViewById(R.id.textView_expectedService);
            sBookedOn = itemView.findViewById(R.id.textView_serviceBooked);
            checkBox = itemView.findViewById(R.id.checkBox);
            uName = itemView.findViewById(R.id.textView_Username);
            uCity = itemView.findViewById(R.id.textView_Usercity);
            uPhone = itemView.findViewById(R.id.textView_UserPhone);
            uAddress = itemView.findViewById(R.id.textView_UserAddress);
            claout = itemView.findViewById(R.id.cLayout);

            this.mainActivity = mainActivity;
            claout.setOnLongClickListener(mainActivity);
            checkBox.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();
            if (!itemStateArray.get(adapterPosition, false)) {
                checkBox.setChecked(true);
                itemStateArray.put(adapterPosition, true);
                mainActivity.testing(1, getAdapterPosition());
            } else {
                checkBox.setChecked(false);
                itemStateArray.put(adapterPosition, false);
                mainActivity.testing(0, getAdapterPosition());
            }

            // mainActivity.prepareSelection(v, getAdapterPosition());


        }
    }
}
