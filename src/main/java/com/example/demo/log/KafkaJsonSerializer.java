package com.example.demo.log;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.util.IdGenUtils;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Serializer;

import java.net.InetAddress;
import java.util.Map;

/**
 * TODO
 * @author nature
 * @version 1.0 2020/11/9
 */
public class KafkaJsonSerializer implements Serializer<byte[]> {

    private String hostIp;

    @SneakyThrows
    @Override
    public void configure(Map<String, ?> map, boolean b) {
        hostIp = InetAddress.getLocalHost().getHostAddress();
    }

    @Override
    public byte[] serialize(String topic, byte[] data) {

        JSONObject message = new JSONObject();
        JSONObject metadata = new JSONObject();
        JSONObject fields = new JSONObject();
        message.put("@metadata", metadata);
        message.put("fields", fields);

        metadata.put("topic", topic);
        fields.put("host_ip", hostIp);
        message.put("id", IdGenUtils.nextId());
        message.put("msg_msg", new String(data));

        String result = message.toJSONString();
        return result.getBytes();
    }

    @Override
    public void close() {

    }

}
