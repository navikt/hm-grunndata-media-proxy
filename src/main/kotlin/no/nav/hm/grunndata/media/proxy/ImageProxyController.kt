package no.nav.hm.grunndata.media.proxy

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpHeaders.CACHE_CONTROL
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.net.URI

@Controller
class ImageProxyController(private val imageHandler: ImageHandler,
                           @Value("\${media.storage.cdnurl}") val cdnUrl: String) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ImageProxyController::class.java)
    }
    init {
        LOG.info("using backend cdn url: $cdnUrl")
    }

    @Get(uri = "/img/150w/{uri:.*}.jpg", produces = ["image/jpeg"])
    suspend fun resizeJpgImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val jpgUri = URI("$cdnUrl/$uri.jpg")
        val path = request.path
        LOG.debug("Request for $path")
        return HttpResponse
            .ok(imageHandler.createCachedImageVersion(path,jpgUri, Dimension(150, 150)))
            .header(CACHE_CONTROL, "public, immutable, max-age=2592000")
    }

    @Get(uri = "/img/150w/{uri:.*}.png", produces = ["image/png"])
    suspend fun resizePngImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val pngUri = URI("$cdnUrl/$uri.png")
        val path = request.path
        LOG.debug("Request for $path")
        return HttpResponse
            .ok(imageHandler.createCachedImageVersion(path,pngUri, Dimension(150, 150)))
            .header(CACHE_CONTROL, "public, immutable, max-age=2592000")

    }
}
