package com.midad_pos.general_ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;


import com.google.android.material.textfield.TextInputEditText;
import com.midad_pos.R;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.midad_pos.model.CustomerModel;
import com.midad_pos.model.DiscountModel;
import com.midad_pos.model.ItemModel;
import com.midad_pos.model.ModifierModel;
import com.midad_pos.model.ShiftModel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
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
}










