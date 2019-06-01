package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.ZoneService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.ZoneValueObject
import me.chipnesh.proceil.service.dto.ZoneCriteria
import me.chipnesh.proceil.service.ZoneQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.ZoneModel].
 */
@RestController
@RequestMapping("/api")
class ZoneResource(
    val zoneService: ZoneService,
    val zoneQueryService: ZoneQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /zones` : Create a new zone.
     *
     * @param zoneValueObject the zoneValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new zoneValueObject, or with status `400 (Bad Request)` if the zone has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/zones")
    fun createZone(@RequestBody zoneValueObject: ZoneValueObject): ResponseEntity<ZoneValueObject> {
        log.debug("REST request to save Zone : {}", zoneValueObject)
        if (zoneValueObject.id != null) {
            throw BadRequestAlertException("A new zone cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = zoneService.save(zoneValueObject)
        return ResponseEntity.created(URI("/api/zones/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /zones` : Updates an existing zone.
     *
     * @param zoneValueObject the zoneValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated zoneValueObject,
     * or with status `400 (Bad Request)` if the zoneValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the zoneValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/zones")
    fun updateZone(@RequestBody zoneValueObject: ZoneValueObject): ResponseEntity<ZoneValueObject> {
        log.debug("REST request to update Zone : {}", zoneValueObject)
        if (zoneValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = zoneService.save(zoneValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, zoneValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /zones` : get all the zones.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of zones in body.
     */
    @GetMapping("/zones")
    fun getAllZones(criteria: ZoneCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<ZoneValueObject>> {
        log.debug("REST request to get Zones by criteria: {}", criteria)
        val page = zoneQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /zones/count}` : count all the zones.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/zones/count")
    fun countZones(criteria: ZoneCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Zones by criteria: {}", criteria)
        return ResponseEntity.ok().body(zoneQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /zones/:id` : get the "id" zone.
     *
     * @param id the id of the zoneValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the zoneValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/zones/{id}")
    fun getZone(@PathVariable id: Long): ResponseEntity<ZoneValueObject> {
        log.debug("REST request to get Zone : {}", id)
        val zoneValueObject = zoneService.findOne(id)
        return ResponseUtil.wrapOrNotFound(zoneValueObject)
    }

    /**
     * `DELETE  /zones/:id` : delete the "id" zone.
     *
     * @param id the id of the zoneValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/zones/{id}")
    fun deleteZone(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Zone : {}", id)
        zoneService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "zone"
    }
}
