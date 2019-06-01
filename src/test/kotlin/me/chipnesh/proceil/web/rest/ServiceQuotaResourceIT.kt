package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.ServiceQuotaModel
import me.chipnesh.proceil.repository.ServiceQuotaRepository
import me.chipnesh.proceil.service.ServiceQuotaService
import me.chipnesh.proceil.service.dto.ServiceQuotaValueObject
import me.chipnesh.proceil.service.mapper.ServiceQuotaMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.ServiceQuotaQueryService

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

import me.chipnesh.proceil.domain.enumeration.ServiceQuotingStatus
/**
 * Test class for the ServiceQuotaResource REST controller.
 *
 * @see ServiceQuotaResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class ServiceQuotaResourceIT {

    @Autowired
    private lateinit var serviceQuotaRepository: ServiceQuotaRepository

    @Autowired
    private lateinit var serviceQuotaMapper: ServiceQuotaMapper

    @Autowired
    private lateinit var serviceQuotaService: ServiceQuotaService

    @Autowired
    private lateinit var serviceQuotaQueryService: ServiceQuotaQueryService

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

    private lateinit var restServiceQuotaMockMvc: MockMvc

    private lateinit var serviceQuotaModel: ServiceQuotaModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val serviceQuotaResource = ServiceQuotaResource(serviceQuotaService, serviceQuotaQueryService)
        this.restServiceQuotaMockMvc = MockMvcBuilders.standaloneSetup(serviceQuotaResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        serviceQuotaModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createServiceQuota() {
        val databaseSizeBeforeCreate = serviceQuotaRepository.findAll().size

        // Create the ServiceQuota
        val serviceQuotaValueObject = serviceQuotaMapper.toDto(serviceQuotaModel)
        restServiceQuotaMockMvc.perform(
            post("/api/service-quotas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceQuotaValueObject))
        ).andExpect(status().isCreated)

        // Validate the ServiceQuota in the database
        val serviceQuotaList = serviceQuotaRepository.findAll()
        assertThat(serviceQuotaList).hasSize(databaseSizeBeforeCreate + 1)
        val testServiceQuota = serviceQuotaList[serviceQuotaList.size - 1]
        assertThat(testServiceQuota.dateFrom).isEqualTo(DEFAULT_DATE_FROM)
        assertThat(testServiceQuota.dateTo).isEqualTo(DEFAULT_DATE_TO)
        assertThat(testServiceQuota.quotaStatus).isEqualTo(DEFAULT_QUOTA_STATUS)
        assertThat(testServiceQuota.quantityToQuote).isEqualTo(DEFAULT_QUANTITY_TO_QUOTE)
    }

    @Test
    @Transactional
    fun createServiceQuotaWithExistingId() {
        val databaseSizeBeforeCreate = serviceQuotaRepository.findAll().size

        // Create the ServiceQuota with an existing ID
        serviceQuotaModel.id = 1L
        val serviceQuotaValueObject = serviceQuotaMapper.toDto(serviceQuotaModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceQuotaMockMvc.perform(
            post("/api/service-quotas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceQuotaValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the ServiceQuota in the database
        val serviceQuotaList = serviceQuotaRepository.findAll()
        assertThat(serviceQuotaList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllServiceQuotas() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList
        restServiceQuotaMockMvc.perform(get("/api/service-quotas?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceQuotaModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].dateFrom").value(hasItem(DEFAULT_DATE_FROM.toString())))
            .andExpect(jsonPath("$.[*].dateTo").value(hasItem(DEFAULT_DATE_TO.toString())))
            .andExpect(jsonPath("$.[*].quotaStatus").value(hasItem(DEFAULT_QUOTA_STATUS.toString())))
            .andExpect(jsonPath("$.[*].quantityToQuote").value(hasItem(DEFAULT_QUANTITY_TO_QUOTE)))
    }

    @Test
    @Transactional
    fun getServiceQuota() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        val id = serviceQuotaModel.id
        assertNotNull(id)

        // Get the serviceQuota
        restServiceQuotaMockMvc.perform(get("/api/service-quotas/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.dateFrom").value(DEFAULT_DATE_FROM.toString()))
            .andExpect(jsonPath("$.dateTo").value(DEFAULT_DATE_TO.toString()))
            .andExpect(jsonPath("$.quotaStatus").value(DEFAULT_QUOTA_STATUS.toString()))
            .andExpect(jsonPath("$.quantityToQuote").value(DEFAULT_QUANTITY_TO_QUOTE))
    }

    @Test
    @Transactional
    fun getAllServiceQuotasByDateFromIsEqualToSomething() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList where dateFrom equals to DEFAULT_DATE_FROM
        defaultServiceQuotaShouldBeFound("dateFrom.equals=$DEFAULT_DATE_FROM")

        // Get all the serviceQuotaList where dateFrom equals to UPDATED_DATE_FROM
        defaultServiceQuotaShouldNotBeFound("dateFrom.equals=$UPDATED_DATE_FROM")
    }

    @Test
    @Transactional
    fun getAllServiceQuotasByDateFromIsInShouldWork() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList where dateFrom in DEFAULT_DATE_FROM or UPDATED_DATE_FROM
        defaultServiceQuotaShouldBeFound("dateFrom.in=$DEFAULT_DATE_FROM,$UPDATED_DATE_FROM")

        // Get all the serviceQuotaList where dateFrom equals to UPDATED_DATE_FROM
        defaultServiceQuotaShouldNotBeFound("dateFrom.in=$UPDATED_DATE_FROM")
    }

    @Test
    @Transactional
    fun getAllServiceQuotasByDateFromIsNullOrNotNull() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList where dateFrom is not null
        defaultServiceQuotaShouldBeFound("dateFrom.specified=true")

        // Get all the serviceQuotaList where dateFrom is null
        defaultServiceQuotaShouldNotBeFound("dateFrom.specified=false")
    }

    @Test
    @Transactional
    fun getAllServiceQuotasByDateToIsEqualToSomething() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList where dateTo equals to DEFAULT_DATE_TO
        defaultServiceQuotaShouldBeFound("dateTo.equals=$DEFAULT_DATE_TO")

        // Get all the serviceQuotaList where dateTo equals to UPDATED_DATE_TO
        defaultServiceQuotaShouldNotBeFound("dateTo.equals=$UPDATED_DATE_TO")
    }

    @Test
    @Transactional
    fun getAllServiceQuotasByDateToIsInShouldWork() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList where dateTo in DEFAULT_DATE_TO or UPDATED_DATE_TO
        defaultServiceQuotaShouldBeFound("dateTo.in=$DEFAULT_DATE_TO,$UPDATED_DATE_TO")

        // Get all the serviceQuotaList where dateTo equals to UPDATED_DATE_TO
        defaultServiceQuotaShouldNotBeFound("dateTo.in=$UPDATED_DATE_TO")
    }

    @Test
    @Transactional
    fun getAllServiceQuotasByDateToIsNullOrNotNull() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList where dateTo is not null
        defaultServiceQuotaShouldBeFound("dateTo.specified=true")

        // Get all the serviceQuotaList where dateTo is null
        defaultServiceQuotaShouldNotBeFound("dateTo.specified=false")
    }

    @Test
    @Transactional
    fun getAllServiceQuotasByQuotaStatusIsEqualToSomething() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList where quotaStatus equals to DEFAULT_QUOTA_STATUS
        defaultServiceQuotaShouldBeFound("quotaStatus.equals=$DEFAULT_QUOTA_STATUS")

        // Get all the serviceQuotaList where quotaStatus equals to UPDATED_QUOTA_STATUS
        defaultServiceQuotaShouldNotBeFound("quotaStatus.equals=$UPDATED_QUOTA_STATUS")
    }

    @Test
    @Transactional
    fun getAllServiceQuotasByQuotaStatusIsInShouldWork() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList where quotaStatus in DEFAULT_QUOTA_STATUS or UPDATED_QUOTA_STATUS
        defaultServiceQuotaShouldBeFound("quotaStatus.in=$DEFAULT_QUOTA_STATUS,$UPDATED_QUOTA_STATUS")

        // Get all the serviceQuotaList where quotaStatus equals to UPDATED_QUOTA_STATUS
        defaultServiceQuotaShouldNotBeFound("quotaStatus.in=$UPDATED_QUOTA_STATUS")
    }

    @Test
    @Transactional
    fun getAllServiceQuotasByQuotaStatusIsNullOrNotNull() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList where quotaStatus is not null
        defaultServiceQuotaShouldBeFound("quotaStatus.specified=true")

        // Get all the serviceQuotaList where quotaStatus is null
        defaultServiceQuotaShouldNotBeFound("quotaStatus.specified=false")
    }

    @Test
    @Transactional
    fun getAllServiceQuotasByQuantityToQuoteIsEqualToSomething() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList where quantityToQuote equals to DEFAULT_QUANTITY_TO_QUOTE
        defaultServiceQuotaShouldBeFound("quantityToQuote.equals=$DEFAULT_QUANTITY_TO_QUOTE")

        // Get all the serviceQuotaList where quantityToQuote equals to UPDATED_QUANTITY_TO_QUOTE
        defaultServiceQuotaShouldNotBeFound("quantityToQuote.equals=$UPDATED_QUANTITY_TO_QUOTE")
    }

    @Test
    @Transactional
    fun getAllServiceQuotasByQuantityToQuoteIsInShouldWork() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList where quantityToQuote in DEFAULT_QUANTITY_TO_QUOTE or UPDATED_QUANTITY_TO_QUOTE
        defaultServiceQuotaShouldBeFound("quantityToQuote.in=$DEFAULT_QUANTITY_TO_QUOTE,$UPDATED_QUANTITY_TO_QUOTE")

        // Get all the serviceQuotaList where quantityToQuote equals to UPDATED_QUANTITY_TO_QUOTE
        defaultServiceQuotaShouldNotBeFound("quantityToQuote.in=$UPDATED_QUANTITY_TO_QUOTE")
    }

    @Test
    @Transactional
    fun getAllServiceQuotasByQuantityToQuoteIsNullOrNotNull() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList where quantityToQuote is not null
        defaultServiceQuotaShouldBeFound("quantityToQuote.specified=true")

        // Get all the serviceQuotaList where quantityToQuote is null
        defaultServiceQuotaShouldNotBeFound("quantityToQuote.specified=false")
    }
    @Test
    @Transactional
    fun getAllServiceQuotasByQuantityToQuoteIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList where quantityToQuote greater than or equals to DEFAULT_QUANTITY_TO_QUOTE
        defaultServiceQuotaShouldBeFound("quantityToQuote.greaterOrEqualThan=$DEFAULT_QUANTITY_TO_QUOTE")

        // Get all the serviceQuotaList where quantityToQuote greater than or equals to UPDATED_QUANTITY_TO_QUOTE
        defaultServiceQuotaShouldNotBeFound("quantityToQuote.greaterOrEqualThan=$UPDATED_QUANTITY_TO_QUOTE")
    }

    @Test
    @Transactional
    fun getAllServiceQuotasByQuantityToQuoteIsLessThanSomething() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        // Get all the serviceQuotaList where quantityToQuote less than or equals to DEFAULT_QUANTITY_TO_QUOTE
        defaultServiceQuotaShouldNotBeFound("quantityToQuote.lessThan=$DEFAULT_QUANTITY_TO_QUOTE")

        // Get all the serviceQuotaList where quantityToQuote less than or equals to UPDATED_QUANTITY_TO_QUOTE
        defaultServiceQuotaShouldBeFound("quantityToQuote.lessThan=$UPDATED_QUANTITY_TO_QUOTE")
    }

    @Test
    @Transactional
    fun getAllServiceQuotasByServiceIsEqualToSomething() {
        // Initialize the database
        val service = ServiceResourceIT.createEntity(em)
        em.persist(service)
        em.flush()
        serviceQuotaModel.service = service
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)
        val serviceId = service.id

        // Get all the serviceQuotaList where service equals to serviceId
        defaultServiceQuotaShouldBeFound("serviceId.equals=$serviceId")

        // Get all the serviceQuotaList where service equals to serviceId + 1
        defaultServiceQuotaShouldNotBeFound("serviceId.equals=${serviceId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultServiceQuotaShouldBeFound(filter: String) {
        restServiceQuotaMockMvc.perform(get("/api/service-quotas?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceQuotaModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].dateFrom").value(hasItem(DEFAULT_DATE_FROM.toString())))
            .andExpect(jsonPath("$.[*].dateTo").value(hasItem(DEFAULT_DATE_TO.toString())))
            .andExpect(jsonPath("$.[*].quotaStatus").value(hasItem(DEFAULT_QUOTA_STATUS.toString())))
            .andExpect(jsonPath("$.[*].quantityToQuote").value(hasItem(DEFAULT_QUANTITY_TO_QUOTE)))

        // Check, that the count call also returns 1
        restServiceQuotaMockMvc.perform(get("/api/service-quotas/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultServiceQuotaShouldNotBeFound(filter: String) {
        restServiceQuotaMockMvc.perform(get("/api/service-quotas?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restServiceQuotaMockMvc.perform(get("/api/service-quotas/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingServiceQuota() {
        // Get the serviceQuota
        restServiceQuotaMockMvc.perform(get("/api/service-quotas/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateServiceQuota() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        val databaseSizeBeforeUpdate = serviceQuotaRepository.findAll().size

        // Update the serviceQuota
        val id = serviceQuotaModel.id
        assertNotNull(id)
        val updatedServiceQuota = serviceQuotaRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedServiceQuota are not directly saved in db
        em.detach(updatedServiceQuota)
        updatedServiceQuota.dateFrom = UPDATED_DATE_FROM
        updatedServiceQuota.dateTo = UPDATED_DATE_TO
        updatedServiceQuota.quotaStatus = UPDATED_QUOTA_STATUS
        updatedServiceQuota.quantityToQuote = UPDATED_QUANTITY_TO_QUOTE
        val serviceQuotaValueObject = serviceQuotaMapper.toDto(updatedServiceQuota)

        restServiceQuotaMockMvc.perform(
            put("/api/service-quotas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceQuotaValueObject))
        ).andExpect(status().isOk)

        // Validate the ServiceQuota in the database
        val serviceQuotaList = serviceQuotaRepository.findAll()
        assertThat(serviceQuotaList).hasSize(databaseSizeBeforeUpdate)
        val testServiceQuota = serviceQuotaList[serviceQuotaList.size - 1]
        assertThat(testServiceQuota.dateFrom).isEqualTo(UPDATED_DATE_FROM)
        assertThat(testServiceQuota.dateTo).isEqualTo(UPDATED_DATE_TO)
        assertThat(testServiceQuota.quotaStatus).isEqualTo(UPDATED_QUOTA_STATUS)
        assertThat(testServiceQuota.quantityToQuote).isEqualTo(UPDATED_QUANTITY_TO_QUOTE)
    }

    @Test
    @Transactional
    fun updateNonExistingServiceQuota() {
        val databaseSizeBeforeUpdate = serviceQuotaRepository.findAll().size

        // Create the ServiceQuota
        val serviceQuotaValueObject = serviceQuotaMapper.toDto(serviceQuotaModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceQuotaMockMvc.perform(
            put("/api/service-quotas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceQuotaValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the ServiceQuota in the database
        val serviceQuotaList = serviceQuotaRepository.findAll()
        assertThat(serviceQuotaList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteServiceQuota() {
        // Initialize the database
        serviceQuotaRepository.saveAndFlush(serviceQuotaModel)

        val databaseSizeBeforeDelete = serviceQuotaRepository.findAll().size

        val id = serviceQuotaModel.id
        assertNotNull(id)

        // Delete the serviceQuota
        restServiceQuotaMockMvc.perform(
            delete("/api/service-quotas/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val serviceQuotaList = serviceQuotaRepository.findAll()
        assertThat(serviceQuotaList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(ServiceQuotaModel::class.java)
        val serviceQuotaModel1 = ServiceQuotaModel()
        serviceQuotaModel1.id = 1L
        val serviceQuotaModel2 = ServiceQuotaModel()
        serviceQuotaModel2.id = serviceQuotaModel1.id
        assertThat(serviceQuotaModel1).isEqualTo(serviceQuotaModel2)
        serviceQuotaModel2.id = 2L
        assertThat(serviceQuotaModel1).isNotEqualTo(serviceQuotaModel2)
        serviceQuotaModel1.id = null
        assertThat(serviceQuotaModel1).isNotEqualTo(serviceQuotaModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(ServiceQuotaValueObject::class.java)
        val serviceQuotaValueObject1 = ServiceQuotaValueObject()
        serviceQuotaValueObject1.id = 1L
        val serviceQuotaValueObject2 = ServiceQuotaValueObject()
        assertThat(serviceQuotaValueObject1).isNotEqualTo(serviceQuotaValueObject2)
        serviceQuotaValueObject2.id = serviceQuotaValueObject1.id
        assertThat(serviceQuotaValueObject1).isEqualTo(serviceQuotaValueObject2)
        serviceQuotaValueObject2.id = 2L
        assertThat(serviceQuotaValueObject1).isNotEqualTo(serviceQuotaValueObject2)
        serviceQuotaValueObject1.id = null
        assertThat(serviceQuotaValueObject1).isNotEqualTo(serviceQuotaValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(serviceQuotaMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(serviceQuotaMapper.fromId(null)).isNull()
    }

    companion object {

        private val DEFAULT_DATE_FROM: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_DATE_FROM: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_DATE_TO: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_DATE_TO: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_QUOTA_STATUS: ServiceQuotingStatus = ServiceQuotingStatus.NEW
        private val UPDATED_QUOTA_STATUS: ServiceQuotingStatus = ServiceQuotingStatus.QUOTED

        private const val DEFAULT_QUANTITY_TO_QUOTE: Int = 1
        private const val UPDATED_QUANTITY_TO_QUOTE: Int = 2
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): ServiceQuotaModel {
            val serviceQuotaModel = ServiceQuotaModel()
            serviceQuotaModel.dateFrom = DEFAULT_DATE_FROM
            serviceQuotaModel.dateTo = DEFAULT_DATE_TO
            serviceQuotaModel.quotaStatus = DEFAULT_QUOTA_STATUS
            serviceQuotaModel.quantityToQuote = DEFAULT_QUANTITY_TO_QUOTE

        return serviceQuotaModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): ServiceQuotaModel {
            val serviceQuotaModel = ServiceQuotaModel()
            serviceQuotaModel.dateFrom = UPDATED_DATE_FROM
            serviceQuotaModel.dateTo = UPDATED_DATE_TO
            serviceQuotaModel.quotaStatus = UPDATED_QUOTA_STATUS
            serviceQuotaModel.quantityToQuote = UPDATED_QUANTITY_TO_QUOTE

        return serviceQuotaModel
        }
    }
}
