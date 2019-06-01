package me.chipnesh.proceil.service

import javax.persistence.criteria.JoinType

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import io.github.jhipster.service.QueryService

import me.chipnesh.proceil.domain.FeedbackModel
import me.chipnesh.proceil.domain.FeedbackModel_
import me.chipnesh.proceil.domain.CustomerModel_
import me.chipnesh.proceil.repository.FeedbackRepository
import me.chipnesh.proceil.service.dto.FeedbackCriteria
import me.chipnesh.proceil.service.dto.FeedbackValueObject
import me.chipnesh.proceil.service.mapper.FeedbackMapper

/**
 * Service for executing complex queries for [FeedbackModel] entities in the database.
 * The main input is a [FeedbackCriteria] which gets converted to [Specification],
 * in a way that all the filters must apply.
 * It returns a [MutableList] of [FeedbackValueObject] or a [Page] of [FeedbackValueObject] which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
class FeedbackQueryService(
    val feedbackRepository: FeedbackRepository,
    val feedbackMapper: FeedbackMapper
) : QueryService<FeedbackModel>() {

    private val log = LoggerFactory.getLogger(FeedbackQueryService::class.java)

    /**
     * Return a [MutableList] of [FeedbackValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: FeedbackCriteria?): MutableList<FeedbackValueObject> {
        log.debug("find by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return feedbackMapper.toDto(feedbackRepository.findAll(specification))
    }

    /**
     * Return a [Page] of [FeedbackValueObject] which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    fun findByCriteria(criteria: FeedbackCriteria?, page: Pageable): Page<FeedbackValueObject> {
        log.debug("find by criteria : {}, page: {}", criteria, page)
        val specification = createSpecification(criteria)
        return feedbackRepository.findAll(specification, page)
            .map(feedbackMapper::toDto)
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    fun countByCriteria(criteria: FeedbackCriteria?): Long {
        log.debug("count by criteria : {}", criteria)
        val specification = createSpecification(criteria)
        return feedbackRepository.count(specification)
    }

    /**
     * Function to convert [FeedbackCriteria] to a [Specification].
     */
    private fun createSpecification(criteria: FeedbackCriteria?): Specification<FeedbackModel?> {
        var specification: Specification<FeedbackModel?> = Specification.where(null)
        if (criteria != null) {
            if (criteria.id != null) {
                specification = specification.and(buildSpecification(criteria.id, FeedbackModel_.id))
            }
            if (criteria.caption != null) {
                specification = specification.and(buildStringSpecification(criteria.caption, FeedbackModel_.caption))
            }
            if (criteria.email != null) {
                specification = specification.and(buildStringSpecification(criteria.email, FeedbackModel_.email))
            }
            if (criteria.authorId != null) {
                specification = specification.and(buildSpecification(criteria.authorId) {
                    it.join(FeedbackModel_.author, JoinType.LEFT).get(CustomerModel_.id)
                })
            }
        }
        return specification
    }
}
