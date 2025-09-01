package com.example.kuge.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Music {
    @TableId(type = IdType.AUTO)
    private Integer music_id;
    private String music_name;
    private Integer album_id;
    private Integer user_id;
    private Integer favorite_count;
    private Integer is_vip;
    private String music_url;
    private LocalDateTime  create_time;
    private LocalDateTime update_time;

}
