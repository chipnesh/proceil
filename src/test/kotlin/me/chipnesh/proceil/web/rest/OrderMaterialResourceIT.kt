package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.OrderMaterialModel
import me.chipnesh.proceil.repository.OrderMaterialRepository
import me.chipnesh.proceil.service.OrderMaterialService
import me.chipnesh.proceil.service.dto.OrderMaterialValueObject
import me.chipnesh.proceil.service.mapper.OrderMaterialMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.OrderMaterialQueryService

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

import me.chipnesh.proceil.domain.enumeration.MeasureUnit
/**
 * Test class for the OrderMaterialResource REST controller.
 *
 * @see OrderMaterialResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class OrderMaterialResourceIT {

    @Autowired
    private lateinit var orderMaterialRepository: OrderMaterialRepository

    @Autowired
    private lateinit var orderMaterialMapper: OrderMaterialMapper

    @Autowired
    private lateinit var orderMaterialService: OrderMaterialService

    @Autowired
    private lateinit var orderMaterialQueryService: OrderMaterialQueryService

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

    private lateinit var restOrderMaterialMockMvc: MockMvc

    private lateinit var orderMaterialModel: OrderMaterialModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val orderMaterialResource = OrderMaterialResource(orderMaterialService, orderMaterialQueryService)
        this.restOrderMaterialMockMvc = MockMvcBuilders.standaloneSetup(orderMaterialResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        orderMaterialModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createOrderMaterial() {
        val databaseSizeBeforeCreate = orderMaterialRepository.findAll().size

        // Create the OrderMaterial
        val orderMaterialValueObject = orderMaterialMapper.toDto(orderMaterialModel)
        restOrderMaterialMockMvc.perform(
            post("/api/order-materials")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderMaterialValueObject))
        ).andExpect(status().isCreated)

        // Validate the OrderMaterial in the database
        val orderMaterialList = orderMaterialRepository.findAll()
        assertThat(orderMaterialList).hasSize(databaseSizeBeforeCreate + 1)
        val testOrderMaterial = orderMaterialList[orderMaterialList.size - 1]
        assertThat(testOrderMaterial.materialSummary).isEqualTo(DEFAULT_MATERIAL_SUMMARY)
        assertThat(testOrderMaterial.createdDate).isEqualTo(DEFAULT_CREATED_DATE)
        assertThat(testOrderMaterial.materialQuantity).isEqualTo(DEFAULT_MATERIAL_QUANTITY)
        assertThat(testOrderMaterial.measureUnit).isEqualTo(DEFAULT_MEASURE_UNIT)
    }

    @Test
    @Transactional
    fun createOrderMaterialWithExistingId() {
        val databaseSizeBeforeCreate = orderMaterialRepository.findAll().size

        // Create the OrderMaterial with an existing ID
        orderMaterialModel.id = 1L
        val orderMaterialValueObject = orderMaterialMapper.toDto(orderMaterialModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderMaterialMockMvc.perform(
            post("/api/order-materials")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderMaterialValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the OrderMaterial in the database
        val orderMaterialList = orderMaterialRepository.findAll()
        assertThat(orderMaterialList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkMaterialQuantityIsRequired() {
        val databaseSizeBeforeTest = orderMaterialRepository.findAll().size
        // set the field null
        orderMaterialModel.materialQuantity = null

        // Create the OrderMaterial, which fails.
        val orderMaterialValueObject = orderMaterialMapper.toDto(orderMaterialModel)

        restOrderMaterialMockMvc.perform(
            post("/api/order-materials")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderMaterialValueObject))
        ).andExpect(status().isBadRequest)

        val orderMaterialList = orderMaterialRepository.findAll()
        assertThat(orderMaterialList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllOrderMaterials() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList
        restOrderMaterialMockMvc.perform(get("/api/order-materials?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderMaterialModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].materialSummary").value(hasItem(DEFAULT_MATERIAL_SUMMARY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].materialQuantity").value(hasItem(DEFAULT_MATERIAL_QUANTITY)))
            .andExpect(jsonPath("$.[*].measureUnit").value(hasItem(DEFAULT_MEASURE_UNIT.toString())))
    }

    @Test
    @Transactional
    fun getOrderMaterial() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        val id = orderMaterialModel.id
        assertNotNull(id)

        // Get the orderMaterial
        restOrderMaterialMockMvc.perform(get("/api/order-materials/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.materialSummary").value(DEFAULT_MATERIAL_SUMMARY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.materialQuantity").value(DEFAULT_MATERIAL_QUANTITY))
            .andExpect(jsonPath("$.measureUnit").value(DEFAULT_MEASURE_UNIT.toString()))
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByMaterialSummaryIsEqualToSomething() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList where materialSummary equals to DEFAULT_MATERIAL_SUMMARY
        defaultOrderMaterialShouldBeFound("materialSummary.equals=$DEFAULT_MATERIAL_SUMMARY")

        // Get all the orderMaterialList where materialSummary equals to UPDATED_MATERIAL_SUMMARY
        defaultOrderMaterialShouldNotBeFound("materialSummary.equals=$UPDATED_MATERIAL_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByMaterialSummaryIsInShouldWork() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList where materialSummary in DEFAULT_MATERIAL_SUMMARY or UPDATED_MATERIAL_SUMMARY
        defaultOrderMaterialShouldBeFound("materialSummary.in=$DEFAULT_MATERIAL_SUMMARY,$UPDATED_MATERIAL_SUMMARY")

        // Get all the orderMaterialList where materialSummary equals to UPDATED_MATERIAL_SUMMARY
        defaultOrderMaterialShouldNotBeFound("materialSummary.in=$UPDATED_MATERIAL_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByMaterialSummaryIsNullOrNotNull() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList where materialSummary is not null
        defaultOrderMaterialShouldBeFound("materialSummary.specified=true")

        // Get all the orderMaterialList where materialSummary is null
        defaultOrderMaterialShouldNotBeFound("materialSummary.specified=false")
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByCreatedDateIsEqualToSomething() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList where createdDate equals to DEFAULT_CREATED_DATE
        defaultOrderMaterialShouldBeFound("createdDate.equals=$DEFAULT_CREATED_DATE")

        // Get all the orderMaterialList where createdDate equals to UPDATED_CREATED_DATE
        defaultOrderMaterialShouldNotBeFound("createdDate.equals=$UPDATED_CREATED_DATE")
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByCreatedDateIsInShouldWork() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultOrderMaterialShouldBeFound("createdDate.in=$DEFAULT_CREATED_DATE,$UPDATED_CREATED_DATE")

        // Get all the orderMaterialList where createdDate equals to UPDATED_CREATED_DATE
        defaultOrderMaterialShouldNotBeFound("createdDate.in=$UPDATED_CREATED_DATE")
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList where createdDate is not null
        defaultOrderMaterialShouldBeFound("createdDate.specified=true")

        // Get all the orderMaterialList where createdDate is null
        defaultOrderMaterialShouldNotBeFound("createdDate.specified=false")
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByMaterialQuantityIsEqualToSomething() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList where materialQuantity equals to DEFAULT_MATERIAL_QUANTITY
        defaultOrderMaterialShouldBeFound("materialQuantity.equals=$DEFAULT_MATERIAL_QUANTITY")

        // Get all the orderMaterialList where materialQuantity equals to UPDATED_MATERIAL_QUANTITY
        defaultOrderMaterialShouldNotBeFound("materialQuantity.equals=$UPDATED_MATERIAL_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByMaterialQuantityIsInShouldWork() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList where materialQuantity in DEFAULT_MATERIAL_QUANTITY or UPDATED_MATERIAL_QUANTITY
        defaultOrderMaterialShouldBeFound("materialQuantity.in=$DEFAULT_MATERIAL_QUANTITY,$UPDATED_MATERIAL_QUANTITY")

        // Get all the orderMaterialList where materialQuantity equals to UPDATED_MATERIAL_QUANTITY
        defaultOrderMaterialShouldNotBeFound("materialQuantity.in=$UPDATED_MATERIAL_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByMaterialQuantityIsNullOrNotNull() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList where materialQuantity is not null
        defaultOrderMaterialShouldBeFound("materialQuantity.specified=true")

        // Get all the orderMaterialList where materialQuantity is null
        defaultOrderMaterialShouldNotBeFound("materialQuantity.specified=false")
    }
    @Test
    @Transactional
    fun getAllOrderMaterialsByMaterialQuantityIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList where materialQuantity greater than or equals to DEFAULT_MATERIAL_QUANTITY
        defaultOrderMaterialShouldBeFound("materialQuantity.greaterOrEqualThan=$DEFAULT_MATERIAL_QUANTITY")

        // Get all the orderMaterialList where materialQuantity greater than or equals to UPDATED_MATERIAL_QUANTITY
        defaultOrderMaterialShouldNotBeFound("materialQuantity.greaterOrEqualThan=$UPDATED_MATERIAL_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByMaterialQuantityIsLessThanSomething() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList where materialQuantity less than or equals to DEFAULT_MATERIAL_QUANTITY
        defaultOrderMaterialShouldNotBeFound("materialQuantity.lessThan=$DEFAULT_MATERIAL_QUANTITY")

        // Get all the orderMaterialList where materialQuantity less than or equals to UPDATED_MATERIAL_QUANTITY
        defaultOrderMaterialShouldBeFound("materialQuantity.lessThan=$UPDATED_MATERIAL_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByMeasureUnitIsEqualToSomething() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList where measureUnit equals to DEFAULT_MEASURE_UNIT
        defaultOrderMaterialShouldBeFound("measureUnit.equals=$DEFAULT_MEASURE_UNIT")

        // Get all the orderMaterialList where measureUnit equals to UPDATED_MEASURE_UNIT
        defaultOrderMaterialShouldNotBeFound("measureUnit.equals=$UPDATED_MEASURE_UNIT")
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByMeasureUnitIsInShouldWork() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList where measureUnit in DEFAULT_MEASURE_UNIT or UPDATED_MEASURE_UNIT
        defaultOrderMaterialShouldBeFound("measureUnit.in=$DEFAULT_MEASURE_UNIT,$UPDATED_MEASURE_UNIT")

        // Get all the orderMaterialList where measureUnit equals to UPDATED_MEASURE_UNIT
        defaultOrderMaterialShouldNotBeFound("measureUnit.in=$UPDATED_MEASURE_UNIT")
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByMeasureUnitIsNullOrNotNull() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        // Get all the orderMaterialList where measureUnit is not null
        defaultOrderMaterialShouldBeFound("measureUnit.specified=true")

        // Get all the orderMaterialList where measureUnit is null
        defaultOrderMaterialShouldNotBeFound("measureUnit.specified=false")
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByReserveIsEqualToSomething() {
        // Initialize the database
        val reserve = MaterialReserveResourceIT.createEntity(em)
        em.persist(reserve)
        em.flush()
        orderMaterialModel.reserve = reserve
        orderMaterialRepository.saveAndFlush(orderMaterialModel)
        val reserveId = reserve.id

        // Get all the orderMaterialList where reserve equals to reserveId
        defaultOrderMaterialShouldBeFound("reserveId.equals=$reserveId")

        // Get all the orderMaterialList where reserve equals to reserveId + 1
        defaultOrderMaterialShouldNotBeFound("reserveId.equals=${reserveId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllOrderMaterialsByOrderIsEqualToSomething() {
        // Initialize the database
        val order = CustomerOrderResourceIT.createEntity(em)
        em.persist(order)
        em.flush()
        orderMaterialModel.order = order
        orderMaterialRepository.saveAndFlush(orderMaterialModel)
        val orderId = order.id

        // Get all the orderMaterialList where order equals to orderId
        defaultOrderMaterialShouldBeFound("orderId.equals=$orderId")

        // Get all the orderMaterialList where order equals to orderId + 1
        defaultOrderMaterialShouldNotBeFound("orderId.equals=${orderId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultOrderMaterialShouldBeFound(filter: String) {
        restOrderMaterialMockMvc.perform(get("/api/order-materials?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderMaterialModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].materialSummary").value(hasItem(DEFAULT_MATERIAL_SUMMARY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].materialQuantity").value(hasItem(DEFAULT_MATERIAL_QUANTITY)))
            .andExpect(jsonPath("$.[*].measureUnit").value(hasItem(DEFAULT_MEASURE_UNIT.toString())))

        // Check, that the count call also returns 1
        restOrderMaterialMockMvc.perform(get("/api/order-materials/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultOrderMaterialShouldNotBeFound(filter: String) {
        restOrderMaterialMockMvc.perform(get("/api/order-materials?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restOrderMaterialMockMvc.perform(get("/api/order-materials/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingOrderMaterial() {
        // Get the orderMaterial
        restOrderMaterialMockMvc.perform(get("/api/order-materials/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateOrderMaterial() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        val databaseSizeBeforeUpdate = orderMaterialRepository.findAll().size

        // Update the orderMaterial
        val id = orderMaterialModel.id
        assertNotNull(id)
        val updatedOrderMaterial = orderMaterialRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedOrderMaterial are not directly saved in db
        em.detach(updatedOrderMaterial)
        updatedOrderMaterial.materialSummary = UPDATED_MATERIAL_SUMMARY
        updatedOrderMaterial.createdDate = UPDATED_CREATED_DATE
        updatedOrderMaterial.materialQuantity = UPDATED_MATERIAL_QUANTITY
        updatedOrderMaterial.measureUnit = UPDATED_MEASURE_UNIT
        val orderMaterialValueObject = orderMaterialMapper.toDto(updatedOrderMaterial)

        restOrderMaterialMockMvc.perform(
            put("/api/order-materials")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderMaterialValueObject))
        ).andExpect(status().isOk)

        // Validate the OrderMaterial in the database
        val orderMaterialList = orderMaterialRepository.findAll()
        assertThat(orderMaterialList).hasSize(databaseSizeBeforeUpdate)
        val testOrderMaterial = orderMaterialList[orderMaterialList.size - 1]
        assertThat(testOrderMaterial.materialSummary).isEqualTo(UPDATED_MATERIAL_SUMMARY)
        assertThat(testOrderMaterial.createdDate).isEqualTo(UPDATED_CREATED_DATE)
        assertThat(testOrderMaterial.materialQuantity).isEqualTo(UPDATED_MATERIAL_QUANTITY)
        assertThat(testOrderMaterial.measureUnit).isEqualTo(UPDATED_MEASURE_UNIT)
    }

    @Test
    @Transactional
    fun updateNonExistingOrderMaterial() {
        val databaseSizeBeforeUpdate = orderMaterialRepository.findAll().size

        // Create the OrderMaterial
        val orderMaterialValueObject = orderMaterialMapper.toDto(orderMaterialModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMaterialMockMvc.perform(
            put("/api/order-materials")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderMaterialValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the OrderMaterial in the database
        val orderMaterialList = orderMaterialRepository.findAll()
        assertThat(orderMaterialList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteOrderMaterial() {
        // Initialize the database
        orderMaterialRepository.saveAndFlush(orderMaterialModel)

        val databaseSizeBeforeDelete = orderMaterialRepository.findAll().size

        val id = orderMaterialModel.id
        assertNotNull(id)

        // Delete the orderMaterial
        restOrderMaterialMockMvc.perform(
            delete("/api/order-materials/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val orderMaterialList = orderMaterialRepository.findAll()
        assertThat(orderMaterialList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(OrderMaterialModel::class.java)
        val orderMaterialModel1 = OrderMaterialModel()
        orderMaterialModel1.id = 1L
        val orderMaterialModel2 = OrderMaterialModel()
        orderMaterialModel2.id = orderMaterialModel1.id
        assertThat(orderMaterialModel1).isEqualTo(orderMaterialModel2)
        orderMaterialModel2.id = 2L
        assertThat(orderMaterialModel1).isNotEqualTo(orderMaterialModel2)
        orderMaterialModel1.id = null
        assertThat(orderMaterialModel1).isNotEqualTo(orderMaterialModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(OrderMaterialValueObject::class.java)
        val orderMaterialValueObject1 = OrderMaterialValueObject()
        orderMaterialValueObject1.id = 1L
        val orderMaterialValueObject2 = OrderMaterialValueObject()
        assertThat(orderMaterialValueObject1).isNotEqualTo(orderMaterialValueObject2)
        orderMaterialValueObject2.id = orderMaterialValueObject1.id
        assertThat(orderMaterialValueObject1).isEqualTo(orderMaterialValueObject2)
        orderMaterialValueObject2.id = 2L
        assertThat(orderMaterialValueObject1).isNotEqualTo(orderMaterialValueObject2)
        orderMaterialValueObject1.id = null
        assertThat(orderMaterialValueObject1).isNotEqualTo(orderMaterialValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(orderMaterialMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(orderMaterialMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_MATERIAL_SUMMARY: String = "AAAAAAAAAA"
        private const val UPDATED_MATERIAL_SUMMARY = "BBBBBBBBBB"

        private val DEFAULT_CREATED_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_CREATED_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_MATERIAL_QUANTITY: Int = 0
        private const val UPDATED_MATERIAL_QUANTITY: Int = 1

        private val DEFAULT_MEASURE_UNIT: MeasureUnit = MeasureUnit.METER
        private val UPDATED_MEASURE_UNIT: MeasureUnit = MeasureUnit.SQUARE_METER
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): OrderMaterialModel {
            val orderMaterialModel = OrderMaterialModel()
            orderMaterialModel.materialSummary = DEFAULT_MATERIAL_SUMMARY
            orderMaterialModel.createdDate = DEFAULT_CREATED_DATE
            orderMaterialModel.materialQuantity = DEFAULT_MATERIAL_QUANTITY
            orderMaterialModel.measureUnit = DEFAULT_MEASURE_UNIT

        return orderMaterialModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): OrderMaterialModel {
            val orderMaterialModel = OrderMaterialModel()
            orderMaterialModel.materialSummary = UPDATED_MATERIAL_SUMMARY
            orderMaterialModel.createdDate = UPDATED_CREATED_DATE
            orderMaterialModel.materialQuantity = UPDATED_MATERIAL_QUANTITY
            orderMaterialModel.measureUnit = UPDATED_MEASURE_UNIT

        return orderMaterialModel
        }
    }
}
