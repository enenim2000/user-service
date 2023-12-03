package com.elara.accountservice.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyncPermissionRequest {

    private String appName;
    private List<Data> permissions;

    @Getter
    @Setter
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        //The value of PreAuthorize CREATE_USER
        private String permission;

        private String description;

        private String httpMethod;

        private String uriPath;

        private boolean isSecured;
    }

}
