package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.service.EmployeeService
import me.chipnesh.proceil.web.rest.errors.BadRequestAlertException
import me.chipnesh.proceil.service.dto.EmployeeValueObject
import me.chipnesh.proceil.service.dto.EmployeeCriteria
import me.chipnesh.proceil.service.EmployeeQueryService

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
 * REST controller for managing [me.chipnesh.proceil.domain.EmployeeModel].
 */
@RestController
@RequestMapping("/api")
class EmployeeResource(
    val employeeService: EmployeeService,
    val employeeQueryService: EmployeeQueryService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /employees` : Create a new employee.
     *
     * @param employeeValueObject the employeeValueObject to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new employeeValueObject, or with status `400 (Bad Request)` if the employee has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/employees")
    fun createEmployee(@Valid @RequestBody employeeValueObject: EmployeeValueObject): ResponseEntity<EmployeeValueObject> {
        log.debug("REST request to save Employee : {}", employeeValueObject)
        if (employeeValueObject.id != null) {
            throw BadRequestAlertException("A new employee cannot already have an ID", ENTITY_NAME, "idexists")
        }
        val result = employeeService.save(employeeValueObject)
        return ResponseEntity.created(URI("/api/employees/" + result.id))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /employees` : Updates an existing employee.
     *
     * @param employeeValueObject the employeeValueObject to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated employeeValueObject,
     * or with status `400 (Bad Request)` if the employeeValueObject is not valid,
     * or with status `500 (Internal Server Error)` if the employeeValueObject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/employees")
    fun updateEmployee(@Valid @RequestBody employeeValueObject: EmployeeValueObject): ResponseEntity<EmployeeValueObject> {
        log.debug("REST request to update Employee : {}", employeeValueObject)
        if (employeeValueObject.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = employeeService.save(employeeValueObject)
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, employeeValueObject.id.toString()))
            .body(result)
    }

    /**
     * `GET  /employees` : get all the employees.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of employees in body.
     */
    @GetMapping("/employees")
    fun getAllEmployees(criteria: EmployeeCriteria, pageable: Pageable, @RequestParam queryParams: MultiValueMap<String, String>, uriBuilder: UriComponentsBuilder): ResponseEntity<MutableList<EmployeeValueObject>> {
        log.debug("REST request to get Employees by criteria: {}", criteria)
        val page = employeeQueryService.findByCriteria(criteria, pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
    * `GET  /employees/count}` : count all the employees.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
    */
    @GetMapping("/employees/count")
    fun countEmployees(criteria: EmployeeCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Employees by criteria: {}", criteria)
        return ResponseEntity.ok().body(employeeQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /employees/:id` : get the "id" employee.
     *
     * @param id the id of the employeeValueObject to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the employeeValueObject, or with status `404 (Not Found)`.
     */
    @GetMapping("/employees/{id}")
    fun getEmployee(@PathVariable id: Long): ResponseEntity<EmployeeValueObject> {
        log.debug("REST request to get Employee : {}", id)
        val employeeValueObject = employeeService.findOne(id)
        return ResponseUtil.wrapOrNotFound(employeeValueObject)
    }

    /**
     * `DELETE  /employees/:id` : delete the "id" employee.
     *
     * @param id the id of the employeeValueObject to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/employees/{id}")
    fun deleteEmployee(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Employee : {}", id)
        employeeService.delete(id)
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    companion object {
        private const val ENTITY_NAME = "employee"
    }
}
