package no.nav.hm.grunndata.media.proxy

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpHeaders.CACHE_CONTROL
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
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
        const val SMALL = 150
        const val LARGE = 1600
        const val MAX_AGE = "2592000"
    }
    init {
        LOG.info("using backend cdn url: $cdnUrl")
    }

    @Get(uri = "/img/${SMALL}w/{uri:.*}.jpg", produces = ["image/jpeg"])
    suspend fun resizeSmallJpgImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val jpgUri = URI("$cdnUrl/$uri.jpg")
        val path = request.path
        LOG.debug("Request for $path")
        return HttpResponse
            .ok(imageHandler.createCachedImageVersion(path,jpgUri, Dimension(SMALL, SMALL)))
            .cacheControl(MAX_AGE)
    }

    @Get(uri = "/img/${SMALL}w/{uri:.*}.png", produces = ["image/png"])
    suspend fun resizeSmallPngImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val pngUri = URI("$cdnUrl/$uri.png")
        val path = request.path
        LOG.debug("Request for $path")
        return HttpResponse
            .ok(imageHandler.createCachedImageVersion(path,pngUri, Dimension(SMALL, SMALL)))
            .cacheControl(MAX_AGE)

    }

    @Get(uri = "/img/${LARGE}w/{uri:.*}.jpg", produces = ["image/jpeg"])
    suspend fun resizeLargeJpgImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val jpgUri = URI("$cdnUrl/$uri.jpg")
        val path = request.path
        LOG.debug("Request for $path")
        return HttpResponse
            .ok(imageHandler.createCachedImageVersion(path,jpgUri, Dimension(LARGE, LARGE)))
            .cacheControl(MAX_AGE)
    }

    @Get(uri = "/img/${LARGE}w/{uri:.*}.png", produces = ["image/png"])
    suspend fun resizeLargePngImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val pngUri = URI("$cdnUrl/$uri.png")
        val path = request.path
        LOG.debug("Request for $path")
        return HttpResponse
            .ok(imageHandler.createCachedImageVersion(path,pngUri, Dimension(LARGE, LARGE)))
            .cacheControl(MAX_AGE)

    }
}

fun MutableHttpResponse<ByteArray>.cacheControl(maxAge: String)
= this.header(CACHE_CONTROL, "public, immutable, max-age=$maxAge")
