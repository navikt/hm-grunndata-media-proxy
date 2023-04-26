package no.nav.hm.grunndata.media.proxy

import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.Cacheable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.net.URI
import javax.imageio.ImageIO
import kotlin.math.min


@Singleton
@CacheConfig("images")
open class ImageHandler {

    companion object {
        private val LOG = LoggerFactory.getLogger(ImageHandler::class.java)
    }

    private fun resizeImage(image: BufferedImage, boundary: Dimension): BufferedImage {
        val imageDimension = Dimension(image.width, image.height)
        val scaled = getScaledDimension(imageDimension, boundary)
        val resizedImage = BufferedImage(scaled.width, scaled.height, BufferedImage.TYPE_INT_RGB)
        val graphics2D = resizedImage.createGraphics()
        graphics2D.drawImage(image, 0, 0, scaled.width, scaled.height, null)
        graphics2D.dispose()
        return resizedImage
    }

    private fun resizeImage(imageUri: URI, boundary: Dimension): BufferedImage {
        val image = ImageIO.read(imageUri.toURL())
        return if (image.width > boundary.width || image.height > boundary.height)
            resizeImage(image, boundary) else image
    }

    private fun getScaledDimension(imageSize: Dimension, boundary: Dimension): Dimension {
        val widthRatio = boundary.getWidth() / imageSize.getWidth()
        val heightRatio = boundary.getHeight() / imageSize.getHeight()
        val ratio = min(widthRatio, heightRatio)
        return Dimension((imageSize.width * ratio).toInt(), (imageSize.height * ratio).toInt())
    }


    private fun createImageVersion(sourceUri: URI, imageVersion: Dimension): ByteArray {
        val formatName = sourceUri.path.substringAfterLast(".").lowercase()
        val bos = ByteArrayOutputStream()
        ImageIO.write(resizeImage(sourceUri, imageVersion), formatName, bos)
        return bos.toByteArray()
    }


    @Cacheable(parameters = ["cachePath"])
    open suspend fun createCachedImageVersion(cachePath: String, sourceUri: URI, imageVersion: Dimension): ByteArray {
        LOG.info("Creating imageVersion with $cachePath")
        return createImageVersion(sourceUri, imageVersion)
    }



}
