package com.snap.fosdem.service

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import java.util.concurrent.TimeUnit


@Bean
fun caffeineConfig(): Caffeine<Any,Any> {
    return Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.DAYS)
}
@Bean
fun cacheManager(caffeine: Caffeine<Any, Any>): CacheManager {
    val caffeineCacheManager = CaffeineCacheManager()
    caffeineCacheManager.setCaffeine(caffeine)
    return caffeineCacheManager
}