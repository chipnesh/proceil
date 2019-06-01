package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.MaterialAvailabilityModel
import me.chipnesh.proceil.repository.MaterialAvailabilityRepository
import me.chipnesh.proceil.service.dto.MaterialAvailabilityValueObject
import me.chipnesh.proceil.service.mapper.MaterialAvailabilityMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [MaterialAvailabilityModel].
 */
@Service
@Transactional
class MaterialAvailabilityService(
    val materialAvailabilityRepository: MaterialAvailabilityRepository,
    val materialAvailabilityMapper: MaterialAvailabilityMapper
) {

    private val log = LoggerFactory.getLogger(MaterialAvailabilityService::class.java)

    /**
     * Save a materialAvailability.
     *
     * @param materialAvailabilityValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(materialAvailabilityValueObject: MaterialAvailabilityValueObject): MaterialAvailabilityValueObject {
        log.debug("Request to save MaterialAvailability : {}", materialAvailabilityValueObject)

        var materialAvailabilityModel = materialAvailabilityMapper.toEntity(materialAvailabilityValueObject)
        materialAvailabilityModel = materialAvailabilityRepository.save(materialAvailabilityModel)
        return materialAvailabilityMapper.toDto(materialAvailabilityModel)
    }

    /**
     * Get all the materialAvailabilities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<MaterialAvailabilityValueObject> {
        log.debug("Request to get all MaterialAvailabilities")
        return materialAvailabilityRepository.findAll(pageable)
            .map(materialAvailabilityMapper::toDto)
    }

    /**
     * Get one materialAvailability by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<MaterialAvailabilityValueObject> {
        log.debug("Request to get MaterialAvailability : {}", id)
        return materialAvailabilityRepository.findById(id)
            .map(materialAvailabilityMapper::toDto)
    }

    /**
     * Delete the materialAvailability by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete MaterialAvailability : {}", id)

        materialAvailabilityRepository.deleteById(id)
    }
}
