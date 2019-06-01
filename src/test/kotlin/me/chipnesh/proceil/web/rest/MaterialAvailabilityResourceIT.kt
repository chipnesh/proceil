package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.MaterialAvailabilityModel
import me.chipnesh.proceil.repository.MaterialAvailabilityRepository
import me.chipnesh.proceil.service.MaterialAvailabilityService
import me.chipnesh.proceil.service.dto.MaterialAvailabilityValueObject
import me.chipnesh.proceil.service.mapper.MaterialAvailabilityMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.MaterialAvailabilityQueryService

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
 * Test class for the MaterialAvailabilityResource REST controller.
 *
 * @see MaterialAvailabilityResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class MaterialAvailabilityResourceIT {

    @Autowired
    private lateinit var materialAvailabilityRepository: MaterialAvailabilityRepository

    @Autowired
    private lateinit var materialAvailabilityMapper: MaterialAvailabilityMapper

    @Autowired
    private lateinit var materialAvailabilityService: MaterialAvailabilityService

    @Autowired
    private lateinit var materialAvailabilityQueryService: MaterialAvailabilityQueryService

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

    private lateinit var restMaterialAvailabilityMockMvc: MockMvc

    private lateinit var materialAvailabilityModel: MaterialAvailabilityModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val materialAvailabilityResource = MaterialAvailabilityResource(materialAvailabilityService, materialAvailabilityQueryService)
        this.restMaterialAvailabilityMockMvc = MockMvcBuilders.standaloneSetup(materialAvailabilityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        materialAvailabilityModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createMaterialAvailability() {
        val databaseSizeBeforeCreate = materialAvailabilityRepository.findAll().size

        // Create the MaterialAvailability
        val materialAvailabilityValueObject = materialAvailabilityMapper.toDto(materialAvailabilityModel)
        restMaterialAvailabilityMockMvc.perform(
            post("/api/material-availabilities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialAvailabilityValueObject))
        ).andExpect(status().isCreated)

        // Validate the MaterialAvailability in the database
        val materialAvailabilityList = materialAvailabilityRepository.findAll()
        assertThat(materialAvailabilityList).hasSize(databaseSizeBeforeCreate + 1)
        val testMaterialAvailability = materialAvailabilityList[materialAvailabilityList.size - 1]
        assertThat(testMaterialAvailability.availabilitySummary).isEqualTo(DEFAULT_AVAILABILITY_SUMMARY)
        assertThat(testMaterialAvailability.remainingQuantity).isEqualTo(DEFAULT_REMAINING_QUANTITY)
        assertThat(testMaterialAvailability.measureUnit).isEqualTo(DEFAULT_MEASURE_UNIT)
    }

    @Test
    @Transactional
    fun createMaterialAvailabilityWithExistingId() {
        val databaseSizeBeforeCreate = materialAvailabilityRepository.findAll().size

        // Create the MaterialAvailability with an existing ID
        materialAvailabilityModel.id = 1L
        val materialAvailabilityValueObject = materialAvailabilityMapper.toDto(materialAvailabilityModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restMaterialAvailabilityMockMvc.perform(
            post("/api/material-availabilities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialAvailabilityValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the MaterialAvailability in the database
        val materialAvailabilityList = materialAvailabilityRepository.findAll()
        assertThat(materialAvailabilityList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllMaterialAvailabilities() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        // Get all the materialAvailabilityList
        restMaterialAvailabilityMockMvc.perform(get("/api/material-availabilities?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(materialAvailabilityModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].availabilitySummary").value(hasItem(DEFAULT_AVAILABILITY_SUMMARY)))
            .andExpect(jsonPath("$.[*].remainingQuantity").value(hasItem(DEFAULT_REMAINING_QUANTITY)))
            .andExpect(jsonPath("$.[*].measureUnit").value(hasItem(DEFAULT_MEASURE_UNIT.toString())))
    }

    @Test
    @Transactional
    fun getMaterialAvailability() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        val id = materialAvailabilityModel.id
        assertNotNull(id)

        // Get the materialAvailability
        restMaterialAvailabilityMockMvc.perform(get("/api/material-availabilities/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.availabilitySummary").value(DEFAULT_AVAILABILITY_SUMMARY))
            .andExpect(jsonPath("$.remainingQuantity").value(DEFAULT_REMAINING_QUANTITY))
            .andExpect(jsonPath("$.measureUnit").value(DEFAULT_MEASURE_UNIT.toString()))
    }

    @Test
    @Transactional
    fun getAllMaterialAvailabilitiesByAvailabilitySummaryIsEqualToSomething() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        // Get all the materialAvailabilityList where availabilitySummary equals to DEFAULT_AVAILABILITY_SUMMARY
        defaultMaterialAvailabilityShouldBeFound("availabilitySummary.equals=$DEFAULT_AVAILABILITY_SUMMARY")

        // Get all the materialAvailabilityList where availabilitySummary equals to UPDATED_AVAILABILITY_SUMMARY
        defaultMaterialAvailabilityShouldNotBeFound("availabilitySummary.equals=$UPDATED_AVAILABILITY_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllMaterialAvailabilitiesByAvailabilitySummaryIsInShouldWork() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        // Get all the materialAvailabilityList where availabilitySummary in DEFAULT_AVAILABILITY_SUMMARY or UPDATED_AVAILABILITY_SUMMARY
        defaultMaterialAvailabilityShouldBeFound("availabilitySummary.in=$DEFAULT_AVAILABILITY_SUMMARY,$UPDATED_AVAILABILITY_SUMMARY")

        // Get all the materialAvailabilityList where availabilitySummary equals to UPDATED_AVAILABILITY_SUMMARY
        defaultMaterialAvailabilityShouldNotBeFound("availabilitySummary.in=$UPDATED_AVAILABILITY_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllMaterialAvailabilitiesByAvailabilitySummaryIsNullOrNotNull() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        // Get all the materialAvailabilityList where availabilitySummary is not null
        defaultMaterialAvailabilityShouldBeFound("availabilitySummary.specified=true")

        // Get all the materialAvailabilityList where availabilitySummary is null
        defaultMaterialAvailabilityShouldNotBeFound("availabilitySummary.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialAvailabilitiesByRemainingQuantityIsEqualToSomething() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        // Get all the materialAvailabilityList where remainingQuantity equals to DEFAULT_REMAINING_QUANTITY
        defaultMaterialAvailabilityShouldBeFound("remainingQuantity.equals=$DEFAULT_REMAINING_QUANTITY")

        // Get all the materialAvailabilityList where remainingQuantity equals to UPDATED_REMAINING_QUANTITY
        defaultMaterialAvailabilityShouldNotBeFound("remainingQuantity.equals=$UPDATED_REMAINING_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllMaterialAvailabilitiesByRemainingQuantityIsInShouldWork() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        // Get all the materialAvailabilityList where remainingQuantity in DEFAULT_REMAINING_QUANTITY or UPDATED_REMAINING_QUANTITY
        defaultMaterialAvailabilityShouldBeFound("remainingQuantity.in=$DEFAULT_REMAINING_QUANTITY,$UPDATED_REMAINING_QUANTITY")

        // Get all the materialAvailabilityList where remainingQuantity equals to UPDATED_REMAINING_QUANTITY
        defaultMaterialAvailabilityShouldNotBeFound("remainingQuantity.in=$UPDATED_REMAINING_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllMaterialAvailabilitiesByRemainingQuantityIsNullOrNotNull() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        // Get all the materialAvailabilityList where remainingQuantity is not null
        defaultMaterialAvailabilityShouldBeFound("remainingQuantity.specified=true")

        // Get all the materialAvailabilityList where remainingQuantity is null
        defaultMaterialAvailabilityShouldNotBeFound("remainingQuantity.specified=false")
    }
    @Test
    @Transactional
    fun getAllMaterialAvailabilitiesByRemainingQuantityIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        // Get all the materialAvailabilityList where remainingQuantity greater than or equals to DEFAULT_REMAINING_QUANTITY
        defaultMaterialAvailabilityShouldBeFound("remainingQuantity.greaterOrEqualThan=$DEFAULT_REMAINING_QUANTITY")

        // Get all the materialAvailabilityList where remainingQuantity greater than or equals to UPDATED_REMAINING_QUANTITY
        defaultMaterialAvailabilityShouldNotBeFound("remainingQuantity.greaterOrEqualThan=$UPDATED_REMAINING_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllMaterialAvailabilitiesByRemainingQuantityIsLessThanSomething() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        // Get all the materialAvailabilityList where remainingQuantity less than or equals to DEFAULT_REMAINING_QUANTITY
        defaultMaterialAvailabilityShouldNotBeFound("remainingQuantity.lessThan=$DEFAULT_REMAINING_QUANTITY")

        // Get all the materialAvailabilityList where remainingQuantity less than or equals to UPDATED_REMAINING_QUANTITY
        defaultMaterialAvailabilityShouldBeFound("remainingQuantity.lessThan=$UPDATED_REMAINING_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllMaterialAvailabilitiesByMeasureUnitIsEqualToSomething() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        // Get all the materialAvailabilityList where measureUnit equals to DEFAULT_MEASURE_UNIT
        defaultMaterialAvailabilityShouldBeFound("measureUnit.equals=$DEFAULT_MEASURE_UNIT")

        // Get all the materialAvailabilityList where measureUnit equals to UPDATED_MEASURE_UNIT
        defaultMaterialAvailabilityShouldNotBeFound("measureUnit.equals=$UPDATED_MEASURE_UNIT")
    }

    @Test
    @Transactional
    fun getAllMaterialAvailabilitiesByMeasureUnitIsInShouldWork() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        // Get all the materialAvailabilityList where measureUnit in DEFAULT_MEASURE_UNIT or UPDATED_MEASURE_UNIT
        defaultMaterialAvailabilityShouldBeFound("measureUnit.in=$DEFAULT_MEASURE_UNIT,$UPDATED_MEASURE_UNIT")

        // Get all the materialAvailabilityList where measureUnit equals to UPDATED_MEASURE_UNIT
        defaultMaterialAvailabilityShouldNotBeFound("measureUnit.in=$UPDATED_MEASURE_UNIT")
    }

    @Test
    @Transactional
    fun getAllMaterialAvailabilitiesByMeasureUnitIsNullOrNotNull() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        // Get all the materialAvailabilityList where measureUnit is not null
        defaultMaterialAvailabilityShouldBeFound("measureUnit.specified=true")

        // Get all the materialAvailabilityList where measureUnit is null
        defaultMaterialAvailabilityShouldNotBeFound("measureUnit.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialAvailabilitiesByMaterialIsEqualToSomething() {
        // Initialize the database
        val material = MaterialResourceIT.createEntity(em)
        em.persist(material)
        em.flush()
        materialAvailabilityModel.material = material
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)
        val materialId = material.id

        // Get all the materialAvailabilityList where material equals to materialId
        defaultMaterialAvailabilityShouldBeFound("materialId.equals=$materialId")

        // Get all the materialAvailabilityList where material equals to materialId + 1
        defaultMaterialAvailabilityShouldNotBeFound("materialId.equals=${materialId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllMaterialAvailabilitiesByAvailableAtIsEqualToSomething() {
        // Initialize the database
        val availableAt = ZoneResourceIT.createEntity(em)
        em.persist(availableAt)
        em.flush()
        materialAvailabilityModel.availableAt = availableAt
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)
        val availableAtId = availableAt.id

        // Get all the materialAvailabilityList where availableAt equals to availableAtId
        defaultMaterialAvailabilityShouldBeFound("availableAtId.equals=$availableAtId")

        // Get all the materialAvailabilityList where availableAt equals to availableAtId + 1
        defaultMaterialAvailabilityShouldNotBeFound("availableAtId.equals=${availableAtId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultMaterialAvailabilityShouldBeFound(filter: String) {
        restMaterialAvailabilityMockMvc.perform(get("/api/material-availabilities?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(materialAvailabilityModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].availabilitySummary").value(hasItem(DEFAULT_AVAILABILITY_SUMMARY)))
            .andExpect(jsonPath("$.[*].remainingQuantity").value(hasItem(DEFAULT_REMAINING_QUANTITY)))
            .andExpect(jsonPath("$.[*].measureUnit").value(hasItem(DEFAULT_MEASURE_UNIT.toString())))

        // Check, that the count call also returns 1
        restMaterialAvailabilityMockMvc.perform(get("/api/material-availabilities/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultMaterialAvailabilityShouldNotBeFound(filter: String) {
        restMaterialAvailabilityMockMvc.perform(get("/api/material-availabilities?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restMaterialAvailabilityMockMvc.perform(get("/api/material-availabilities/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingMaterialAvailability() {
        // Get the materialAvailability
        restMaterialAvailabilityMockMvc.perform(get("/api/material-availabilities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateMaterialAvailability() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        val databaseSizeBeforeUpdate = materialAvailabilityRepository.findAll().size

        // Update the materialAvailability
        val id = materialAvailabilityModel.id
        assertNotNull(id)
        val updatedMaterialAvailability = materialAvailabilityRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedMaterialAvailability are not directly saved in db
        em.detach(updatedMaterialAvailability)
        updatedMaterialAvailability.availabilitySummary = UPDATED_AVAILABILITY_SUMMARY
        updatedMaterialAvailability.remainingQuantity = UPDATED_REMAINING_QUANTITY
        updatedMaterialAvailability.measureUnit = UPDATED_MEASURE_UNIT
        val materialAvailabilityValueObject = materialAvailabilityMapper.toDto(updatedMaterialAvailability)

        restMaterialAvailabilityMockMvc.perform(
            put("/api/material-availabilities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialAvailabilityValueObject))
        ).andExpect(status().isOk)

        // Validate the MaterialAvailability in the database
        val materialAvailabilityList = materialAvailabilityRepository.findAll()
        assertThat(materialAvailabilityList).hasSize(databaseSizeBeforeUpdate)
        val testMaterialAvailability = materialAvailabilityList[materialAvailabilityList.size - 1]
        assertThat(testMaterialAvailability.availabilitySummary).isEqualTo(UPDATED_AVAILABILITY_SUMMARY)
        assertThat(testMaterialAvailability.remainingQuantity).isEqualTo(UPDATED_REMAINING_QUANTITY)
        assertThat(testMaterialAvailability.measureUnit).isEqualTo(UPDATED_MEASURE_UNIT)
    }

    @Test
    @Transactional
    fun updateNonExistingMaterialAvailability() {
        val databaseSizeBeforeUpdate = materialAvailabilityRepository.findAll().size

        // Create the MaterialAvailability
        val materialAvailabilityValueObject = materialAvailabilityMapper.toDto(materialAvailabilityModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaterialAvailabilityMockMvc.perform(
            put("/api/material-availabilities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialAvailabilityValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the MaterialAvailability in the database
        val materialAvailabilityList = materialAvailabilityRepository.findAll()
        assertThat(materialAvailabilityList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteMaterialAvailability() {
        // Initialize the database
        materialAvailabilityRepository.saveAndFlush(materialAvailabilityModel)

        val databaseSizeBeforeDelete = materialAvailabilityRepository.findAll().size

        val id = materialAvailabilityModel.id
        assertNotNull(id)

        // Delete the materialAvailability
        restMaterialAvailabilityMockMvc.perform(
            delete("/api/material-availabilities/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val materialAvailabilityList = materialAvailabilityRepository.findAll()
        assertThat(materialAvailabilityList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(MaterialAvailabilityModel::class.java)
        val materialAvailabilityModel1 = MaterialAvailabilityModel()
        materialAvailabilityModel1.id = 1L
        val materialAvailabilityModel2 = MaterialAvailabilityModel()
        materialAvailabilityModel2.id = materialAvailabilityModel1.id
        assertThat(materialAvailabilityModel1).isEqualTo(materialAvailabilityModel2)
        materialAvailabilityModel2.id = 2L
        assertThat(materialAvailabilityModel1).isNotEqualTo(materialAvailabilityModel2)
        materialAvailabilityModel1.id = null
        assertThat(materialAvailabilityModel1).isNotEqualTo(materialAvailabilityModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(MaterialAvailabilityValueObject::class.java)
        val materialAvailabilityValueObject1 = MaterialAvailabilityValueObject()
        materialAvailabilityValueObject1.id = 1L
        val materialAvailabilityValueObject2 = MaterialAvailabilityValueObject()
        assertThat(materialAvailabilityValueObject1).isNotEqualTo(materialAvailabilityValueObject2)
        materialAvailabilityValueObject2.id = materialAvailabilityValueObject1.id
        assertThat(materialAvailabilityValueObject1).isEqualTo(materialAvailabilityValueObject2)
        materialAvailabilityValueObject2.id = 2L
        assertThat(materialAvailabilityValueObject1).isNotEqualTo(materialAvailabilityValueObject2)
        materialAvailabilityValueObject1.id = null
        assertThat(materialAvailabilityValueObject1).isNotEqualTo(materialAvailabilityValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(materialAvailabilityMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(materialAvailabilityMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_AVAILABILITY_SUMMARY: String = "AAAAAAAAAA"
        private const val UPDATED_AVAILABILITY_SUMMARY = "BBBBBBBBBB"

        private const val DEFAULT_REMAINING_QUANTITY: Int = 1
        private const val UPDATED_REMAINING_QUANTITY: Int = 2

        private val DEFAULT_MEASURE_UNIT: MeasureUnit = MeasureUnit.METER
        private val UPDATED_MEASURE_UNIT: MeasureUnit = MeasureUnit.SQUARE_METER
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): MaterialAvailabilityModel {
            val materialAvailabilityModel = MaterialAvailabilityModel()
            materialAvailabilityModel.availabilitySummary = DEFAULT_AVAILABILITY_SUMMARY
            materialAvailabilityModel.remainingQuantity = DEFAULT_REMAINING_QUANTITY
            materialAvailabilityModel.measureUnit = DEFAULT_MEASURE_UNIT

        return materialAvailabilityModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): MaterialAvailabilityModel {
            val materialAvailabilityModel = MaterialAvailabilityModel()
            materialAvailabilityModel.availabilitySummary = UPDATED_AVAILABILITY_SUMMARY
            materialAvailabilityModel.remainingQuantity = UPDATED_REMAINING_QUANTITY
            materialAvailabilityModel.measureUnit = UPDATED_MEASURE_UNIT

        return materialAvailabilityModel
        }
    }
}
