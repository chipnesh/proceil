package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.MaterialAvailabilityModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [MaterialAvailabilityModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface MaterialAvailabilityRepository : JpaRepository<MaterialAvailabilityModel, Long>, JpaSpecificationExecutor<MaterialAvailabilityModel>
