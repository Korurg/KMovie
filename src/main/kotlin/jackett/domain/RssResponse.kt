package com.github.korurg.jackett.domain

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("rss", "", "")
data class RssResponse(
    @XmlElement(true)
    @XmlSerialName("channel", "", "")
    val channel: Channel
)
