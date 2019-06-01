package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.MaterialModel
import me.chipnesh.proceil.domain.MaterialModel_
import me.chipnesh.proceil.domain.AttachedImageModel_
import me.chipnesh.proceil.repository.MaterialRepository
import me.chipnesh.proceil.service.dto.MaterialCriteria
import me.chipnesh.proceil.service.dto.MaterialValueObject
import me.chipnesh.proceil.service.mapper.MaterialMapper

/**
 * Service for executing complex queries for [MaterialModel] entities in the database.
 * The main input is a [MaterialCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [MaterialValueObject] or a [Page] of [MaterialValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class MaterialQueryService(
    val materialRepository: MaterialRepository,
    val materialMapper: MaterialMapper
) : QueryService<MaterialModel>() {

    private val log = LoggerFactory.getLogger(MaterialQueryService::class.java)

    /**
     * Return a [MutableList] of [MaterialValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MaterialCriteria?): MutableList<MaterialValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return materialMapper.toDto(materialRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [MaterialValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: MaterialCriteria?, page: Pageable): Page<MaterialValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return materialRepository.findAll(specification, page)
            .map(materialMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: MaterialCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return materialRepository.count(specification)
    }

    /**
     * Function to convert [MaterialCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: MaterialCriteria?): Specification<MaterialModel?> {
        var specification: Specification<MaterialModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, MaterialModel_.id))
            }
            if (criteria.materialName != null) {
                specification = specification.and(buildStringSpecification(criteria.materialName, MaterialModel_.materialName))
            }
            if (criteria.materialDescription != null) {
                specification = specification.and(buildStringSpecification(criteria.materialDescription, MaterialModel_.materialDescription))
            }
            if (criteria.materialPrice != null) {
                specification = specification.and(buildRangeSpecification(criteria.materialPrice, MaterialModel_.materialPrice))
            }
            if (criteria.imageId != null) {
                specification = specification.and(buildSpecification(criteria.imageId) {
                    it.join(MaterialModel_.images, JoinType.LEFT).get(AttachedImageModel_.id)
                })
            }
        }
        return specification
    }
}
