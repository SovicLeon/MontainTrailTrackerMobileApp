package com.leonsovic.mysecondapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.leonsovic.lib.Hike
import com.leonsovic.lib.Trail
import com.leonsovic.mysecondapp.databinding.FragmentMainBinding
import java.util.*

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = activity?.application as App

        val measure = app.getMeasure()

        // adapter
        binding.recyclerViewMain.layoutManager = LinearLayoutManager(activity)

        val adapter = HikeAdapter(app.data, object:HikeAdapter.MyOnClick{
            override fun onClick(p0: View?, pos:Int) {

            }},requireContext(),measure)

        binding.recyclerViewMain.adapter = adapter

        adapter.onLongClickObject = object:HikeAdapter.MyOnClick{
            override fun onClick(p0: View?, pos:Int) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Delete")
                builder.setMessage(app.data.hikeList[pos].toString())
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("Yes"){dialogInterface, which ->
                    app.data.hikeList.removeAt(pos)
                    adapter.notifyDataSetChanged()
                    app.saveData()
                }
                builder.setNeutralButton("Cancel"){dialogInterface , which ->
                }
                builder.setNegativeButton("No"){dialogInterface, which ->
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        }
        
        adapter.onClickedObject = object:HikeAdapter.MyOnClick{
            override fun onClick(p0: View?, pos:Int) {
                val bundle = bundleOf(
                    "posH" to pos
                )
                findNavController().navigate(
                    R.id.addFragment,
                    bundle
                )
            }
        }

        var toRView = arguments?.getString("toRView")

        var titleA = arguments?.getString("titleT")
        var descriptionA = arguments?.getString("descriptionT")
        var hikeIDA = arguments?.getInt("hikeIDT")
        var dateA = arguments?.getString("dateT")
        var selectTrailA = arguments?.getString("selectTrailT")
        var posT = arguments?.getInt("posT")

        if (selectTrailA != null) {
            var bundle = Bundle()
            this.arguments = bundle
            Log.i("posHREC2",hikeIDA.toString())
            Log.i("posT2",posT.toString())
            bundle = bundleOf(
                "posA" to posT,
                "titleA" to titleA.toString(),
                "descriptionA" to descriptionA.toString(),
                "hikeIDA" to hikeIDA,
                "dateA" to dateA.toString(),
                "selectTrailA" to "1"
            )
            findNavController().navigate(
                R.id.addFragment,
                bundle
            )
        }

        if (toRView != null) {
            var bundle = Bundle()
            this.arguments = bundle
            findNavController().navigate(R.id.recycleViewFragment)
        }

        // binding/old
        binding.recycleViewButton.setOnClickListener {
            findNavController().navigate(R.id.recycleViewFragment)
        }
        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.addFragment)
        }
        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
        binding.aboutButton.setOnClickListener {
            findNavController().navigate(R.id.infoFragment)
        }
        binding.exitButton.setOnClickListener {
            activity?.finish()
        }

        app.fragmentOpened("mainOpened")
    }
}