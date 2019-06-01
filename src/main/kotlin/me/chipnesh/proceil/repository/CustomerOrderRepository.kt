package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.CustomerOrderModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [CustomerOrderModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface CustomerOrderRepository : JpaRepository<CustomerOrderModel, Long>, JpaSpecificationExecutor<CustomerOrderModel>
