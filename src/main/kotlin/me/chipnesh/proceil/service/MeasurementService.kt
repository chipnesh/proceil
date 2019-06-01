package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.MeasurementModel
import me.chipnesh.proceil.repository.MeasurementRepository
import me.chipnesh.proceil.service.dto.MeasurementValueObject
import me.chipnesh.proceil.service.mapper.MeasurementMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [MeasurementModel].
 */
@Service
@Transactional
class MeasurementService(
    val measurementRepository: MeasurementRepository,
    val measurementMapper: MeasurementMapper
) {

    private val log = LoggerFactory.getLogger(MeasurementService::class.java)

    /**
     * Save a measurement.
     *
     * @param measurementValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(measurementValueObject: MeasurementValueObject): MeasurementValueObject {
        log.debug("Request to save Measurement : {}", measurementValueObject)

        var measurementModel = measurementMapper.toEntity(measurementValueObject)
        measurementModel = measurementRepository.save(measurementModel)
        return measurementMapper.toDto(measurementModel)
    }

    /**
     * Get all the measurements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<MeasurementValueObject> {
        log.debug("Request to get all Measurements")
        return measurementRepository.findAll(pageable)
            .map(measurementMapper::toDto)
    }

    /**
     * Get one measurement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<MeasurementValueObject> {
        log.debug("Request to get Measurement : {}", id)
        return measurementRepository.findById(id)
            .map(measurementMapper::toDto)
    }

    /**
     * Delete the measurement by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Measurement : {}", id)

        measurementRepository.deleteById(id)
    }
}
