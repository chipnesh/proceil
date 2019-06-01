package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.MaterialArrivalModel
import me.chipnesh.proceil.domain.MaterialArrivalModel_
import me.chipnesh.proceil.domain.MaterialRequestModel_
import me.chipnesh.proceil.repository.MaterialArrivalRepository
import me.chipnesh.proceil.service.dto.MaterialArrivalCriteria
import me.chipnesh.proceil.service.dto.MaterialArrivalValueObject
import me.chipnesh.proceil.service.mapper.MaterialArrivalMapper

/**
 * Service for executing complex queries for [MaterialArrivalModel] entities in the database.
 * The main input is a [MaterialArrivalCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [MaterialArrivalValueObject] or a [Page] of [MaterialArrivalValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class MaterialArrivalQueryService(
    val materialArrivalRepository: MaterialArrivalRepository,
    val materialArrivalMapper: MaterialArrivalMapper
) : QueryService<MaterialArrivalModel>() {

    private val log = LoggerFactory.getLogger(MaterialArrivalQueryService::class.java)

    /**
     * Return a [MutableList] of [MaterialArrivalValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MaterialArrivalCriteria?): MutableList<MaterialArrivalValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return materialArrivalMapper.toDto(materialArrivalRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [MaterialArrivalValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MaterialArrivalCriteria?, page: Pageable): Page<MaterialArrivalValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return materialArrivalRepository.findAll(specification, page)
            .map(materialArrivalMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: MaterialArrivalCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return materialArrivalRepository.count(specification)
    }

    /**
     * Function to convert [MaterialArrivalCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: MaterialArrivalCriteria?): Specification<MaterialArrivalModel?> {
        var specification: Specification<MaterialArrivalModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, MaterialArrivalModel_.id))
            }
            if (criteria.arrivalSummary != null) {
                specification = specification.and(buildStringSpecification(criteria.arrivalSummary, MaterialArrivalModel_.arrivalSummary))
            }
            if (criteria.arrivalDate != null) {
                specification = specification.and(buildRangeSpecification(criteria.arrivalDate, MaterialArrivalModel_.arrivalDate))
            }
            if (criteria.arrivedQuantity != null) {
                specification = specification.and(buildRangeSpecification(criteria.arrivedQuantity, MaterialArrivalModel_.arrivedQuantity))
            }
            if (criteria.measureUnit != null) {
                specification = specification.and(buildSpecification(criteria.measureUnit, MaterialArrivalModel_.measureUnit))
            }
            if (criteria.requestId != null) {
                specification = specification.and(buildSpecification(criteria.requestId) {
                    it.join(MaterialArrivalModel_.request, JoinType.LEFT).get(MaterialRequestModel_.id)
                })
            }
        }
        return specification
    }
}
