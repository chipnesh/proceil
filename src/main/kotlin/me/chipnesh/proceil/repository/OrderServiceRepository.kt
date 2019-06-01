package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.OrderServiceModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [OrderServiceModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface OrderServiceRepository : JpaRepository<OrderServiceModel, Long>, JpaSpecificationExecutor<OrderServiceModel>
