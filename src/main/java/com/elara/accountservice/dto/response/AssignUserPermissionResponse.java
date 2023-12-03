package com.elara.accountservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignUserPermissionResponse extends BaseResponse {
    public AssignUserPermissionResponse() {
        super();
    }
}
