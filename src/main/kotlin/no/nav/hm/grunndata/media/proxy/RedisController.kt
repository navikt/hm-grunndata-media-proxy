package no.nav.hm.grunndata.media.proxy

import io.lettuce.core.RedisClient
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import no.nav.hm.grunndata.media.proxy.ImageProxyController.Companion.LARGE
import no.nav.hm.grunndata.media.proxy.ImageProxyController.Companion.MEDIUM
import no.nav.hm.grunndata.media.proxy.ImageProxyController.Companion.SMALL
import org.slf4j.LoggerFactory

@Controller("/internal/redis")
class RedisController(private val redisClient: RedisClient) {

    companion object {
        private val LOG = LoggerFactory.getLogger(RedisController::class.java)
    }

    @Get("/flush{?id}")
    fun flush(@QueryValue id: String) {
        val keyS = "images:/imageproxy/${SMALL}d/$id"
        val keyM = "images:/imageproxy/${MEDIUM}d/$id"
        val keyL = "images:/imageproxy/${LARGE}d/$id"
        LOG.info("Deleting keys $keyS, $keyM, $keyL")
        val commands = redisClient.connect().sync()
        commands.del(keyS)
        commands.del(keyM)
        commands.del(keyL)
    }


}