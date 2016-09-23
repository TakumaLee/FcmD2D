package com.kuma.sample.fcmd2d.http;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CipherSuite;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

/**
 * Created by TakumaLee on 15/6/22.
 */
public class OkHttpClientConnect {
    private static final String TAG = OkHttpClientConnect.class.getSimpleName();

    public static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
    public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded; charset=UTF-8";

    static ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
            .build();
    private static ConnectionPool threadPoolExecutor = new ConnectionPool(5, 15000, TimeUnit.MILLISECONDS);

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().connectionPool(threadPoolExecutor).build();

    public static void excuteAutoGet(String url) {
        if (url.contains("https")) {
            excuteSSLGet(url);
        } else {
            excuteGet(url, null);
        }
    }

    public static void excuteAutoGet(String url, NetworkCallback callback) {
        if (url.contains("https")) {
            excuteSSLGet(url, callback);
        } else {
            excuteGet(url, callback);
        }
    }

    public static void excuteAutoPost(String url, String json, @Nullable NetworkCallback callback) {
        if (url.contains("https")) {
            excuteSSLPost(url, json, CONTENT_TYPE_FORM_URLENCODED, callback);
        } else {
            excutePost(url, json, CONTENT_TYPE_FORM_URLENCODED, callback);
        }
    }

    public static void excuteAutoPost(String url, String json, String contentType, @Nullable NetworkCallback callback) {
        if (url.contains("https")) {
            excuteSSLPost(url, json, contentType, callback);
        } else {
            excutePost(url, json, contentType, callback);
        }
    }

    public static void excuteAutoPost(String header, String headerValue, String url, String json, String contentType, @Nullable NetworkCallback callback) {
        if (url.contains("https")) {
            excuteSSLPost(header, headerValue, url, json, contentType, callback);
        } else {
            excutePost(header, headerValue, url, json, contentType, callback);
        }
    }

    public static void excuteAutoMultiPartRequest(String url, RequestBody body, NetworkCallback callback) {
        if (url.contains("https")) {
            excuteSSLMultiPartRequest(url, body, callback);
        } else {
            excuteMultiPartRequest(url, body, callback);
        }
    }

    private static void excuteSSLPost(String url, String json) {
        initSSL();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(null);
    }

    private static void excuteSSLPost(String url, String json, String contentType, final NetworkCallback callback) {
        initSSL();
        RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", contentType)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response.code(), response.body().string());
            }
        });
    }

    private static void excutePost(String url, String json, String contentType, final NetworkCallback callback) {
        RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", contentType)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response.code(), response.body().string());
            }
        });
    }

    private static void excuteSSLPost(String header, String headerValue, String url, String json, String contentType, final NetworkCallback callback) {
        initSSL();
        RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader(header, headerValue)
                .addHeader("Content-Type", contentType)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response.code(), response.body().string());
            }
        });
    }

    private static void excutePost(String header, String headerValue, String url, String json, String contentType, final NetworkCallback callback) {
        RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader(header, headerValue)
                .addHeader("Content-Type", contentType)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response.code(), response.body().string());
            }
        });
    }

    private static void excuteGet(String url, final NetworkCallback callback) {
        Request request = new Request.Builder()
                .tag(url)
                .url(url)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("User-Agent", "Mozilla/5.0")
                .build();
        //okHttpClient.newCall(request).enqueue(callback);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response.code(), response.body().string());
            }
        });
    }

    private static void excuteSSLGet(String url) {
        initSSL();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("User-Agent", "Mozilla/5.0")
                .build();
        okHttpClient.newCall(request).enqueue(null);
    }

    private static void excuteSSLGet(String url, final NetworkCallback callback) {
        initSSL();
        Request request = new Request.Builder()
                .tag(url)
                .url(url)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("User-Agent", "Mozilla/5.0")
                .build();
        //okHttpClient.newCall(request).enqueue(callback);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response.code(), response.body().string());
            }
        });
    }

    private static void excuteMultiPartRequest(String url, RequestBody requestBody, final NetworkCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response.code(), response.body().string());
            }
        });

    }

    private static void excuteSSLMultiPartRequest(String url, RequestBody requestBody, final NetworkCallback callback) {
        initSSL();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response.code(), response.body().string());
            }
        });

    }

    public static void initSSL(OkHttpClient okHttpClient) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
//                    Log.v(TAG, "SSL verify: " + session.getCipherSuite());
//                    if (hostname.equals("dramarket.chocolabs.com") && session.getCipherSuite().equals(spec.cipherSuites().get(1).name())) {
                        return true;
//                    }
//                    return false;
                }
            });

            okHttpClient = builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void initSSL() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
//                    Log.v(TAG, "SSL verify: " + session.getCipherSuite());
//                    if (hostname.equals("dramarket.chocolabs.com") && session.getCipherSuite().equals(spec.cipherSuites().get(1).name())) {
                        return true;
//                    }
//                    return false;
                }
            });

            okHttpClient = builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
