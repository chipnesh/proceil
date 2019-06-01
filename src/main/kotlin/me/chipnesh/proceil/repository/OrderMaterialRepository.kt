package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.OrderMaterialModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [OrderMaterialModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface OrderMaterialRepository : JpaRepository<OrderMaterialModel, Long>, JpaSpecificationExecutor<OrderMaterialModel>
