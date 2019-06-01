package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.OrderServiceService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.OrderServiceValueObject
import me.chipnesh.proceil.service.dto.OrderServiceCriteria
import me.chipnesh.proceil.service.OrderServiceQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.OrderServiceModel].
 */
@RestController
@RequestMapping("/api")
class OrderServiceResource(
    val orderServiceService: OrderServiceService,
    val orderServiceQueryService: OrderServiceQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /order-services` : Create a new orderService.
     *
     * @param orderServiceValueObject the orderServiceValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new orderServiceValueObject, or with status `400 (Bad Request)` if the orderService has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order-services")
    fun createOrderService(@RequestBody orderServiceValueObject: OrderServiceValueObject): ResponseEntity<OrderServiceValueObject> {
        log.debug("REST request to save OrderService : {}", orderServiceValueObject)
        if (orderServiceValueObject.id != null) {
            throw BadRequestAlertException("A new orderService cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = orderServiceService.save(orderServiceValueObject)
        return ResponseEntity.created(URI("/api/order-services/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /order-services` : Updates an existing orderService.
     *
     * @param orderServiceValueObject the orderServiceValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated orderServiceValueObject,
     * or with status `400 (Bad Request)` if the orderServiceValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the orderServiceValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/order-services")
    fun updateOrderService(@RequestBody orderServiceValueObject: OrderServiceValueObject): ResponseEntity<OrderServiceValueObject> {
        log.debug("REST request to update OrderService : {}", orderServiceValueObject)
        if (orderServiceValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = orderServiceService.save(orderServiceValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderServiceValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /order-services` : get all the orderServices.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of orderServices in body.
     */
    @GetMapping("/order-services")
    fun getAllOrderServices(criteria: OrderServiceCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<OrderServiceValueObject>> {
        log.debug("REST request to get OrderServices by criteria: {}", criteria)
        val page = orderServiceQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /order-services/count}` : count all the orderServices.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/order-services/count")
    fun countOrderServices(criteria: OrderServiceCriteria): ResponseEntity<Long> {
        log.debug("REST request to count OrderServices by criteria: {}", criteria)
        return ResponseEntity.ok().body(orderServiceQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /order-services/:id` : get the "id" orderService.
     *
     * @param id the id of the orderServiceValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the orderServiceValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/order-services/{id}")
    fun getOrderService(@PathVariable id: Long): ResponseEntity<OrderServiceValueObject> {
        log.debug("REST request to get OrderService : {}", id)
        val orderServiceValueObject = orderServiceService.findOne(id)
        return ResponseUtil.wrapOrNotFound(orderServiceValueObject)
    }

    /**
     * `DELETE  /order-services/:id` : delete the "id" orderService.
     *
     * @param id the id of the orderServiceValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/order-services/{id}")
    fun deleteOrderService(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete OrderService : {}", id)
        orderServiceService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "orderService"
    }
}
