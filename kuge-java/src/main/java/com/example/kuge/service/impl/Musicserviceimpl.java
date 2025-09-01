package com.example.kuge.service.impl;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.kuge.config.RabbitMqConfig;
import com.example.kuge.mapper.AlbumServiceMapper;
import com.example.kuge.mapper.MusicServiceMapper;
import com.example.kuge.pojo.*;
import com.example.kuge.service.Musicservice;
import com.example.kuge.util.ESClientUtil;
import com.example.kuge.util.JwtUtil;
import com.example.kuge.util.OssUploadUtil;
import com.example.kuge.util.SetValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class Musicserviceimpl implements Musicservice {
    @Autowired
    AlbumServiceMapper albumServiceMapper;
    @Autowired
    MusicServiceMapper musicServiceMapper;
    @Autowired
    RedisTemplate<String, String> stringRedisTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    OssUploadUtil ossUploadUtil;
    @Autowired
    ESClientUtil esClientUtil;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Transactional
    @Override
    public void addmusic(String token, String music_name, String album_name, Integer vip, MultipartFile file) throws JsonProcessingException {
        String music_url;
        try {
            music_url = ossUploadUtil.uploadFile(file);
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
        Music music;
        Integer id=JwtUtil.getid(token);
        String user_id = "kuge:user:"+ id + ":" + album_name;
        String json = stringRedisTemplate.opsForValue().get(user_id);
        Album album=new Album();
        if(json!=null){
            album = objectMapper.readValue(json, Album.class);
        }
        try {
            if (album.getAlbum_id() == null) {
                album = albumServiceMapper.selectAlbum(album_name, id);
                if (album == null) {
                    album = SetValue.SetAlbum(album_name, id);
                    albumServiceMapper.insert(album);
                    album = albumServiceMapper.selectAlbum(album_name, id);
                    System.out.println(album.getAlbum_name());
                    music = SetValue.SetMusic(music_name, album.getAlbum_id(), id, music_url, vip);
                    musicServiceMapper.insert(music);
                } else {
                    music = SetValue.SetMusic(music_name, album.getAlbum_id(), id, music_url, vip);
                    musicServiceMapper.insert(music);
                }
                String newAlbumJson = objectMapper.writeValueAsString(album);
                stringRedisTemplate.opsForValue().set(user_id, newAlbumJson, 30, TimeUnit.MINUTES);
            } else {
                music = SetValue.SetMusic(music_name, album.getAlbum_id(), id, music_url, vip);
                musicServiceMapper.insert(music);
            }
            MusicBase musicBase=musicServiceMapper.selectMusicBaseById(music.getMusic_id());
            RestHighLevelClient client = ESClientUtil.getClient();
            IndexRequest request = new IndexRequest("music");
            request.id(String.valueOf(musicBase.getMusic_id()));
            String jsonData = JSON.toJSONString(musicBase);
            request.source(jsonData, XContentType.JSON);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        }
        catch (Exception e){
            ossUploadUtil.delete(music_url);
            throw new RuntimeException("数据库操作失败:" + e.getMessage());
        }
        finally {
            try {
                ESClientUtil.closeClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public PageBean<MusicPublicView> findmusicpublic(String token, Integer pageNum, Integer pagesize) {
        PageBean<MusicPublicView> pageBean =new PageBean<>();
        PageHelper.startPage(pageNum,pagesize);
        Map<String, Object> map = JwtUtil.parseToken(token);
        Integer id = (Integer) map.get("id");
        List<MusicPublicView> list=musicServiceMapper.findmusicpublic(id);
        if(list==null){
            throw new RuntimeException("未查询到歌曲");
        }
        Page<MusicPublicView> p=(Page<MusicPublicView>) list;
        pageBean.setTotal(p.getTotal());
        pageBean.setItems(p.getResult());
        return pageBean;
    }

    @Override
    public PageBean<MusicPublicView> findMusicByName(String token, String musicName, Integer pageNum, Integer pagesize) throws JsonProcessingException {
        PageBean<MusicPublicView> pageBean =new PageBean<>();
        PageHelper.startPage(pageNum,pagesize);
        Map<String, Object> map = JwtUtil.parseToken(token);
        Integer id = (Integer) map.get("id");
        String user_id="kuge:user:"+id+":music:"+musicName;
        String json=stringRedisTemplate.opsForValue().get(user_id);
        if (json==null){
            List<MusicPublicView> list=musicServiceMapper.findMusicByName(id,musicName);
            if(list.isEmpty()){
                stringRedisTemplate.opsForValue().set(user_id,"[]",6, TimeUnit.HOURS);
                return pageBean;
            }
            else{
                String json1=objectMapper.writeValueAsString(list);
                stringRedisTemplate.opsForValue().set(user_id,json1,6,TimeUnit.HOURS);
                Page<MusicPublicView> p=(Page<MusicPublicView>) list;
                pageBean.setTotal(p.getTotal());
                pageBean.setItems(p.getResult());
                return pageBean;
            }
        }
        else{
            List<MusicPublicView> list=objectMapper.readValue(json,new TypeReference<List<MusicPublicView>>() {});
            long total = list.size();
            int start = (pageNum - 1) * pagesize;
            int end = Math.min(start + pagesize, list.size());
            pageBean.setTotal(total);
            pageBean.setItems(list.subList(start, end));
            return pageBean;
        }
    }

    @Override
    public PageBean<MusicBase> findMusicBaseService(Integer pageNum, Integer pagesize) throws JsonProcessingException {
        String lockKey = "kuge:musicbase:lock";
        PageBean<MusicBase> pageBean =new PageBean<>();
        String str="kuge:musicbase";
        Set<ZSetOperations.TypedTuple<String>> musicTuples =stringRedisTemplate.opsForZSet().reverseRangeWithScores(str,0,-1);
        if(musicTuples!=null&& !musicTuples.isEmpty()){
            List<MusicBase> list=new ArrayList<>();
            for(ZSetOperations.TypedTuple<String> tuple :musicTuples){
                String json = tuple.getValue();
                MusicBase music=objectMapper.readValue(json,MusicBase.class);
                list.add(music);
            }
            long  total= list.size();
            int start = (pageNum - 1) * pagesize;
            int end = Math.min(start + pagesize, list.size());
            pageBean.setTotal(total);
            pageBean.setItems(list.subList(start, end));
            return pageBean;
        }
        else{
            RLock lock = redissonClient.getLock(lockKey);
            try {
                boolean isLocked = lock.tryLock(100, 10000, TimeUnit.MILLISECONDS);
                if (!isLocked){
                    Thread.sleep(50);
                    return findMusicBaseService(pageNum, pagesize);
                }
                List<MusicBase> list=musicServiceMapper.findMusicBase();
                long  total= list.size();
                int start = (pageNum - 1) * pagesize;
                int end = Math.min(start + pagesize, list.size());
                pageBean.setTotal(total);
                pageBean.setItems(list.subList(start, end));
                for(MusicBase music:list){
                    String json = objectMapper.writeValueAsString(music);
                    double score=music.getFavorite_count();
                    stringRedisTemplate.opsForZSet().add(str, json, score);
                }
                stringRedisTemplate.expire(str,30,TimeUnit.MINUTES);
                return pageBean;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    @Override
    public PageBean<MusicBase> findMusic(Integer pageNum, Integer pagesize,String music_name,String token) throws IOException {
        PageBean<MusicBase> pageBean =new PageBean<>();
        Map<String, Object> map = JwtUtil.parseToken(token);
        Integer id = (Integer) map.get("id");
        String str="kuge:"+id+":music:"+music_name;
        Set<ZSetOperations.TypedTuple<String>> musicTuples =stringRedisTemplate.opsForZSet().reverseRangeWithScores(str,0,-1);
        if(!musicTuples.isEmpty()){
            List<MusicBase> list=new ArrayList<>();
            for(ZSetOperations.TypedTuple<String> tuple :musicTuples){
                String json = tuple.getValue();
                MusicBase music=objectMapper.readValue(json,MusicBase.class);
                list.add(music);
            }
            long  total= list.size();
            int start = (pageNum - 1) * pagesize;
            int end = Math.min(start + pagesize, list.size());
            pageBean.setTotal(total);
            pageBean.setItems(list.subList(start, end));
            return pageBean;
        }
        else{
            RestHighLevelClient client=esClientUtil.getClient();
            SearchRequest searchRequest = new SearchRequest("music");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(music_name)
                    .field("music_name", 3.0f)
                    .field("album_name", 2.0f)
                    .field("user_name", 1.0f)
                    .fuzziness(Fuzziness.AUTO);
            sourceBuilder.query(queryBuilder);
            sourceBuilder.from((pageNum - 1) * pagesize);
            sourceBuilder.size(pagesize);
            searchRequest.source(sourceBuilder);
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            client.close();
            List<MusicBase> musicList = new ArrayList<>();
            long total = response.getHits().getTotalHits().value;
            for (SearchHit hit : response.getHits()) {
                MusicBase music = objectMapper.readValue(hit.getSourceAsString(), MusicBase.class);
                musicList.add(music);
            }
            pageBean.setTotal(total);
            pageBean.setItems(musicList);
            for(MusicBase music:musicList){
                String json = objectMapper.writeValueAsString(music);
                System.out.println(music.getFavorite_count());
                double score=music.getFavorite_count();
                stringRedisTemplate.opsForZSet().add(str, json, score);
            }
            stringRedisTemplate.expire(str,12,TimeUnit.HOURS);
            return pageBean;
        }
    }

    @Override
    public void putFavourite(String token,Integer music_id) throws JsonProcessingException {
        Integer id = JwtUtil.getid(token);
        String str="kuge:music:id:"+music_id;
        String json;
        //TODO 先去查询redis
        str=str+":user_id:"+id+"favourite";
        json=stringRedisTemplate.opsForValue().get(str);
        Favourite favourite;
        if (json != null && !json.isEmpty()) {
            //异步同步
            favourite = objectMapper.readValue(json,Favourite.class);
            if (favourite.getState()==1){
                favourite.setState(0);
            }
            else{
                favourite.setState(1);
            }
            json=objectMapper.writeValueAsString(favourite);
            stringRedisTemplate.opsForValue().set(str,json,30,TimeUnit.MINUTES);
        }
        else{
            favourite=musicServiceMapper.selectFavorite(id,music_id);
            if (favourite!=null){
                //异步同步数据
                if (favourite.getState()==1){
                    favourite.setState(0);
                }
                else{
                    favourite.setState(1);
                }
                json=objectMapper.writeValueAsString(favourite);
                stringRedisTemplate.opsForValue().set(str,json,30,TimeUnit.MINUTES);
            }
            else{
                favourite=new Favourite();
                favourite.setUser_id(id);
                favourite.setMusic_id(music_id);
                favourite.setState(1);
                favourite.setCreate_time(LocalDateTime.now());
                favourite.setUpdate_time(LocalDateTime.now());
                musicServiceMapper.insertfavourite(favourite);
                json=objectMapper.writeValueAsString(favourite);
                stringRedisTemplate.opsForValue().set(str,json,30,TimeUnit.MINUTES);
            }
        }
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.FAVOURITE_EXCHANGE,
                RabbitMqConfig.FAVOURITE_ROUTING_KEY,
                json
        );
        str="kuge:user:"+id+":favourite:";
        stringRedisTemplate.delete(str);
    }

    @Override
    public PageBean<MusicBase> getFavourite(String token, Integer pageNum, Integer pagesize) throws JsonProcessingException {
        //TODO 修改查询代码
        PageBean<MusicBase> pageBean =new PageBean<>();
        Map<String, Object> map = JwtUtil.parseToken(token);
        Integer id = (Integer) map.get("id");
        String str="kuge:user:"+id+":favourite:";
        String json=stringRedisTemplate.opsForValue().get(str);
        if(json==null){
            List<MusicBase> list = musicServiceMapper.getFavourite(id);
            if(!list.isEmpty()){
                String json1=objectMapper.writeValueAsString(list);
                stringRedisTemplate.opsForValue().set(str,json1,6,TimeUnit.HOURS);
            }
            long total = list.size();
            int start = (pageNum - 1) * pagesize;
            int end = Math.min(start + pagesize, list.size());
            pageBean.setTotal(total);
            pageBean.setItems(list.subList(start, end));
            return pageBean;
        }
        else{
            List<MusicBase> list =objectMapper.readValue(json,new TypeReference<List<MusicBase>>() {});
            long total = list.size();
            int start = (pageNum - 1) * pagesize;
            int end = Math.min(start + pagesize, list.size());
            pageBean.setTotal(total);
            pageBean.setItems(list.subList(start, end));
            return pageBean;
        }
    }

    @Override
    public void deleteFavourite(String token, String musicName, String userName) throws JsonProcessingException {
        Map<String, Object> map = JwtUtil.parseToken(token);
        Integer id = (Integer) map.get("id");
        Music music=musicServiceMapper.findMusicByMusicName(musicName,userName);
        musicServiceMapper.deleteFavourite(music.getMusic_id(),id);
        UpdateWrapper wrapper=new UpdateWrapper<>();
        wrapper.eq("music_id",music.getMusic_id());
        wrapper.set("favorite_count",music.getFavorite_count()-1);
        musicServiceMapper.update(wrapper);
        List<MusicBase> list = musicServiceMapper.getFavourite(id);
        String str="kuge:user:"+id+":favourite:";
        if(!list.isEmpty()){
            String json1=objectMapper.writeValueAsString(list);
            stringRedisTemplate.opsForValue().set(str,json1,6,TimeUnit.HOURS);
        }
        else{
            stringRedisTemplate.delete(str);
        }

    }

}
