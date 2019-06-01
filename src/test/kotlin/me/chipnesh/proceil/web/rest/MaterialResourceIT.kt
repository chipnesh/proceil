package me.chipnesh.proceil.web.rest

import me.chipnesh.proceil.ProceilApp
import me.chipnesh.proceil.domain.MaterialModel
import me.chipnesh.proceil.repository.MaterialRepository
import me.chipnesh.proceil.service.MaterialService
import me.chipnesh.proceil.service.dto.MaterialValueObject
import me.chipnesh.proceil.service.mapper.MaterialMapper
import me.chipnesh.proceil.web.rest.errors.ExceptionTranslator
import me.chipnesh.proceil.service.MaterialQueryService

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
 * Test class for the MaterialResource REST controller.
 *
 * @see MaterialResource
 */
@SpringBootTest(classes = [ProceilApp::class])
class MaterialResourceIT {

    @Autowired
    private lateinit var materialRepository: MaterialRepository

    @Autowired
    private lateinit var materialMapper: MaterialMapper

    @Autowired
    private lateinit var materialService: MaterialService

    @Autowired
    private lateinit var materialQueryService: MaterialQueryService

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

    private lateinit var restMaterialMockMvc: MockMvc

    private lateinit var materialModel: MaterialModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val materialResource = MaterialResource(materialService, materialQueryService)
        this.restMaterialMockMvc = MockMvcBuilders.standaloneSetup(materialResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        materialModel = createEntity(em)
    }

    @Test
    @Transactional
    fun createMaterial() {
        val databaseSizeBeforeCreate = materialRepository.findAll().size

        // Create the Material
        val materialValueObject = materialMapper.toDto(materialModel)
        restMaterialMockMvc.perform(
            post("/api/materials")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialValueObject))
        ).andExpect(status().isCreated)

        // Validate the Material in the database
        val materialList = materialRepository.findAll()
        assertThat(materialList).hasSize(databaseSizeBeforeCreate + 1)
        val testMaterial = materialList[materialList.size - 1]
        assertThat(testMaterial.materialName).isEqualTo(DEFAULT_MATERIAL_NAME)
        assertThat(testMaterial.materialDescription).isEqualTo(DEFAULT_MATERIAL_DESCRIPTION)
        assertThat(testMaterial.materialPrice).isEqualTo(DEFAULT_MATERIAL_PRICE)
    }

    @Test
    @Transactional
    fun createMaterialWithExistingId() {
        val databaseSizeBeforeCreate = materialRepository.findAll().size

        // Create the Material with an existing ID
        materialModel.id = 1L
        val materialValueObject = materialMapper.toDto(materialModel)

        // An entity with an existing ID cannot be created, so this API call must fail
        restMaterialMockMvc.perform(
            post("/api/materials")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Material in the database
        val materialList = materialRepository.findAll()
        assertThat(materialList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun getAllMaterials() {
        // Initialize the database
        materialRepository.saveAndFlush(materialModel)

        // Get all the materialList
        restMaterialMockMvc.perform(get("/api/materials?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(materialModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].materialName").value(hasItem(DEFAULT_MATERIAL_NAME)))
            .andExpect(jsonPath("$.[*].materialDescription").value(hasItem(DEFAULT_MATERIAL_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].materialPrice").value(hasItem(DEFAULT_MATERIAL_PRICE.toInt())))
    }

    @Test
    @Transactional
    fun getMaterial() {
        // Initialize the database
        materialRepository.saveAndFlush(materialModel)

        val id = materialModel.id
        assertNotNull(id)

        // Get the material
        restMaterialMockMvc.perform(get("/api/materials/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.materialName").value(DEFAULT_MATERIAL_NAME))
            .andExpect(jsonPath("$.materialDescription").value(DEFAULT_MATERIAL_DESCRIPTION))
            .andExpect(jsonPath("$.materialPrice").value(DEFAULT_MATERIAL_PRICE.toInt()))
    }

    @Test
    @Transactional
    fun getAllMaterialsByMaterialNameIsEqualToSomething() {
        // Initialize the database
        materialRepository.saveAndFlush(materialModel)

        // Get all the materialList where materialName equals to DEFAULT_MATERIAL_NAME
        defaultMaterialShouldBeFound("materialName.equals=$DEFAULT_MATERIAL_NAME")

        // Get all the materialList where materialName equals to UPDATED_MATERIAL_NAME
        defaultMaterialShouldNotBeFound("materialName.equals=$UPDATED_MATERIAL_NAME")
    }

    @Test
    @Transactional
    fun getAllMaterialsByMaterialNameIsInShouldWork() {
        // Initialize the database
        materialRepository.saveAndFlush(materialModel)

        // Get all the materialList where materialName in DEFAULT_MATERIAL_NAME or UPDATED_MATERIAL_NAME
        defaultMaterialShouldBeFound("materialName.in=$DEFAULT_MATERIAL_NAME,$UPDATED_MATERIAL_NAME")

        // Get all the materialList where materialName equals to UPDATED_MATERIAL_NAME
        defaultMaterialShouldNotBeFound("materialName.in=$UPDATED_MATERIAL_NAME")
    }

    @Test
    @Transactional
    fun getAllMaterialsByMaterialNameIsNullOrNotNull() {
        // Initialize the database
        materialRepository.saveAndFlush(materialModel)

        // Get all the materialList where materialName is not null
        defaultMaterialShouldBeFound("materialName.specified=true")

        // Get all the materialList where materialName is null
        defaultMaterialShouldNotBeFound("materialName.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialsByMaterialDescriptionIsEqualToSomething() {
        // Initialize the database
        materialRepository.saveAndFlush(materialModel)

        // Get all the materialList where materialDescription equals to DEFAULT_MATERIAL_DESCRIPTION
        defaultMaterialShouldBeFound("materialDescription.equals=$DEFAULT_MATERIAL_DESCRIPTION")

        // Get all the materialList where materialDescription equals to UPDATED_MATERIAL_DESCRIPTION
        defaultMaterialShouldNotBeFound("materialDescription.equals=$UPDATED_MATERIAL_DESCRIPTION")
    }

    @Test
    @Transactional
    fun getAllMaterialsByMaterialDescriptionIsInShouldWork() {
        // Initialize the database
        materialRepository.saveAndFlush(materialModel)

        // Get all the materialList where materialDescription in DEFAULT_MATERIAL_DESCRIPTION or UPDATED_MATERIAL_DESCRIPTION
        defaultMaterialShouldBeFound("materialDescription.in=$DEFAULT_MATERIAL_DESCRIPTION,$UPDATED_MATERIAL_DESCRIPTION")

        // Get all the materialList where materialDescription equals to UPDATED_MATERIAL_DESCRIPTION
        defaultMaterialShouldNotBeFound("materialDescription.in=$UPDATED_MATERIAL_DESCRIPTION")
    }

    @Test
    @Transactional
    fun getAllMaterialsByMaterialDescriptionIsNullOrNotNull() {
        // Initialize the database
        materialRepository.saveAndFlush(materialModel)

        // Get all the materialList where materialDescription is not null
        defaultMaterialShouldBeFound("materialDescription.specified=true")

        // Get all the materialList where materialDescription is null
        defaultMaterialShouldNotBeFound("materialDescription.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialsByMaterialPriceIsEqualToSomething() {
        // Initialize the database
        materialRepository.saveAndFlush(materialModel)

        // Get all the materialList where materialPrice equals to DEFAULT_MATERIAL_PRICE
        defaultMaterialShouldBeFound("materialPrice.equals=$DEFAULT_MATERIAL_PRICE")

        // Get all the materialList where materialPrice equals to UPDATED_MATERIAL_PRICE
        defaultMaterialShouldNotBeFound("materialPrice.equals=$UPDATED_MATERIAL_PRICE")
    }

    @Test
    @Transactional
    fun getAllMaterialsByMaterialPriceIsInShouldWork() {
        // Initialize the database
        materialRepository.saveAndFlush(materialModel)

        // Get all the materialList where materialPrice in DEFAULT_MATERIAL_PRICE or UPDATED_MATERIAL_PRICE
        defaultMaterialShouldBeFound("materialPrice.in=$DEFAULT_MATERIAL_PRICE,$UPDATED_MATERIAL_PRICE")

        // Get all the materialList where materialPrice equals to UPDATED_MATERIAL_PRICE
        defaultMaterialShouldNotBeFound("materialPrice.in=$UPDATED_MATERIAL_PRICE")
    }

    @Test
    @Transactional
    fun getAllMaterialsByMaterialPriceIsNullOrNotNull() {
        // Initialize the database
        materialRepository.saveAndFlush(materialModel)

        // Get all the materialList where materialPrice is not null
        defaultMaterialShouldBeFound("materialPrice.specified=true")

        // Get all the materialList where materialPrice is null
        defaultMaterialShouldNotBeFound("materialPrice.specified=false")
    }

    @Test
    @Transactional
    fun getAllMaterialsByImageIsEqualToSomething() {
        // Initialize the database
        val image = AttachedImageResourceIT.createEntity(em)
        em.persist(image)
        em.flush()
        materialModel.addImage(image)
        materialRepository.saveAndFlush(materialModel)
        val imageId = image.id

        // Get all the materialList where image equals to imageId
        defaultMaterialShouldBeFound("imageId.equals=$imageId")

        // Get all the materialList where image equals to imageId + 1
        defaultMaterialShouldNotBeFound("imageId.equals=${imageId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultMaterialShouldBeFound(filter: String) {
        restMaterialMockMvc.perform(get("/api/materials?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(materialModel.id?.toInt())))
            .andExpect(jsonPath("$.[*].materialName").value(hasItem(DEFAULT_MATERIAL_NAME)))
            .andExpect(jsonPath("$.[*].materialDescription").value(hasItem(DEFAULT_MATERIAL_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].materialPrice").value(hasItem(DEFAULT_MATERIAL_PRICE.toInt())))

        // Check, that the count call also returns 1
        restMaterialMockMvc.perform(get("/api/materials/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultMaterialShouldNotBeFound(filter: String) {
        restMaterialMockMvc.perform(get("/api/materials?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restMaterialMockMvc.perform(get("/api/materials/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingMaterial() {
        // Get the material
        restMaterialMockMvc.perform(get("/api/materials/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateMaterial() {
        // Initialize the database
        materialRepository.saveAndFlush(materialModel)

        val databaseSizeBeforeUpdate = materialRepository.findAll().size

        // Update the material
        val id = materialModel.id
        assertNotNull(id)
        val updatedMaterial = materialRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedMaterial are not directly saved in db
        em.detach(updatedMaterial)
        updatedMaterial.materialName = UPDATED_MATERIAL_NAME
        updatedMaterial.materialDescription = UPDATED_MATERIAL_DESCRIPTION
        updatedMaterial.materialPrice = UPDATED_MATERIAL_PRICE
        val materialValueObject = materialMapper.toDto(updatedMaterial)

        restMaterialMockMvc.perform(
            put("/api/materials")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialValueObject))
        ).andExpect(status().isOk)

        // Validate the Material in the database
        val materialList = materialRepository.findAll()
        assertThat(materialList).hasSize(databaseSizeBeforeUpdate)
        val testMaterial = materialList[materialList.size - 1]
        assertThat(testMaterial.materialName).isEqualTo(UPDATED_MATERIAL_NAME)
        assertThat(testMaterial.materialDescription).isEqualTo(UPDATED_MATERIAL_DESCRIPTION)
        assertThat(testMaterial.materialPrice).isEqualTo(UPDATED_MATERIAL_PRICE)
    }

    @Test
    @Transactional
    fun updateNonExistingMaterial() {
        val databaseSizeBeforeUpdate = materialRepository.findAll().size

        // Create the Material
        val materialValueObject = materialMapper.toDto(materialModel)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaterialMockMvc.perform(
            put("/api/materials")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(materialValueObject))
        ).andExpect(status().isBadRequest)

        // Validate the Material in the database
        val materialList = materialRepository.findAll()
        assertThat(materialList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    fun deleteMaterial() {
        // Initialize the database
        materialRepository.saveAndFlush(materialModel)

        val databaseSizeBeforeDelete = materialRepository.findAll().size

        val id = materialModel.id
        assertNotNull(id)

        // Delete the material
        restMaterialMockMvc.perform(
            delete("/api/materials/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val materialList = materialRepository.findAll()
        assertThat(materialList).hasSize(databaseSizeBeforeDelete - 1)
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(MaterialModel::class.java)
        val materialModel1 = MaterialModel()
        materialModel1.id = 1L
        val materialModel2 = MaterialModel()
        materialModel2.id = materialModel1.id
        assertThat(materialModel1).isEqualTo(materialModel2)
        materialModel2.id = 2L
        assertThat(materialModel1).isNotEqualTo(materialModel2)
        materialModel1.id = null
        assertThat(materialModel1).isNotEqualTo(materialModel2)
    }

    @Test
    @Transactional
    fun dtoEqualsVerifier() {
        TestUtil.equalsVerifier(MaterialValueObject::class.java)
        val materialValueObject1 = MaterialValueObject()
        materialValueObject1.id = 1L
        val materialValueObject2 = MaterialValueObject()
        assertThat(materialValueObject1).isNotEqualTo(materialValueObject2)
        materialValueObject2.id = materialValueObject1.id
        assertThat(materialValueObject1).isEqualTo(materialValueObject2)
        materialValueObject2.id = 2L
        assertThat(materialValueObject1).isNotEqualTo(materialValueObject2)
        materialValueObject1.id = null
        assertThat(materialValueObject1).isNotEqualTo(materialValueObject2)
    }

    @Test
    @Transactional
    fun testEntityFromId() {
        assertThat(materialMapper.fromId(42L)?.id).isEqualTo(42)
        assertThat(materialMapper.fromId(null)).isNull()
    }

    companion object {

        private const val DEFAULT_MATERIAL_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_MATERIAL_NAME = "BBBBBBBBBB"

        private const val DEFAULT_MATERIAL_DESCRIPTION: String = "AAAAAAAAAA"
        private const val UPDATED_MATERIAL_DESCRIPTION = "BBBBBBBBBB"

        private val DEFAULT_MATERIAL_PRICE: BigDecimal = BigDecimal(1)
        private val UPDATED_MATERIAL_PRICE: BigDecimal = BigDecimal(2)
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): MaterialModel {
            val materialModel = MaterialModel()
            materialModel.materialName = DEFAULT_MATERIAL_NAME
            materialModel.materialDescription = DEFAULT_MATERIAL_DESCRIPTION
            materialModel.materialPrice = DEFAULT_MATERIAL_PRICE

        return materialModel
        }
        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): MaterialModel {
            val materialModel = MaterialModel()
            materialModel.materialName = UPDATED_MATERIAL_NAME
            materialModel.materialDescription = UPDATED_MATERIAL_DESCRIPTION
            materialModel.materialPrice = UPDATED_MATERIAL_PRICE

        return materialModel
        }
    }
}
