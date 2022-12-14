package com.midad_app_pos.mvvm;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

//import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScanner;
import com.midad_app_pos.R;
import com.midad_app_pos.database.AppDatabase;
import com.midad_app_pos.database.DAO;
import com.midad_app_pos.model.AdvantageDataModel;
import com.midad_app_pos.model.AdvantageModel;
import com.midad_app_pos.model.AppSettingModel;
import com.midad_app_pos.model.CustomerDataModel;
import com.midad_app_pos.model.CustomerModel;
import com.midad_app_pos.model.DeliveryDataModel;
import com.midad_app_pos.model.DeliveryModel;
import com.midad_app_pos.model.HomeIndexModel;
import com.midad_app_pos.model.InvoiceIdsModel;
import com.midad_app_pos.model.InvoiceSettingData;
import com.midad_app_pos.model.InvoiceSettings;
import com.midad_app_pos.model.ModifierModel;
import com.midad_app_pos.model.OrderDataModel;
import com.midad_app_pos.model.OrderModel;
import com.midad_app_pos.model.PrinterModel;
import com.midad_app_pos.model.ShiftDataModel;
import com.midad_app_pos.model.SingleCustomerModel;
import com.midad_app_pos.model.StatusResponse;
import com.midad_app_pos.model.UnitModel;
import com.midad_app_pos.model.VariantModel;
import com.midad_app_pos.model.cart.CartList;
import com.midad_app_pos.model.cart.CartModel;
import com.midad_app_pos.model.cart.ManageCartModel;
import com.midad_app_pos.model.AddCustomerModel;
import com.midad_app_pos.model.CategoryDataModel;
import com.midad_app_pos.model.CategoryModel;
import com.midad_app_pos.model.CountryModel;
import com.midad_app_pos.model.DiscountDataModel;
import com.midad_app_pos.model.DiscountModel;
import com.midad_app_pos.model.ItemModel;
import com.midad_app_pos.model.ItemsDataModel;
import com.midad_app_pos.model.UserModel;
import com.midad_app_pos.preferences.Preferences;
import com.midad_app_pos.remote.Api;
import com.midad_app_pos.share.Common;
import com.midad_app_pos.tags.Tags;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class HomeMvvm extends AndroidViewModel {
    private final String TAG = HomeMvvm.class.getName();
    private MutableLiveData<List<CategoryModel>> categories;
    private MutableLiveData<CategoryModel> selectedCategory;
    private MutableLiveData<DeliveryModel> selectedDeliveryOptions;
    private MutableLiveData<Integer> ticketCount;
    private MutableLiveData<String> searchQuery;
    private MutableLiveData<Boolean> isOpenedCustomerDialog;
    private MutableLiveData<String> searchQueryCustomer;
    private MutableLiveData<List<CountryModel>> countriesData;
    private MutableLiveData<AddCustomerModel> addCustomerModel;
    private MutableLiveData<Boolean> isAddCustomerDialogShow;
    private MutableLiveData<Integer> countryPos;
    private MutableLiveData<Boolean> isScanOpened;
    private MutableLiveData<Integer> camera;
    private MutableLiveData<String> onError;
    private List<ItemModel> mainItemList = new ArrayList<>();
    private List<DiscountModel> mainDiscountList = new ArrayList<>();
    public MutableLiveData<List<DiscountModel>> mainItemDiscountList = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<List<ItemModel>> items;
    private UserModel userModel;
    private MutableLiveData<String> price;
    private MutableLiveData<Boolean> isDialogPriceOpened;
    private MutableLiveData<Boolean> isDialogExtrasOpened;
    private MutableLiveData<Boolean> isDialogDiscountsOpened;
    private MutableLiveData<Boolean> isDialogOpenedTicketsOpened;

    private MutableLiveData<Integer> itemForPricePos;
    private MutableLiveData<ItemModel> itemForPrice;

    private MutableLiveData<List<DiscountModel>> discounts;
    public boolean showPin = true;
    public boolean forNavigation = false;
    public boolean orientationChanged = false;

    private MutableLiveData<Boolean> isTicketModel;
    ///////////////////////////
    private List<CustomerModel> mainCustomerList = new ArrayList<>();
    private MutableLiveData<List<CustomerModel>> customers;
    private MutableLiveData<Boolean> isCustomerLoading;
    private MutableLiveData<Boolean> onCustomerUpdatedSuccess;
    private MutableLiveData<List<DeliveryModel>> deliveryOptions;

    /////////////////////////////////////////////////////////////////
    private MutableLiveData<Integer> ticketSortPos;
    private MutableLiveData<Boolean> isOpenedTicketSearchOpened;
    private MutableLiveData<String> queryMyOpenedTickets;
    private MutableLiveData<Boolean> isLoadingOpenedTickets;
    private MutableLiveData<List<OrderModel.Sale>> mainOrders;
    private MutableLiveData<List<OrderModel.Sale>> orders;
    private List<String> deletedSales = new ArrayList<>();
    private MutableLiveData<Boolean> isDeleteAllDraftTicketsSelected;
    private MutableLiveData<Boolean> canDeleteOpenedTickets;
    private MutableLiveData<AdvantageModel> advantage;
    private MutableLiveData<InvoiceSettings> invoiceSettings;

    //////////////////////////////////////////////////////////////////////////
    private ManageCartModel manageCartModel;
    private MutableLiveData<CartList> cart;
    public boolean isItemForUpdate = false;
    private MutableLiveData<List<DiscountModel>> cartDiscounts;
    private List<DiscountModel> deletedCartDiscounts = new ArrayList<>();
    private AppDatabase database;
    private DAO dao;
    private MutableLiveData<List<PrinterModel>> printers;

    private MutableLiveData<UserModel.Data> userModelData;

    ///////////////////////////////////////////////////////////////////////////
    private MutableLiveData<AppSettingModel> appSettingModel;

    private CompositeDisposable disposable = new CompositeDisposable();


    public HomeMvvm(@NonNull Application application) {
        super(application);
        manageCartModel = ManageCartModel.newInstance();
        database = AppDatabase.getInstance(application);
        dao = database.getDAO();
        userModel = Preferences.getInstance().getUserData(getApplication().getApplicationContext());
        CountryModel[] countries = new CountryModel[]{new CountryModel("", getApplication().getApplicationContext().getString(R.string.country), "", 0, ""), new CountryModel("AD", "Andorra", "+376", R.drawable.flag_ad, "EUR"), new CountryModel("AE", "United Arab Emirates", "+971", R.drawable.flag_ae, "AED"), new CountryModel("AF", "Afghanistan", "+93", R.drawable.flag_af, "AFN"), new CountryModel("AG", "Antigua and Barbuda", "+1", R.drawable.flag_ag, "XCD"), new CountryModel("AI", "Anguilla", "+1", R.drawable.flag_ai, "XCD"), new CountryModel("AL", "Albania", "+355", R.drawable.flag_al, "ALL"), new CountryModel("AM", "Armenia", "+374", R.drawable.flag_am, "AMD"), new CountryModel("AO", "Angola", "+244", R.drawable.flag_ao, "AOA"), new CountryModel("AQ", "Antarctica", "+672", R.drawable.flag_aq, "USD"), new CountryModel("AR", "Argentina", "+54", R.drawable.flag_ar, "ARS"), new CountryModel("AS", "American Samoa", "+1", R.drawable.flag_as, "USD"), new CountryModel("AT", "Austria", "+43", R.drawable.flag_at, "EUR"), new CountryModel("AU", "Australia", "+61", R.drawable.flag_au, "AUD"), new CountryModel("AW", "Aruba", "+297", R.drawable.flag_aw, "AWG"), new CountryModel("AX", "Aland Islands", "+358", R.drawable.flag_ax, "EUR"), new CountryModel("AZ", "Azerbaijan", "+994", R.drawable.flag_az, "AZN"), new CountryModel("BA", "Bosnia and Herzegovina", "+387", R.drawable.flag_ba, "BAM"), new CountryModel("BB", "Barbados", "+1", R.drawable.flag_bb, "BBD"), new CountryModel("BD", "Bangladesh", "+880", R.drawable.flag_bd, "BDT"), new CountryModel("BE", "Belgium", "+32", R.drawable.flag_be, "EUR"), new CountryModel("BF", "Burkina Faso", "+226", R.drawable.flag_bf, "XOF"), new CountryModel("BG", "Bulgaria", "+359", R.drawable.flag_bg, "BGN"), new CountryModel("BH", "Bahrain", "+973", R.drawable.flag_bh, "BHD"), new CountryModel("BI", "Burundi", "+257", R.drawable.flag_bi, "BIF"), new CountryModel("BJ", "Benin", "+229", R.drawable.flag_bj, "XOF"), new CountryModel("BL", "Saint Barthelemy", "+590", R.drawable.flag_bl, "EUR"), new CountryModel("BM", "Bermuda", "+1", R.drawable.flag_bm, "BMD"), new CountryModel("BN", "Brunei Darussalam", "+673", R.drawable.flag_bn, "BND"), new CountryModel("BO", "Bolivia, Plurinational State of", "+591", R.drawable.flag_bo, "BOB"), new CountryModel("BQ", "Bonaire", "+599", R.drawable.flag_bq, "USD"), new CountryModel("BR", "Brazil", "+55", R.drawable.flag_br, "BRL"), new CountryModel("BS", "Bahamas", "+1", R.drawable.flag_bs, "BSD"), new CountryModel("BT", "Bhutan", "+975", R.drawable.flag_bt, "BTN"), new CountryModel("BV", "Bouvet Island", "+47", R.drawable.flag_bv, "NOK"), new CountryModel("BW", "Botswana", "+267", R.drawable.flag_bw, "BWP"), new CountryModel("BY", "Belarus", "+375", R.drawable.flag_by, "BYR"), new CountryModel("BZ", "Belize", "+501", R.drawable.flag_bz, "BZD"), new CountryModel("CA", "Canada", "+1", R.drawable.flag_ca, "CAD"), new CountryModel("CC", "Cocos (Keeling) Islands", "+61", R.drawable.flag_cc, "AUD"), new CountryModel("CD", "Congo, The Democratic Republic of the", "+243", R.drawable.flag_cd, "CDF"), new CountryModel("CF", "Central African Republic", "+236", R.drawable.flag_cf, "XAF"), new CountryModel("CG", "Congo", "+242", R.drawable.flag_cg, "XAF"), new CountryModel("CH", "Switzerland", "+41", R.drawable.flag_ch, "CHF"), new CountryModel("CI", "Ivory Coast", "+225", R.drawable.flag_ci, "XOF"), new CountryModel("CK", "Cook Islands", "+682", R.drawable.flag_ck, "NZD"), new CountryModel("CL", "Chile", "+56", R.drawable.flag_cl, "CLP"), new CountryModel("CM", "Cameroon", "+237", R.drawable.flag_cm, "XAF"), new CountryModel("CN", "China", "+86", R.drawable.flag_cn, "CNY"), new CountryModel("CO", "Colombia", "+57", R.drawable.flag_co, "COP"), new CountryModel("CR", "Costa Rica", "+506", R.drawable.flag_cr, "CRC"), new CountryModel("CU", "Cuba", "+53", R.drawable.flag_cu, "CUP"), new CountryModel("CV", "Cape Verde", "+238", R.drawable.flag_cv, "CVE"), new CountryModel("CW", "Curacao", "+599", R.drawable.flag_cw, "ANG"), new CountryModel("CX", "Christmas Island", "+61", R.drawable.flag_cx, "AUD"), new CountryModel("CY", "Cyprus", "+357", R.drawable.flag_cy, "EUR"), new CountryModel("CZ", "Czech Republic", "+420", R.drawable.flag_cz, "CZK"), new CountryModel("DE", "Germany", "+49", R.drawable.flag_de, "EUR"), new CountryModel("DJ", "Djibouti", "+253", R.drawable.flag_dj, "DJF"), new CountryModel("DK", "Denmark", "+45", R.drawable.flag_dk, "DKK"), new CountryModel("DM", "Dominica", "+1", R.drawable.flag_dm, "XCD"), new CountryModel("DO", "Dominican Republic", "+1", R.drawable.flag_do, "DOP"), new CountryModel("DZ", "Algeria", "+213", R.drawable.flag_dz, "DZD"), new CountryModel("EC", "Ecuador", "+593", R.drawable.flag_ec, "USD"), new CountryModel("EE", "Estonia", "+372", R.drawable.flag_ee, "EUR"), new CountryModel("EG", "Egypt", "+20", R.drawable.flag_eg, "EGP"), new CountryModel("EH", "Western Sahara", "+212", R.drawable.flag_eh, "MAD"), new CountryModel("ER", "Eritrea", "+291", R.drawable.flag_er, "ERN"), new CountryModel("ES", "Spain", "+34", R.drawable.flag_es, "EUR"), new CountryModel("ET", "Ethiopia", "+251", R.drawable.flag_et, "ETB"), new CountryModel("FI", "Finland", "+358", R.drawable.flag_fi, "EUR"), new CountryModel("FJ", "Fiji", "+679", R.drawable.flag_fj, "FJD"), new CountryModel("FK", "Falkland Islands (Malvinas)", "+500", R.drawable.flag_fk, "FKP"), new CountryModel("FM", "Micronesia, Federated States of", "+691", R.drawable.flag_fm, "USD"), new CountryModel("FO", "Faroe Islands", "+298", R.drawable.flag_fo, "DKK"), new CountryModel("FR", "France", "+33", R.drawable.flag_fr, "EUR"), new CountryModel("GA", "Gabon", "+241", R.drawable.flag_ga, "XAF"), new CountryModel("GB", "United Kingdom", "+44", R.drawable.flag_gb, "GBP"), new CountryModel("GD", "Grenada", "+1", R.drawable.flag_gd, "XCD"), new CountryModel("GE", "Georgia", "+995", R.drawable.flag_ge, "GEL"), new CountryModel("GF", "French Guiana", "+594", R.drawable.flag_gf, "EUR"), new CountryModel("GG", "Guernsey", "+44", R.drawable.flag_gg, "GGP"), new CountryModel("GH", "Ghana", "+233", R.drawable.flag_gh, "GHS"), new CountryModel("GI", "Gibraltar", "+350", R.drawable.flag_gi, "GIP"), new CountryModel("GL", "Greenland", "+299", R.drawable.flag_gl, "DKK"), new CountryModel("GM", "Gambia", "+220", R.drawable.flag_gm, "GMD"), new CountryModel("GN", "Guinea", "+224", R.drawable.flag_gn, "GNF"), new CountryModel("GP", "Guadeloupe", "+590", R.drawable.flag_gp, "EUR"), new CountryModel("GQ", "Equatorial Guinea", "+240", R.drawable.flag_gq, "XAF"), new CountryModel("GR", "Greece", "+30", R.drawable.flag_gr, "EUR"), new CountryModel("GS", "South Georgia and the South Sandwich Islands", "+500", R.drawable.flag_gs, "GBP"), new CountryModel("GT", "Guatemala", "+502", R.drawable.flag_gt, "GTQ"), new CountryModel("GU", "Guam", "+1", R.drawable.flag_gu, "USD"), new CountryModel("GW", "Guinea-Bissau", "+245", R.drawable.flag_gw, "XOF"), new CountryModel("GY", "Guyana", "+595", R.drawable.flag_gy, "GYD"), new CountryModel("HK", "Hong Kong", "+852", R.drawable.flag_hk, "HKD"), new CountryModel("HM", "Heard Island and McDonald Islands", "+000", R.drawable.flag_hm, "AUD"), new CountryModel("HN", "Honduras", "+504", R.drawable.flag_hn, "HNL"), new CountryModel("HR", "Croatia", "+385", R.drawable.flag_hr, "HRK"), new CountryModel("HT", "Haiti", "+509", R.drawable.flag_ht, "HTG"), new CountryModel("HU", "Hungary", "+36", R.drawable.flag_hu, "HUF"), new CountryModel("ID", "Indonesia", "+62", R.drawable.flag_id, "IDR"), new CountryModel("IE", "Ireland", "+353", R.drawable.flag_ie, "EUR"), new CountryModel("IL", "Israel", "+972", R.drawable.flag_il, "ILS"), new CountryModel("IM", "Isle of Man", "+44", R.drawable.flag_im, "GBP"), new CountryModel("IN", "India", "+91", R.drawable.flag_in, "INR"), new CountryModel("IO", "British Indian Ocean Territory", "+246", R.drawable.flag_io, "USD"), new CountryModel("IQ", "Iraq", "+964", R.drawable.flag_iq, "IQD"), new CountryModel("IR", "Iran, Islamic Republic of", "+98", R.drawable.flag_ir, "IRR"), new CountryModel("IS", "Iceland", "+354", R.drawable.flag_is, "ISK"), new CountryModel("IT", "Italy", "+39", R.drawable.flag_it, "EUR"), new CountryModel("JE", "Jersey", "+44", R.drawable.flag_je, "JEP"), new CountryModel("JM", "Jamaica", "+1", R.drawable.flag_jm, "JMD"), new CountryModel("JO", "Jordan", "+962", R.drawable.flag_jo, "JOD"), new CountryModel("JP", "Japan", "+81", R.drawable.flag_jp, "JPY"), new CountryModel("KE", "Kenya", "+254", R.drawable.flag_ke, "KES"), new CountryModel("KG", "Kyrgyzstan", "+996", R.drawable.flag_kg, "KGS"), new CountryModel("KH", "Cambodia", "+855", R.drawable.flag_kh, "KHR"), new CountryModel("KI", "Kiribati", "+686", R.drawable.flag_ki, "AUD"), new CountryModel("KM", "Comoros", "+269", R.drawable.flag_km, "KMF"), new CountryModel("KN", "Saint Kitts and Nevis", "+1", R.drawable.flag_kn, "XCD"), new CountryModel("KP", "North Korea", "+850", R.drawable.flag_kp, "KPW"), new CountryModel("KR", "South Korea", "+82", R.drawable.flag_kr, "KRW"), new CountryModel("KW", "Kuwait", "+965", R.drawable.flag_kw, "KWD"), new CountryModel("KY", "Cayman Islands", "+345", R.drawable.flag_ky, "KYD"), new CountryModel("KZ", "Kazakhstan", "+7", R.drawable.flag_kz, "KZT"), new CountryModel("LA", "Lao People's Democratic Republic", "+856", R.drawable.flag_la, "LAK"), new CountryModel("LB", "Lebanon", "+961", R.drawable.flag_lb, "LBP"), new CountryModel("LC", "Saint Lucia", "+1", R.drawable.flag_lc, "XCD"), new CountryModel("LI", "Liechtenstein", "+423", R.drawable.flag_li, "CHF"), new CountryModel("LK", "Sri Lanka", "+94", R.drawable.flag_lk, "LKR"), new CountryModel("LR", "Liberia", "+231", R.drawable.flag_lr, "LRD"), new CountryModel("LS", "Lesotho", "+266", R.drawable.flag_ls, "LSL"), new CountryModel("LT", "Lithuania", "+370", R.drawable.flag_lt, "LTL"), new CountryModel("LU", "Luxembourg", "+352", R.drawable.flag_lu, "EUR"), new CountryModel("LV", "Latvia", "+371", R.drawable.flag_lv, "LVL"), new CountryModel("LY", "Libyan Arab Jamahiriya", "+218", R.drawable.flag_ly, "LYD"), new CountryModel("MA", "Morocco", "+212", R.drawable.flag_ma, "MAD"), new CountryModel("MC", "Monaco", "+377", R.drawable.flag_mc, "EUR"), new CountryModel("MD", "Moldova, Republic of", "+373", R.drawable.flag_md, "MDL"), new CountryModel("ME", "Montenegro", "+382", R.drawable.flag_me, "EUR"), new CountryModel("MF", "Saint Martin", "+590", R.drawable.flag_mf, "EUR"), new CountryModel("MG", "Madagascar", "+261", R.drawable.flag_mg, "MGA"), new CountryModel("MH", "Marshall Islands", "+692", R.drawable.flag_mh, "USD"), new CountryModel("MK", "Macedonia, The Former Yugoslav Republic of", "+389", R.drawable.flag_mk, "MKD"), new CountryModel("ML", "Mali", "+223", R.drawable.flag_ml, "XOF"), new CountryModel("MM", "Myanmar", "+95", R.drawable.flag_mm, "MMK"), new CountryModel("MN", "Mongolia", "+976", R.drawable.flag_mn, "MNT"), new CountryModel("MO", "Macao", "+853", R.drawable.flag_mo, "MOP"), new CountryModel("MP", "Northern Mariana Islands", "+1", R.drawable.flag_mp, "USD"), new CountryModel("MQ", "Martinique", "+596", R.drawable.flag_mq, "EUR"), new CountryModel("MR", "Mauritania", "+222", R.drawable.flag_mr, "MRO"), new CountryModel("MS", "Montserrat", "+1", R.drawable.flag_ms, "XCD"), new CountryModel("MT", "Malta", "+356", R.drawable.flag_mt, "EUR"), new CountryModel("MU", "Mauritius", "+230", R.drawable.flag_mu, "MUR"), new CountryModel("MV", "Maldives", "+960", R.drawable.flag_mv, "MVR"), new CountryModel("MW", "Malawi", "+265", R.drawable.flag_mw, "MWK"), new CountryModel("MX", "Mexico", "+52", R.drawable.flag_mx, "MXN"), new CountryModel("MY", "Malaysia", "+60", R.drawable.flag_my, "MYR"), new CountryModel("MZ", "Mozambique", "+258", R.drawable.flag_mz, "MZN"), new CountryModel("NA", "Namibia", "+264", R.drawable.flag_na, "NAD"), new CountryModel("NC", "New Caledonia", "+687", R.drawable.flag_nc, "XPF"), new CountryModel("NE", "Niger", "+227", R.drawable.flag_ne, "XOF"), new CountryModel("NF", "Norfolk Island", "+672", R.drawable.flag_nf, "AUD"), new CountryModel("NG", "Nigeria", "+234", R.drawable.flag_ng, "NGN"), new CountryModel("NI", "Nicaragua", "+505", R.drawable.flag_ni, "NIO"), new CountryModel("NL", "Netherlands", "+31", R.drawable.flag_nl, "EUR"), new CountryModel("NO", "Norway", "+47", R.drawable.flag_no, "NOK"), new CountryModel("NP", "Nepal", "+977", R.drawable.flag_np, "NPR"), new CountryModel("NR", "Nauru", "+674", R.drawable.flag_nr, "AUD"), new CountryModel("NU", "Niue", "+683", R.drawable.flag_nu, "NZD"), new CountryModel("NZ", "New Zealand", "+64", R.drawable.flag_nz, "NZD"), new CountryModel("OM", "Oman", "+968", R.drawable.flag_om, "OMR"), new CountryModel("PA", "Panama", "+507", R.drawable.flag_pa, "PAB"), new CountryModel("PE", "Peru", "+51", R.drawable.flag_pe, "PEN"), new CountryModel("PF", "French Polynesia", "+689", R.drawable.flag_pf, "XPF"), new CountryModel("PG", "Papua New Guinea", "+675", R.drawable.flag_pg, "PGK"), new CountryModel("PH", "Philippines", "+63", R.drawable.flag_ph, "PHP"), new CountryModel("PK", "Pakistan", "+92", R.drawable.flag_pk, "PKR"), new CountryModel("PL", "Poland", "+48", R.drawable.flag_pl, "PLN"), new CountryModel("PM", "Saint Pierre and Miquelon", "+508", R.drawable.flag_pm, "EUR"), new CountryModel("PN", "Pitcairn", "+872", R.drawable.flag_pn, "NZD"), new CountryModel("PR", "Puerto Rico", "+1", R.drawable.flag_pr, "USD"), new CountryModel("PS", "Palestinian Territory, Occupied", "+970", R.drawable.flag_ps, "ILS"), new CountryModel("PT", "Portugal", "+351", R.drawable.flag_pt, "EUR"), new CountryModel("PW", "Palau", "+680", R.drawable.flag_pw, "USD"), new CountryModel("PY", "Paraguay", "+595", R.drawable.flag_py, "PYG"), new CountryModel("QA", "Qatar", "+974", R.drawable.flag_qa, "QAR"), new CountryModel("RE", "Reunion", "+262", R.drawable.flag_re, "EUR"), new CountryModel("RO", "Romania", "+40", R.drawable.flag_ro, "RON"), new CountryModel("RS", "Serbia", "+381", R.drawable.flag_rs, "RSD"), new CountryModel("RU", "Russia", "+7", R.drawable.flag_ru, "RUB"), new CountryModel("RW", "Rwanda", "+250", R.drawable.flag_rw, "RWF"), new CountryModel("SA", "Saudi Arabia", "+966", R.drawable.flag_sa, "SAR"), new CountryModel("SB", "Solomon Islands", "+677", R.drawable.flag_sb, "SBD"), new CountryModel("SC", "Seychelles", "+248", R.drawable.flag_sc, "SCR"), new CountryModel("SD", "Sudan", "+249", R.drawable.flag_sd, "SDG"), new CountryModel("SE", "Sweden", "+46", R.drawable.flag_se, "SEK"), new CountryModel("SG", "Singapore", "+65", R.drawable.flag_sg, "SGD"), new CountryModel("SH", "Saint Helena, Ascension and Tristan Da Cunha", "+290", R.drawable.flag_sh, "SHP"), new CountryModel("SI", "Slovenia", "+386", R.drawable.flag_si, "EUR"), new CountryModel("SJ", "Svalbard and Jan Mayen", "+47", R.drawable.flag_sj, "NOK"), new CountryModel("SK", "Slovakia", "+421", R.drawable.flag_sk, "EUR"), new CountryModel("SL", "Sierra Leone", "+232", R.drawable.flag_sl, "SLL"), new CountryModel("SM", "San Marino", "+378", R.drawable.flag_sm, "EUR"), new CountryModel("SN", "Senegal", "+221", R.drawable.flag_sn, "XOF"), new CountryModel("SO", "Somalia", "+252", R.drawable.flag_so, "SOS"), new CountryModel("SR", "Suriname", "+597", R.drawable.flag_sr, "SRD"), new CountryModel("SS", "South Sudan", "+211", R.drawable.flag_ss, "SSP"), new CountryModel("ST", "Sao Tome and Principe", "+239", R.drawable.flag_st, "STD"), new CountryModel("SV", "El Salvador", "+503", R.drawable.flag_sv, "SVC"), new CountryModel("SX", "Sint Maarten", "+1", R.drawable.flag_sx, "ANG"), new CountryModel("SY", "Syrian Arab Republic", "+963", R.drawable.flag_sy, "SYP"), new CountryModel("SZ", "Swaziland", "+268", R.drawable.flag_sz, "SZL"), new CountryModel("TC", "Turks and Caicos Islands", "+1", R.drawable.flag_tc, "USD"), new CountryModel("TD", "Chad", "+235", R.drawable.flag_td, "XAF"), new CountryModel("TF", "French Southern Territories", "+262", R.drawable.flag_tf, "EUR"), new CountryModel("TG", "Togo", "+228", R.drawable.flag_tg, "XOF"), new CountryModel("TH", "Thailand", "+66", R.drawable.flag_th, "THB"), new CountryModel("TJ", "Tajikistan", "+992", R.drawable.flag_tj, "TJS"), new CountryModel("TK", "Tokelau", "+690", R.drawable.flag_tk, "NZD"), new CountryModel("TL", "East Timor", "+670", R.drawable.flag_tl, "USD"), new CountryModel("TM", "Turkmenistan", "+993", R.drawable.flag_tm, "TMT"), new CountryModel("TN", "Tunisia", "+216", R.drawable.flag_tn, "TND"), new CountryModel("TO", "Tonga", "+676", R.drawable.flag_to, "TOP"), new CountryModel("TR", "Turkey", "+90", R.drawable.flag_tr, "TRY"), new CountryModel("TT", "Trinidad and Tobago", "+1", R.drawable.flag_tt, "TTD"), new CountryModel("TV", "Tuvalu", "+688", R.drawable.flag_tv, "AUD"), new CountryModel("TW", "Taiwan", "+886", R.drawable.flag_tw, "TWD"), new CountryModel("TZ", "Tanzania, United Republic of", "+255", R.drawable.flag_tz, "TZS"), new CountryModel("UA", "Ukraine", "+380", R.drawable.flag_ua, "UAH"), new CountryModel("UG", "Uganda", "+256", R.drawable.flag_ug, "UGX"), new CountryModel("UM", "U.S. Minor Outlying Islands", "+1", R.drawable.flag_um, "USD"), new CountryModel("US", "United States", "+1", R.drawable.flag_us, "USD"), new CountryModel("UY", "Uruguay", "+598", R.drawable.flag_uy, "UYU"), new CountryModel("UZ", "Uzbekistan", "+998", R.drawable.flag_uz, "UZS"), new CountryModel("VA", "Holy See (Vatican City State)", "+379", R.drawable.flag_va, "EUR"), new CountryModel("VC", "Saint Vincent and the Grenadines", "+1", R.drawable.flag_vc, "XCD"), new CountryModel("VE", "Venezuela, Bolivarian Republic of", "+58", R.drawable.flag_ve, "VEF"), new CountryModel("VG", "Virgin Islands, British", "+1", R.drawable.flag_vg, "USD"), new CountryModel("VI", "Virgin Islands, U.S.", "+1", R.drawable.flag_vi, "USD"), new CountryModel("VN", "Vietnam", "+84", R.drawable.flag_vn, "VND"), new CountryModel("VU", "Vanuatu", "+678", R.drawable.flag_vu, "VUV"), new CountryModel("WF", "Wallis and Futuna", "+681", R.drawable.flag_wf, "XPF"), new CountryModel("WS", "Samoa", "+685", R.drawable.flag_ws, "WST"), new CountryModel("XK", "Kosovo", "+383", R.drawable.flag_xk, "EUR"), new CountryModel("YE", "Yemen", "+967", R.drawable.flag_ye, "YER"), new CountryModel("YT", "Mayotte", "+262", R.drawable.flag_yt, "EUR"), new CountryModel("ZA", "South Africa", "+27", R.drawable.flag_za, "ZAR"), new CountryModel("ZM", "Zambia", "+260", R.drawable.flag_zm, "ZMW"), new CountryModel("ZW", "Zimbabwe", "+263", R.drawable.flag_zw, "USD")};
        getCountriesData().setValue(new ArrayList<>(Arrays.asList(countries)));
        getCountryPos().setValue(0);
        getAddCustomerModel();
    }

    public void loadHomeData() {

        userModel = Preferences.getInstance().getUserData(getApplication().getApplicationContext());

        if (userModel != null && userModel.getData() != null ) {

            if (userModel.getData().getSelectedUser() != null){
                getInvoiceSetting();
                getCategoryData();

                if (userModel.getData().getSelectedWereHouse() != null && userModel.getData().getSelectedPos() != null){
                    getAdvantageSettings();
                    getCurrentShift();
                }
                getDining();
                getDraftTickets();
                getCustomers();
            }

            if (!userModel.getData().getUser().isAvailable()){
                getProfile();

            }

        }

        getDiscountsData();
        getItemsData();
        getCartData();
        getPrinters();


          /*if (userModel!=null){
            getCartData();
            getPrinters();
            if (userModel.getData().getSelectedUser() != null){
                Log.e("dd","yy");

                getHomeIndex(userModel.getData().getSelectedUser().getId(), userModel.getData().getSelectedPos().getId(), userModel.getData().getSelectedWereHouse().getId());

            }else {
                Log.e("dd","dd");
                getHomeIndex(userModel.getData().getUser().getId(), userModel.getData().getSelectedPos().getId(), userModel.getData().getSelectedWereHouse().getId());

            }

        }*/

    }

    public void getCartData() {
        getCart().setValue(manageCartModel.getCartModel(getApplication().getApplicationContext()));

    }

    public MutableLiveData<List<PrinterModel>> getPrintersInstance() {
        if (printers == null) {
            printers = new MutableLiveData<>();
        }

        return printers;
    }


    public MutableLiveData<CartList> getCart() {
        if (cart == null) {
            cart = new MutableLiveData<>();

        }
        return cart;
    }

    public MutableLiveData<Boolean> getIsTicketModel() {
        if (isTicketModel == null) {
            isTicketModel = new MutableLiveData<>();
            isTicketModel.setValue(false);
        }
        return isTicketModel;
    }

    public MutableLiveData<AdvantageModel> getAdvantage() {
        if (advantage == null) {
            advantage = new MutableLiveData<>();

        }
        return advantage;
    }

    public MutableLiveData<InvoiceSettings> getInvoiceSettings() {
        if (invoiceSettings == null) {
            invoiceSettings = new MutableLiveData<>();

        }
        return invoiceSettings;
    }


    public MutableLiveData<List<DeliveryModel>> getDeliveryOptions() {
        if (deliveryOptions == null) {
            deliveryOptions = new MutableLiveData<>();

        }
        return deliveryOptions;
    }

    public MutableLiveData<UserModel.Data> getUserModelData() {
        if (userModelData == null) {
            userModelData = new MutableLiveData<>();

        }
        return userModelData;
    }

    public MutableLiveData<List<CategoryModel>> getCategories() {
        if (categories == null) {
            categories = new MutableLiveData<>();

        }
        return categories;
    }


    public MutableLiveData<CategoryModel> getSelectedCategory() {
        if (selectedCategory == null) {
            selectedCategory = new MutableLiveData<>();
            selectedCategory.setValue(new CategoryModel(-1, getApplication().getApplicationContext().getString(R.string.all_items)));

        }
        return selectedCategory;
    }

    public MutableLiveData<DeliveryModel> getSelectedDeliveryOptions() {
        if (selectedDeliveryOptions == null) {
            selectedDeliveryOptions = new MutableLiveData<>();

        }
        return selectedDeliveryOptions;
    }

    public MutableLiveData<Boolean> getIsOpenedCustomerDialog() {
        if (isOpenedCustomerDialog == null) {
            isOpenedCustomerDialog = new MutableLiveData<>();
        }
        return isOpenedCustomerDialog;
    }

    public MutableLiveData<String> getPrice() {
        if (price == null) {
            price = new MutableLiveData<>();
            price.setValue("0.00");
        }
        return price;
    }

    public MutableLiveData<Boolean> getIsAddCustomerDialogShow() {
        if (isAddCustomerDialogShow == null) {
            isAddCustomerDialogShow = new MutableLiveData<>();
        }
        return isAddCustomerDialogShow;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        if (isLoading == null) {
            isLoading = new MutableLiveData<>();
        }
        return isLoading;
    }

    public MutableLiveData<List<ItemModel>> getItems() {
        if (items == null) {
            items = new MutableLiveData<>();
        }
        return items;
    }


    public MutableLiveData<List<DiscountModel>> getDiscounts() {
        if (discounts == null) {
            discounts = new MutableLiveData<>();
        }
        return discounts;
    }

    public MutableLiveData<String> getSearchQuery() {
        if (searchQuery == null) {
            searchQuery = new MutableLiveData<>();
            searchQuery.setValue("");
        }
        return searchQuery;
    }

    public MutableLiveData<String> getSearchQueryCustomer() {
        if (searchQueryCustomer == null) {
            searchQueryCustomer = new MutableLiveData<>();
            searchQueryCustomer.setValue("");
        }
        return searchQueryCustomer;
    }

    public MutableLiveData<List<CountryModel>> getCountriesData() {
        if (countriesData == null) {
            countriesData = new MutableLiveData<>();
        }

        return countriesData;
    }

    public MutableLiveData<AddCustomerModel> getAddCustomerModel() {
        if (addCustomerModel == null) {
            addCustomerModel = new MutableLiveData<>();
            /*AddCustomerModel model = new AddCustomerModel();
            addCustomerModel.setValue(model);*/
        }
        return addCustomerModel;
    }

    public MutableLiveData<Integer> getCountryPos() {
        if (countryPos == null) {
            countryPos = new MutableLiveData<>();

        }
        return countryPos;
    }

    public MutableLiveData<String> getOnError() {
        if (onError == null) {
            onError = new MutableLiveData<>();

        }
        return onError;
    }

    public MutableLiveData<Boolean> getIsDialogPriceOpened() {
        if (isDialogPriceOpened == null) {
            isDialogPriceOpened = new MutableLiveData<>();
            isDialogPriceOpened.setValue(false);
        }
        return isDialogPriceOpened;
    }

    public MutableLiveData<Boolean> getIsDialogExtrasOpened() {
        if (isDialogExtrasOpened == null) {
            isDialogExtrasOpened = new MutableLiveData<>();
            isDialogExtrasOpened.setValue(false);
        }
        return isDialogExtrasOpened;
    }

    public MutableLiveData<Boolean> getIsDialogDiscountsOpened() {
        if (isDialogDiscountsOpened == null) {
            isDialogDiscountsOpened = new MutableLiveData<>();
            isDialogDiscountsOpened.setValue(false);
        }
        return isDialogDiscountsOpened;
    }

    public MutableLiveData<Boolean> getIsDialogOpenedTicketsOpened() {
        if (isDialogOpenedTicketsOpened == null) {
            isDialogOpenedTicketsOpened = new MutableLiveData<>();
            isDialogOpenedTicketsOpened.setValue(false);
        }
        return isDialogOpenedTicketsOpened;
    }


    public MutableLiveData<Integer> getItemForPricePos() {
        if (itemForPricePos == null) {
            itemForPricePos = new MutableLiveData<>();
        }
        return itemForPricePos;
    }

    public MutableLiveData<ItemModel> getItemForPrice() {
        if (itemForPrice == null) {
            itemForPrice = new MutableLiveData<>();
        }
        return itemForPrice;
    }

    public MutableLiveData<Integer> getTicketCount() {
        if (ticketCount == null) {
            ticketCount = new MutableLiveData<>();
            ticketCount.setValue(0);
        }
        return ticketCount;
    }

    public MutableLiveData<List<DiscountModel>> getCartDiscounts() {
        if (cartDiscounts == null) {
            cartDiscounts = new MutableLiveData<>();
        }
        return cartDiscounts;
    }

    public MutableLiveData<Boolean> getIsScanOpened() {
        if (isScanOpened == null) {
            isScanOpened = new MutableLiveData<>();
            isScanOpened.setValue(false);
        }
        return isScanOpened;
    }

    public MutableLiveData<Integer> getCamera() {
        if (camera == null) {
            camera = new MutableLiveData<>();
            camera.setValue(CodeScanner.CAMERA_BACK);
        }
        return camera;
    }

    public MutableLiveData<List<CustomerModel>> getCustomersInstance() {
        if (customers == null) {
            customers = new MutableLiveData<>();
        }
        return customers;
    }

    public MutableLiveData<Boolean> getIsCustomerLoading() {
        if (isCustomerLoading == null) {
            isCustomerLoading = new MutableLiveData<>();
        }
        return isCustomerLoading;
    }

    public MutableLiveData<Boolean> getOnCustomerUpdatedSuccess() {
        if (onCustomerUpdatedSuccess == null) {
            onCustomerUpdatedSuccess = new MutableLiveData<>();
        }
        return onCustomerUpdatedSuccess;
    }

    public MutableLiveData<AppSettingModel> getAppSettingModel() {
        if (appSettingModel == null) {
            appSettingModel = new MutableLiveData<>();

        }
        return appSettingModel;
    }

    public MutableLiveData<Integer> getTicketSortPos() {
        if (ticketSortPos == null) {
            ticketSortPos = new MutableLiveData<>();
            ticketSortPos.setValue(0);
        }
        return ticketSortPos;
    }

    public MutableLiveData<Boolean> getIsOpenedTicketSearchOpened() {
        if (isOpenedTicketSearchOpened == null) {
            isOpenedTicketSearchOpened = new MutableLiveData<>();
            isOpenedTicketSearchOpened.setValue(false);
        }
        return isOpenedTicketSearchOpened;
    }

    public MutableLiveData<String> getQueryMyOpenedTickets() {
        if (queryMyOpenedTickets == null) {
            queryMyOpenedTickets = new MutableLiveData<>();
            queryMyOpenedTickets.setValue("");
        }
        return queryMyOpenedTickets;
    }

    public MutableLiveData<Boolean> getIsLoadingOpenedTickets() {
        if (isLoadingOpenedTickets == null) {
            isLoadingOpenedTickets = new MutableLiveData<>();
        }
        return isLoadingOpenedTickets;
    }

    public MutableLiveData<Boolean> getIsDeleteAllDraftTicketsSelected() {
        if (isDeleteAllDraftTicketsSelected == null) {
            isDeleteAllDraftTicketsSelected = new MutableLiveData<>();
        }
        return isDeleteAllDraftTicketsSelected;
    }

    public MutableLiveData<Boolean> getCanDeleteOpenedTickets() {
        if (canDeleteOpenedTickets == null) {
            canDeleteOpenedTickets = new MutableLiveData<>();
        }
        return canDeleteOpenedTickets;
    }


    public MutableLiveData<List<OrderModel.Sale>> getMainOrders() {
        if (mainOrders == null) {
            mainOrders = new MutableLiveData<>();
            mainOrders.setValue(new ArrayList<>());
        }

        return mainOrders;
    }

    public MutableLiveData<List<OrderModel.Sale>> getOrders() {
        if (orders == null) {
            orders = new MutableLiveData<>();
        }

        return orders;
    }

    public void getPrinters() {
        dao.getPrinters().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<PrinterModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<PrinterModel> list) {
                        getPrintersInstance().setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void getHomeIndex(String user_id, String pos_id, String warehouse_id) {
        if (mainItemList.size() > 0) {
            getIsLoading().setValue(false);

        } else {
            getIsLoading().setValue(true);
        }
        getIsCustomerLoading().setValue(true);
        getIsLoadingOpenedTickets().setValue(true);


        List<CategoryModel> list = new ArrayList<>();

        list.add(new CategoryModel(-1, getApplication().getApplicationContext().getString(R.string.all_items)));
        list.add(new CategoryModel(-2, getApplication().getApplicationContext().getString(R.string.discounts_items)));
        getCategories().setValue(list);
        getSelectedCategory().setValue(getSelectedCategory().getValue());


        Api.getService(Tags.base_url)
                .homeIndex(user_id, pos_id, warehouse_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<HomeIndexModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<HomeIndexModel> response) {
                        getIsLoading().setValue(false);
                        getIsCustomerLoading().setValue(false);

                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData() != null) {
                                        AppSettingModel settingModel = Preferences.getInstance().getAppSetting(getApplication().getApplicationContext());

                                        if (response.body().getData().getAdvantage() != null) {
                                            settingModel.setAdvantageModel(response.body().getData().getAdvantage());
                                            Preferences.getInstance().createUpdateAppSetting(getApplication().getApplicationContext(), settingModel);
                                            getAppSettingModel().setValue(settingModel);
                                        }

                                        if (response.body().getData().getShift() != null) {
                                            settingModel.setIsShiftOpen(1);
                                            settingModel.setShift_id(response.body().getData().getShift().getId());
                                            Preferences.getInstance().createUpdateAppSetting(getApplication().getApplicationContext(), settingModel);
                                            getAppSettingModel().setValue(settingModel);

                                        } else {
                                            settingModel.setIsShiftOpen(0);
                                            settingModel.setShift_id(null);
                                            Preferences.getInstance().createUpdateAppSetting(getApplication().getApplicationContext(), settingModel);
                                            getAppSettingModel().setValue(settingModel);
                                            clearCart();
                                        }

                                        if (response.body().getData().getDining() != null) {
                                            getDeliveryOptions().setValue(response.body().getData().getDining());
                                            if (response.body().getData().getDining().size() > 0) {
                                                if (getSelectedDeliveryOptions().getValue() == null) {
                                                    getSelectedDeliveryOptions().setValue(response.body().getData().getDining().get(0));

                                                }
                                                addDeliveryOption(response.body().getData().getDining().get(0).getId(), response.body().getData().getDining().get(0).getName());
                                            } else {
                                                getSelectedDeliveryOptions().setValue(null);
                                            }
                                        } else {
                                            getSelectedDeliveryOptions().setValue(null);

                                        }

                                        if (response.body().getData().getCustomers() != null) {
                                            mainCustomerList.clear();
                                            mainCustomerList.addAll(response.body().getData().getCustomers());
                                            searchCustomer(getSearchQueryCustomer().getValue());

                                        }

                                        if (response.body().getData().getCategories() != null) {
                                            list.addAll(response.body().getData().getCategories());
                                            getCategories().setValue(list);
                                        }

                                        if (response.body().getData().getProfile() != null) {
                                            getUserModelData().setValue(response.body().getData().getProfile());

                                        }

                                        if (response.body().getData().getItems() != null) {
                                            mainItemList.clear();
                                            mainItemList.addAll(response.body().getData().getItems());
                                            search(getSearchQuery().getValue());
                                        }

                                        if (response.body().getData().getDiscounts() != null) {
                                            mainDiscountList.clear();
                                            mainDiscountList.addAll(response.body().getData().getDiscounts());
                                            mainItemDiscountList.setValue(response.body().getData().getDiscounts());

                                            if (getSelectedCategory().getValue() != null && getSelectedCategory().getValue().getId() == -2) {
                                                search(getSearchQuery().getValue());

                                            }
                                        }

                                        if (response.body().getData().getDraft_sales() != null) {
                                            updateData(response.body().getData().getDraft_sales());

                                        }

                                        if (response.body().getData().getReceipt() != null) {
                                            getInvoiceSettings().setValue(response.body().getData().getReceipt());
                                        }


                                    }
                                } else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        getIsLoading().setValue(false);
                        getIsCustomerLoading().setValue(false);
                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }

    public void getAdvantageSettings() {
        Api.getService(Tags.base_url)
                .getAdvantage(userModel.getData().getSelectedUser().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<AdvantageDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<AdvantageDataModel> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                AppSettingModel settingModel = Preferences.getInstance().getAppSetting(getApplication().getApplicationContext());

                                if (response.body().getStatus() == 200) {
                                    settingModel.setAdvantageModel(response.body().getData());
                                    Preferences.getInstance().createUpdateAppSetting(getApplication().getApplicationContext(), settingModel);
                                    getAppSettingModel().setValue(settingModel);

                                }
                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }

    public void getCurrentShift() {
        Api.getService(Tags.base_url)
                .currentShift(userModel.getData().getSelectedUser().getId(), userModel.getData().getSelectedWereHouse().getId(), userModel.getData().getSelectedPos().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ShiftDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<ShiftDataModel> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                AppSettingModel settingModel = Preferences.getInstance().getAppSetting(getApplication().getApplicationContext());

                                if (response.body().getStatus() == 200) {
                                    settingModel.setIsShiftOpen(1);
                                    settingModel.setShift_id(response.body().getData().getId());
                                    Preferences.getInstance().createUpdateAppSetting(getApplication().getApplicationContext(), settingModel);
                                    getAppSettingModel().setValue(settingModel);

                                } else if (response.body().getStatus() == 201) {
                                    settingModel.setIsShiftOpen(0);
                                    settingModel.setShift_id(null);
                                    Preferences.getInstance().createUpdateAppSetting(getApplication().getApplicationContext(), settingModel);
                                    getAppSettingModel().setValue(settingModel);
                                    clearCart();

                                } else {

                                }
                            } else {


                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }

    public void getDining() {
        Api.getService(Tags.base_url)
                .getDining(userModel.getData().getSelectedUser().getId(), userModel.getData().getSelectedWereHouse().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<DeliveryDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<DeliveryDataModel> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    getDeliveryOptions().setValue(response.body().getData());
                                    if (response.body().getData().size() > 0) {
                                        if (getSelectedDeliveryOptions().getValue() == null) {
                                            getSelectedDeliveryOptions().setValue(response.body().getData().get(0));

                                        }
                                        addDeliveryOption(response.body().getData().get(0).getId(), response.body().getData().get(0).getName());
                                    } else {
                                        getSelectedDeliveryOptions().setValue(null);
                                    }

                                } else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }
    public void getInvoiceSetting() {
        Api.getService(Tags.base_url)
                .getReceiptSetting(userModel.getData().getSelectedUser().getId(), userModel.getData().getSelectedWereHouse().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<InvoiceSettingData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<InvoiceSettingData> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    getInvoiceSettings().setValue(response.body().getData());

                                } else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }


    public void getCustomers() {
        getIsCustomerLoading().setValue(true);
        Api.getService(Tags.base_url)
                .getCustomers(userModel.getData().getSelectedUser() != null ? userModel.getData().getSelectedUser().getId() : userModel.getData().getUser().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<CustomerDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<CustomerDataModel> response) {
                        getIsCustomerLoading().setValue(false);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    mainCustomerList.clear();
                                    mainCustomerList.addAll(response.body().getData());
                                    searchCustomer(getSearchQueryCustomer().getValue());

                                } else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        getIsCustomerLoading().setValue(false);

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }

    public void getProfile() {
        Api.getService(Tags.base_url)
                .getProfile(userModel.getData().getSelectedUser().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<UserModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<UserModel> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    //getUserModelData().setValue(response.body());
                                }
                            }
                        } else {

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }

    public void searchCustomer(String query) {
        getSearchQueryCustomer().setValue(query);

        if (query == null || query.isEmpty()) {
            Observable.fromIterable(mainCustomerList)
                    .filter(customerModel -> {

                        if (getCart().getValue() != null && getCart().getValue().getCustomerModel() != null && getCart().getValue().getCustomerModel().getId().equals(customerModel.getId())) {
                            customerModel.setAddedToCart(true);
                        }


                        return true;

                    }).toList().toObservable()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .debounce(1, TimeUnit.SECONDS)
                    .subscribe(new Observer<List<CustomerModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable.add(d);
                        }

                        @Override
                        public void onNext(List<CustomerModel> list) {
                            getCustomersInstance().setValue(list);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Observable.fromIterable(mainCustomerList)
                    .filter(customerModel -> {

                        if (getCart().getValue() != null && getCart().getValue().getCustomerModel() != null && getCart().getValue().getCustomerModel().getId().equals(customerModel.getId())) {
                            customerModel.setAddedToCart(true);
                        }

                        if (customerModel.getName().startsWith(query)) {

                            return true;
                        } else if (customerModel.getEmail().startsWith(query)) {

                            return true;
                        } else if (customerModel.getPhone_number().startsWith(query)) {

                            return true;
                        }

                        return false;

                    }).toList().toObservable()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .debounce(1, TimeUnit.SECONDS)
                    .subscribe(new Observer<List<CustomerModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable.add(d);
                        }

                        @Override
                        public void onNext(List<CustomerModel> list) {
                            getCustomersInstance().setValue(list);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        }
    }

    public void getCategoryData() {
        List<CategoryModel> list = new ArrayList<>();

        list.add(new CategoryModel(-1, getApplication().getApplicationContext().getString(R.string.all_items)));
        list.add(new CategoryModel(-2, getApplication().getApplicationContext().getString(R.string.discounts_items)));
        getCategories().setValue(list);
        getSelectedCategory().setValue(getSelectedCategory().getValue());

        Api.getService(Tags.base_url)
                .categories(userModel.getData().getSelectedUser().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<CategoryDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<CategoryDataModel> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    list.addAll(response.body().getData());
                                    getCategories().setValue(list);
                                } else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }

    public void search(String query) {
        getSearchQuery().setValue(query);
        if (query.isEmpty()) {
            if (getSelectedCategory().getValue() != null) {
                if (getSelectedCategory().getValue().getId() == -1) {
                    getItems().setValue(mainItemList);

                } else if (getSelectedCategory().getValue().getId() == -2) {
                    List<DiscountModel> list = new ArrayList<>();
                    for (DiscountModel discountModel : mainDiscountList) {


                        if (isDiscountExist(discountModel)) {
                            discountModel.setSelected(true);

                        } else {
                            discountModel.setSelected(false);
                        }

                        list.add(discountModel);
                    }

                    getDiscounts().setValue(list);

                } else {

                    if (mainItemList != null) {
                        List<ItemModel> list = new ArrayList<>();
                        for (ItemModel itemModel : mainItemList) {
                            if (itemModel.getCategory() != null) {
                                if (itemModel.getCategory().getId() == getSelectedCategory().getValue().getId()) {


                                    list.add(itemModel);

                                }
                            } else {
                                // list.add(itemModel);

                            }
                        }


                        getItems().setValue(list);
                    }

                }
            }
        } else {
            if (getSelectedCategory().getValue() != null) {
                if (getSelectedCategory().getValue().getId() == -2) {
                    if (mainDiscountList != null) {
                        List<DiscountModel> list = new ArrayList<>();
                        for (DiscountModel discountModel : mainDiscountList) {
                            if (discountModel.getTitle().toLowerCase().startsWith(query.toLowerCase())) {
                                if (isDiscountExist(discountModel)) {
                                    discountModel.setSelected(true);
                                } else {
                                    discountModel.setSelected(false);

                                }
                                list.add(discountModel);
                            }
                        }

                        getDiscounts().setValue(list);
                    }
                } else {
                    if (mainItemList != null) {
                        List<ItemModel> list = new ArrayList<>();
                        for (ItemModel itemModel : mainItemList) {
                            if (itemModel.getCategory() != null) {
                                if (getSelectedCategory().getValue() != null && getSelectedCategory().getValue().getId() == -1 && getSelectedCategory().getValue().getId() == -2) {
                                    if (itemModel.getName().toLowerCase().startsWith(query.toLowerCase())) {
                                        list.add(itemModel);
                                    }
                                } else {
                                    if (itemModel.getName().toLowerCase().startsWith(query.toLowerCase()) && itemModel.getCategory().getId() == getSelectedCategory().getValue().getId()) {
                                        list.add(itemModel);
                                    }
                                }
                            } else {
                                if (itemModel.getName().toLowerCase().startsWith(query.toLowerCase())) {
                                    list.add(itemModel);
                                }
                            }
                        }

                        getItems().setValue(list);
                    }

                }
            }

        }

    }

    public void getItemsData() {
        if (mainItemList.size() > 0) {
            getIsLoading().setValue(false);

        } else {
            getIsLoading().setValue(true);
        }
        Api.getService(Tags.base_url)
                .items(userModel.getData().getSelectedUser() != null ? userModel.getData().getSelectedUser().getId() : userModel.getData().getUser().getId(), userModel.getData().getSelectedWereHouse().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ItemsDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<ItemsDataModel> response) {
                        getIsLoading().setValue(false);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    mainItemList.clear();
                                    mainItemList.addAll(response.body().getData());
                                    search(getSearchQuery().getValue());

                                } else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        getIsLoading().setValue(false);

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }

    public void getDiscountsData() {

        Api.getService(Tags.base_url)
                .discount(userModel.getData().getSelectedUser() != null ? userModel.getData().getSelectedUser().getId() : userModel.getData().getUser().getId(), userModel.getData().getSelectedWereHouse().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<DiscountDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<DiscountDataModel> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    mainDiscountList.clear();
                                    mainDiscountList.addAll(response.body().getData());
                                    mainItemDiscountList.setValue(response.body().getData());

                                    if (getSelectedCategory().getValue() != null && getSelectedCategory().getValue().getId() == -2) {
                                        search(getSearchQuery().getValue());

                                    }
                                } else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }

    public void addCustomer(Context context) {
        if (getAddCustomerModel().getValue() == null) {
            return;
        }
        userModel = Preferences.getInstance().getUserData(getApplication().getApplicationContext());

        AddCustomerModel addCustomerModel = getAddCustomerModel().getValue();
        ProgressDialog dialog = Common.createProgressDialog(context, context.getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .addCustomer(userModel.getData().getSelectedUser().getId(), addCustomerModel.getName(), addCustomerModel.getEmail(), addCustomerModel.getPhone(), addCustomerModel.getAddress(), addCustomerModel.getCity(), addCustomerModel.getPostal_code(), addCustomerModel.getCountry(), addCustomerModel.getTax())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<SingleCustomerModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<SingleCustomerModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (getCustomersInstance().getValue() != null) {

                                        getOnCustomerUpdatedSuccess().setValue(true);
                                        getCustomersInstance().getValue().add(0, response.body().getData());
                                        searchCustomer(getSearchQueryCustomer().getValue());
                                    }

                                } else {
                                    Log.e("error", response.body().getStatus() + "__" + response.body().getMessage().toString());
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }

                    }
                });

    }

    public void updatePrice(String p) {
        String price = getPrice().getValue();

        if (price != null) {
            if ((price.equals("0.00") && p.equals("00")) || price.isEmpty()) {
                getPrice().setValue("0.00");

            } else if (price.length() >= 9) {
                getPrice().setValue("999,999.99");
            } else {
                price = price.replace(".", "");
                price = price.replace(",", "");
                price += p;
                float pr = Float.parseFloat(price) / 100.00f;
                price = String.format(Locale.US, "%.2f", pr);

                if (price.length() == 7 || price.length() == 8) {
                    StringBuilder builder = new StringBuilder(price);
                    builder.insert(price.length() - 6, ",");
                    price = builder.toString();

                }
                getPrice().setValue(price);
            }


        }
    }

    public void removeLastPriceIndex() {
        String price = getPrice().getValue();
        if (price != null) {
            if (!price.equals("0.00")) {

                price = price.replace(".", "").replace(",", "");

                price = price.substring(0, price.length() - 1);

                float pr = Float.parseFloat(price) / 100.00f;
                price = String.format(Locale.US, "%.2f", pr);

                if (price.length() == 7 || price.length() == 8) {
                    StringBuilder builder = new StringBuilder(price);
                    builder.insert(price.length() - 6, ",");
                    price = builder.toString();

                }
                getPrice().setValue(price);

            }
        }
    }

    public void addCartItem(int add_update_type) {
        if (getItemForPrice().getValue() != null) {
            ItemModel itemModel = getItemForPrice().getValue();
            double total = 0.0;
            if (itemModel.getSelectedVariant() != null) {
                total = Double.parseDouble(itemModel.getSelectedVariant().getPrice()) * itemModel.getAmount();
            } else {
                total = Double.parseDouble(itemModel.getPrice()) * itemModel.getAmount();

            }

            itemModel.setNetTotal(total);
            manageCartModel.addItemToCart(getItemForPrice().getValue(), add_update_type, getApplication().getApplicationContext());
            getCart().setValue(manageCartModel.getCartModel(getApplication().getApplicationContext()));
        }
    }

    private boolean isDiscountExist(DiscountModel discountModel) {
        for (DiscountModel model : manageCartModel.getCartModel(getApplication().getApplicationContext()).getDiscounts()) {
            if (model.getId().equals(discountModel.getId())) {
                return true;
            }
        }
        return false;
    }

    public void clearCart() {
        manageCartModel.clearCart(getApplication().getApplicationContext());
        itemForPrice = null;
        getCart().setValue(new CartList());
    }

    public void removeItemFromCart(int pos) {
        if (getCart().getValue() != null) {
            manageCartModel.deleteItemFromCart(getCart().getValue().getItems().get(pos), getApplication().getApplicationContext());
            getCart().getValue().removeItem(pos);
            if (getCart().getValue().getItems().size() == 0) {
                manageCartModel.clearCart(getApplication().getApplicationContext());
                getCart().setValue(new CartList());

            } else {
                getCart().setValue(getCart().getValue());

            }

        }
    }

    public void getCartDiscount() {
        List<DiscountModel> discounts = getCartDiscounts().getValue();
        if (discounts != null) {
            discounts.clear();
        } else {
            discounts = new ArrayList<>();
        }
        deletedCartDiscounts = new ArrayList<>();

        if (getCart().getValue() != null && getCart().getValue().getDiscounts().size() > 0) {
            discounts.addAll(getCart().getValue().getDiscounts());
        }

        getCartDiscounts().setValue(discounts);
    }

    public void deleteCartDiscountItem(int pos) {
        if (getCartDiscounts().getValue() != null) {
            deletedCartDiscounts.add(getCartDiscounts().getValue().get(pos));

            getCartDiscounts().getValue().remove(pos);
        }

        getCartDiscounts().setValue(getCartDiscounts().getValue());

    }

    public void deleteGeneralDiscountFromCart() {
        if (getCart().getValue() != null) {
            manageCartModel.deleteGeneralCartDiscount(deletedCartDiscounts, getApplication().getApplicationContext());
            getCart().getValue().removeGeneralDiscount(deletedCartDiscounts);
            getCart().setValue(getCart().getValue());
        }
    }

    public void addDiscountForAllTicket(DiscountModel discountModel) {
        manageCartModel.addDiscountForAllTicket(discountModel, getApplication().getApplicationContext());
    }

    public void addDeliveryOption(String deliveryId, String deliveryName) {
        manageCartModel.addDeliveryToCart(deliveryId, deliveryName, getApplication().getApplicationContext());
    }

    public void assignCustomerToCart(CustomerModel customerModel) {
        if (customerModel.isAddedToCart()) {
            if (getCart().getValue() != null) {
                getCart().getValue().setCustomerModel(customerModel);
            }
            manageCartModel.assignCustomerToCart(customerModel, getApplication().getApplicationContext());

        } else {
            removeCustomerFromCart();
        }
    }

    public void removeCustomerFromCart() {
        if (getCart().getValue() != null) {
            getCart().getValue().setCustomerModel(null);
        }
        manageCartModel.removeCustomerFromCart(getApplication().getApplicationContext());
    }

    //////////////////////////////////////////////////////////////////////
    public void getDraftTickets() {
        getIsLoadingOpenedTickets().setValue(true);
        UserModel userModel = Preferences.getInstance().getUserData(getApplication().getApplicationContext());
        Api.getService(Tags.base_url)
                .getDraftOrders(userModel.getData().getSelectedUser().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new SingleObserver<Response<OrderDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<OrderDataModel> response) {
                        getIsLoadingOpenedTickets().setValue(false);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    updateData(response.body().getData());
                                }
                            }
                        } else {
                            Toast.makeText(getApplication().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();

                            try {
                                if (response.errorBody() != null) {
                                    Log.e(TAG, response.code() + "__" + response.errorBody().string());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        getIsLoadingOpenedTickets().setValue(false);
                        Log.e("error", e.getMessage() + "");

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Toast.makeText(getApplication().getApplicationContext(), R.string.check_network, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplication().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void updateData(List<OrderModel> data) {
        List<OrderModel.Sale> sales = new ArrayList<>();
        for (OrderModel model : data) {
            List<OrderModel.Sale> list = new ArrayList<>();
            for (OrderModel.Sale sale : model.getSales()) {
                sale.setOrder_date(model.getDate());
                list.add(sale);
            }


            sales.addAll(list);
        }
        getMainOrders().setValue(sales);
        searchMyTicket();
    }

    public void searchMyTicket() {
        String query = getQueryMyOpenedTickets().getValue();
        List<OrderModel.Sale> list = new ArrayList<>();
        if (query == null || query.isEmpty()) {

            if (getMainOrders().getValue() != null) {
                for (OrderModel.Sale sale : getMainOrders().getValue()) {
                    int pos = getDeletedDraftPos(sale);
                    sale.setSelected(pos != -1);
                    list.add(sale);
                }
                if (getTicketSortPos().getValue() != null) {
                    int pos = getTicketSortPos().getValue();
                    Collections.sort(list, (o2, o1) -> {
                        if (pos == 1) {
                            if (Double.parseDouble(o1.getGrand_total()) > Double.parseDouble(o2.getGrand_total())) {
                                return 1;
                            } else if (Double.parseDouble(o1.getGrand_total()) < Double.parseDouble(o2.getGrand_total())) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else if (pos == 2) {
                            if (Long.parseLong(o1.getDate()) > Long.parseLong(o2.getDate())) {
                                return 1;
                            } else if (Long.parseLong(o1.getDate()) < Long.parseLong(o2.getDate())) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else if (pos == 3) {
                            return o1.getUser().getName().compareToIgnoreCase(o2.getUser().getName());

                        } else {
                            return o1.getName().compareToIgnoreCase(o2.getName());

                        }
                    });


                }

            }

        } else {

            if (getMainOrders().getValue() != null) {
                for (OrderModel.Sale sale : getMainOrders().getValue()) {
                    int pos = getDeletedDraftPos(sale);
                    sale.setSelected(pos != -1);

                    if (sale.getName() != null && !sale.getName().isEmpty() && sale.getName().equalsIgnoreCase(query)) {
                        list.add(sale);
                    }

                }
                if (getTicketSortPos().getValue() != null) {
                    int pos = getTicketSortPos().getValue();
                    Collections.sort(list, (o2, o1) -> {
                        if (pos == 1) {
                            if (Double.parseDouble(o1.getGrand_total()) > Double.parseDouble(o2.getGrand_total())) {
                                return 1;
                            } else if (Double.parseDouble(o1.getGrand_total()) < Double.parseDouble(o2.getGrand_total())) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else if (pos == 2) {
                            if (Long.parseLong(o1.getDate()) > Long.parseLong(o2.getDate())) {
                                return 1;
                            } else if (Long.parseLong(o1.getDate()) < Long.parseLong(o2.getDate())) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else if (pos == 3) {
                            return o1.getUser().getName().compareToIgnoreCase(o2.getUser().getName());

                        } else {
                            return o1.getName().compareToIgnoreCase(o2.getName());

                        }
                    });

                }
            }
        }


        getIsDeleteAllDraftTicketsSelected().setValue(getOrders().getValue() != null && deletedSales.size() == getOrders().getValue().size());

        getOrders().setValue(list);
    }


    public void saveTicket(Context context) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        CartList cartList = manageCartModel.getCartModel(getApplication().getApplicationContext());

        if (cartList != null) {
            String customer_id = null;
            if (cartList.getCustomerModel() != null) {
                customer_id = cartList.getCustomerModel().getId();
            }
            List<CartModel.Detail> detailList = new ArrayList<>();


            for (ItemModel itemModel : cartList.getItems()) {
                double totalDiscount = 0.0;
                List<CartModel.Discount> discountsItem = new ArrayList<>();
                List<CartModel.Modifier> modifiers = new ArrayList<>();


                double tax_rate = 0.0;
                if (itemModel.getTax() != null) {

                    tax_rate = Double.parseDouble(itemModel.getTax().getRate());

                }

                for (DiscountModel discountModel : itemModel.getDiscounts()) {
                    CartModel.Discount discount = new CartModel.Discount(discountModel.getId());
                    discountsItem.add(discount);
                    totalDiscount += Double.parseDouble(discountModel.getValue());
                }
                String variant_id = "";
                double price = Double.parseDouble(itemModel.getPrice().replace(",", ""));
                if (itemModel.getSelectedVariant() != null) {
                    variant_id = itemModel.getSelectedVariant().getId();
                    price = Double.parseDouble(itemModel.getSelectedVariant().getPrice().replace(",", ""));
                }

                for (ModifierModel modifierModel : itemModel.getModifiers()) {
                    double total = 0.0;
                    List<CartModel.ModifierDetails> modifierDetails = new ArrayList<>();
                    for (ModifierModel.Data data : modifierModel.getModifiers_data()) {
                        double totalDataCost = itemModel.getAmount() * Double.parseDouble(data.getCost());
                        total += totalDataCost;

                        if (data.isSelected()) {
                            CartModel.ModifierDetails details = new CartModel.ModifierDetails(data.getId(), Double.parseDouble(data.getCost()), totalDataCost);
                            modifierDetails.add(details);
                        }

                    }
                    CartModel.Modifier modifier = new CartModel.Modifier(itemModel.getId(), modifierModel.getId(), total + "", modifierDetails);

                    modifiers.add(modifier);
                }

                itemModel.calculateTotal();
                CartModel.Detail detail = new CartModel.Detail(itemModel.getId(), itemModel.getName(), itemModel.getTotalPrice() + "", variant_id, itemModel.getAmount(), price, totalDiscount + "", tax_rate + "", itemModel.getComment(), modifiers, discountsItem);
                detailList.add(detail);
            }


            AppSettingModel settingModel = Preferences.getInstance().getAppSetting(getApplication().getApplicationContext());
            List<CartModel.Payment> payments = new ArrayList<>();


            List<CartModel.Discount> discounts = new ArrayList<>();
            for (DiscountModel discountModel : cartList.getDiscounts()) {
                CartModel.Discount discount = new CartModel.Discount(discountModel.getId());
                discounts.add(discount);
            }

            String sale_status = "";
            String draft = "";
            String name = "";
            if (cartList.getSale_id() == null) {
                sale_status = "3";
                draft = "0";

            } else {
                sale_status = "3";
                draft = "1";
            }

            String dining_id = null;
            String diningName = "";
            if (getSelectedDeliveryOptions().getValue() != null) {
                dining_id = getSelectedDeliveryOptions().getValue().getId();
                diningName = getSelectedDeliveryOptions().getValue().getName();
            }
            cartList.setDelivery_id(dining_id);

            CartModel.Cart cart = new CartModel.Cart(getTicketName(), settingModel.getShift_id(), getDate(), cartList.getNetTotalPrice(), cartList.getTotalTaxPrice(), cartList.getTotalDiscountValue(), cartList.getTotalPrice(), dining_id, diningName, userModel.getData().getSelectedPos().getId(), cartList.getSale_id(), sale_status, draft, payments, discounts, detailList);
            List<CartModel.Cart> carts = new ArrayList<>();
            carts.add(cart);
            CartModel cartModel = new CartModel(userModel.getData().getSelectedUser().getId(), userModel.getData().getSelectedWereHouse().getId(), customer_id, userModel.getData().getSelectedPos().getId(), carts);


            Api.getService(Tags.base_url)
                    .storeOrder(cartModel)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Response<InvoiceIdsModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable.add(d);
                        }

                        @Override
                        public void onSuccess(Response<InvoiceIdsModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    Log.e(TAG, response.body().getStatus() + "" + response.body().getMessage().toString());

                                    if (response.body().getStatus() == 200) {
                                        getDraftTickets();
                                        clearCart();
                                    } else if (response.body().getStatus() == 412) {
                                        Toast.makeText(context, R.string.amount_not_avail, Toast.LENGTH_SHORT).show();

                                    }

                                }
                            } else {


                                try {
                                    Log.e(TAG, response.code() + "__" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }


                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Log.e(TAG, e.getMessage() + "");

                            if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                                Toast.makeText(getApplication().getApplicationContext(), R.string.check_network, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplication().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();


                            }
                        }
                    });
        }
    }

    private String getDate() {
        return String.valueOf(new Date().getTime());

    }

    private String getTicketName() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        return "Ticket - " + simpleDateFormat.format(new Date());
    }

    public void addFromDraftToCart(OrderModel.Sale sale) {
        CartList cartListModel = manageCartModel.getCartModel(getApplication().getApplicationContext());

        Observable.just(cartListModel)
                .map(cartList -> {
                    cartList.setSale_id(sale.getId());
                    List<DiscountModel> discounts = new ArrayList<>();
                    List<ItemModel> items = new ArrayList<>();

                    for (OrderModel.OrderDiscount orderDiscount : sale.getDiscounts()) {

                        discounts.add(orderDiscount.getDiscount());
                    }
                    cartList.setDiscounts(discounts);
                    for (OrderModel.Detail detail : sale.getDetails()) {
                        ItemModel itemModel = detail.getProduct();
                        itemModel.setComment(detail.getComment());
                        VariantModel variant = detail.getVariant();

                        List<VariantModel> variantModelList = new ArrayList<>();
                        for (VariantModel variantModel : detail.getProduct().getVariants()) {
                            variantModel.setCode(variantModel.getPivot().getItem_code());
                            variantModel.setPrice(variantModel.getPivot().getAdditional_price());
                            if (variant != null && variantModel.getId().equals(variant.getId())) {
                                variantModel.setSelected(true);
                            } else {
                                variantModel.setSelected(false);
                            }
                            variantModelList.add(variantModel);
                        }
                        itemModel.setVariants(variantModelList);

                        if (variant != null) {
                            variant.setPrice(detail.getNet_unit_price());

                        }
                        itemModel.setSelectedVariant(variant);


                        List<DiscountModel> discs = new ArrayList<>();
                        for (OrderModel.OrderDiscount discount : detail.getDiscounts()) {
                            discs.add(discount.getDiscount());
                        }


                        List<ModifierModel.Data> selectedModifiers = new ArrayList<>();
                        for (OrderModel.SaleModifierData mod : detail.getSale_modifiers()) {
                            for (OrderModel.SaleModifier saleModifier : mod.getSale_modifier_data()) {
                                ModifierModel.Data modifier_data = saleModifier.getModifier_data();
                                modifier_data.setSelected(true);
                                selectedModifiers.add(modifier_data);
                            }
                        }


                        if (itemModel.getPrice().equals("0")) {
                            itemModel.setPrice(detail.getNet_unit_price());
                        }
                        itemModel.setSelectedModifiers(selectedModifiers);

                        List<ModifierModel> modifiers = new ArrayList<>();
                        for (ModifierModel model : detail.getProduct().getModifiers()) {

                            for (ModifierModel.Data data : model.getModifiers_data()) {
                                if (isModifierExist(data, selectedModifiers)) {

                                    data.setSelected(true);
                                } else {
                                    data.setSelected(false);
                                }

                            }
                            modifiers.add(model);

                        }


                        itemModel.setModifiers(modifiers);
                        itemModel.setDiscounts(discs);
                        itemModel.setAmount(Integer.parseInt(detail.getQty()));

                        items.add(detail.getProduct());
                    }
                    cartList.setItems(items);

                    cartList.setCustomerModel(sale.getCustomer());
                    manageCartModel.assignCustomerToCart(sale.getCustomer(), getApplication().getApplicationContext());
                    if (sale.getDining() != null) {
                        manageCartModel.addDeliveryToCart(sale.getDining().getId(), sale.getDining().getName(), getApplication().getApplicationContext());


                    }


                    return cartList;
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CartList>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(CartList cartList) {
                        manageCartModel.updateCartModel(getApplication().getApplicationContext(), cartList);
                        getCart().setValue(cartList);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private boolean isModifierExist(ModifierModel.Data data, List<ModifierModel.Data> selectedModifiers) {
        for (ModifierModel.Data modifierModel : selectedModifiers) {
            if (modifierModel.getId().equals(data.getId())) {
                return true;
            }
        }

        return false;
    }

    public void addDraftTicketToDelete(OrderModel.Sale sale) {

        int index = getDeletedDraftPos(sale);
        if (sale.isSelected()) {
            if (index == -1) {
                deletedSales.add(sale.getId());

            }


        } else {
            if (index != -1) {
                deletedSales.remove(index);
            }
        }

        getIsDeleteAllDraftTicketsSelected().setValue(getOrders().getValue() != null && deletedSales.size() == getOrders().getValue().size());

        getCanDeleteOpenedTickets().setValue(deletedSales.size() > 0);

    }

    public void addAllDraftTicketToDelete(boolean isChecked) {
        deletedSales.clear();
        if (getOrders().getValue() != null && getOrders().getValue().size() > 0) {
            for (OrderModel.Sale sale : getOrders().getValue()) {
                sale.setSelected(isChecked);
                if (isChecked) {
                    deletedSales.add(sale.getId());
                }
            }
            getOrders().setValue(getOrders().getValue());

        } else {
            getIsDeleteAllDraftTicketsSelected().setValue(false);
        }

        getCanDeleteOpenedTickets().setValue(deletedSales.size() > 0);


    }

    private int getDeletedDraftPos(OrderModel.Sale sale) {
        if (getOrders().getValue() != null) {
            int index = 0;
            for (String id : deletedSales) {
                if (id.equals(sale.getId())) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    public void deleteDraftTickets(Context context) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();


        Api.getService(Tags.base_url)
                .deleteDraftTicket(userModel.getData().getSelectedUser().getId(), deletedSales)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<StatusResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    List<OrderModel.Sale> list = new ArrayList<>();
                                    List<OrderModel.Sale> mainList = new ArrayList<>();

                                    if (getOrders().getValue() != null) {
                                        for (OrderModel.Sale sale : getOrders().getValue()) {
                                            if (!sale.isSelected()) {
                                                list.add(sale);
                                                mainList.add(sale);
                                            }
                                        }
                                        getMainOrders().setValue(mainList);
                                        getOrders().setValue(list);

                                    }
                                    getCanDeleteOpenedTickets().setValue(false);
                                    getIsDeleteAllDraftTicketsSelected().setValue(false);

                                    deletedSales.clear();
                                }
                            }
                        } else {
                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        Log.e(TAG, e.getMessage() + "");

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Toast.makeText(getApplication().getApplicationContext(), R.string.check_network, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplication().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();


                        }
                    }
                });

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
