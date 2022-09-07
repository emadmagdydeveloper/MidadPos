package com.midad_pos.uis.activity_pos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityPosBinding;
import com.midad_pos.databinding.ActivityStoreBinding;
import com.midad_pos.model.POSModel;
import com.midad_pos.model.UserModel;
import com.midad_pos.model.WereHouse;
import com.midad_pos.mvvm.SelectedPosMvvm;
import com.midad_pos.mvvm.SelectedWereHouseMvvm;
import com.midad_pos.share.Common;
import com.midad_pos.tags.Tags;
import com.midad_pos.uis.activity_base.BaseActivity;
import com.midad_pos.uis.activity_home.HomeActivity;

import java.util.List;

public class PosActivity extends BaseActivity {
    private SelectedPosMvvm mvvm;
    private ActivityPosBinding binding;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pos);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        userModel = (UserModel) intent.getSerializableExtra("data");

    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(SelectedPosMvvm.class);
        binding.setEnabled(false);
        setUpToolbar(binding.toolbarStore, getString(R.string.select_store), R.color.colorPrimary, R.color.white);

        if (mvvm.getPosName().getValue() != null) {

            binding.setTitle(mvvm.getPosName().getValue());
        }

        if (mvvm.getPosModel().getValue() != null) {
            binding.setEnabled(true);
        } else {
            binding.setEnabled(false);
        }
        binding.tvWereHouse.setOnClickListener(v -> {
            createPopupMenu(v,userModel.getData().getSelectedWereHouse().getPos());

        });
        binding.btnNext.setOnClickListener(v -> {
            userModel.getData().setSelectedPos(mvvm.getPosModel().getValue());

            if (userModel.getData().getSelectedWereHouse().getPos().size()>0){
                int count = 0;
                for (POSModel pos :userModel.getData().getSelectedWereHouse().getPos()){
                    if (!pos.isIs_login()){
                        count++;
                    }
                }
                if (count>0){
                    userModel.getData().setSelectedPos(mvvm.getPosModel().getValue());
                    navigateToHomeActivity(userModel);
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

    private void navigateToHomeActivity(UserModel userModel) {
        setUserModel(userModel);
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
        overridePendingTransition(0,0);
    }

    private void createPopupMenu(View view, List<POSModel> posModelList) {
        int pos = 0;
        PopupMenu popupMenu = new PopupMenu(this, view);
        for (POSModel posModel : posModelList) {
            popupMenu.getMenu().add(1,pos, 1, posModel.getTitle());
            pos +=1;
        }


        popupMenu.setOnMenuItemClickListener(item -> {
            int index = item.getItemId();
            POSModel posModel = userModel.getData().getSelectedWereHouse().getPos().get(index);
            mvvm.getPosModel().setValue(posModel);
            mvvm.getPosName().setValue(posModel.getTitle());
            binding.setTitle(posModel.getTitle());
            binding.setEnabled(true);
            return true;
        });

        popupMenu.show();
    }

}