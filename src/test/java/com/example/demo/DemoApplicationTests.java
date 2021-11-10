package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

@Slf4j
@SpringBootTest
class DemoApplicationTests {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Test
    void contextLoads() throws IOException {
        assertNotNull(restHighLevelClient);

        SearchRequest request = new SearchRequest("log4j2-kafka-demo-*");
        SearchSourceBuilder builder = request.source();
        builder.from(0);
        builder.size(100);
        builder.sort("id", SortOrder.ASC);

        QueryBuilder queryBuilder = QueryBuilders.simpleQueryStringQuery("msg_msg:\"\"");
        builder.query(queryBuilder);

        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
//            hit.getSourceAsMap();
            log.info(hit.getSourceAsString());
        }
    }

}
