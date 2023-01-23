package com.leonsovic.mysecondapp

import android.annotation.SuppressLint
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.google.gson.*
import com.leonsovic.lib.Trail
import com.leonsovic.mysecondapp.databinding.FragmentAddTrailBinding
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.log

var latitude = 0.0
var longitude = 0.0

private interface ElevationListener {
    fun onElevationReceived(elevation: Double)
}

private class GetElevationTask(private val listener: ElevationListener) : AsyncTask<Void, Void, Double>() {
    override fun doInBackground(vararg p0: Void?): Double? {
        val client = OkHttpClient()
        //val url = "https://api.open-elevation.com/api/v1/lookup?locations=$latitude,$longitude"
        val url = "https://api.open-meteo.com/v1/elevation?latitude=$latitude&longitude=$longitude"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val responseBody = response.body

        if (response.isSuccessful && responseBody != null) {
            /*val json = JSONObject(responseBody.string())
            val resultsArray = json.getJSONArray("results")
            val firstResult = resultsArray.getJSONObject(0)
            return firstResult.getDouble("elevation")*/
            val jsonString = responseBody.string()
            val json = JSONObject(jsonString)
            return json.getJSONArray("elevation").getDouble(0)
        } else {
            return 0.0
        }
    }

    override fun onPostExecute(result: Double) {
        // Update the UI with the elevation result
        listener.onElevationReceived(result)
    }
}

class AddTrailFragment : Fragment(R.layout.fragment_add_trail) {
    private lateinit var binding: FragmentAddTrailBinding
    var edit = false
    var selectTrail = false
    private lateinit var mapView: MapView

    private var lat: MutableList<Double> = ArrayList()
    private var lon: MutableList<Double> = ArrayList()

    private var distanceSum: Double = 0.0
    private var altitudeSum: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTrailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = activity?.application as App

        mapView = binding.map
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        var mapController = mapView.controller

        mapController.setZoom(6.0)
        val startPoint = GeoPoint(48.0, 20.0)
        mapController.setCenter(startPoint)

        var markerStart = Marker(mapView)
        markerStart.title = "Start point"
        markerStart.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        markerStart.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_position_start)

        var markerEnd = Marker(mapView)
        markerEnd.title = "End point"
        markerEnd.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        markerEnd.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_position_end)

        var alt1 = 0.0
        var alt2 = 0.0

        binding.startButton.setOnClickListener {
            val center = mapView.mapCenter
            if (mapView.overlays.size == 0) {
                markerStart.position = center as GeoPoint?
                mapView.overlays.add(markerStart)
                lat.add(center.latitude)
                lon.add(center.longitude)
            } else {
                if (mapView.overlays.size != 1) {
                    var markerTmp = markerEnd

                    mapView.overlays.removeAt(mapView.overlays.size-2)

                    mapView.overlays.add(makeMarker(markerTmp.position))

                    markerEnd.position = center as GeoPoint?
                    mapView.overlays.add(markerEnd)

                    makePath(getMidMarker(mapView.overlays[mapView.overlays.size-2] as Marker),markerEnd)
                } else {
                    markerEnd.position = center as GeoPoint?
                    mapView.overlays.add(markerEnd)
                    makePath(markerStart,markerEnd)
                }

                lat.add(center.latitude)
                lon.add(center.longitude)

                val loc1 = Location("")
                loc1.setLatitude(lat[lat.size-2])
                loc1.setLongitude(lon[lon.size-2])

                val loc2 = Location("")
                loc2.setLatitude(lat[lat.size-1])
                loc2.setLongitude(lon[lon.size-1])

                val distanceInMeters: Float = loc1.distanceTo(loc2)
                distanceSum += distanceInMeters.toDouble()
                binding.distanceFragmentView.text = String.format("Distance %.2f%s", distanceSum, "m")

                getAltitude(lat[lat.size-2], lon[lon.size-2], object : ElevationListener {
                    override fun onElevationReceived(elevation: Double) {
                        alt1 = elevation
                        getAltitude(lat[lat.size-1], lon[lon.size-1], object : ElevationListener {
                            override fun onElevationReceived(elevation: Double) {
                                alt2 = elevation
                                altitudeSum += abs(alt1 - alt2)

                                binding.altitudeFragmentView.text = String.format("Altitude %.2f%s", altitudeSum, "m")
                            }
                        })
                    }
                })
            }
        }

        binding.endButton.setOnClickListener {
            if (mapView.overlays.size == 1) {
                mapView.overlays.removeAt(0)
                distanceSum = 0.0
                altitudeSum = 0.0
            } else if (mapView.overlays.size == 3) {
                mapView.overlays.removeAt(mapView.overlays.size-1)
                mapView.overlays.removeAt(mapView.overlays.size-1)
                distanceSum = 0.0
                altitudeSum = 0.0
            } else if (mapView.overlays.size > 3) {
                mapView.overlays.removeAt(mapView.overlays.size-1)
                mapView.overlays.removeAt(mapView.overlays.size-1)
                markerEnd.position = getMidMarker(mapView.overlays[mapView.overlays.size-1] as Marker).position
                mapView.overlays.removeAt(mapView.overlays.size-1)
                mapView.overlays.add(mapView.overlays.size-1,markerEnd)

                val loc1 = Location("")
                loc1.setLatitude(lat[lat.size-2])
                loc1.setLongitude(lon[lon.size-2])

                val loc2 = Location("")
                loc2.setLatitude(lat[lat.size-1])
                loc2.setLongitude(lon[lon.size-1])

                getAltitude(lat[lat.size-2], lon[lon.size-2], object : ElevationListener {
                    override fun onElevationReceived(elevation: Double) {
                        alt1 = elevation
                        getAltitude(lat[lat.size-1], lon[lon.size-1], object : ElevationListener {
                            override fun onElevationReceived(elevation: Double) {
                                alt2 = elevation
                                altitudeSum -= abs(alt1 - alt2)

                                binding.altitudeFragmentView.text = String.format("Altitude %.2f%s", altitudeSum, "m")

                                lat.removeAt(lat.size-1)
                                lon.removeAt(lon.size-1)
                            }
                        })
                    }
                })

                val distanceInMeters: Float = loc1.distanceTo(loc2)
                distanceSum -= distanceInMeters.toDouble()
            }

            binding.distanceFragmentView.text = String.format("Distance %.2f%s", distanceSum, "m")
            binding.altitudeFragmentView.text = String.format("Altitude %.2f%s", altitudeSum, "m")
        }

        var titleS = arguments?.getString("titleS")
        var descriptionS = arguments?.getString("descriptionS")
        var trailIDS = arguments?.getString("trailIDS")
        var dateS = arguments?.getString("dateS")
        var selectTrailS = arguments?.getString("selectTrailS")

        if (selectTrailS != null) {
            val bundle = Bundle()
            this.arguments = bundle
            selectTrail = true
            binding.addTrailFragmentButton.text = "Select"
        }

        var pos = arguments?.getInt("posT")

        if (pos != null) {
            if (pos >= 0 && app.data.trailList.size > 0) {
                edit = true
                val bundle = Bundle()
                this.arguments = bundle
                if (!selectTrail) {
                    binding.addTrailFragmentButton.text = "Edit"
                }
                binding.distanceFragmentView.setText("Distance " + app.data.trailList[pos].getDistance().toString() + "m", TextView.BufferType.EDITABLE)
                binding.altitudeFragmentView.setText("Altitude " + app.data.trailList[pos].getAltitude().toString() + "m", TextView.BufferType.EDITABLE)
                binding.trailTitleEditText.setText(app.data.trailList[pos].getTitle(), TextView.BufferType.EDITABLE)

                distanceSum = app.data.trailList[pos].getDistance()
                altitudeSum = app.data.trailList[pos].getAltitude()

                var latPOS = app.data.trailList[pos].getLat().replace("\"", "").replace("[", "").replace("]", "").replace(",", "").split(" ")
                lat = latPOS.map { it.toDouble() }.toMutableList()

                var lonPOS = app.data.trailList[pos].getLon().replace("\"", "").replace("[", "").replace("]", "").replace(",", "").split(" ")
                lon = lonPOS.map { it.toDouble() }.toMutableList()

                mapController.setZoom(15.0)
                val startPoint = GeoPoint(lat[0], lon[0])
                mapController.setCenter(startPoint)

                while (mapView.overlays.size > 0) {
                    mapView.overlays.removeAt(0)
                }

                var i = 0
                while (i < lat.size) {
                    val center = GeoPoint(lat[i], lon[i])
                    if (mapView.overlays.size == 0) {
                        markerStart.position = center
                        mapView.overlays.add(markerStart)
                    } else {
                        if (mapView.overlays.size != 1) {
                            var markerTmp = markerEnd

                            mapView.overlays.removeAt(mapView.overlays.size-2)

                            mapView.overlays.add(makeMarker(markerTmp.position))

                            markerEnd.position = center
                            mapView.overlays.add(markerEnd)

                            makePath(getMidMarker(mapView.overlays[mapView.overlays.size-2] as Marker),markerEnd)
                        } else {
                            markerEnd.position = center
                            mapView.overlays.add(markerEnd)
                            makePath(markerStart,markerEnd)
                        }
                    }
                    i++
                }
            }
        }

        binding.addTrailFragmentButton.setOnClickListener {
            val gson = Gson()
            if (binding.trailTitleEditText.text.toString() != "" && distanceSum > 0.0) {
                if (edit) {
                    if (pos != null) {
                        app.data.trailList.removeAt(pos)
                    }
                }
                if (selectTrail) {
                    app.data.addTrail(
                        Trail(binding.trailTitleEditText.text.toString(),
                            distanceSum,
                            altitudeSum,
                            gson.toJson(lat.toString()),
                            gson.toJson(lon.toString())
                        )
                    )
                    val bundle = bundleOf(
                        "titleA" to titleS.toString(),
                        "descriptionA" to descriptionS.toString(),
                        "trailIDA" to trailIDS.toString(),
                        "dateA" to dateS.toString(),
                        "selectTrailA" to "1"
                    )
                    findNavController().navigate(
                        R.id.recycleViewFragment,
                        bundle
                    )
                } else {
                    app.data.addTrail(
                        Trail(binding.trailTitleEditText.text.toString(),
                            distanceSum,
                            altitudeSum,
                            gson.toJson(lat.toString()),
                            gson.toJson(lon.toString())
                        )
                    )
                    app.saveData()
                    findNavController().popBackStack()
                }
            }
        }

        app.fragmentOpened("addTrailOpened")
    }

    private fun getMidMarker(otherMarker: Marker): Marker {
        var marker = Marker(mapView)
        marker.title = "mid point"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.position = otherMarker.position
        marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_position_default)
        return marker
    }

    private fun makeMarker(center: GeoPoint): Marker {
        var marker = Marker(mapView)
        marker.title = "mid point"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.position = center
        marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_position_default)
        return marker
    }

    private fun makePath(markerStart: Marker, markerEnd: Marker) {
        val polyline = Polyline(mapView)
        polyline.addPoint(markerStart.position)
        polyline.addPoint(markerEnd.position)
        mapView.overlays.add(polyline)
    }

    private fun getAltitude(latitudeIN: Double, longitudeIN: Double, listener: ElevationListener) {
        longitude = longitudeIN
        latitude = latitudeIN
        GetElevationTask(listener).execute()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}