package com.example.kuge.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Favourite {
    private Integer id;
    private Integer user_id;
    private Integer music_id;
    private Integer state;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}
