package com.github.korurg.jackett.domain.torznab

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
data class Channel(

    @XmlElement(true)
    val title: String = "",

    @XmlElement(true)
    @XmlSerialName("item", "", "")
    val items: List<TorrentItem> = emptyList()
) {

}
