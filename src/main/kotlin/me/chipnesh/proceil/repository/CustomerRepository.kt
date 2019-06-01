package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.CustomerModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [CustomerModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface CustomerRepository : JpaRepository<CustomerModel, Long>, JpaSpecificationExecutor<CustomerModel>
