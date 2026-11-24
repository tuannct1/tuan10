package com.example.tuan10

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tuan10.databinding.ItemSongBinding

class MyAdapter(
    private var songList: List<Song>,
    private val onItemClick: (Song) -> Unit,
    private val onMoreClick: ((Song) -> Unit)? = null
) : RecyclerView.Adapter<MyAdapter.SongViewHolder>() {

    class SongViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]

        holder.binding.tvItemTitle.text = song.title
        holder.binding.tvItemArtist.text = song.artist
        holder.binding.ivItemCover.setImageResource(R.drawable.ic_launcher_background)

        holder.itemView.setOnClickListener {
            onItemClick(song)
        }

        holder.binding.btnItemMore.setOnClickListener {
            onMoreClick?.invoke(song)
        }
    }

    override fun getItemCount(): Int = songList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateSongs(newList: List<Song>) {
        songList = newList
        notifyDataSetChanged()
    }
}