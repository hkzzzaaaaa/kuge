package com.example.kuge.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicBase {
    private int music_id;
    private String music_name;
    private String music_url;
    private String user_name;
    private String album_name;
    private Integer favorite_count;
    private Integer vip;
}
