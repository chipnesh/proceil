package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.OrderMaterialService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.OrderMaterialValueObject
import me.chipnesh.proceil.service.dto.OrderMaterialCriteria
import me.chipnesh.proceil.service.OrderMaterialQueryService

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

import javax.validation.Valid
import java.net.URI
import java.net.URISyntaxException

/**
 * REST controller for managing [me.chipnesh.proceil.domain.OrderMaterialModel].
 */
@RestController
@RequestMapping("/api")
class OrderMaterialResource(
    val orderMaterialService: OrderMaterialService,
    val orderMaterialQueryService: OrderMaterialQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /order-materials` : Create a new orderMaterial.
     *
     * @param orderMaterialValueObject the orderMaterialValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new orderMaterialValueObject, or with status `400 (Bad Request)` if the orderMaterial has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order-materials")
    fun createOrderMaterial(@Valid @RequestBody orderMaterialValueObject: OrderMaterialValueObject): ResponseEntity<OrderMaterialValueObject> {
        log.debug("REST request to save OrderMaterial : {}", orderMaterialValueObject)
        if (orderMaterialValueObject.id != null) {
            throw BadRequestAlertException("A new orderMaterial cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = orderMaterialService.save(orderMaterialValueObject)
        return ResponseEntity.created(URI("/api/order-materials/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /order-materials` : Updates an existing orderMaterial.
     *
     * @param orderMaterialValueObject the orderMaterialValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated orderMaterialValueObject,
     * or with status `400 (Bad Request)` if the orderMaterialValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the orderMaterialValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/order-materials")
    fun updateOrderMaterial(@Valid @RequestBody orderMaterialValueObject: OrderMaterialValueObject): ResponseEntity<OrderMaterialValueObject> {
        log.debug("REST request to update OrderMaterial : {}", orderMaterialValueObject)
        if (orderMaterialValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = orderMaterialService.save(orderMaterialValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderMaterialValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /order-materials` : get all the orderMaterials.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of orderMaterials in body.
     */
    @GetMapping("/order-materials")
    fun getAllOrderMaterials(criteria: OrderMaterialCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<OrderMaterialValueObject>> {
        log.debug("REST request to get OrderMaterials by criteria: {}", criteria)
        val page = orderMaterialQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /order-materials/count}` : count all the orderMaterials.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/order-materials/count")
    fun countOrderMaterials(criteria: OrderMaterialCriteria): ResponseEntity<Long> {
        log.debug("REST request to count OrderMaterials by criteria: {}", criteria)
        return ResponseEntity.ok().body(orderMaterialQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /order-materials/:id` : get the "id" orderMaterial.
     *
     * @param id the id of the orderMaterialValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the orderMaterialValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/order-materials/{id}")
    fun getOrderMaterial(@PathVariable id: Long): ResponseEntity<OrderMaterialValueObject> {
        log.debug("REST request to get OrderMaterial : {}", id)
        val orderMaterialValueObject = orderMaterialService.findOne(id)
        return ResponseUtil.wrapOrNotFound(orderMaterialValueObject)
    }

    /**
     * `DELETE  /order-materials/:id` : delete the "id" orderMaterial.
     *
     * @param id the id of the orderMaterialValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/order-materials/{id}")
    fun deleteOrderMaterial(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete OrderMaterial : {}", id)
        orderMaterialService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "orderMaterial"
    }
}
