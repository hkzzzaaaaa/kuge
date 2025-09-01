package com.example.kuge.exception;

import com.example.kuge.pojo.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;

@RestControllerAdvice
public class ExceptionHand  {
     @ExceptionHandler(Exception.class)
     public Result HandlerException(Exception e){
          e.printStackTrace();
          return Result.error(StringUtils.hasLength(e.getMessage())?e.getMessage():"操作失败");
     }
}
