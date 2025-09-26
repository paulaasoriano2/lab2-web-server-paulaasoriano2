package es.unizar.webeng.lab2.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.*



@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class IntegrationTest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    /*
     * Test that ensures that the path "http://localhost:$port" that does not exist
     * returns the error page created in error.html
    */
    @Test
    fun `in case of error it returns the error page created`() {
        
        // Added the Accept header: text/html
        val headers = HttpHeaders().apply {
            accept = listOf(MediaType.TEXT_HTML)
        }
        val entity = HttpEntity<String>(headers)

        // The request to the path that does not exist
        val response = restTemplate.exchange(
            "http://localhost:$port",
            HttpMethod.GET,
            entity,
            String::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        //assertThat(response.headers.contentType).isEqualTo(MediaType.TEXT_HTML)   //this line does not work because charset is added at the end
        assertThat(response.headers.contentType?.toString()).startsWith("text/html")
        assertThat(response.body).contains("<title>Error 404 — Página no encontrada</title>")
        assertThat(response.body).contains("<h1>Oops — Página no encontrada</h1>")
    }

    /*
     * Test that ensures that the path "http://localhost:$port/time" returns a JSON with the current time
    */
    @Test fun `time returns current time`(){
        val response = restTemplate.getForEntity("http://localhost:$port/time", String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.headers.contentType?.toString()).startsWith("application/json")
        assertThat(response.body).contains("time")
    }

    
}
