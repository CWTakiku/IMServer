package rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import exception.IMException;
import okhttp3.logging.HttpLoggingInterceptor;
import po.BaseResponse;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

/**
 * Date: 2019-04-21
 * Time: 16:45
 *
 * @author yrw
 */
public abstract class AbstractRestService<R> {

    protected R restClient;

    public AbstractRestService(Class<R> clazz, String url) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        // 设置时间格式
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                        //.setDateFormat(dateFormat.toPattern())
                        // 设置一个过滤器，数据库级别的Model不进行Json转换
                        .create()))
                .build();

        this.restClient = retrofit.create(clazz);
    }

    protected <T> T doRequest(RestFunction<T> function) {
        try {
            Response<BaseResponse<T>> response = function.doRequest();
            if (!response.isSuccessful()) {
                throw new IMException("[rest service] status is not 200, response body: " + response.toString());
            }
            if (response.body() == null) {
                throw new IMException("[rest service] response body is null");
            }
            if (response.body().getCode() != BaseResponse.SUCCESS) {
                throw new IMException("[rest service] status is not 200, response body: " + new ObjectMapper().writeValueAsString(response.body()));
            }
            return response.body().getData();
        } catch (IOException e) {
            throw new IMException("[rest service] has error", e);
        }
    }

    @FunctionalInterface
    protected interface RestFunction<T> {
        /**
         * 执行一个http请求
         *
         * @return
         * @throws IOException
         */
        Response<BaseResponse<T>> doRequest() throws IOException;
    }
}
