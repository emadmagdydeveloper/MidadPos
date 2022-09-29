package com.midad_pos.services;

import com.midad_pos.model.CategoryDataModel;
import com.midad_pos.model.DiscountDataModel;
import com.midad_pos.model.HomeIndexModel;
import com.midad_pos.model.ItemsDataModel;
import com.midad_pos.model.SingleCategoryData;
import com.midad_pos.model.StatusResponse;
import com.midad_pos.model.UserModel;

import java.util.List;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {

    @FormUrlEncoded
    @POST("api/auth/login")
    Single<Response<UserModel>> login(@Field("email") String email,
                                      @Field("password") String password);

    @FormUrlEncoded
    @POST("api/items/storeCategory")
    Single<Response<SingleCategoryData>> addCategory(@Field("user_id") String user_id,
                                                     @Field("name") String name,
                                                     @Field("color") String color
    );

    @FormUrlEncoded
    @POST("api/items/deleteCategories")
    Single<Response<StatusResponse>> deleteCategories(@Field("user_id") String user_id,
                                                      @Field("category_ids[]") List<Integer> ids);

    @FormUrlEncoded
    @POST("api/items/deleteDiscounts")
    Single<Response<StatusResponse>> deleteDiscounts(@Field("user_id") String user_id,
                                                     @Field("discount_ids[]") List<Integer> ids);

    @FormUrlEncoded
    @POST("api/items/updateCategory")
    Single<Response<StatusResponse>> updateCategory(@Field("category_id") String category_id,
                                                    @Field("user_id") String user_id,
                                                    @Field("name") String name,
                                                    @Field("color") String color);


    @GET("api/home/categories")
    Single<Response<CategoryDataModel>> categories(@Query("user_id") String user_id);

    @GET("api/home/index")
    Single<Response<HomeIndexModel>> homeIndex(@Query("user_id") String user_id);

    @Multipart
    @POST("api/items/storeItems")
    Single<Response<StatusResponse>> addItem(@Part("user_id") RequestBody user_id,
                                             @Part("name") RequestBody name,
                                             @Part("category_id") RequestBody category_id,
                                             @Part("code") RequestBody code,
                                             @Part("price") RequestBody price,
                                             @Part("cost") RequestBody cost,
                                             @Part("unit_id") RequestBody unit_id,
                                             @Part("tax_id") RequestBody tax_id,
                                             @Part("barcode_symbology") RequestBody barcode_symbology,
                                             @Part("image_type") RequestBody image_type,
                                             @Part("color") RequestBody color,
                                             @Part("shape") RequestBody shape,
                                             @Part("modifier_ids[]") List<RequestBody> modifiers,
                                             @Part List<MultipartBody.Part> images
    );

    @GET("api/home/items")
    Single<Response<ItemsDataModel>> items(@Query("user_id") String user_id,
                                           @Query("warehouse_id") String warehouse_id);

    @FormUrlEncoded
    @POST("api/items/deleteItems")
    Single<Response<StatusResponse>> deleteItems(@Field("user_id") String user_id,
                                                 @Field("item_ids[]") List<Integer> ids);

    @FormUrlEncoded
    @POST("api/items/assignItemsToCategory")
    Single<Response<StatusResponse>> assignItems(@Field("user_id") String user_id,
                                                 @Field("category_id") String category_id,
                                                 @Field("products_id[]") List<String> ids);

    @GET("api/home/discounts")
    Single<Response<DiscountDataModel>> discount(@Query("user_id") String user_id,
                                                 @Query("warehouse_id") String warehouse_id);

    @FormUrlEncoded
    @POST("api/discount/storeDiscount")
    Single<Response<StatusResponse>> addDiscount(@Field("user_id") String user_id,
                                                 @Field("title") String title,
                                                 @Field("type") String type,
                                                 @Field("value") String value,
                                                 @Field("for_all") String for_all
    );

    @FormUrlEncoded
    @POST("api/discount/updateDiscount")
    Single<Response<StatusResponse>> updateDiscount(@Field("user_id") String user_id,
                                                    @Field("discount_id") String discount_id,
                                                    @Field("title") String title,
                                                    @Field("type") String type,
                                                    @Field("value") String value,
                                                    @Field("for_all") String for_all
    );


}