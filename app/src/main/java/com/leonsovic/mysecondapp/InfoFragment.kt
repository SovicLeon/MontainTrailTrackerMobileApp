package com.leonsovic.mysecondapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.leonsovic.mysecondapp.databinding.FragmentInfoBinding
import com.leonsovic.mysecondapp.databinding.FragmentMainBinding

class InfoFragment : Fragment(R.layout.fragment_info) {
    private lateinit var binding: FragmentInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = activity?.application as App

        binding.appAuthor.text = String.format(resources.getString(R.string.appAuthor), resources.getString(R.string.author))
        binding.appVersion.text = String.format(resources.getString(R.string.appVersion), resources.getString(R.string.version))

        binding.idView.text = "ID: " + app.id

        app.fragmentOpened("aboutOpened")
    }
}