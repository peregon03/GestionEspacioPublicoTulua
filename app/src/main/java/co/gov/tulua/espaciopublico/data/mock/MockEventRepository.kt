package co.gov.tulua.espaciopublico.data.mock

import co.gov.tulua.espaciopublico.domain.model.Event
import co.gov.tulua.espaciopublico.domain.model.EventStatus
import co.gov.tulua.espaciopublico.domain.model.GeoLocation
import co.gov.tulua.espaciopublico.domain.model.SpotAssignment
import co.gov.tulua.espaciopublico.domain.repository.EventRepository
import javax.inject.Inject

class MockEventRepository @Inject constructor() : EventRepository {

    private val events = mutableListOf(
        Event(
            id = 1L, code = "RAD-EV-2024-041", name = "Feria Artesanal de Tuluá",
            status = EventStatus.PROGRAMADA,
            startDate = "2026-12-10", endDate = "2026-12-15",
            location = GeoLocation("Parque Boyacá, Cra 26 #26-01", 4.0840, -76.1945, 3),
            organizer = "Secretaría de Cultura", comuna = "Comuna 1",
            totalSpots = 45, assignedSpots = 32, zoneId = 3L,
        ),
        Event(
            id = 2L, code = "RAD-EV-2024-042", name = "Mercado Campesino Navideño",
            status = EventStatus.PROGRAMADA,
            startDate = "2026-12-20", endDate = "2026-12-24",
            location = GeoLocation("Plaza de Mercado Central, Cra 24 #27-01", 4.0825, -76.1930, 5),
            organizer = "Secretaría de Gobierno", comuna = "Comuna 1",
            totalSpots = 60, assignedSpots = 45, zoneId = 5L,
        ),
        Event(
            id = 3L, code = "RAD-EV-2024-043", name = "Festival Gastronómico del Valle",
            status = EventStatus.EN_CURSO,
            startDate = "2026-11-15", endDate = "2026-11-22",
            location = GeoLocation("Parque Céspedes, Cll 27 #27-01", 4.0832, -76.1938, 4),
            organizer = "Cámara de Comercio de Tuluá", comuna = "Comuna 2",
            totalSpots = 30, assignedSpots = 30, zoneId = 4L,
        ),
        Event(
            id = 4L, code = "RAD-EV-2024-044", name = "Feria del Libro y la Cultura",
            status = EventStatus.EN_CURSO,
            startDate = "2026-11-18", endDate = "2026-11-25",
            location = GeoLocation("Parque Boyacá, Cra 26 #26-18", 4.0842, -76.1947, 3),
            organizer = "Biblioteca Municipal", comuna = "Comuna 1",
            totalSpots = 20, assignedSpots = 18, zoneId = 3L,
        ),
        Event(
            id = 5L, code = "RAD-EV-2024-045", name = "Expo Emprendedores Tuluá",
            status = EventStatus.PROGRAMADA,
            startDate = "2027-01-15", endDate = "2027-01-20",
            location = GeoLocation("Carrera 25 #25-10", 4.0855, -76.1958, 1),
            organizer = "SENA Regional Valle", comuna = "Comuna 1",
            totalSpots = 50, assignedSpots = 12, zoneId = 1L,
        ),
        Event(
            id = 6L, code = "RAD-EV-2024-046", name = "Noche de Velitas Comunitaria",
            status = EventStatus.PROGRAMADA,
            startDate = "2026-12-07", endDate = "2026-12-07",
            location = GeoLocation("Parque Céspedes, Cll 27 #27-10", 4.0833, -76.1939, 4),
            organizer = "Junta de Acción Comunal", comuna = "Comuna 2",
            totalSpots = 15, assignedSpots = 10, zoneId = 4L,
        ),
        Event(
            id = 7L, code = "RAD-EV-2024-047", name = "Feria Agropecuaria del Centro Valle",
            status = EventStatus.FINALIZADA,
            startDate = "2026-10-01", endDate = "2026-10-05",
            location = GeoLocation("Galería, Cll 26 #25-05", 4.0848, -76.1952, 2),
            organizer = "Secretaría de Agricultura", comuna = "Comuna 1",
            totalSpots = 40, assignedSpots = 40, zoneId = 2L,
        ),
        Event(
            id = 8L, code = "RAD-EV-2024-048", name = "Bazar Escolar Fin de Año",
            status = EventStatus.FINALIZADA,
            startDate = "2026-10-20", endDate = "2026-10-22",
            location = GeoLocation("Carrera 25 #26-05", 4.0860, -76.1962, 1),
            organizer = "Secretaría de Educación", comuna = "Comuna 1",
            totalSpots = 25, assignedSpots = 25, zoneId = 1L,
        ),
    )

    private val spots = mutableMapOf<Long, MutableList<SpotAssignment>>().apply {
        // Pre-populate spots for event 3 (en curso, full)
        put(3L, (1..30).map { i ->
            SpotAssignment(
                id = i.toLong(), eventId = 3L,
                spotLabel = "${('A' + (i - 1) / 10)}-${String.format("%02d", (i - 1) % 10 + 1)}",
                vendorId = ((i - 1) % 20 + 1).toLong(),
                vendorName = "Comerciante $i",
            )
        }.toMutableList())
        // Some spots for event 1
        put(1L, (1..32).map { i ->
            SpotAssignment(
                id = 100L + i, eventId = 1L,
                spotLabel = "${('A' + (i - 1) / 15)}-${String.format("%02d", (i - 1) % 15 + 1)}",
                vendorId = if (i <= 20) ((i - 1) % 20 + 1).toLong() else null,
                vendorName = if (i <= 20) "Comerciante $i" else null,
            )
        }.toMutableList())
    }

    override suspend fun getEvents(month: String?, status: EventStatus?): Result<List<Event>> {
        val filtered = events.filter { e ->
            (month == null || e.startDate.startsWith(month) || e.endDate.startsWith(month)) &&
                (status == null || e.status == status)
        }
        return Result.success(filtered)
    }

    override suspend fun getEventById(id: Long): Result<Event> {
        val event = events.find { it.id == id }
            ?: return Result.failure(NoSuchElementException("Evento $id no encontrado"))
        return Result.success(event)
    }

    override suspend fun createEvent(event: Event): Result<Event> {
        val newEvent = event.copy(id = (events.maxOfOrNull { it.id } ?: 0) + 1)
        events.add(newEvent)
        return Result.success(newEvent)
    }

    override suspend fun updateEvent(event: Event): Result<Event> {
        val index = events.indexOfFirst { it.id == event.id }
        if (index == -1) return Result.failure(NoSuchElementException("Evento ${event.id} no encontrado"))
        events[index] = event
        return Result.success(event)
    }

    override suspend fun getSpots(eventId: Long): Result<List<SpotAssignment>> {
        return Result.success(spots[eventId] ?: emptyList())
    }

    override suspend fun assignSpot(eventId: Long, assignment: SpotAssignment): Result<SpotAssignment> {
        val eventSpots = spots.getOrPut(eventId) { mutableListOf() }
        val newAssignment = assignment.copy(id = (eventSpots.maxOfOrNull { it.id } ?: 0) + 1)
        eventSpots.add(newAssignment)
        // Update assigned count
        val event = events.find { it.id == eventId }
        if (event != null) {
            val idx = events.indexOf(event)
            events[idx] = event.copy(assignedSpots = event.assignedSpots + 1)
        }
        return Result.success(newAssignment)
    }

    override suspend fun removeAssignment(eventId: Long, assignmentId: Long): Result<Unit> {
        spots[eventId]?.removeAll { it.id == assignmentId }
        val event = events.find { it.id == eventId }
        if (event != null) {
            val idx = events.indexOf(event)
            events[idx] = event.copy(assignedSpots = (event.assignedSpots - 1).coerceAtLeast(0))
        }
        return Result.success(Unit)
    }

    override suspend fun validateOverlap(zoneId: Long, startDate: String, endDate: String): Result<Boolean> {
        val hasOverlap = events.any { e ->
            e.zoneId == zoneId &&
                e.status != EventStatus.FINALIZADA &&
                e.startDate <= endDate && e.endDate >= startDate
        }
        return Result.success(!hasOverlap)
    }
}
