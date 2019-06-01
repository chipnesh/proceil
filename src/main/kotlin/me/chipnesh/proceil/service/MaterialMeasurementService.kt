package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.MaterialMeasurementModel
import me.chipnesh.proceil.repository.MaterialMeasurementRepository
import me.chipnesh.proceil.service.dto.MaterialMeasurementValueObject
import me.chipnesh.proceil.service.mapper.MaterialMeasurementMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [MaterialMeasurementModel].
 */
@Service
@Transactional
class MaterialMeasurementService(
    val materialMeasurementRepository: MaterialMeasurementRepository,
    val materialMeasurementMapper: MaterialMeasurementMapper
) {

    private val log = LoggerFactory.getLogger(MaterialMeasurementService::class.java)

    /**
     * Save a materialMeasurement.
     *
     * @param materialMeasurementValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(materialMeasurementValueObject: MaterialMeasurementValueObject): MaterialMeasurementValueObject {
        log.debug("Request to save MaterialMeasurement : {}", materialMeasurementValueObject)

        var materialMeasurementModel = materialMeasurementMapper.toEntity(materialMeasurementValueObject)
        materialMeasurementModel = materialMeasurementRepository.save(materialMeasurementModel)
        return materialMeasurementMapper.toDto(materialMeasurementModel)
    }

    /**
     * Get all the materialMeasurements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<MaterialMeasurementValueObject> {
        log.debug("Request to get all MaterialMeasurements")
        return materialMeasurementRepository.findAll(pageable)
            .map(materialMeasurementMapper::toDto)
    }

    /**
     * Get one materialMeasurement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<MaterialMeasurementValueObject> {
        log.debug("Request to get MaterialMeasurement : {}", id)
        return materialMeasurementRepository.findById(id)
            .map(materialMeasurementMapper::toDto)
    }

    /**
     * Delete the materialMeasurement by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete MaterialMeasurement : {}", id)

        materialMeasurementRepository.deleteById(id)
    }
}
