package no.nav.hm.grunndata.media.proxy

import io.micronaut.cache.annotation.CacheConfig
import io.micronaut.cache.annotation.Cacheable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.net.URI
import io.micronaut.context.annotation.Value

@Singleton
@CacheConfig("files")
open class FileHandler(@Value("\${media.storage.cdnurl}") private val cdnUrl: String) {

    companion object {
        private val LOG = LoggerFactory.getLogger(FileHandler::class.java)
    }

    @Cacheable(parameters = ["cachePath"])
    open fun createCachedFile(cachePath: String, sourceUri: URI): ByteArray {
        LOG.info("Creating cached $cachePath file for $sourceUri")
        return CdnStream.use(sourceUri, cdnUrl) {
            it.readBytes()
        }
    }

}