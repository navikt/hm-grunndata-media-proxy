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
        const val SMALL = 400
        const val MEDIUM = 800
        const val LARGE = 1600
        const val MAX_AGE = "2592000"
    }
    init {
        LOG.info("using backend cdn url: $cdnUrl")
    }

    @Get(uri = "/${SMALL}d/{uri:.*}.jpg", produces = ["image/jpeg"])
    fun resizeSmallJpgImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val jpgUri = URI("$cdnUrl/$uri.jpg")
        return createCachedImageVersion(request, jpgUri, ImageFormat.JPG, Dimension(SMALL, SMALL))
    }

    @Get(uri = "/${SMALL}d/{uri:.*}.png", produces = ["image/png"])
    fun resizeSmallPngImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val pngUri = URI("$cdnUrl/$uri.png")
        return createCachedImageVersion(request, pngUri, ImageFormat.PNG, Dimension(SMALL, SMALL))

    }

    @Get(uri = "/${MEDIUM}d/{uri:.*}.jpg", produces = ["image/jpeg"])
    fun resizeMediumJpgImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val jpgUri = URI("$cdnUrl/$uri.jpg")
        return createCachedImageVersion(request, jpgUri, ImageFormat.JPG, Dimension(MEDIUM, MEDIUM))
    }

    @Get(uri = "/${MEDIUM}d/{uri:.*}.png", produces = ["image/png"])
    fun resizeMediumPngImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val pngUri = URI("$cdnUrl/$uri.png")
        return createCachedImageVersion(request, pngUri, ImageFormat.PNG, Dimension(MEDIUM, MEDIUM))

    }
    @Get(uri = "/${LARGE}d/{uri:.*}.jpg", produces = ["image/jpeg"])
    fun resizeLargeJpgImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val jpgUri = URI("$cdnUrl/$uri.jpg")
        return createCachedImageVersion(request, jpgUri, ImageFormat.JPG, Dimension(LARGE, LARGE))
    }

    @Get(uri = "/${LARGE}d/{uri:.*}.png", produces = ["image/png"])
    fun resizeLargePngImage(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val pngUri = URI("$cdnUrl/$uri.png")
        return createCachedImageVersion(request, pngUri, ImageFormat.PNG, Dimension(LARGE, LARGE))

    }

    private fun createCachedImageVersion(request: HttpRequest<*>,
                                         jpgUri: URI, format: ImageFormat, dimension: Dimension): MutableHttpResponse<ByteArray> {
        val path = request.path
        LOG.debug("Request for $path")
        return HttpResponse
            .ok(imageHandler.createCachedImageVersion(path, jpgUri, format, dimension))
            .header(CACHE_CONTROL, "public, immutable, max-age=$MAX_AGE")
    }

}
