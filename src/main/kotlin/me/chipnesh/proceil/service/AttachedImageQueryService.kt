package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.AttachedImageModel
import me.chipnesh.proceil.domain.AttachedImageModel_
import me.chipnesh.proceil.domain.MaterialModel_
import me.chipnesh.proceil.domain.ServiceModel_
import me.chipnesh.proceil.repository.AttachedImageRepository
import me.chipnesh.proceil.service.dto.AttachedImageCriteria
import me.chipnesh.proceil.service.dto.AttachedImageValueObject
import me.chipnesh.proceil.service.mapper.AttachedImageMapper

/**
 * Service for executing complex queries for [AttachedImageModel] entities in the database.
 * The main input is a [AttachedImageCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [AttachedImageValueObject] or a [Page] of [AttachedImageValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class AttachedImageQueryService(
    val attachedImageRepository: AttachedImageRepository,
    val attachedImageMapper: AttachedImageMapper
) : QueryService<AttachedImageModel>() {

    private val log = LoggerFactory.getLogger(AttachedImageQueryService::class.java)

    /**
     * Return a [MutableList] of [AttachedImageValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: AttachedImageCriteria?): MutableList<AttachedImageValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return attachedImageMapper.toDto(attachedImageRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [AttachedImageValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: AttachedImageCriteria?, page: Pageable): Page<AttachedImageValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return attachedImageRepository.findAll(specification, page)
            .map(attachedImageMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: AttachedImageCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return attachedImageRepository.count(specification)
    }

    /**
     * Function to convert [AttachedImageCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: AttachedImageCriteria?): Specification<AttachedImageModel?> {
        var specification: Specification<AttachedImageModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, AttachedImageModel_.id))
            }
            if (criteria.imageName != null) {
                specification = specification.and(buildStringSpecification(criteria.imageName, AttachedImageModel_.imageName))
            }
            if (criteria.materialId != null) {
                specification = specification.and(buildSpecification(criteria.materialId) {
                    it.join(AttachedImageModel_.material, JoinType.LEFT).get(MaterialModel_.id)
                })
            }
            if (criteria.serviceId != null) {
                specification = specification.and(buildSpecification(criteria.serviceId) {
                    it.join(AttachedImageModel_.service, JoinType.LEFT).get(ServiceModel_.id)
                })
            }
        }
        return specification
    }
}
