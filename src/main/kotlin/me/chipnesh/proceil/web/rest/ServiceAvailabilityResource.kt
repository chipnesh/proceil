package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.ServiceAvailabilityService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.ServiceAvailabilityValueObject
import me.chipnesh.proceil.service.dto.ServiceAvailabilityCriteria
import me.chipnesh.proceil.service.ServiceAvailabilityQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.ServiceAvailabilityModel].
 */
@RestController
@RequestMapping("/api")
class ServiceAvailabilityResource(
    val serviceAvailabilityService: ServiceAvailabilityService,
    val serviceAvailabilityQueryService: ServiceAvailabilityQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /service-availabilities` : Create a new serviceAvailability.
     *
     * @param serviceAvailabilityValueObject the serviceAvailabilityValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new serviceAvailabilityValueObject, or with status `400 (Bad Request)` if the serviceAvailability has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/service-availabilities")
    fun createServiceAvailability(@RequestBody serviceAvailabilityValueObject: ServiceAvailabilityValueObject): ResponseEntity<ServiceAvailabilityValueObject> {
        log.debug("REST request to save ServiceAvailability : {}", serviceAvailabilityValueObject)
        if (serviceAvailabilityValueObject.id != null) {
            throw BadRequestAlertException("A new serviceAvailability cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = serviceAvailabilityService.save(serviceAvailabilityValueObject)
        return ResponseEntity.created(URI("/api/service-availabilities/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /service-availabilities` : Updates an existing serviceAvailability.
     *
     * @param serviceAvailabilityValueObject the serviceAvailabilityValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated serviceAvailabilityValueObject,
     * or with status `400 (Bad Request)` if the serviceAvailabilityValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the serviceAvailabilityValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/service-availabilities")
    fun updateServiceAvailability(@RequestBody serviceAvailabilityValueObject: ServiceAvailabilityValueObject): ResponseEntity<ServiceAvailabilityValueObject> {
        log.debug("REST request to update ServiceAvailability : {}", serviceAvailabilityValueObject)
        if (serviceAvailabilityValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = serviceAvailabilityService.save(serviceAvailabilityValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceAvailabilityValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /service-availabilities` : get all the serviceAvailabilities.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of serviceAvailabilities in body.
     */
    @GetMapping("/service-availabilities")
    fun getAllServiceAvailabilities(criteria: ServiceAvailabilityCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<ServiceAvailabilityValueObject>> {
        log.debug("REST request to get ServiceAvailabilities by criteria: {}", criteria)
        val page = serviceAvailabilityQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /service-availabilities/count}` : count all the serviceAvailabilities.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/service-availabilities/count")
    fun countServiceAvailabilities(criteria: ServiceAvailabilityCriteria): ResponseEntity<Long> {
        log.debug("REST request to count ServiceAvailabilities by criteria: {}", criteria)
        return ResponseEntity.ok().body(serviceAvailabilityQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /service-availabilities/:id` : get the "id" serviceAvailability.
     *
     * @param id the id of the serviceAvailabilityValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the serviceAvailabilityValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/service-availabilities/{id}")
    fun getServiceAvailability(@PathVariable id: Long): ResponseEntity<ServiceAvailabilityValueObject> {
        log.debug("REST request to get ServiceAvailability : {}", id)
        val serviceAvailabilityValueObject = serviceAvailabilityService.findOne(id)
        return ResponseUtil.wrapOrNotFound(serviceAvailabilityValueObject)
    }

    /**
     * `DELETE  /service-availabilities/:id` : delete the "id" serviceAvailability.
     *
     * @param id the id of the serviceAvailabilityValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/service-availabilities/{id}")
    fun deleteServiceAvailability(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete ServiceAvailability : {}", id)
        serviceAvailabilityService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "serviceAvailability"
    }
}
