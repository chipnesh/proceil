package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.MeasurementModel
import me.chipnesh.proceil.repository.MeasurementRepository
import me.chipnesh.proceil.service.MeasurementService
import me.chipnesh.proceil.service.dto.MeasurementValueObject
import me.chipnesh.proceil.service.mapper.MeasurementMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.MeasurementQueryService

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
 * Test class for the MeasurementResource REST controller.
 *
 * @see MeasurementResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class MeasurementResourceIT {

    @Autowired
    private lateinit var measurementRepository: MeasurementRepository

    @Autowired
    private lateinit var measurementMapper: MeasurementMapper

    @Autowired
    private lateinit var measurementService: MeasurementService

    @Autowired
    private lateinit var measurementQueryService: MeasurementQueryService

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

    private lateinit var restMeasurementMockMvc: MockMvc

    private lateinit var measurementModel: MeasurementModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val measurementResource = MeasurementResource(measurementService, measurementQueryService)
        this.restMeasurementMockMvc = MockMvcBuilders.standaloneSetup(measurementResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        measurementModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createMeasurement() {
        val databaseSizeBeforeCreate = measurementRepository.findAll().size

        // Create the Measurement
        val measurementValueObject = measurementMapper.toDto(measurementModel)
        restMeasurementMockMvc.perform(
            post("/api/measurements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(measurementValueObject))
        ).andExpect(status().isCreated)

        // Validate the Measurement in the database
        val measurementList = measurementRepository.findAll()
        assertThat(measurementList).hasSize(databaseSizeBeforeCreate + 1)
        val testMeasurement = measurementList[measurementList.size - 1]
        assertThat(testMeasurement.measurementSummary).isEqualTo(DEFAULT_MEASUREMENT_SUMMARY)
        assertThat(testMeasurement.measureDate).isEqualTo(DEFAULT_MEASURE_DATE)
        assertThat(testMeasurement.measureNote).isEqualTo(DEFAULT_MEASURE_NOTE)
        assertThat(testMeasurement.measureAddress).isEqualTo(DEFAULT_MEASURE_ADDRESS)
    }

    @Test
    @Transactional
    fun createMeasurementWithExistingId() {
        val databaseSizeBeforeCreate = measurementRepository.findAll().size

        // Create the Measurement with an existing ID
        measurementModel.id = 1L
        val measurementValueObject = measurementMapper.toDto(measurementModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restMeasurementMockMvc.perform(
            post("/api/measurements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(measurementValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Measurement in the database
        val measurementList = measurementRepository.findAll()
        assertThat(measurementList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllMeasurements() {
        // Initialize the database
        measurementRepository.saveAndFlush(measurementModel)

        // Get all the measurementList
        restMeasurementMockMvc.perform(get("/api/measurements?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(measurementModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].measurementSummary").value(hasItem(DEFAULT_MEASUREMENT_SUMMARY)))
            .andExpect(jsonPath("$.[*].measureDate").value(hasItem(DEFAULT_MEASURE_DATE.toString())))
            .andExpect(jsonPath("$.[*].measureNote").value(hasItem(DEFAULT_MEASURE_NOTE.toString())))
            .andExpect(jsonPath("$.[*].measureAddress").value(hasItem(DEFAULT_MEASURE_ADDRESS)))
    }

    @Test
    @Transactional
    fun getMeasurement() {
        // Initialize the database
        measurementRepository.saveAndFlush(measurementModel)

        val id = measurementModel.id
        assertNotNull(id)

        // Get the measurement
        restMeasurementMockMvc.perform(get("/api/measurements/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.measurementSummary").value(DEFAULT_MEASUREMENT_SUMMARY))
            .andExpect(jsonPath("$.measureDate").value(DEFAULT_MEASURE_DATE.toString()))
            .andExpect(jsonPath("$.measureNote").value(DEFAULT_MEASURE_NOTE.toString()))
            .andExpect(jsonPath("$.measureAddress").value(DEFAULT_MEASURE_ADDRESS))
    }

    @Test
    @Transactional
    fun getAllMeasurementsByMeasurementSummaryIsEqualToSomething() {
        // Initialize the database
        measurementRepository.saveAndFlush(measurementModel)

        // Get all the measurementList where measurementSummary equals to DEFAULT_MEASUREMENT_SUMMARY
        defaultMeasurementShouldBeFound("measurementSummary.equals=$DEFAULT_MEASUREMENT_SUMMARY")

        // Get all the measurementList where measurementSummary equals to UPDATED_MEASUREMENT_SUMMARY
        defaultMeasurementShouldNotBeFound("measurementSummary.equals=$UPDATED_MEASUREMENT_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllMeasurementsByMeasurementSummaryIsInShouldWork() {
        // Initialize the database
        measurementRepository.saveAndFlush(measurementModel)

        // Get all the measurementList where measurementSummary in DEFAULT_MEASUREMENT_SUMMARY or UPDATED_MEASUREMENT_SUMMARY
        defaultMeasurementShouldBeFound("measurementSummary.in=$DEFAULT_MEASUREMENT_SUMMARY,$UPDATED_MEASUREMENT_SUMMARY")

        // Get all the measurementList where measurementSummary equals to UPDATED_MEASUREMENT_SUMMARY
        defaultMeasurementShouldNotBeFound("measurementSummary.in=$UPDATED_MEASUREMENT_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllMeasurementsByMeasurementSummaryIsNullOrNotNull() {
        // Initialize the database
        measurementRepository.saveAndFlush(measurementModel)

        // Get all the measurementList where measurementSummary is not null
        defaultMeasurementShouldBeFound("measurementSummary.specified=true")

        // Get all the measurementList where measurementSummary is null
        defaultMeasurementShouldNotBeFound("measurementSummary.specified=false")
    }

    @Test
    @Transactional
    fun getAllMeasurementsByMeasureDateIsEqualToSomething() {
        // Initialize the database
        measurementRepository.saveAndFlush(measurementModel)

        // Get all the measurementList where measureDate equals to DEFAULT_MEASURE_DATE
        defaultMeasurementShouldBeFound("measureDate.equals=$DEFAULT_MEASURE_DATE")

        // Get all the measurementList where measureDate equals to UPDATED_MEASURE_DATE
        defaultMeasurementShouldNotBeFound("measureDate.equals=$UPDATED_MEASURE_DATE")
    }

    @Test
    @Transactional
    fun getAllMeasurementsByMeasureDateIsInShouldWork() {
        // Initialize the database
        measurementRepository.saveAndFlush(measurementModel)

        // Get all the measurementList where measureDate in DEFAULT_MEASURE_DATE or UPDATED_MEASURE_DATE
        defaultMeasurementShouldBeFound("measureDate.in=$DEFAULT_MEASURE_DATE,$UPDATED_MEASURE_DATE")

        // Get all the measurementList where measureDate equals to UPDATED_MEASURE_DATE
        defaultMeasurementShouldNotBeFound("measureDate.in=$UPDATED_MEASURE_DATE")
    }

    @Test
    @Transactional
    fun getAllMeasurementsByMeasureDateIsNullOrNotNull() {
        // Initialize the database
        measurementRepository.saveAndFlush(measurementModel)

        // Get all the measurementList where measureDate is not null
        defaultMeasurementShouldBeFound("measureDate.specified=true")

        // Get all the measurementList where measureDate is null
        defaultMeasurementShouldNotBeFound("measureDate.specified=false")
    }

    @Test
    @Transactional
    fun getAllMeasurementsByMeasureAddressIsEqualToSomething() {
        // Initialize the database
        measurementRepository.saveAndFlush(measurementModel)

        // Get all the measurementList where measureAddress equals to DEFAULT_MEASURE_ADDRESS
        defaultMeasurementShouldBeFound("measureAddress.equals=$DEFAULT_MEASURE_ADDRESS")

        // Get all the measurementList where measureAddress equals to UPDATED_MEASURE_ADDRESS
        defaultMeasurementShouldNotBeFound("measureAddress.equals=$UPDATED_MEASURE_ADDRESS")
    }

    @Test
    @Transactional
    fun getAllMeasurementsByMeasureAddressIsInShouldWork() {
        // Initialize the database
        measurementRepository.saveAndFlush(measurementModel)

        // Get all the measurementList where measureAddress in DEFAULT_MEASURE_ADDRESS or UPDATED_MEASURE_ADDRESS
        defaultMeasurementShouldBeFound("measureAddress.in=$DEFAULT_MEASURE_ADDRESS,$UPDATED_MEASURE_ADDRESS")

        // Get all the measurementList where measureAddress equals to UPDATED_MEASURE_ADDRESS
        defaultMeasurementShouldNotBeFound("measureAddress.in=$UPDATED_MEASURE_ADDRESS")
    }

    @Test
    @Transactional
    fun getAllMeasurementsByMeasureAddressIsNullOrNotNull() {
        // Initialize the database
        measurementRepository.saveAndFlush(measurementModel)

        // Get all the measurementList where measureAddress is not null
        defaultMeasurementShouldBeFound("measureAddress.specified=true")

        // Get all the measurementList where measureAddress is null
        defaultMeasurementShouldNotBeFound("measureAddress.specified=false")
    }

    @Test
    @Transactional
    fun getAllMeasurementsByMaterialsIsEqualToSomething() {
        // Initialize the database
        val materials = MaterialMeasurementResourceIT.createEntity(em)
        em.persist(materials)
        em.flush()
        measurementModel.addMaterials(materials)
        measurementRepository.saveAndFlush(measurementModel)
        val materialsId = materials.id

        // Get all the measurementList where materials equals to materialsId
        defaultMeasurementShouldBeFound("materialsId.equals=$materialsId")

        // Get all the measurementList where materials equals to materialsId + 1
        defaultMeasurementShouldNotBeFound("materialsId.equals=${materialsId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllMeasurementsByWorkerIsEqualToSomething() {
        // Initialize the database
        val worker = EmployeeResourceIT.createEntity(em)
        em.persist(worker)
        em.flush()
        measurementModel.worker = worker
        measurementRepository.saveAndFlush(measurementModel)
        val workerId = worker.id

        // Get all the measurementList where worker equals to workerId
        defaultMeasurementShouldBeFound("workerId.equals=$workerId")

        // Get all the measurementList where worker equals to workerId + 1
        defaultMeasurementShouldNotBeFound("workerId.equals=${workerId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllMeasurementsByClientIsEqualToSomething() {
        // Initialize the database
        val client = CustomerResourceIT.createEntity(em)
        em.persist(client)
        em.flush()
        measurementModel.client = client
        measurementRepository.saveAndFlush(measurementModel)
        val clientId = client.id

        // Get all the measurementList where client equals to clientId
        defaultMeasurementShouldBeFound("clientId.equals=$clientId")

        // Get all the measurementList where client equals to clientId + 1
        defaultMeasurementShouldNotBeFound("clientId.equals=${clientId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultMeasurementShouldBeFound(filter: String) {
        restMeasurementMockMvc.perform(get("/api/measurements?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(measurementModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].measurementSummary").value(hasItem(DEFAULT_MEASUREMENT_SUMMARY)))
            .andExpect(jsonPath("$.[*].measureDate").value(hasItem(DEFAULT_MEASURE_DATE.toString())))
            .andExpect(jsonPath("$.[*].measureNote").value(hasItem(DEFAULT_MEASURE_NOTE.toString())))
            .andExpect(jsonPath("$.[*].measureAddress").value(hasItem(DEFAULT_MEASURE_ADDRESS)))

        // Check, that the count call also returns 1
        restMeasurementMockMvc.perform(get("/api/measurements/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultMeasurementShouldNotBeFound(filter: String) {
        restMeasurementMockMvc.perform(get("/api/measurements?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restMeasurementMockMvc.perform(get("/api/measurements/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingMeasurement() {
        // Get the measurement
        restMeasurementMockMvc.perform(get("/api/measurements/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateMeasurement() {
        // Initialize the database
        measurementRepository.saveAndFlush(measurementModel)

        val databaseSizeBeforeUpdate = measurementRepository.findAll().size

        // Update the measurement
        val id = measurementModel.id
        assertNotNull(id)
        val updatedMeasurement = measurementRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedMeasurement are not directly saved in db
        em.detach(updatedMeasurement)
        updatedMeasurement.measurementSummary = UPDATED_MEASUREMENT_SUMMARY
        updatedMeasurement.measureDate = UPDATED_MEASURE_DATE
        updatedMeasurement.measureNote = UPDATED_MEASURE_NOTE
        updatedMeasurement.measureAddress = UPDATED_MEASURE_ADDRESS
        val measurementValueObject = measurementMapper.toDto(updatedMeasurement)

        restMeasurementMockMvc.perform(
            put("/api/measurements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(measurementValueObject))
        ).andExpect(status().isOk)

        // Validate the Measurement in the database
        val measurementList = measurementRepository.findAll()
        assertThat(measurementList).hasSize(databaseSizeBeforeUpdate)
        val testMeasurement = measurementList[measurementList.size - 1]
        assertThat(testMeasurement.measurementSummary).isEqualTo(UPDATED_MEASUREMENT_SUMMARY)
        assertThat(testMeasurement.measureDate).isEqualTo(UPDATED_MEASURE_DATE)
        assertThat(testMeasurement.measureNote).isEqualTo(UPDATED_MEASURE_NOTE)
        assertThat(testMeasurement.measureAddress).isEqualTo(UPDATED_MEASURE_ADDRESS)
    }

    @Test
    @Transactional
    fun updateNonExistingMeasurement() {
        val databaseSizeBeforeUpdate = measurementRepository.findAll().size

        // Create the Measurement
        val measurementValueObject = measurementMapper.toDto(measurementModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMeasurementMockMvc.perform(
            put("/api/measurements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(measurementValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Measurement in the database
        val measurementList = measurementRepository.findAll()
        assertThat(measurementList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteMeasurement() {
        // Initialize the database
        measurementRepository.saveAndFlush(measurementModel)

        val databaseSizeBeforeDelete = measurementRepository.findAll().size

        val id = measurementModel.id
        assertNotNull(id)

        // Delete the measurement
        restMeasurementMockMvc.perform(
            delete("/api/measurements/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val measurementList = measurementRepository.findAll()
        assertThat(measurementList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(MeasurementModel::class.java)
        val measurementModel1 = MeasurementModel()
        measurementModel1.id = 1L
        val measurementModel2 = MeasurementModel()
        measurementModel2.id = measurementModel1.id
        assertThat(measurementModel1).isEqualTo(measurementModel2)
        measurementModel2.id = 2L
        assertThat(measurementModel1).isNotEqualTo(measurementModel2)
        measurementModel1.id = null
        assertThat(measurementModel1).isNotEqualTo(measurementModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(MeasurementValueObject::class.java)
        val measurementValueObject1 = MeasurementValueObject()
        measurementValueObject1.id = 1L
        val measurementValueObject2 = MeasurementValueObject()
        assertThat(measurementValueObject1).isNotEqualTo(measurementValueObject2)
        measurementValueObject2.id = measurementValueObject1.id
        assertThat(measurementValueObject1).isEqualTo(measurementValueObject2)
        measurementValueObject2.id = 2L
        assertThat(measurementValueObject1).isNotEqualTo(measurementValueObject2)
        measurementValueObject1.id = null
        assertThat(measurementValueObject1).isNotEqualTo(measurementValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(measurementMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(measurementMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_MEASUREMENT_SUMMARY: String = "AAAAAAAAAA"
        private const val UPDATED_MEASUREMENT_SUMMARY = "BBBBBBBBBB"

        private val DEFAULT_MEASURE_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_MEASURE_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_MEASURE_NOTE: String = "AAAAAAAAAA"
        private const val UPDATED_MEASURE_NOTE = "BBBBBBBBBB"

        private const val DEFAULT_MEASURE_ADDRESS: String = "AAAAAAAAAA"
        private const val UPDATED_MEASURE_ADDRESS = "BBBBBBBBBB"
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): MeasurementModel {
            val measurementModel = MeasurementModel()
            measurementModel.measurementSummary = DEFAULT_MEASUREMENT_SUMMARY
            measurementModel.measureDate = DEFAULT_MEASURE_DATE
            measurementModel.measureNote = DEFAULT_MEASURE_NOTE
            measurementModel.measureAddress = DEFAULT_MEASURE_ADDRESS

        return measurementModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): MeasurementModel {
            val measurementModel = MeasurementModel()
            measurementModel.measurementSummary = UPDATED_MEASUREMENT_SUMMARY
            measurementModel.measureDate = UPDATED_MEASURE_DATE
            measurementModel.measureNote = UPDATED_MEASURE_NOTE
            measurementModel.measureAddress = UPDATED_MEASURE_ADDRESS

        return measurementModel
        }
    }
}
