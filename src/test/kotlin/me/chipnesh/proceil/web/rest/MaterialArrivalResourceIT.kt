package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.MaterialArrivalModel
import me.chipnesh.proceil.repository.MaterialArrivalRepository
import me.chipnesh.proceil.service.MaterialArrivalService
import me.chipnesh.proceil.service.dto.MaterialArrivalValueObject
import me.chipnesh.proceil.service.mapper.MaterialArrivalMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.MaterialArrivalQueryService

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
 * Test class for the MaterialArrivalResource REST controller.
 *
 * @see MaterialArrivalResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class MaterialArrivalResourceIT {

    @Autowired
    private lateinit var materialArrivalRepository: MaterialArrivalRepository

    @Autowired
    private lateinit var materialArrivalMapper: MaterialArrivalMapper

    @Autowired
    private lateinit var materialArrivalService: MaterialArrivalService

    @Autowired
    private lateinit var materialArrivalQueryService: MaterialArrivalQueryService

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

    private lateinit var restMaterialArrivalMockMvc: MockMvc

    private lateinit var materialArrivalModel: MaterialArrivalModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val materialArrivalResource = MaterialArrivalResource(materialArrivalService, materialArrivalQueryService)
        this.restMaterialArrivalMockMvc = MockMvcBuilders.standaloneSetup(materialArrivalResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        materialArrivalModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createMaterialArrival() {
        val databaseSizeBeforeCreate = materialArrivalRepository.findAll().size

        // Create the MaterialArrival
        val materialArrivalValueObject = materialArrivalMapper.toDto(materialArrivalModel)
        restMaterialArrivalMockMvc.perform(
            post("/api/material-arrivals")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialArrivalValueObject))
        ).andExpect(status().isCreated)

        // Validate the MaterialArrival in the database
        val materialArrivalList = materialArrivalRepository.findAll()
        assertThat(materialArrivalList).hasSize(databaseSizeBeforeCreate + 1)
        val testMaterialArrival = materialArrivalList[materialArrivalList.size - 1]
        assertThat(testMaterialArrival.arrivalSummary).isEqualTo(DEFAULT_ARRIVAL_SUMMARY)
        assertThat(testMaterialArrival.arrivalDate).isEqualTo(DEFAULT_ARRIVAL_DATE)
        assertThat(testMaterialArrival.arrivalNote).isEqualTo(DEFAULT_ARRIVAL_NOTE)
        assertThat(testMaterialArrival.arrivedQuantity).isEqualTo(DEFAULT_ARRIVED_QUANTITY)
        assertThat(testMaterialArrival.measureUnit).isEqualTo(DEFAULT_MEASURE_UNIT)
    }

    @Test
    @Transactional
    fun createMaterialArrivalWithExistingId() {
        val databaseSizeBeforeCreate = materialArrivalRepository.findAll().size

        // Create the MaterialArrival with an existing ID
        materialArrivalModel.id = 1L
        val materialArrivalValueObject = materialArrivalMapper.toDto(materialArrivalModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restMaterialArrivalMockMvc.perform(
            post("/api/material-arrivals")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialArrivalValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the MaterialArrival in the database
        val materialArrivalList = materialArrivalRepository.findAll()
        assertThat(materialArrivalList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllMaterialArrivals() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList
        restMaterialArrivalMockMvc.perform(get("/api/material-arrivals?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(materialArrivalModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].arrivalSummary").value(hasItem(DEFAULT_ARRIVAL_SUMMARY)))
            .andExpect(jsonPath("$.[*].arrivalDate").value(hasItem(DEFAULT_ARRIVAL_DATE.toString())))
            .andExpect(jsonPath("$.[*].arrivalNote").value(hasItem(DEFAULT_ARRIVAL_NOTE.toString())))
            .andExpect(jsonPath("$.[*].arrivedQuantity").value(hasItem(DEFAULT_ARRIVED_QUANTITY)))
            .andExpect(jsonPath("$.[*].measureUnit").value(hasItem(DEFAULT_MEASURE_UNIT.toString())))
    }

    @Test
    @Transactional
    fun getMaterialArrival() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        val id = materialArrivalModel.id
        assertNotNull(id)

        // Get the materialArrival
        restMaterialArrivalMockMvc.perform(get("/api/material-arrivals/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.arrivalSummary").value(DEFAULT_ARRIVAL_SUMMARY))
            .andExpect(jsonPath("$.arrivalDate").value(DEFAULT_ARRIVAL_DATE.toString()))
            .andExpect(jsonPath("$.arrivalNote").value(DEFAULT_ARRIVAL_NOTE.toString()))
            .andExpect(jsonPath("$.arrivedQuantity").value(DEFAULT_ARRIVED_QUANTITY))
            .andExpect(jsonPath("$.measureUnit").value(DEFAULT_MEASURE_UNIT.toString()))
    }

    @Test
    @Transactional
    fun getAllMaterialArrivalsByArrivalSummaryIsEqualToSomething() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList where arrivalSummary equals to DEFAULT_ARRIVAL_SUMMARY
        defaultMaterialArrivalShouldBeFound("arrivalSummary.equals=$DEFAULT_ARRIVAL_SUMMARY")

        // Get all the materialArrivalList where arrivalSummary equals to UPDATED_ARRIVAL_SUMMARY
        defaultMaterialArrivalShouldNotBeFound("arrivalSummary.equals=$UPDATED_ARRIVAL_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllMaterialArrivalsByArrivalSummaryIsInShouldWork() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList where arrivalSummary in DEFAULT_ARRIVAL_SUMMARY or UPDATED_ARRIVAL_SUMMARY
        defaultMaterialArrivalShouldBeFound("arrivalSummary.in=$DEFAULT_ARRIVAL_SUMMARY,$UPDATED_ARRIVAL_SUMMARY")

        // Get all the materialArrivalList where arrivalSummary equals to UPDATED_ARRIVAL_SUMMARY
        defaultMaterialArrivalShouldNotBeFound("arrivalSummary.in=$UPDATED_ARRIVAL_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllMaterialArrivalsByArrivalSummaryIsNullOrNotNull() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList where arrivalSummary is not null
        defaultMaterialArrivalShouldBeFound("arrivalSummary.specified=true")

        // Get all the materialArrivalList where arrivalSummary is null
        defaultMaterialArrivalShouldNotBeFound("arrivalSummary.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialArrivalsByArrivalDateIsEqualToSomething() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList where arrivalDate equals to DEFAULT_ARRIVAL_DATE
        defaultMaterialArrivalShouldBeFound("arrivalDate.equals=$DEFAULT_ARRIVAL_DATE")

        // Get all the materialArrivalList where arrivalDate equals to UPDATED_ARRIVAL_DATE
        defaultMaterialArrivalShouldNotBeFound("arrivalDate.equals=$UPDATED_ARRIVAL_DATE")
    }

    @Test
    @Transactional
    fun getAllMaterialArrivalsByArrivalDateIsInShouldWork() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList where arrivalDate in DEFAULT_ARRIVAL_DATE or UPDATED_ARRIVAL_DATE
        defaultMaterialArrivalShouldBeFound("arrivalDate.in=$DEFAULT_ARRIVAL_DATE,$UPDATED_ARRIVAL_DATE")

        // Get all the materialArrivalList where arrivalDate equals to UPDATED_ARRIVAL_DATE
        defaultMaterialArrivalShouldNotBeFound("arrivalDate.in=$UPDATED_ARRIVAL_DATE")
    }

    @Test
    @Transactional
    fun getAllMaterialArrivalsByArrivalDateIsNullOrNotNull() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList where arrivalDate is not null
        defaultMaterialArrivalShouldBeFound("arrivalDate.specified=true")

        // Get all the materialArrivalList where arrivalDate is null
        defaultMaterialArrivalShouldNotBeFound("arrivalDate.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialArrivalsByArrivedQuantityIsEqualToSomething() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList where arrivedQuantity equals to DEFAULT_ARRIVED_QUANTITY
        defaultMaterialArrivalShouldBeFound("arrivedQuantity.equals=$DEFAULT_ARRIVED_QUANTITY")

        // Get all the materialArrivalList where arrivedQuantity equals to UPDATED_ARRIVED_QUANTITY
        defaultMaterialArrivalShouldNotBeFound("arrivedQuantity.equals=$UPDATED_ARRIVED_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllMaterialArrivalsByArrivedQuantityIsInShouldWork() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList where arrivedQuantity in DEFAULT_ARRIVED_QUANTITY or UPDATED_ARRIVED_QUANTITY
        defaultMaterialArrivalShouldBeFound("arrivedQuantity.in=$DEFAULT_ARRIVED_QUANTITY,$UPDATED_ARRIVED_QUANTITY")

        // Get all the materialArrivalList where arrivedQuantity equals to UPDATED_ARRIVED_QUANTITY
        defaultMaterialArrivalShouldNotBeFound("arrivedQuantity.in=$UPDATED_ARRIVED_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllMaterialArrivalsByArrivedQuantityIsNullOrNotNull() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList where arrivedQuantity is not null
        defaultMaterialArrivalShouldBeFound("arrivedQuantity.specified=true")

        // Get all the materialArrivalList where arrivedQuantity is null
        defaultMaterialArrivalShouldNotBeFound("arrivedQuantity.specified=false")
    }
    @Test
    @Transactional
    fun getAllMaterialArrivalsByArrivedQuantityIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList where arrivedQuantity greater than or equals to DEFAULT_ARRIVED_QUANTITY
        defaultMaterialArrivalShouldBeFound("arrivedQuantity.greaterOrEqualThan=$DEFAULT_ARRIVED_QUANTITY")

        // Get all the materialArrivalList where arrivedQuantity greater than or equals to UPDATED_ARRIVED_QUANTITY
        defaultMaterialArrivalShouldNotBeFound("arrivedQuantity.greaterOrEqualThan=$UPDATED_ARRIVED_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllMaterialArrivalsByArrivedQuantityIsLessThanSomething() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList where arrivedQuantity less than or equals to DEFAULT_ARRIVED_QUANTITY
        defaultMaterialArrivalShouldNotBeFound("arrivedQuantity.lessThan=$DEFAULT_ARRIVED_QUANTITY")

        // Get all the materialArrivalList where arrivedQuantity less than or equals to UPDATED_ARRIVED_QUANTITY
        defaultMaterialArrivalShouldBeFound("arrivedQuantity.lessThan=$UPDATED_ARRIVED_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllMaterialArrivalsByMeasureUnitIsEqualToSomething() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList where measureUnit equals to DEFAULT_MEASURE_UNIT
        defaultMaterialArrivalShouldBeFound("measureUnit.equals=$DEFAULT_MEASURE_UNIT")

        // Get all the materialArrivalList where measureUnit equals to UPDATED_MEASURE_UNIT
        defaultMaterialArrivalShouldNotBeFound("measureUnit.equals=$UPDATED_MEASURE_UNIT")
    }

    @Test
    @Transactional
    fun getAllMaterialArrivalsByMeasureUnitIsInShouldWork() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList where measureUnit in DEFAULT_MEASURE_UNIT or UPDATED_MEASURE_UNIT
        defaultMaterialArrivalShouldBeFound("measureUnit.in=$DEFAULT_MEASURE_UNIT,$UPDATED_MEASURE_UNIT")

        // Get all the materialArrivalList where measureUnit equals to UPDATED_MEASURE_UNIT
        defaultMaterialArrivalShouldNotBeFound("measureUnit.in=$UPDATED_MEASURE_UNIT")
    }

    @Test
    @Transactional
    fun getAllMaterialArrivalsByMeasureUnitIsNullOrNotNull() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        // Get all the materialArrivalList where measureUnit is not null
        defaultMaterialArrivalShouldBeFound("measureUnit.specified=true")

        // Get all the materialArrivalList where measureUnit is null
        defaultMaterialArrivalShouldNotBeFound("measureUnit.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialArrivalsByRequestIsEqualToSomething() {
        // Initialize the database
        val request = MaterialRequestResourceIT.createEntity(em)
        em.persist(request)
        em.flush()
        materialArrivalModel.request = request
        materialArrivalRepository.saveAndFlush(materialArrivalModel)
        val requestId = request.id

        // Get all the materialArrivalList where request equals to requestId
        defaultMaterialArrivalShouldBeFound("requestId.equals=$requestId")

        // Get all the materialArrivalList where request equals to requestId + 1
        defaultMaterialArrivalShouldNotBeFound("requestId.equals=${requestId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultMaterialArrivalShouldBeFound(filter: String) {
        restMaterialArrivalMockMvc.perform(get("/api/material-arrivals?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(materialArrivalModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].arrivalSummary").value(hasItem(DEFAULT_ARRIVAL_SUMMARY)))
            .andExpect(jsonPath("$.[*].arrivalDate").value(hasItem(DEFAULT_ARRIVAL_DATE.toString())))
            .andExpect(jsonPath("$.[*].arrivalNote").value(hasItem(DEFAULT_ARRIVAL_NOTE.toString())))
            .andExpect(jsonPath("$.[*].arrivedQuantity").value(hasItem(DEFAULT_ARRIVED_QUANTITY)))
            .andExpect(jsonPath("$.[*].measureUnit").value(hasItem(DEFAULT_MEASURE_UNIT.toString())))

        // Check, that the count call also returns 1
        restMaterialArrivalMockMvc.perform(get("/api/material-arrivals/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultMaterialArrivalShouldNotBeFound(filter: String) {
        restMaterialArrivalMockMvc.perform(get("/api/material-arrivals?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restMaterialArrivalMockMvc.perform(get("/api/material-arrivals/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingMaterialArrival() {
        // Get the materialArrival
        restMaterialArrivalMockMvc.perform(get("/api/material-arrivals/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateMaterialArrival() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        val databaseSizeBeforeUpdate = materialArrivalRepository.findAll().size

        // Update the materialArrival
        val id = materialArrivalModel.id
        assertNotNull(id)
        val updatedMaterialArrival = materialArrivalRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedMaterialArrival are not directly saved in db
        em.detach(updatedMaterialArrival)
        updatedMaterialArrival.arrivalSummary = UPDATED_ARRIVAL_SUMMARY
        updatedMaterialArrival.arrivalDate = UPDATED_ARRIVAL_DATE
        updatedMaterialArrival.arrivalNote = UPDATED_ARRIVAL_NOTE
        updatedMaterialArrival.arrivedQuantity = UPDATED_ARRIVED_QUANTITY
        updatedMaterialArrival.measureUnit = UPDATED_MEASURE_UNIT
        val materialArrivalValueObject = materialArrivalMapper.toDto(updatedMaterialArrival)

        restMaterialArrivalMockMvc.perform(
            put("/api/material-arrivals")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialArrivalValueObject))
        ).andExpect(status().isOk)

        // Validate the MaterialArrival in the database
        val materialArrivalList = materialArrivalRepository.findAll()
        assertThat(materialArrivalList).hasSize(databaseSizeBeforeUpdate)
        val testMaterialArrival = materialArrivalList[materialArrivalList.size - 1]
        assertThat(testMaterialArrival.arrivalSummary).isEqualTo(UPDATED_ARRIVAL_SUMMARY)
        assertThat(testMaterialArrival.arrivalDate).isEqualTo(UPDATED_ARRIVAL_DATE)
        assertThat(testMaterialArrival.arrivalNote).isEqualTo(UPDATED_ARRIVAL_NOTE)
        assertThat(testMaterialArrival.arrivedQuantity).isEqualTo(UPDATED_ARRIVED_QUANTITY)
        assertThat(testMaterialArrival.measureUnit).isEqualTo(UPDATED_MEASURE_UNIT)
    }

    @Test
    @Transactional
    fun updateNonExistingMaterialArrival() {
        val databaseSizeBeforeUpdate = materialArrivalRepository.findAll().size

        // Create the MaterialArrival
        val materialArrivalValueObject = materialArrivalMapper.toDto(materialArrivalModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaterialArrivalMockMvc.perform(
            put("/api/material-arrivals")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialArrivalValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the MaterialArrival in the database
        val materialArrivalList = materialArrivalRepository.findAll()
        assertThat(materialArrivalList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteMaterialArrival() {
        // Initialize the database
        materialArrivalRepository.saveAndFlush(materialArrivalModel)

        val databaseSizeBeforeDelete = materialArrivalRepository.findAll().size

        val id = materialArrivalModel.id
        assertNotNull(id)

        // Delete the materialArrival
        restMaterialArrivalMockMvc.perform(
            delete("/api/material-arrivals/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val materialArrivalList = materialArrivalRepository.findAll()
        assertThat(materialArrivalList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(MaterialArrivalModel::class.java)
        val materialArrivalModel1 = MaterialArrivalModel()
        materialArrivalModel1.id = 1L
        val materialArrivalModel2 = MaterialArrivalModel()
        materialArrivalModel2.id = materialArrivalModel1.id
        assertThat(materialArrivalModel1).isEqualTo(materialArrivalModel2)
        materialArrivalModel2.id = 2L
        assertThat(materialArrivalModel1).isNotEqualTo(materialArrivalModel2)
        materialArrivalModel1.id = null
        assertThat(materialArrivalModel1).isNotEqualTo(materialArrivalModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(MaterialArrivalValueObject::class.java)
        val materialArrivalValueObject1 = MaterialArrivalValueObject()
        materialArrivalValueObject1.id = 1L
        val materialArrivalValueObject2 = MaterialArrivalValueObject()
        assertThat(materialArrivalValueObject1).isNotEqualTo(materialArrivalValueObject2)
        materialArrivalValueObject2.id = materialArrivalValueObject1.id
        assertThat(materialArrivalValueObject1).isEqualTo(materialArrivalValueObject2)
        materialArrivalValueObject2.id = 2L
        assertThat(materialArrivalValueObject1).isNotEqualTo(materialArrivalValueObject2)
        materialArrivalValueObject1.id = null
        assertThat(materialArrivalValueObject1).isNotEqualTo(materialArrivalValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(materialArrivalMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(materialArrivalMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_ARRIVAL_SUMMARY: String = "AAAAAAAAAA"
        private const val UPDATED_ARRIVAL_SUMMARY = "BBBBBBBBBB"

        private val DEFAULT_ARRIVAL_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_ARRIVAL_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_ARRIVAL_NOTE: String = "AAAAAAAAAA"
        private const val UPDATED_ARRIVAL_NOTE = "BBBBBBBBBB"

        private const val DEFAULT_ARRIVED_QUANTITY: Int = 1
        private const val UPDATED_ARRIVED_QUANTITY: Int = 2

        private val DEFAULT_MEASURE_UNIT: MeasureUnit = MeasureUnit.METER
        private val UPDATED_MEASURE_UNIT: MeasureUnit = MeasureUnit.SQUARE_METER
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): MaterialArrivalModel {
            val materialArrivalModel = MaterialArrivalModel()
            materialArrivalModel.arrivalSummary = DEFAULT_ARRIVAL_SUMMARY
            materialArrivalModel.arrivalDate = DEFAULT_ARRIVAL_DATE
            materialArrivalModel.arrivalNote = DEFAULT_ARRIVAL_NOTE
            materialArrivalModel.arrivedQuantity = DEFAULT_ARRIVED_QUANTITY
            materialArrivalModel.measureUnit = DEFAULT_MEASURE_UNIT

        return materialArrivalModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): MaterialArrivalModel {
            val materialArrivalModel = MaterialArrivalModel()
            materialArrivalModel.arrivalSummary = UPDATED_ARRIVAL_SUMMARY
            materialArrivalModel.arrivalDate = UPDATED_ARRIVAL_DATE
            materialArrivalModel.arrivalNote = UPDATED_ARRIVAL_NOTE
            materialArrivalModel.arrivedQuantity = UPDATED_ARRIVED_QUANTITY
            materialArrivalModel.measureUnit = UPDATED_MEASURE_UNIT

        return materialArrivalModel
        }
    }
}
