package com.midad_pos.services;

import com.midad_pos.model.CategoryDataModel;
import com.midad_pos.model.HomeIndexModel;
import com.midad_pos.model.StatusResponse;
import com.midad_pos.model.UserModel;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Service {

    @FormUrlEncoded
    @POST("api/auth/login")
    Single<Response<UserModel>> login(@Field("email") String email,
                                      @Field("password") String password);

    @FormUrlEncoded
    @POST("api/items/storeCategory")
    Single<Response<StatusResponse>> addCategory(@Field("user_id") String user_id,
                                                 @Field("name") String name,
                                                 @Field("color") String color
    );

    @GET("api/home/categories")
    Single<Response<CategoryDataModel>> categories(@Query("user_id") String user_id);

    @GET("api/home/index")
    Single<Response<HomeIndexModel>> homeIndex(@Query("user_id") String user_id);

}