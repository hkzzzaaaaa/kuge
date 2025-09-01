package com.example.kuge.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicPublicView {
    private String music_name;
    private String user_name;
    private String album_name;
    private Integer vip;
}
