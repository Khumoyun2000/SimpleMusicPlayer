package com.example.a10.dars.sodda.musicplayer

import android.content.ContentResolver
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.a10.dars.sodda.musicplayer.databinding.FragmentAllMusicListBinding
import com.example.a10.dars.sodda.musicplayer.model.Music
import com.example.a10.dars.sodda.musicplayer.utils.MySharedPreferences

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [AllMusicListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllMusicListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var handler: Handler
    lateinit var list: ArrayList<Music>
    lateinit var binding: FragmentAllMusicListBinding
    var mediaPlayer: MediaPlayer? = null
    var n: Int? = null
    val TAG = "fragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MySharedPreferences.init(requireContext())
        list = ArrayList()
        list = loadASongs()
        handler = Handler(Looper.getMainLooper())
        binding = FragmentAllMusicListBinding.inflate(inflater, container, false)
        n = arguments?.getInt("music", -1)
        Log.d(TAG, "onCreateView: ")
        playMediaPlayer()
        binding.apply { }
        return binding.root
    }


    override fun onPause() {
        super.onPause()
        mediaPlayer?.stop()
        Log.d(TAG, "onPuse:${n} ")
    }

    val runnable = object : Runnable {
        override fun run() {
            handler.postDelayed(this, 100)
            binding.seekBar.progress = mediaPlayer!!.currentPosition
            val time = milliSecondsToTimer(mediaPlayer!!.duration.toLong())
            val currentTime = milliSecondsToTimer(mediaPlayer!!.currentPosition.toLong())
            binding.time.text = "$currentTime / $time"
        }

    }

    fun milliSecondsToTimer(milliseconds: Long): String? {
        var finalTimerString = ""
        var secondsString = ""
        var minutesString = ""
        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        minutesString = if (minutes < 10) {
            "0$minutes"
        } else {
            "" + minutes
        }
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString $minutesString : $secondsString"

        // return timer string
        return finalTimerString
    }

    fun playMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(list[n!!].uri)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            binding.seekBar.max = mediaPlayer?.duration!!
            handler.postDelayed(runnable, 100)

            binding.playPause.setImageResource(R.drawable.ic_baseline_pause_24)

            binding.apply {
                name.text = list[n!!].name
                author.text = list[n!!].author
            }
        }
        binding.apply {
            circle.setOnClickListener {
                if (mediaPlayer?.isPlaying == true) {
                    playPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    mediaPlayer?.pause()
                } else {
                    playPause.setImageResource(R.drawable.ic_baseline_pause_24)
                    mediaPlayer?.start()
                }
            }
            next.setOnClickListener {
                if (n!! < list.size-1)
                    n = n?.plus(1)
                mediaPlayer?.reset()
                mediaPlayer?.setDataSource(list[n!!].uri)
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                binding.apply {
                    name.text = list[n!!].name
                    author.text = list[n!!].author
                }
            }
            previous.setOnClickListener {
                if (n!! >= 1)
                    n = n?.minus(1)
                mediaPlayer?.reset()
                mediaPlayer?.setDataSource(list[n!!].uri)
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                binding.apply {
                    name.text = list[n!!].name
                    author.text = list[n!!].author
                }

            }

        }
        binding.skipNext30.setOnClickListener {
            if (mediaPlayer?.duration == mediaPlayer!!.currentPosition) {
                if (n!! < list.size)
                    n = n?.plus(1)
                mediaPlayer?.reset()
                mediaPlayer?.setDataSource(list[n!!].uri)
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                binding.apply {
                    name.text = list[n!!].name
                    author.text = list[n!!].author
                }
            } else
                mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition.plus(3000))
        }
        binding.skipForward30.setOnClickListener {

            mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition.minus(3000))
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2)
                    mediaPlayer!!.seekTo(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

    }

    fun loadASongs(): ArrayList<Music> {
        var uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var selection: String = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val list = ArrayList<Music>()
        val contentResolver: ContentResolver = requireContext().contentResolver
        val cursor: Cursor? = contentResolver.query(uri, null, selection, null, null)
        cursor?.use { cursor ->
            // Cache column indices.
            val url = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val author =
                cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val name =
                cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)


            while (cursor.moveToNext()) {
                val urlSong = cursor.getString(url)

                val authorSong = cursor.getString(author)
                val nameSong = cursor.getString(name)


                val music = Music(urlSong, nameSong, authorSong)
                list.add(music)
            }
        }
        return list
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllMusicListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllMusicListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

private const val ARG_PARAM2 = "param2"

