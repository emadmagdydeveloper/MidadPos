package com.midad_pos.services;

import com.midad_pos.model.UserModel;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Service {

    @FormUrlEncoded
    @POST("api/auth/login")
    Single<Response<UserModel>> login(@Field("email") String email,
                                      @Field("password") String password);
}