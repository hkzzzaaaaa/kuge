package com.example.kuge;

import com.alibaba.fastjson.JSON;
import com.example.kuge.mapper.LoginMapper;
import com.example.kuge.mapper.MusicServiceMapper;
import com.example.kuge.pojo.MusicBase;
import com.example.kuge.pojo.User;
import com.example.kuge.util.ESClientUtil;
import com.example.kuge.util.PassWordUtil;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class KugeApplicationTests {
    @Autowired
    ESClientUtil esClientUtil;
    @Autowired
    private MusicServiceMapper musicServiceMapper;
    @Test
    public void CreateEs() throws IOException {
        RestHighLevelClient client = ESClientUtil.getClient();
            CreateIndexRequest request = new CreateIndexRequest("music");
            request.settings(Settings.builder()
                    .put("number_of_shards", 3)
                    .put("number_of_replicas", 1)
                    .put("analysis.analyzer.custom_analyzer.type", "custom")
                    .put("analysis.analyzer.custom_analyzer.tokenizer", "ik_max_word")
                    .putList("analysis.analyzer.custom_analyzer.filter", "lowercase", "asciifolding")
                    .put("analysis.analyzer.ik_smart_analyzer.type", "custom")
                    .put("analysis.analyzer.ik_smart_analyzer.tokenizer", "ik_smart")
                    .putList("analysis.analyzer.ik_smart_analyzer.filter", "lowercase", "asciifolding")
            );
            XContentBuilder mapping = JsonXContent.contentBuilder()
                    .startObject()
                    .startObject("properties")
                    // music_id字段
                    .startObject("music_id")
                    .field("type", "integer")
                    .endObject()
                    // music_name字段
                    .startObject("music_name")
                    .field("type", "text")
                    .field("analyzer", "ik_smart_analyzer")
                    .startObject("fields")
                    .startObject("keyword")
                    .field("type", "keyword")
                    .endObject()
                    .endObject()
                    .endObject()
                    // music_url字段
                    .startObject("music_url")
                    .field("type", "keyword")
                    .endObject()
                    // user_name字段
                    .startObject("user_name")
                    .field("type", "text")
                    .field("analyzer", "ik_smart_analyzer")
                    .startObject("fields")
                    .startObject("keyword")
                    .field("type", "keyword")
                    .endObject()
                    .endObject()
                    .endObject()
                    // album_name字段
                    .startObject("album_name")
                    .field("type", "text")
                    .field("analyzer", "ik_smart_analyzer")
                    .startObject("fields")
                    .startObject("keyword")
                    .field("type", "keyword")
                    .endObject()
                    .endObject()
                    .endObject()
                    // favorite_count字段
                    .startObject("favorite_count")
                    .field("type", "integer")
                    .endObject()
                    // vip字段
                    .startObject("vip")
                    .field("type", "integer")
                    .endObject()
                    .endObject()
                    .endObject();

            request.mapping(mapping.toString());
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            if (response.isAcknowledged()) {
                System.out.println("索引创建成功");
            } else {
                System.out.println("索引创建失败");
            }
        }
    @Test
    public void SyncMusicToEs() throws IOException {
        RestHighLevelClient client = ESClientUtil.getClient();
        List<MusicBase> list=musicServiceMapper.selectMusicBase();
        BulkRequest bulkRequest = new BulkRequest();
        for (MusicBase music : list) {
            IndexRequest request = new IndexRequest("music")
                    .id(String.valueOf(music.getMusic_id()))
                    .source(JSON.toJSONString(music), XContentType.JSON);
            bulkRequest.add(request);
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        if (bulkResponse.hasFailures()) {
            System.out.println("批量同步失败: " + bulkResponse.buildFailureMessage());
        } else {
            System.out.println("批量同步成功，共处理 " + list.size() + " 条数据");
        }
        client.close();
    }
}
