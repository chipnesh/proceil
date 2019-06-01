package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.CustomerOrderModel
import me.chipnesh.proceil.repository.CustomerOrderRepository
import me.chipnesh.proceil.service.CustomerOrderService
import me.chipnesh.proceil.service.dto.CustomerOrderValueObject
import me.chipnesh.proceil.service.mapper.CustomerOrderMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.CustomerOrderQueryService

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

import me.chipnesh.proceil.domain.enumeration.OrderStatus
/**
 * Test class for the CustomerOrderResource REST controller.
 *
 * @see CustomerOrderResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class CustomerOrderResourceIT {

    @Autowired
    private lateinit var customerOrderRepository: CustomerOrderRepository

    @Autowired
    private lateinit var customerOrderMapper: CustomerOrderMapper

    @Autowired
    private lateinit var customerOrderService: CustomerOrderService

    @Autowired
    private lateinit var customerOrderQueryService: CustomerOrderQueryService

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

    private lateinit var restCustomerOrderMockMvc: MockMvc

    private lateinit var customerOrderModel: CustomerOrderModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val customerOrderResource = CustomerOrderResource(customerOrderService, customerOrderQueryService)
        this.restCustomerOrderMockMvc = MockMvcBuilders.standaloneSetup(customerOrderResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        customerOrderModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createCustomerOrder() {
        val databaseSizeBeforeCreate = customerOrderRepository.findAll().size

        // Create the CustomerOrder
        val customerOrderValueObject = customerOrderMapper.toDto(customerOrderModel)
        restCustomerOrderMockMvc.perform(
            post("/api/customer-orders")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customerOrderValueObject))
        ).andExpect(status().isCreated)

        // Validate the CustomerOrder in the database
        val customerOrderList = customerOrderRepository.findAll()
        assertThat(customerOrderList).hasSize(databaseSizeBeforeCreate + 1)
        val testCustomerOrder = customerOrderList[customerOrderList.size - 1]
        assertThat(testCustomerOrder.orderSummary).isEqualTo(DEFAULT_ORDER_SUMMARY)
        assertThat(testCustomerOrder.createdDate).isEqualTo(DEFAULT_CREATED_DATE)
        assertThat(testCustomerOrder.deadlineDate).isEqualTo(DEFAULT_DEADLINE_DATE)
        assertThat(testCustomerOrder.orderStatus).isEqualTo(DEFAULT_ORDER_STATUS)
        assertThat(testCustomerOrder.orderPaid).isEqualTo(DEFAULT_ORDER_PAID)
        assertThat(testCustomerOrder.orderNote).isEqualTo(DEFAULT_ORDER_NOTE)
    }

    @Test
    @Transactional
    fun createCustomerOrderWithExistingId() {
        val databaseSizeBeforeCreate = customerOrderRepository.findAll().size

        // Create the CustomerOrder with an existing ID
        customerOrderModel.id = 1L
        val customerOrderValueObject = customerOrderMapper.toDto(customerOrderModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerOrderMockMvc.perform(
            post("/api/customer-orders")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customerOrderValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the CustomerOrder in the database
        val customerOrderList = customerOrderRepository.findAll()
        assertThat(customerOrderList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllCustomerOrders() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList
        restCustomerOrderMockMvc.perform(get("/api/customer-orders?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customerOrderModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].orderSummary").value(hasItem(DEFAULT_ORDER_SUMMARY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deadlineDate").value(hasItem(DEFAULT_DEADLINE_DATE.toString())))
            .andExpect(jsonPath("$.[*].orderStatus").value(hasItem(DEFAULT_ORDER_STATUS.toString())))
            .andExpect(jsonPath("$.[*].orderPaid").value(hasItem(DEFAULT_ORDER_PAID)))
            .andExpect(jsonPath("$.[*].orderNote").value(hasItem(DEFAULT_ORDER_NOTE.toString())))
    }

    @Test
    @Transactional
    fun getCustomerOrder() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        val id = customerOrderModel.id
        assertNotNull(id)

        // Get the customerOrder
        restCustomerOrderMockMvc.perform(get("/api/customer-orders/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.orderSummary").value(DEFAULT_ORDER_SUMMARY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.deadlineDate").value(DEFAULT_DEADLINE_DATE.toString()))
            .andExpect(jsonPath("$.orderStatus").value(DEFAULT_ORDER_STATUS.toString()))
            .andExpect(jsonPath("$.orderPaid").value(DEFAULT_ORDER_PAID))
            .andExpect(jsonPath("$.orderNote").value(DEFAULT_ORDER_NOTE.toString()))
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByOrderSummaryIsEqualToSomething() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where orderSummary equals to DEFAULT_ORDER_SUMMARY
        defaultCustomerOrderShouldBeFound("orderSummary.equals=$DEFAULT_ORDER_SUMMARY")

        // Get all the customerOrderList where orderSummary equals to UPDATED_ORDER_SUMMARY
        defaultCustomerOrderShouldNotBeFound("orderSummary.equals=$UPDATED_ORDER_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByOrderSummaryIsInShouldWork() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where orderSummary in DEFAULT_ORDER_SUMMARY or UPDATED_ORDER_SUMMARY
        defaultCustomerOrderShouldBeFound("orderSummary.in=$DEFAULT_ORDER_SUMMARY,$UPDATED_ORDER_SUMMARY")

        // Get all the customerOrderList where orderSummary equals to UPDATED_ORDER_SUMMARY
        defaultCustomerOrderShouldNotBeFound("orderSummary.in=$UPDATED_ORDER_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByOrderSummaryIsNullOrNotNull() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where orderSummary is not null
        defaultCustomerOrderShouldBeFound("orderSummary.specified=true")

        // Get all the customerOrderList where orderSummary is null
        defaultCustomerOrderShouldNotBeFound("orderSummary.specified=false")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByCreatedDateIsEqualToSomething() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where createdDate equals to DEFAULT_CREATED_DATE
        defaultCustomerOrderShouldBeFound("createdDate.equals=$DEFAULT_CREATED_DATE")

        // Get all the customerOrderList where createdDate equals to UPDATED_CREATED_DATE
        defaultCustomerOrderShouldNotBeFound("createdDate.equals=$UPDATED_CREATED_DATE")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByCreatedDateIsInShouldWork() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultCustomerOrderShouldBeFound("createdDate.in=$DEFAULT_CREATED_DATE,$UPDATED_CREATED_DATE")

        // Get all the customerOrderList where createdDate equals to UPDATED_CREATED_DATE
        defaultCustomerOrderShouldNotBeFound("createdDate.in=$UPDATED_CREATED_DATE")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where createdDate is not null
        defaultCustomerOrderShouldBeFound("createdDate.specified=true")

        // Get all the customerOrderList where createdDate is null
        defaultCustomerOrderShouldNotBeFound("createdDate.specified=false")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByDeadlineDateIsEqualToSomething() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where deadlineDate equals to DEFAULT_DEADLINE_DATE
        defaultCustomerOrderShouldBeFound("deadlineDate.equals=$DEFAULT_DEADLINE_DATE")

        // Get all the customerOrderList where deadlineDate equals to UPDATED_DEADLINE_DATE
        defaultCustomerOrderShouldNotBeFound("deadlineDate.equals=$UPDATED_DEADLINE_DATE")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByDeadlineDateIsInShouldWork() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where deadlineDate in DEFAULT_DEADLINE_DATE or UPDATED_DEADLINE_DATE
        defaultCustomerOrderShouldBeFound("deadlineDate.in=$DEFAULT_DEADLINE_DATE,$UPDATED_DEADLINE_DATE")

        // Get all the customerOrderList where deadlineDate equals to UPDATED_DEADLINE_DATE
        defaultCustomerOrderShouldNotBeFound("deadlineDate.in=$UPDATED_DEADLINE_DATE")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByDeadlineDateIsNullOrNotNull() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where deadlineDate is not null
        defaultCustomerOrderShouldBeFound("deadlineDate.specified=true")

        // Get all the customerOrderList where deadlineDate is null
        defaultCustomerOrderShouldNotBeFound("deadlineDate.specified=false")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByOrderStatusIsEqualToSomething() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where orderStatus equals to DEFAULT_ORDER_STATUS
        defaultCustomerOrderShouldBeFound("orderStatus.equals=$DEFAULT_ORDER_STATUS")

        // Get all the customerOrderList where orderStatus equals to UPDATED_ORDER_STATUS
        defaultCustomerOrderShouldNotBeFound("orderStatus.equals=$UPDATED_ORDER_STATUS")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByOrderStatusIsInShouldWork() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where orderStatus in DEFAULT_ORDER_STATUS or UPDATED_ORDER_STATUS
        defaultCustomerOrderShouldBeFound("orderStatus.in=$DEFAULT_ORDER_STATUS,$UPDATED_ORDER_STATUS")

        // Get all the customerOrderList where orderStatus equals to UPDATED_ORDER_STATUS
        defaultCustomerOrderShouldNotBeFound("orderStatus.in=$UPDATED_ORDER_STATUS")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByOrderStatusIsNullOrNotNull() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where orderStatus is not null
        defaultCustomerOrderShouldBeFound("orderStatus.specified=true")

        // Get all the customerOrderList where orderStatus is null
        defaultCustomerOrderShouldNotBeFound("orderStatus.specified=false")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByOrderPaidIsEqualToSomething() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where orderPaid equals to DEFAULT_ORDER_PAID
        defaultCustomerOrderShouldBeFound("orderPaid.equals=$DEFAULT_ORDER_PAID")

        // Get all the customerOrderList where orderPaid equals to UPDATED_ORDER_PAID
        defaultCustomerOrderShouldNotBeFound("orderPaid.equals=$UPDATED_ORDER_PAID")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByOrderPaidIsInShouldWork() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where orderPaid in DEFAULT_ORDER_PAID or UPDATED_ORDER_PAID
        defaultCustomerOrderShouldBeFound("orderPaid.in=$DEFAULT_ORDER_PAID,$UPDATED_ORDER_PAID")

        // Get all the customerOrderList where orderPaid equals to UPDATED_ORDER_PAID
        defaultCustomerOrderShouldNotBeFound("orderPaid.in=$UPDATED_ORDER_PAID")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByOrderPaidIsNullOrNotNull() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        // Get all the customerOrderList where orderPaid is not null
        defaultCustomerOrderShouldBeFound("orderPaid.specified=true")

        // Get all the customerOrderList where orderPaid is null
        defaultCustomerOrderShouldNotBeFound("orderPaid.specified=false")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByMaterialsIsEqualToSomething() {
        // Initialize the database
        val materials = OrderMaterialResourceIT.createEntity(em)
        em.persist(materials)
        em.flush()
        customerOrderModel.addMaterials(materials)
        customerOrderRepository.saveAndFlush(customerOrderModel)
        val materialsId = materials.id

        // Get all the customerOrderList where materials equals to materialsId
        defaultCustomerOrderShouldBeFound("materialsId.equals=$materialsId")

        // Get all the customerOrderList where materials equals to materialsId + 1
        defaultCustomerOrderShouldNotBeFound("materialsId.equals=${materialsId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByServiceIsEqualToSomething() {
        // Initialize the database
        val service = OrderServiceResourceIT.createEntity(em)
        em.persist(service)
        em.flush()
        customerOrderModel.addService(service)
        customerOrderRepository.saveAndFlush(customerOrderModel)
        val serviceId = service.id

        // Get all the customerOrderList where service equals to serviceId
        defaultCustomerOrderShouldBeFound("serviceId.equals=$serviceId")

        // Get all the customerOrderList where service equals to serviceId + 1
        defaultCustomerOrderShouldNotBeFound("serviceId.equals=${serviceId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByManagerIsEqualToSomething() {
        // Initialize the database
        val manager = EmployeeResourceIT.createEntity(em)
        em.persist(manager)
        em.flush()
        customerOrderModel.manager = manager
        customerOrderRepository.saveAndFlush(customerOrderModel)
        val managerId = manager.id

        // Get all the customerOrderList where manager equals to managerId
        defaultCustomerOrderShouldBeFound("managerId.equals=$managerId")

        // Get all the customerOrderList where manager equals to managerId + 1
        defaultCustomerOrderShouldNotBeFound("managerId.equals=${managerId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllCustomerOrdersByCustomerIsEqualToSomething() {
        // Initialize the database
        val customer = CustomerResourceIT.createEntity(em)
        em.persist(customer)
        em.flush()
        customerOrderModel.customer = customer
        customerOrderRepository.saveAndFlush(customerOrderModel)
        val customerId = customer.id

        // Get all the customerOrderList where customer equals to customerId
        defaultCustomerOrderShouldBeFound("customerId.equals=$customerId")

        // Get all the customerOrderList where customer equals to customerId + 1
        defaultCustomerOrderShouldNotBeFound("customerId.equals=${customerId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultCustomerOrderShouldBeFound(filter: String) {
        restCustomerOrderMockMvc.perform(get("/api/customer-orders?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customerOrderModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].orderSummary").value(hasItem(DEFAULT_ORDER_SUMMARY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].deadlineDate").value(hasItem(DEFAULT_DEADLINE_DATE.toString())))
            .andExpect(jsonPath("$.[*].orderStatus").value(hasItem(DEFAULT_ORDER_STATUS.toString())))
            .andExpect(jsonPath("$.[*].orderPaid").value(hasItem(DEFAULT_ORDER_PAID)))
            .andExpect(jsonPath("$.[*].orderNote").value(hasItem(DEFAULT_ORDER_NOTE.toString())))

        // Check, that the count call also returns 1
        restCustomerOrderMockMvc.perform(get("/api/customer-orders/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultCustomerOrderShouldNotBeFound(filter: String) {
        restCustomerOrderMockMvc.perform(get("/api/customer-orders?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restCustomerOrderMockMvc.perform(get("/api/customer-orders/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingCustomerOrder() {
        // Get the customerOrder
        restCustomerOrderMockMvc.perform(get("/api/customer-orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateCustomerOrder() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        val databaseSizeBeforeUpdate = customerOrderRepository.findAll().size

        // Update the customerOrder
        val id = customerOrderModel.id
        assertNotNull(id)
        val updatedCustomerOrder = customerOrderRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedCustomerOrder are not directly saved in db
        em.detach(updatedCustomerOrder)
        updatedCustomerOrder.orderSummary = UPDATED_ORDER_SUMMARY
        updatedCustomerOrder.createdDate = UPDATED_CREATED_DATE
        updatedCustomerOrder.deadlineDate = UPDATED_DEADLINE_DATE
        updatedCustomerOrder.orderStatus = UPDATED_ORDER_STATUS
        updatedCustomerOrder.orderPaid = UPDATED_ORDER_PAID
        updatedCustomerOrder.orderNote = UPDATED_ORDER_NOTE
        val customerOrderValueObject = customerOrderMapper.toDto(updatedCustomerOrder)

        restCustomerOrderMockMvc.perform(
            put("/api/customer-orders")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customerOrderValueObject))
        ).andExpect(status().isOk)

        // Validate the CustomerOrder in the database
        val customerOrderList = customerOrderRepository.findAll()
        assertThat(customerOrderList).hasSize(databaseSizeBeforeUpdate)
        val testCustomerOrder = customerOrderList[customerOrderList.size - 1]
        assertThat(testCustomerOrder.orderSummary).isEqualTo(UPDATED_ORDER_SUMMARY)
        assertThat(testCustomerOrder.createdDate).isEqualTo(UPDATED_CREATED_DATE)
        assertThat(testCustomerOrder.deadlineDate).isEqualTo(UPDATED_DEADLINE_DATE)
        assertThat(testCustomerOrder.orderStatus).isEqualTo(UPDATED_ORDER_STATUS)
        assertThat(testCustomerOrder.orderPaid).isEqualTo(UPDATED_ORDER_PAID)
        assertThat(testCustomerOrder.orderNote).isEqualTo(UPDATED_ORDER_NOTE)
    }

    @Test
    @Transactional
    fun updateNonExistingCustomerOrder() {
        val databaseSizeBeforeUpdate = customerOrderRepository.findAll().size

        // Create the CustomerOrder
        val customerOrderValueObject = customerOrderMapper.toDto(customerOrderModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerOrderMockMvc.perform(
            put("/api/customer-orders")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(customerOrderValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the CustomerOrder in the database
        val customerOrderList = customerOrderRepository.findAll()
        assertThat(customerOrderList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteCustomerOrder() {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrderModel)

        val databaseSizeBeforeDelete = customerOrderRepository.findAll().size

        val id = customerOrderModel.id
        assertNotNull(id)

        // Delete the customerOrder
        restCustomerOrderMockMvc.perform(
            delete("/api/customer-orders/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val customerOrderList = customerOrderRepository.findAll()
        assertThat(customerOrderList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(CustomerOrderModel::class.java)
        val customerOrderModel1 = CustomerOrderModel()
        customerOrderModel1.id = 1L
        val customerOrderModel2 = CustomerOrderModel()
        customerOrderModel2.id = customerOrderModel1.id
        assertThat(customerOrderModel1).isEqualTo(customerOrderModel2)
        customerOrderModel2.id = 2L
        assertThat(customerOrderModel1).isNotEqualTo(customerOrderModel2)
        customerOrderModel1.id = null
        assertThat(customerOrderModel1).isNotEqualTo(customerOrderModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(CustomerOrderValueObject::class.java)
        val customerOrderValueObject1 = CustomerOrderValueObject()
        customerOrderValueObject1.id = 1L
        val customerOrderValueObject2 = CustomerOrderValueObject()
        assertThat(customerOrderValueObject1).isNotEqualTo(customerOrderValueObject2)
        customerOrderValueObject2.id = customerOrderValueObject1.id
        assertThat(customerOrderValueObject1).isEqualTo(customerOrderValueObject2)
        customerOrderValueObject2.id = 2L
        assertThat(customerOrderValueObject1).isNotEqualTo(customerOrderValueObject2)
        customerOrderValueObject1.id = null
        assertThat(customerOrderValueObject1).isNotEqualTo(customerOrderValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(customerOrderMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(customerOrderMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_ORDER_SUMMARY: String = "AAAAAAAAAA"
        private const val UPDATED_ORDER_SUMMARY = "BBBBBBBBBB"

        private val DEFAULT_CREATED_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_CREATED_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_DEADLINE_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_DEADLINE_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_ORDER_STATUS: OrderStatus = OrderStatus.NEW
        private val UPDATED_ORDER_STATUS: OrderStatus = OrderStatus.PENDING

        private const val DEFAULT_ORDER_PAID: Boolean = false
        private const val UPDATED_ORDER_PAID: Boolean = true

        private const val DEFAULT_ORDER_NOTE: String = "AAAAAAAAAA"
        private const val UPDATED_ORDER_NOTE = "BBBBBBBBBB"
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): CustomerOrderModel {
            val customerOrderModel = CustomerOrderModel()
            customerOrderModel.orderSummary = DEFAULT_ORDER_SUMMARY
            customerOrderModel.createdDate = DEFAULT_CREATED_DATE
            customerOrderModel.deadlineDate = DEFAULT_DEADLINE_DATE
            customerOrderModel.orderStatus = DEFAULT_ORDER_STATUS
            customerOrderModel.orderPaid = DEFAULT_ORDER_PAID
            customerOrderModel.orderNote = DEFAULT_ORDER_NOTE

        return customerOrderModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): CustomerOrderModel {
            val customerOrderModel = CustomerOrderModel()
            customerOrderModel.orderSummary = UPDATED_ORDER_SUMMARY
            customerOrderModel.createdDate = UPDATED_CREATED_DATE
            customerOrderModel.deadlineDate = UPDATED_DEADLINE_DATE
            customerOrderModel.orderStatus = UPDATED_ORDER_STATUS
            customerOrderModel.orderPaid = UPDATED_ORDER_PAID
            customerOrderModel.orderNote = UPDATED_ORDER_NOTE

        return customerOrderModel
        }
    }
}
