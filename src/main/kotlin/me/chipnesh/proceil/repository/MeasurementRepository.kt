package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.MeasurementModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [MeasurementModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface MeasurementRepository : JpaRepository<MeasurementModel, Long>, JpaSpecificationExecutor<MeasurementModel>
