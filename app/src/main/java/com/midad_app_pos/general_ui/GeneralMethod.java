package com.midad_app_pos.general_ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;


import com.google.android.material.textfield.TextInputEditText;
import com.midad_app_pos.R;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.midad_app_pos.model.CustomerModel;
import com.midad_app_pos.model.DiscountModel;
import com.midad_app_pos.model.ItemModel;
import com.midad_app_pos.model.ModifierModel;
import com.midad_app_pos.model.OrderModel;
import com.midad_app_pos.model.PrinterModel;
import com.midad_app_pos.model.ShiftModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class GeneralMethod {


    @BindingAdapter("image")
    public static void image(View view, String imageUrl) {

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);


                if (view instanceof CircleImageView) {
                    CircleImageView imageView = (CircleImageView) view;
                    if (imageUrl != null) {
                        RequestOptions options = new RequestOptions().override(view.getWidth(), view.getHeight());
                        Glide.with(view.getContext()).asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .load(imageUrl)
                                .centerCrop()
                                .apply(options)
                                .into(imageView);
                    }
                } else if (view instanceof RoundedImageView) {
                    RoundedImageView imageView = (RoundedImageView) view;

                    if (imageUrl != null) {

                        RequestOptions options = new RequestOptions().override(view.getWidth(), view.getHeight());
                        Glide.with(view.getContext()).asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .load(imageUrl)
                                .centerCrop()
                                .apply(options)
                                .into(imageView);

                    }
                } else if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;

                    if (imageUrl != null) {

                        RequestOptions options = new RequestOptions().override(view.getWidth(), view.getHeight());
                        Glide.with(view.getContext()).asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .load(imageUrl)
                                .centerCrop()
                                .apply(options)
                                .into(imageView);
                    }
                }

            }
        });


    }


    @BindingAdapter("color")
    public static void color(CardView view, String color) {
        if (color != null) {
            try {
                int c = Color.parseColor(color);
                view.setCardBackgroundColor(c);
            } catch (Exception e) {
                view.setCardBackgroundColor(view.getContext().getResources().getColor(R.color.cat1));

            }

        }

    }


    @BindingAdapter("extra")
    public static void extra(TextView view, ModifierModel model) {
        if (model != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (ModifierModel.Data data : model.getModifiers_data()) {
                stringBuilder.append(data.getTitle());
                stringBuilder.append(",");
            }

            int length = stringBuilder.length();
            int lastIndex = stringBuilder.lastIndexOf(",");
            if (lastIndex == (length - 1)) {
                String substring = stringBuilder.substring(0, length - 1);
                view.setText(substring);
            } else {
                view.setText(stringBuilder.toString());
            }
        }

    }


    @BindingAdapter("imageColor")
    public static void imageColor(ImageView view, ItemModel model) {
        if (model != null) {
            if (model.getImage_type().equals("color")) {
                switch (model.getShape()) {
                    case "1":
                        view.setImageResource(R.drawable.ic_fill_hard_circle);
                        break;
                    case "2":
                        view.setImageResource(R.drawable.ic_fill_square);
                        break;
                    case "3":
                        view.setImageResource(R.drawable.ic_fill_circle);
                        break;
                    case "4":
                        view.setImageResource(R.drawable.ic_fill_6_shape);
                        break;
                }

                try {
                    view.setColorFilter(Color.parseColor(model.getColor()));

                } catch (Exception e) {
                    view.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.cat1));

                }

            }
        }

    }

    @BindingAdapter("textItemColor")
    public static void textColor(TextView view, ItemModel model) {
        if (model != null) {
            if (model.getImage_type().equals("color")) {
                @SuppressLint("ResourceType") String color = view.getContext().getResources().getString(R.color.cat1);
                if (model.getColor().equalsIgnoreCase(color)) {
                    view.setTextColor(ContextCompat.getColor(view.getContext(), R.color.black));
                } else {
                    view.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));

                }

            }
        }

    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("variant")
    public static void variantPrice(TextView view, ItemModel model) {
        if (model != null) {
            if (model.isIs_variant()) {
                view.setText(model.getVariants().size() + " " + view.getContext().getString(R.string.variants));
            } else {
                String price = model.getPrice();

                view.setText(model.getPrice().equals("0") ? view.getContext().getString(R.string.variable) : String.format(Locale.US, "%.2f", Float.parseFloat(price)));
            }
        }

    }

    @BindingAdapter({"numberFormat", "type"})
    public static void numFormat(View view, String num, String type) {
        if (num != null) {
            if (type != null) {
                String newFormat;
                if (type.equals("per")) {
                    newFormat = num + "%";
                } else {
                    newFormat = String.format(Locale.US, "%.2f", Double.parseDouble(num));
                }
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    textView.setText(newFormat);
                } else if (view instanceof TextInputEditText) {
                    EditText editText = (EditText) view;
                    editText.setText(newFormat);

                }

            } else {
                String newFormat = String.format(Locale.US, "%.2f", Double.parseDouble(num));

                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    textView.setText(newFormat);
                } else if (view instanceof TextInputEditText) {
                    EditText editText = (EditText) view;
                    editText.setText(newFormat);

                }
            }

        }

    }


    @BindingAdapter("amountFormat")
    public static void amountFormat(TextView view, String amountFormat) {
        if (amountFormat != null) {
            amountFormat = amountFormat.replace(",", "");
            String newFormat = String.format(Locale.US, "%.2f", Double.parseDouble(amountFormat));
            view.setText(newFormat);
        }

    }


    @BindingAdapter("discountTitle")
    public static void discountTitle(TextView view, DiscountModel model) {
        if (model != null) {
            String type = model.getType();
            if (type != null) {
                String newFormat;
                if (type.equals("per")) {
                    newFormat = model.getValue() + "%";
                } else {
                    newFormat = String.format(Locale.US, "%.2f", Double.parseDouble(model.getValue()));
                }
                view.setText(newFormat);
            } else {
                String newFormat = String.format(Locale.US, "%.2f", Double.parseDouble(model.getValue()));

                view.setText(model.getTitle() + "," + newFormat);
            }

        }

    }

    @BindingAdapter("itemTotal")
    public static void itemTotal(TextView view, ItemModel model) {
        if (model != null) {
            String newFormat = String.format(Locale.US, "%.2f", model.getTotalPrice());

            view.setText(model.getName() + " " + newFormat);

        }

    }

    @BindingAdapter("iconTint")
    public static void iconTint(ImageView view, boolean isSelected) {

        if (isSelected) {
            view.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.grey17));
        } else {
            view.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.grey14));

        }

    }

    @BindingAdapter("cartItemExtra")
    public static void cartItemTotal(TextView view, ItemModel model) {
        if (model != null) {
            StringBuilder builder = new StringBuilder();
            for (ModifierModel.Data data : model.getSelectedModifiers()) {
                builder.append(data.getTitle());
                builder.append(",");
            }
            String data = builder.toString();
            if (data.endsWith(",")) {
                data = data.substring(0, data.lastIndexOf(","));
            }

            view.setText(data);

        }

    }

    @BindingAdapter("customerName")
    public static void customerName(TextView view, CustomerModel model) {
        if (model != null) {
            if (model.getName() != null && !model.getName().isEmpty()) {
                view.setText(model.getName());
            } else if (model.getEmail() != null && !model.getEmail().isEmpty()) {
                view.setText(model.getEmail());
            } else if (model.getPhone_number() != null && !model.getPhone_number().isEmpty()) {
                view.setText(model.getPhone_number());
            }

        }

    }

    @BindingAdapter("time")
    public static void time(TextView view, String date) {
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date parse = format.parse(date);
                if (parse!=null){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                    simpleDateFormat.setTimeZone(TimeZone.getDefault());
                    String t = simpleDateFormat.format(parse);
                    view.setText(t);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

    @BindingAdapter("payInOutAmount")
    public static void payInOutAmount(TextView view, ShiftModel.PayInOutModel model) {
        if (model != null) {
            String amount = String.format(Locale.US,"%.2f",model.getAmount());
            if (model.getType().equalsIgnoreCase("out")) {
                amount = "-" + amount;
            }

            view.setText(amount);

        }

    }

    @BindingAdapter({"shiftStartEndDate","lang"})
    public static void shiftStartDate(TextView view,ShiftModel model,String lang) {
        if (model != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale(lang));
            try {
                Date parse = format.parse(model.getCreated_at());
                String t ="";
                if (parse!=null){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd", new Locale(lang));
                     t = simpleDateFormat.format(parse);
                }

                Date parse2 = format.parse(model.getUpdated_at());
                String t2="";
                if (parse2!=null){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd", new Locale(lang));
                    t2 = simpleDateFormat.format(parse2);
                }

                view.setText(t+" - "+t2);



            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

    @BindingAdapter({"shiftStartEndTime","lang"})
    public static void shiftStartTime(TextView view,ShiftModel model,String lang) {
        if (model != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale(lang));
            try {
                Date parse = format.parse(model.getCreated_at());
                String t ="";
                if (parse!=null){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", new Locale(lang));
                    t = simpleDateFormat.format(parse);
                }

                Date parse2 = format.parse(model.getUpdated_at());
                String t2="";
                if (parse2!=null){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", new Locale(lang));
                    t2 = simpleDateFormat.format(parse2);
                }

                view.setText(t+" - "+t2);



            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

    @BindingAdapter({"shiftDate","lang"})
    public static void shiftDate(TextView view,ShiftModel model,String lang) {
        if (model != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale(lang));
            try {
                Date parse = format.parse(model.getCreated_at());
                String t ="";
                if (parse!=null){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", new Locale(lang));
                    t = simpleDateFormat.format(parse);
                }


                view.setText(t);



            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

    @BindingAdapter({"receiptTime","lang"})
    public static void receiptTime(TextView view,String date,String lang) {
        if (date != null&&lang!=null) {
            String t ="";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale(lang));
            try {
                Date parse = simpleDateFormat.parse(date);
                SimpleDateFormat format = new SimpleDateFormat("hh:mm a", new Locale(lang));
                if (parse != null) {
                    t = format.format(parse);
                    view.setText(t);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


            view.setText(t);


        }



    }

    @BindingAdapter({"receiptDate","lang"})
    public static void receiptDate(TextView view,String date,String lang) {
        if (date != null&&lang!=null) {
            String t ="";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale(lang));
            try {
                Date parse = simpleDateFormat.parse(date);
                String pattern= "";
                if (lang.equals("ar")){
                    pattern = "EEE ،dd MMM ،yyyy";
                }else {
                    pattern = "EEE ,dd MMM ,yyyy";

                }
                SimpleDateFormat format = new SimpleDateFormat(pattern, new Locale(lang));
                if (parse != null) {
                    t = format.format(parse);
                    view.setText(t);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


            view.setText(t);


        }

    }

    @BindingAdapter("paymentIcon")
    public static void paymentIcon(ImageView view, OrderModel.Sale model) {
        if (model !=null) {
            if (model.getPayments().size()==1){
                if (model.getPayments().get(0).getPayment().getType().equalsIgnoreCase("cash")){
                    view.setImageResource(R.drawable.ic_cash);
                }else if (model.getPayments().get(0).getPayment().getType().equals("card")){
                    view.setImageResource(R.drawable.ic_card);

                }else {
                    view.setImageResource(R.drawable.ic_pay_other);
                }
            }else if (model.getPayments().size()>1){
                view.setImageResource(R.drawable.ic_cash_card);


            }


        }

    }

    @BindingAdapter({"timeStampDate","lang"})
    public static void timeStampDate(TextView view,String date,String lang) {
        if (date != null&&lang!=null) {
            String t ="";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", new Locale(lang));
            t = simpleDateFormat.format(new Date(Long.parseLong(date)));


            view.setText(t);


        }

    }

    @BindingAdapter({"timeStampDate2","lang"})
    public static void timeStampDate2(TextView view,String date,String lang) {
        if (date != null&&lang!=null) {
            String t ="";
            String format = "";
            if (lang.equals("ar")){
                format ="EEE، dd MMM،yyyy";
            }else {
                format ="EEE, dd MMM,yyyy";

            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, new Locale(lang));
            t = simpleDateFormat.format(new Date(Long.parseLong(date)));


            view.setText(t);


        }

    }

    @BindingAdapter({"timeStampTicketOwner","lang"})
    public static void timeStampTicketOwner(TextView view,OrderModel.Sale model,String lang) {
        if (model != null&&lang!=null) {
            String t ="";
            String format = "";
            if (lang.equals("ar")){
                format ="EEE، dd MMM،yyyy";
            }else {
                format ="EEE, dd MMM,yyyy";

            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, new Locale(lang));
            t = simpleDateFormat.format(new Date(Long.parseLong(model.getDate())))+", "+model.getUser().getName();


            view.setText(t);


        }

    }

    @BindingAdapter({"timeStampTime","lang"})
    public static void timeStampTime(TextView view,String date,String lang) {
        if (date != null&&lang!=null) {
            String t ="";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", new Locale(lang));
            t = simpleDateFormat.format(new Date(Long.parseLong(date)));


            view.setText(t);


        }

    }

    @BindingAdapter({"timeStamp","lang"})
    public static void timeStamp(TextView view,String date,String lang) {
        if (date != null&&lang!=null) {
            String t ="";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", new Locale(lang));
            t = simpleDateFormat.format(new Date(Long.parseLong(date)));


            view.setText(t);


        }

    }

    @BindingAdapter("orderExtras")
    public static void orderExtras(TextView view,OrderModel.Detail detail) {
        if (detail!=null){
            StringBuilder text = new StringBuilder();
            for (OrderModel.SaleModifierData data:detail.getSale_modifiers()){
                for (OrderModel.SaleModifier modifier: data.getSale_modifier_data()){
                    text.append("+ ").append(modifier.getModifier_data().getTitle()).append("(").append(String.format(Locale.US,"%.2f",Double.parseDouble(modifier.getModifier_data().getCost()))).append(")\n");
                }
            }
            String data = text.toString();
            if (text.lastIndexOf("\n")!=-1&&text.lastIndexOf("\n")==(text.length()-1)){
                data = text.substring(0,text.length()-1);
            }
            view.setText(data);
        }

    }

    @BindingAdapter("orderItemAmount")
    public static void orderItemAmount(TextView view,OrderModel.Detail detail) {
        if (detail!=null){
            String price = String.format(Locale.US,"%.2f",Double.parseDouble(detail.getNet_unit_price()));
            String amount = detail.getQty()+" X "+price;
            view.setText(amount);
        }

    }

    @BindingAdapter("discount")
    public static void discount(TextView view,OrderModel.OrderDiscount model) {
        if (model!=null){
            String price = String.format(Locale.US,"%.2f",Double.parseDouble(model.getGrand_total()));
            String amount =price+"-";
            view.setText(amount);
        }

    }

    @BindingAdapter("printerType")
    public static void printerType(TextView view, PrinterModel model) {
        Context context = view.getContext();
        if (model!=null){
            if (model.getPrinter_type().equals("sunmi")){
                view.setText("Sunmi");
            }else if (model.getPrinter_type().equals("kitchen")){
                if (model.getBluetooth_name()!=null){
                    String text = context.getString(R.string.kitchen_display) + " - " + model.getBluetooth_name();
                    view.setText(text);

                }else {
                    view.setText(context.getString(R.string.kitchen_display));

                }

            }else if (model.getPrinter_type().equals("other")){
                if (model.getBluetooth_name()!=null){
                    String text = context.getString(R.string.other_model) + " - " + model.getBluetooth_name();
                    view.setText(text);

                }else {
                    view.setText(context.getString(R.string.other_model));

                }

            }
        }

    }




}










