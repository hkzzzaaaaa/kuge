package com.example.kuge.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "orders")
public class Orders {
    @TableId
    private Integer id;
    private String outTradeNo;
    private Integer user_id;
    private Integer payaccount;
    private Integer status;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}
