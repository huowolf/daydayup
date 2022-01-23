## okhttp 配置

```java
        client = new OkHttpClient.Builder()
              	.addInterceptor(new GzipRequestInterceptor())
				.connectionPool(new ConnectionPool(10, 500, TimeUnit.MILLISECONDS))
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .authenticator(new Authenticator() {
                    @Nullable
                    @Override
                    public okhttp3.Request authenticate(Route route, okhttp3.Response response) {
                        String credential = Credentials.basic(userName, passwd);
                        return response.request().newBuilder().header("Authorization", credential).build();
                    }

                }).build();
```

## 参考

https://square.github.io/okhttp/recipes/