package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.ServiceQuotaModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [ServiceQuotaModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface ServiceQuotaRepository : JpaRepository<ServiceQuotaModel, Long>, JpaSpecificationExecutor<ServiceQuotaModel>
