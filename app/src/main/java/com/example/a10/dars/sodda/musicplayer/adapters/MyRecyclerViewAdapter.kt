package com.example.a10.dars.sodda.musicplayer.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a10.dars.sodda.musicplayer.R
import com.example.a10.dars.sodda.musicplayer.databinding.RvItemBinding
import com.example.a10.dars.sodda.musicplayer.model.Music

class MyRecyclerViewAdapter(
    val list: ArrayList<Music>,
    val onMyItemClickListener: OnMyItemClickListener
) :
    RecyclerView.Adapter<MyRecyclerViewAdapter.Vh>() {
    inner class Vh(val rvItem: RvItemBinding) : RecyclerView.ViewHolder(rvItem.root) {
        fun onBind(music: Music) {
            rvItem.tvSongsName.text = music.name
            rvItem.tvSongAuthor.text = music.author
            val bm = BitmapFactory.decodeFile(music.image)
            rvItem.image.setImageResource(R.drawable.surface)
            rvItem.root.setOnClickListener {
                onMyItemClickListener.onRootClickListener(
                    music,
                    position
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(RvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        val user = list[position]
        holder.onBind(user)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnMyItemClickListener {
        fun onRootClickListener(music: Music, position: Int)
    }
}