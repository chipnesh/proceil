package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.ServiceModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ServiceModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface ServiceRepository : JpaRepository<ServiceModel, Long>, JpaSpecificationExecutor<ServiceModel>
