package com.example.aaa;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
public interface Api {


    @FormUrlEncoded
    @POST("api/v1/register")
    Call<JSONObject> createuser(
            @Field("fullName") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("dob") String dob,
            @Field("country") String country,
            @Field("state") String state,
            @Field("city") String city,
            @Field("phone") String phone
            );
}