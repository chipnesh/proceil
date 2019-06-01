package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.FeedbackModel
import me.chipnesh.proceil.repository.FeedbackRepository
import me.chipnesh.proceil.service.FeedbackService
import me.chipnesh.proceil.service.dto.FeedbackValueObject
import me.chipnesh.proceil.service.mapper.FeedbackMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.FeedbackQueryService

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
 * Test class for the FeedbackResource REST controller.
 *
 * @see FeedbackResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class FeedbackResourceIT {

    @Autowired
    private lateinit var feedbackRepository: FeedbackRepository

    @Autowired
    private lateinit var feedbackMapper: FeedbackMapper

    @Autowired
    private lateinit var feedbackService: FeedbackService

    @Autowired
    private lateinit var feedbackQueryService: FeedbackQueryService

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

    private lateinit var restFeedbackMockMvc: MockMvc

    private lateinit var feedbackModel: FeedbackModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val feedbackResource = FeedbackResource(feedbackService, feedbackQueryService)
        this.restFeedbackMockMvc = MockMvcBuilders.standaloneSetup(feedbackResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        feedbackModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createFeedback() {
        val databaseSizeBeforeCreate = feedbackRepository.findAll().size

        // Create the Feedback
        val feedbackValueObject = feedbackMapper.toDto(feedbackModel)
        restFeedbackMockMvc.perform(
            post("/api/feedbacks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(feedbackValueObject))
        ).andExpect(status().isCreated)

        // Validate the Feedback in the database
        val feedbackList = feedbackRepository.findAll()
        assertThat(feedbackList).hasSize(databaseSizeBeforeCreate + 1)
        val testFeedback = feedbackList[feedbackList.size - 1]
        assertThat(testFeedback.caption).isEqualTo(DEFAULT_CAPTION)
        assertThat(testFeedback.email).isEqualTo(DEFAULT_EMAIL)
        assertThat(testFeedback.text).isEqualTo(DEFAULT_TEXT)
        assertThat(testFeedback.feedbackResponse).isEqualTo(DEFAULT_FEEDBACK_RESPONSE)
    }

    @Test
    @Transactional
    fun createFeedbackWithExistingId() {
        val databaseSizeBeforeCreate = feedbackRepository.findAll().size

        // Create the Feedback with an existing ID
        feedbackModel.id = 1L
        val feedbackValueObject = feedbackMapper.toDto(feedbackModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeedbackMockMvc.perform(
            post("/api/feedbacks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(feedbackValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Feedback in the database
        val feedbackList = feedbackRepository.findAll()
        assertThat(feedbackList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkCaptionIsRequired() {
        val databaseSizeBeforeTest = feedbackRepository.findAll().size
        // set the field null
        feedbackModel.caption = null

        // Create the Feedback, which fails.
        val feedbackValueObject = feedbackMapper.toDto(feedbackModel)

        restFeedbackMockMvc.perform(
            post("/api/feedbacks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(feedbackValueObject))
        ).andExpect(status().isBadRequest)

        val feedbackList = feedbackRepository.findAll()
        assertThat(feedbackList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllFeedbacks() {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedbackModel)

        // Get all the feedbackList
        restFeedbackMockMvc.perform(get("/api/feedbacks?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(feedbackModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())))
            .andExpect(jsonPath("$.[*].feedbackResponse").value(hasItem(DEFAULT_FEEDBACK_RESPONSE.toString())))
    }

    @Test
    @Transactional
    fun getFeedback() {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedbackModel)

        val id = feedbackModel.id
        assertNotNull(id)

        // Get the feedback
        restFeedbackMockMvc.perform(get("/api/feedbacks/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.caption").value(DEFAULT_CAPTION))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()))
            .andExpect(jsonPath("$.feedbackResponse").value(DEFAULT_FEEDBACK_RESPONSE.toString()))
    }

    @Test
    @Transactional
    fun getAllFeedbacksByCaptionIsEqualToSomething() {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedbackModel)

        // Get all the feedbackList where caption equals to DEFAULT_CAPTION
        defaultFeedbackShouldBeFound("caption.equals=$DEFAULT_CAPTION")

        // Get all the feedbackList where caption equals to UPDATED_CAPTION
        defaultFeedbackShouldNotBeFound("caption.equals=$UPDATED_CAPTION")
    }

    @Test
    @Transactional
    fun getAllFeedbacksByCaptionIsInShouldWork() {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedbackModel)

        // Get all the feedbackList where caption in DEFAULT_CAPTION or UPDATED_CAPTION
        defaultFeedbackShouldBeFound("caption.in=$DEFAULT_CAPTION,$UPDATED_CAPTION")

        // Get all the feedbackList where caption equals to UPDATED_CAPTION
        defaultFeedbackShouldNotBeFound("caption.in=$UPDATED_CAPTION")
    }

    @Test
    @Transactional
    fun getAllFeedbacksByCaptionIsNullOrNotNull() {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedbackModel)

        // Get all the feedbackList where caption is not null
        defaultFeedbackShouldBeFound("caption.specified=true")

        // Get all the feedbackList where caption is null
        defaultFeedbackShouldNotBeFound("caption.specified=false")
    }

    @Test
    @Transactional
    fun getAllFeedbacksByEmailIsEqualToSomething() {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedbackModel)

        // Get all the feedbackList where email equals to DEFAULT_EMAIL
        defaultFeedbackShouldBeFound("email.equals=$DEFAULT_EMAIL")

        // Get all the feedbackList where email equals to UPDATED_EMAIL
        defaultFeedbackShouldNotBeFound("email.equals=$UPDATED_EMAIL")
    }

    @Test
    @Transactional
    fun getAllFeedbacksByEmailIsInShouldWork() {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedbackModel)

        // Get all the feedbackList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultFeedbackShouldBeFound("email.in=$DEFAULT_EMAIL,$UPDATED_EMAIL")

        // Get all the feedbackList where email equals to UPDATED_EMAIL
        defaultFeedbackShouldNotBeFound("email.in=$UPDATED_EMAIL")
    }

    @Test
    @Transactional
    fun getAllFeedbacksByEmailIsNullOrNotNull() {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedbackModel)

        // Get all the feedbackList where email is not null
        defaultFeedbackShouldBeFound("email.specified=true")

        // Get all the feedbackList where email is null
        defaultFeedbackShouldNotBeFound("email.specified=false")
    }

    @Test
    @Transactional
    fun getAllFeedbacksByAuthorIsEqualToSomething() {
        // Initialize the database
        val author = CustomerResourceIT.createEntity(em)
        em.persist(author)
        em.flush()
        feedbackModel.author = author
        feedbackRepository.saveAndFlush(feedbackModel)
        val authorId = author.id

        // Get all the feedbackList where author equals to authorId
        defaultFeedbackShouldBeFound("authorId.equals=$authorId")

        // Get all the feedbackList where author equals to authorId + 1
        defaultFeedbackShouldNotBeFound("authorId.equals=${authorId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultFeedbackShouldBeFound(filter: String) {
        restFeedbackMockMvc.perform(get("/api/feedbacks?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(feedbackModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())))
            .andExpect(jsonPath("$.[*].feedbackResponse").value(hasItem(DEFAULT_FEEDBACK_RESPONSE.toString())))

        // Check, that the count call also returns 1
        restFeedbackMockMvc.perform(get("/api/feedbacks/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultFeedbackShouldNotBeFound(filter: String) {
        restFeedbackMockMvc.perform(get("/api/feedbacks?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restFeedbackMockMvc.perform(get("/api/feedbacks/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingFeedback() {
        // Get the feedback
        restFeedbackMockMvc.perform(get("/api/feedbacks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateFeedback() {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedbackModel)

        val databaseSizeBeforeUpdate = feedbackRepository.findAll().size

        // Update the feedback
        val id = feedbackModel.id
        assertNotNull(id)
        val updatedFeedback = feedbackRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedFeedback are not directly saved in db
        em.detach(updatedFeedback)
        updatedFeedback.caption = UPDATED_CAPTION
        updatedFeedback.email = UPDATED_EMAIL
        updatedFeedback.text = UPDATED_TEXT
        updatedFeedback.feedbackResponse = UPDATED_FEEDBACK_RESPONSE
        val feedbackValueObject = feedbackMapper.toDto(updatedFeedback)

        restFeedbackMockMvc.perform(
            put("/api/feedbacks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(feedbackValueObject))
        ).andExpect(status().isOk)

        // Validate the Feedback in the database
        val feedbackList = feedbackRepository.findAll()
        assertThat(feedbackList).hasSize(databaseSizeBeforeUpdate)
        val testFeedback = feedbackList[feedbackList.size - 1]
        assertThat(testFeedback.caption).isEqualTo(UPDATED_CAPTION)
        assertThat(testFeedback.email).isEqualTo(UPDATED_EMAIL)
        assertThat(testFeedback.text).isEqualTo(UPDATED_TEXT)
        assertThat(testFeedback.feedbackResponse).isEqualTo(UPDATED_FEEDBACK_RESPONSE)
    }

    @Test
    @Transactional
    fun updateNonExistingFeedback() {
        val databaseSizeBeforeUpdate = feedbackRepository.findAll().size

        // Create the Feedback
        val feedbackValueObject = feedbackMapper.toDto(feedbackModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeedbackMockMvc.perform(
            put("/api/feedbacks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(feedbackValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Feedback in the database
        val feedbackList = feedbackRepository.findAll()
        assertThat(feedbackList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteFeedback() {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedbackModel)

        val databaseSizeBeforeDelete = feedbackRepository.findAll().size

        val id = feedbackModel.id
        assertNotNull(id)

        // Delete the feedback
        restFeedbackMockMvc.perform(
            delete("/api/feedbacks/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val feedbackList = feedbackRepository.findAll()
        assertThat(feedbackList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(FeedbackModel::class.java)
        val feedbackModel1 = FeedbackModel()
        feedbackModel1.id = 1L
        val feedbackModel2 = FeedbackModel()
        feedbackModel2.id = feedbackModel1.id
        assertThat(feedbackModel1).isEqualTo(feedbackModel2)
        feedbackModel2.id = 2L
        assertThat(feedbackModel1).isNotEqualTo(feedbackModel2)
        feedbackModel1.id = null
        assertThat(feedbackModel1).isNotEqualTo(feedbackModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(FeedbackValueObject::class.java)
        val feedbackValueObject1 = FeedbackValueObject()
        feedbackValueObject1.id = 1L
        val feedbackValueObject2 = FeedbackValueObject()
        assertThat(feedbackValueObject1).isNotEqualTo(feedbackValueObject2)
        feedbackValueObject2.id = feedbackValueObject1.id
        assertThat(feedbackValueObject1).isEqualTo(feedbackValueObject2)
        feedbackValueObject2.id = 2L
        assertThat(feedbackValueObject1).isNotEqualTo(feedbackValueObject2)
        feedbackValueObject1.id = null
        assertThat(feedbackValueObject1).isNotEqualTo(feedbackValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(feedbackMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(feedbackMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_CAPTION: String = "AAAAAAAAAA"
        private const val UPDATED_CAPTION = "BBBBBBBBBB"

        private const val DEFAULT_EMAIL: String = "AAAAAAAAAA"
        private const val UPDATED_EMAIL = "BBBBBBBBBB"

        private const val DEFAULT_TEXT: String = "AAAAAAAAAA"
        private const val UPDATED_TEXT = "BBBBBBBBBB"

        private const val DEFAULT_FEEDBACK_RESPONSE: String = "AAAAAAAAAA"
        private const val UPDATED_FEEDBACK_RESPONSE = "BBBBBBBBBB"
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): FeedbackModel {
            val feedbackModel = FeedbackModel()
            feedbackModel.caption = DEFAULT_CAPTION
            feedbackModel.email = DEFAULT_EMAIL
            feedbackModel.text = DEFAULT_TEXT
            feedbackModel.feedbackResponse = DEFAULT_FEEDBACK_RESPONSE

        return feedbackModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): FeedbackModel {
            val feedbackModel = FeedbackModel()
            feedbackModel.caption = UPDATED_CAPTION
            feedbackModel.email = UPDATED_EMAIL
            feedbackModel.text = UPDATED_TEXT
            feedbackModel.feedbackResponse = UPDATED_FEEDBACK_RESPONSE

        return feedbackModel
        }
    }
}
