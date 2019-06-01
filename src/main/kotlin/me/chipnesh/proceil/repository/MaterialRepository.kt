package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.MaterialModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [MaterialModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface MaterialRepository : JpaRepository<MaterialModel, Long>, JpaSpecificationExecutor<MaterialModel>
