package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.MaterialMeasurementModel
import me.chipnesh.proceil.repository.MaterialMeasurementRepository
import me.chipnesh.proceil.service.MaterialMeasurementService
import me.chipnesh.proceil.service.dto.MaterialMeasurementValueObject
import me.chipnesh.proceil.service.mapper.MaterialMeasurementMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.MaterialMeasurementQueryService

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

import me.chipnesh.proceil.domain.enumeration.MeasureUnit
/**
 * Test class for the MaterialMeasurementResource REST controller.
 *
 * @see MaterialMeasurementResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class MaterialMeasurementResourceIT {

    @Autowired
    private lateinit var materialMeasurementRepository: MaterialMeasurementRepository

    @Autowired
    private lateinit var materialMeasurementMapper: MaterialMeasurementMapper

    @Autowired
    private lateinit var materialMeasurementService: MaterialMeasurementService

    @Autowired
    private lateinit var materialMeasurementQueryService: MaterialMeasurementQueryService

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

    private lateinit var restMaterialMeasurementMockMvc: MockMvc

    private lateinit var materialMeasurementModel: MaterialMeasurementModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val materialMeasurementResource = MaterialMeasurementResource(materialMeasurementService, materialMeasurementQueryService)
        this.restMaterialMeasurementMockMvc = MockMvcBuilders.standaloneSetup(materialMeasurementResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        materialMeasurementModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createMaterialMeasurement() {
        val databaseSizeBeforeCreate = materialMeasurementRepository.findAll().size

        // Create the MaterialMeasurement
        val materialMeasurementValueObject = materialMeasurementMapper.toDto(materialMeasurementModel)
        restMaterialMeasurementMockMvc.perform(
            post("/api/material-measurements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialMeasurementValueObject))
        ).andExpect(status().isCreated)

        // Validate the MaterialMeasurement in the database
        val materialMeasurementList = materialMeasurementRepository.findAll()
        assertThat(materialMeasurementList).hasSize(databaseSizeBeforeCreate + 1)
        val testMaterialMeasurement = materialMeasurementList[materialMeasurementList.size - 1]
        assertThat(testMaterialMeasurement.measurementSummary).isEqualTo(DEFAULT_MEASUREMENT_SUMMARY)
        assertThat(testMaterialMeasurement.measurementValue).isEqualTo(DEFAULT_MEASUREMENT_VALUE)
        assertThat(testMaterialMeasurement.measureUnit).isEqualTo(DEFAULT_MEASURE_UNIT)
    }

    @Test
    @Transactional
    fun createMaterialMeasurementWithExistingId() {
        val databaseSizeBeforeCreate = materialMeasurementRepository.findAll().size

        // Create the MaterialMeasurement with an existing ID
        materialMeasurementModel.id = 1L
        val materialMeasurementValueObject = materialMeasurementMapper.toDto(materialMeasurementModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restMaterialMeasurementMockMvc.perform(
            post("/api/material-measurements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialMeasurementValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the MaterialMeasurement in the database
        val materialMeasurementList = materialMeasurementRepository.findAll()
        assertThat(materialMeasurementList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllMaterialMeasurements() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        // Get all the materialMeasurementList
        restMaterialMeasurementMockMvc.perform(get("/api/material-measurements?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(materialMeasurementModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].measurementSummary").value(hasItem(DEFAULT_MEASUREMENT_SUMMARY)))
            .andExpect(jsonPath("$.[*].measurementValue").value(hasItem(DEFAULT_MEASUREMENT_VALUE)))
            .andExpect(jsonPath("$.[*].measureUnit").value(hasItem(DEFAULT_MEASURE_UNIT.toString())))
    }

    @Test
    @Transactional
    fun getMaterialMeasurement() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        val id = materialMeasurementModel.id
        assertNotNull(id)

        // Get the materialMeasurement
        restMaterialMeasurementMockMvc.perform(get("/api/material-measurements/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.measurementSummary").value(DEFAULT_MEASUREMENT_SUMMARY))
            .andExpect(jsonPath("$.measurementValue").value(DEFAULT_MEASUREMENT_VALUE))
            .andExpect(jsonPath("$.measureUnit").value(DEFAULT_MEASURE_UNIT.toString()))
    }

    @Test
    @Transactional
    fun getAllMaterialMeasurementsByMeasurementSummaryIsEqualToSomething() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        // Get all the materialMeasurementList where measurementSummary equals to DEFAULT_MEASUREMENT_SUMMARY
        defaultMaterialMeasurementShouldBeFound("measurementSummary.equals=$DEFAULT_MEASUREMENT_SUMMARY")

        // Get all the materialMeasurementList where measurementSummary equals to UPDATED_MEASUREMENT_SUMMARY
        defaultMaterialMeasurementShouldNotBeFound("measurementSummary.equals=$UPDATED_MEASUREMENT_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllMaterialMeasurementsByMeasurementSummaryIsInShouldWork() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        // Get all the materialMeasurementList where measurementSummary in DEFAULT_MEASUREMENT_SUMMARY or UPDATED_MEASUREMENT_SUMMARY
        defaultMaterialMeasurementShouldBeFound("measurementSummary.in=$DEFAULT_MEASUREMENT_SUMMARY,$UPDATED_MEASUREMENT_SUMMARY")

        // Get all the materialMeasurementList where measurementSummary equals to UPDATED_MEASUREMENT_SUMMARY
        defaultMaterialMeasurementShouldNotBeFound("measurementSummary.in=$UPDATED_MEASUREMENT_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllMaterialMeasurementsByMeasurementSummaryIsNullOrNotNull() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        // Get all the materialMeasurementList where measurementSummary is not null
        defaultMaterialMeasurementShouldBeFound("measurementSummary.specified=true")

        // Get all the materialMeasurementList where measurementSummary is null
        defaultMaterialMeasurementShouldNotBeFound("measurementSummary.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialMeasurementsByMeasurementValueIsEqualToSomething() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        // Get all the materialMeasurementList where measurementValue equals to DEFAULT_MEASUREMENT_VALUE
        defaultMaterialMeasurementShouldBeFound("measurementValue.equals=$DEFAULT_MEASUREMENT_VALUE")

        // Get all the materialMeasurementList where measurementValue equals to UPDATED_MEASUREMENT_VALUE
        defaultMaterialMeasurementShouldNotBeFound("measurementValue.equals=$UPDATED_MEASUREMENT_VALUE")
    }

    @Test
    @Transactional
    fun getAllMaterialMeasurementsByMeasurementValueIsInShouldWork() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        // Get all the materialMeasurementList where measurementValue in DEFAULT_MEASUREMENT_VALUE or UPDATED_MEASUREMENT_VALUE
        defaultMaterialMeasurementShouldBeFound("measurementValue.in=$DEFAULT_MEASUREMENT_VALUE,$UPDATED_MEASUREMENT_VALUE")

        // Get all the materialMeasurementList where measurementValue equals to UPDATED_MEASUREMENT_VALUE
        defaultMaterialMeasurementShouldNotBeFound("measurementValue.in=$UPDATED_MEASUREMENT_VALUE")
    }

    @Test
    @Transactional
    fun getAllMaterialMeasurementsByMeasurementValueIsNullOrNotNull() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        // Get all the materialMeasurementList where measurementValue is not null
        defaultMaterialMeasurementShouldBeFound("measurementValue.specified=true")

        // Get all the materialMeasurementList where measurementValue is null
        defaultMaterialMeasurementShouldNotBeFound("measurementValue.specified=false")
    }
    @Test
    @Transactional
    fun getAllMaterialMeasurementsByMeasurementValueIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        // Get all the materialMeasurementList where measurementValue greater than or equals to DEFAULT_MEASUREMENT_VALUE
        defaultMaterialMeasurementShouldBeFound("measurementValue.greaterOrEqualThan=$DEFAULT_MEASUREMENT_VALUE")

        // Get all the materialMeasurementList where measurementValue greater than or equals to UPDATED_MEASUREMENT_VALUE
        defaultMaterialMeasurementShouldNotBeFound("measurementValue.greaterOrEqualThan=$UPDATED_MEASUREMENT_VALUE")
    }

    @Test
    @Transactional
    fun getAllMaterialMeasurementsByMeasurementValueIsLessThanSomething() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        // Get all the materialMeasurementList where measurementValue less than or equals to DEFAULT_MEASUREMENT_VALUE
        defaultMaterialMeasurementShouldNotBeFound("measurementValue.lessThan=$DEFAULT_MEASUREMENT_VALUE")

        // Get all the materialMeasurementList where measurementValue less than or equals to UPDATED_MEASUREMENT_VALUE
        defaultMaterialMeasurementShouldBeFound("measurementValue.lessThan=$UPDATED_MEASUREMENT_VALUE")
    }

    @Test
    @Transactional
    fun getAllMaterialMeasurementsByMeasureUnitIsEqualToSomething() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        // Get all the materialMeasurementList where measureUnit equals to DEFAULT_MEASURE_UNIT
        defaultMaterialMeasurementShouldBeFound("measureUnit.equals=$DEFAULT_MEASURE_UNIT")

        // Get all the materialMeasurementList where measureUnit equals to UPDATED_MEASURE_UNIT
        defaultMaterialMeasurementShouldNotBeFound("measureUnit.equals=$UPDATED_MEASURE_UNIT")
    }

    @Test
    @Transactional
    fun getAllMaterialMeasurementsByMeasureUnitIsInShouldWork() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        // Get all the materialMeasurementList where measureUnit in DEFAULT_MEASURE_UNIT or UPDATED_MEASURE_UNIT
        defaultMaterialMeasurementShouldBeFound("measureUnit.in=$DEFAULT_MEASURE_UNIT,$UPDATED_MEASURE_UNIT")

        // Get all the materialMeasurementList where measureUnit equals to UPDATED_MEASURE_UNIT
        defaultMaterialMeasurementShouldNotBeFound("measureUnit.in=$UPDATED_MEASURE_UNIT")
    }

    @Test
    @Transactional
    fun getAllMaterialMeasurementsByMeasureUnitIsNullOrNotNull() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        // Get all the materialMeasurementList where measureUnit is not null
        defaultMaterialMeasurementShouldBeFound("measureUnit.specified=true")

        // Get all the materialMeasurementList where measureUnit is null
        defaultMaterialMeasurementShouldNotBeFound("measureUnit.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialMeasurementsByMaterialIsEqualToSomething() {
        // Initialize the database
        val material = MaterialResourceIT.createEntity(em)
        em.persist(material)
        em.flush()
        materialMeasurementModel.material = material
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)
        val materialId = material.id

        // Get all the materialMeasurementList where material equals to materialId
        defaultMaterialMeasurementShouldBeFound("materialId.equals=$materialId")

        // Get all the materialMeasurementList where material equals to materialId + 1
        defaultMaterialMeasurementShouldNotBeFound("materialId.equals=${materialId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllMaterialMeasurementsByMeasurementIsEqualToSomething() {
        // Initialize the database
        val measurement = MeasurementResourceIT.createEntity(em)
        em.persist(measurement)
        em.flush()
        materialMeasurementModel.measurement = measurement
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)
        val measurementId = measurement.id

        // Get all the materialMeasurementList where measurement equals to measurementId
        defaultMaterialMeasurementShouldBeFound("measurementId.equals=$measurementId")

        // Get all the materialMeasurementList where measurement equals to measurementId + 1
        defaultMaterialMeasurementShouldNotBeFound("measurementId.equals=${measurementId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultMaterialMeasurementShouldBeFound(filter: String) {
        restMaterialMeasurementMockMvc.perform(get("/api/material-measurements?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(materialMeasurementModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].measurementSummary").value(hasItem(DEFAULT_MEASUREMENT_SUMMARY)))
            .andExpect(jsonPath("$.[*].measurementValue").value(hasItem(DEFAULT_MEASUREMENT_VALUE)))
            .andExpect(jsonPath("$.[*].measureUnit").value(hasItem(DEFAULT_MEASURE_UNIT.toString())))

        // Check, that the count call also returns 1
        restMaterialMeasurementMockMvc.perform(get("/api/material-measurements/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultMaterialMeasurementShouldNotBeFound(filter: String) {
        restMaterialMeasurementMockMvc.perform(get("/api/material-measurements?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restMaterialMeasurementMockMvc.perform(get("/api/material-measurements/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingMaterialMeasurement() {
        // Get the materialMeasurement
        restMaterialMeasurementMockMvc.perform(get("/api/material-measurements/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateMaterialMeasurement() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        val databaseSizeBeforeUpdate = materialMeasurementRepository.findAll().size

        // Update the materialMeasurement
        val id = materialMeasurementModel.id
        assertNotNull(id)
        val updatedMaterialMeasurement = materialMeasurementRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedMaterialMeasurement are not directly saved in db
        em.detach(updatedMaterialMeasurement)
        updatedMaterialMeasurement.measurementSummary = UPDATED_MEASUREMENT_SUMMARY
        updatedMaterialMeasurement.measurementValue = UPDATED_MEASUREMENT_VALUE
        updatedMaterialMeasurement.measureUnit = UPDATED_MEASURE_UNIT
        val materialMeasurementValueObject = materialMeasurementMapper.toDto(updatedMaterialMeasurement)

        restMaterialMeasurementMockMvc.perform(
            put("/api/material-measurements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialMeasurementValueObject))
        ).andExpect(status().isOk)

        // Validate the MaterialMeasurement in the database
        val materialMeasurementList = materialMeasurementRepository.findAll()
        assertThat(materialMeasurementList).hasSize(databaseSizeBeforeUpdate)
        val testMaterialMeasurement = materialMeasurementList[materialMeasurementList.size - 1]
        assertThat(testMaterialMeasurement.measurementSummary).isEqualTo(UPDATED_MEASUREMENT_SUMMARY)
        assertThat(testMaterialMeasurement.measurementValue).isEqualTo(UPDATED_MEASUREMENT_VALUE)
        assertThat(testMaterialMeasurement.measureUnit).isEqualTo(UPDATED_MEASURE_UNIT)
    }

    @Test
    @Transactional
    fun updateNonExistingMaterialMeasurement() {
        val databaseSizeBeforeUpdate = materialMeasurementRepository.findAll().size

        // Create the MaterialMeasurement
        val materialMeasurementValueObject = materialMeasurementMapper.toDto(materialMeasurementModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaterialMeasurementMockMvc.perform(
            put("/api/material-measurements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialMeasurementValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the MaterialMeasurement in the database
        val materialMeasurementList = materialMeasurementRepository.findAll()
        assertThat(materialMeasurementList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteMaterialMeasurement() {
        // Initialize the database
        materialMeasurementRepository.saveAndFlush(materialMeasurementModel)

        val databaseSizeBeforeDelete = materialMeasurementRepository.findAll().size

        val id = materialMeasurementModel.id
        assertNotNull(id)

        // Delete the materialMeasurement
        restMaterialMeasurementMockMvc.perform(
            delete("/api/material-measurements/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val materialMeasurementList = materialMeasurementRepository.findAll()
        assertThat(materialMeasurementList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(MaterialMeasurementModel::class.java)
        val materialMeasurementModel1 = MaterialMeasurementModel()
        materialMeasurementModel1.id = 1L
        val materialMeasurementModel2 = MaterialMeasurementModel()
        materialMeasurementModel2.id = materialMeasurementModel1.id
        assertThat(materialMeasurementModel1).isEqualTo(materialMeasurementModel2)
        materialMeasurementModel2.id = 2L
        assertThat(materialMeasurementModel1).isNotEqualTo(materialMeasurementModel2)
        materialMeasurementModel1.id = null
        assertThat(materialMeasurementModel1).isNotEqualTo(materialMeasurementModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(MaterialMeasurementValueObject::class.java)
        val materialMeasurementValueObject1 = MaterialMeasurementValueObject()
        materialMeasurementValueObject1.id = 1L
        val materialMeasurementValueObject2 = MaterialMeasurementValueObject()
        assertThat(materialMeasurementValueObject1).isNotEqualTo(materialMeasurementValueObject2)
        materialMeasurementValueObject2.id = materialMeasurementValueObject1.id
        assertThat(materialMeasurementValueObject1).isEqualTo(materialMeasurementValueObject2)
        materialMeasurementValueObject2.id = 2L
        assertThat(materialMeasurementValueObject1).isNotEqualTo(materialMeasurementValueObject2)
        materialMeasurementValueObject1.id = null
        assertThat(materialMeasurementValueObject1).isNotEqualTo(materialMeasurementValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(materialMeasurementMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(materialMeasurementMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_MEASUREMENT_SUMMARY: String = "AAAAAAAAAA"
        private const val UPDATED_MEASUREMENT_SUMMARY = "BBBBBBBBBB"

        private const val DEFAULT_MEASUREMENT_VALUE: Int = 1
        private const val UPDATED_MEASUREMENT_VALUE: Int = 2

        private val DEFAULT_MEASURE_UNIT: MeasureUnit = MeasureUnit.METER
        private val UPDATED_MEASURE_UNIT: MeasureUnit = MeasureUnit.SQUARE_METER
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): MaterialMeasurementModel {
            val materialMeasurementModel = MaterialMeasurementModel()
            materialMeasurementModel.measurementSummary = DEFAULT_MEASUREMENT_SUMMARY
            materialMeasurementModel.measurementValue = DEFAULT_MEASUREMENT_VALUE
            materialMeasurementModel.measureUnit = DEFAULT_MEASURE_UNIT

        return materialMeasurementModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): MaterialMeasurementModel {
            val materialMeasurementModel = MaterialMeasurementModel()
            materialMeasurementModel.measurementSummary = UPDATED_MEASUREMENT_SUMMARY
            materialMeasurementModel.measurementValue = UPDATED_MEASUREMENT_VALUE
            materialMeasurementModel.measureUnit = UPDATED_MEASURE_UNIT

        return materialMeasurementModel
        }
    }
}
