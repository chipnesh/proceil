package me.chipnesh.proceil.service

import me.chipnesh.proceil.domain.FacilityModel
import me.chipnesh.proceil.repository.FacilityRepository
import me.chipnesh.proceil.service.dto.FacilityValueObject
import me.chipnesh.proceil.service.mapper.FacilityMapper
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.Optional

/**
 * Service Implementation for managing [FacilityModel].
 */
@Service
@Transactional
class FacilityService(
    val facilityRepository: FacilityRepository,
    val facilityMapper: FacilityMapper
) {

    private val log = LoggerFactory.getLogger(FacilityService::class.java)

    /**
     * Save a facility.
     *
     * @param facilityValueObject the entity to save.
     * @return the persisted entity.
     */
    fun save(facilityValueObject: FacilityValueObject): FacilityValueObject {
        log.debug("Request to save Facility : {}", facilityValueObject)

        var facilityModel = facilityMapper.toEntity(facilityValueObject)
        facilityModel = facilityRepository.save(facilityModel)
        return facilityMapper.toDto(facilityModel)
    }

    /**
     * Get all the facilities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<FacilityValueObject> {
        log.debug("Request to get all Facilities")
        return facilityRepository.findAll(pageable)
            .map(facilityMapper::toDto)
    }

    /**
     * Get one facility by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<FacilityValueObject> {
        log.debug("Request to get Facility : {}", id)
        return facilityRepository.findById(id)
            .map(facilityMapper::toDto)
    }

    /**
     * Delete the facility by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Facility : {}", id)

        facilityRepository.deleteById(id)
    }
}
