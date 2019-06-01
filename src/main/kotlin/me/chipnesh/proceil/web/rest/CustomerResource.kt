package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.CustomerService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.CustomerValueObject
import me.chipnesh.proceil.service.dto.CustomerCriteria
import me.chipnesh.proceil.service.CustomerQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.CustomerModel].
 */
@RestController
@RequestMapping("/api")
class CustomerResource(
    val customerService: CustomerService,
    val customerQueryService: CustomerQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /customers` : Create a new customer.
     *
     * @param customerValueObject the customerValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new customerValueObject, or with status `400 (Bad Request)` if the customer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/customers")
    fun createCustomer(@Valid @RequestBody customerValueObject: CustomerValueObject): ResponseEntity<CustomerValueObject> {
        log.debug("REST request to save Customer : {}", customerValueObject)
        if (customerValueObject.id != null) {
            throw BadRequestAlertException("A new customer cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = customerService.save(customerValueObject)
        return ResponseEntity.created(URI("/api/customers/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /customers` : Updates an existing customer.
     *
     * @param customerValueObject the customerValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated customerValueObject,
     * or with status `400 (Bad Request)` if the customerValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the customerValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/customers")
    fun updateCustomer(@Valid @RequestBody customerValueObject: CustomerValueObject): ResponseEntity<CustomerValueObject> {
        log.debug("REST request to update Customer : {}", customerValueObject)
        if (customerValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = customerService.save(customerValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customerValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /customers` : get all the customers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of customers in body.
     */
    @GetMapping("/customers")
    fun getAllCustomers(criteria: CustomerCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<CustomerValueObject>> {
        log.debug("REST request to get Customers by criteria: {}", criteria)
        val page = customerQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /customers/count}` : count all the customers.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/customers/count")
    fun countCustomers(criteria: CustomerCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Customers by criteria: {}", criteria)
        return ResponseEntity.ok().body(customerQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /customers/:id` : get the "id" customer.
     *
     * @param id the id of the customerValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the customerValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/customers/{id}")
    fun getCustomer(@PathVariable id: Long): ResponseEntity<CustomerValueObject> {
        log.debug("REST request to get Customer : {}", id)
        val customerValueObject = customerService.findOne(id)
        return ResponseUtil.wrapOrNotFound(customerValueObject)
    }

    /**
     * `DELETE  /customers/:id` : delete the "id" customer.
     *
     * @param id the id of the customerValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/customers/{id}")
    fun deleteCustomer(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Customer : {}", id)
        customerService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "customer"
    }
}
