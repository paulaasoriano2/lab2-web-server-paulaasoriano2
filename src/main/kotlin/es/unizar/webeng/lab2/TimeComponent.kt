package es.unizar.webeng.lab2

import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

/*
 * Definition of a Data Transfer Object (DTO)
 * to transfer data between software application subsystems.
*/
data class TimeDTO(
    val time: LocalDateTime,
)

/*
 * Creation of a Time Provider Interface,
 * which allows for different implementations of time
 * retrieval, making the code more flexible and testable.
*/
interface TimeProvider {
    fun now(): LocalDateTime
}

/*
 * Implementation of the Time Provider Service
*/
@Service
class TimeService : TimeProvider {
    override fun now(): LocalDateTime = LocalDateTime.now()
}

/*
 * Creation of an Extension Function,
 * which allows to extend a class with new functionality without
 * inheriting from it.
*/
fun LocalDateTime.toDTO(): TimeDTO = TimeDTO(time = this)

/*
 * Creation of a REST Controller
 * Dependency injection is used here to inject the
 * TimeProvider service into the controller.
*/
@RestController
class TimeController(
    private val service: TimeProvider,
) {
    @GetMapping("/time")
    fun time(): TimeDTO = service.now().toDTO()
}
