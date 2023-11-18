package com.dhx.bi.model.DTO.spark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author adorabled4
 * @className ChatRequest
 * @date : 2023/11/17/ 17:45
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRequest {
    private Header header;
    private Parameter parameter;
    private Payload payload;

    @Data
    @Builder
    public static class Header {
        private String app_id;
        private String uid;
    }

    @Data
    @Builder
    public static class Parameter {
        private Chat chat;
    }

    @Data
    @Builder
    public static class Chat {
        private String domain;
        private double temperature;
        private int max_tokens;

    }

    @Data
    @Builder
    public static class Payload {
        private Message message;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private List<Text> text;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Text {
        String role;
        String content;
    }
}

