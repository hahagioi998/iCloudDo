package org.jnyou.starter.autoconfigure.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 代码千万行，注释第一行，
 * 注释不规范，同事泪两行。
 * <p>
 * 自动配置
 *
 * @author Jnyou
 * @version 1.0.0
 */
//是个配置类
@Configuration
// 指明配置类，这样AnimalProperties才能被注入
@EnableConfigurationProperties(AnimalProperties.class)
// 在web应用中起效
// @ConditionalOnWebApplication
@ConditionalOnClass(AnimalProperties.class)
// @ConditionalOnProperty(prefix = "animal", value = "enable", matchIfMissing = true)
public class AnimalAutoConfiguration {

    private final AnimalProperties animalProperties;

    public AnimalAutoConfiguration(AnimalProperties animalProperties) {
        this.animalProperties = animalProperties;
    }

    /**
     * 当容器中没有这个Bean时才注入
     */
    @Bean
    @ConditionalOnMissingBean(AnimalService.class)
    public AnimalService animalService() {
        return new AnimalService(animalProperties);
    }

    /**
     * 只有满足当FishEnvironmentCondition.matches 返回true时才实例化FishService
     */
    @Bean
    @Conditional(FishEnvironmentCondition.class)
    @ConditionalOnMissingBean(FishService.class)
    public FishService fishService() {
        return new FishService(animalProperties);
    }

    /**
     * 只有满足当BirdEnvironmentCondition.matches 返回true时才实例化BirdService
     */
    @Bean
    @Conditional(BirdEnvironmentCondition.class)
    @ConditionalOnMissingBean(BirdService.class)
    public BirdService birdService() {
        return new BirdService(animalProperties);
    }

}
