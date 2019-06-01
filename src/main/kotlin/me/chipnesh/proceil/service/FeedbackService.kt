package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.FeedbackModel
import me.chipnesh.proceil.repository.FeedbackRepository
import me.chipnesh.proceil.service.dto.FeedbackValueObject
import me.chipnesh.proceil.service.mapper.FeedbackMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [FeedbackModel].
 */
@Service
@Transactional
class FeedbackService(
    val feedbackRepository: FeedbackRepository,
    val feedbackMapper: FeedbackMapper
) {

    private val log = LoggerFactory.getLogger(FeedbackService::class.java)

    /**
     * Save a feedback.
     *
     * @param feedbackValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(feedbackValueObject: FeedbackValueObject): FeedbackValueObject {
        log.debug("Request to save Feedback : {}", feedbackValueObject)

        var feedbackModel = feedbackMapper.toEntity(feedbackValueObject)
        feedbackModel = feedbackRepository.save(feedbackModel)
        return feedbackMapper.toDto(feedbackModel)
    }

    /**
     * Get all the feedbacks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<FeedbackValueObject> {
        log.debug("Request to get all Feedbacks")
        return feedbackRepository.findAll(pageable)
            .map(feedbackMapper::toDto)
    }

    /**
     * Get one feedback by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<FeedbackValueObject> {
        log.debug("Request to get Feedback : {}", id)
        return feedbackRepository.findById(id)
            .map(feedbackMapper::toDto)
    }

    /**
     * Delete the feedback by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Feedback : {}", id)

        feedbackRepository.deleteById(id)
    }
}
