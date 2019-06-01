package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.EmployeeModel
import me.chipnesh.proceil.repository.EmployeeRepository
import me.chipnesh.proceil.service.EmployeeService
import me.chipnesh.proceil.service.dto.EmployeeValueObject
import me.chipnesh.proceil.service.mapper.EmployeeMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.EmployeeQueryService

import kotlin.test.assertNotNull

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import javax.persistence.EntityManager

import me.chipnesh.proceil.web.rest.TestUtil.createFormattingConversionService
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test class for the EmployeeResource REST controller.
 *
 * @see EmployeeResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class EmployeeResourceIT {

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    @Autowired
    private lateinit var employeeMapper: EmployeeMapper

    @Autowired
    private lateinit var employeeService: EmployeeService

    @Autowired
    private lateinit var employeeQueryService: EmployeeQueryService

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var validator: Validator

    private lateinit var restEmployeeMockMvc: MockMvc

    private lateinit var employeeModel: EmployeeModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val employeeResource = EmployeeResource(employeeService, employeeQueryService)
        this.restEmployeeMockMvc = MockMvcBuilders.standaloneSetup(employeeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        employeeModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createEmployee() {
        val databaseSizeBeforeCreate = employeeRepository.findAll().size

        // Create the Employee
        val employeeValueObject = employeeMapper.toDto(employeeModel)
        restEmployeeMockMvc.perform(
            post("/api/employees")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(employeeValueObject))
        ).andExpect(status().isCreated)

        // Validate the Employee in the database
        val employeeList = employeeRepository.findAll()
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate + 1)
        val testEmployee = employeeList[employeeList.size - 1]
        assertThat(testEmployee.employeeName).isEqualTo(DEFAULT_EMPLOYEE_NAME)
        assertThat(testEmployee.phone).isEqualTo(DEFAULT_PHONE)
    }

    @Test
    @Transactional
    fun createEmployeeWithExistingId() {
        val databaseSizeBeforeCreate = employeeRepository.findAll().size

        // Create the Employee with an existing ID
        employeeModel.id = 1L
        val employeeValueObject = employeeMapper.toDto(employeeModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmployeeMockMvc.perform(
            post("/api/employees")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(employeeValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Employee in the database
        val employeeList = employeeRepository.findAll()
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllEmployees() {
        // Initialize the database
        employeeRepository.saveAndFlush(employeeModel)

        // Get all the employeeList
        restEmployeeMockMvc.perform(get("/api/employees?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employeeModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].employeeName").value(hasItem(DEFAULT_EMPLOYEE_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
    }

    @Test
    @Transactional
    fun getEmployee() {
        // Initialize the database
        employeeRepository.saveAndFlush(employeeModel)

        val id = employeeModel.id
        assertNotNull(id)

        // Get the employee
        restEmployeeMockMvc.perform(get("/api/employees/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.employeeName").value(DEFAULT_EMPLOYEE_NAME))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
    }

    @Test
    @Transactional
    fun getAllEmployeesByEmployeeNameIsEqualToSomething() {
        // Initialize the database
        employeeRepository.saveAndFlush(employeeModel)

        // Get all the employeeList where employeeName equals to DEFAULT_EMPLOYEE_NAME
        defaultEmployeeShouldBeFound("employeeName.equals=$DEFAULT_EMPLOYEE_NAME")

        // Get all the employeeList where employeeName equals to UPDATED_EMPLOYEE_NAME
        defaultEmployeeShouldNotBeFound("employeeName.equals=$UPDATED_EMPLOYEE_NAME")
    }

    @Test
    @Transactional
    fun getAllEmployeesByEmployeeNameIsInShouldWork() {
        // Initialize the database
        employeeRepository.saveAndFlush(employeeModel)

        // Get all the employeeList where employeeName in DEFAULT_EMPLOYEE_NAME or UPDATED_EMPLOYEE_NAME
        defaultEmployeeShouldBeFound("employeeName.in=$DEFAULT_EMPLOYEE_NAME,$UPDATED_EMPLOYEE_NAME")

        // Get all the employeeList where employeeName equals to UPDATED_EMPLOYEE_NAME
        defaultEmployeeShouldNotBeFound("employeeName.in=$UPDATED_EMPLOYEE_NAME")
    }

    @Test
    @Transactional
    fun getAllEmployeesByEmployeeNameIsNullOrNotNull() {
        // Initialize the database
        employeeRepository.saveAndFlush(employeeModel)

        // Get all the employeeList where employeeName is not null
        defaultEmployeeShouldBeFound("employeeName.specified=true")

        // Get all the employeeList where employeeName is null
        defaultEmployeeShouldNotBeFound("employeeName.specified=false")
    }

    @Test
    @Transactional
    fun getAllEmployeesByPhoneIsEqualToSomething() {
        // Initialize the database
        employeeRepository.saveAndFlush(employeeModel)

        // Get all the employeeList where phone equals to DEFAULT_PHONE
        defaultEmployeeShouldBeFound("phone.equals=$DEFAULT_PHONE")

        // Get all the employeeList where phone equals to UPDATED_PHONE
        defaultEmployeeShouldNotBeFound("phone.equals=$UPDATED_PHONE")
    }

    @Test
    @Transactional
    fun getAllEmployeesByPhoneIsInShouldWork() {
        // Initialize the database
        employeeRepository.saveAndFlush(employeeModel)

        // Get all the employeeList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultEmployeeShouldBeFound("phone.in=$DEFAULT_PHONE,$UPDATED_PHONE")

        // Get all the employeeList where phone equals to UPDATED_PHONE
        defaultEmployeeShouldNotBeFound("phone.in=$UPDATED_PHONE")
    }

    @Test
    @Transactional
    fun getAllEmployeesByPhoneIsNullOrNotNull() {
        // Initialize the database
        employeeRepository.saveAndFlush(employeeModel)

        // Get all the employeeList where phone is not null
        defaultEmployeeShouldBeFound("phone.specified=true")

        // Get all the employeeList where phone is null
        defaultEmployeeShouldNotBeFound("phone.specified=false")
    }

    @Test
    @Transactional
    fun getAllEmployeesByMeasurementIsEqualToSomething() {
        // Initialize the database
        val measurement = MeasurementResourceIT.createEntity(em)
        em.persist(measurement)
        em.flush()
        employeeModel.addMeasurement(measurement)
        employeeRepository.saveAndFlush(employeeModel)
        val measurementId = measurement.id

        // Get all the employeeList where measurement equals to measurementId
        defaultEmployeeShouldBeFound("measurementId.equals=$measurementId")

        // Get all the employeeList where measurement equals to measurementId + 1
        defaultEmployeeShouldNotBeFound("measurementId.equals=${measurementId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultEmployeeShouldBeFound(filter: String) {
        restEmployeeMockMvc.perform(get("/api/employees?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employeeModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].employeeName").value(hasItem(DEFAULT_EMPLOYEE_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))

        // Check, that the count call also returns 1
        restEmployeeMockMvc.perform(get("/api/employees/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultEmployeeShouldNotBeFound(filter: String) {
        restEmployeeMockMvc.perform(get("/api/employees?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restEmployeeMockMvc.perform(get("/api/employees/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingEmployee() {
        // Get the employee
        restEmployeeMockMvc.perform(get("/api/employees/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateEmployee() {
        // Initialize the database
        employeeRepository.saveAndFlush(employeeModel)

        val databaseSizeBeforeUpdate = employeeRepository.findAll().size

        // Update the employee
        val id = employeeModel.id
        assertNotNull(id)
        val updatedEmployee = employeeRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedEmployee are not directly saved in db
        em.detach(updatedEmployee)
        updatedEmployee.employeeName = UPDATED_EMPLOYEE_NAME
        updatedEmployee.phone = UPDATED_PHONE
        val employeeValueObject = employeeMapper.toDto(updatedEmployee)

        restEmployeeMockMvc.perform(
            put("/api/employees")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(employeeValueObject))
        ).andExpect(status().isOk)

        // Validate the Employee in the database
        val employeeList = employeeRepository.findAll()
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate)
        val testEmployee = employeeList[employeeList.size - 1]
        assertThat(testEmployee.employeeName).isEqualTo(UPDATED_EMPLOYEE_NAME)
        assertThat(testEmployee.phone).isEqualTo(UPDATED_PHONE)
    }

    @Test
    @Transactional
    fun updateNonExistingEmployee() {
        val databaseSizeBeforeUpdate = employeeRepository.findAll().size

        // Create the Employee
        val employeeValueObject = employeeMapper.toDto(employeeModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeMockMvc.perform(
            put("/api/employees")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(employeeValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Employee in the database
        val employeeList = employeeRepository.findAll()
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteEmployee() {
        // Initialize the database
        employeeRepository.saveAndFlush(employeeModel)

        val databaseSizeBeforeDelete = employeeRepository.findAll().size

        val id = employeeModel.id
        assertNotNull(id)

        // Delete the employee
        restEmployeeMockMvc.perform(
            delete("/api/employees/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val employeeList = employeeRepository.findAll()
        assertThat(employeeList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(EmployeeModel::class.java)
        val employeeModel1 = EmployeeModel()
        employeeModel1.id = 1L
        val employeeModel2 = EmployeeModel()
        employeeModel2.id = employeeModel1.id
        assertThat(employeeModel1).isEqualTo(employeeModel2)
        employeeModel2.id = 2L
        assertThat(employeeModel1).isNotEqualTo(employeeModel2)
        employeeModel1.id = null
        assertThat(employeeModel1).isNotEqualTo(employeeModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(EmployeeValueObject::class.java)
        val employeeValueObject1 = EmployeeValueObject()
        employeeValueObject1.id = 1L
        val employeeValueObject2 = EmployeeValueObject()
        assertThat(employeeValueObject1).isNotEqualTo(employeeValueObject2)
        employeeValueObject2.id = employeeValueObject1.id
        assertThat(employeeValueObject1).isEqualTo(employeeValueObject2)
        employeeValueObject2.id = 2L
        assertThat(employeeValueObject1).isNotEqualTo(employeeValueObject2)
        employeeValueObject1.id = null
        assertThat(employeeValueObject1).isNotEqualTo(employeeValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(employeeMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(employeeMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_EMPLOYEE_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_EMPLOYEE_NAME = "BBBBBBBBBB"

        private const val DEFAULT_PHONE: String = "AAAAAAAAAA"
        private const val UPDATED_PHONE = "BBBBBBBBBB"
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): EmployeeModel {
            val employeeModel = EmployeeModel()
            employeeModel.employeeName = DEFAULT_EMPLOYEE_NAME
            employeeModel.phone = DEFAULT_PHONE

        return employeeModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): EmployeeModel {
            val employeeModel = EmployeeModel()
            employeeModel.employeeName = UPDATED_EMPLOYEE_NAME
            employeeModel.phone = UPDATED_PHONE

        return employeeModel
        }
    }
}
