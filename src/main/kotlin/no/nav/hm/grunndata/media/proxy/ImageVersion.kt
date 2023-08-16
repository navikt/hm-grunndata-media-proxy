package no.nav.hm.grunndata.media.proxy

import java.awt.Dimension
import java.net.URI

data class ImageVersion(
    val cachePath: String,
    val sourceUri: URI,
    val format: ImageFormat,
    val imageVersion: Dimension,
    val byteArray: ByteArray
)
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageVersion

        if (cachePath != other.cachePath) return false
        if (sourceUri != other.sourceUri) return false
        if (format != other.format) return false
        if (imageVersion != other.imageVersion) return false
        return true
    }

    override fun hashCode(): Int {
        var result = cachePath.hashCode()
        result = 31 * result + sourceUri.hashCode()
        result = 31 * result + format.hashCode()
        result = 31 * result + imageVersion.hashCode()
        return result
    }
}

