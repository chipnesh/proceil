package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.MeasurementService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.MeasurementValueObject
import me.chipnesh.proceil.service.dto.MeasurementCriteria
import me.chipnesh.proceil.service.MeasurementQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.MeasurementModel].
 */
@RestController
@RequestMapping("/api")
class MeasurementResource(
    val measurementService: MeasurementService,
    val measurementQueryService: MeasurementQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /measurements` : Create a new measurement.
     *
     * @param measurementValueObject the measurementValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new measurementValueObject, or with status `400 (Bad Request)` if the measurement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/measurements")
    fun createMeasurement(@RequestBody measurementValueObject: MeasurementValueObject): ResponseEntity<MeasurementValueObject> {
        log.debug("REST request to save Measurement : {}", measurementValueObject)
        if (measurementValueObject.id != null) {
            throw BadRequestAlertException("A new measurement cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = measurementService.save(measurementValueObject)
        return ResponseEntity.created(URI("/api/measurements/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /measurements` : Updates an existing measurement.
     *
     * @param measurementValueObject the measurementValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated measurementValueObject,
     * or with status `400 (Bad Request)` if the measurementValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the measurementValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/measurements")
    fun updateMeasurement(@RequestBody measurementValueObject: MeasurementValueObject): ResponseEntity<MeasurementValueObject> {
        log.debug("REST request to update Measurement : {}", measurementValueObject)
        if (measurementValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = measurementService.save(measurementValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, measurementValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /measurements` : get all the measurements.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of measurements in body.
     */
    @GetMapping("/measurements")
    fun getAllMeasurements(criteria: MeasurementCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<MeasurementValueObject>> {
        log.debug("REST request to get Measurements by criteria: {}", criteria)
        val page = measurementQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /measurements/count}` : count all the measurements.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/measurements/count")
    fun countMeasurements(criteria: MeasurementCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Measurements by criteria: {}", criteria)
        return ResponseEntity.ok().body(measurementQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /measurements/:id` : get the "id" measurement.
     *
     * @param id the id of the measurementValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the measurementValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/measurements/{id}")
    fun getMeasurement(@PathVariable id: Long): ResponseEntity<MeasurementValueObject> {
        log.debug("REST request to get Measurement : {}", id)
        val measurementValueObject = measurementService.findOne(id)
        return ResponseUtil.wrapOrNotFound(measurementValueObject)
    }

    /**
     * `DELETE  /measurements/:id` : delete the "id" measurement.
     *
     * @param id the id of the measurementValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/measurements/{id}")
    fun deleteMeasurement(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Measurement : {}", id)
        measurementService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "measurement"
    }
}
