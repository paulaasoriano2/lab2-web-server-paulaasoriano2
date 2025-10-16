package es.unizar.webeng.lab2

import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory
import org.apache.hc.core5.http.config.RegistryBuilder
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.function.Supplier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/*
 * Configuration class to create a TestRestTemplate that ignores SSL certificate errors.
*/
@Configuration
class TestConfig {
    /*
     * Create a TestRestTemplate that ignores SSL certificate errors.
     * This is necessary because the server uses a self-signed certificate.
     */
    @Bean
    fun testRestTemplate(): TestRestTemplate {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts =
            arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()

                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String,
                    ) {
                    }

                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String,
                    ) {
                    }
                },
            )

        // Install the all-trusting trust manager
        val sslContext =
            SSLContext.getInstance("TLS").apply {
                init(null, trustAllCerts, SecureRandom())
            }

        // Create an SSL socket factory with our all-trusting manager
        val sslSocketFactory =
            SSLConnectionSocketFactory(
                sslContext,
                NoopHostnameVerifier.INSTANCE,
            )

        // Create a registry of socket factories, for both HTTP and HTTPS
        val socketFactoryRegistry =
            RegistryBuilder
                .create<org.apache.hc.client5.http.socket.ConnectionSocketFactory>()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build()

        // Create an HttpClient that uses our socket factory registry
        val connectionManager = PoolingHttpClientConnectionManager(socketFactoryRegistry)
        val httpClient =
            HttpClients
                .custom()
                .setConnectionManager(connectionManager)
                .build()

        val factory = HttpComponentsClientHttpRequestFactory(httpClient)

        return TestRestTemplate(
            RestTemplateBuilder().requestFactory(Supplier { factory }),
        )
    }
}
