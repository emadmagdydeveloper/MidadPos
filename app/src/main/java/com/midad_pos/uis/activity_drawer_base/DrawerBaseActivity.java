package com.midad_pos.uis.activity_drawer_base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.MessageQueue;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.navigation.NavigationView;
import com.midad_pos.R;
import com.midad_pos.databinding.ActivityDrawerBaseBinding;
import com.midad_pos.databinding.NavigationViewHeaderBinding;
import com.midad_pos.mvvm.BaseMvvm;
import com.midad_pos.share.App;
import com.midad_pos.uis.activity_base.BaseActivity;
import com.midad_pos.uis.activity_home.HomeActivity;
import com.midad_pos.uis.activity_items.ItemsActivity;
import com.midad_pos.uis.activity_receipts.ReceiptsActivity;
import com.midad_pos.uis.activity_settings.SettingsActivity;
import com.midad_pos.uis.activity_shift.ShiftActivity;
import com.midad_pos.uis.activity_support.SupportActivity;

import java.util.Objects;

public class DrawerBaseActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityDrawerBaseBinding binding;
    private NavigationViewHeaderBinding navigationViewHeaderBinding;
    private int itemId;
    private int selectedPos = 0;

    @Override
    public void setContentView(View view) {

        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_drawer_base, null, false);
        navigationViewHeaderBinding = DataBindingUtil.bind(binding.navigationView.getHeaderView(0));
        binding.frameLayoutContent.addView(view);
        super.setContentView(binding.getRoot());
        binding.navigationView.setNavigationItemSelectedListener(this);
        baseMvvm.getOnUserRefreshed().observe(this, aBoolean -> {
            navigationViewHeaderBinding.setModel(getUserModel());
        });
        if (getUserModel()!=null){
            baseMvvm.getPayments();

        }
        navigationViewHeaderBinding.lock.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            showPinCodeView();

        });

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        navigationViewHeaderBinding.setModel(getUserModel());


    }

    protected void setUpDrawer(Toolbar toolbar, boolean isNormal) {
        if (isNormal) {
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.open, R.string.close);
            toggle.syncState();
            toggle.setDrawerIndicatorEnabled(true);
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        } else {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(false);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, null, R.string.open, R.string.close);
            toggle.syncState();
            toggle.setDrawerIndicatorEnabled(false);
            binding.navigationView.setVisibility(View.GONE);
            toolbar.setNavigationIcon(null);
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        }

    }

    public void showShift(boolean visibility){
        binding.navigationView.getMenu().findItem(R.id.shift).setVisible(visibility);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        itemId = item.getItemId();

        if (item.getItemId() == R.id.sales) {
            if (selectedPos != 0) {
                App.navigate = true;
                /*App app = (App) getApplicationContext();
                app.killAllActivities();*/
                navigationDrawer(HomeActivity.class);


            }



        } else if (item.getItemId() == R.id.receipts) {
            App.navigate = true;

            if (selectedPos != 1) {
                navigationDrawer(ReceiptsActivity.class);
            }


        } else if (item.getItemId() == R.id.shift) {
            App.navigate = true;

            if (selectedPos != 2) {
                navigationDrawer(ShiftActivity.class);
            }


        } else if (item.getItemId() == R.id.items) {
            App.navigate = true;

            if (selectedPos != 3) {
                navigationDrawer(ItemsActivity.class);
            }


        } else if (item.getItemId() == R.id.settings) {
            App.navigate = true;

            if (selectedPos != 4) {
                navigationDrawer(SettingsActivity.class);
            }

        } else if (item.getItemId() == R.id.support) {
            App.navigate = true;

            if (selectedPos != 6) {
                navigationDrawer(SupportActivity.class);
            }


        } else if (item.getItemId() == R.id.backOffice) {
            App.navigate = true;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://midad-pos.wem-tech.site/login"));
            startActivity(intent);


        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    public void navigation(Class<?> activityClass) {
        App.navigate = true;
        Intent intent = new Intent(DrawerBaseActivity.this, activityClass);
        startActivity(intent);
        finishAffinity();
        overridePendingTransition(0, 0);


    }

    public void navigationDrawer(Class<?> activityClass) {
        binding.drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (activityClass!=null){
                    Intent intent = new Intent(DrawerBaseActivity.this, activityClass);
                    startActivity(intent);
                    finishAffinity();
                    overridePendingTransition(0, 0);
                }

            }
        });


    }

    public void updateSelectedPos(int pos) {
        this.selectedPos = pos;
        binding.navigationView.getMenu().getItem(pos).setChecked(true);

    }


    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        showPinCodeView();




    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        hidePinCodeView();

    }
}