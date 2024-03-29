package no.nav.hm.grunndata.media.proxy

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import java.net.URI


@Controller("/file")
class FileProxyController(private val fileHandler: FileHandler,
                          @Value("\${media.storage.cdnurl}") val cdnUrl: String) {

    companion object {
        const val MAX_FILE_AGE = "2592000"
    }

    @Get("/{uri:.*}.pdf", produces = ["application/pdf"])
    fun getPdfFile(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val pdfUri = URI("$cdnUrl/$uri.pdf")
        return HttpResponse.ok(fileHandler.createCachedFile(request.path, pdfUri))
            .header("Cache-Control", "max-age=$MAX_FILE_AGE")
    }

    @Get("/{uri:.*}.png", produces = ["image/png"])
    fun getPngFile(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val pngUri = URI("$cdnUrl/$uri.png")
        return HttpResponse.ok(fileHandler.createCachedFile(request.path, pngUri))
            .header("Cache-Control", "max-age=$MAX_FILE_AGE")
    }

    @Get("/{uri:.*}.jpg", produces = ["image/jpeg"])
    fun getJpgFile(uri: String, request: HttpRequest<*>): HttpResponse<ByteArray> {
        val jpgUri = URI("$cdnUrl/$uri.jpg")
        return HttpResponse.ok(fileHandler.createCachedFile(request.path, jpgUri))
            .header("Cache-Control", "max-age=$MAX_FILE_AGE")
    }

}