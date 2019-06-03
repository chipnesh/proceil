package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.MaterialRequestModel
import me.chipnesh.proceil.repository.MaterialRequestRepository
import me.chipnesh.proceil.service.MaterialRequestService
import me.chipnesh.proceil.service.dto.MaterialRequestValueObject
import me.chipnesh.proceil.service.mapper.MaterialRequestMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.MaterialRequestQueryService

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

import me.chipnesh.proceil.domain.enumeration.MaterialRequestStatus
import me.chipnesh.proceil.domain.enumeration.MeasureUnit
/**
 * Test class for the MaterialRequestResource REST controller.
 *
 * @see MaterialRequestResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class MaterialRequestResourceIT {

    @Autowired
    private lateinit var materialRequestRepository: MaterialRequestRepository

    @Autowired
    private lateinit var materialRequestMapper: MaterialRequestMapper

    @Autowired
    private lateinit var materialRequestService: MaterialRequestService

    @Autowired
    private lateinit var materialRequestQueryService: MaterialRequestQueryService

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

    private lateinit var restMaterialRequestMockMvc: MockMvc

    private lateinit var materialRequestModel: MaterialRequestModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val materialRequestResource = MaterialRequestResource(materialRequestService, materialRequestQueryService)
        this.restMaterialRequestMockMvc = MockMvcBuilders.standaloneSetup(materialRequestResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        materialRequestModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createMaterialRequest() {
        val databaseSizeBeforeCreate = materialRequestRepository.findAll().size

        // Create the MaterialRequest
        val materialRequestValueObject = materialRequestMapper.toDto(materialRequestModel)
        restMaterialRequestMockMvc.perform(
            post("/api/material-requests")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialRequestValueObject))
        ).andExpect(status().isCreated)

        // Validate the MaterialRequest in the database
        val materialRequestList = materialRequestRepository.findAll()
        assertThat(materialRequestList).hasSize(databaseSizeBeforeCreate + 1)
        val testMaterialRequest = materialRequestList[materialRequestList.size - 1]
        assertThat(testMaterialRequest.requestSummary).isEqualTo(DEFAULT_REQUEST_SUMMARY)
        assertThat(testMaterialRequest.createdDate).isEqualTo(DEFAULT_CREATED_DATE)
        assertThat(testMaterialRequest.closedDate).isEqualTo(DEFAULT_CLOSED_DATE)
        assertThat(testMaterialRequest.requestNote).isEqualTo(DEFAULT_REQUEST_NOTE)
        assertThat(testMaterialRequest.requestPriority).isEqualTo(DEFAULT_REQUEST_PRIORITY)
        assertThat(testMaterialRequest.requestStatus).isEqualTo(DEFAULT_REQUEST_STATUS)
        assertThat(testMaterialRequest.requestedQuantity).isEqualTo(DEFAULT_REQUESTED_QUANTITY)
        assertThat(testMaterialRequest.measureUnit).isEqualTo(DEFAULT_MEASURE_UNIT)
    }

    @Test
    @Transactional
    fun createMaterialRequestWithExistingId() {
        val databaseSizeBeforeCreate = materialRequestRepository.findAll().size

        // Create the MaterialRequest with an existing ID
        materialRequestModel.id = 1L
        val materialRequestValueObject = materialRequestMapper.toDto(materialRequestModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restMaterialRequestMockMvc.perform(
            post("/api/material-requests")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialRequestValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the MaterialRequest in the database
        val materialRequestList = materialRequestRepository.findAll()
        assertThat(materialRequestList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllMaterialRequests() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList
        restMaterialRequestMockMvc.perform(get("/api/material-requests?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(materialRequestModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].requestSummary").value(hasItem(DEFAULT_REQUEST_SUMMARY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].closedDate").value(hasItem(DEFAULT_CLOSED_DATE.toString())))
            .andExpect(jsonPath("$.[*].requestNote").value(hasItem(DEFAULT_REQUEST_NOTE.toString())))
            .andExpect(jsonPath("$.[*].requestPriority").value(hasItem(DEFAULT_REQUEST_PRIORITY)))
            .andExpect(jsonPath("$.[*].requestStatus").value(hasItem(DEFAULT_REQUEST_STATUS.toString())))
            .andExpect(jsonPath("$.[*].requestedQuantity").value(hasItem(DEFAULT_REQUESTED_QUANTITY)))
            .andExpect(jsonPath("$.[*].measureUnit").value(hasItem(DEFAULT_MEASURE_UNIT.toString())))
    }

    @Test
    @Transactional
    fun getMaterialRequest() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        val id = materialRequestModel.id
        assertNotNull(id)

        // Get the materialRequest
        restMaterialRequestMockMvc.perform(get("/api/material-requests/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.requestSummary").value(DEFAULT_REQUEST_SUMMARY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.closedDate").value(DEFAULT_CLOSED_DATE.toString()))
            .andExpect(jsonPath("$.requestNote").value(DEFAULT_REQUEST_NOTE.toString()))
            .andExpect(jsonPath("$.requestPriority").value(DEFAULT_REQUEST_PRIORITY))
            .andExpect(jsonPath("$.requestStatus").value(DEFAULT_REQUEST_STATUS.toString()))
            .andExpect(jsonPath("$.requestedQuantity").value(DEFAULT_REQUESTED_QUANTITY))
            .andExpect(jsonPath("$.measureUnit").value(DEFAULT_MEASURE_UNIT.toString()))
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestSummaryIsEqualToSomething() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestSummary equals to DEFAULT_REQUEST_SUMMARY
        defaultMaterialRequestShouldBeFound("requestSummary.equals=$DEFAULT_REQUEST_SUMMARY")

        // Get all the materialRequestList where requestSummary equals to UPDATED_REQUEST_SUMMARY
        defaultMaterialRequestShouldNotBeFound("requestSummary.equals=$UPDATED_REQUEST_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestSummaryIsInShouldWork() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestSummary in DEFAULT_REQUEST_SUMMARY or UPDATED_REQUEST_SUMMARY
        defaultMaterialRequestShouldBeFound("requestSummary.in=$DEFAULT_REQUEST_SUMMARY,$UPDATED_REQUEST_SUMMARY")

        // Get all the materialRequestList where requestSummary equals to UPDATED_REQUEST_SUMMARY
        defaultMaterialRequestShouldNotBeFound("requestSummary.in=$UPDATED_REQUEST_SUMMARY")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestSummaryIsNullOrNotNull() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestSummary is not null
        defaultMaterialRequestShouldBeFound("requestSummary.specified=true")

        // Get all the materialRequestList where requestSummary is null
        defaultMaterialRequestShouldNotBeFound("requestSummary.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByCreatedDateIsEqualToSomething() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where createdDate equals to DEFAULT_CREATED_DATE
        defaultMaterialRequestShouldBeFound("createdDate.equals=$DEFAULT_CREATED_DATE")

        // Get all the materialRequestList where createdDate equals to UPDATED_CREATED_DATE
        defaultMaterialRequestShouldNotBeFound("createdDate.equals=$UPDATED_CREATED_DATE")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByCreatedDateIsInShouldWork() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultMaterialRequestShouldBeFound("createdDate.in=$DEFAULT_CREATED_DATE,$UPDATED_CREATED_DATE")

        // Get all the materialRequestList where createdDate equals to UPDATED_CREATED_DATE
        defaultMaterialRequestShouldNotBeFound("createdDate.in=$UPDATED_CREATED_DATE")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where createdDate is not null
        defaultMaterialRequestShouldBeFound("createdDate.specified=true")

        // Get all the materialRequestList where createdDate is null
        defaultMaterialRequestShouldNotBeFound("createdDate.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByClosedDateIsEqualToSomething() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where closedDate equals to DEFAULT_CLOSED_DATE
        defaultMaterialRequestShouldBeFound("closedDate.equals=$DEFAULT_CLOSED_DATE")

        // Get all the materialRequestList where closedDate equals to UPDATED_CLOSED_DATE
        defaultMaterialRequestShouldNotBeFound("closedDate.equals=$UPDATED_CLOSED_DATE")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByClosedDateIsInShouldWork() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where closedDate in DEFAULT_CLOSED_DATE or UPDATED_CLOSED_DATE
        defaultMaterialRequestShouldBeFound("closedDate.in=$DEFAULT_CLOSED_DATE,$UPDATED_CLOSED_DATE")

        // Get all the materialRequestList where closedDate equals to UPDATED_CLOSED_DATE
        defaultMaterialRequestShouldNotBeFound("closedDate.in=$UPDATED_CLOSED_DATE")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByClosedDateIsNullOrNotNull() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where closedDate is not null
        defaultMaterialRequestShouldBeFound("closedDate.specified=true")

        // Get all the materialRequestList where closedDate is null
        defaultMaterialRequestShouldNotBeFound("closedDate.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestPriorityIsEqualToSomething() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestPriority equals to DEFAULT_REQUEST_PRIORITY
        defaultMaterialRequestShouldBeFound("requestPriority.equals=$DEFAULT_REQUEST_PRIORITY")

        // Get all the materialRequestList where requestPriority equals to UPDATED_REQUEST_PRIORITY
        defaultMaterialRequestShouldNotBeFound("requestPriority.equals=$UPDATED_REQUEST_PRIORITY")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestPriorityIsInShouldWork() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestPriority in DEFAULT_REQUEST_PRIORITY or UPDATED_REQUEST_PRIORITY
        defaultMaterialRequestShouldBeFound("requestPriority.in=$DEFAULT_REQUEST_PRIORITY,$UPDATED_REQUEST_PRIORITY")

        // Get all the materialRequestList where requestPriority equals to UPDATED_REQUEST_PRIORITY
        defaultMaterialRequestShouldNotBeFound("requestPriority.in=$UPDATED_REQUEST_PRIORITY")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestPriorityIsNullOrNotNull() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestPriority is not null
        defaultMaterialRequestShouldBeFound("requestPriority.specified=true")

        // Get all the materialRequestList where requestPriority is null
        defaultMaterialRequestShouldNotBeFound("requestPriority.specified=false")
    }
    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestPriorityIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestPriority greater than or equals to DEFAULT_REQUEST_PRIORITY
        defaultMaterialRequestShouldBeFound("requestPriority.greaterOrEqualThan=$DEFAULT_REQUEST_PRIORITY")

        // Get all the materialRequestList where requestPriority greater than or equals to UPDATED_REQUEST_PRIORITY
        defaultMaterialRequestShouldNotBeFound("requestPriority.greaterOrEqualThan=$UPDATED_REQUEST_PRIORITY")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestPriorityIsLessThanSomething() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestPriority less than or equals to DEFAULT_REQUEST_PRIORITY
        defaultMaterialRequestShouldNotBeFound("requestPriority.lessThan=$DEFAULT_REQUEST_PRIORITY")

        // Get all the materialRequestList where requestPriority less than or equals to UPDATED_REQUEST_PRIORITY
        defaultMaterialRequestShouldBeFound("requestPriority.lessThan=$UPDATED_REQUEST_PRIORITY")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestStatusIsEqualToSomething() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestStatus equals to DEFAULT_REQUEST_STATUS
        defaultMaterialRequestShouldBeFound("requestStatus.equals=$DEFAULT_REQUEST_STATUS")

        // Get all the materialRequestList where requestStatus equals to UPDATED_REQUEST_STATUS
        defaultMaterialRequestShouldNotBeFound("requestStatus.equals=$UPDATED_REQUEST_STATUS")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestStatusIsInShouldWork() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestStatus in DEFAULT_REQUEST_STATUS or UPDATED_REQUEST_STATUS
        defaultMaterialRequestShouldBeFound("requestStatus.in=$DEFAULT_REQUEST_STATUS,$UPDATED_REQUEST_STATUS")

        // Get all the materialRequestList where requestStatus equals to UPDATED_REQUEST_STATUS
        defaultMaterialRequestShouldNotBeFound("requestStatus.in=$UPDATED_REQUEST_STATUS")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestStatusIsNullOrNotNull() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestStatus is not null
        defaultMaterialRequestShouldBeFound("requestStatus.specified=true")

        // Get all the materialRequestList where requestStatus is null
        defaultMaterialRequestShouldNotBeFound("requestStatus.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestedQuantityIsEqualToSomething() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestedQuantity equals to DEFAULT_REQUESTED_QUANTITY
        defaultMaterialRequestShouldBeFound("requestedQuantity.equals=$DEFAULT_REQUESTED_QUANTITY")

        // Get all the materialRequestList where requestedQuantity equals to UPDATED_REQUESTED_QUANTITY
        defaultMaterialRequestShouldNotBeFound("requestedQuantity.equals=$UPDATED_REQUESTED_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestedQuantityIsInShouldWork() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestedQuantity in DEFAULT_REQUESTED_QUANTITY or UPDATED_REQUESTED_QUANTITY
        defaultMaterialRequestShouldBeFound("requestedQuantity.in=$DEFAULT_REQUESTED_QUANTITY,$UPDATED_REQUESTED_QUANTITY")

        // Get all the materialRequestList where requestedQuantity equals to UPDATED_REQUESTED_QUANTITY
        defaultMaterialRequestShouldNotBeFound("requestedQuantity.in=$UPDATED_REQUESTED_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestedQuantityIsNullOrNotNull() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestedQuantity is not null
        defaultMaterialRequestShouldBeFound("requestedQuantity.specified=true")

        // Get all the materialRequestList where requestedQuantity is null
        defaultMaterialRequestShouldNotBeFound("requestedQuantity.specified=false")
    }
    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestedQuantityIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestedQuantity greater than or equals to DEFAULT_REQUESTED_QUANTITY
        defaultMaterialRequestShouldBeFound("requestedQuantity.greaterOrEqualThan=$DEFAULT_REQUESTED_QUANTITY")

        // Get all the materialRequestList where requestedQuantity greater than or equals to UPDATED_REQUESTED_QUANTITY
        defaultMaterialRequestShouldNotBeFound("requestedQuantity.greaterOrEqualThan=$UPDATED_REQUESTED_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequestedQuantityIsLessThanSomething() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where requestedQuantity less than or equals to DEFAULT_REQUESTED_QUANTITY
        defaultMaterialRequestShouldNotBeFound("requestedQuantity.lessThan=$DEFAULT_REQUESTED_QUANTITY")

        // Get all the materialRequestList where requestedQuantity less than or equals to UPDATED_REQUESTED_QUANTITY
        defaultMaterialRequestShouldBeFound("requestedQuantity.lessThan=$UPDATED_REQUESTED_QUANTITY")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByMeasureUnitIsEqualToSomething() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where measureUnit equals to DEFAULT_MEASURE_UNIT
        defaultMaterialRequestShouldBeFound("measureUnit.equals=$DEFAULT_MEASURE_UNIT")

        // Get all the materialRequestList where measureUnit equals to UPDATED_MEASURE_UNIT
        defaultMaterialRequestShouldNotBeFound("measureUnit.equals=$UPDATED_MEASURE_UNIT")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByMeasureUnitIsInShouldWork() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where measureUnit in DEFAULT_MEASURE_UNIT or UPDATED_MEASURE_UNIT
        defaultMaterialRequestShouldBeFound("measureUnit.in=$DEFAULT_MEASURE_UNIT,$UPDATED_MEASURE_UNIT")

        // Get all the materialRequestList where measureUnit equals to UPDATED_MEASURE_UNIT
        defaultMaterialRequestShouldNotBeFound("measureUnit.in=$UPDATED_MEASURE_UNIT")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByMeasureUnitIsNullOrNotNull() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        // Get all the materialRequestList where measureUnit is not null
        defaultMaterialRequestShouldBeFound("measureUnit.specified=true")

        // Get all the materialRequestList where measureUnit is null
        defaultMaterialRequestShouldNotBeFound("measureUnit.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByRequesterIsEqualToSomething() {
        // Initialize the database
        val requester = FacilityResourceIT.createEntity(em)
        em.persist(requester)
        em.flush()
        materialRequestModel.requester = requester
        materialRequestRepository.saveAndFlush(materialRequestModel)
        val requesterId = requester.id

        // Get all the materialRequestList where requester equals to requesterId
        defaultMaterialRequestShouldBeFound("requesterId.equals=$requesterId")

        // Get all the materialRequestList where requester equals to requesterId + 1
        defaultMaterialRequestShouldNotBeFound("requesterId.equals=${requesterId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllMaterialRequestsByMaterialIsEqualToSomething() {
        // Initialize the database
        val material = MaterialResourceIT.createEntity(em)
        em.persist(material)
        em.flush()
        materialRequestModel.material = material
        materialRequestRepository.saveAndFlush(materialRequestModel)
        val materialId = material.id

        // Get all the materialRequestList where material equals to materialId
        defaultMaterialRequestShouldBeFound("materialId.equals=$materialId")

        // Get all the materialRequestList where material equals to materialId + 1
        defaultMaterialRequestShouldNotBeFound("materialId.equals=${materialId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultMaterialRequestShouldBeFound(filter: String) {
        restMaterialRequestMockMvc.perform(get("/api/material-requests?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(materialRequestModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].requestSummary").value(hasItem(DEFAULT_REQUEST_SUMMARY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].closedDate").value(hasItem(DEFAULT_CLOSED_DATE.toString())))
            .andExpect(jsonPath("$.[*].requestNote").value(hasItem(DEFAULT_REQUEST_NOTE.toString())))
            .andExpect(jsonPath("$.[*].requestPriority").value(hasItem(DEFAULT_REQUEST_PRIORITY)))
            .andExpect(jsonPath("$.[*].requestStatus").value(hasItem(DEFAULT_REQUEST_STATUS.toString())))
            .andExpect(jsonPath("$.[*].requestedQuantity").value(hasItem(DEFAULT_REQUESTED_QUANTITY)))
            .andExpect(jsonPath("$.[*].measureUnit").value(hasItem(DEFAULT_MEASURE_UNIT.toString())))

        // Check, that the count call also returns 1
        restMaterialRequestMockMvc.perform(get("/api/material-requests/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultMaterialRequestShouldNotBeFound(filter: String) {
        restMaterialRequestMockMvc.perform(get("/api/material-requests?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restMaterialRequestMockMvc.perform(get("/api/material-requests/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingMaterialRequest() {
        // Get the materialRequest
        restMaterialRequestMockMvc.perform(get("/api/material-requests/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateMaterialRequest() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        val databaseSizeBeforeUpdate = materialRequestRepository.findAll().size

        // Update the materialRequest
        val id = materialRequestModel.id
        assertNotNull(id)
        val updatedMaterialRequest = materialRequestRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedMaterialRequest are not directly saved in db
        em.detach(updatedMaterialRequest)
        updatedMaterialRequest.requestSummary = UPDATED_REQUEST_SUMMARY
        updatedMaterialRequest.createdDate = UPDATED_CREATED_DATE
        updatedMaterialRequest.closedDate = UPDATED_CLOSED_DATE
        updatedMaterialRequest.requestNote = UPDATED_REQUEST_NOTE
        updatedMaterialRequest.requestPriority = UPDATED_REQUEST_PRIORITY
        updatedMaterialRequest.requestStatus = UPDATED_REQUEST_STATUS
        updatedMaterialRequest.requestedQuantity = UPDATED_REQUESTED_QUANTITY
        updatedMaterialRequest.measureUnit = UPDATED_MEASURE_UNIT
        val materialRequestValueObject = materialRequestMapper.toDto(updatedMaterialRequest)

        restMaterialRequestMockMvc.perform(
            put("/api/material-requests")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialRequestValueObject))
        ).andExpect(status().isOk)

        // Validate the MaterialRequest in the database
        val materialRequestList = materialRequestRepository.findAll()
        assertThat(materialRequestList).hasSize(databaseSizeBeforeUpdate)
        val testMaterialRequest = materialRequestList[materialRequestList.size - 1]
        assertThat(testMaterialRequest.requestSummary).isEqualTo(UPDATED_REQUEST_SUMMARY)
        assertThat(testMaterialRequest.createdDate).isEqualTo(UPDATED_CREATED_DATE)
        assertThat(testMaterialRequest.closedDate).isEqualTo(UPDATED_CLOSED_DATE)
        assertThat(testMaterialRequest.requestNote).isEqualTo(UPDATED_REQUEST_NOTE)
        assertThat(testMaterialRequest.requestPriority).isEqualTo(UPDATED_REQUEST_PRIORITY)
        assertThat(testMaterialRequest.requestStatus).isEqualTo(UPDATED_REQUEST_STATUS)
        assertThat(testMaterialRequest.requestedQuantity).isEqualTo(UPDATED_REQUESTED_QUANTITY)
        assertThat(testMaterialRequest.measureUnit).isEqualTo(UPDATED_MEASURE_UNIT)
    }

    @Test
    @Transactional
    fun updateNonExistingMaterialRequest() {
        val databaseSizeBeforeUpdate = materialRequestRepository.findAll().size

        // Create the MaterialRequest
        val materialRequestValueObject = materialRequestMapper.toDto(materialRequestModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaterialRequestMockMvc.perform(
            put("/api/material-requests")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialRequestValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the MaterialRequest in the database
        val materialRequestList = materialRequestRepository.findAll()
        assertThat(materialRequestList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteMaterialRequest() {
        // Initialize the database
        materialRequestRepository.saveAndFlush(materialRequestModel)

        val databaseSizeBeforeDelete = materialRequestRepository.findAll().size

        val id = materialRequestModel.id
        assertNotNull(id)

        // Delete the materialRequest
        restMaterialRequestMockMvc.perform(
            delete("/api/material-requests/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val materialRequestList = materialRequestRepository.findAll()
        assertThat(materialRequestList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(MaterialRequestModel::class.java)
        val materialRequestModel1 = MaterialRequestModel()
        materialRequestModel1.id = 1L
        val materialRequestModel2 = MaterialRequestModel()
        materialRequestModel2.id = materialRequestModel1.id
        assertThat(materialRequestModel1).isEqualTo(materialRequestModel2)
        materialRequestModel2.id = 2L
        assertThat(materialRequestModel1).isNotEqualTo(materialRequestModel2)
        materialRequestModel1.id = null
        assertThat(materialRequestModel1).isNotEqualTo(materialRequestModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(MaterialRequestValueObject::class.java)
        val materialRequestValueObject1 = MaterialRequestValueObject()
        materialRequestValueObject1.id = 1L
        val materialRequestValueObject2 = MaterialRequestValueObject()
        assertThat(materialRequestValueObject1).isNotEqualTo(materialRequestValueObject2)
        materialRequestValueObject2.id = materialRequestValueObject1.id
        assertThat(materialRequestValueObject1).isEqualTo(materialRequestValueObject2)
        materialRequestValueObject2.id = 2L
        assertThat(materialRequestValueObject1).isNotEqualTo(materialRequestValueObject2)
        materialRequestValueObject1.id = null
        assertThat(materialRequestValueObject1).isNotEqualTo(materialRequestValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(materialRequestMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(materialRequestMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_REQUEST_SUMMARY: String = "AAAAAAAAAA"
        private const val UPDATED_REQUEST_SUMMARY = "BBBBBBBBBB"

        private val DEFAULT_CREATED_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_CREATED_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_CLOSED_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_CLOSED_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_REQUEST_NOTE: String = "AAAAAAAAAA"
        private const val UPDATED_REQUEST_NOTE = "BBBBBBBBBB"

        private const val DEFAULT_REQUEST_PRIORITY: Int = 1
        private const val UPDATED_REQUEST_PRIORITY: Int = 2

        private val DEFAULT_REQUEST_STATUS: MaterialRequestStatus = MaterialRequestStatus.NEW
        private val UPDATED_REQUEST_STATUS: MaterialRequestStatus = MaterialRequestStatus.FINISHED

        private const val DEFAULT_REQUESTED_QUANTITY: Int = 1
        private const val UPDATED_REQUESTED_QUANTITY: Int = 2

        private val DEFAULT_MEASURE_UNIT: MeasureUnit = MeasureUnit.METER
        private val UPDATED_MEASURE_UNIT: MeasureUnit = MeasureUnit.SQUARE_METER
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): MaterialRequestModel {
            val materialRequestModel = MaterialRequestModel()
            materialRequestModel.requestSummary = DEFAULT_REQUEST_SUMMARY
            materialRequestModel.createdDate = DEFAULT_CREATED_DATE
            materialRequestModel.closedDate = DEFAULT_CLOSED_DATE
            materialRequestModel.requestNote = DEFAULT_REQUEST_NOTE
            materialRequestModel.requestPriority = DEFAULT_REQUEST_PRIORITY
            materialRequestModel.requestStatus = DEFAULT_REQUEST_STATUS
            materialRequestModel.requestedQuantity = DEFAULT_REQUESTED_QUANTITY
            materialRequestModel.measureUnit = DEFAULT_MEASURE_UNIT

        return materialRequestModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): MaterialRequestModel {
            val materialRequestModel = MaterialRequestModel()
            materialRequestModel.requestSummary = UPDATED_REQUEST_SUMMARY
            materialRequestModel.createdDate = UPDATED_CREATED_DATE
            materialRequestModel.closedDate = UPDATED_CLOSED_DATE
            materialRequestModel.requestNote = UPDATED_REQUEST_NOTE
            materialRequestModel.requestPriority = UPDATED_REQUEST_PRIORITY
            materialRequestModel.requestStatus = UPDATED_REQUEST_STATUS
            materialRequestModel.requestedQuantity = UPDATED_REQUESTED_QUANTITY
            materialRequestModel.measureUnit = UPDATED_MEASURE_UNIT

        return materialRequestModel
        }
    }
}
