package no.nav.hm.grunndata.media.proxy

import io.kotest.matchers.nulls.shouldNotBeNull
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import no.nav.hm.grunndata.media.proxy.ImageProxyController.Companion.LARGE
import org.junit.jupiter.api.Test
import java.awt.Dimension
import java.io.File
import java.net.URI

@MicronautTest
class ImageHandlerTest(private val imageHandler: ImageHandler) {

    //@Test
    fun imageHandler() {
        val imageUrl = URI("https://cdn.dev.nav.no/teamdigihot/grunndata/media/v1/orig/38227.jpg")
        val byteArray = imageHandler.createImageVersion(imageUrl, ImageFormat.JPG, Dimension(LARGE, LARGE))
        val file = File("test.jpg").writeBytes(byteArray)
        file.shouldNotBeNull()
    }
}
