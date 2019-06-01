package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.MaterialAvailabilityService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.MaterialAvailabilityValueObject
import me.chipnesh.proceil.service.dto.MaterialAvailabilityCriteria
import me.chipnesh.proceil.service.MaterialAvailabilityQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.MaterialAvailabilityModel].
 */
@RestController
@RequestMapping("/api")
class MaterialAvailabilityResource(
    val materialAvailabilityService: MaterialAvailabilityService,
    val materialAvailabilityQueryService: MaterialAvailabilityQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /material-availabilities` : Create a new materialAvailability.
     *
     * @param materialAvailabilityValueObject the materialAvailabilityValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new materialAvailabilityValueObject, or with status `400 (Bad Request)` if the materialAvailability has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/material-availabilities")
    fun createMaterialAvailability(@RequestBody materialAvailabilityValueObject: MaterialAvailabilityValueObject): ResponseEntity<MaterialAvailabilityValueObject> {
        log.debug("REST request to save MaterialAvailability : {}", materialAvailabilityValueObject)
        if (materialAvailabilityValueObject.id != null) {
            throw BadRequestAlertException("A new materialAvailability cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = materialAvailabilityService.save(materialAvailabilityValueObject)
        return ResponseEntity.created(URI("/api/material-availabilities/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /material-availabilities` : Updates an existing materialAvailability.
     *
     * @param materialAvailabilityValueObject the materialAvailabilityValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated materialAvailabilityValueObject,
     * or with status `400 (Bad Request)` if the materialAvailabilityValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the materialAvailabilityValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/material-availabilities")
    fun updateMaterialAvailability(@RequestBody materialAvailabilityValueObject: MaterialAvailabilityValueObject): ResponseEntity<MaterialAvailabilityValueObject> {
        log.debug("REST request to update MaterialAvailability : {}", materialAvailabilityValueObject)
        if (materialAvailabilityValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = materialAvailabilityService.save(materialAvailabilityValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, materialAvailabilityValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /material-availabilities` : get all the materialAvailabilities.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of materialAvailabilities in body.
     */
    @GetMapping("/material-availabilities")
    fun getAllMaterialAvailabilities(criteria: MaterialAvailabilityCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<MaterialAvailabilityValueObject>> {
        log.debug("REST request to get MaterialAvailabilities by criteria: {}", criteria)
        val page = materialAvailabilityQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /material-availabilities/count}` : count all the materialAvailabilities.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/material-availabilities/count")
    fun countMaterialAvailabilities(criteria: MaterialAvailabilityCriteria): ResponseEntity<Long> {
        log.debug("REST request to count MaterialAvailabilities by criteria: {}", criteria)
        return ResponseEntity.ok().body(materialAvailabilityQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /material-availabilities/:id` : get the "id" materialAvailability.
     *
     * @param id the id of the materialAvailabilityValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the materialAvailabilityValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/material-availabilities/{id}")
    fun getMaterialAvailability(@PathVariable id: Long): ResponseEntity<MaterialAvailabilityValueObject> {
        log.debug("REST request to get MaterialAvailability : {}", id)
        val materialAvailabilityValueObject = materialAvailabilityService.findOne(id)
        return ResponseUtil.wrapOrNotFound(materialAvailabilityValueObject)
    }

    /**
     * `DELETE  /material-availabilities/:id` : delete the "id" materialAvailability.
     *
     * @param id the id of the materialAvailabilityValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/material-availabilities/{id}")
    fun deleteMaterialAvailability(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete MaterialAvailability : {}", id)
        materialAvailabilityService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "materialAvailability"
    }
}
