package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.FacilityService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.FacilityValueObject
import me.chipnesh.proceil.service.dto.FacilityCriteria
import me.chipnesh.proceil.service.FacilityQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.FacilityModel].
 */
@RestController
@RequestMapping("/api")
class FacilityResource(
    val facilityService: FacilityService,
    val facilityQueryService: FacilityQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /facilities` : Create a new facility.
     *
     * @param facilityValueObject the facilityValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new facilityValueObject, or with status `400 (Bad Request)` if the facility has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/facilities")
    fun createFacility(@RequestBody facilityValueObject: FacilityValueObject): ResponseEntity<FacilityValueObject> {
        log.debug("REST request to save Facility : {}", facilityValueObject)
        if (facilityValueObject.id != null) {
            throw BadRequestAlertException("A new facility cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = facilityService.save(facilityValueObject)
        return ResponseEntity.created(URI("/api/facilities/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /facilities` : Updates an existing facility.
     *
     * @param facilityValueObject the facilityValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated facilityValueObject,
     * or with status `400 (Bad Request)` if the facilityValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the facilityValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/facilities")
    fun updateFacility(@RequestBody facilityValueObject: FacilityValueObject): ResponseEntity<FacilityValueObject> {
        log.debug("REST request to update Facility : {}", facilityValueObject)
        if (facilityValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = facilityService.save(facilityValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, facilityValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /facilities` : get all the facilities.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of facilities in body.
     */
    @GetMapping("/facilities")
    fun getAllFacilities(criteria: FacilityCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<FacilityValueObject>> {
        log.debug("REST request to get Facilities by criteria: {}", criteria)
        val page = facilityQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /facilities/count}` : count all the facilities.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/facilities/count")
    fun countFacilities(criteria: FacilityCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Facilities by criteria: {}", criteria)
        return ResponseEntity.ok().body(facilityQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /facilities/:id` : get the "id" facility.
     *
     * @param id the id of the facilityValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the facilityValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/facilities/{id}")
    fun getFacility(@PathVariable id: Long): ResponseEntity<FacilityValueObject> {
        log.debug("REST request to get Facility : {}", id)
        val facilityValueObject = facilityService.findOne(id)
        return ResponseUtil.wrapOrNotFound(facilityValueObject)
    }

    /**
     * `DELETE  /facilities/:id` : delete the "id" facility.
     *
     * @param id the id of the facilityValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/facilities/{id}")
    fun deleteFacility(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Facility : {}", id)
        facilityService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "facility"
    }
}
