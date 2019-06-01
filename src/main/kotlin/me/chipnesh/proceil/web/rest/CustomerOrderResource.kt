package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.CustomerOrderService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.CustomerOrderValueObject
import me.chipnesh.proceil.service.dto.CustomerOrderCriteria
import me.chipnesh.proceil.service.CustomerOrderQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.CustomerOrderModel].
 */
@RestController
@RequestMapping("/api")
class CustomerOrderResource(
    val customerOrderService: CustomerOrderService,
    val customerOrderQueryService: CustomerOrderQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /customer-orders` : Create a new customerOrder.
     *
     * @param customerOrderValueObject the customerOrderValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new customerOrderValueObject, or with status `400 (Bad Request)` if the customerOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/customer-orders")
    fun createCustomerOrder(@RequestBody customerOrderValueObject: CustomerOrderValueObject): ResponseEntity<CustomerOrderValueObject> {
        log.debug("REST request to save CustomerOrder : {}", customerOrderValueObject)
        if (customerOrderValueObject.id != null) {
            throw BadRequestAlertException("A new customerOrder cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = customerOrderService.save(customerOrderValueObject)
        return ResponseEntity.created(URI("/api/customer-orders/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /customer-orders` : Updates an existing customerOrder.
     *
     * @param customerOrderValueObject the customerOrderValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated customerOrderValueObject,
     * or with status `400 (Bad Request)` if the customerOrderValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the customerOrderValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/customer-orders")
    fun updateCustomerOrder(@RequestBody customerOrderValueObject: CustomerOrderValueObject): ResponseEntity<CustomerOrderValueObject> {
        log.debug("REST request to update CustomerOrder : {}", customerOrderValueObject)
        if (customerOrderValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = customerOrderService.save(customerOrderValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customerOrderValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /customer-orders` : get all the customerOrders.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of customerOrders in body.
     */
    @GetMapping("/customer-orders")
    fun getAllCustomerOrders(criteria: CustomerOrderCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<CustomerOrderValueObject>> {
        log.debug("REST request to get CustomerOrders by criteria: {}", criteria)
        val page = customerOrderQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /customer-orders/count}` : count all the customerOrders.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/customer-orders/count")
    fun countCustomerOrders(criteria: CustomerOrderCriteria): ResponseEntity<Long> {
        log.debug("REST request to count CustomerOrders by criteria: {}", criteria)
        return ResponseEntity.ok().body(customerOrderQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /customer-orders/:id` : get the "id" customerOrder.
     *
     * @param id the id of the customerOrderValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the customerOrderValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/customer-orders/{id}")
    fun getCustomerOrder(@PathVariable id: Long): ResponseEntity<CustomerOrderValueObject> {
        log.debug("REST request to get CustomerOrder : {}", id)
        val customerOrderValueObject = customerOrderService.findOne(id)
        return ResponseUtil.wrapOrNotFound(customerOrderValueObject)
    }

    /**
     * `DELETE  /customer-orders/:id` : delete the "id" customerOrder.
     *
     * @param id the id of the customerOrderValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/customer-orders/{id}")
    fun deleteCustomerOrder(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete CustomerOrder : {}", id)
        customerOrderService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "customerOrder"
    }
}
