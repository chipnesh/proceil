package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.MaterialMeasurementService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.MaterialMeasurementValueObject
import me.chipnesh.proceil.service.dto.MaterialMeasurementCriteria
import me.chipnesh.proceil.service.MaterialMeasurementQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.MaterialMeasurementModel].
 */
@RestController
@RequestMapping("/api")
class MaterialMeasurementResource(
    val materialMeasurementService: MaterialMeasurementService,
    val materialMeasurementQueryService: MaterialMeasurementQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /material-measurements` : Create a new materialMeasurement.
     *
     * @param materialMeasurementValueObject the materialMeasurementValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new materialMeasurementValueObject, or with status `400 (Bad Request)` if the materialMeasurement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/material-measurements")
    fun createMaterialMeasurement(@RequestBody materialMeasurementValueObject: MaterialMeasurementValueObject): ResponseEntity<MaterialMeasurementValueObject> {
        log.debug("REST request to save MaterialMeasurement : {}", materialMeasurementValueObject)
        if (materialMeasurementValueObject.id != null) {
            throw BadRequestAlertException("A new materialMeasurement cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = materialMeasurementService.save(materialMeasurementValueObject)
        return ResponseEntity.created(URI("/api/material-measurements/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /material-measurements` : Updates an existing materialMeasurement.
     *
     * @param materialMeasurementValueObject the materialMeasurementValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated materialMeasurementValueObject,
     * or with status `400 (Bad Request)` if the materialMeasurementValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the materialMeasurementValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/material-measurements")
    fun updateMaterialMeasurement(@RequestBody materialMeasurementValueObject: MaterialMeasurementValueObject): ResponseEntity<MaterialMeasurementValueObject> {
        log.debug("REST request to update MaterialMeasurement : {}", materialMeasurementValueObject)
        if (materialMeasurementValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = materialMeasurementService.save(materialMeasurementValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, materialMeasurementValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /material-measurements` : get all the materialMeasurements.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of materialMeasurements in body.
     */
    @GetMapping("/material-measurements")
    fun getAllMaterialMeasurements(criteria: MaterialMeasurementCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<MaterialMeasurementValueObject>> {
        log.debug("REST request to get MaterialMeasurements by criteria: {}", criteria)
        val page = materialMeasurementQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /material-measurements/count}` : count all the materialMeasurements.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/material-measurements/count")
    fun countMaterialMeasurements(criteria: MaterialMeasurementCriteria): ResponseEntity<Long> {
        log.debug("REST request to count MaterialMeasurements by criteria: {}", criteria)
        return ResponseEntity.ok().body(materialMeasurementQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /material-measurements/:id` : get the "id" materialMeasurement.
     *
     * @param id the id of the materialMeasurementValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the materialMeasurementValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/material-measurements/{id}")
    fun getMaterialMeasurement(@PathVariable id: Long): ResponseEntity<MaterialMeasurementValueObject> {
        log.debug("REST request to get MaterialMeasurement : {}", id)
        val materialMeasurementValueObject = materialMeasurementService.findOne(id)
        return ResponseUtil.wrapOrNotFound(materialMeasurementValueObject)
    }

    /**
     * `DELETE  /material-measurements/:id` : delete the "id" materialMeasurement.
     *
     * @param id the id of the materialMeasurementValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/material-measurements/{id}")
    fun deleteMaterialMeasurement(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete MaterialMeasurement : {}", id)
        materialMeasurementService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "materialMeasurement"
    }
}
