package com.youreye.bussro.util

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

/**
 * [LocationToDistance]
 * 두 좌표(위도, 경도) 사이 거리를 구하는 코드
 */

class LocationToDistance {

    fun distance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
        unit: String
    ): Double {
        val theta = lon1 - lon2
        var dist =
            sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(
                deg2rad(lat2)
            ) * cos(deg2rad(theta))

        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515

        if (unit == "kilo") {
            dist *= 1.609344
        } else if (unit == "meter") {
            dist *= 1609.344
        }

        return dist
    }

    // This function converts decimal degrees to radians
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    // This function converts radians to decimal degrees
    private fun rad2deg(rad: Double): Double {
        return rad * 180 / Math.PI
    }
}