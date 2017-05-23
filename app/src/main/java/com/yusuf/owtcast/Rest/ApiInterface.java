package com.yusuf.owtcast.Rest;

import com.squareup.okhttp.ResponseBody;
import com.yusuf.owtcast.Model.AdInfo;
import com.yusuf.owtcast.Model.AdLogResponse;
import com.yusuf.owtcast.Model.OwtResponse;
import com.yusuf.owtcast.Model.UserInfo;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by DragonWarrior on 1/29/2017.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("API/request.php/signup")
    Call<OwtResponse> signup(@Field("firstname") String firstName,
                             @Field("lastname") String lastName,
                             @Field("DriverMobile") String driverMobile,
                             @Field("DriverEmail") String driverEmail,
                             @Field("DriverCity") String driverCity,
                             @Field("DriverRef") String driverRef,
                             @Field("password")  String password);

    @FormUrlEncoded
    @POST("API/request.php/login")
    Call<UserInfo> login(@Field("DriverMobile") String driverMobile,
                         @Field("password") String password);

    @FormUrlEncoded
    @POST("API/request.php/banking")
    Call<OwtResponse> banking(@Field("Bank") String bank,
                              @Field("Branch") String branch,
                              @Field("Type") String type,
                              @Field("Account") String account,
                              @Field("Driver") String driver);

    @FormUrlEncoded
    @POST("API/request.php/trip")
    Call<List<AdInfo>> tripLog(@Field("driver") String driver,
                               @Field("Latitude") String latitude,
                               @Field("Longitude") String longitude,
                               @Field("slots") String slots);

    @FormUrlEncoded
    @POST("API/request.php/exit")
    Call<OwtResponse> tripEnd(@Field("Trip") String trip);

    @FormUrlEncoded
    @POST("API/request.php/ads")
    Call<AdLogResponse> adLog(@Field("Trip") String trip,
                              @Field("Ad") String ad,
                              @Field("Latitude") String latitude,
                              @Field("Longitude") String longitude);


    @FormUrlEncoded
    @POST("API/request.php/plays")
    Call<OwtResponse> adQuality(@Field("Correlation") String correlation,
                                @Field("Volume") String volume,
                                @Field("Image") String image,
                                @Field("Play") String play);



    @POST
    @Streaming
    Call<okhttp3.ResponseBody> downloadAudio(@Url String fileUrl);

    @POST
    @Streaming
    Call<okhttp3.ResponseBody> downloadImage(@Url String fileUrl);

    @Multipart
    @POST("API/image")
    Call<okhttp3.ResponseBody> uploadImage(@Part("description")RequestBody description, @Part MultipartBody.Part file);
}
