package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.ServiceModel
import me.chipnesh.proceil.repository.ServiceRepository
import me.chipnesh.proceil.service.ServiceService
import me.chipnesh.proceil.service.dto.ServiceValueObject
import me.chipnesh.proceil.service.mapper.ServiceMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.ServiceQueryService

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
import java.math.BigDecimal

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
 * Test class for the ServiceResource REST controller.
 *
 * @see ServiceResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class ServiceResourceIT {

    @Autowired
    private lateinit var serviceRepository: ServiceRepository

    @Autowired
    private lateinit var serviceMapper: ServiceMapper

    @Autowired
    private lateinit var serviceService: ServiceService

    @Autowired
    private lateinit var serviceQueryService: ServiceQueryService

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

    private lateinit var restServiceMockMvc: MockMvc

    private lateinit var serviceModel: ServiceModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val serviceResource = ServiceResource(serviceService, serviceQueryService)
        this.restServiceMockMvc = MockMvcBuilders.standaloneSetup(serviceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        serviceModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createService() {
        val databaseSizeBeforeCreate = serviceRepository.findAll().size

        // Create the Service
        val serviceValueObject = serviceMapper.toDto(serviceModel)
        restServiceMockMvc.perform(
            post("/api/services")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceValueObject))
        ).andExpect(status().isCreated)

        // Validate the Service in the database
        val serviceList = serviceRepository.findAll()
        assertThat(serviceList).hasSize(databaseSizeBeforeCreate + 1)
        val testService = serviceList[serviceList.size - 1]
        assertThat(testService.serviceName).isEqualTo(DEFAULT_SERVICE_NAME)
        assertThat(testService.serviceDescription).isEqualTo(DEFAULT_SERVICE_DESCRIPTION)
        assertThat(testService.servicePrice).isEqualTo(DEFAULT_SERVICE_PRICE)
    }

    @Test
    @Transactional
    fun createServiceWithExistingId() {
        val databaseSizeBeforeCreate = serviceRepository.findAll().size

        // Create the Service with an existing ID
        serviceModel.id = 1L
        val serviceValueObject = serviceMapper.toDto(serviceModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceMockMvc.perform(
            post("/api/services")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Service in the database
        val serviceList = serviceRepository.findAll()
        assertThat(serviceList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllServices() {
        // Initialize the database
        serviceRepository.saveAndFlush(serviceModel)

        // Get all the serviceList
        restServiceMockMvc.perform(get("/api/services?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].serviceName").value(hasItem(DEFAULT_SERVICE_NAME)))
            .andExpect(jsonPath("$.[*].serviceDescription").value(hasItem(DEFAULT_SERVICE_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].servicePrice").value(hasItem(DEFAULT_SERVICE_PRICE.toInt())))
    }

    @Test
    @Transactional
    fun getService() {
        // Initialize the database
        serviceRepository.saveAndFlush(serviceModel)

        val id = serviceModel.id
        assertNotNull(id)

        // Get the service
        restServiceMockMvc.perform(get("/api/services/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.serviceName").value(DEFAULT_SERVICE_NAME))
            .andExpect(jsonPath("$.serviceDescription").value(DEFAULT_SERVICE_DESCRIPTION))
            .andExpect(jsonPath("$.servicePrice").value(DEFAULT_SERVICE_PRICE.toInt()))
    }

    @Test
    @Transactional
    fun getAllServicesByServiceNameIsEqualToSomething() {
        // Initialize the database
        serviceRepository.saveAndFlush(serviceModel)

        // Get all the serviceList where serviceName equals to DEFAULT_SERVICE_NAME
        defaultServiceShouldBeFound("serviceName.equals=$DEFAULT_SERVICE_NAME")

        // Get all the serviceList where serviceName equals to UPDATED_SERVICE_NAME
        defaultServiceShouldNotBeFound("serviceName.equals=$UPDATED_SERVICE_NAME")
    }

    @Test
    @Transactional
    fun getAllServicesByServiceNameIsInShouldWork() {
        // Initialize the database
        serviceRepository.saveAndFlush(serviceModel)

        // Get all the serviceList where serviceName in DEFAULT_SERVICE_NAME or UPDATED_SERVICE_NAME
        defaultServiceShouldBeFound("serviceName.in=$DEFAULT_SERVICE_NAME,$UPDATED_SERVICE_NAME")

        // Get all the serviceList where serviceName equals to UPDATED_SERVICE_NAME
        defaultServiceShouldNotBeFound("serviceName.in=$UPDATED_SERVICE_NAME")
    }

    @Test
    @Transactional
    fun getAllServicesByServiceNameIsNullOrNotNull() {
        // Initialize the database
        serviceRepository.saveAndFlush(serviceModel)

        // Get all the serviceList where serviceName is not null
        defaultServiceShouldBeFound("serviceName.specified=true")

        // Get all the serviceList where serviceName is null
        defaultServiceShouldNotBeFound("serviceName.specified=false")
    }

    @Test
    @Transactional
    fun getAllServicesByServiceDescriptionIsEqualToSomething() {
        // Initialize the database
        serviceRepository.saveAndFlush(serviceModel)

        // Get all the serviceList where serviceDescription equals to DEFAULT_SERVICE_DESCRIPTION
        defaultServiceShouldBeFound("serviceDescription.equals=$DEFAULT_SERVICE_DESCRIPTION")

        // Get all the serviceList where serviceDescription equals to UPDATED_SERVICE_DESCRIPTION
        defaultServiceShouldNotBeFound("serviceDescription.equals=$UPDATED_SERVICE_DESCRIPTION")
    }

    @Test
    @Transactional
    fun getAllServicesByServiceDescriptionIsInShouldWork() {
        // Initialize the database
        serviceRepository.saveAndFlush(serviceModel)

        // Get all the serviceList where serviceDescription in DEFAULT_SERVICE_DESCRIPTION or UPDATED_SERVICE_DESCRIPTION
        defaultServiceShouldBeFound("serviceDescription.in=$DEFAULT_SERVICE_DESCRIPTION,$UPDATED_SERVICE_DESCRIPTION")

        // Get all the serviceList where serviceDescription equals to UPDATED_SERVICE_DESCRIPTION
        defaultServiceShouldNotBeFound("serviceDescription.in=$UPDATED_SERVICE_DESCRIPTION")
    }

    @Test
    @Transactional
    fun getAllServicesByServiceDescriptionIsNullOrNotNull() {
        // Initialize the database
        serviceRepository.saveAndFlush(serviceModel)

        // Get all the serviceList where serviceDescription is not null
        defaultServiceShouldBeFound("serviceDescription.specified=true")

        // Get all the serviceList where serviceDescription is null
        defaultServiceShouldNotBeFound("serviceDescription.specified=false")
    }

    @Test
    @Transactional
    fun getAllServicesByServicePriceIsEqualToSomething() {
        // Initialize the database
        serviceRepository.saveAndFlush(serviceModel)

        // Get all the serviceList where servicePrice equals to DEFAULT_SERVICE_PRICE
        defaultServiceShouldBeFound("servicePrice.equals=$DEFAULT_SERVICE_PRICE")

        // Get all the serviceList where servicePrice equals to UPDATED_SERVICE_PRICE
        defaultServiceShouldNotBeFound("servicePrice.equals=$UPDATED_SERVICE_PRICE")
    }

    @Test
    @Transactional
    fun getAllServicesByServicePriceIsInShouldWork() {
        // Initialize the database
        serviceRepository.saveAndFlush(serviceModel)

        // Get all the serviceList where servicePrice in DEFAULT_SERVICE_PRICE or UPDATED_SERVICE_PRICE
        defaultServiceShouldBeFound("servicePrice.in=$DEFAULT_SERVICE_PRICE,$UPDATED_SERVICE_PRICE")

        // Get all the serviceList where servicePrice equals to UPDATED_SERVICE_PRICE
        defaultServiceShouldNotBeFound("servicePrice.in=$UPDATED_SERVICE_PRICE")
    }

    @Test
    @Transactional
    fun getAllServicesByServicePriceIsNullOrNotNull() {
        // Initialize the database
        serviceRepository.saveAndFlush(serviceModel)

        // Get all the serviceList where servicePrice is not null
        defaultServiceShouldBeFound("servicePrice.specified=true")

        // Get all the serviceList where servicePrice is null
        defaultServiceShouldNotBeFound("servicePrice.specified=false")
    }

    @Test
    @Transactional
    fun getAllServicesByImageIsEqualToSomething() {
        // Initialize the database
        val image = AttachedImageResourceIT.createEntity(em)
        em.persist(image)
        em.flush()
        serviceModel.addImage(image)
        serviceRepository.saveAndFlush(serviceModel)
        val imageId = image.id

        // Get all the serviceList where image equals to imageId
        defaultServiceShouldBeFound("imageId.equals=$imageId")

        // Get all the serviceList where image equals to imageId + 1
        defaultServiceShouldNotBeFound("imageId.equals=${imageId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultServiceShouldBeFound(filter: String) {
        restServiceMockMvc.perform(get("/api/services?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(serviceModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].serviceName").value(hasItem(DEFAULT_SERVICE_NAME)))
            .andExpect(jsonPath("$.[*].serviceDescription").value(hasItem(DEFAULT_SERVICE_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].servicePrice").value(hasItem(DEFAULT_SERVICE_PRICE.toInt())))

        // Check, that the count call also returns 1
        restServiceMockMvc.perform(get("/api/services/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultServiceShouldNotBeFound(filter: String) {
        restServiceMockMvc.perform(get("/api/services?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restServiceMockMvc.perform(get("/api/services/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingService() {
        // Get the service
        restServiceMockMvc.perform(get("/api/services/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateService() {
        // Initialize the database
        serviceRepository.saveAndFlush(serviceModel)

        val databaseSizeBeforeUpdate = serviceRepository.findAll().size

        // Update the service
        val id = serviceModel.id
        assertNotNull(id)
        val updatedService = serviceRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedService are not directly saved in db
        em.detach(updatedService)
        updatedService.serviceName = UPDATED_SERVICE_NAME
        updatedService.serviceDescription = UPDATED_SERVICE_DESCRIPTION
        updatedService.servicePrice = UPDATED_SERVICE_PRICE
        val serviceValueObject = serviceMapper.toDto(updatedService)

        restServiceMockMvc.perform(
            put("/api/services")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceValueObject))
        ).andExpect(status().isOk)

        // Validate the Service in the database
        val serviceList = serviceRepository.findAll()
        assertThat(serviceList).hasSize(databaseSizeBeforeUpdate)
        val testService = serviceList[serviceList.size - 1]
        assertThat(testService.serviceName).isEqualTo(UPDATED_SERVICE_NAME)
        assertThat(testService.serviceDescription).isEqualTo(UPDATED_SERVICE_DESCRIPTION)
        assertThat(testService.servicePrice).isEqualTo(UPDATED_SERVICE_PRICE)
    }

    @Test
    @Transactional
    fun updateNonExistingService() {
        val databaseSizeBeforeUpdate = serviceRepository.findAll().size

        // Create the Service
        val serviceValueObject = serviceMapper.toDto(serviceModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceMockMvc.perform(
            put("/api/services")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(serviceValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Service in the database
        val serviceList = serviceRepository.findAll()
        assertThat(serviceList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteService() {
        // Initialize the database
        serviceRepository.saveAndFlush(serviceModel)

        val databaseSizeBeforeDelete = serviceRepository.findAll().size

        val id = serviceModel.id
        assertNotNull(id)

        // Delete the service
        restServiceMockMvc.perform(
            delete("/api/services/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val serviceList = serviceRepository.findAll()
        assertThat(serviceList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(ServiceModel::class.java)
        val serviceModel1 = ServiceModel()
        serviceModel1.id = 1L
        val serviceModel2 = ServiceModel()
        serviceModel2.id = serviceModel1.id
        assertThat(serviceModel1).isEqualTo(serviceModel2)
        serviceModel2.id = 2L
        assertThat(serviceModel1).isNotEqualTo(serviceModel2)
        serviceModel1.id = null
        assertThat(serviceModel1).isNotEqualTo(serviceModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(ServiceValueObject::class.java)
        val serviceValueObject1 = ServiceValueObject()
        serviceValueObject1.id = 1L
        val serviceValueObject2 = ServiceValueObject()
        assertThat(serviceValueObject1).isNotEqualTo(serviceValueObject2)
        serviceValueObject2.id = serviceValueObject1.id
        assertThat(serviceValueObject1).isEqualTo(serviceValueObject2)
        serviceValueObject2.id = 2L
        assertThat(serviceValueObject1).isNotEqualTo(serviceValueObject2)
        serviceValueObject1.id = null
        assertThat(serviceValueObject1).isNotEqualTo(serviceValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(serviceMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(serviceMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_SERVICE_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_SERVICE_NAME = "BBBBBBBBBB"

        private const val DEFAULT_SERVICE_DESCRIPTION: String = "AAAAAAAAAA"
        private const val UPDATED_SERVICE_DESCRIPTION = "BBBBBBBBBB"

        private val DEFAULT_SERVICE_PRICE: BigDecimal = BigDecimal(1)
        private val UPDATED_SERVICE_PRICE: BigDecimal = BigDecimal(2)
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): ServiceModel {
            val serviceModel = ServiceModel()
            serviceModel.serviceName = DEFAULT_SERVICE_NAME
            serviceModel.serviceDescription = DEFAULT_SERVICE_DESCRIPTION
            serviceModel.servicePrice = DEFAULT_SERVICE_PRICE

        return serviceModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): ServiceModel {
            val serviceModel = ServiceModel()
            serviceModel.serviceName = UPDATED_SERVICE_NAME
            serviceModel.serviceDescription = UPDATED_SERVICE_DESCRIPTION
            serviceModel.servicePrice = UPDATED_SERVICE_PRICE

        return serviceModel
        }
    }
}
