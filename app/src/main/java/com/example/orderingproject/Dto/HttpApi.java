package com.example.orderingproject.Dto;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 *
 * @param <T> 서버에서 받을 DTO 자료형
 */
public class HttpApi<T> {

    private final URL url;
    private final String httpMethod;
    private HttpURLConnection cnn;
    private ObjectMapper mapper;

    /**
     * @param httpMethod: GET, POST, PUT, UPDATE, DELETE
     */
    public HttpApi(URL url, String httpMethod) {
        this.url = url;
        this.httpMethod = httpMethod;

        init();
    }

    private void init() {
        try {
            cnn = (HttpURLConnection) url.openConnection();
            cnn.setRequestMethod(httpMethod);
            mapper = new ObjectMapper();

            // 서버 응답 Data를 json 형식의 타입으로 요청.
            cnn.setRequestProperty("Accept", "application/json");
            // 서버에 보내는 데이터 형식을 json 형식으로 설정
            cnn.setRequestProperty("Content-Type", "application/json;utf-8");
        } catch (IOException e) {
            // TODO 안드로이드에서 오류 알림창 띄울 것
        }
    }

    /**
     *
     * 서버로 보내는 데이터가 없을 때
     */
    public ResultDto<T> requestToServer() {

        // InputStream 으로  서버로 부터 응답을 받겠다는 설정
        cnn.setDoInput(true);

        try {
            return getResponse();

        } catch (IOException e) {
            // TODO 안드로이드에서 오류 알림창 띄울 것
        }

        return null;
    }

    /**
     *
     * @param dto: 서버로 보내는 객체
     */
    public ResultDto<T> requestToServer(Object dto) {

        cnn.setDoOutput(true);
        cnn.setDoInput(true);

        try {
            // 서버로 보내는 json
            String objJson = mapper.writeValueAsString(dto);
            OutputStream os = cnn.getOutputStream();
            byte[] input = objJson.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);

            Log.e("서버 요청 성공","return");
            return getResponse();
        } catch (IOException e) {
            Log.e("서버 요청 실패 ::",e.toString());// TODO 안드로이드에서 오류 알림창 띄울 것
        }

        return null;
    }

    private ResultDto<T> getResponse() throws IOException {
        InputStream inputStream = cnn.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder =  new StringBuilder();
        String inputLine;

        while ((inputLine = bufferedReader.readLine()) != null) {
            builder.append(inputLine);
        }

        bufferedReader.close();
        String json = builder.toString();

        ResultDto<T> response = mapper.readValue(json, new TypeReference<ResultDto<T>>() { });
        return response;
    }
}