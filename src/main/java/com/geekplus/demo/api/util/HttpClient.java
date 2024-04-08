package com.geekplus.demo.api.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;

import org.apache.tomcat.util.http.fileupload.IOUtils;

@Slf4j
public class HttpClient {


    public static String sendPostRequest(String requestUrl, String data, HttpHeader[] headers) {
        // 这里使用原生的jdk方式， 正式环境建议使用带有HTTP连接池的第三方jar包， 如 okhttp
        return jdkWay(requestUrl, data, headers);
    }

    private static String jdkWay(String requestUrl, String data, HttpHeader[] headers) {
        StringBuilder sb = new StringBuilder("");
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(30000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("POST");
            if (headers != null) {
                for (HttpHeader header : headers) {
                    urlConnection.addRequestProperty(header.getKey(), header.getValue());
                }
            }
            OutputStream outputStream = urlConnection.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            writer.write(data);
            writer.flush();
            writer.close();
            int responseCode = urlConnection.getResponseCode();
            if (200 == responseCode) {
                inputStream = urlConnection.getInputStream();
            } else {
                inputStream = urlConnection.getErrorStream();
            }
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("error:", e);
        } finally {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(inputStreamReader);
            IOUtils.closeQuietly(inputStream);
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return "";
    }

}
