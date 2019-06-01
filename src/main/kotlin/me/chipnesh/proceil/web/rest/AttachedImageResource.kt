package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.AttachedImageService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.AttachedImageValueObject
import me.chipnesh.proceil.service.dto.AttachedImageCriteria
import me.chipnesh.proceil.service.AttachedImageQueryService

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

import java.net.URI
import java.net.URISyntaxException

/**
 * REST controller for managing [me.chipnesh.proceil.domain.AttachedImageModel].
 */
@RestController
@RequestMapping("/api")
class AttachedImageResource(
    val attachedImageService: AttachedImageService,
    val attachedImageQueryService: AttachedImageQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /attached-images` : Create a new attachedImage.
     *
     * @param attachedImageValueObject the attachedImageValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new attachedImageValueObject, or with status `400 (Bad Request)` if the attachedImage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/attached-images")
    fun createAttachedImage(@RequestBody attachedImageValueObject: AttachedImageValueObject): ResponseEntity<AttachedImageValueObject> {
        log.debug("REST request to save AttachedImage : {}", attachedImageValueObject)
        if (attachedImageValueObject.id != null) {
            throw BadRequestAlertException("A new attachedImage cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = attachedImageService.save(attachedImageValueObject)
        return ResponseEntity.created(URI("/api/attached-images/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /attached-images` : Updates an existing attachedImage.
     *
     * @param attachedImageValueObject the attachedImageValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated attachedImageValueObject,
     * or with status `400 (Bad Request)` if the attachedImageValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the attachedImageValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/attached-images")
    fun updateAttachedImage(@RequestBody attachedImageValueObject: AttachedImageValueObject): ResponseEntity<AttachedImageValueObject> {
        log.debug("REST request to update AttachedImage : {}", attachedImageValueObject)
        if (attachedImageValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = attachedImageService.save(attachedImageValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, attachedImageValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /attached-images` : get all the attachedImages.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of attachedImages in body.
     */
    @GetMapping("/attached-images")
    fun getAllAttachedImages(criteria: AttachedImageCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<AttachedImageValueObject>> {
        log.debug("REST request to get AttachedImages by criteria: {}", criteria)
        val page = attachedImageQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /attached-images/count}` : count all the attachedImages.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/attached-images/count")
    fun countAttachedImages(criteria: AttachedImageCriteria): ResponseEntity<Long> {
        log.debug("REST request to count AttachedImages by criteria: {}", criteria)
        return ResponseEntity.ok().body(attachedImageQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /attached-images/:id` : get the "id" attachedImage.
     *
     * @param id the id of the attachedImageValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the attachedImageValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/attached-images/{id}")
    fun getAttachedImage(@PathVariable id: Long): ResponseEntity<AttachedImageValueObject> {
        log.debug("REST request to get AttachedImage : {}", id)
        val attachedImageValueObject = attachedImageService.findOne(id)
        return ResponseUtil.wrapOrNotFound(attachedImageValueObject)
    }

    /**
     * `DELETE  /attached-images/:id` : delete the "id" attachedImage.
     *
     * @param id the id of the attachedImageValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/attached-images/{id}")
    fun deleteAttachedImage(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete AttachedImage : {}", id)
        attachedImageService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "attachedImage"
    }
}
