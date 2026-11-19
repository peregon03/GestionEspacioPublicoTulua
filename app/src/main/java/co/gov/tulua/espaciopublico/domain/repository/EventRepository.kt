package co.gov.tulua.espaciopublico.domain.repository

import co.gov.tulua.espaciopublico.domain.model.Event
import co.gov.tulua.espaciopublico.domain.model.EventStatus
import co.gov.tulua.espaciopublico.domain.model.SpotAssignment

interface EventRepository {
    suspend fun getEvents(
        month: String? = null,
        status: EventStatus? = null,
    ): Result<List<Event>>

    suspend fun getEventById(id: Long): Result<Event>
    suspend fun createEvent(event: Event): Result<Event>
    suspend fun updateEvent(event: Event): Result<Event>
    suspend fun getSpots(eventId: Long): Result<List<SpotAssignment>>
    suspend fun assignSpot(eventId: Long, assignment: SpotAssignment): Result<SpotAssignment>
    suspend fun removeAssignment(eventId: Long, assignmentId: Long): Result<Unit>
    suspend fun validateOverlap(zoneId: Long, startDate: String, endDate: String): Result<Boolean>
}
