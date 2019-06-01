package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.MaterialReserveModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [MaterialReserveModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface MaterialReserveRepository : JpaRepository<MaterialReserveModel, Long>, JpaSpecificationExecutor<MaterialReserveModel>
