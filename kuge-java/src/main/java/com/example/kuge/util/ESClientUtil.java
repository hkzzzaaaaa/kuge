package com.example.kuge.util;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ESClientUtil {
    private static RestHighLevelClient client;
    public static RestHighLevelClient getClient() {
        client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
                        .setRequestConfigCallback(requestConfigBuilder -> {
                            // 设置连接超时为30秒
                            requestConfigBuilder.setConnectTimeout(30000);
                            // 设置套接字超时为30秒
                            requestConfigBuilder.setSocketTimeout(30000);
                            return requestConfigBuilder;
                        })
        );
        return client;
    }
    public static void closeClient() throws IOException {
        if (client != null) {
            client.close();
        }
    }
}

