package no.nav.hm.grunndata.media.proxy

import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.Cacheable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import javax.imageio.*
import kotlin.math.min


@Singleton
@CacheConfig("images")
open class ImageHandler {

    companion object {
        private val LOG = LoggerFactory.getLogger(ImageHandler::class.java)
    }

    init {
        val readers: Iterator<ImageReader> = ImageIO.getImageReadersByFormatName("JPEG")
        ImageIO.setUseCache(false)
        while (readers.hasNext()) {
            LOG.info("reader: " + readers.next())
        }
    }
    private fun resizeImage(image: BufferedImage, boundary: Dimension): BufferedImage {
        val imageDimension = Dimension(image.width, image.height)
        val scaled = getScaledDimension(imageDimension, boundary)
        val resizedImage = BufferedImage(scaled.width, scaled.height, BufferedImage.TYPE_INT_RGB)
        val graphics2D = resizedImage.createGraphics()
        graphics2D.drawImage(image, 0, 0, scaled.width, scaled.height, null)
        graphics2D.dispose()
        image.flush()
        return resizedImage
    }

    private fun resizeImage(imageUri: URI, boundary: Dimension): BufferedImage {
        imageUri.toURL().openStream().use {
            val image = readImage(it)
            return if (image.width > boundary.width || image.height > boundary.height)
                resizeImage(image, boundary) else image
        }
    }

    private fun getScaledDimension(imageSize: Dimension, boundary: Dimension): Dimension {
        val widthRatio = boundary.getWidth() / imageSize.getWidth()
        val heightRatio = boundary.getHeight() / imageSize.getHeight()
        val ratio = min(widthRatio, heightRatio)
        return Dimension((imageSize.width * ratio).toInt(), (imageSize.height * ratio).toInt())
    }


    fun createImageVersion(sourceUri: URI, format: ImageFormat, imageVersion: Dimension): ByteArray {
        ByteArrayOutputStream().use {
            val resized = resizeImage(sourceUri, imageVersion)
            writeImage(resized, format.extension, it)
            resized.flush()
            return it.toByteArray()
        }
    }


    @Cacheable(parameters = ["cachePath"])
    open fun createCachedImageVersion(cachePath: String, sourceUri: URI, format: ImageFormat, imageVersion: Dimension): ByteArray {
        LOG.info("Creating imageVersion with $cachePath")
        return createImageVersion(sourceUri, format, imageVersion)
    }

    private fun readImage(input: InputStream): BufferedImage {
        LOG.info("Reading image input stream witch cache setting: ${ImageIO.getUseCache()}")
        ImageIO.createImageInputStream(input).use {
            val readers = ImageIO.getImageReaders(it)
            val reader = readers.next()
            try {
                reader.input = it
                val param = reader.defaultReadParam
                return reader.read(0, param)
            }
            finally {
                reader.dispose()
            }
        }
    }

    private fun writeImage(image: BufferedImage, format: String, output: OutputStream) {
        ImageIO.createImageOutputStream(output).use {
            val writers = ImageIO.getImageWritersByFormatName(format)
            val writer = writers.next()
            try {
                writer.output = it
                writer.write(image)
            } finally {
                writer.dispose()
            }
        }
    }

}

enum class ImageFormat(val extension: String) {
    JPG("jpg"),
    PNG("png")
}
