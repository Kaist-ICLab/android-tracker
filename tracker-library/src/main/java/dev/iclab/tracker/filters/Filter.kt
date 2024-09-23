package dev.iclab.tracker.filters

typealias Filter = (Map<String, Any>) -> Map<String, Any>

fun Map<String, Any>.applyFilters(filters: List<Filter>): Map<String,Any> {
    var currentMap = this
    for (filter in filters) {
        currentMap = filter(currentMap)
    }
    return currentMap
}