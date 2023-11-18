package com.dhx.bi.model.DTO.spark;

import lombok.Data;

import java.util.List;

/**
 * @author adorabled4
 * @className ChatRequest
 * @date : 2023/11/17/ 17:45
 **/
@Data
public class ChatResponse {

    private Header header;

    private Payload payload;

    @Data
    public static class Header {
        private int code;
        private String message;
        private String sid;
        private int status;
    }

    @Data
    public static class Payload {
        private Choices choices;
        private Usage usage;


    }

    @Data
    public static class Choices {
        private int status;
        private int seq;
        private List<ContentRoleIndex> text;
    }

    @Data
    public static class ContentRoleIndex {
        private String content;
        private String role;
        private int index;
    }

    @Data
    public static class Usage {
        private Tokens text;

        @Data
        public static class Tokens {
            private int question_tokens;
            private int prompt_tokens;
            private int completion_tokens;
            private int total_tokens;
        }
    }

}
