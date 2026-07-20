package com.example.learnkotlin.domain.model.home

sealed class RouteDetailItem {

    /**
     * 1. Fare - Distance - Stops
     */
    data class Summary(
        val fare: String,
        val distance: String,
        val stopCount: Int
    ) : RouteDetailItem()

    /**
     * 2. Outbound / Inbound
     */
    data class Direction(
        val selected: Variant
    ) : RouteDetailItem()

    /**
     * 3. Map
     */
    data class Map(
        val points: List<RoutePoint>,
        val stops: List<RouteStop>
    ) : RouteDetailItem()

    /**
     * 4. Route information / Bus stop
     */
    data class Segment(
        val selected: SegmentType
    ) : RouteDetailItem()

    /**
     * 5. Operator / Operating hours / Payment...
     * Chỉ xuất hiện khi chọn Route information
     */
    data class Information(

        val operator: String,

        val payment: String,

        val operatingHours: OperatingHours

    ) : RouteDetailItem()

    data class OperatingHours(

        val day1: String,
        val time1: String,

        val day2: String,
        val time2: String,

        val day3: String,
        val time3: String

    )

    /**
     * 6. Bus stop item
     * Chỉ xuất hiện khi chọn Bus stop
     */
    data class Stop(

        val stop: RouteStop,

        val distanceText: String? = null,

        val isFirst: Boolean,

        val isLast: Boolean,

        val isPassed: Boolean = false,
        val isCurrentBusStop: Boolean = false,
        var currentStopIndex: Int = 3

    ) : RouteDetailItem()
}

enum class Variant {
    OUTBOUND,
    INBOUND
}

enum class SegmentType {
    ROUTE_INFORMATION,
    BUS_STOP
}

data class InfoRow(

    val left: String,

    val right: String? = null

)

/*
Route information
listOf(

    Summary(...),

    Direction(...),

    Map(...),

    Segment(SegmentType.ROUTE_INFORMATION),

    Information(...Operator...),

    Information(...Operating hours...),

    Information(...Payment...)

)
* */

/*

Bus stop
listOf(

    Summary(...),

    Direction(...),

    Map(...),

    Segment(SegmentType.BUS_STOP)

) + stops.mapIndexed { index, stop ->

    RouteDetailItem.Stop(

        stop = stop,

        isFirst = index == 0,

        isLast = index == stops.lastIndex

    )

}
* */