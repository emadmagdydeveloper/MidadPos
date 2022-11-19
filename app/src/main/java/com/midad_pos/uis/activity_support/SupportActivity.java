package com.midad_pos.uis.activity_support;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivitySupportBinding;
import com.midad_pos.mvvm.SupportMvvm;
import com.midad_pos.uis.activity_drawer_base.DrawerBaseActivity;
import com.midad_pos.uis.activity_web_view.WebViewActivity;

import io.reactivex.disposables.CompositeDisposable;

public class SupportActivity extends DrawerBaseActivity {
    private SupportMvvm mvvm;
    private ActivitySupportBinding binding;
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_support, null, false);
        setContentView(binding.getRoot());
        setUpDrawer(binding.toolbarSupport,true);
        updateSelectedPos(6);
        initView();

    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(SupportMvvm.class);
        baseMvvm.getOnPinSuccess().observe(this,aBoolean -> {
            mvvm.showPin = false;
        });
        if (mvvm.getPositions().getValue() != null) {
            updateSelections(mvvm.getPositions().getValue());

        }
        if (binding.flHelpLayout!=null&&binding.helpLayout!=null&&binding.helpLayout.arrowBack!=null){
            binding.helpLayout.setLang(getLang());
            binding.helpLayout.arrowBack.setOnClickListener(view -> binding.flHelpLayout.setVisibility(View.GONE));
        }

        if (binding.flLegalInformationLayout!=null&&binding.legalInformationLayout!=null&&binding.legalInformationLayout.arrowBack!=null){
            binding.legalInformationLayout.setLang(getLang());
            binding.legalInformationLayout.arrowBack.setOnClickListener(view -> binding.flLegalInformationLayout.setVisibility(View.GONE));
        }



        if (binding.flLegalInformationDetailsLand != null) {
            mvvm.getPositions().setValue(mvvm.getPositions().getValue() != -1 ? mvvm.getPositions().getValue() : 0);
            updateSelections(mvvm.getPositions().getValue() != -1 ? mvvm.getPositions().getValue() : 0);
        }


        binding.help.setOnClickListener(view -> {
            mvvm.getPositions().setValue(0);
            updateSelections(0);
        });

        binding.legalInformation.setOnClickListener(view -> {
            mvvm.getPositions().setValue(1);
            updateSelections(1);
        });

        if (binding.helpLayout!=null){
            binding.helpLayout.helpCenter.setOnClickListener(view -> {
                mvvm.forNavigation = true;

                Intent intent =new Intent(this, WebViewActivity.class);
                intent.putExtra("url","https://www.google.com");
                startActivity(intent);
            });
        }

        if (binding.helpLayout!=null){
            binding.helpLayout.community.setOnClickListener(view -> {
                mvvm.forNavigation = true;

                Intent intent =new Intent(this, WebViewActivity.class);
                intent.putExtra("url","https://www.google.com");
                startActivity(intent);
            });
        }


        if (binding.legalInformationLayout!=null){
            binding.legalInformationLayout.terms.setOnClickListener(view -> {
                mvvm.forNavigation = true;

                Intent intent =new Intent(this, WebViewActivity.class);
                intent.putExtra("url","https://www.google.com");
                startActivity(intent);
            });
        }

        if (binding.legalInformationLayout!=null){
            binding.legalInformationLayout.privacy.setOnClickListener(view -> {
                mvvm.forNavigation = true;

                Intent intent =new Intent(this, WebViewActivity.class);
                intent.putExtra("url","https://www.google.com");
                startActivity(intent);
            });
        }

        if (binding.helpDetailsLayout!=null){
            binding.helpDetailsLayout.helpCenter.setOnClickListener(view -> {
                mvvm.forNavigation = true;

                Intent intent =new Intent(this, WebViewActivity.class);
                intent.putExtra("url","https://www.google.com");
                startActivity(intent);
            });
        }

        if (binding.helpDetailsLayout!=null){
            binding.helpDetailsLayout.community.setOnClickListener(view -> {
                mvvm.forNavigation = true;

                Intent intent =new Intent(this, WebViewActivity.class);
                intent.putExtra("url","https://www.google.com");
                startActivity(intent);
            });
        }


        if (binding.LegalInformationDetailsLayout!=null){
            binding.LegalInformationDetailsLayout.terms.setOnClickListener(view -> {
                mvvm.forNavigation = true;

                Intent intent =new Intent(this, WebViewActivity.class);
                intent.putExtra("url","https://www.google.com");
                startActivity(intent);
            });
        }

        if (binding.LegalInformationDetailsLayout!=null){
            binding.LegalInformationDetailsLayout.privacy.setOnClickListener(view -> {
                mvvm.forNavigation = true;

                Intent intent =new Intent(this, WebViewActivity.class);
                intent.putExtra("url","https://www.google.com");
                startActivity(intent);
            });
        }



    }


    private void updateSelections(int pos) {
        if (pos == 0) {
            binding.help.setBackgroundResource(R.color.grey1);
            binding.legalInformation.setBackgroundResource(R.color.transparent);


            if (binding.flHelpLayout != null && binding.flLegalInformationLayout != null ) {
                binding.flHelpLayout.setVisibility(View.VISIBLE);
                binding.flLegalInformationLayout.setVisibility(View.GONE);

                binding.help.setBackgroundResource(R.color.transparent);
                binding.legalInformation.setBackgroundResource(R.color.transparent);

            } else if (binding.flHelpDetailsLand != null && binding.flLegalInformationDetailsLand != null) {
                binding.flHelpDetailsLand.setVisibility(View.VISIBLE);
                binding.flLegalInformationDetailsLand.setVisibility(View.GONE);

                if (binding.iconHelp!=null&&binding.iconLegal!=null&&binding.tvHelp!=null&&binding.tvLegal!=null){
                    binding.iconHelp.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    binding.iconLegal.setColorFilter(getResources().getColor(R.color.grey4));

                    binding.tvHelp.setTextColor(getResources().getColor(R.color.colorPrimary));
                    binding.tvLegal.setTextColor(getResources().getColor(R.color.black));

                }
            }

        } else if (pos == 1) {
            binding.help.setBackgroundResource(R.color.transparent);
            binding.legalInformation.setBackgroundResource(R.color.grey1);

            if (binding.flHelpLayout != null && binding.flLegalInformationLayout != null ) {
                binding.flHelpLayout.setVisibility(View.GONE);
                binding.flLegalInformationLayout.setVisibility(View.VISIBLE);

                binding.help.setBackgroundResource(R.color.transparent);
                binding.legalInformation.setBackgroundResource(R.color.transparent);

            } else if (binding.flHelpDetailsLand != null && binding.flLegalInformationDetailsLand != null ) {
                binding.flHelpDetailsLand.setVisibility(View.GONE);
                binding.flLegalInformationDetailsLand.setVisibility(View.VISIBLE);

                if (binding.iconHelp!=null&&binding.iconLegal!=null&&binding.tvHelp!=null&&binding.tvLegal!=null){
                    binding.iconHelp.setColorFilter(getResources().getColor(R.color.grey4));
                    binding.iconLegal.setColorFilter(getResources().getColor(R.color.colorPrimary));

                    binding.tvHelp.setTextColor(getResources().getColor(R.color.black));
                    binding.tvLegal.setTextColor(getResources().getColor(R.color.colorPrimary));

                }
            }



        }


    }


    protected void onResume() {
        super.onResume();
        if (mvvm.showPin){
            showPinCodeView();
        }else {
            hidePinCodeView();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (mvvm.forNavigation){
            mvvm.showPin = false;

        }else {
            mvvm.showPin = true;

        }


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("pin",mvvm.showPin);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mvvm.showPin = savedInstanceState.getBoolean("pin");
        try {
            super.onRestoreInstanceState(savedInstanceState);

        } catch (Exception e) {
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public void onBackPressed() {
        if (binding.flHelpLayout!=null&&binding.flHelpLayout.getVisibility()==View.VISIBLE){
            binding.flHelpLayout.setVisibility(View.GONE);
        }else if (binding.flLegalInformationLayout!=null&&binding.flLegalInformationLayout.getVisibility()==View.VISIBLE){
            binding.flLegalInformationLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }


    }


}