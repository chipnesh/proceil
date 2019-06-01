package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.MaterialService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.MaterialValueObject
import me.chipnesh.proceil.service.dto.MaterialCriteria
import me.chipnesh.proceil.service.MaterialQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.MaterialModel].
 */
@RestController
@RequestMapping("/api")
class MaterialResource(
    val materialService: MaterialService,
    val materialQueryService: MaterialQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /materials` : Create a new material.
     *
     * @param materialValueObject the materialValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new materialValueObject, or with status `400 (Bad Request)` if the material has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/materials")
    fun createMaterial(@RequestBody materialValueObject: MaterialValueObject): ResponseEntity<MaterialValueObject> {
        log.debug("REST request to save Material : {}", materialValueObject)
        if (materialValueObject.id != null) {
            throw BadRequestAlertException("A new material cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = materialService.save(materialValueObject)
        return ResponseEntity.created(URI("/api/materials/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /materials` : Updates an existing material.
     *
     * @param materialValueObject the materialValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated materialValueObject,
     * or with status `400 (Bad Request)` if the materialValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the materialValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/materials")
    fun updateMaterial(@RequestBody materialValueObject: MaterialValueObject): ResponseEntity<MaterialValueObject> {
        log.debug("REST request to update Material : {}", materialValueObject)
        if (materialValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = materialService.save(materialValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, materialValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /materials` : get all the materials.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of materials in body.
     */
    @GetMapping("/materials")
    fun getAllMaterials(criteria: MaterialCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<MaterialValueObject>> {
        log.debug("REST request to get Materials by criteria: {}", criteria)
        val page = materialQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /materials/count}` : count all the materials.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/materials/count")
    fun countMaterials(criteria: MaterialCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Materials by criteria: {}", criteria)
        return ResponseEntity.ok().body(materialQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /materials/:id` : get the "id" material.
     *
     * @param id the id of the materialValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the materialValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/materials/{id}")
    fun getMaterial(@PathVariable id: Long): ResponseEntity<MaterialValueObject> {
        log.debug("REST request to get Material : {}", id)
        val materialValueObject = materialService.findOne(id)
        return ResponseUtil.wrapOrNotFound(materialValueObject)
    }

    /**
     * `DELETE  /materials/:id` : delete the "id" material.
     *
     * @param id the id of the materialValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/materials/{id}")
    fun deleteMaterial(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Material : {}", id)
        materialService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "material"
    }
}
