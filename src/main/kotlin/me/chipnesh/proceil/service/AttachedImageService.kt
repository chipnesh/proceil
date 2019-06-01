package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.AttachedImageModel
import me.chipnesh.proceil.repository.AttachedImageRepository
import me.chipnesh.proceil.service.dto.AttachedImageValueObject
import me.chipnesh.proceil.service.mapper.AttachedImageMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [AttachedImageModel].
 */
@Service
@Transactional
class AttachedImageService(
    val attachedImageRepository: AttachedImageRepository,
    val attachedImageMapper: AttachedImageMapper
) {

    private val log = LoggerFactory.getLogger(AttachedImageService::class.java)

    /**
     * Save a attachedImage.
     *
     * @param attachedImageValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(attachedImageValueObject: AttachedImageValueObject): AttachedImageValueObject {
        log.debug("Request to save AttachedImage : {}", attachedImageValueObject)

        var attachedImageModel = attachedImageMapper.toEntity(attachedImageValueObject)
        attachedImageModel = attachedImageRepository.save(attachedImageModel)
        return attachedImageMapper.toDto(attachedImageModel)
    }

    /**
     * Get all the attachedImages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<AttachedImageValueObject> {
        log.debug("Request to get all AttachedImages")
        return attachedImageRepository.findAll(pageable)
            .map(attachedImageMapper::toDto)
    }

    /**
     * Get one attachedImage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<AttachedImageValueObject> {
        log.debug("Request to get AttachedImage : {}", id)
        return attachedImageRepository.findById(id)
            .map(attachedImageMapper::toDto)
    }

    /**
     * Delete the attachedImage by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete AttachedImage : {}", id)

        attachedImageRepository.deleteById(id)
    }
}
