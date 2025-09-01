package com.example.kuge.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.kuge.config.RabbitMqConfig;
import com.example.kuge.config.RedisConfig;
import com.example.kuge.mapper.FavouriteMapper;
import com.example.kuge.mapper.MusicServiceMapper;
import com.example.kuge.mapper.PayMapper;
import com.example.kuge.pojo.Favourite;
import com.example.kuge.pojo.Music;
import com.example.kuge.pojo.Orders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RabbitMqLister {
    private static final long[] DELAY_TIMES ={7*60*1000,5*60*1000,2*60*1000,30*1000};
    @Autowired
    private PayMapper payMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    FavouriteMapper favouriteMapper;
    @Autowired
    private  MusicServiceMapper musicServiceMapper;
    @Autowired
    private RedissonClient redissonClient;
    @RabbitListener(queues = RabbitMqConfig.DELAY_QUEUE_NAME)
    public void handleDelayMessage(String outTradeNo,@Header(value = "checkTimes", required = false) Integer checkTimes){
        System.out.println("RabbitMq监听中");
        QueryWrapper<Orders> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("outTradeNo",outTradeNo);
        Orders order = payMapper.selectOne(queryWrapper);
        if (order.getStatus()==1){
            LocalDateTime time=payMapper.selectvip(order.getUser_id());
            if (time.isAfter(LocalDateTime.now())){
                //在time的基础上增加x个月的时间
                time=time.plusMonths(order.getPayaccount()/30);
            }
            else {
               //在当前时间增加x个月时间
                time=LocalDateTime.now();
                time=time.plusMonths(order.getPayaccount()/30);
            }
            payMapper.updatevip(time,order.getUser_id());
        }
        else{
          //TODO status为未支付，再次发送延迟消息
            if (checkTimes>0){
                System.out.println("延迟消息重新发送");
                checkTimes--;
                Integer finalCheckTimes = checkTimes;
                rabbitTemplate.convertAndSend(
                        RabbitMqConfig.DELAY_EXCHANGE_NAME,
                        RabbitMqConfig.DELAY_ROUTING_KEY,
                        outTradeNo,
                        message -> {
                            message.getMessageProperties().setHeader("x-delay",DELAY_TIMES[finalCheckTimes-1]);
                            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            message.getMessageProperties().setHeader("checkTimes", finalCheckTimes);
                            return message;
                        }
                );
            }
            else{
                //TODO 修改订单状态为取消
                UpdateWrapper<Orders> updateWrapper=new UpdateWrapper<>();
                updateWrapper.eq("outTradeNo",outTradeNo);
                updateWrapper.set("status",2);
                updateWrapper.set("update_time",LocalDateTime.now());
                payMapper.update(updateWrapper);
            }
        }
    }
    @RabbitListener(queues = RabbitMqConfig.FAVOURITE_QUEUE)
    public void listenFavouriteQueue(String message) {
        System.out.println("成功接收消息");
        try {
            Favourite favourite = objectMapper.readValue(message, Favourite.class);
            UpdateWrapper<Favourite> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("state",favourite.getState());
            updateWrapper.eq("user_id",favourite.getUser_id());
            updateWrapper.eq("music_id",favourite.getMusic_id());
            favouriteMapper.update(updateWrapper);
            //TODO 获取分布式锁修改数据库收藏量
            String lockKey = "lock:music:favourite:" + favourite.getMusic_id();
            RLock lock = redissonClient.getLock(lockKey);
            try {
                boolean isLocked = lock.tryLock(5, 10, java.util.concurrent.TimeUnit.SECONDS);
                if (!isLocked) {
                    throw new RuntimeException("获取分布式锁失败，无法更新音乐收藏量，音乐ID：" + favourite.getMusic_id());
                }
                Music music = musicServiceMapper.selectById(favourite.getMusic_id());
                UpdateWrapper<Music> updateWrapper2 = new UpdateWrapper<>();
                int count;
                if (favourite.getState()==1){
                    count=1;
                }
                else{
                    count=-1;
                }
                updateWrapper2.set("favorite_count", music.getFavorite_count() + count);
                updateWrapper2.eq("music_id", favourite.getMusic_id());
                musicServiceMapper.update(updateWrapper2);
            }
            catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
            finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }catch (Exception e) {
         System.out.println(e.getMessage());
        }
    }

}
