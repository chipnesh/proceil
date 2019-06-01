package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.CustomerModel
import me.chipnesh.proceil.repository.CustomerRepository
import me.chipnesh.proceil.service.CustomerService
import me.chipnesh.proceil.service.dto.CustomerValueObject
import me.chipnesh.proceil.service.mapper.CustomerMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.CustomerQueryService

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
import java.time.Instant
import java.time.temporal.ChronoUnit

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
 * Test class for the CustomerResource REST controller.
 *
 * @see CustomerResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class CustomerResourceIT {

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var customerMapper: CustomerMapper

    @Autowired
    private lateinit var customerService: CustomerService

    @Autowired
    private lateinit var customerQueryService: CustomerQueryService

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

    private lateinit var restCustomerMockMvc: MockMvc

    private lateinit var customerModel: CustomerModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val customerResource = CustomerResource(customerService, customerQueryService)
        this.restCustomerMockMvc = MockMvcBuilders.standaloneSetup(customerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        customerModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createCustomer() {
        val databaseSizeBeforeCreate = customerRepository.findAll().size

        // Create the Customer
        val customerValueObject = customerMapper.toDto(customerModel)
        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customerValueObject))
        ).andExpect(status().isCreated)

        // Validate the Customer in the database
        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeCreate + 1)
        val testCustomer = customerList[customerList.size - 1]
        assertThat(testCustomer.customerSummary).isEqualTo(DEFAULT_CUSTOMER_SUMMARY)
        assertThat(testCustomer.firstname).isEqualTo(DEFAULT_FIRSTNAME)
        assertThat(testCustomer.lastname).isEqualTo(DEFAULT_LASTNAME)
        assertThat(testCustomer.middlename).isEqualTo(DEFAULT_MIDDLENAME)
        assertThat(testCustomer.birthDate).isEqualTo(DEFAULT_BIRTH_DATE)
        assertThat(testCustomer.email).isEqualTo(DEFAULT_EMAIL)
        assertThat(testCustomer.phone).isEqualTo(DEFAULT_PHONE)
        assertThat(testCustomer.address).isEqualTo(DEFAULT_ADDRESS)
    }

    @Test
    @Transactional
    fun createCustomerWithExistingId() {
        val databaseSizeBeforeCreate = customerRepository.findAll().size

        // Create the Customer with an existing ID
        customerModel.id = 1L
        val customerValueObject = customerMapper.toDto(customerModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customerValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Customer in the database
        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkFirstnameIsRequired() {
        val databaseSizeBeforeTest = customerRepository.findAll().size
        // set the field null
        customerModel.firstname = null

        // Create the Customer, which fails.
        val customerValueObject = customerMapper.toDto(customerModel)

        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customerValueObject))
        ).andExpect(status().isBadRequest)

        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkLastnameIsRequired() {
        val databaseSizeBeforeTest = customerRepository.findAll().size
        // set the field null
        customerModel.lastname = null

        // Create the Customer, which fails.
        val customerValueObject = customerMapper.toDto(customerModel)

        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customerValueObject))
        ).andExpect(status().isBadRequest)

        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkEmailIsRequired() {
        val databaseSizeBeforeTest = customerRepository.findAll().size
        // set the field null
        customerModel.email = null

        // Create the Customer, which fails.
        val customerValueObject = customerMapper.toDto(customerModel)

        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customerValueObject))
        ).andExpect(status().isBadRequest)

        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllCustomers() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList
        restCustomerMockMvc.perform(get("/api/customers?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customerModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].customerSummary").value(hasItem(DEFAULT_CUSTOMER_SUMMARY)))
            .andExpect(jsonPath("$.[*].firstname").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].lastname").value(hasItem(DEFAULT_LASTNAME)))
            .andExpect(jsonPath("$.[*].middlename").value(hasItem(DEFAULT_MIDDLENAME)))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
    }

    @Test
    @Transactional
    fun getCustomer() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        val id = customerModel.id
        assertNotNull(id)

        // Get the customer
        restCustomerMockMvc.perform(get("/api/customers/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.customerSummary").value(DEFAULT_CUSTOMER_SUMMARY))
            .andExpect(jsonPath("$.firstname").value(DEFAULT_FIRSTNAME))
            .andExpect(jsonPath("$.lastname").value(DEFAULT_LASTNAME))
            .andExpect(jsonPath("$.middlename").value(DEFAULT_MIDDLENAME))
            .andExpect(jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
    }

    @Test
    @Transactional
    fun getAllCustomersByCustomerSummaryIsEqualToSomething() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where customerSummary equals to DEFAULT_CUSTOMER_SUMMARY
        defaultCustomerShouldBeFound("customerSummary.equals=$DEFAULT_CUSTOMER_SUMMARY")

        // Get all the customerList where customerSummary equals to UPDATED_CUSTOMER_SUMMARY
        defaultCustomerShouldNotBeFound("customerSummary.equals=$UPDATED_CUSTOMER_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllCustomersByCustomerSummaryIsInShouldWork() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where customerSummary in DEFAULT_CUSTOMER_SUMMARY or UPDATED_CUSTOMER_SUMMARY
        defaultCustomerShouldBeFound("customerSummary.in=$DEFAULT_CUSTOMER_SUMMARY,$UPDATED_CUSTOMER_SUMMARY")

        // Get all the customerList where customerSummary equals to UPDATED_CUSTOMER_SUMMARY
        defaultCustomerShouldNotBeFound("customerSummary.in=$UPDATED_CUSTOMER_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllCustomersByCustomerSummaryIsNullOrNotNull() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where customerSummary is not null
        defaultCustomerShouldBeFound("customerSummary.specified=true")

        // Get all the customerList where customerSummary is null
        defaultCustomerShouldNotBeFound("customerSummary.specified=false")
    }

    @Test
    @Transactional
    fun getAllCustomersByFirstnameIsEqualToSomething() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where firstname equals to DEFAULT_FIRSTNAME
        defaultCustomerShouldBeFound("firstname.equals=$DEFAULT_FIRSTNAME")

        // Get all the customerList where firstname equals to UPDATED_FIRSTNAME
        defaultCustomerShouldNotBeFound("firstname.equals=$UPDATED_FIRSTNAME")
    }

    @Test
    @Transactional
    fun getAllCustomersByFirstnameIsInShouldWork() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where firstname in DEFAULT_FIRSTNAME or UPDATED_FIRSTNAME
        defaultCustomerShouldBeFound("firstname.in=$DEFAULT_FIRSTNAME,$UPDATED_FIRSTNAME")

        // Get all the customerList where firstname equals to UPDATED_FIRSTNAME
        defaultCustomerShouldNotBeFound("firstname.in=$UPDATED_FIRSTNAME")
    }

    @Test
    @Transactional
    fun getAllCustomersByFirstnameIsNullOrNotNull() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where firstname is not null
        defaultCustomerShouldBeFound("firstname.specified=true")

        // Get all the customerList where firstname is null
        defaultCustomerShouldNotBeFound("firstname.specified=false")
    }

    @Test
    @Transactional
    fun getAllCustomersByLastnameIsEqualToSomething() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where lastname equals to DEFAULT_LASTNAME
        defaultCustomerShouldBeFound("lastname.equals=$DEFAULT_LASTNAME")

        // Get all the customerList where lastname equals to UPDATED_LASTNAME
        defaultCustomerShouldNotBeFound("lastname.equals=$UPDATED_LASTNAME")
    }

    @Test
    @Transactional
    fun getAllCustomersByLastnameIsInShouldWork() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where lastname in DEFAULT_LASTNAME or UPDATED_LASTNAME
        defaultCustomerShouldBeFound("lastname.in=$DEFAULT_LASTNAME,$UPDATED_LASTNAME")

        // Get all the customerList where lastname equals to UPDATED_LASTNAME
        defaultCustomerShouldNotBeFound("lastname.in=$UPDATED_LASTNAME")
    }

    @Test
    @Transactional
    fun getAllCustomersByLastnameIsNullOrNotNull() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where lastname is not null
        defaultCustomerShouldBeFound("lastname.specified=true")

        // Get all the customerList where lastname is null
        defaultCustomerShouldNotBeFound("lastname.specified=false")
    }

    @Test
    @Transactional
    fun getAllCustomersByMiddlenameIsEqualToSomething() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where middlename equals to DEFAULT_MIDDLENAME
        defaultCustomerShouldBeFound("middlename.equals=$DEFAULT_MIDDLENAME")

        // Get all the customerList where middlename equals to UPDATED_MIDDLENAME
        defaultCustomerShouldNotBeFound("middlename.equals=$UPDATED_MIDDLENAME")
    }

    @Test
    @Transactional
    fun getAllCustomersByMiddlenameIsInShouldWork() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where middlename in DEFAULT_MIDDLENAME or UPDATED_MIDDLENAME
        defaultCustomerShouldBeFound("middlename.in=$DEFAULT_MIDDLENAME,$UPDATED_MIDDLENAME")

        // Get all the customerList where middlename equals to UPDATED_MIDDLENAME
        defaultCustomerShouldNotBeFound("middlename.in=$UPDATED_MIDDLENAME")
    }

    @Test
    @Transactional
    fun getAllCustomersByMiddlenameIsNullOrNotNull() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where middlename is not null
        defaultCustomerShouldBeFound("middlename.specified=true")

        // Get all the customerList where middlename is null
        defaultCustomerShouldNotBeFound("middlename.specified=false")
    }

    @Test
    @Transactional
    fun getAllCustomersByBirthDateIsEqualToSomething() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where birthDate equals to DEFAULT_BIRTH_DATE
        defaultCustomerShouldBeFound("birthDate.equals=$DEFAULT_BIRTH_DATE")

        // Get all the customerList where birthDate equals to UPDATED_BIRTH_DATE
        defaultCustomerShouldNotBeFound("birthDate.equals=$UPDATED_BIRTH_DATE")
    }

    @Test
    @Transactional
    fun getAllCustomersByBirthDateIsInShouldWork() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where birthDate in DEFAULT_BIRTH_DATE or UPDATED_BIRTH_DATE
        defaultCustomerShouldBeFound("birthDate.in=$DEFAULT_BIRTH_DATE,$UPDATED_BIRTH_DATE")

        // Get all the customerList where birthDate equals to UPDATED_BIRTH_DATE
        defaultCustomerShouldNotBeFound("birthDate.in=$UPDATED_BIRTH_DATE")
    }

    @Test
    @Transactional
    fun getAllCustomersByBirthDateIsNullOrNotNull() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where birthDate is not null
        defaultCustomerShouldBeFound("birthDate.specified=true")

        // Get all the customerList where birthDate is null
        defaultCustomerShouldNotBeFound("birthDate.specified=false")
    }

    @Test
    @Transactional
    fun getAllCustomersByEmailIsEqualToSomething() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where email equals to DEFAULT_EMAIL
        defaultCustomerShouldBeFound("email.equals=$DEFAULT_EMAIL")

        // Get all the customerList where email equals to UPDATED_EMAIL
        defaultCustomerShouldNotBeFound("email.equals=$UPDATED_EMAIL")
    }

    @Test
    @Transactional
    fun getAllCustomersByEmailIsInShouldWork() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultCustomerShouldBeFound("email.in=$DEFAULT_EMAIL,$UPDATED_EMAIL")

        // Get all the customerList where email equals to UPDATED_EMAIL
        defaultCustomerShouldNotBeFound("email.in=$UPDATED_EMAIL")
    }

    @Test
    @Transactional
    fun getAllCustomersByEmailIsNullOrNotNull() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where email is not null
        defaultCustomerShouldBeFound("email.specified=true")

        // Get all the customerList where email is null
        defaultCustomerShouldNotBeFound("email.specified=false")
    }

    @Test
    @Transactional
    fun getAllCustomersByPhoneIsEqualToSomething() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where phone equals to DEFAULT_PHONE
        defaultCustomerShouldBeFound("phone.equals=$DEFAULT_PHONE")

        // Get all the customerList where phone equals to UPDATED_PHONE
        defaultCustomerShouldNotBeFound("phone.equals=$UPDATED_PHONE")
    }

    @Test
    @Transactional
    fun getAllCustomersByPhoneIsInShouldWork() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultCustomerShouldBeFound("phone.in=$DEFAULT_PHONE,$UPDATED_PHONE")

        // Get all the customerList where phone equals to UPDATED_PHONE
        defaultCustomerShouldNotBeFound("phone.in=$UPDATED_PHONE")
    }

    @Test
    @Transactional
    fun getAllCustomersByPhoneIsNullOrNotNull() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where phone is not null
        defaultCustomerShouldBeFound("phone.specified=true")

        // Get all the customerList where phone is null
        defaultCustomerShouldNotBeFound("phone.specified=false")
    }

    @Test
    @Transactional
    fun getAllCustomersByAddressIsEqualToSomething() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where address equals to DEFAULT_ADDRESS
        defaultCustomerShouldBeFound("address.equals=$DEFAULT_ADDRESS")

        // Get all the customerList where address equals to UPDATED_ADDRESS
        defaultCustomerShouldNotBeFound("address.equals=$UPDATED_ADDRESS")
    }

    @Test
    @Transactional
    fun getAllCustomersByAddressIsInShouldWork() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultCustomerShouldBeFound("address.in=$DEFAULT_ADDRESS,$UPDATED_ADDRESS")

        // Get all the customerList where address equals to UPDATED_ADDRESS
        defaultCustomerShouldNotBeFound("address.in=$UPDATED_ADDRESS")
    }

    @Test
    @Transactional
    fun getAllCustomersByAddressIsNullOrNotNull() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        // Get all the customerList where address is not null
        defaultCustomerShouldBeFound("address.specified=true")

        // Get all the customerList where address is null
        defaultCustomerShouldNotBeFound("address.specified=false")
    }

    @Test
    @Transactional
    fun getAllCustomersByFeedbackIsEqualToSomething() {
        // Initialize the database
        val feedback = FeedbackResourceIT.createEntity(em)
        em.persist(feedback)
        em.flush()
        customerModel.addFeedback(feedback)
        customerRepository.saveAndFlush(customerModel)
        val feedbackId = feedback.id

        // Get all the customerList where feedback equals to feedbackId
        defaultCustomerShouldBeFound("feedbackId.equals=$feedbackId")

        // Get all the customerList where feedback equals to feedbackId + 1
        defaultCustomerShouldNotBeFound("feedbackId.equals=${feedbackId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllCustomersByMeasurementIsEqualToSomething() {
        // Initialize the database
        val measurement = MeasurementResourceIT.createEntity(em)
        em.persist(measurement)
        em.flush()
        customerModel.addMeasurement(measurement)
        customerRepository.saveAndFlush(customerModel)
        val measurementId = measurement.id

        // Get all the customerList where measurement equals to measurementId
        defaultCustomerShouldBeFound("measurementId.equals=$measurementId")

        // Get all the customerList where measurement equals to measurementId + 1
        defaultCustomerShouldNotBeFound("measurementId.equals=${measurementId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllCustomersByOrderIsEqualToSomething() {
        // Initialize the database
        val order = CustomerOrderResourceIT.createEntity(em)
        em.persist(order)
        em.flush()
        customerModel.addOrder(order)
        customerRepository.saveAndFlush(customerModel)
        val orderId = order.id

        // Get all the customerList where order equals to orderId
        defaultCustomerShouldBeFound("orderId.equals=$orderId")

        // Get all the customerList where order equals to orderId + 1
        defaultCustomerShouldNotBeFound("orderId.equals=${orderId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultCustomerShouldBeFound(filter: String) {
        restCustomerMockMvc.perform(get("/api/customers?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customerModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].customerSummary").value(hasItem(DEFAULT_CUSTOMER_SUMMARY)))
            .andExpect(jsonPath("$.[*].firstname").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].lastname").value(hasItem(DEFAULT_LASTNAME)))
            .andExpect(jsonPath("$.[*].middlename").value(hasItem(DEFAULT_MIDDLENAME)))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))

        // Check, that the count call also returns 1
        restCustomerMockMvc.perform(get("/api/customers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultCustomerShouldNotBeFound(filter: String) {
        restCustomerMockMvc.perform(get("/api/customers?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restCustomerMockMvc.perform(get("/api/customers/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingCustomer() {
        // Get the customer
        restCustomerMockMvc.perform(get("/api/customers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateCustomer() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        val databaseSizeBeforeUpdate = customerRepository.findAll().size

        // Update the customer
        val id = customerModel.id
        assertNotNull(id)
        val updatedCustomer = customerRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedCustomer are not directly saved in db
        em.detach(updatedCustomer)
        updatedCustomer.customerSummary = UPDATED_CUSTOMER_SUMMARY
        updatedCustomer.firstname = UPDATED_FIRSTNAME
        updatedCustomer.lastname = UPDATED_LASTNAME
        updatedCustomer.middlename = UPDATED_MIDDLENAME
        updatedCustomer.birthDate = UPDATED_BIRTH_DATE
        updatedCustomer.email = UPDATED_EMAIL
        updatedCustomer.phone = UPDATED_PHONE
        updatedCustomer.address = UPDATED_ADDRESS
        val customerValueObject = customerMapper.toDto(updatedCustomer)

        restCustomerMockMvc.perform(
            put("/api/customers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customerValueObject))
        ).andExpect(status().isOk)

        // Validate the Customer in the database
        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate)
        val testCustomer = customerList[customerList.size - 1]
        assertThat(testCustomer.customerSummary).isEqualTo(UPDATED_CUSTOMER_SUMMARY)
        assertThat(testCustomer.firstname).isEqualTo(UPDATED_FIRSTNAME)
        assertThat(testCustomer.lastname).isEqualTo(UPDATED_LASTNAME)
        assertThat(testCustomer.middlename).isEqualTo(UPDATED_MIDDLENAME)
        assertThat(testCustomer.birthDate).isEqualTo(UPDATED_BIRTH_DATE)
        assertThat(testCustomer.email).isEqualTo(UPDATED_EMAIL)
        assertThat(testCustomer.phone).isEqualTo(UPDATED_PHONE)
        assertThat(testCustomer.address).isEqualTo(UPDATED_ADDRESS)
    }

    @Test
    @Transactional
    fun updateNonExistingCustomer() {
        val databaseSizeBeforeUpdate = customerRepository.findAll().size

        // Create the Customer
        val customerValueObject = customerMapper.toDto(customerModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerMockMvc.perform(
            put("/api/customers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customerValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Customer in the database
        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteCustomer() {
        // Initialize the database
        customerRepository.saveAndFlush(customerModel)

        val databaseSizeBeforeDelete = customerRepository.findAll().size

        val id = customerModel.id
        assertNotNull(id)

        // Delete the customer
        restCustomerMockMvc.perform(
            delete("/api/customers/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(CustomerModel::class.java)
        val customerModel1 = CustomerModel()
        customerModel1.id = 1L
        val customerModel2 = CustomerModel()
        customerModel2.id = customerModel1.id
        assertThat(customerModel1).isEqualTo(customerModel2)
        customerModel2.id = 2L
        assertThat(customerModel1).isNotEqualTo(customerModel2)
        customerModel1.id = null
        assertThat(customerModel1).isNotEqualTo(customerModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(CustomerValueObject::class.java)
        val customerValueObject1 = CustomerValueObject()
        customerValueObject1.id = 1L
        val customerValueObject2 = CustomerValueObject()
        assertThat(customerValueObject1).isNotEqualTo(customerValueObject2)
        customerValueObject2.id = customerValueObject1.id
        assertThat(customerValueObject1).isEqualTo(customerValueObject2)
        customerValueObject2.id = 2L
        assertThat(customerValueObject1).isNotEqualTo(customerValueObject2)
        customerValueObject1.id = null
        assertThat(customerValueObject1).isNotEqualTo(customerValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(customerMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(customerMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_CUSTOMER_SUMMARY: String = "AAAAAAAAAA"
        private const val UPDATED_CUSTOMER_SUMMARY = "BBBBBBBBBB"

        private const val DEFAULT_FIRSTNAME: String = "AAAAAAAAAA"
        private const val UPDATED_FIRSTNAME = "BBBBBBBBBB"

        private const val DEFAULT_LASTNAME: String = "AAAAAAAAAA"
        private const val UPDATED_LASTNAME = "BBBBBBBBBB"

        private const val DEFAULT_MIDDLENAME: String = "AAAAAAAAAA"
        private const val UPDATED_MIDDLENAME = "BBBBBBBBBB"

        private val DEFAULT_BIRTH_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_BIRTH_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_EMAIL: String = "AAAAAAAAAA"
        private const val UPDATED_EMAIL = "BBBBBBBBBB"

        private const val DEFAULT_PHONE: String = "AAAAAAAAAA"
        private const val UPDATED_PHONE = "BBBBBBBBBB"

        private const val DEFAULT_ADDRESS: String = "AAAAAAAAAA"
        private const val UPDATED_ADDRESS = "BBBBBBBBBB"
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): CustomerModel {
            val customerModel = CustomerModel()
            customerModel.customerSummary = DEFAULT_CUSTOMER_SUMMARY
            customerModel.firstname = DEFAULT_FIRSTNAME
            customerModel.lastname = DEFAULT_LASTNAME
            customerModel.middlename = DEFAULT_MIDDLENAME
            customerModel.birthDate = DEFAULT_BIRTH_DATE
            customerModel.email = DEFAULT_EMAIL
            customerModel.phone = DEFAULT_PHONE
            customerModel.address = DEFAULT_ADDRESS

        return customerModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): CustomerModel {
            val customerModel = CustomerModel()
            customerModel.customerSummary = UPDATED_CUSTOMER_SUMMARY
            customerModel.firstname = UPDATED_FIRSTNAME
            customerModel.lastname = UPDATED_LASTNAME
            customerModel.middlename = UPDATED_MIDDLENAME
            customerModel.birthDate = UPDATED_BIRTH_DATE
            customerModel.email = UPDATED_EMAIL
            customerModel.phone = UPDATED_PHONE
            customerModel.address = UPDATED_ADDRESS

        return customerModel
        }
    }
}
