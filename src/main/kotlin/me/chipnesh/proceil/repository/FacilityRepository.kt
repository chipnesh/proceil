package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.FacilityModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [FacilityModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface FacilityRepository : JpaRepository<FacilityModel, Long>, JpaSpecificationExecutor<FacilityModel>
