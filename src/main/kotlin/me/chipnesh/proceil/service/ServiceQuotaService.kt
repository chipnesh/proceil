package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.ServiceQuotaModel
import me.chipnesh.proceil.repository.ServiceQuotaRepository
import me.chipnesh.proceil.service.dto.ServiceQuotaValueObject
import me.chipnesh.proceil.service.mapper.ServiceQuotaMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [ServiceQuotaModel].
 */
@Service
@Transactional
class ServiceQuotaService(
    val serviceQuotaRepository: ServiceQuotaRepository,
    val serviceQuotaMapper: ServiceQuotaMapper
) {

    private val log = LoggerFactory.getLogger(ServiceQuotaService::class.java)

    /**
     * Save a serviceQuota.
     *
     * @param serviceQuotaValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(serviceQuotaValueObject: ServiceQuotaValueObject): ServiceQuotaValueObject {
        log.debug("Request to save ServiceQuota : {}", serviceQuotaValueObject)

        var serviceQuotaModel = serviceQuotaMapper.toEntity(serviceQuotaValueObject)
        serviceQuotaModel = serviceQuotaRepository.save(serviceQuotaModel)
        return serviceQuotaMapper.toDto(serviceQuotaModel)
    }

    /**
     * Get all the serviceQuotas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<ServiceQuotaValueObject> {
        log.debug("Request to get all ServiceQuotas")
        return serviceQuotaRepository.findAll(pageable)
            .map(serviceQuotaMapper::toDto)
    }

    /**
     * Get one serviceQuota by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<ServiceQuotaValueObject> {
        log.debug("Request to get ServiceQuota : {}", id)
        return serviceQuotaRepository.findById(id)
            .map(serviceQuotaMapper::toDto)
    }

    /**
     * Delete the serviceQuota by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete ServiceQuota : {}", id)

        serviceQuotaRepository.deleteById(id)
    }
}
