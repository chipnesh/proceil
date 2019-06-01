package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.MaterialReserveService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.MaterialReserveValueObject
import me.chipnesh.proceil.service.dto.MaterialReserveCriteria
import me.chipnesh.proceil.service.MaterialReserveQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.MaterialReserveModel].
 */
@RestController
@RequestMapping("/api")
class MaterialReserveResource(
    val materialReserveService: MaterialReserveService,
    val materialReserveQueryService: MaterialReserveQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /material-reserves` : Create a new materialReserve.
     *
     * @param materialReserveValueObject the materialReserveValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new materialReserveValueObject, or with status `400 (Bad Request)` if the materialReserve has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/material-reserves")
    fun createMaterialReserve(@RequestBody materialReserveValueObject: MaterialReserveValueObject): ResponseEntity<MaterialReserveValueObject> {
        log.debug("REST request to save MaterialReserve : {}", materialReserveValueObject)
        if (materialReserveValueObject.id != null) {
            throw BadRequestAlertException("A new materialReserve cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = materialReserveService.save(materialReserveValueObject)
        return ResponseEntity.created(URI("/api/material-reserves/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /material-reserves` : Updates an existing materialReserve.
     *
     * @param materialReserveValueObject the materialReserveValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated materialReserveValueObject,
     * or with status `400 (Bad Request)` if the materialReserveValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the materialReserveValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/material-reserves")
    fun updateMaterialReserve(@RequestBody materialReserveValueObject: MaterialReserveValueObject): ResponseEntity<MaterialReserveValueObject> {
        log.debug("REST request to update MaterialReserve : {}", materialReserveValueObject)
        if (materialReserveValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = materialReserveService.save(materialReserveValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, materialReserveValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /material-reserves` : get all the materialReserves.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of materialReserves in body.
     */
    @GetMapping("/material-reserves")
    fun getAllMaterialReserves(criteria: MaterialReserveCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<MaterialReserveValueObject>> {
        log.debug("REST request to get MaterialReserves by criteria: {}", criteria)
        val page = materialReserveQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /material-reserves/count}` : count all the materialReserves.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/material-reserves/count")
    fun countMaterialReserves(criteria: MaterialReserveCriteria): ResponseEntity<Long> {
        log.debug("REST request to count MaterialReserves by criteria: {}", criteria)
        return ResponseEntity.ok().body(materialReserveQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /material-reserves/:id` : get the "id" materialReserve.
     *
     * @param id the id of the materialReserveValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the materialReserveValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/material-reserves/{id}")
    fun getMaterialReserve(@PathVariable id: Long): ResponseEntity<MaterialReserveValueObject> {
        log.debug("REST request to get MaterialReserve : {}", id)
        val materialReserveValueObject = materialReserveService.findOne(id)
        return ResponseUtil.wrapOrNotFound(materialReserveValueObject)
    }

    /**
     * `DELETE  /material-reserves/:id` : delete the "id" materialReserve.
     *
     * @param id the id of the materialReserveValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/material-reserves/{id}")
    fun deleteMaterialReserve(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete MaterialReserve : {}", id)
        materialReserveService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "materialReserve"
    }
}
