package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.MaterialRequestModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [MaterialRequestModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface MaterialRequestRepository : JpaRepository<MaterialRequestModel, Long>, JpaSpecificationExecutor<MaterialRequestModel>
