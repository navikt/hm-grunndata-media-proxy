package no.nav.hm.grunndata.media.proxy

import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.Cacheable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.net.URI

@Singleton
@CacheConfig("files")
open class FileHandler {

    companion object {
        private val LOG = LoggerFactory.getLogger(FileHandler::class.java)
    }

    @Cacheable(parameters = ["cachePath"])
    open fun createCachedFile(cachePath: String, sourceUri: URI): ByteArray {
        LOG.info("Creating cached $cachePath file for $sourceUri")
        return sourceUri.toURL().openStream().use {
            it.readBytes()
        }
    }

}