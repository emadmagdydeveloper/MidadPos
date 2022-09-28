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


import com.midad_pos.R;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.midad_pos.model.ItemModel;
import com.midad_pos.model.ModifierModel;

import java.util.Locale;

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

    @BindingAdapter({"numberFormat","type"})
    public static void numFormat(TextView view,String num,String type) {
        if (num != null) {
            if (type!=null){
                String newFormat;
                if (type.equals("per")){
                    newFormat = num + "%";
                }else {
                    newFormat = String.format(Locale.US, "%.2f", Double.parseDouble(num));
                }
                view.setText(newFormat);
            }else {
                String newFormat = String.format(Locale.US, "%.2f", Double.parseDouble(num));

                view.setText(newFormat);
            }

        }

    }
}










