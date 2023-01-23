package com.leonsovic.mysecondapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leonsovic.lib.Hike
import com.leonsovic.mysecondapp.databinding.FragmentAddBinding
import java.util.*


class AddFragment : Fragment(R.layout.fragment_add) {
    private lateinit var binding: FragmentAddBinding
    private lateinit var datePickerDialog: DatePickerDialog
    var edit = false
    var dateSet = false
    var editTrail = false
    var yearG = 0
    var monthG = 0
    var dayG = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = activity?.application as App

        var titleA = arguments?.getString("titleA")
        var descriptionA = arguments?.getString("descriptionA")
        var hikeIDA = arguments?.getInt("hikeIDA")
        var dateA = arguments?.getString("dateA")
        var selectTrailA = arguments?.getString("selectTrailA")
        var posA = arguments?.getInt("posA")

        var pos = arguments?.getInt("posH")

        if (posA != null) {
            if (selectTrailA != null && posA >= 0 && posA < app.data.trailList.size) {
                Log.i("posT3",posA.toString())
                var bundle = Bundle()
                this.arguments = bundle
                val dateArr = dateA.toString().split(" ")
                pos = -1
                binding.datePickerButton.setText(makeDateString(dateArr[0].toInt(), dateArr[1].toInt()+1, dateArr[2].toInt()))
                dateSet = true
                binding.editHikeTitle.setText(titleA.toString())
                binding.editDescription.setText(descriptionA.toString())
                binding.selectTrailButton.setText(app.data.trailList[posA].getTitle())
                if (hikeIDA != null) {
                    Log.i("posHIKEIDA",hikeIDA.toString())
                    if (hikeIDA >= 0 && hikeIDA < app.data.trailList.size) {
                        edit = true
                        editTrail = true
                        binding.addButton.text = "Edit"
                        val bundle = Bundle()
                        this.arguments = bundle
                        pos = hikeIDA
                    }
                }
            }
        }


        if (pos != null) {
            if (pos >= 0) {
                Log.i("posTRAIL",pos.toString())
                edit = true
                val bundle = Bundle()
                this.arguments = bundle
                binding.addButton.text = "Edit"
                binding.datePickerButton.setText(makeDateString(app.data.hikeList[pos].getDate().date, app.data.hikeList[pos].getDate().month, app.data.hikeList[pos].getDate().year))
                dateSet = true
                binding.editHikeTitle.setText(app.data.hikeList[pos].getTitle())
                binding.editDescription.setText(app.data.hikeList[pos].getDescription())
                if (!editTrail) {
                    binding.selectTrailButton.setText(app.data.hikeList[pos].getTrail().getTitle())
                }
            }
        }

        binding.addButton.setOnClickListener {
            if (posA != null) {
                if (binding.editHikeTitle.text.toString() != "" && binding.editDescription.text.toString() != "" && posA >= 0 && posA < app.data.trailList.size) {
                    if (edit) {
                        if (pos != null) {
                            app.data.hikeList.removeAt(pos)
                        }
                    }
                    app.data.addHike(
                        Hike(
                            binding.editHikeTitle.text.toString(),
                            binding.editDescription.text.toString(),
                            app.data.trailList[posA],
                            Date(yearG,monthG+1,dayG)
                        )
                    )
                    app.saveData()
                    findNavController().popBackStack()
                }
            }
        }

        initDatePicker()
        if (!dateSet) {
            binding.datePickerButton.text = getTodaysDate()
        }

        binding.datePickerButton.setOnClickListener {
            openDatePicker(view)
        }

        binding.selectTrailButton.setOnClickListener {
            Log.i("pos",pos.toString())
            if (edit) {
                val bundle = bundleOf(
                    "titleS" to binding.editHikeTitle.text.toString(),
                    "descriptionS" to binding.editDescription.text.toString(),
                    "hikeIDS" to pos,
                    "dateS" to "$dayG $monthG $yearG",
                    "selectTrailS" to "1",
                    "edit" to 1
                )
                findNavController().navigate(
                    R.id.recycleViewFragment,
                    bundle
                )
            } else {
                val bundle = bundleOf(
                    "titleS" to binding.editHikeTitle.text.toString(),
                    "descriptionS" to binding.editDescription.text.toString(),
                    "hikeIDS" to -1,
                    "dateS" to "$dayG $monthG $yearG",
                    "selectTrailS" to "1",
                    "edit" to 0
                )
                findNavController().navigate(
                    R.id.recycleViewFragment,
                    bundle
                )
            }
        }


        app.fragmentOpened("addOpened")
    }

    private fun getTodaysDate(): String? {
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        var month = cal[Calendar.MONTH]
        yearG = year
        monthG = month
        month = month + 1
        val day = cal[Calendar.DAY_OF_MONTH]
        dayG = day
        return makeDateString(day, month, year)
    }

    private fun initDatePicker() {
        val dateSetListener =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month = month + 1
                val date = makeDateString(day, month, year)
                binding.datePickerButton.setText(date)
            }
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]
        val style: Int = AlertDialog.THEME_HOLO_LIGHT
        datePickerDialog = DatePickerDialog(requireContext(), style, dateSetListener, year, month, day)
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String? {
        yearG = year
        monthG = month-1
        dayG = day
        return getMonthFormat(month) + " " + day + " " + year
    }

    private fun getMonthFormat(month: Int): String {
        if (month == 1) return "JAN"
        if (month == 2) return "FEB"
        if (month == 3) return "MAR"
        if (month == 4) return "APR"
        if (month == 5) return "MAY"
        if (month == 6) return "JUN"
        if (month == 7) return "JUL"
        if (month == 8) return "AUG"
        if (month == 9) return "SEP"
        if (month == 10) return "OCT"
        if (month == 11) return "NOV"
        return if (month == 12) "DEC" else "JAN"

        //default should never happen
    }

    fun openDatePicker(view: View?) {
        datePickerDialog.show()
    }

}