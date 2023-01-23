package com.leonsovic.mysecondapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.leonsovic.lib.Hiker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class HikeAdapter(private val data: Hiker, private val onClickObject: MyOnClick, private val context: Context, private val measure: String) :
    RecyclerView.Adapter<HikeAdapter.ViewHolder>() {

    private lateinit var mapView: MapView

    lateinit var onLongClickObject: HikeAdapter.MyOnClick
    lateinit var onClickedObject: HikeAdapter.MyOnClick

    interface MyOnClick {
        fun onClick(p0: View?, position:Int)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val hikeOut: TextView = itemView.findViewById(R.id.hikeNameView)
        val dateOut: TextView = itemView.findViewById(R.id.hikeDateView)
        val distanceOut: TextView = itemView.findViewById(R.id.hikeDistanceView)
        val altitudeOut: TextView = itemView.findViewById(R.id.hikeAltitudeView)
        val mapLocation: MapView = itemView.findViewById(R.id.map)
        val measure1:  TextView = itemView.findViewById(R.id.measureView1)
        val measure2:  TextView = itemView.findViewById(R.id.measureView2)
        val line: CardView = itemView.findViewById(R.id.cvLine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_main_rview, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.hikeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = data.hikeList[position]

        var lat: MutableList<Double>
        var lon: MutableList<Double>

        var latPOS = ItemsViewModel.getTrail().getLat().replace("\"", "").replace("[", "").replace("]", "").replace(",", "").split(" ")
        lat = latPOS.map { it.toDouble() }.toMutableList()

        var lonPOS = ItemsViewModel.getTrail().getLon().replace("\"", "").replace("[", "").replace("]", "").replace(",", "").split(" ")
        lon = lonPOS.map { it.toDouble() }.toMutableList()

        holder.imageView.setImageResource(R.drawable.ic_baseline_nordic_walking_24)
        holder.hikeOut.text = ItemsViewModel.getTitle()
        holder.dateOut.text = buildString {
            append(ItemsViewModel.getDate().date)
            append(".")
            append(ItemsViewModel.getDate().month)
            append(".")
            append(ItemsViewModel.getDate().year)
        }
        var distance: Double
        var altitude: Double

        if (measure == "ft") {
            distance = ItemsViewModel.getTrail().getDistance() * 3.2808399
            altitude = ItemsViewModel.getTrail().getAltitude() * 3.2808399
        } else {
            distance = ItemsViewModel.getTrail().getDistance()
            altitude = ItemsViewModel.getTrail().getAltitude()
        }

        holder.distanceOut.text = String.format("Distance %.2f", distance)
        holder.altitudeOut.text = String.format(", Altitude %.2f", altitude)
        holder.measure1.text = measure
        holder.measure2.text = measure

        mapView = holder.mapLocation

        holder.mapLocation.setTileSource(TileSourceFactory.MAPNIK)
        holder.mapLocation.setMultiTouchControls(true)
        var mapController = holder.mapLocation.controller

        mapController.setZoom(15.0)
        val startPoint = GeoPoint(lat[0], lon[0])
        mapController.setCenter(startPoint)

        var markerStart = Marker(holder.mapLocation)
        markerStart.title = "Start point"
        markerStart.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        markerStart.icon = ContextCompat.getDrawable(context, R.drawable.ic_position_start)

        var markerEnd = Marker(holder.mapLocation)
        markerEnd.title = "End point"
        markerEnd.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        markerEnd.icon = ContextCompat.getDrawable(context, R.drawable.ic_position_end)

        while (holder.mapLocation.overlays.size > 0) {
            holder.mapLocation.overlays.removeAt(0)
        }

        var i = 0
        while (i < lat.size) {
            val center = GeoPoint(lat[i], lon[i])
            if (holder.mapLocation.overlays.size == 0) {
                markerStart.position = center
                holder.mapLocation.overlays.add(markerStart)
            } else {
                if (holder.mapLocation.overlays.size != 1) {
                    var markerTmp = markerEnd

                    holder.mapLocation.overlays.removeAt(holder.mapLocation.overlays.size-2)

                    holder.mapLocation.overlays.add(makeMarker(markerTmp.position))

                    markerEnd.position = center
                    holder.mapLocation.overlays.add(markerEnd)

                    makePath(getMidMarker(holder.mapLocation.overlays[holder.mapLocation.overlays.size-2] as Marker),markerEnd)
                } else {
                    markerEnd.position = center
                    holder.mapLocation.overlays.add(markerEnd)
                    makePath(markerStart,markerEnd)
                }
            }
            i++
        }

        holder.line.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                onClickedObject.onClick(p0,holder.adapterPosition)
            }
        })

        holder.line.setOnLongClickListener(object: View.OnLongClickListener{
            override fun onLongClick(p0: View?): Boolean {
                onLongClickObject.onClick(p0,holder.adapterPosition)
                return true
            }
        })
    }

    private fun getMidMarker(otherMarker: Marker): Marker {
        var marker = Marker(mapView)
        marker.title = "mid point"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.position = otherMarker.position
        marker.icon = ContextCompat.getDrawable(context, R.drawable.ic_position_default)
        return marker
    }

    private fun makeMarker(center: GeoPoint): Marker {
        var marker = Marker(mapView)
        marker.title = "mid point"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.position = center
        marker.icon = ContextCompat.getDrawable(context, R.drawable.ic_position_default)
        return marker
    }

    private fun makePath(markerStart: Marker, markerEnd: Marker) {
        val polyline = Polyline(mapView)
        polyline.addPoint(markerStart.position)
        polyline.addPoint(markerEnd.position)
        mapView.overlays.add(polyline)
    }
}