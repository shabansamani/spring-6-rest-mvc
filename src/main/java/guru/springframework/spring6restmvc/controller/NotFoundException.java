package guru.springframework.spring6restmvc.controller;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = org.springframework.http.HttpStatus.NOT_FOUND, reason = "Value not found")
public class NotFoundException extends RuntimeException {

  public NotFoundException() {}

  public NotFoundException(String msg) {
    super(msg);
  }

  public NotFoundException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public NotFoundException(Throwable cause) {
    super(cause);
  }

  public NotFoundException(String msg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(msg, cause, enableSuppression, writableStackTrace);
  }
}
