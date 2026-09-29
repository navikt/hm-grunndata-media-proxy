package no.nav.hm.grunndata.media.proxy

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI

object CdnStream {
    fun <T> use(uri: URI, cdnUrl: String, block: (InputStream) -> T): T {
        val configuredCdn = URI(cdnUrl)
        require(uri.scheme == configuredCdn.scheme &&
                uri.host == configuredCdn.host &&
                uri.effectivePort() == configuredCdn.effectivePort()) {
            "Source URI must use the configured CDN"
        }

        val connection = uri.toURL().openConnection() as HttpURLConnection
        connection.instanceFollowRedirects = false
        return try {
            connection.inputStream.use(block)
        } finally {
            connection.disconnect()
        }
    }

    private fun URI.effectivePort(): Int = if (port == -1) {
        when (scheme.lowercase()) {
            "https" -> 443
            "http" -> 80
            else -> -1
        }
    } else {
        port
    }
}
