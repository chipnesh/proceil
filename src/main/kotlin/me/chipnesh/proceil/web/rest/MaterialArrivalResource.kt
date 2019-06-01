package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.MaterialArrivalService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.MaterialArrivalValueObject
import me.chipnesh.proceil.service.dto.MaterialArrivalCriteria
import me.chipnesh.proceil.service.MaterialArrivalQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.MaterialArrivalModel].
 */
@RestController
@RequestMapping("/api")
class MaterialArrivalResource(
    val materialArrivalService: MaterialArrivalService,
    val materialArrivalQueryService: MaterialArrivalQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /material-arrivals` : Create a new materialArrival.
     *
     * @param materialArrivalValueObject the materialArrivalValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new materialArrivalValueObject, or with status `400 (Bad Request)` if the materialArrival has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/material-arrivals")
    fun createMaterialArrival(@RequestBody materialArrivalValueObject: MaterialArrivalValueObject): ResponseEntity<MaterialArrivalValueObject> {
        log.debug("REST request to save MaterialArrival : {}", materialArrivalValueObject)
        if (materialArrivalValueObject.id != null) {
            throw BadRequestAlertException("A new materialArrival cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = materialArrivalService.save(materialArrivalValueObject)
        return ResponseEntity.created(URI("/api/material-arrivals/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /material-arrivals` : Updates an existing materialArrival.
     *
     * @param materialArrivalValueObject the materialArrivalValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated materialArrivalValueObject,
     * or with status `400 (Bad Request)` if the materialArrivalValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the materialArrivalValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/material-arrivals")
    fun updateMaterialArrival(@RequestBody materialArrivalValueObject: MaterialArrivalValueObject): ResponseEntity<MaterialArrivalValueObject> {
        log.debug("REST request to update MaterialArrival : {}", materialArrivalValueObject)
        if (materialArrivalValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = materialArrivalService.save(materialArrivalValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, materialArrivalValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /material-arrivals` : get all the materialArrivals.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of materialArrivals in body.
     */
    @GetMapping("/material-arrivals")
    fun getAllMaterialArrivals(criteria: MaterialArrivalCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<MaterialArrivalValueObject>> {
        log.debug("REST request to get MaterialArrivals by criteria: {}", criteria)
        val page = materialArrivalQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /material-arrivals/count}` : count all the materialArrivals.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/material-arrivals/count")
    fun countMaterialArrivals(criteria: MaterialArrivalCriteria): ResponseEntity<Long> {
        log.debug("REST request to count MaterialArrivals by criteria: {}", criteria)
        return ResponseEntity.ok().body(materialArrivalQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /material-arrivals/:id` : get the "id" materialArrival.
     *
     * @param id the id of the materialArrivalValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the materialArrivalValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/material-arrivals/{id}")
    fun getMaterialArrival(@PathVariable id: Long): ResponseEntity<MaterialArrivalValueObject> {
        log.debug("REST request to get MaterialArrival : {}", id)
        val materialArrivalValueObject = materialArrivalService.findOne(id)
        return ResponseUtil.wrapOrNotFound(materialArrivalValueObject)
    }

    /**
     * `DELETE  /material-arrivals/:id` : delete the "id" materialArrival.
     *
     * @param id the id of the materialArrivalValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/material-arrivals/{id}")
    fun deleteMaterialArrival(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete MaterialArrival : {}", id)
        materialArrivalService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "materialArrival"
    }
}
