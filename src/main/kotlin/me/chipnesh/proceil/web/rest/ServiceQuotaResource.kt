package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.ServiceQuotaService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.ServiceQuotaValueObject
import me.chipnesh.proceil.service.dto.ServiceQuotaCriteria
import me.chipnesh.proceil.service.ServiceQuotaQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.ServiceQuotaModel].
 */
@RestController
@RequestMapping("/api")
class ServiceQuotaResource(
    val serviceQuotaService: ServiceQuotaService,
    val serviceQuotaQueryService: ServiceQuotaQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /service-quotas` : Create a new serviceQuota.
     *
     * @param serviceQuotaValueObject the serviceQuotaValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new serviceQuotaValueObject, or with status `400 (Bad Request)` if the serviceQuota has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/service-quotas")
    fun createServiceQuota(@RequestBody serviceQuotaValueObject: ServiceQuotaValueObject): ResponseEntity<ServiceQuotaValueObject> {
        log.debug("REST request to save ServiceQuota : {}", serviceQuotaValueObject)
        if (serviceQuotaValueObject.id != null) {
            throw BadRequestAlertException("A new serviceQuota cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = serviceQuotaService.save(serviceQuotaValueObject)
        return ResponseEntity.created(URI("/api/service-quotas/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /service-quotas` : Updates an existing serviceQuota.
     *
     * @param serviceQuotaValueObject the serviceQuotaValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated serviceQuotaValueObject,
     * or with status `400 (Bad Request)` if the serviceQuotaValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the serviceQuotaValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/service-quotas")
    fun updateServiceQuota(@RequestBody serviceQuotaValueObject: ServiceQuotaValueObject): ResponseEntity<ServiceQuotaValueObject> {
        log.debug("REST request to update ServiceQuota : {}", serviceQuotaValueObject)
        if (serviceQuotaValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = serviceQuotaService.save(serviceQuotaValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, serviceQuotaValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /service-quotas` : get all the serviceQuotas.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of serviceQuotas in body.
     */
    @GetMapping("/service-quotas")
    fun getAllServiceQuotas(criteria: ServiceQuotaCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<ServiceQuotaValueObject>> {
        log.debug("REST request to get ServiceQuotas by criteria: {}", criteria)
        val page = serviceQuotaQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /service-quotas/count}` : count all the serviceQuotas.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/service-quotas/count")
    fun countServiceQuotas(criteria: ServiceQuotaCriteria): ResponseEntity<Long> {
        log.debug("REST request to count ServiceQuotas by criteria: {}", criteria)
        return ResponseEntity.ok().body(serviceQuotaQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /service-quotas/:id` : get the "id" serviceQuota.
     *
     * @param id the id of the serviceQuotaValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the serviceQuotaValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/service-quotas/{id}")
    fun getServiceQuota(@PathVariable id: Long): ResponseEntity<ServiceQuotaValueObject> {
        log.debug("REST request to get ServiceQuota : {}", id)
        val serviceQuotaValueObject = serviceQuotaService.findOne(id)
        return ResponseUtil.wrapOrNotFound(serviceQuotaValueObject)
    }

    /**
     * `DELETE  /service-quotas/:id` : delete the "id" serviceQuota.
     *
     * @param id the id of the serviceQuotaValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/service-quotas/{id}")
    fun deleteServiceQuota(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete ServiceQuota : {}", id)
        serviceQuotaService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "serviceQuota"
    }
}
