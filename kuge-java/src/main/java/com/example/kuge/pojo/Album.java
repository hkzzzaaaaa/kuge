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
@TableName(value = "album")
public class Album {
    @TableId
    private Integer album_id;
    private String album_name;
    private Integer user_id;
    private LocalDateTime create_time;
    private LocalDateTime update_time;

}
