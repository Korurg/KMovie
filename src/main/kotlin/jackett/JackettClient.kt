package com.github.korurg.jackett

import com.github.korurg.jackett.domain.RssResponse
import com.github.korurg.configuration.data.RemoteJackettInstance
import io.ktor.client.HttpClient
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import nl.adaptivity.xmlutil.serialization.XML

class JackettClient(private val client: HttpClient, private val xml: XML) {
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
