package com.github.korurg.jackett

import com.github.korurg.configuration.data.RemoteJackettInstance
import com.github.korurg.jackett.domain.torznab.RssResponse
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import nl.adaptivity.xmlutil.serialization.XML

class JacketTorznabClient(private val client: HttpClient, private val xml: XML) {
    suspend fun search(instance: RemoteJackettInstance, apiKey: String, query: String): RssResponse {
        val response = client.get("${instance.url.trimEnd('/')}/api/v2.0/indexers/all/results/torznab/api") {
            expectSuccess = true
            parameter("apikey", apiKey)
            parameter("t", "search")
            parameter("q", query)
        }
        return xml.decodeFromString(RssResponse.serializer(), response.bodyAsText())
    }
}
