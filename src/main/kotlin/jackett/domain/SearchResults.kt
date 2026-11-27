package com.github.korurg.jackett.domain

data class SearchResults(val items: List<SearchResult>, val failedIndexers: List<String>)
