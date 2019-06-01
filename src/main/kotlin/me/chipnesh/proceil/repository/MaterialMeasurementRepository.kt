package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.MaterialMeasurementModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [MaterialMeasurementModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface MaterialMeasurementRepository : JpaRepository<MaterialMeasurementModel, Long>, JpaSpecificationExecutor<MaterialMeasurementModel>
