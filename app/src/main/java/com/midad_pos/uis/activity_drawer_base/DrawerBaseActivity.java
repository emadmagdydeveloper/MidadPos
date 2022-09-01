package com.midad_pos.uis.activity_drawer_base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.midad_pos.R;
import com.midad_pos.databinding.ActivityDrawerBaseBinding;
import com.midad_pos.share.App;
import com.midad_pos.uis.activity_base.BaseActivity;
import com.midad_pos.uis.activity_items.ItemsActivity;
import com.midad_pos.uis.activity_receipts.ReceiptsActivity;
import com.midad_pos.uis.activity_settings.SettingsActivity;
import com.midad_pos.uis.activity_shift.ShiftActivity;
import com.midad_pos.uis.activity_support.SupportActivity;

import java.util.Objects;

public class DrawerBaseActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityDrawerBaseBinding binding;
    private int selectedPos = 0;

    @Override
    public void setContentView(View view) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_drawer_base, null, false);
        binding.frameLayoutContent.addView(view);
        super.setContentView(binding.getRoot());
        binding.navigationView.setNavigationItemSelectedListener(this);

    }

    protected void setUpDrawer(Toolbar toolbar,boolean isNormal) {
        if (isNormal){
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.open, R.string.close);
            toggle.syncState();
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        }else {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, null, R.string.open, R.string.close);
            toggle.syncState();
            binding.navigationView.setVisibility(View.GONE);
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        }

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);

        if (item.getItemId() == R.id.sales) {
            if (selectedPos!=0){
                App app = (App) getApplicationContext();
                app.killAllActivities();
            }



        } else if (item.getItemId() == R.id.receipts) {
            if (selectedPos!=1){
                navigation(ReceiptsActivity.class);
            }

        }else if (item.getItemId() == R.id.shift) {
            if (selectedPos!=2){
                navigation(ShiftActivity.class);
            }

        }else if (item.getItemId() == R.id.items) {
            if (selectedPos!=3){
                navigation(ItemsActivity.class);
            }

        }else if (item.getItemId() == R.id.settings) {
            if (selectedPos!=4){
                navigation(SettingsActivity.class);
            }

        }else if (item.getItemId() == R.id.support) {
            if (selectedPos!=6){
                navigation(SupportActivity.class);
            }

        }else if (item.getItemId() == R.id.backOffice) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://midad-pos.wem-tech.site/login"));
            startActivity(intent);

        }
        return true;
    }

    public void navigation(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }



    public void updateSelectedPos(int pos) {
        this.selectedPos = pos;
        binding.navigationView.getMenu().getItem(pos).setChecked(true);
    }


    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();

        }
    }


}