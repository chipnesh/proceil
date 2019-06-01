package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.MaterialRequestService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.MaterialRequestValueObject
import me.chipnesh.proceil.service.dto.MaterialRequestCriteria
import me.chipnesh.proceil.service.MaterialRequestQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.MaterialRequestModel].
 */
@RestController
@RequestMapping("/api")
class MaterialRequestResource(
    val materialRequestService: MaterialRequestService,
    val materialRequestQueryService: MaterialRequestQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /material-requests` : Create a new materialRequest.
     *
     * @param materialRequestValueObject the materialRequestValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new materialRequestValueObject, or with status `400 (Bad Request)` if the materialRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/material-requests")
    fun createMaterialRequest(@RequestBody materialRequestValueObject: MaterialRequestValueObject): ResponseEntity<MaterialRequestValueObject> {
        log.debug("REST request to save MaterialRequest : {}", materialRequestValueObject)
        if (materialRequestValueObject.id != null) {
            throw BadRequestAlertException("A new materialRequest cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = materialRequestService.save(materialRequestValueObject)
        return ResponseEntity.created(URI("/api/material-requests/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /material-requests` : Updates an existing materialRequest.
     *
     * @param materialRequestValueObject the materialRequestValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated materialRequestValueObject,
     * or with status `400 (Bad Request)` if the materialRequestValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the materialRequestValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/material-requests")
    fun updateMaterialRequest(@RequestBody materialRequestValueObject: MaterialRequestValueObject): ResponseEntity<MaterialRequestValueObject> {
        log.debug("REST request to update MaterialRequest : {}", materialRequestValueObject)
        if (materialRequestValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = materialRequestService.save(materialRequestValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, materialRequestValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /material-requests` : get all the materialRequests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of materialRequests in body.
     */
    @GetMapping("/material-requests")
    fun getAllMaterialRequests(criteria: MaterialRequestCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<MaterialRequestValueObject>> {
        log.debug("REST request to get MaterialRequests by criteria: {}", criteria)
        val page = materialRequestQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /material-requests/count}` : count all the materialRequests.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/material-requests/count")
    fun countMaterialRequests(criteria: MaterialRequestCriteria): ResponseEntity<Long> {
        log.debug("REST request to count MaterialRequests by criteria: {}", criteria)
        return ResponseEntity.ok().body(materialRequestQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /material-requests/:id` : get the "id" materialRequest.
     *
     * @param id the id of the materialRequestValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the materialRequestValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/material-requests/{id}")
    fun getMaterialRequest(@PathVariable id: Long): ResponseEntity<MaterialRequestValueObject> {
        log.debug("REST request to get MaterialRequest : {}", id)
        val materialRequestValueObject = materialRequestService.findOne(id)
        return ResponseUtil.wrapOrNotFound(materialRequestValueObject)
    }

    /**
     * `DELETE  /material-requests/:id` : delete the "id" materialRequest.
     *
     * @param id the id of the materialRequestValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/material-requests/{id}")
    fun deleteMaterialRequest(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete MaterialRequest : {}", id)
        materialRequestService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "materialRequest"
    }
}
