package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.AttachedImageModel
import me.chipnesh.proceil.repository.AttachedImageRepository
import me.chipnesh.proceil.service.AttachedImageService
import me.chipnesh.proceil.service.dto.AttachedImageValueObject
import me.chipnesh.proceil.service.mapper.AttachedImageMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.AttachedImageQueryService

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
import org.springframework.util.Base64Utils
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
 * Test class for the AttachedImageResource REST controller.
 *
 * @see AttachedImageResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class AttachedImageResourceIT {

    @Autowired
    private lateinit var attachedImageRepository: AttachedImageRepository

    @Autowired
    private lateinit var attachedImageMapper: AttachedImageMapper

    @Autowired
    private lateinit var attachedImageService: AttachedImageService

    @Autowired
    private lateinit var attachedImageQueryService: AttachedImageQueryService

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

    private lateinit var restAttachedImageMockMvc: MockMvc

    private lateinit var attachedImageModel: AttachedImageModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val attachedImageResource = AttachedImageResource(attachedImageService, attachedImageQueryService)
        this.restAttachedImageMockMvc = MockMvcBuilders.standaloneSetup(attachedImageResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        attachedImageModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createAttachedImage() {
        val databaseSizeBeforeCreate = attachedImageRepository.findAll().size

        // Create the AttachedImage
        val attachedImageValueObject = attachedImageMapper.toDto(attachedImageModel)
        restAttachedImageMockMvc.perform(
            post("/api/attached-images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(attachedImageValueObject))
        ).andExpect(status().isCreated)

        // Validate the AttachedImage in the database
        val attachedImageList = attachedImageRepository.findAll()
        assertThat(attachedImageList).hasSize(databaseSizeBeforeCreate + 1)
        val testAttachedImage = attachedImageList[attachedImageList.size - 1]
        assertThat(testAttachedImage.imageName).isEqualTo(DEFAULT_IMAGE_NAME)
        assertThat(testAttachedImage.imageFile).isEqualTo(DEFAULT_IMAGE_FILE)
        assertThat(testAttachedImage.imageFileContentType).isEqualTo(DEFAULT_IMAGE_FILE_CONTENT_TYPE)
    }

    @Test
    @Transactional
    fun createAttachedImageWithExistingId() {
        val databaseSizeBeforeCreate = attachedImageRepository.findAll().size

        // Create the AttachedImage with an existing ID
        attachedImageModel.id = 1L
        val attachedImageValueObject = attachedImageMapper.toDto(attachedImageModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restAttachedImageMockMvc.perform(
            post("/api/attached-images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(attachedImageValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the AttachedImage in the database
        val attachedImageList = attachedImageRepository.findAll()
        assertThat(attachedImageList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllAttachedImages() {
        // Initialize the database
        attachedImageRepository.saveAndFlush(attachedImageModel)

        // Get all the attachedImageList
        restAttachedImageMockMvc.perform(get("/api/attached-images?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attachedImageModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].imageName").value(hasItem(DEFAULT_IMAGE_NAME)))
            .andExpect(jsonPath("$.[*].imageFileContentType").value(hasItem(DEFAULT_IMAGE_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE_FILE))))
    }

    @Test
    @Transactional
    fun getAttachedImage() {
        // Initialize the database
        attachedImageRepository.saveAndFlush(attachedImageModel)

        val id = attachedImageModel.id
        assertNotNull(id)

        // Get the attachedImage
        restAttachedImageMockMvc.perform(get("/api/attached-images/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.imageName").value(DEFAULT_IMAGE_NAME))
            .andExpect(jsonPath("$.imageFileContentType").value(DEFAULT_IMAGE_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.imageFile").value(Base64Utils.encodeToString(DEFAULT_IMAGE_FILE)))
    }

    @Test
    @Transactional
    fun getAllAttachedImagesByImageNameIsEqualToSomething() {
        // Initialize the database
        attachedImageRepository.saveAndFlush(attachedImageModel)

        // Get all the attachedImageList where imageName equals to DEFAULT_IMAGE_NAME
        defaultAttachedImageShouldBeFound("imageName.equals=$DEFAULT_IMAGE_NAME")

        // Get all the attachedImageList where imageName equals to UPDATED_IMAGE_NAME
        defaultAttachedImageShouldNotBeFound("imageName.equals=$UPDATED_IMAGE_NAME")
    }

    @Test
    @Transactional
    fun getAllAttachedImagesByImageNameIsInShouldWork() {
        // Initialize the database
        attachedImageRepository.saveAndFlush(attachedImageModel)

        // Get all the attachedImageList where imageName in DEFAULT_IMAGE_NAME or UPDATED_IMAGE_NAME
        defaultAttachedImageShouldBeFound("imageName.in=$DEFAULT_IMAGE_NAME,$UPDATED_IMAGE_NAME")

        // Get all the attachedImageList where imageName equals to UPDATED_IMAGE_NAME
        defaultAttachedImageShouldNotBeFound("imageName.in=$UPDATED_IMAGE_NAME")
    }

    @Test
    @Transactional
    fun getAllAttachedImagesByImageNameIsNullOrNotNull() {
        // Initialize the database
        attachedImageRepository.saveAndFlush(attachedImageModel)

        // Get all the attachedImageList where imageName is not null
        defaultAttachedImageShouldBeFound("imageName.specified=true")

        // Get all the attachedImageList where imageName is null
        defaultAttachedImageShouldNotBeFound("imageName.specified=false")
    }

    @Test
    @Transactional
    fun getAllAttachedImagesByMaterialIsEqualToSomething() {
        // Initialize the database
        val material = MaterialResourceIT.createEntity(em)
        em.persist(material)
        em.flush()
        attachedImageModel.material = material
        attachedImageRepository.saveAndFlush(attachedImageModel)
        val materialId = material.id

        // Get all the attachedImageList where material equals to materialId
        defaultAttachedImageShouldBeFound("materialId.equals=$materialId")

        // Get all the attachedImageList where material equals to materialId + 1
        defaultAttachedImageShouldNotBeFound("materialId.equals=${materialId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllAttachedImagesByServiceIsEqualToSomething() {
        // Initialize the database
        val service = ServiceResourceIT.createEntity(em)
        em.persist(service)
        em.flush()
        attachedImageModel.service = service
        attachedImageRepository.saveAndFlush(attachedImageModel)
        val serviceId = service.id

        // Get all the attachedImageList where service equals to serviceId
        defaultAttachedImageShouldBeFound("serviceId.equals=$serviceId")

        // Get all the attachedImageList where service equals to serviceId + 1
        defaultAttachedImageShouldNotBeFound("serviceId.equals=${serviceId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultAttachedImageShouldBeFound(filter: String) {
        restAttachedImageMockMvc.perform(get("/api/attached-images?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attachedImageModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].imageName").value(hasItem(DEFAULT_IMAGE_NAME)))
            .andExpect(jsonPath("$.[*].imageFileContentType").value(hasItem(DEFAULT_IMAGE_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE_FILE))))

        // Check, that the count call also returns 1
        restAttachedImageMockMvc.perform(get("/api/attached-images/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultAttachedImageShouldNotBeFound(filter: String) {
        restAttachedImageMockMvc.perform(get("/api/attached-images?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restAttachedImageMockMvc.perform(get("/api/attached-images/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingAttachedImage() {
        // Get the attachedImage
        restAttachedImageMockMvc.perform(get("/api/attached-images/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateAttachedImage() {
        // Initialize the database
        attachedImageRepository.saveAndFlush(attachedImageModel)

        val databaseSizeBeforeUpdate = attachedImageRepository.findAll().size

        // Update the attachedImage
        val id = attachedImageModel.id
        assertNotNull(id)
        val updatedAttachedImage = attachedImageRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedAttachedImage are not directly saved in db
        em.detach(updatedAttachedImage)
        updatedAttachedImage.imageName = UPDATED_IMAGE_NAME
        updatedAttachedImage.imageFile = UPDATED_IMAGE_FILE
        updatedAttachedImage.imageFileContentType = UPDATED_IMAGE_FILE_CONTENT_TYPE
        val attachedImageValueObject = attachedImageMapper.toDto(updatedAttachedImage)

        restAttachedImageMockMvc.perform(
            put("/api/attached-images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(attachedImageValueObject))
        ).andExpect(status().isOk)

        // Validate the AttachedImage in the database
        val attachedImageList = attachedImageRepository.findAll()
        assertThat(attachedImageList).hasSize(databaseSizeBeforeUpdate)
        val testAttachedImage = attachedImageList[attachedImageList.size - 1]
        assertThat(testAttachedImage.imageName).isEqualTo(UPDATED_IMAGE_NAME)
        assertThat(testAttachedImage.imageFile).isEqualTo(UPDATED_IMAGE_FILE)
        assertThat(testAttachedImage.imageFileContentType).isEqualTo(UPDATED_IMAGE_FILE_CONTENT_TYPE)
    }

    @Test
    @Transactional
    fun updateNonExistingAttachedImage() {
        val databaseSizeBeforeUpdate = attachedImageRepository.findAll().size

        // Create the AttachedImage
        val attachedImageValueObject = attachedImageMapper.toDto(attachedImageModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttachedImageMockMvc.perform(
            put("/api/attached-images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(attachedImageValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the AttachedImage in the database
        val attachedImageList = attachedImageRepository.findAll()
        assertThat(attachedImageList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteAttachedImage() {
        // Initialize the database
        attachedImageRepository.saveAndFlush(attachedImageModel)

        val databaseSizeBeforeDelete = attachedImageRepository.findAll().size

        val id = attachedImageModel.id
        assertNotNull(id)

        // Delete the attachedImage
        restAttachedImageMockMvc.perform(
            delete("/api/attached-images/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val attachedImageList = attachedImageRepository.findAll()
        assertThat(attachedImageList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(AttachedImageModel::class.java)
        val attachedImageModel1 = AttachedImageModel()
        attachedImageModel1.id = 1L
        val attachedImageModel2 = AttachedImageModel()
        attachedImageModel2.id = attachedImageModel1.id
        assertThat(attachedImageModel1).isEqualTo(attachedImageModel2)
        attachedImageModel2.id = 2L
        assertThat(attachedImageModel1).isNotEqualTo(attachedImageModel2)
        attachedImageModel1.id = null
        assertThat(attachedImageModel1).isNotEqualTo(attachedImageModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(AttachedImageValueObject::class.java)
        val attachedImageValueObject1 = AttachedImageValueObject()
        attachedImageValueObject1.id = 1L
        val attachedImageValueObject2 = AttachedImageValueObject()
        assertThat(attachedImageValueObject1).isNotEqualTo(attachedImageValueObject2)
        attachedImageValueObject2.id = attachedImageValueObject1.id
        assertThat(attachedImageValueObject1).isEqualTo(attachedImageValueObject2)
        attachedImageValueObject2.id = 2L
        assertThat(attachedImageValueObject1).isNotEqualTo(attachedImageValueObject2)
        attachedImageValueObject1.id = null
        assertThat(attachedImageValueObject1).isNotEqualTo(attachedImageValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(attachedImageMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(attachedImageMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_IMAGE_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_IMAGE_NAME = "BBBBBBBBBB"

        private val DEFAULT_IMAGE_FILE: ByteArray = TestUtil.createByteArray(1, "0")
        private val UPDATED_IMAGE_FILE: ByteArray = TestUtil.createByteArray(1, "1")
        private const val DEFAULT_IMAGE_FILE_CONTENT_TYPE: String = "image/jpg"
        private const val UPDATED_IMAGE_FILE_CONTENT_TYPE: String = "image/png"
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): AttachedImageModel {
            val attachedImageModel = AttachedImageModel()
            attachedImageModel.imageName = DEFAULT_IMAGE_NAME
            attachedImageModel.imageFile = DEFAULT_IMAGE_FILE
            attachedImageModel.imageFileContentType = DEFAULT_IMAGE_FILE_CONTENT_TYPE

        return attachedImageModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): AttachedImageModel {
            val attachedImageModel = AttachedImageModel()
            attachedImageModel.imageName = UPDATED_IMAGE_NAME
            attachedImageModel.imageFile = UPDATED_IMAGE_FILE
            attachedImageModel.imageFileContentType = UPDATED_IMAGE_FILE_CONTENT_TYPE

        return attachedImageModel
        }
    }
}
