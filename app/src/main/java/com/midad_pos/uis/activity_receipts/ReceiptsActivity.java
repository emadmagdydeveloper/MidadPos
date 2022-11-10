package com.midad_pos.uis.activity_receipts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.midad_pos.R;
import com.midad_pos.adapter.ReceiptAdapter;
import com.midad_pos.databinding.ActivityReceiptsBinding;
import com.midad_pos.databinding.DialogSendEmailBinding;
import com.midad_pos.mvvm.ReceiptDetailsMvvm;
import com.midad_pos.uis.activity_drawer_base.DrawerBaseActivity;
import com.midad_pos.uis.activity_refund.RefundActivity;
import com.midad_pos.uis.activity_send_ticket_email.SendTicketEmailActivity;

public class ReceiptsActivity extends DrawerBaseActivity {
    private ReceiptDetailsMvvm mvvm;
    private ActivityReceiptsBinding binding;
    private ReceiptAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_receipts, null, false);
        setContentView(binding.getRoot());
        setUpDrawer(binding.toolbarReceiptsLayout.toolbarReceipts, true);
        updateSelectedPos(1);
        initView();
    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(ReceiptDetailsMvvm.class);
        binding.setLang(getLang());
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recView.setHasFixedSize(true);
        adapter = new ReceiptAdapter(this, getLang());
        binding.recView.setAdapter(adapter);

        mvvm.getIsLoading().observe(this,loading->binding.swipeRefresh.setRefreshing(loading));
        mvvm.getOrders().observe(this, list -> {
            if (adapter != null) {
                adapter.updateList(list);
                binding.recView.postDelayed(()->binding.recView.smoothScrollToPosition(0),100);
            }
        });
        
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        
        mvvm.getShowTicketDetails().observe(this, show -> {
            if (show) {
                if (binding.llReceiptDetailsPort != null) {
                    binding.llReceiptDetailsPort.setVisibility(View.VISIBLE);
                }

                if (binding.llReceiptDetails != null) {
                    binding.llReceiptDetails.setVisibility(View.VISIBLE);
                }
            } else {
                if (binding.llReceiptDetails != null) {
                    binding.llReceiptDetails.setVisibility(View.GONE);
                }
                if (binding.llReceiptDetailsPort != null) {
                    binding.llReceiptDetailsPort.setVisibility(View.GONE);
                }
            }

        });

        if (mvvm.getIsSearchOpen().getValue() != null && mvvm.getIsSearchOpen().getValue()) {
            binding.llSearch.setVisibility(View.VISIBLE);
            binding.edtSearch.setText(mvvm.getSearchQuery().getValue());

        }


        if (mvvm.getIsDialogShown().getValue() != null && mvvm.getIsDialogShown().getValue()) {
            createDialogEmail(mvvm.getShowSendEmailDialog().getValue());
        }
        if (binding.btnShowAllTickets != null) {
            binding.btnShowAllTickets.setOnClickListener(view -> {
                mvvm.getShowTicketDetails().setValue(true);

            });
        }

        if (binding.arrowBack != null) {
            binding.arrowBack.setOnClickListener(view -> {
                mvvm.getShowTicketDetails().setValue(false);

            });
        }

        if (binding.llReceiptDetailsLand != null) {
            mvvm.getShowTicketDetails().setValue(true);
        }

        binding.tvOpenSearch.setOnClickListener(view -> {
            binding.llSearch.setVisibility(View.VISIBLE);
            mvvm.getIsSearchOpen().setValue(true);
        });

        binding.closeSearch.setOnClickListener(view -> {
            if (mvvm.getSearchQuery().getValue() != null) {
                if (mvvm.getSearchQuery().getValue().isEmpty()) {
                    mvvm.getIsSearchOpen().setValue(false);
                    binding.llSearch.setVisibility(View.GONE);

                } else {
                    mvvm.getSearchQuery().setValue("");
                    binding.edtSearch.setText("");

                }
            }


        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String query = editable.toString().trim();
                mvvm.getSearchQuery().setValue(query);
                binding.edtSearch.removeTextChangedListener(this);
                mvvm.search();
                binding.edtSearch.addTextChangedListener(this);
            }
        });

        binding.btnRefund.setOnClickListener(view -> {
            Intent intent = new Intent(this, RefundActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        binding.toolbarLandMenu.setOnClickListener(this::createPopUpMenuSendTicket);

        binding.swipeRefresh.setOnRefreshListener(()->mvvm.getOrdersData());
    }

    @SuppressLint("RestrictedApi")
    private void createPopUpMenuSendTicket(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.send_ticket_email_menu);
        MenuPopupHelper menuHelper = new MenuPopupHelper(this, (MenuBuilder) popupMenu.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.sendEmail) {
                if (binding.llReceiptDetails != null) {
                    Intent intent = new Intent(this, SendTicketEmailActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else {
                    createDialogEmail("");
                }

            }
            return true;
        });

    }

    private void createDialogEmail(String email) {
        mvvm.getIsDialogShown().setValue(true);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        DialogSendEmailBinding dialogSendEmailBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_send_email, null, false);
        dialog.setView(dialogSendEmailBinding.getRoot());

        dialogSendEmailBinding.close.setOnClickListener(view -> {
            mvvm.getIsDialogShown().setValue(false);
            mvvm.getShowSendEmailDialog().setValue("");
            dialog.dismiss();
        });
        dialogSendEmailBinding.email.setText(email);

        dialogSendEmailBinding.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String mail = editable.toString().trim();
                mvvm.getShowSendEmailDialog().setValue(mail);
                dialogSendEmailBinding.setIsValid(Patterns.EMAIL_ADDRESS.matcher(mail).matches());

            }
        });

        dialog.show();
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);

        } catch (Exception e) {
        }
    }
}