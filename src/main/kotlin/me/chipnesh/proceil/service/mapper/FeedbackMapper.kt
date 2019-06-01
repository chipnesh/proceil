package me.chipnesh.proceil.service.mapper

import me.chipnesh.proceil.domain.FeedbackModel
import me.chipnesh.proceil.service.dto.FeedbackValueObject

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity [FeedbackModel] and its DTO [FeedbackValueObject].
 */
@Mapper(componentModel = "spring", uses = [CustomerMapper::class])
interface FeedbackMapper : EntityMapper<FeedbackValueObject, FeedbackModel> {
    @Mappings(
        Mapping(source = "author.id", target = "authorId"),
        Mapping(source = "author.customerSummary", target = "authorCustomerSummary")
    )
    override fun toDto(entity: FeedbackModel): FeedbackValueObject
    @Mappings(
        Mapping(source = "authorId", target = "author")
    )
    override fun toEntity(dto: FeedbackValueObject): FeedbackModel

    fun fromId(id: Long?): FeedbackModel? {
        if (id == null) {
            return null
        }
        val feedbackModel = FeedbackModel()
        feedbackModel.id = id
        return feedbackModel
    }
}
