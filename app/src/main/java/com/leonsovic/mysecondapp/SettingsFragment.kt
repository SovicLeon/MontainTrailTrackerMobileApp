package com.leonsovic.mysecondapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.leonsovic.mysecondapp.databinding.FragmentRecycleViewBinding
import com.leonsovic.mysecondapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = activity?.application as App

        val measure = app.getMeasure()
        if (measure == "m") {
            binding.metric.isChecked = true
        } else {
            binding.imperial.isChecked = true
        }

        binding.metric.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                app.setMeasure("m")
            }
        }

        binding.imperial.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                app.setMeasure("ft")
            }
        }

        app.fragmentOpened("settingsOpened")
    }
}