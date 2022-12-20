package com.midad_app_pos.adapter;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_app_pos.R;
import com.midad_app_pos.databinding.BluetoothRowBinding;
import com.midad_app_pos.uis.activity_add_printer.AddPrinterActivity;

import java.util.ArrayList;
import java.util.List;

public class DevicesBluetoothAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BluetoothDevice> list = new ArrayList<>();
    private Context context;

    public DevicesBluetoothAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BluetoothRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bluetooth_row, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myHolder.binding.setTitle(list.get(position).getName());
        myHolder.itemView.setOnClickListener(v -> {
            if (context instanceof AddPrinterActivity){
                AddPrinterActivity activity = (AddPrinterActivity) context;
                activity.selectBluetoothDevice(list.get(myHolder.getAdapterPosition()));
            }
        });

    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private BluetoothRowBinding binding;

        public Holder(@NonNull BluetoothRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<BluetoothDevice> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
