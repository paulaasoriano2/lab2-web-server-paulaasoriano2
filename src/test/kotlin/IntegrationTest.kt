package es.unizar.webeng.lab2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestConfig::class)
class IntegrationTest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    /*
     * This test checks that the error page is returned when accessing a non-existing page.
     */
    @Test
    fun `in case of error it returns the error page created`() {
        val headers =
            HttpHeaders().apply {
                accept = listOf(MediaType.TEXT_HTML)
            }
        val entity = HttpEntity<String>(headers)

        val response =
            restTemplate.exchange(
                "https://localhost:$port",
                HttpMethod.GET,
                entity,
                String::class.java,
            )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.headers.contentType?.toString()).startsWith("text/html")
        assertThat(response.body).contains("<title>Error 404 — Página no encontrada</title>")
        assertThat(response.body).contains("<h1>Oops — Página no encontrada</h1>")
    }

    /*
     * This test checks that the /time endpoint returns the current time in JSON format.
     */
    @Test
    fun `time returns current time`() {
        val response =
            restTemplate.getForEntity(
                "https://localhost:$port/time",
                String::class.java,
            )
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.headers.contentType?.toString()).startsWith("application/json")
        assertThat(response.body).contains("time")
    }
}
