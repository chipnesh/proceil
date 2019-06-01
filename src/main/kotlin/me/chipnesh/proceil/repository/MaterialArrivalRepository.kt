package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.MaterialArrivalModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [MaterialArrivalModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface MaterialArrivalRepository : JpaRepository<MaterialArrivalModel, Long>, JpaSpecificationExecutor<MaterialArrivalModel>
