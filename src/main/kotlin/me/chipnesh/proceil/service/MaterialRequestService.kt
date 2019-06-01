package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.MaterialRequestModel
import me.chipnesh.proceil.repository.MaterialRequestRepository
import me.chipnesh.proceil.service.dto.MaterialRequestValueObject
import me.chipnesh.proceil.service.mapper.MaterialRequestMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [MaterialRequestModel].
 */
@Service
@Transactional
class MaterialRequestService(
    val materialRequestRepository: MaterialRequestRepository,
    val materialRequestMapper: MaterialRequestMapper
) {

    private val log = LoggerFactory.getLogger(MaterialRequestService::class.java)

    /**
     * Save a materialRequest.
     *
     * @param materialRequestValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(materialRequestValueObject: MaterialRequestValueObject): MaterialRequestValueObject {
        log.debug("Request to save MaterialRequest : {}", materialRequestValueObject)

        var materialRequestModel = materialRequestMapper.toEntity(materialRequestValueObject)
        materialRequestModel = materialRequestRepository.save(materialRequestModel)
        return materialRequestMapper.toDto(materialRequestModel)
    }

    /**
     * Get all the materialRequests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<MaterialRequestValueObject> {
        log.debug("Request to get all MaterialRequests")
        return materialRequestRepository.findAll(pageable)
            .map(materialRequestMapper::toDto)
    }

    /**
     * Get one materialRequest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<MaterialRequestValueObject> {
        log.debug("Request to get MaterialRequest : {}", id)
        return materialRequestRepository.findById(id)
            .map(materialRequestMapper::toDto)
    }

    /**
     * Delete the materialRequest by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete MaterialRequest : {}", id)

        materialRequestRepository.deleteById(id)
    }
}
