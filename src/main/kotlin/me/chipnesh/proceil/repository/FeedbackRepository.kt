package me.chipnesh.proceil.repository

import me.chipnesh.proceil.domain.FeedbackModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [FeedbackModel] entity.
 */
@SuppressWarnings("unused")
@Repository
interface FeedbackRepository : JpaRepository<FeedbackModel, Long>, JpaSpecificationExecutor<FeedbackModel>
