package im.bigs.pg.external.config


import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration


@Configuration
class WebClientConfig {

    @Bean
    fun webClient(): WebClient {
        val timeout = Duration.ofSeconds(10)

        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout.toMillis().toInt())
            .responseTimeout(timeout)
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(timeout.seconds.toInt()))
                conn.addHandlerLast(WriteTimeoutHandler(timeout.seconds.toInt()))
            }

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .defaultHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }
}