package com.github.korurg.jackett.domain

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
data class TorrentItem(
    @XmlElement(true)
    val title:String = "",
    @XmlElement(true)
    @XmlSerialName("jackettindexer", "", "")
    val jackettIndexer:JackettIndexer,
    @XmlElement(true)
    val link:String,
    @XmlElement(true)
    val size: Long,
)
