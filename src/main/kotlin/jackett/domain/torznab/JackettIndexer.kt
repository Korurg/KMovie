package com.github.korurg.jackett.domain.torznab

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
data class JackettIndexer(
    @XmlElement(false)
    val id: String = "",
    @XmlValue
    val name: String = ""
) {
}
