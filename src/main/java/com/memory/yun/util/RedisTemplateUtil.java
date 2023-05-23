package com.memory.yun.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @Author NJUPT wly
 * @Date 2022/1/29 12:51 下午
 * @Version 1.0
 */
@Slf4j
@Component
public class RedisTemplateUtil {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 增加有序集合
     *
     * @param key
     * @param value
     * @param seqNo
     * @return
     */
    public Boolean addZset(String key, Object value, double seqNo) {
        try {
            return redisTemplate.opsForZSet().add(key, value, seqNo);
        } catch (Exception e) {
            log.error("[RedisUtils.addZset] [error]", e);
            return false;
        }
    }

    /**
     * 获取zset集合数量
     *
     * @param key
     * @return
     */
    public Long countZset(String key) {
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception e) {
            log.error("[RedisUtils.countZset] [error] [key is {}]", key, e);
            return 0L;
        }
    }

    /**
     * 获取zset指定范围内的集合
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> rangeZset(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.error("[RedisUtils.rangeZset] [error] [key is {},start is {},end is {}]", key, start, end, e);
            return null;
        }
    }

    /**
     * 获取zset指定范围内的集合
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> rangeReverseZset(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            log.error("[RedisUtils.rangeZset] [error] [key is {},start is {},end is {}]", key, start, end, e);
            return null;
        }
    }

    /**
     * 根据key和value移除指定元素
     *
     * @param key
     * @param value
     * @return
     */
    public Long removeZset(String key, Object value) {
        return redisTemplate.opsForZSet().remove(key, value);
    }

    /**
     * 获取对应key和value的score
     *
     * @param key
     * @param value
     * @return
     */
    public Double score(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 指定范围内元素排序
     *
     * @param key
     * @param v1
     * @param v2
     * @return
     */
    public Set<Object> rangeByScore(String key, double v1, double v2) {
        return redisTemplate.opsForZSet().rangeByScore(key, v1, v2);
    }

    /**
     * 指定元素增加指定值
     *
     * @param key
     * @param obj
     * @param score
     * @return
     */
    public Object addScore(String key, Object obj, double score) {
        return redisTemplate.opsForZSet().incrementScore(key, obj, score);
    }

    /**
     * 排名
     *
     * @param key
     * @param obj
     * @return
     */
    public Object rank(String key, Object obj) {
        return redisTemplate.opsForZSet().rank(key, obj);
    }

}
