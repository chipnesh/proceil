package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.ZoneModel
import me.chipnesh.proceil.repository.ZoneRepository
import me.chipnesh.proceil.service.ZoneService
import me.chipnesh.proceil.service.dto.ZoneValueObject
import me.chipnesh.proceil.service.mapper.ZoneMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.ZoneQueryService

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
 * Test class for the ZoneResource REST controller.
 *
 * @see ZoneResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class ZoneResourceIT {

    @Autowired
    private lateinit var zoneRepository: ZoneRepository

    @Autowired
    private lateinit var zoneMapper: ZoneMapper

    @Autowired
    private lateinit var zoneService: ZoneService

    @Autowired
    private lateinit var zoneQueryService: ZoneQueryService

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

    private lateinit var restZoneMockMvc: MockMvc

    private lateinit var zoneModel: ZoneModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val zoneResource = ZoneResource(zoneService, zoneQueryService)
        this.restZoneMockMvc = MockMvcBuilders.standaloneSetup(zoneResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        zoneModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createZone() {
        val databaseSizeBeforeCreate = zoneRepository.findAll().size

        // Create the Zone
        val zoneValueObject = zoneMapper.toDto(zoneModel)
        restZoneMockMvc.perform(
            post("/api/zones")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(zoneValueObject))
        ).andExpect(status().isCreated)

        // Validate the Zone in the database
        val zoneList = zoneRepository.findAll()
        assertThat(zoneList).hasSize(databaseSizeBeforeCreate + 1)
        val testZone = zoneList[zoneList.size - 1]
        assertThat(testZone.zoneName).isEqualTo(DEFAULT_ZONE_NAME)
    }

    @Test
    @Transactional
    fun createZoneWithExistingId() {
        val databaseSizeBeforeCreate = zoneRepository.findAll().size

        // Create the Zone with an existing ID
        zoneModel.id = 1L
        val zoneValueObject = zoneMapper.toDto(zoneModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restZoneMockMvc.perform(
            post("/api/zones")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(zoneValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Zone in the database
        val zoneList = zoneRepository.findAll()
        assertThat(zoneList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllZones() {
        // Initialize the database
        zoneRepository.saveAndFlush(zoneModel)

        // Get all the zoneList
        restZoneMockMvc.perform(get("/api/zones?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(zoneModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].zoneName").value(hasItem(DEFAULT_ZONE_NAME)))
    }

    @Test
    @Transactional
    fun getZone() {
        // Initialize the database
        zoneRepository.saveAndFlush(zoneModel)

        val id = zoneModel.id
        assertNotNull(id)

        // Get the zone
        restZoneMockMvc.perform(get("/api/zones/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.zoneName").value(DEFAULT_ZONE_NAME))
    }

    @Test
    @Transactional
    fun getAllZonesByZoneNameIsEqualToSomething() {
        // Initialize the database
        zoneRepository.saveAndFlush(zoneModel)

        // Get all the zoneList where zoneName equals to DEFAULT_ZONE_NAME
        defaultZoneShouldBeFound("zoneName.equals=$DEFAULT_ZONE_NAME")

        // Get all the zoneList where zoneName equals to UPDATED_ZONE_NAME
        defaultZoneShouldNotBeFound("zoneName.equals=$UPDATED_ZONE_NAME")
    }

    @Test
    @Transactional
    fun getAllZonesByZoneNameIsInShouldWork() {
        // Initialize the database
        zoneRepository.saveAndFlush(zoneModel)

        // Get all the zoneList where zoneName in DEFAULT_ZONE_NAME or UPDATED_ZONE_NAME
        defaultZoneShouldBeFound("zoneName.in=$DEFAULT_ZONE_NAME,$UPDATED_ZONE_NAME")

        // Get all the zoneList where zoneName equals to UPDATED_ZONE_NAME
        defaultZoneShouldNotBeFound("zoneName.in=$UPDATED_ZONE_NAME")
    }

    @Test
    @Transactional
    fun getAllZonesByZoneNameIsNullOrNotNull() {
        // Initialize the database
        zoneRepository.saveAndFlush(zoneModel)

        // Get all the zoneList where zoneName is not null
        defaultZoneShouldBeFound("zoneName.specified=true")

        // Get all the zoneList where zoneName is null
        defaultZoneShouldNotBeFound("zoneName.specified=false")
    }

    @Test
    @Transactional
    fun getAllZonesByMaterialIsEqualToSomething() {
        // Initialize the database
        val material = MaterialAvailabilityResourceIT.createEntity(em)
        em.persist(material)
        em.flush()
        zoneModel.addMaterial(material)
        zoneRepository.saveAndFlush(zoneModel)
        val materialId = material.id

        // Get all the zoneList where material equals to materialId
        defaultZoneShouldBeFound("materialId.equals=$materialId")

        // Get all the zoneList where material equals to materialId + 1
        defaultZoneShouldNotBeFound("materialId.equals=${materialId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllZonesByServiceIsEqualToSomething() {
        // Initialize the database
        val service = ServiceAvailabilityResourceIT.createEntity(em)
        em.persist(service)
        em.flush()
        zoneModel.addService(service)
        zoneRepository.saveAndFlush(zoneModel)
        val serviceId = service.id

        // Get all the zoneList where service equals to serviceId
        defaultZoneShouldBeFound("serviceId.equals=$serviceId")

        // Get all the zoneList where service equals to serviceId + 1
        defaultZoneShouldNotBeFound("serviceId.equals=${serviceId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllZonesByFacilityIsEqualToSomething() {
        // Initialize the database
        val facility = FacilityResourceIT.createEntity(em)
        em.persist(facility)
        em.flush()
        zoneModel.facility = facility
        zoneRepository.saveAndFlush(zoneModel)
        val facilityId = facility.id

        // Get all the zoneList where facility equals to facilityId
        defaultZoneShouldBeFound("facilityId.equals=$facilityId")

        // Get all the zoneList where facility equals to facilityId + 1
        defaultZoneShouldNotBeFound("facilityId.equals=${facilityId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultZoneShouldBeFound(filter: String) {
        restZoneMockMvc.perform(get("/api/zones?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(zoneModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].zoneName").value(hasItem(DEFAULT_ZONE_NAME)))

        // Check, that the count call also returns 1
        restZoneMockMvc.perform(get("/api/zones/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultZoneShouldNotBeFound(filter: String) {
        restZoneMockMvc.perform(get("/api/zones?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restZoneMockMvc.perform(get("/api/zones/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingZone() {
        // Get the zone
        restZoneMockMvc.perform(get("/api/zones/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateZone() {
        // Initialize the database
        zoneRepository.saveAndFlush(zoneModel)

        val databaseSizeBeforeUpdate = zoneRepository.findAll().size

        // Update the zone
        val id = zoneModel.id
        assertNotNull(id)
        val updatedZone = zoneRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedZone are not directly saved in db
        em.detach(updatedZone)
        updatedZone.zoneName = UPDATED_ZONE_NAME
        val zoneValueObject = zoneMapper.toDto(updatedZone)

        restZoneMockMvc.perform(
            put("/api/zones")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(zoneValueObject))
        ).andExpect(status().isOk)

        // Validate the Zone in the database
        val zoneList = zoneRepository.findAll()
        assertThat(zoneList).hasSize(databaseSizeBeforeUpdate)
        val testZone = zoneList[zoneList.size - 1]
        assertThat(testZone.zoneName).isEqualTo(UPDATED_ZONE_NAME)
    }

    @Test
    @Transactional
    fun updateNonExistingZone() {
        val databaseSizeBeforeUpdate = zoneRepository.findAll().size

        // Create the Zone
        val zoneValueObject = zoneMapper.toDto(zoneModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restZoneMockMvc.perform(
            put("/api/zones")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(zoneValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Zone in the database
        val zoneList = zoneRepository.findAll()
        assertThat(zoneList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteZone() {
        // Initialize the database
        zoneRepository.saveAndFlush(zoneModel)

        val databaseSizeBeforeDelete = zoneRepository.findAll().size

        val id = zoneModel.id
        assertNotNull(id)

        // Delete the zone
        restZoneMockMvc.perform(
            delete("/api/zones/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val zoneList = zoneRepository.findAll()
        assertThat(zoneList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(ZoneModel::class.java)
        val zoneModel1 = ZoneModel()
        zoneModel1.id = 1L
        val zoneModel2 = ZoneModel()
        zoneModel2.id = zoneModel1.id
        assertThat(zoneModel1).isEqualTo(zoneModel2)
        zoneModel2.id = 2L
        assertThat(zoneModel1).isNotEqualTo(zoneModel2)
        zoneModel1.id = null
        assertThat(zoneModel1).isNotEqualTo(zoneModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(ZoneValueObject::class.java)
        val zoneValueObject1 = ZoneValueObject()
        zoneValueObject1.id = 1L
        val zoneValueObject2 = ZoneValueObject()
        assertThat(zoneValueObject1).isNotEqualTo(zoneValueObject2)
        zoneValueObject2.id = zoneValueObject1.id
        assertThat(zoneValueObject1).isEqualTo(zoneValueObject2)
        zoneValueObject2.id = 2L
        assertThat(zoneValueObject1).isNotEqualTo(zoneValueObject2)
        zoneValueObject1.id = null
        assertThat(zoneValueObject1).isNotEqualTo(zoneValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(zoneMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(zoneMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_ZONE_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_ZONE_NAME = "BBBBBBBBBB"
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): ZoneModel {
            val zoneModel = ZoneModel()
            zoneModel.zoneName = DEFAULT_ZONE_NAME

        return zoneModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): ZoneModel {
            val zoneModel = ZoneModel()
            zoneModel.zoneName = UPDATED_ZONE_NAME

        return zoneModel
        }
    }
}
