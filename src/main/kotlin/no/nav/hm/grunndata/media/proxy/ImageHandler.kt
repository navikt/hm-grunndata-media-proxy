package no.nav.hm.grunndata.media.proxy

import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.Cacheable
import jakarta.inject.Singleton
import java.awt.Dimension
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import javax.imageio.*
import javax.imageio.metadata.IIOMetadata
import kotlin.math.min
import org.slf4j.LoggerFactory


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
    private fun resizeImage(imageMeta: BufferedImageMetaData, boundary: Dimension): BufferedImageMetaData {
        val image = imageMeta.bufferedImage
        val imageDimension = Dimension(image.width, image.height)
        val scaled = getScaledDimension(imageDimension, boundary)
        val resizedImage = BufferedImage(scaled.width, scaled.height, BufferedImage.TYPE_INT_RGB)
        val graphics2D = resizedImage.createGraphics()
        graphics2D.drawImage(image, 0, 0, scaled.width, scaled.height, null)
        graphics2D.dispose()
        image.flush()
        return BufferedImageMetaData(resizedImage, imageMeta.metadata, imageMeta.cmyk)
    }

    private fun resizeImage(imageUri: URI, boundary: Dimension): BufferedImageMetaData {
        imageUri.toURL().openStream().use {
            val imageMeta = readImage(it)
            val image = imageMeta.bufferedImage
            return if (image.width > boundary.width || image.height > boundary.height)
                resizeImage(imageMeta, boundary) else imageMeta
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
            val bufferedImageMetaData = resizeImage(sourceUri, imageVersion)
            val image = bufferedImageMetaData.bufferedImage
            writeImage(bufferedImageMetaData, format.extension, it)
            image.flush()
            return it.toByteArray()
        }
    }


    @Cacheable(parameters = ["cachePath"])
    open fun createCachedImageVersion(cachePath: String, sourceUri: URI, format: ImageFormat, imageVersion: Dimension): ByteArray {
        LOG.info("Creating imageVersion with $cachePath for $sourceUri")
        return createImageVersion(sourceUri, format, imageVersion)
    }

    private fun readImage(input: InputStream): BufferedImageMetaData {
        ImageIO.createImageInputStream(input).use {
            val readers = ImageIO.getImageReaders(it)
            val reader = readers.next()
            try {
                reader.input = it
                val metadata = reader.getImageMetadata(0)
                val param = reader.defaultReadParam
                val image = reader.read(0, param)
                var cmyk: Boolean = false
                for (type in reader.getImageTypes(0)) {
                    if (type.colorModel.colorSpace.type == ColorSpace.TYPE_CMYK) {
                        cmyk = true
                    }
                }
                return BufferedImageMetaData(image, metadata, cmyk)
            }
            finally {
                reader.dispose()
            }
        }
    }

    private fun writeImage(bufferedImageMetaData: BufferedImageMetaData, format: String, output: OutputStream) {
        ImageIO.createImageOutputStream(output).use {
            val writers = ImageIO.getImageWritersByFormatName(format)
            val writer = writers.next()
            try {
                writer.output = it
                val image = bufferedImageMetaData.bufferedImage
                writer.write(image)
            } finally {
                writer.dispose()
            }
        }
    }

}
data class BufferedImageMetaData(val bufferedImage: BufferedImage, val metadata: IIOMetadata, val cmyk: Boolean)

enum class ImageFormat(val extension: String) {
    JPG("jpg"),
    PNG("png")
}
