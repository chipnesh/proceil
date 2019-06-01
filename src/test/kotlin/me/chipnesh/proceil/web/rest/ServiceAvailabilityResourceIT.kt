package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.ServiceAvailabilityModel
import me.chipnesh.proceil.repository.ServiceAvailabilityRepository
import me.chipnesh.proceil.service.ServiceAvailabilityService
import me.chipnesh.proceil.service.dto.ServiceAvailabilityValueObject
import me.chipnesh.proceil.service.mapper.ServiceAvailabilityMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.ServiceAvailabilityQueryService

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
 * Test class for the ServiceAvailabilityResource REST controller.
 *
 * @see ServiceAvailabilityResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class ServiceAvailabilityResourceIT {

    @Autowired
    private lateinit var serviceAvailabilityRepository: ServiceAvailabilityRepository

    @Autowired
    private lateinit var serviceAvailabilityMapper: ServiceAvailabilityMapper

    @Autowired
    private lateinit var serviceAvailabilityService: ServiceAvailabilityService

    @Autowired
    private lateinit var serviceAvailabilityQueryService: ServiceAvailabilityQueryService

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

    private lateinit var restServiceAvailabilityMockMvc: MockMvc

    private lateinit var serviceAvailabilityModel: ServiceAvailabilityModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val serviceAvailabilityResource = ServiceAvailabilityResource(serviceAvailabilityService, serviceAvailabilityQueryService)
        this.restServiceAvailabilityMockMvc = MockMvcBuilders.standaloneSetup(serviceAvailabilityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        serviceAvailabilityModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createServiceAvailability() {
        val databaseSizeBeforeCreate = serviceAvailabilityRepository.findAll().size

        // Create the ServiceAvailability
        val serviceAvailabilityValueObject = serviceAvailabilityMapper.toDto(serviceAvailabilityModel)
        restServiceAvailabilityMockMvc.perform(
            post("/api/service-availabilities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceAvailabilityValueObject))
        ).andExpect(status().isCreated)

        // Validate the ServiceAvailability in the database
        val serviceAvailabilityList = serviceAvailabilityRepository.findAll()
        assertThat(serviceAvailabilityList).hasSize(databaseSizeBeforeCreate + 1)
        val testServiceAvailability = serviceAvailabilityList[serviceAvailabilityList.size - 1]
        assertThat(testServiceAvailability.availabilitySummary).isEqualTo(DEFAULT_AVAILABILITY_SUMMARY)
        assertThat(testServiceAvailability.dateFrom).isEqualTo(DEFAULT_DATE_FROM)
        assertThat(testServiceAvailability.dateTo).isEqualTo(DEFAULT_DATE_TO)
        assertThat(testServiceAvailability.remainingQuotas).isEqualTo(DEFAULT_REMAINING_QUOTAS)
    }

    @Test
    @Transactional
    fun createServiceAvailabilityWithExistingId() {
        val databaseSizeBeforeCreate = serviceAvailabilityRepository.findAll().size

        // Create the ServiceAvailability with an existing ID
        serviceAvailabilityModel.id = 1L
        val serviceAvailabilityValueObject = serviceAvailabilityMapper.toDto(serviceAvailabilityModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceAvailabilityMockMvc.perform(
            post("/api/service-availabilities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceAvailabilityValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the ServiceAvailability in the database
        val serviceAvailabilityList = serviceAvailabilityRepository.findAll()
        assertThat(serviceAvailabilityList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilities() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList
        restServiceAvailabilityMockMvc.perform(get("/api/service-availabilities?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceAvailabilityModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].availabilitySummary").value(hasItem(DEFAULT_AVAILABILITY_SUMMARY)))
            .andExpect(jsonPath("$.[*].dateFrom").value(hasItem(DEFAULT_DATE_FROM.toString())))
            .andExpect(jsonPath("$.[*].dateTo").value(hasItem(DEFAULT_DATE_TO.toString())))
            .andExpect(jsonPath("$.[*].remainingQuotas").value(hasItem(DEFAULT_REMAINING_QUOTAS)))
    }

    @Test
    @Transactional
    fun getServiceAvailability() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        val id = serviceAvailabilityModel.id
        assertNotNull(id)

        // Get the serviceAvailability
        restServiceAvailabilityMockMvc.perform(get("/api/service-availabilities/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.availabilitySummary").value(DEFAULT_AVAILABILITY_SUMMARY))
            .andExpect(jsonPath("$.dateFrom").value(DEFAULT_DATE_FROM.toString()))
            .andExpect(jsonPath("$.dateTo").value(DEFAULT_DATE_TO.toString()))
            .andExpect(jsonPath("$.remainingQuotas").value(DEFAULT_REMAINING_QUOTAS))
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByAvailabilitySummaryIsEqualToSomething() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList where availabilitySummary equals to DEFAULT_AVAILABILITY_SUMMARY
        defaultServiceAvailabilityShouldBeFound("availabilitySummary.equals=$DEFAULT_AVAILABILITY_SUMMARY")

        // Get all the serviceAvailabilityList where availabilitySummary equals to UPDATED_AVAILABILITY_SUMMARY
        defaultServiceAvailabilityShouldNotBeFound("availabilitySummary.equals=$UPDATED_AVAILABILITY_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByAvailabilitySummaryIsInShouldWork() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList where availabilitySummary in DEFAULT_AVAILABILITY_SUMMARY or UPDATED_AVAILABILITY_SUMMARY
        defaultServiceAvailabilityShouldBeFound("availabilitySummary.in=$DEFAULT_AVAILABILITY_SUMMARY,$UPDATED_AVAILABILITY_SUMMARY")

        // Get all the serviceAvailabilityList where availabilitySummary equals to UPDATED_AVAILABILITY_SUMMARY
        defaultServiceAvailabilityShouldNotBeFound("availabilitySummary.in=$UPDATED_AVAILABILITY_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByAvailabilitySummaryIsNullOrNotNull() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList where availabilitySummary is not null
        defaultServiceAvailabilityShouldBeFound("availabilitySummary.specified=true")

        // Get all the serviceAvailabilityList where availabilitySummary is null
        defaultServiceAvailabilityShouldNotBeFound("availabilitySummary.specified=false")
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByDateFromIsEqualToSomething() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList where dateFrom equals to DEFAULT_DATE_FROM
        defaultServiceAvailabilityShouldBeFound("dateFrom.equals=$DEFAULT_DATE_FROM")

        // Get all the serviceAvailabilityList where dateFrom equals to UPDATED_DATE_FROM
        defaultServiceAvailabilityShouldNotBeFound("dateFrom.equals=$UPDATED_DATE_FROM")
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByDateFromIsInShouldWork() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList where dateFrom in DEFAULT_DATE_FROM or UPDATED_DATE_FROM
        defaultServiceAvailabilityShouldBeFound("dateFrom.in=$DEFAULT_DATE_FROM,$UPDATED_DATE_FROM")

        // Get all the serviceAvailabilityList where dateFrom equals to UPDATED_DATE_FROM
        defaultServiceAvailabilityShouldNotBeFound("dateFrom.in=$UPDATED_DATE_FROM")
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByDateFromIsNullOrNotNull() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList where dateFrom is not null
        defaultServiceAvailabilityShouldBeFound("dateFrom.specified=true")

        // Get all the serviceAvailabilityList where dateFrom is null
        defaultServiceAvailabilityShouldNotBeFound("dateFrom.specified=false")
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByDateToIsEqualToSomething() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList where dateTo equals to DEFAULT_DATE_TO
        defaultServiceAvailabilityShouldBeFound("dateTo.equals=$DEFAULT_DATE_TO")

        // Get all the serviceAvailabilityList where dateTo equals to UPDATED_DATE_TO
        defaultServiceAvailabilityShouldNotBeFound("dateTo.equals=$UPDATED_DATE_TO")
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByDateToIsInShouldWork() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList where dateTo in DEFAULT_DATE_TO or UPDATED_DATE_TO
        defaultServiceAvailabilityShouldBeFound("dateTo.in=$DEFAULT_DATE_TO,$UPDATED_DATE_TO")

        // Get all the serviceAvailabilityList where dateTo equals to UPDATED_DATE_TO
        defaultServiceAvailabilityShouldNotBeFound("dateTo.in=$UPDATED_DATE_TO")
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByDateToIsNullOrNotNull() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList where dateTo is not null
        defaultServiceAvailabilityShouldBeFound("dateTo.specified=true")

        // Get all the serviceAvailabilityList where dateTo is null
        defaultServiceAvailabilityShouldNotBeFound("dateTo.specified=false")
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByRemainingQuotasIsEqualToSomething() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList where remainingQuotas equals to DEFAULT_REMAINING_QUOTAS
        defaultServiceAvailabilityShouldBeFound("remainingQuotas.equals=$DEFAULT_REMAINING_QUOTAS")

        // Get all the serviceAvailabilityList where remainingQuotas equals to UPDATED_REMAINING_QUOTAS
        defaultServiceAvailabilityShouldNotBeFound("remainingQuotas.equals=$UPDATED_REMAINING_QUOTAS")
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByRemainingQuotasIsInShouldWork() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList where remainingQuotas in DEFAULT_REMAINING_QUOTAS or UPDATED_REMAINING_QUOTAS
        defaultServiceAvailabilityShouldBeFound("remainingQuotas.in=$DEFAULT_REMAINING_QUOTAS,$UPDATED_REMAINING_QUOTAS")

        // Get all the serviceAvailabilityList where remainingQuotas equals to UPDATED_REMAINING_QUOTAS
        defaultServiceAvailabilityShouldNotBeFound("remainingQuotas.in=$UPDATED_REMAINING_QUOTAS")
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByRemainingQuotasIsNullOrNotNull() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList where remainingQuotas is not null
        defaultServiceAvailabilityShouldBeFound("remainingQuotas.specified=true")

        // Get all the serviceAvailabilityList where remainingQuotas is null
        defaultServiceAvailabilityShouldNotBeFound("remainingQuotas.specified=false")
    }
    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByRemainingQuotasIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList where remainingQuotas greater than or equals to DEFAULT_REMAINING_QUOTAS
        defaultServiceAvailabilityShouldBeFound("remainingQuotas.greaterOrEqualThan=$DEFAULT_REMAINING_QUOTAS")

        // Get all the serviceAvailabilityList where remainingQuotas greater than or equals to UPDATED_REMAINING_QUOTAS
        defaultServiceAvailabilityShouldNotBeFound("remainingQuotas.greaterOrEqualThan=$UPDATED_REMAINING_QUOTAS")
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByRemainingQuotasIsLessThanSomething() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        // Get all the serviceAvailabilityList where remainingQuotas less than or equals to DEFAULT_REMAINING_QUOTAS
        defaultServiceAvailabilityShouldNotBeFound("remainingQuotas.lessThan=$DEFAULT_REMAINING_QUOTAS")

        // Get all the serviceAvailabilityList where remainingQuotas less than or equals to UPDATED_REMAINING_QUOTAS
        defaultServiceAvailabilityShouldBeFound("remainingQuotas.lessThan=$UPDATED_REMAINING_QUOTAS")
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByServiceIsEqualToSomething() {
        // Initialize the database
        val service = ServiceResourceIT.createEntity(em)
        em.persist(service)
        em.flush()
        serviceAvailabilityModel.service = service
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)
        val serviceId = service.id

        // Get all the serviceAvailabilityList where service equals to serviceId
        defaultServiceAvailabilityShouldBeFound("serviceId.equals=$serviceId")

        // Get all the serviceAvailabilityList where service equals to serviceId + 1
        defaultServiceAvailabilityShouldNotBeFound("serviceId.equals=${serviceId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllServiceAvailabilitiesByProvidedByIsEqualToSomething() {
        // Initialize the database
        val providedBy = ZoneResourceIT.createEntity(em)
        em.persist(providedBy)
        em.flush()
        serviceAvailabilityModel.providedBy = providedBy
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)
        val providedById = providedBy.id

        // Get all the serviceAvailabilityList where providedBy equals to providedById
        defaultServiceAvailabilityShouldBeFound("providedById.equals=$providedById")

        // Get all the serviceAvailabilityList where providedBy equals to providedById + 1
        defaultServiceAvailabilityShouldNotBeFound("providedById.equals=${providedById?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultServiceAvailabilityShouldBeFound(filter: String) {
        restServiceAvailabilityMockMvc.perform(get("/api/service-availabilities?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceAvailabilityModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].availabilitySummary").value(hasItem(DEFAULT_AVAILABILITY_SUMMARY)))
            .andExpect(jsonPath("$.[*].dateFrom").value(hasItem(DEFAULT_DATE_FROM.toString())))
            .andExpect(jsonPath("$.[*].dateTo").value(hasItem(DEFAULT_DATE_TO.toString())))
            .andExpect(jsonPath("$.[*].remainingQuotas").value(hasItem(DEFAULT_REMAINING_QUOTAS)))

        // Check, that the count call also returns 1
        restServiceAvailabilityMockMvc.perform(get("/api/service-availabilities/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultServiceAvailabilityShouldNotBeFound(filter: String) {
        restServiceAvailabilityMockMvc.perform(get("/api/service-availabilities?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restServiceAvailabilityMockMvc.perform(get("/api/service-availabilities/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingServiceAvailability() {
        // Get the serviceAvailability
        restServiceAvailabilityMockMvc.perform(get("/api/service-availabilities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateServiceAvailability() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        val databaseSizeBeforeUpdate = serviceAvailabilityRepository.findAll().size

        // Update the serviceAvailability
        val id = serviceAvailabilityModel.id
        assertNotNull(id)
        val updatedServiceAvailability = serviceAvailabilityRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedServiceAvailability are not directly saved in db
        em.detach(updatedServiceAvailability)
        updatedServiceAvailability.availabilitySummary = UPDATED_AVAILABILITY_SUMMARY
        updatedServiceAvailability.dateFrom = UPDATED_DATE_FROM
        updatedServiceAvailability.dateTo = UPDATED_DATE_TO
        updatedServiceAvailability.remainingQuotas = UPDATED_REMAINING_QUOTAS
        val serviceAvailabilityValueObject = serviceAvailabilityMapper.toDto(updatedServiceAvailability)

        restServiceAvailabilityMockMvc.perform(
            put("/api/service-availabilities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceAvailabilityValueObject))
        ).andExpect(status().isOk)

        // Validate the ServiceAvailability in the database
        val serviceAvailabilityList = serviceAvailabilityRepository.findAll()
        assertThat(serviceAvailabilityList).hasSize(databaseSizeBeforeUpdate)
        val testServiceAvailability = serviceAvailabilityList[serviceAvailabilityList.size - 1]
        assertThat(testServiceAvailability.availabilitySummary).isEqualTo(UPDATED_AVAILABILITY_SUMMARY)
        assertThat(testServiceAvailability.dateFrom).isEqualTo(UPDATED_DATE_FROM)
        assertThat(testServiceAvailability.dateTo).isEqualTo(UPDATED_DATE_TO)
        assertThat(testServiceAvailability.remainingQuotas).isEqualTo(UPDATED_REMAINING_QUOTAS)
    }

    @Test
    @Transactional
    fun updateNonExistingServiceAvailability() {
        val databaseSizeBeforeUpdate = serviceAvailabilityRepository.findAll().size

        // Create the ServiceAvailability
        val serviceAvailabilityValueObject = serviceAvailabilityMapper.toDto(serviceAvailabilityModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceAvailabilityMockMvc.perform(
            put("/api/service-availabilities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceAvailabilityValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the ServiceAvailability in the database
        val serviceAvailabilityList = serviceAvailabilityRepository.findAll()
        assertThat(serviceAvailabilityList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteServiceAvailability() {
        // Initialize the database
        serviceAvailabilityRepository.saveAndFlush(serviceAvailabilityModel)

        val databaseSizeBeforeDelete = serviceAvailabilityRepository.findAll().size

        val id = serviceAvailabilityModel.id
        assertNotNull(id)

        // Delete the serviceAvailability
        restServiceAvailabilityMockMvc.perform(
            delete("/api/service-availabilities/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val serviceAvailabilityList = serviceAvailabilityRepository.findAll()
        assertThat(serviceAvailabilityList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(ServiceAvailabilityModel::class.java)
        val serviceAvailabilityModel1 = ServiceAvailabilityModel()
        serviceAvailabilityModel1.id = 1L
        val serviceAvailabilityModel2 = ServiceAvailabilityModel()
        serviceAvailabilityModel2.id = serviceAvailabilityModel1.id
        assertThat(serviceAvailabilityModel1).isEqualTo(serviceAvailabilityModel2)
        serviceAvailabilityModel2.id = 2L
        assertThat(serviceAvailabilityModel1).isNotEqualTo(serviceAvailabilityModel2)
        serviceAvailabilityModel1.id = null
        assertThat(serviceAvailabilityModel1).isNotEqualTo(serviceAvailabilityModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(ServiceAvailabilityValueObject::class.java)
        val serviceAvailabilityValueObject1 = ServiceAvailabilityValueObject()
        serviceAvailabilityValueObject1.id = 1L
        val serviceAvailabilityValueObject2 = ServiceAvailabilityValueObject()
        assertThat(serviceAvailabilityValueObject1).isNotEqualTo(serviceAvailabilityValueObject2)
        serviceAvailabilityValueObject2.id = serviceAvailabilityValueObject1.id
        assertThat(serviceAvailabilityValueObject1).isEqualTo(serviceAvailabilityValueObject2)
        serviceAvailabilityValueObject2.id = 2L
        assertThat(serviceAvailabilityValueObject1).isNotEqualTo(serviceAvailabilityValueObject2)
        serviceAvailabilityValueObject1.id = null
        assertThat(serviceAvailabilityValueObject1).isNotEqualTo(serviceAvailabilityValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(serviceAvailabilityMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(serviceAvailabilityMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_AVAILABILITY_SUMMARY: String = "AAAAAAAAAA"
        private const val UPDATED_AVAILABILITY_SUMMARY = "BBBBBBBBBB"

        private val DEFAULT_DATE_FROM: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_DATE_FROM: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_DATE_TO: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_DATE_TO: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_REMAINING_QUOTAS: Int = 1
        private const val UPDATED_REMAINING_QUOTAS: Int = 2
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): ServiceAvailabilityModel {
            val serviceAvailabilityModel = ServiceAvailabilityModel()
            serviceAvailabilityModel.availabilitySummary = DEFAULT_AVAILABILITY_SUMMARY
            serviceAvailabilityModel.dateFrom = DEFAULT_DATE_FROM
            serviceAvailabilityModel.dateTo = DEFAULT_DATE_TO
            serviceAvailabilityModel.remainingQuotas = DEFAULT_REMAINING_QUOTAS

        return serviceAvailabilityModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): ServiceAvailabilityModel {
            val serviceAvailabilityModel = ServiceAvailabilityModel()
            serviceAvailabilityModel.availabilitySummary = UPDATED_AVAILABILITY_SUMMARY
            serviceAvailabilityModel.dateFrom = UPDATED_DATE_FROM
            serviceAvailabilityModel.dateTo = UPDATED_DATE_TO
            serviceAvailabilityModel.remainingQuotas = UPDATED_REMAINING_QUOTAS

        return serviceAvailabilityModel
        }
    }
}
