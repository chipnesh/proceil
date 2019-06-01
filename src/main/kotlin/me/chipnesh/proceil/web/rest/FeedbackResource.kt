package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.FeedbackService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.FeedbackValueObject
import me.chipnesh.proceil.service.dto.FeedbackCriteria
import me.chipnesh.proceil.service.FeedbackQueryService

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.validation.Valid
import java.net.URI
import java.net.URISyntaxException

/**
 * REST controller for managing [me.chipnesh.proceil.domain.FeedbackModel].
 */
@RestController
@RequestMapping("/api")
class FeedbackResource(
    val feedbackService: FeedbackService,
    val feedbackQueryService: FeedbackQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /feedbacks` : Create a new feedback.
     *
     * @param feedbackValueObject the feedbackValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new feedbackValueObject, or with status `400 (Bad Request)` if the feedback has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/feedbacks")
    fun createFeedback(@Valid @RequestBody feedbackValueObject: FeedbackValueObject): ResponseEntity<FeedbackValueObject> {
        log.debug("REST request to save Feedback : {}", feedbackValueObject)
        if (feedbackValueObject.id != null) {
            throw BadRequestAlertException("A new feedback cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = feedbackService.save(feedbackValueObject)
        return ResponseEntity.created(URI("/api/feedbacks/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /feedbacks` : Updates an existing feedback.
     *
     * @param feedbackValueObject the feedbackValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated feedbackValueObject,
     * or with status `400 (Bad Request)` if the feedbackValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the feedbackValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/feedbacks")
    fun updateFeedback(@Valid @RequestBody feedbackValueObject: FeedbackValueObject): ResponseEntity<FeedbackValueObject> {
        log.debug("REST request to update Feedback : {}", feedbackValueObject)
        if (feedbackValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = feedbackService.save(feedbackValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, feedbackValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /feedbacks` : get all the feedbacks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of feedbacks in body.
     */
    @GetMapping("/feedbacks")
    fun getAllFeedbacks(criteria: FeedbackCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<FeedbackValueObject>> {
        log.debug("REST request to get Feedbacks by criteria: {}", criteria)
        val page = feedbackQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /feedbacks/count}` : count all the feedbacks.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/feedbacks/count")
    fun countFeedbacks(criteria: FeedbackCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Feedbacks by criteria: {}", criteria)
        return ResponseEntity.ok().body(feedbackQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /feedbacks/:id` : get the "id" feedback.
     *
     * @param id the id of the feedbackValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the feedbackValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/feedbacks/{id}")
    fun getFeedback(@PathVariable id: Long): ResponseEntity<FeedbackValueObject> {
        log.debug("REST request to get Feedback : {}", id)
        val feedbackValueObject = feedbackService.findOne(id)
        return ResponseUtil.wrapOrNotFound(feedbackValueObject)
    }

    /**
     * `DELETE  /feedbacks/:id` : delete the "id" feedback.
     *
     * @param id the id of the feedbackValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/feedbacks/{id}")
    fun deleteFeedback(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Feedback : {}", id)
        feedbackService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "feedback"
    }
}
