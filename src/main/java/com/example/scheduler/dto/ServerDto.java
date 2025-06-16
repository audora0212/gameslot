// dto/ServerDto.java
package com.example.scheduler.dto;

import lombok.*;
import java.util.Set;

public class ServerDto {
    @Data public static class CreateRequest { private String name; }
    @Data public static class Response {
        private Long id; private String name; private String owner; private Set<String> members;
    }
}