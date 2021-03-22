package com.packsendme.api.google.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.packsendme.api.google.config.Redis_Config;
import com.packsendme.roadbrewa.entity.TollsFuel;

@Repository
@Transactional
public class TollsFuelCacheImpl_Dao implements ICrudCache<TollsFuel>{
	
	@Autowired(required=true)
	private RedisTemplate<Object, Object> redisTemplate;

	@Autowired
	private Redis_Config cacheConfig;
	

	@Override
	public TollsFuel findOne(String hashKey) {
		try {
			return (TollsFuel) redisTemplate.opsForHash().get(cacheConfig.CACHE_FUELTOLLS, hashKey);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

 
}
