package com.elara.accountservice.dto.request;

import com.elara.accountservice.validator.Required;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateGroupRequest {
    @Required(message = "companyCode.required")
    private String companyCode;

    @Required(message = "Group.GroupName.Required")
    private String groupName;

    @Required(message = "Group.Description.Required")
    private String description;

}
