package com.midad_pos.services;

import com.midad_pos.model.CategoryDataModel;
import com.midad_pos.model.CustomerDataModel;
import com.midad_pos.model.DeliveryDataModel;
import com.midad_pos.model.DiscountDataModel;
import com.midad_pos.model.HomeIndexModel;
import com.midad_pos.model.ItemsDataModel;
import com.midad_pos.model.OrderDataModel;
import com.midad_pos.model.PaymentDataModel;
import com.midad_pos.model.ShiftDataModel;
import com.midad_pos.model.ShiftsDataModel;
import com.midad_pos.model.SingleCategoryData;
import com.midad_pos.model.SingleCustomerModel;
import com.midad_pos.model.SinglePayInOutData;
import com.midad_pos.model.StatusResponse;
import com.midad_pos.model.UserModel;
import com.midad_pos.model.cart.CartModel;

import java.util.List;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
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
    @POST("api/auth/register")
    Single<Response<UserModel>> signUp(@Field("email") String email,
                                       @Field("password") String password,
                                       @Field("phone_number") String phone_number,
                                       @Field("full_name") String full_name

    );


    @GET("api/auth/getProfile")
    Single<Response<UserModel>> getProfile(@Query("user_id") String user_id);


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
                                             @Part("warehouse_id") RequestBody warehouse_id,
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

    @GET("api/customers")
    Single<Response<CustomerDataModel>> getCustomers(@Query("user_id") String user_id);

    @FormUrlEncoded
    @POST("api/customers")
    Single<Response<SingleCustomerModel>> addCustomer(@Field("user_id") String user_id,
                                                      @Field("name") String name,
                                                      @Field("email") String email,
                                                      @Field("phone_number") String phone_number,
                                                      @Field("address") String address,
                                                      @Field("city") String city,
                                                      @Field("postal_code") String postal_code,
                                                      @Field("country") String country,
                                                      @Field("tax_number") String tax_number
    );

    @GET("api/setting/dinner")
    Single<Response<DeliveryDataModel>> getDining(@Query("user_id") String user_id,
                                                  @Query("warehouse_id") String warehouse_id
    );

    @GET("api/setting/payments")
    Single<Response<PaymentDataModel>> getPayments(@Query("user_id") String user_id,
                                                   @Query("warehouse_id") String warehouse_id
    );


    @FormUrlEncoded
    @POST("api/shift/storeShift")
    Single<Response<ShiftDataModel>> openShift(@Field("user_id") String user_id,
                                               @Field("cash_in_hand") String cash_in_hand,
                                               @Field("warehouse_id") String warehouse_id,
                                               @Field("pos_id") String pos_id
    );

    @FormUrlEncoded
    @POST("api/shift/closeShift")
    Single<Response<StatusResponse>> closeShift(@Field("user_id") String user_id,
                                                @Field("shift_id") String shift_id,
                                                @Field("actual_cash") String actual_cash,
                                                @Field("expected_cash") String expected_cash


    );

    @GET("api/shift/currentShift")
    Single<Response<ShiftDataModel>> currentShift(@Query("user_id") String user_id,
                                                  @Query("warehouse_id") String warehouse_id,
                                                  @Query("pos_id") String pos_id
    );


    @POST("api/orders/storeOrder")
    Single<Response<StatusResponse>> storeOrder(@Body CartModel cartModel);

    @FormUrlEncoded
    @POST("api/shift/storeShiftData")
    Single<Response<SinglePayInOutData>> payInOut(@Field("user_id") String user_id,
                                                  @Field("cash_register_id") String cash_register_id,
                                                  @Field("amount") String amount,
                                                  @Field("comment") String comment,
                                                  @Field("type") String type

    );

    @GET("api/shift/allShifts")
    Single<Response<ShiftsDataModel>> shifts(@Query("user_id") String user_id,
                                             @Query("warehouse_id") String warehouse_id,
                                             @Query("pos_id") String pos_id
    );

    @GET("api/orders/orders")
    Single<Response<OrderDataModel>> getOrders(@Query("user_id") String user_id
    );

    @GET("api/orders/draftOrders")
    Single<Response<OrderDataModel>> getDraftOrders(@Query("user_id") String user_id
    );


    @FormUrlEncoded
    @POST("api/orders/deleteOrders")
    Single<Response<StatusResponse>> deleteDraftTicket(@Field("user_id") String user_id,
                                                       @Field("saleIds[]") List<String> ids


    );

    @FormUrlEncoded
    @POST("api/auth/logout")
    Single<Response<StatusResponse>> logout(@Field("user_id") String user_id,
                                            @Field("pos_id") String pos_id


    );

    @FormUrlEncoded
    @POST("api/auth/sendEmail")
    Single<Response<StatusResponse>> forgotPassword(@Field("email") String email

    );
}