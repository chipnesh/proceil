package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.MaterialReserveModel
import me.chipnesh.proceil.repository.MaterialReserveRepository
import me.chipnesh.proceil.service.MaterialReserveService
import me.chipnesh.proceil.service.dto.MaterialReserveValueObject
import me.chipnesh.proceil.service.mapper.MaterialReserveMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.MaterialReserveQueryService

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

import me.chipnesh.proceil.domain.enumeration.MaterialReserveStatus
import me.chipnesh.proceil.domain.enumeration.MeasureUnit
/**
 * Test class for the MaterialReserveResource REST controller.
 *
 * @see MaterialReserveResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class MaterialReserveResourceIT {

    @Autowired
    private lateinit var materialReserveRepository: MaterialReserveRepository

    @Autowired
    private lateinit var materialReserveMapper: MaterialReserveMapper

    @Autowired
    private lateinit var materialReserveService: MaterialReserveService

    @Autowired
    private lateinit var materialReserveQueryService: MaterialReserveQueryService

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

    private lateinit var restMaterialReserveMockMvc: MockMvc

    private lateinit var materialReserveModel: MaterialReserveModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val materialReserveResource = MaterialReserveResource(materialReserveService, materialReserveQueryService)
        this.restMaterialReserveMockMvc = MockMvcBuilders.standaloneSetup(materialReserveResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        materialReserveModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createMaterialReserve() {
        val databaseSizeBeforeCreate = materialReserveRepository.findAll().size

        // Create the MaterialReserve
        val materialReserveValueObject = materialReserveMapper.toDto(materialReserveModel)
        restMaterialReserveMockMvc.perform(
            post("/api/material-reserves")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialReserveValueObject))
        ).andExpect(status().isCreated)

        // Validate the MaterialReserve in the database
        val materialReserveList = materialReserveRepository.findAll()
        assertThat(materialReserveList).hasSize(databaseSizeBeforeCreate + 1)
        val testMaterialReserve = materialReserveList[materialReserveList.size - 1]
        assertThat(testMaterialReserve.reserveDate).isEqualTo(DEFAULT_RESERVE_DATE)
        assertThat(testMaterialReserve.reserveStatus).isEqualTo(DEFAULT_RESERVE_STATUS)
        assertThat(testMaterialReserve.quantityToReserve).isEqualTo(DEFAULT_QUANTITY_TO_RESERVE)
        assertThat(testMaterialReserve.measureUnit).isEqualTo(DEFAULT_MEASURE_UNIT)
    }

    @Test
    @Transactional
    fun createMaterialReserveWithExistingId() {
        val databaseSizeBeforeCreate = materialReserveRepository.findAll().size

        // Create the MaterialReserve with an existing ID
        materialReserveModel.id = 1L
        val materialReserveValueObject = materialReserveMapper.toDto(materialReserveModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restMaterialReserveMockMvc.perform(
            post("/api/material-reserves")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialReserveValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the MaterialReserve in the database
        val materialReserveList = materialReserveRepository.findAll()
        assertThat(materialReserveList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllMaterialReserves() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList
        restMaterialReserveMockMvc.perform(get("/api/material-reserves?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(materialReserveModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].reserveDate").value(hasItem(DEFAULT_RESERVE_DATE.toString())))
            .andExpect(jsonPath("$.[*].reserveStatus").value(hasItem(DEFAULT_RESERVE_STATUS.toString())))
            .andExpect(jsonPath("$.[*].quantityToReserve").value(hasItem(DEFAULT_QUANTITY_TO_RESERVE)))
            .andExpect(jsonPath("$.[*].measureUnit").value(hasItem(DEFAULT_MEASURE_UNIT.toString())))
    }

    @Test
    @Transactional
    fun getMaterialReserve() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        val id = materialReserveModel.id
        assertNotNull(id)

        // Get the materialReserve
        restMaterialReserveMockMvc.perform(get("/api/material-reserves/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.reserveDate").value(DEFAULT_RESERVE_DATE.toString()))
            .andExpect(jsonPath("$.reserveStatus").value(DEFAULT_RESERVE_STATUS.toString()))
            .andExpect(jsonPath("$.quantityToReserve").value(DEFAULT_QUANTITY_TO_RESERVE))
            .andExpect(jsonPath("$.measureUnit").value(DEFAULT_MEASURE_UNIT.toString()))
    }

    @Test
    @Transactional
    fun getAllMaterialReservesByReserveDateIsEqualToSomething() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList where reserveDate equals to DEFAULT_RESERVE_DATE
        defaultMaterialReserveShouldBeFound("reserveDate.equals=$DEFAULT_RESERVE_DATE")

        // Get all the materialReserveList where reserveDate equals to UPDATED_RESERVE_DATE
        defaultMaterialReserveShouldNotBeFound("reserveDate.equals=$UPDATED_RESERVE_DATE")
    }

    @Test
    @Transactional
    fun getAllMaterialReservesByReserveDateIsInShouldWork() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList where reserveDate in DEFAULT_RESERVE_DATE or UPDATED_RESERVE_DATE
        defaultMaterialReserveShouldBeFound("reserveDate.in=$DEFAULT_RESERVE_DATE,$UPDATED_RESERVE_DATE")

        // Get all the materialReserveList where reserveDate equals to UPDATED_RESERVE_DATE
        defaultMaterialReserveShouldNotBeFound("reserveDate.in=$UPDATED_RESERVE_DATE")
    }

    @Test
    @Transactional
    fun getAllMaterialReservesByReserveDateIsNullOrNotNull() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList where reserveDate is not null
        defaultMaterialReserveShouldBeFound("reserveDate.specified=true")

        // Get all the materialReserveList where reserveDate is null
        defaultMaterialReserveShouldNotBeFound("reserveDate.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialReservesByReserveStatusIsEqualToSomething() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList where reserveStatus equals to DEFAULT_RESERVE_STATUS
        defaultMaterialReserveShouldBeFound("reserveStatus.equals=$DEFAULT_RESERVE_STATUS")

        // Get all the materialReserveList where reserveStatus equals to UPDATED_RESERVE_STATUS
        defaultMaterialReserveShouldNotBeFound("reserveStatus.equals=$UPDATED_RESERVE_STATUS")
    }

    @Test
    @Transactional
    fun getAllMaterialReservesByReserveStatusIsInShouldWork() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList where reserveStatus in DEFAULT_RESERVE_STATUS or UPDATED_RESERVE_STATUS
        defaultMaterialReserveShouldBeFound("reserveStatus.in=$DEFAULT_RESERVE_STATUS,$UPDATED_RESERVE_STATUS")

        // Get all the materialReserveList where reserveStatus equals to UPDATED_RESERVE_STATUS
        defaultMaterialReserveShouldNotBeFound("reserveStatus.in=$UPDATED_RESERVE_STATUS")
    }

    @Test
    @Transactional
    fun getAllMaterialReservesByReserveStatusIsNullOrNotNull() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList where reserveStatus is not null
        defaultMaterialReserveShouldBeFound("reserveStatus.specified=true")

        // Get all the materialReserveList where reserveStatus is null
        defaultMaterialReserveShouldNotBeFound("reserveStatus.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialReservesByQuantityToReserveIsEqualToSomething() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList where quantityToReserve equals to DEFAULT_QUANTITY_TO_RESERVE
        defaultMaterialReserveShouldBeFound("quantityToReserve.equals=$DEFAULT_QUANTITY_TO_RESERVE")

        // Get all the materialReserveList where quantityToReserve equals to UPDATED_QUANTITY_TO_RESERVE
        defaultMaterialReserveShouldNotBeFound("quantityToReserve.equals=$UPDATED_QUANTITY_TO_RESERVE")
    }

    @Test
    @Transactional
    fun getAllMaterialReservesByQuantityToReserveIsInShouldWork() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList where quantityToReserve in DEFAULT_QUANTITY_TO_RESERVE or UPDATED_QUANTITY_TO_RESERVE
        defaultMaterialReserveShouldBeFound("quantityToReserve.in=$DEFAULT_QUANTITY_TO_RESERVE,$UPDATED_QUANTITY_TO_RESERVE")

        // Get all the materialReserveList where quantityToReserve equals to UPDATED_QUANTITY_TO_RESERVE
        defaultMaterialReserveShouldNotBeFound("quantityToReserve.in=$UPDATED_QUANTITY_TO_RESERVE")
    }

    @Test
    @Transactional
    fun getAllMaterialReservesByQuantityToReserveIsNullOrNotNull() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList where quantityToReserve is not null
        defaultMaterialReserveShouldBeFound("quantityToReserve.specified=true")

        // Get all the materialReserveList where quantityToReserve is null
        defaultMaterialReserveShouldNotBeFound("quantityToReserve.specified=false")
    }
    @Test
    @Transactional
    fun getAllMaterialReservesByQuantityToReserveIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList where quantityToReserve greater than or equals to DEFAULT_QUANTITY_TO_RESERVE
        defaultMaterialReserveShouldBeFound("quantityToReserve.greaterOrEqualThan=$DEFAULT_QUANTITY_TO_RESERVE")

        // Get all the materialReserveList where quantityToReserve greater than or equals to UPDATED_QUANTITY_TO_RESERVE
        defaultMaterialReserveShouldNotBeFound("quantityToReserve.greaterOrEqualThan=$UPDATED_QUANTITY_TO_RESERVE")
    }

    @Test
    @Transactional
    fun getAllMaterialReservesByQuantityToReserveIsLessThanSomething() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList where quantityToReserve less than or equals to DEFAULT_QUANTITY_TO_RESERVE
        defaultMaterialReserveShouldNotBeFound("quantityToReserve.lessThan=$DEFAULT_QUANTITY_TO_RESERVE")

        // Get all the materialReserveList where quantityToReserve less than or equals to UPDATED_QUANTITY_TO_RESERVE
        defaultMaterialReserveShouldBeFound("quantityToReserve.lessThan=$UPDATED_QUANTITY_TO_RESERVE")
    }

    @Test
    @Transactional
    fun getAllMaterialReservesByMeasureUnitIsEqualToSomething() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList where measureUnit equals to DEFAULT_MEASURE_UNIT
        defaultMaterialReserveShouldBeFound("measureUnit.equals=$DEFAULT_MEASURE_UNIT")

        // Get all the materialReserveList where measureUnit equals to UPDATED_MEASURE_UNIT
        defaultMaterialReserveShouldNotBeFound("measureUnit.equals=$UPDATED_MEASURE_UNIT")
    }

    @Test
    @Transactional
    fun getAllMaterialReservesByMeasureUnitIsInShouldWork() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList where measureUnit in DEFAULT_MEASURE_UNIT or UPDATED_MEASURE_UNIT
        defaultMaterialReserveShouldBeFound("measureUnit.in=$DEFAULT_MEASURE_UNIT,$UPDATED_MEASURE_UNIT")

        // Get all the materialReserveList where measureUnit equals to UPDATED_MEASURE_UNIT
        defaultMaterialReserveShouldNotBeFound("measureUnit.in=$UPDATED_MEASURE_UNIT")
    }

    @Test
    @Transactional
    fun getAllMaterialReservesByMeasureUnitIsNullOrNotNull() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        // Get all the materialReserveList where measureUnit is not null
        defaultMaterialReserveShouldBeFound("measureUnit.specified=true")

        // Get all the materialReserveList where measureUnit is null
        defaultMaterialReserveShouldNotBeFound("measureUnit.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialReservesByMaterialIsEqualToSomething() {
        // Initialize the database
        val material = MaterialResourceIT.createEntity(em)
        em.persist(material)
        em.flush()
        materialReserveModel.material = material
        materialReserveRepository.saveAndFlush(materialReserveModel)
        val materialId = material.id

        // Get all the materialReserveList where material equals to materialId
        defaultMaterialReserveShouldBeFound("materialId.equals=$materialId")

        // Get all the materialReserveList where material equals to materialId + 1
        defaultMaterialReserveShouldNotBeFound("materialId.equals=${materialId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultMaterialReserveShouldBeFound(filter: String) {
        restMaterialReserveMockMvc.perform(get("/api/material-reserves?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(materialReserveModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].reserveDate").value(hasItem(DEFAULT_RESERVE_DATE.toString())))
            .andExpect(jsonPath("$.[*].reserveStatus").value(hasItem(DEFAULT_RESERVE_STATUS.toString())))
            .andExpect(jsonPath("$.[*].quantityToReserve").value(hasItem(DEFAULT_QUANTITY_TO_RESERVE)))
            .andExpect(jsonPath("$.[*].measureUnit").value(hasItem(DEFAULT_MEASURE_UNIT.toString())))

        // Check, that the count call also returns 1
        restMaterialReserveMockMvc.perform(get("/api/material-reserves/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultMaterialReserveShouldNotBeFound(filter: String) {
        restMaterialReserveMockMvc.perform(get("/api/material-reserves?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restMaterialReserveMockMvc.perform(get("/api/material-reserves/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingMaterialReserve() {
        // Get the materialReserve
        restMaterialReserveMockMvc.perform(get("/api/material-reserves/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateMaterialReserve() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        val databaseSizeBeforeUpdate = materialReserveRepository.findAll().size

        // Update the materialReserve
        val id = materialReserveModel.id
        assertNotNull(id)
        val updatedMaterialReserve = materialReserveRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedMaterialReserve are not directly saved in db
        em.detach(updatedMaterialReserve)
        updatedMaterialReserve.reserveDate = UPDATED_RESERVE_DATE
        updatedMaterialReserve.reserveStatus = UPDATED_RESERVE_STATUS
        updatedMaterialReserve.quantityToReserve = UPDATED_QUANTITY_TO_RESERVE
        updatedMaterialReserve.measureUnit = UPDATED_MEASURE_UNIT
        val materialReserveValueObject = materialReserveMapper.toDto(updatedMaterialReserve)

        restMaterialReserveMockMvc.perform(
            put("/api/material-reserves")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialReserveValueObject))
        ).andExpect(status().isOk)

        // Validate the MaterialReserve in the database
        val materialReserveList = materialReserveRepository.findAll()
        assertThat(materialReserveList).hasSize(databaseSizeBeforeUpdate)
        val testMaterialReserve = materialReserveList[materialReserveList.size - 1]
        assertThat(testMaterialReserve.reserveDate).isEqualTo(UPDATED_RESERVE_DATE)
        assertThat(testMaterialReserve.reserveStatus).isEqualTo(UPDATED_RESERVE_STATUS)
        assertThat(testMaterialReserve.quantityToReserve).isEqualTo(UPDATED_QUANTITY_TO_RESERVE)
        assertThat(testMaterialReserve.measureUnit).isEqualTo(UPDATED_MEASURE_UNIT)
    }

    @Test
    @Transactional
    fun updateNonExistingMaterialReserve() {
        val databaseSizeBeforeUpdate = materialReserveRepository.findAll().size

        // Create the MaterialReserve
        val materialReserveValueObject = materialReserveMapper.toDto(materialReserveModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaterialReserveMockMvc.perform(
            put("/api/material-reserves")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialReserveValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the MaterialReserve in the database
        val materialReserveList = materialReserveRepository.findAll()
        assertThat(materialReserveList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteMaterialReserve() {
        // Initialize the database
        materialReserveRepository.saveAndFlush(materialReserveModel)

        val databaseSizeBeforeDelete = materialReserveRepository.findAll().size

        val id = materialReserveModel.id
        assertNotNull(id)

        // Delete the materialReserve
        restMaterialReserveMockMvc.perform(
            delete("/api/material-reserves/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val materialReserveList = materialReserveRepository.findAll()
        assertThat(materialReserveList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(MaterialReserveModel::class.java)
        val materialReserveModel1 = MaterialReserveModel()
        materialReserveModel1.id = 1L
        val materialReserveModel2 = MaterialReserveModel()
        materialReserveModel2.id = materialReserveModel1.id
        assertThat(materialReserveModel1).isEqualTo(materialReserveModel2)
        materialReserveModel2.id = 2L
        assertThat(materialReserveModel1).isNotEqualTo(materialReserveModel2)
        materialReserveModel1.id = null
        assertThat(materialReserveModel1).isNotEqualTo(materialReserveModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(MaterialReserveValueObject::class.java)
        val materialReserveValueObject1 = MaterialReserveValueObject()
        materialReserveValueObject1.id = 1L
        val materialReserveValueObject2 = MaterialReserveValueObject()
        assertThat(materialReserveValueObject1).isNotEqualTo(materialReserveValueObject2)
        materialReserveValueObject2.id = materialReserveValueObject1.id
        assertThat(materialReserveValueObject1).isEqualTo(materialReserveValueObject2)
        materialReserveValueObject2.id = 2L
        assertThat(materialReserveValueObject1).isNotEqualTo(materialReserveValueObject2)
        materialReserveValueObject1.id = null
        assertThat(materialReserveValueObject1).isNotEqualTo(materialReserveValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(materialReserveMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(materialReserveMapper.fromId(null)).isNull()
    }

    companion object {

        private val DEFAULT_RESERVE_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_RESERVE_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_RESERVE_STATUS: MaterialReserveStatus = MaterialReserveStatus.NEW
        private val UPDATED_RESERVE_STATUS: MaterialReserveStatus = MaterialReserveStatus.RESERVED

        private const val DEFAULT_QUANTITY_TO_RESERVE: Int = 1
        private const val UPDATED_QUANTITY_TO_RESERVE: Int = 2

        private val DEFAULT_MEASURE_UNIT: MeasureUnit = MeasureUnit.METER
        private val UPDATED_MEASURE_UNIT: MeasureUnit = MeasureUnit.SQUARE_METER
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): MaterialReserveModel {
            val materialReserveModel = MaterialReserveModel()
            materialReserveModel.reserveDate = DEFAULT_RESERVE_DATE
            materialReserveModel.reserveStatus = DEFAULT_RESERVE_STATUS
            materialReserveModel.quantityToReserve = DEFAULT_QUANTITY_TO_RESERVE
            materialReserveModel.measureUnit = DEFAULT_MEASURE_UNIT

        return materialReserveModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): MaterialReserveModel {
            val materialReserveModel = MaterialReserveModel()
            materialReserveModel.reserveDate = UPDATED_RESERVE_DATE
            materialReserveModel.reserveStatus = UPDATED_RESERVE_STATUS
            materialReserveModel.quantityToReserve = UPDATED_QUANTITY_TO_RESERVE
            materialReserveModel.measureUnit = UPDATED_MEASURE_UNIT

        return materialReserveModel
        }
    }
}
