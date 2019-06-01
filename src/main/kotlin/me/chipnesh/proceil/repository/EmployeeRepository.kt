package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.EmployeeModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [EmployeeModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface EmployeeRepository : JpaRepository<EmployeeModel, Long>, JpaSpecificationExecutor<EmployeeModel>
