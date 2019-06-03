package me.chipnesh.proceil.service

import io.github.jhipster.service.filter.IntegerFilter
import io.github.jhipster.service.filter.LongFilter
import me.chipnesh.proceil.domain.MaterialReserveModel
import me.chipnesh.proceil.domain.enumeration.MaterialReserveStatus
import me.chipnesh.proceil.domain.enumeration.MaterialReserveStatus.*
import me.chipnesh.proceil.repository.MaterialReserveRepository
import me.chipnesh.proceil.service.dto.MaterialAvailabilityCriteria
import me.chipnesh.proceil.service.dto.MaterialReserveValueObject
import me.chipnesh.proceil.service.mapper.MaterialReserveMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [MaterialReserveModel].
 */
@Service
@Transactional
class MaterialReserveService(
    val materialReserveRepository: MaterialReserveRepository,
    val materialReserveMapper: MaterialReserveMapper,
    val materialAvailabilityQueryService: MaterialAvailabilityQueryService,
    val materialAvailabilityService: MaterialAvailabilityService
) {

    private val log = LoggerFactory.getLogger(MaterialReserveService::class.java)

    /**
     * Save a materialReserve.
     *
     * @param materialReserveValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(materialReserveValueObject: MaterialReserveValueObject): MaterialReserveValueObject {
        log.debug("Request to save MaterialReserve : {}", materialReserveValueObject)

        if (materialReserveValueObject.id == null) {
            materialReserveValueObject.reserveStatus = NEW
        }

        var materialReserveModel = materialReserveMapper.toEntity(materialReserveValueObject)
        materialReserveModel = materialReserveRepository.save(materialReserveModel)

        materialReserveModel.material?.id?.let { id ->
            val query = MaterialAvailabilityCriteria().apply {
                materialId = LongFilter().apply { equals = id }
                remainingQuantity = IntegerFilter().apply { greaterOrEqualThan = materialReserveModel.quantityToReserve }
            }
            materialAvailabilityQueryService.findByCriteria(query).firstOrNull()?.let { available ->
                val remaining = requireNotNull(available.remainingQuantity)
                val toReserve = requireNotNull(materialReserveModel.quantityToReserve)
                available.remainingQuantity = remaining - toReserve
                materialReserveModel.reserveStatus = RESERVED
                materialAvailabilityService.save(available)

            } ?: run {
                materialReserveModel.reserveStatus = OUT_OF_STOCK
            }
            materialReserveRepository.save(materialReserveModel)
        }
        return materialReserveMapper.toDto(materialReserveModel)
    }

    /**
     * Get all the materialReserves.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<MaterialReserveValueObject> {
        log.debug("Request to get all MaterialReserves")
        return materialReserveRepository.findAll(pageable)
            .map(materialReserveMapper::toDto)
    }

    /**
     * Get one materialReserve by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<MaterialReserveValueObject> {
        log.debug("Request to get MaterialReserve : {}", id)
        return materialReserveRepository.findById(id)
            .map(materialReserveMapper::toDto)
    }

    /**
     * Delete the materialReserve by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete MaterialReserve : {}", id)
        materialReserveRepository.findById(id).ifPresent { materialReserveModel ->
            materialReserveModel.material?.id?.let { id ->
                val query = MaterialAvailabilityCriteria().apply {
                    materialId = LongFilter().apply { equals = id }
                }
                materialAvailabilityQueryService.findByCriteria(query).firstOrNull()?.let {
                    val remaining = requireNotNull(it.remainingQuantity)
                    val toReserve = requireNotNull(materialReserveModel.quantityToReserve)
                    it.remainingQuantity = remaining + toReserve
                    materialAvailabilityService.save(it)

                    materialReserveModel.reserveStatus = RESERVED
                    materialReserveRepository.save(materialReserveModel)
                }
            }
        }

        materialReserveRepository.deleteById(id)
    }
}
