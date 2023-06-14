package ru.qwonix.android.foxwhiskers.util

import com.yandex.mapkit.map.CircleMapObject
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectVisitor
import com.yandex.mapkit.map.PolygonMapObject
import com.yandex.mapkit.map.PolylineMapObject

abstract class ForEachMapObjectVisitor : MapObjectVisitor {

    override fun onPolylineVisited(p0: PolylineMapObject) {}

    override fun onPolygonVisited(p0: PolygonMapObject) {}

    override fun onCircleVisited(p0: CircleMapObject) {}

    override fun onCollectionVisitStart(p0: MapObjectCollection): Boolean {
        return true
    }

    override fun onCollectionVisitEnd(p0: MapObjectCollection) {}

    override fun onClusterizedCollectionVisitStart(p0: ClusterizedPlacemarkCollection): Boolean {
        return true
    }

    override fun onClusterizedCollectionVisitEnd(p0: ClusterizedPlacemarkCollection) {}
}