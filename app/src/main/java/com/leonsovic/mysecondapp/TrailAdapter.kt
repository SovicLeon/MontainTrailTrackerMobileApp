package com.leonsovic.mysecondapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.leonsovic.lib.Hiker

class TrailAdapter(private val data:Hiker, private val onClickObject: MyOnClick) :
    RecyclerView.Adapter<TrailAdapter.ViewHolder>() {

    lateinit var onLongClickObject: TrailAdapter.MyOnClick
    lateinit var onClickedObject: TrailAdapter.MyOnClick

    interface MyOnClick {
        fun onClick(p0: View?, position:Int)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val titleOut: TextView = itemView.findViewById(R.id.titleOut)
        val distanceOut: TextView = itemView.findViewById(R.id.distanceOut)
        val altitudeOut: TextView = itemView.findViewById(R.id.altitudeOut)
        val line: CardView = itemView.findViewById(R.id.cvLine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_rview, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.trailList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = data.trailList[position]
        holder.imageView.setImageResource(R.drawable.ic_baseline_nordic_walking_24)
        holder.titleOut.text = ItemsViewModel.getTitle()
        holder.distanceOut.text = String.format("%.2f%s", ItemsViewModel.getDistance(), "m")
        holder.altitudeOut.text = String.format("%.2f%s", ItemsViewModel.getAltitude(), "m")

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
}