package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.ServiceService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.ServiceValueObject
import me.chipnesh.proceil.service.dto.ServiceCriteria
import me.chipnesh.proceil.service.ServiceQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.ServiceModel].
 */
@RestController
@RequestMapping("/api")
class ServiceResource(
    val serviceService: ServiceService,
    val serviceQueryService: ServiceQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /services` : Create a new service.
     *
     * @param serviceValueObject the serviceValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new serviceValueObject, or with status `400 (Bad Request)` if the service has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/services")
    fun createService(@RequestBody serviceValueObject: ServiceValueObject): ResponseEntity<ServiceValueObject> {
        log.debug("REST request to save Service : {}", serviceValueObject)
        if (serviceValueObject.id != null) {
            throw BadRequestAlertException("A new service cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = serviceService.save(serviceValueObject)
        return ResponseEntity.created(URI("/api/services/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /services` : Updates an existing service.
     *
     * @param serviceValueObject the serviceValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated serviceValueObject,
     * or with status `400 (Bad Request)` if the serviceValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the serviceValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/services")
    fun updateService(@RequestBody serviceValueObject: ServiceValueObject): ResponseEntity<ServiceValueObject> {
        log.debug("REST request to update Service : {}", serviceValueObject)
        if (serviceValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = serviceService.save(serviceValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /services` : get all the services.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of services in body.
     */
    @GetMapping("/services")
    fun getAllServices(criteria: ServiceCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<ServiceValueObject>> {
        log.debug("REST request to get Services by criteria: {}", criteria)
        val page = serviceQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /services/count}` : count all the services.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/services/count")
    fun countServices(criteria: ServiceCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Services by criteria: {}", criteria)
        return ResponseEntity.ok().body(serviceQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /services/:id` : get the "id" service.
     *
     * @param id the id of the serviceValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the serviceValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/services/{id}")
    fun getService(@PathVariable id: Long): ResponseEntity<ServiceValueObject> {
        log.debug("REST request to get Service : {}", id)
        val serviceValueObject = serviceService.findOne(id)
        return ResponseUtil.wrapOrNotFound(serviceValueObject)
    }

    /**
     * `DELETE  /services/:id` : delete the "id" service.
     *
     * @param id the id of the serviceValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/services/{id}")
    fun deleteService(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Service : {}", id)
        serviceService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "service"
    }
}
