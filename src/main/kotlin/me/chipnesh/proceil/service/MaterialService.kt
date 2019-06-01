package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.MaterialModel
import me.chipnesh.proceil.repository.MaterialRepository
import me.chipnesh.proceil.service.dto.MaterialValueObject
import me.chipnesh.proceil.service.mapper.MaterialMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [MaterialModel].
 */
@Service
@Transactional
class MaterialService(
    val materialRepository: MaterialRepository,
    val materialMapper: MaterialMapper
) {

    private val log = LoggerFactory.getLogger(MaterialService::class.java)

    /**
     * Save a material.
     *
     * @param materialValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(materialValueObject: MaterialValueObject): MaterialValueObject {
        log.debug("Request to save Material : {}", materialValueObject)

        var materialModel = materialMapper.toEntity(materialValueObject)
        materialModel = materialRepository.save(materialModel)
        return materialMapper.toDto(materialModel)
    }

    /**
     * Get all the materials.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<MaterialValueObject> {
        log.debug("Request to get all Materials")
        return materialRepository.findAll(pageable)
            .map(materialMapper::toDto)
    }

    /**
     * Get one material by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<MaterialValueObject> {
        log.debug("Request to get Material : {}", id)
        return materialRepository.findById(id)
            .map(materialMapper::toDto)
    }

    /**
     * Delete the material by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Material : {}", id)

        materialRepository.deleteById(id)
    }
}
