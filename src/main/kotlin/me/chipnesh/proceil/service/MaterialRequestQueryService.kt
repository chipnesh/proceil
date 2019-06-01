package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.MaterialRequestModel
import me.chipnesh.proceil.domain.MaterialRequestModel_
import me.chipnesh.proceil.domain.FacilityModel_
import me.chipnesh.proceil.domain.MaterialModel_
import me.chipnesh.proceil.repository.MaterialRequestRepository
import me.chipnesh.proceil.service.dto.MaterialRequestCriteria
import me.chipnesh.proceil.service.dto.MaterialRequestValueObject
import me.chipnesh.proceil.service.mapper.MaterialRequestMapper

/**
 * Service for executing complex queries for [MaterialRequestModel] entities in the database.
 * The main input is a [MaterialRequestCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [MaterialRequestValueObject] or a [Page] of [MaterialRequestValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class MaterialRequestQueryService(
    val materialRequestRepository: MaterialRequestRepository,
    val materialRequestMapper: MaterialRequestMapper
) : QueryService<MaterialRequestModel>() {

    private val log = LoggerFactory.getLogger(MaterialRequestQueryService::class.java)

    /**
     * Return a [MutableList] of [MaterialRequestValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MaterialRequestCriteria?): MutableList<MaterialRequestValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return materialRequestMapper.toDto(materialRequestRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [MaterialRequestValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MaterialRequestCriteria?, page: Pageable): Page<MaterialRequestValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return materialRequestRepository.findAll(specification, page)
            .map(materialRequestMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: MaterialRequestCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return materialRequestRepository.count(specification)
    }

    /**
     * Function to convert [MaterialRequestCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: MaterialRequestCriteria?): Specification<MaterialRequestModel?> {
        var specification: Specification<MaterialRequestModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, MaterialRequestModel_.id))
            }
            if (criteria.requestSummary != null) {
                specification = specification.and(buildStringSpecification(criteria.requestSummary, MaterialRequestModel_.requestSummary))
            }
            if (criteria.createdDate != null) {
                specification = specification.and(buildRangeSpecification(criteria.createdDate, MaterialRequestModel_.createdDate))
            }
            if (criteria.closedDate != null) {
                specification = specification.and(buildRangeSpecification(criteria.closedDate, MaterialRequestModel_.closedDate))
            }
            if (criteria.requestPriority != null) {
                specification = specification.and(buildRangeSpecification(criteria.requestPriority, MaterialRequestModel_.requestPriority))
            }
            if (criteria.requestStatus != null) {
                specification = specification.and(buildSpecification(criteria.requestStatus, MaterialRequestModel_.requestStatus))
            }
            if (criteria.requestedQuantity != null) {
                specification = specification.and(buildRangeSpecification(criteria.requestedQuantity, MaterialRequestModel_.requestedQuantity))
            }
            if (criteria.measureUnit != null) {
                specification = specification.and(buildSpecification(criteria.measureUnit, MaterialRequestModel_.measureUnit))
            }
            if (criteria.requesterId != null) {
                specification = specification.and(buildSpecification(criteria.requesterId) {
                    it.join(MaterialRequestModel_.requester, JoinType.LEFT).get(FacilityModel_.id)
                })
            }
            if (criteria.materialId != null) {
                specification = specification.and(buildSpecification(criteria.materialId) {
                    it.join(MaterialRequestModel_.material, JoinType.LEFT).get(MaterialModel_.id)
                })
            }
        }
        return specification
    }
}
