package com.elara.userservice.dto.response;

import com.elara.userservice.enums.ResponseCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse {

  protected String responseCode;
  protected String responseMessage;

  public BaseResponse() {
    this.responseCode = ResponseCode.SUCCESSFUL.getValue();
    this.responseMessage = "Successful";
  }
}