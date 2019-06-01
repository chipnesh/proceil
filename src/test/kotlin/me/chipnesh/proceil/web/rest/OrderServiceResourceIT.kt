package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.OrderServiceModel
import me.chipnesh.proceil.repository.OrderServiceRepository
import me.chipnesh.proceil.service.OrderServiceService
import me.chipnesh.proceil.service.dto.OrderServiceValueObject
import me.chipnesh.proceil.service.mapper.OrderServiceMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.OrderServiceQueryService

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
 * Test class for the OrderServiceResource REST controller.
 *
 * @see OrderServiceResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class OrderServiceResourceIT {

    @Autowired
    private lateinit var orderServiceRepository: OrderServiceRepository

    @Autowired
    private lateinit var orderServiceMapper: OrderServiceMapper

    @Autowired
    private lateinit var orderServiceService: OrderServiceService

    @Autowired
    private lateinit var orderServiceQueryService: OrderServiceQueryService

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

    private lateinit var restOrderServiceMockMvc: MockMvc

    private lateinit var orderServiceModel: OrderServiceModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val orderServiceResource = OrderServiceResource(orderServiceService, orderServiceQueryService)
        this.restOrderServiceMockMvc = MockMvcBuilders.standaloneSetup(orderServiceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        orderServiceModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createOrderService() {
        val databaseSizeBeforeCreate = orderServiceRepository.findAll().size

        // Create the OrderService
        val orderServiceValueObject = orderServiceMapper.toDto(orderServiceModel)
        restOrderServiceMockMvc.perform(
            post("/api/order-services")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderServiceValueObject))
        ).andExpect(status().isCreated)

        // Validate the OrderService in the database
        val orderServiceList = orderServiceRepository.findAll()
        assertThat(orderServiceList).hasSize(databaseSizeBeforeCreate + 1)
        val testOrderService = orderServiceList[orderServiceList.size - 1]
        assertThat(testOrderService.serviceSummary).isEqualTo(DEFAULT_SERVICE_SUMMARY)
        assertThat(testOrderService.createdDate).isEqualTo(DEFAULT_CREATED_DATE)
        assertThat(testOrderService.serviceDate).isEqualTo(DEFAULT_SERVICE_DATE)
    }

    @Test
    @Transactional
    fun createOrderServiceWithExistingId() {
        val databaseSizeBeforeCreate = orderServiceRepository.findAll().size

        // Create the OrderService with an existing ID
        orderServiceModel.id = 1L
        val orderServiceValueObject = orderServiceMapper.toDto(orderServiceModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderServiceMockMvc.perform(
            post("/api/order-services")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderServiceValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the OrderService in the database
        val orderServiceList = orderServiceRepository.findAll()
        assertThat(orderServiceList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllOrderServices() {
        // Initialize the database
        orderServiceRepository.saveAndFlush(orderServiceModel)

        // Get all the orderServiceList
        restOrderServiceMockMvc.perform(get("/api/order-services?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderServiceModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].serviceSummary").value(hasItem(DEFAULT_SERVICE_SUMMARY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].serviceDate").value(hasItem(DEFAULT_SERVICE_DATE.toString())))
    }

    @Test
    @Transactional
    fun getOrderService() {
        // Initialize the database
        orderServiceRepository.saveAndFlush(orderServiceModel)

        val id = orderServiceModel.id
        assertNotNull(id)

        // Get the orderService
        restOrderServiceMockMvc.perform(get("/api/order-services/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.serviceSummary").value(DEFAULT_SERVICE_SUMMARY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.serviceDate").value(DEFAULT_SERVICE_DATE.toString()))
    }

    @Test
    @Transactional
    fun getAllOrderServicesByServiceSummaryIsEqualToSomething() {
        // Initialize the database
        orderServiceRepository.saveAndFlush(orderServiceModel)

        // Get all the orderServiceList where serviceSummary equals to DEFAULT_SERVICE_SUMMARY
        defaultOrderServiceShouldBeFound("serviceSummary.equals=$DEFAULT_SERVICE_SUMMARY")

        // Get all the orderServiceList where serviceSummary equals to UPDATED_SERVICE_SUMMARY
        defaultOrderServiceShouldNotBeFound("serviceSummary.equals=$UPDATED_SERVICE_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllOrderServicesByServiceSummaryIsInShouldWork() {
        // Initialize the database
        orderServiceRepository.saveAndFlush(orderServiceModel)

        // Get all the orderServiceList where serviceSummary in DEFAULT_SERVICE_SUMMARY or UPDATED_SERVICE_SUMMARY
        defaultOrderServiceShouldBeFound("serviceSummary.in=$DEFAULT_SERVICE_SUMMARY,$UPDATED_SERVICE_SUMMARY")

        // Get all the orderServiceList where serviceSummary equals to UPDATED_SERVICE_SUMMARY
        defaultOrderServiceShouldNotBeFound("serviceSummary.in=$UPDATED_SERVICE_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllOrderServicesByServiceSummaryIsNullOrNotNull() {
        // Initialize the database
        orderServiceRepository.saveAndFlush(orderServiceModel)

        // Get all the orderServiceList where serviceSummary is not null
        defaultOrderServiceShouldBeFound("serviceSummary.specified=true")

        // Get all the orderServiceList where serviceSummary is null
        defaultOrderServiceShouldNotBeFound("serviceSummary.specified=false")
    }

    @Test
    @Transactional
    fun getAllOrderServicesByCreatedDateIsEqualToSomething() {
        // Initialize the database
        orderServiceRepository.saveAndFlush(orderServiceModel)

        // Get all the orderServiceList where createdDate equals to DEFAULT_CREATED_DATE
        defaultOrderServiceShouldBeFound("createdDate.equals=$DEFAULT_CREATED_DATE")

        // Get all the orderServiceList where createdDate equals to UPDATED_CREATED_DATE
        defaultOrderServiceShouldNotBeFound("createdDate.equals=$UPDATED_CREATED_DATE")
    }

    @Test
    @Transactional
    fun getAllOrderServicesByCreatedDateIsInShouldWork() {
        // Initialize the database
        orderServiceRepository.saveAndFlush(orderServiceModel)

        // Get all the orderServiceList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultOrderServiceShouldBeFound("createdDate.in=$DEFAULT_CREATED_DATE,$UPDATED_CREATED_DATE")

        // Get all the orderServiceList where createdDate equals to UPDATED_CREATED_DATE
        defaultOrderServiceShouldNotBeFound("createdDate.in=$UPDATED_CREATED_DATE")
    }

    @Test
    @Transactional
    fun getAllOrderServicesByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        orderServiceRepository.saveAndFlush(orderServiceModel)

        // Get all the orderServiceList where createdDate is not null
        defaultOrderServiceShouldBeFound("createdDate.specified=true")

        // Get all the orderServiceList where createdDate is null
        defaultOrderServiceShouldNotBeFound("createdDate.specified=false")
    }

    @Test
    @Transactional
    fun getAllOrderServicesByServiceDateIsEqualToSomething() {
        // Initialize the database
        orderServiceRepository.saveAndFlush(orderServiceModel)

        // Get all the orderServiceList where serviceDate equals to DEFAULT_SERVICE_DATE
        defaultOrderServiceShouldBeFound("serviceDate.equals=$DEFAULT_SERVICE_DATE")

        // Get all the orderServiceList where serviceDate equals to UPDATED_SERVICE_DATE
        defaultOrderServiceShouldNotBeFound("serviceDate.equals=$UPDATED_SERVICE_DATE")
    }

    @Test
    @Transactional
    fun getAllOrderServicesByServiceDateIsInShouldWork() {
        // Initialize the database
        orderServiceRepository.saveAndFlush(orderServiceModel)

        // Get all the orderServiceList where serviceDate in DEFAULT_SERVICE_DATE or UPDATED_SERVICE_DATE
        defaultOrderServiceShouldBeFound("serviceDate.in=$DEFAULT_SERVICE_DATE,$UPDATED_SERVICE_DATE")

        // Get all the orderServiceList where serviceDate equals to UPDATED_SERVICE_DATE
        defaultOrderServiceShouldNotBeFound("serviceDate.in=$UPDATED_SERVICE_DATE")
    }

    @Test
    @Transactional
    fun getAllOrderServicesByServiceDateIsNullOrNotNull() {
        // Initialize the database
        orderServiceRepository.saveAndFlush(orderServiceModel)

        // Get all the orderServiceList where serviceDate is not null
        defaultOrderServiceShouldBeFound("serviceDate.specified=true")

        // Get all the orderServiceList where serviceDate is null
        defaultOrderServiceShouldNotBeFound("serviceDate.specified=false")
    }

    @Test
    @Transactional
    fun getAllOrderServicesByQuotaIsEqualToSomething() {
        // Initialize the database
        val quota = ServiceQuotaResourceIT.createEntity(em)
        em.persist(quota)
        em.flush()
        orderServiceModel.quota = quota
        orderServiceRepository.saveAndFlush(orderServiceModel)
        val quotaId = quota.id

        // Get all the orderServiceList where quota equals to quotaId
        defaultOrderServiceShouldBeFound("quotaId.equals=$quotaId")

        // Get all the orderServiceList where quota equals to quotaId + 1
        defaultOrderServiceShouldNotBeFound("quotaId.equals=${quotaId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllOrderServicesByExecutorIsEqualToSomething() {
        // Initialize the database
        val executor = EmployeeResourceIT.createEntity(em)
        em.persist(executor)
        em.flush()
        orderServiceModel.executor = executor
        orderServiceRepository.saveAndFlush(orderServiceModel)
        val executorId = executor.id

        // Get all the orderServiceList where executor equals to executorId
        defaultOrderServiceShouldBeFound("executorId.equals=$executorId")

        // Get all the orderServiceList where executor equals to executorId + 1
        defaultOrderServiceShouldNotBeFound("executorId.equals=${executorId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllOrderServicesByOrderIsEqualToSomething() {
        // Initialize the database
        val order = CustomerOrderResourceIT.createEntity(em)
        em.persist(order)
        em.flush()
        orderServiceModel.order = order
        orderServiceRepository.saveAndFlush(orderServiceModel)
        val orderId = order.id

        // Get all the orderServiceList where order equals to orderId
        defaultOrderServiceShouldBeFound("orderId.equals=$orderId")

        // Get all the orderServiceList where order equals to orderId + 1
        defaultOrderServiceShouldNotBeFound("orderId.equals=${orderId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultOrderServiceShouldBeFound(filter: String) {
        restOrderServiceMockMvc.perform(get("/api/order-services?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderServiceModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].serviceSummary").value(hasItem(DEFAULT_SERVICE_SUMMARY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].serviceDate").value(hasItem(DEFAULT_SERVICE_DATE.toString())))

        // Check, that the count call also returns 1
        restOrderServiceMockMvc.perform(get("/api/order-services/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultOrderServiceShouldNotBeFound(filter: String) {
        restOrderServiceMockMvc.perform(get("/api/order-services?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restOrderServiceMockMvc.perform(get("/api/order-services/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingOrderService() {
        // Get the orderService
        restOrderServiceMockMvc.perform(get("/api/order-services/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateOrderService() {
        // Initialize the database
        orderServiceRepository.saveAndFlush(orderServiceModel)

        val databaseSizeBeforeUpdate = orderServiceRepository.findAll().size

        // Update the orderService
        val id = orderServiceModel.id
        assertNotNull(id)
        val updatedOrderService = orderServiceRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedOrderService are not directly saved in db
        em.detach(updatedOrderService)
        updatedOrderService.serviceSummary = UPDATED_SERVICE_SUMMARY
        updatedOrderService.createdDate = UPDATED_CREATED_DATE
        updatedOrderService.serviceDate = UPDATED_SERVICE_DATE
        val orderServiceValueObject = orderServiceMapper.toDto(updatedOrderService)

        restOrderServiceMockMvc.perform(
            put("/api/order-services")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderServiceValueObject))
        ).andExpect(status().isOk)

        // Validate the OrderService in the database
        val orderServiceList = orderServiceRepository.findAll()
        assertThat(orderServiceList).hasSize(databaseSizeBeforeUpdate)
        val testOrderService = orderServiceList[orderServiceList.size - 1]
        assertThat(testOrderService.serviceSummary).isEqualTo(UPDATED_SERVICE_SUMMARY)
        assertThat(testOrderService.createdDate).isEqualTo(UPDATED_CREATED_DATE)
        assertThat(testOrderService.serviceDate).isEqualTo(UPDATED_SERVICE_DATE)
    }

    @Test
    @Transactional
    fun updateNonExistingOrderService() {
        val databaseSizeBeforeUpdate = orderServiceRepository.findAll().size

        // Create the OrderService
        val orderServiceValueObject = orderServiceMapper.toDto(orderServiceModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderServiceMockMvc.perform(
            put("/api/order-services")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderServiceValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the OrderService in the database
        val orderServiceList = orderServiceRepository.findAll()
        assertThat(orderServiceList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteOrderService() {
        // Initialize the database
        orderServiceRepository.saveAndFlush(orderServiceModel)

        val databaseSizeBeforeDelete = orderServiceRepository.findAll().size

        val id = orderServiceModel.id
        assertNotNull(id)

        // Delete the orderService
        restOrderServiceMockMvc.perform(
            delete("/api/order-services/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val orderServiceList = orderServiceRepository.findAll()
        assertThat(orderServiceList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(OrderServiceModel::class.java)
        val orderServiceModel1 = OrderServiceModel()
        orderServiceModel1.id = 1L
        val orderServiceModel2 = OrderServiceModel()
        orderServiceModel2.id = orderServiceModel1.id
        assertThat(orderServiceModel1).isEqualTo(orderServiceModel2)
        orderServiceModel2.id = 2L
        assertThat(orderServiceModel1).isNotEqualTo(orderServiceModel2)
        orderServiceModel1.id = null
        assertThat(orderServiceModel1).isNotEqualTo(orderServiceModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(OrderServiceValueObject::class.java)
        val orderServiceValueObject1 = OrderServiceValueObject()
        orderServiceValueObject1.id = 1L
        val orderServiceValueObject2 = OrderServiceValueObject()
        assertThat(orderServiceValueObject1).isNotEqualTo(orderServiceValueObject2)
        orderServiceValueObject2.id = orderServiceValueObject1.id
        assertThat(orderServiceValueObject1).isEqualTo(orderServiceValueObject2)
        orderServiceValueObject2.id = 2L
        assertThat(orderServiceValueObject1).isNotEqualTo(orderServiceValueObject2)
        orderServiceValueObject1.id = null
        assertThat(orderServiceValueObject1).isNotEqualTo(orderServiceValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(orderServiceMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(orderServiceMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_SERVICE_SUMMARY: String = "AAAAAAAAAA"
        private const val UPDATED_SERVICE_SUMMARY = "BBBBBBBBBB"

        private val DEFAULT_CREATED_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_CREATED_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_SERVICE_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_SERVICE_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): OrderServiceModel {
            val orderServiceModel = OrderServiceModel()
            orderServiceModel.serviceSummary = DEFAULT_SERVICE_SUMMARY
            orderServiceModel.createdDate = DEFAULT_CREATED_DATE
            orderServiceModel.serviceDate = DEFAULT_SERVICE_DATE

        return orderServiceModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): OrderServiceModel {
            val orderServiceModel = OrderServiceModel()
            orderServiceModel.serviceSummary = UPDATED_SERVICE_SUMMARY
            orderServiceModel.createdDate = UPDATED_CREATED_DATE
            orderServiceModel.serviceDate = UPDATED_SERVICE_DATE

        return orderServiceModel
        }
    }
}
