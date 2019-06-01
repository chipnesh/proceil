package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.FacilityModel
import me.chipnesh.proceil.repository.FacilityRepository
import me.chipnesh.proceil.service.FacilityService
import me.chipnesh.proceil.service.dto.FacilityValueObject
import me.chipnesh.proceil.service.mapper.FacilityMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.FacilityQueryService

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
 * Test class for the FacilityResource REST controller.
 *
 * @see FacilityResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class FacilityResourceIT {

    @Autowired
    private lateinit var facilityRepository: FacilityRepository

    @Autowired
    private lateinit var facilityMapper: FacilityMapper

    @Autowired
    private lateinit var facilityService: FacilityService

    @Autowired
    private lateinit var facilityQueryService: FacilityQueryService

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

    private lateinit var restFacilityMockMvc: MockMvc

    private lateinit var facilityModel: FacilityModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val facilityResource = FacilityResource(facilityService, facilityQueryService)
        this.restFacilityMockMvc = MockMvcBuilders.standaloneSetup(facilityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        facilityModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createFacility() {
        val databaseSizeBeforeCreate = facilityRepository.findAll().size

        // Create the Facility
        val facilityValueObject = facilityMapper.toDto(facilityModel)
        restFacilityMockMvc.perform(
            post("/api/facilities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(facilityValueObject))
        ).andExpect(status().isCreated)

        // Validate the Facility in the database
        val facilityList = facilityRepository.findAll()
        assertThat(facilityList).hasSize(databaseSizeBeforeCreate + 1)
        val testFacility = facilityList[facilityList.size - 1]
        assertThat(testFacility.facilityName).isEqualTo(DEFAULT_FACILITY_NAME)
    }

    @Test
    @Transactional
    fun createFacilityWithExistingId() {
        val databaseSizeBeforeCreate = facilityRepository.findAll().size

        // Create the Facility with an existing ID
        facilityModel.id = 1L
        val facilityValueObject = facilityMapper.toDto(facilityModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restFacilityMockMvc.perform(
            post("/api/facilities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(facilityValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Facility in the database
        val facilityList = facilityRepository.findAll()
        assertThat(facilityList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllFacilities() {
        // Initialize the database
        facilityRepository.saveAndFlush(facilityModel)

        // Get all the facilityList
        restFacilityMockMvc.perform(get("/api/facilities?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(facilityModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].facilityName").value(hasItem(DEFAULT_FACILITY_NAME)))
    }

    @Test
    @Transactional
    fun getFacility() {
        // Initialize the database
        facilityRepository.saveAndFlush(facilityModel)

        val id = facilityModel.id
        assertNotNull(id)

        // Get the facility
        restFacilityMockMvc.perform(get("/api/facilities/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.facilityName").value(DEFAULT_FACILITY_NAME))
    }

    @Test
    @Transactional
    fun getAllFacilitiesByFacilityNameIsEqualToSomething() {
        // Initialize the database
        facilityRepository.saveAndFlush(facilityModel)

        // Get all the facilityList where facilityName equals to DEFAULT_FACILITY_NAME
        defaultFacilityShouldBeFound("facilityName.equals=$DEFAULT_FACILITY_NAME")

        // Get all the facilityList where facilityName equals to UPDATED_FACILITY_NAME
        defaultFacilityShouldNotBeFound("facilityName.equals=$UPDATED_FACILITY_NAME")
    }

    @Test
    @Transactional
    fun getAllFacilitiesByFacilityNameIsInShouldWork() {
        // Initialize the database
        facilityRepository.saveAndFlush(facilityModel)

        // Get all the facilityList where facilityName in DEFAULT_FACILITY_NAME or UPDATED_FACILITY_NAME
        defaultFacilityShouldBeFound("facilityName.in=$DEFAULT_FACILITY_NAME,$UPDATED_FACILITY_NAME")

        // Get all the facilityList where facilityName equals to UPDATED_FACILITY_NAME
        defaultFacilityShouldNotBeFound("facilityName.in=$UPDATED_FACILITY_NAME")
    }

    @Test
    @Transactional
    fun getAllFacilitiesByFacilityNameIsNullOrNotNull() {
        // Initialize the database
        facilityRepository.saveAndFlush(facilityModel)

        // Get all the facilityList where facilityName is not null
        defaultFacilityShouldBeFound("facilityName.specified=true")

        // Get all the facilityList where facilityName is null
        defaultFacilityShouldNotBeFound("facilityName.specified=false")
    }

    @Test
    @Transactional
    fun getAllFacilitiesByZoneIsEqualToSomething() {
        // Initialize the database
        val zone = ZoneResourceIT.createEntity(em)
        em.persist(zone)
        em.flush()
        facilityModel.addZone(zone)
        facilityRepository.saveAndFlush(facilityModel)
        val zoneId = zone.id

        // Get all the facilityList where zone equals to zoneId
        defaultFacilityShouldBeFound("zoneId.equals=$zoneId")

        // Get all the facilityList where zone equals to zoneId + 1
        defaultFacilityShouldNotBeFound("zoneId.equals=${zoneId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultFacilityShouldBeFound(filter: String) {
        restFacilityMockMvc.perform(get("/api/facilities?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(facilityModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].facilityName").value(hasItem(DEFAULT_FACILITY_NAME)))

        // Check, that the count call also returns 1
        restFacilityMockMvc.perform(get("/api/facilities/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultFacilityShouldNotBeFound(filter: String) {
        restFacilityMockMvc.perform(get("/api/facilities?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restFacilityMockMvc.perform(get("/api/facilities/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingFacility() {
        // Get the facility
        restFacilityMockMvc.perform(get("/api/facilities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateFacility() {
        // Initialize the database
        facilityRepository.saveAndFlush(facilityModel)

        val databaseSizeBeforeUpdate = facilityRepository.findAll().size

        // Update the facility
        val id = facilityModel.id
        assertNotNull(id)
        val updatedFacility = facilityRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedFacility are not directly saved in db
        em.detach(updatedFacility)
        updatedFacility.facilityName = UPDATED_FACILITY_NAME
        val facilityValueObject = facilityMapper.toDto(updatedFacility)

        restFacilityMockMvc.perform(
            put("/api/facilities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(facilityValueObject))
        ).andExpect(status().isOk)

        // Validate the Facility in the database
        val facilityList = facilityRepository.findAll()
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate)
        val testFacility = facilityList[facilityList.size - 1]
        assertThat(testFacility.facilityName).isEqualTo(UPDATED_FACILITY_NAME)
    }

    @Test
    @Transactional
    fun updateNonExistingFacility() {
        val databaseSizeBeforeUpdate = facilityRepository.findAll().size

        // Create the Facility
        val facilityValueObject = facilityMapper.toDto(facilityModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacilityMockMvc.perform(
            put("/api/facilities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(facilityValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Facility in the database
        val facilityList = facilityRepository.findAll()
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteFacility() {
        // Initialize the database
        facilityRepository.saveAndFlush(facilityModel)

        val databaseSizeBeforeDelete = facilityRepository.findAll().size

        val id = facilityModel.id
        assertNotNull(id)

        // Delete the facility
        restFacilityMockMvc.perform(
            delete("/api/facilities/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val facilityList = facilityRepository.findAll()
        assertThat(facilityList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(FacilityModel::class.java)
        val facilityModel1 = FacilityModel()
        facilityModel1.id = 1L
        val facilityModel2 = FacilityModel()
        facilityModel2.id = facilityModel1.id
        assertThat(facilityModel1).isEqualTo(facilityModel2)
        facilityModel2.id = 2L
        assertThat(facilityModel1).isNotEqualTo(facilityModel2)
        facilityModel1.id = null
        assertThat(facilityModel1).isNotEqualTo(facilityModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(FacilityValueObject::class.java)
        val facilityValueObject1 = FacilityValueObject()
        facilityValueObject1.id = 1L
        val facilityValueObject2 = FacilityValueObject()
        assertThat(facilityValueObject1).isNotEqualTo(facilityValueObject2)
        facilityValueObject2.id = facilityValueObject1.id
        assertThat(facilityValueObject1).isEqualTo(facilityValueObject2)
        facilityValueObject2.id = 2L
        assertThat(facilityValueObject1).isNotEqualTo(facilityValueObject2)
        facilityValueObject1.id = null
        assertThat(facilityValueObject1).isNotEqualTo(facilityValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(facilityMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(facilityMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_FACILITY_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_FACILITY_NAME = "BBBBBBBBBB"
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): FacilityModel {
            val facilityModel = FacilityModel()
            facilityModel.facilityName = DEFAULT_FACILITY_NAME

        return facilityModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): FacilityModel {
            val facilityModel = FacilityModel()
            facilityModel.facilityName = UPDATED_FACILITY_NAME

        return facilityModel
        }
    }
}
