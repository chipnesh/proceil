package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.ZoneModel
import me.chipnesh.proceil.repository.ZoneRepository
import me.chipnesh.proceil.service.dto.ZoneValueObject
import me.chipnesh.proceil.service.mapper.ZoneMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [ZoneModel].
 */
@Service
@Transactional
class ZoneService(
    val zoneRepository: ZoneRepository,
    val zoneMapper: ZoneMapper
) {

    private val log = LoggerFactory.getLogger(ZoneService::class.java)

    /**
     * Save a zone.
     *
     * @param zoneValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(zoneValueObject: ZoneValueObject): ZoneValueObject {
        log.debug("Request to save Zone : {}", zoneValueObject)

        var zoneModel = zoneMapper.toEntity(zoneValueObject)
        zoneModel = zoneRepository.save(zoneModel)
        return zoneMapper.toDto(zoneModel)
    }

    /**
     * Get all the zones.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<ZoneValueObject> {
        log.debug("Request to get all Zones")
        return zoneRepository.findAll(pageable)
            .map(zoneMapper::toDto)
    }

    /**
     * Get one zone by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<ZoneValueObject> {
        log.debug("Request to get Zone : {}", id)
        return zoneRepository.findById(id)
            .map(zoneMapper::toDto)
    }

    /**
     * Delete the zone by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Zone : {}", id)

        zoneRepository.deleteById(id)
    }
}
