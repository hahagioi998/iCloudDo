package org.jnyou.note.redis.lock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 代码千万行，注释第一行，
 * 注释不规范，同事泪两行。
 * <p>
 * Redisson 配置文件
 * 具体参考官方：
 * http://www.redis.cn/topics/distlock.html
 *
 * @author JnYou
 * @version 1.0.0
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.database}")
    private int database;

    /**
     * 这里只配置单节点的，支持集群、哨兵等发誓配置
     * 可以用Config.fromYAML加载yml文件中的配置
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(String.format("redis://%s:%s", host, port))
                .setDatabase(database);
        return Redisson.create(config);
    }
}
