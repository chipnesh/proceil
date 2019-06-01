package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.ZoneModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ZoneModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface ZoneRepository : JpaRepository<ZoneModel, Long>, JpaSpecificationExecutor<ZoneModel>
