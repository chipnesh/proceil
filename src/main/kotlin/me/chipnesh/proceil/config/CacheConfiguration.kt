package me.chipnesh.proceil.config

import java.time.Duration

import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.jsr107.Eh107Configuration

import io.github.jhipster.config.JHipsterProperties

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class CacheConfiguration(jHipsterProperties: JHipsterProperties) {

    private val jcacheConfiguration: javax.cache.configuration.Configuration<Any, Any>

    init {
        val ehcache = jHipsterProperties.cache.ehcache

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Any::class.java, Any::class.java,
                ResourcePoolsBuilder.heap(ehcache.maxEntries))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.timeToLiveSeconds.toLong())))
                .build())
    }

    @Bean
    fun cacheManagerCustomizer(): JCacheManagerCustomizer {
        return JCacheManagerCustomizer { cm ->
            createCache(cm, me.chipnesh.proceil.repository.UserRepository.USERS_BY_LOGIN_CACHE)
            createCache(cm, me.chipnesh.proceil.repository.UserRepository.USERS_BY_EMAIL_CACHE)
            createCache(cm, me.chipnesh.proceil.domain.UserModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.Authority::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.UserModel::class.java.name + ".authorities")
            createCache(cm, me.chipnesh.proceil.domain.PersistentToken::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.UserModel::class.java.name + ".persistentTokens")
            createCache(cm, me.chipnesh.proceil.domain.AttachedImageModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.MaterialModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.MaterialModel::class.java.name + ".images")
            createCache(cm, me.chipnesh.proceil.domain.ServiceModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.ServiceModel::class.java.name + ".images")
            createCache(cm, me.chipnesh.proceil.domain.MaterialRequestModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.MaterialArrivalModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.FacilityModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.FacilityModel::class.java.name + ".zones")
            createCache(cm, me.chipnesh.proceil.domain.ZoneModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.ZoneModel::class.java.name + ".materials")
            createCache(cm, me.chipnesh.proceil.domain.ZoneModel::class.java.name + ".services")
            createCache(cm, me.chipnesh.proceil.domain.MaterialReserveModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.ServiceQuotaModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.MaterialAvailabilityModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.ServiceAvailabilityModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.CustomerModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.CustomerModel::class.java.name + ".feedbacks")
            createCache(cm, me.chipnesh.proceil.domain.CustomerModel::class.java.name + ".measurements")
            createCache(cm, me.chipnesh.proceil.domain.CustomerModel::class.java.name + ".orders")
            createCache(cm, me.chipnesh.proceil.domain.FeedbackModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.EmployeeModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.EmployeeModel::class.java.name + ".measurements")
            createCache(cm, me.chipnesh.proceil.domain.MaterialMeasurementModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.MeasurementModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.MeasurementModel::class.java.name + ".materials")
            createCache(cm, me.chipnesh.proceil.domain.CustomerOrderModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.CustomerOrderModel::class.java.name + ".materials")
            createCache(cm, me.chipnesh.proceil.domain.CustomerOrderModel::class.java.name + ".services")
            createCache(cm, me.chipnesh.proceil.domain.OrderMaterialModel::class.java.name)
            createCache(cm, me.chipnesh.proceil.domain.OrderServiceModel::class.java.name)
            // jhipster-needle-ehcache-add-entry
        }
    }

    private fun createCache(cm: javax.cache.CacheManager, cacheName: String) {
        val cache: javax.cache.Cache<Any, Any>? = cm.getCache(cacheName)
        if (cache != null) {
            cm.destroyCache(cacheName)
        }
        cm.createCache(cacheName, jcacheConfiguration)
    }
}
