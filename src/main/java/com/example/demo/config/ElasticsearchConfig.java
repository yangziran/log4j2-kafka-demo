package com.example.demo.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch Config
 * @author nature
 * @version 1.0 2020/11/11
 */
@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.hostname:127.0.0.1}")
    private String hostname;
    @Value("${elasticsearch.port:9200}")
    private int port;
    @Value("${elasticsearch.username}")
    private String username;
    @Value("${elasticsearch.password}")
    private String password;
    @Value("${elasticsearch.connection-timeout:3000}")
    private int connectionTimeout;
    @Value("${elasticsearch.socket-timeout:30000}")
    private int socketTimeout;
    @Value("${elasticsearch.connection-request-timeout:500}")
    private int connectionRequestTimeout;

    @Bean
    public CredentialsProvider credentialsProvider() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        return credentialsProvider;
    }

    @Bean
    public RestHighLevelClient restHighLevelClient(CredentialsProvider credentialsProvider) {
        RestClientBuilder restClientBuilder =
                RestClient.builder(new HttpHost(hostname, port)).setHttpClientConfigCallback(httpAsyncClientBuilder -> {
                    httpAsyncClientBuilder.disableAuthCaching();
                    return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                });

        restClientBuilder.setRequestConfigCallback(requestConfigCallback ->
                requestConfigCallback.setConnectTimeout(connectionTimeout)
                                     .setConnectionRequestTimeout(connectionRequestTimeout)
                                     .setSocketTimeout(socketTimeout)
        );

        return new RestHighLevelClient(restClientBuilder);
    }

}
