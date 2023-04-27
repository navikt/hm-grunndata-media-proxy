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

    @Get(uri = "/${SMALL}w/{uri:.*}.jpg", produces = ["image/jpeg"])
    fun resizeSmallJpgImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val jpgUri = URI("$cdnUrl/$uri.jpg")
        return createCachedImageVersion(request, jpgUri, Dimension(SMALL, SMALL))
    }

    @Get(uri = "/${SMALL}w/{uri:.*}.png", produces = ["image/png"])
    fun resizeSmallPngImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val pngUri = URI("$cdnUrl/$uri.png")
        return createCachedImageVersion(request, pngUri, Dimension(SMALL, SMALL))

    }

    @Get(uri = "/${LARGE}w/{uri:.*}.jpg", produces = ["image/jpeg"])
    fun resizeLargeJpgImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val jpgUri = URI("$cdnUrl/$uri.jpg")
        return createCachedImageVersion(request, jpgUri, Dimension(LARGE, LARGE))
    }

    @Get(uri = "/${LARGE}w/{uri:.*}.png", produces = ["image/png"])
    fun resizeLargePngImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val pngUri = URI("$cdnUrl/$uri.png")
        return createCachedImageVersion(request, pngUri, Dimension(LARGE, LARGE))

    }

    private fun createCachedImageVersion(request: HttpRequest<*>,
                                         jpgUri: URI, dimension: Dimension): MutableHttpResponse<ByteArray> {
        val path = request.path
        LOG.debug("Request for large $path")
        return HttpResponse
            .ok(imageHandler.createCachedImageVersion(path, jpgUri, dimension))
            .header(CACHE_CONTROL, "public, immutable, max-age=$MAX_AGE")
    }

}
