package com.elara.userservice.dto.response;

import com.elara.userservice.enums.ResponseCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse {

  private String responseCode;
  private String responseMessage;

  public BaseResponse() {
    this.responseCode = ResponseCode.SUCCESSFUL.getValue();
    this.responseMessage = "Successful";
  }
}