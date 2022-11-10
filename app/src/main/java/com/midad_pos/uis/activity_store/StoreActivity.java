package com.midad_pos.uis.activity_store;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityStoreBinding;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.POSModel;
import com.midad_pos.model.UserModel;
import com.midad_pos.model.WereHouse;
import com.midad_pos.mvvm.SelectedWereHouseMvvm;
import com.midad_pos.share.Common;
import com.midad_pos.tags.Tags;
import com.midad_pos.uis.activity_base.BaseActivity;
import com.midad_pos.uis.activity_pos.PosActivity;

import java.util.List;
import java.util.Objects;

public class StoreActivity extends BaseActivity {
    private SelectedWereHouseMvvm mvvm;
    private ActivityStoreBinding binding;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_store);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        userModel = (UserModel) intent.getSerializableExtra("data");

    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(SelectedWereHouseMvvm.class);
        binding.setEnabled(false);
        setUpToolbar(binding.toolbarStore, getString(R.string.select_store), R.color.colorPrimary, R.color.white);

        if (mvvm.getWereHouseName().getValue() != null) {

            binding.setTitle(mvvm.getWereHouseName().getValue());
        }

        if (mvvm.getWereHouse().getValue() != null) {
            binding.setEnabled(true);
        } else {
            binding.setEnabled(false);
        }
        binding.tvWereHouse.setOnClickListener(v -> {
            createPopupMenu(v,userModel.getData().getUser().getWarehouses());

        });
        binding.btnNext.setOnClickListener(v -> {
            userModel.getData().setSelectedWereHouse(mvvm.getWereHouse().getValue());
            if (mvvm.getWereHouse().getValue()!=null&&mvvm.getWereHouse().getValue().getPos().size()>0){
                int count = 0;
                for (POSModel pos :mvvm.getWereHouse().getValue().getPos()){
                    if (!pos.isIs_login()){
                        count++;
                    }
                }
                if (count>0){
                    navigateToPosActivity(userModel);
                }else {
                    String title = "<font color ='#000000'>"+getString(R.string.no_pos)+" "+"</font><a href='"+ Tags.base_url +"' color='9F56DB'>"+getString(R.string.back_office)+"</a>";

                    Common.createAlertDialog(this,title,true);
                }
            }else {
                String title = "<font color ='#000000'>"+getString(R.string.no_pos)+" "+"</font><a href='"+Tags.base_url+"' color='9F56DB'>"+getString(R.string.back_office)+"</a>";

                Common.createAlertDialog(this,title,true);
            }
        });

    }

    private void navigateToPosActivity(UserModel userModel) {
        Intent intent = new Intent(this, PosActivity.class);
        intent.putExtra("data",userModel);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    private void createPopupMenu(View view, List<WereHouse> wereHouses) {
        int pos = 0;
        PopupMenu popupMenu = new PopupMenu(this, view);
        for (WereHouse wereHouse : wereHouses) {
            popupMenu.getMenu().add(1,pos, 1, wereHouse.getName());
            pos +=1;
        }


        popupMenu.setOnMenuItemClickListener(item -> {
            int index = item.getItemId();
            WereHouse wereHouse = userModel.getData().getUser().getWarehouses().get(index);
            mvvm.getWereHouse().setValue(wereHouse);
            mvvm.getWereHouseName().setValue(wereHouse.getName());
            binding.setTitle(wereHouse.getName());
            binding.setEnabled(true);
            return true;
        });

        popupMenu.show();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);

        }catch (Exception e){}
    }

}