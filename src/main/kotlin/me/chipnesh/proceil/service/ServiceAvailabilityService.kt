package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.ServiceAvailabilityModel
import me.chipnesh.proceil.repository.ServiceAvailabilityRepository
import me.chipnesh.proceil.service.dto.ServiceAvailabilityValueObject
import me.chipnesh.proceil.service.mapper.ServiceAvailabilityMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [ServiceAvailabilityModel].
 */
@Service
@Transactional
class ServiceAvailabilityService(
    val serviceAvailabilityRepository: ServiceAvailabilityRepository,
    val serviceAvailabilityMapper: ServiceAvailabilityMapper
) {

    private val log = LoggerFactory.getLogger(ServiceAvailabilityService::class.java)

    /**
     * Save a serviceAvailability.
     *
     * @param serviceAvailabilityValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(serviceAvailabilityValueObject: ServiceAvailabilityValueObject): ServiceAvailabilityValueObject {
        log.debug("Request to save ServiceAvailability : {}", serviceAvailabilityValueObject)

        var serviceAvailabilityModel = serviceAvailabilityMapper.toEntity(serviceAvailabilityValueObject)
        serviceAvailabilityModel = serviceAvailabilityRepository.save(serviceAvailabilityModel)
        return serviceAvailabilityMapper.toDto(serviceAvailabilityModel)
    }

    /**
     * Get all the serviceAvailabilities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<ServiceAvailabilityValueObject> {
        log.debug("Request to get all ServiceAvailabilities")
        return serviceAvailabilityRepository.findAll(pageable)
            .map(serviceAvailabilityMapper::toDto)
    }

    /**
     * Get one serviceAvailability by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<ServiceAvailabilityValueObject> {
        log.debug("Request to get ServiceAvailability : {}", id)
        return serviceAvailabilityRepository.findById(id)
            .map(serviceAvailabilityMapper::toDto)
    }

    /**
     * Delete the serviceAvailability by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete ServiceAvailability : {}", id)

        serviceAvailabilityRepository.deleteById(id)
    }
}
