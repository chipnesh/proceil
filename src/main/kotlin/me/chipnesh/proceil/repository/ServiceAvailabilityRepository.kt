package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.ServiceAvailabilityModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ServiceAvailabilityModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface ServiceAvailabilityRepository : JpaRepository<ServiceAvailabilityModel, Long>, JpaSpecificationExecutor<ServiceAvailabilityModel>
