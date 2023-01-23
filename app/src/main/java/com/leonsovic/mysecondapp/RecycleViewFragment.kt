package com.leonsovic.mysecondapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.leonsovic.lib.Trail
import com.leonsovic.mysecondapp.databinding.FragmentRecycleViewBinding
import java.util.*

class RecycleViewFragment : Fragment(R.layout.fragment_recycle_view) {
    private lateinit var binding: FragmentRecycleViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecycleViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = activity?.application as App

        var selectTrail = false

        binding.recyclerview.layoutManager = LinearLayoutManager(activity)

        val adapter = TrailAdapter(app.data, object:TrailAdapter.MyOnClick{
            override fun onClick(p0: View?, pos:Int) {

            }})

        binding.recyclerview.adapter = adapter

        adapter.onLongClickObject = object:TrailAdapter.MyOnClick{
            override fun onClick(p0: View?, pos:Int) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Delete")
                builder.setMessage(app.data.trailList[pos].toString())
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("Yes"){dialogInterface, which ->
                    app.data.trailList.removeAt(pos)
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

        var titleS = arguments?.getString("titleS")
        var descriptionS = arguments?.getString("descriptionS")
        var hikeIDS = arguments?.getInt("hikeIDS")
        var dateS = arguments?.getString("dateS")
        var selectTrailS = arguments?.getString("selectTrailS")

        if (selectTrailS != null) {
            val bundle = Bundle()
            this.arguments = bundle
            selectTrail = true
            binding.addTrailButton.isVisible = false
        }

        binding.addTrailButton.setOnClickListener {
            findNavController().navigate(R.id.addTrailFragment)
        }

        adapter.onClickedObject = object:TrailAdapter.MyOnClick{
            override fun onClick(p0: View?, pos:Int) {
                if (selectTrail) {
                    Log.i("posHREC1",hikeIDS.toString())
                    Log.i("posT1",pos.toString())
                    val bundle = bundleOf(
                        "posT" to pos,
                        "titleT" to titleS.toString(),
                        "descriptionT" to descriptionS.toString(),
                        "hikeIDT" to hikeIDS,
                        "dateT" to dateS.toString(),
                        "selectTrailT" to "1"
                    )
                    findNavController().navigate(
                        R.id.mainFragment,
                        bundle
                    )
                } else {
                    val bundle = bundleOf(
                        "posT" to pos
                    )
                    findNavController().navigate(
                        R.id.addTrailFragment,
                        bundle
                    )
                }
            }
        }

        app.fragmentOpened("rViewOpened")
    }
}