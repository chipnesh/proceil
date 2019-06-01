package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.MaterialReserveModel
import me.chipnesh.proceil.domain.MaterialReserveModel_
import me.chipnesh.proceil.domain.MaterialModel_
import me.chipnesh.proceil.repository.MaterialReserveRepository
import me.chipnesh.proceil.service.dto.MaterialReserveCriteria
import me.chipnesh.proceil.service.dto.MaterialReserveValueObject
import me.chipnesh.proceil.service.mapper.MaterialReserveMapper

/**
 * Service for executing complex queries for [MaterialReserveModel] entities in the database.
 * The main input is a [MaterialReserveCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [MaterialReserveValueObject] or a [Page] of [MaterialReserveValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class MaterialReserveQueryService(
    val materialReserveRepository: MaterialReserveRepository,
    val materialReserveMapper: MaterialReserveMapper
) : QueryService<MaterialReserveModel>() {

    private val log = LoggerFactory.getLogger(MaterialReserveQueryService::class.java)

    /**
     * Return a [MutableList] of [MaterialReserveValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MaterialReserveCriteria?): MutableList<MaterialReserveValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return materialReserveMapper.toDto(materialReserveRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [MaterialReserveValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MaterialReserveCriteria?, page: Pageable): Page<MaterialReserveValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return materialReserveRepository.findAll(specification, page)
            .map(materialReserveMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: MaterialReserveCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return materialReserveRepository.count(specification)
    }

    /**
     * Function to convert [MaterialReserveCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: MaterialReserveCriteria?): Specification<MaterialReserveModel?> {
        var specification: Specification<MaterialReserveModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, MaterialReserveModel_.id))
            }
            if (criteria.reserveDate != null) {
                specification = specification.and(buildRangeSpecification(criteria.reserveDate, MaterialReserveModel_.reserveDate))
            }
            if (criteria.reserveStatus != null) {
                specification = specification.and(buildSpecification(criteria.reserveStatus, MaterialReserveModel_.reserveStatus))
            }
            if (criteria.quantityToReserve != null) {
                specification = specification.and(buildRangeSpecification(criteria.quantityToReserve, MaterialReserveModel_.quantityToReserve))
            }
            if (criteria.measureUnit != null) {
                specification = specification.and(buildSpecification(criteria.measureUnit, MaterialReserveModel_.measureUnit))
            }
            if (criteria.materialId != null) {
                specification = specification.and(buildSpecification(criteria.materialId) {
                    it.join(MaterialReserveModel_.material, JoinType.LEFT).get(MaterialModel_.id)
                })
            }
        }
        return specification
    }
}
