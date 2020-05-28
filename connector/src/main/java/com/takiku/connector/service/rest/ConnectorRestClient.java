package com.takiku.connector.service.rest;


import domain.ResultWrapper;
import po.BaseResponse;
import po.Offline;
import po.ShakeHands;
import po.UserCertification;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

/**
 * Date: 2019-05-28
 * Time: 00:18
 *
 * @author yrw
 */
public interface ConnectorRestClient {

    @GET("api/offline/poll/{id}")
    Call<BaseResponse<List<Offline>>> pollOfflineMsg(@Path("id") String userId);

    @POST("api/user/auth")
    Call<BaseResponse<UserCertification>> auth(@Body ShakeHands shakeHands);
}
