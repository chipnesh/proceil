package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.MaterialArrivalModel
import me.chipnesh.proceil.repository.MaterialArrivalRepository
import me.chipnesh.proceil.service.dto.MaterialArrivalValueObject
import me.chipnesh.proceil.service.mapper.MaterialArrivalMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [MaterialArrivalModel].
 */
@Service
@Transactional
class MaterialArrivalService(
    val materialArrivalRepository: MaterialArrivalRepository,
    val materialArrivalMapper: MaterialArrivalMapper
) {

    private val log = LoggerFactory.getLogger(MaterialArrivalService::class.java)

    /**
     * Save a materialArrival.
     *
     * @param materialArrivalValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(materialArrivalValueObject: MaterialArrivalValueObject): MaterialArrivalValueObject {
        log.debug("Request to save MaterialArrival : {}", materialArrivalValueObject)

        var materialArrivalModel = materialArrivalMapper.toEntity(materialArrivalValueObject)
        materialArrivalModel = materialArrivalRepository.save(materialArrivalModel)
        return materialArrivalMapper.toDto(materialArrivalModel)
    }

    /**
     * Get all the materialArrivals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<MaterialArrivalValueObject> {
        log.debug("Request to get all MaterialArrivals")
        return materialArrivalRepository.findAll(pageable)
            .map(materialArrivalMapper::toDto)
    }

    /**
     * Get one materialArrival by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<MaterialArrivalValueObject> {
        log.debug("Request to get MaterialArrival : {}", id)
        return materialArrivalRepository.findById(id)
            .map(materialArrivalMapper::toDto)
    }

    /**
     * Delete the materialArrival by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete MaterialArrival : {}", id)

        materialArrivalRepository.deleteById(id)
    }
}
