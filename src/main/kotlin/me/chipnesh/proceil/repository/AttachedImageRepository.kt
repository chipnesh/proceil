package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.AttachedImageModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [AttachedImageModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface AttachedImageRepository : JpaRepository<AttachedImageModel, Long>, JpaSpecificationExecutor<AttachedImageModel>
