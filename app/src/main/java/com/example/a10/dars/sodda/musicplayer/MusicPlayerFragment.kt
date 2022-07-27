package com.example.a10.dars.sodda.musicplayer

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.a10.dars.sodda.musicplayer.adapters.MyRecyclerViewAdapter
import com.example.a10.dars.sodda.musicplayer.databinding.FragmentMusicPlayerBinding
import com.example.a10.dars.sodda.musicplayer.model.Music
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MusicPlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MusicPlayerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var list: ArrayList<Music>
    lateinit var myRecyclerViewAdapter: MyRecyclerViewAdapter
    lateinit var binding: FragmentMusicPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)

        list = ArrayList()
        askPermission()
        return binding.root
    }

    fun onMyItemClickListener(): MyRecyclerViewAdapter.OnMyItemClickListener {
        val myItemClickListener = object : MyRecyclerViewAdapter.OnMyItemClickListener {
            override fun onRootClickListener(music: Music, position: Int) {
                val bundle = bundleOf("music" to position)
                findNavController().navigate(R.id.allMusicListFragment, bundle)

            }

        }
        return myItemClickListener
    }


    override fun onResume() {
        super.onResume()
        myRecyclerViewAdapter = MyRecyclerViewAdapter(list, onMyItemClickListener())
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
            val duration =
                cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val urlSong = cursor.getString(url)

                val authorSong = cursor.getString(author)
                val nameSong = cursor.getString(name)
                val durationSong = cursor.getString(duration)

                val music = Music(urlSong, nameSong, authorSong, durationSong)
                list.add(music)
            }
        }
        return list
    }

    private fun askPermission() {
        Dexter.withContext(binding.root.context)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener, PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    loadASongs()
                    list = loadASongs()
                    Log.d("aaa", "${list.size}")
                    myRecyclerViewAdapter = MyRecyclerViewAdapter(list, onMyItemClickListener())
                    binding.apply {
                        rv.adapter = myRecyclerViewAdapter
                    }
                }

                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {
                        loadASongs()
                        list = loadASongs()
                        Log.d("aaa", "${list.size}")
                        myRecyclerViewAdapter = MyRecyclerViewAdapter(list, onMyItemClickListener())
                        binding.apply {
                            rv.adapter = myRecyclerViewAdapter
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Permission Denied! plz enable application permission from app settings!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Snackbar.make(
                        binding.root,
                        "Please permit the permission through Settings screen.",
                        Snackbar.LENGTH_LONG
                    ).setAction("Settings", object : View.OnClickListener {
                        override fun onClick(p0: View?) {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri: Uri =
                                Uri.fromParts("package", requireContext().packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }

                    })
                        .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }


            }).onSameThread().check()
    }
//    private fun loadMusic(): ArrayList<Music> {
//        val audios = ArrayList<Music>()
//        var uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        val collection =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                MediaStore.Audio.Media.getContentUri(
//                    MediaStore.VOLUME_EXTERNAL
//                )
//            } else {
//                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//            }
//
//        val projection = arrayOf(
//            MediaStore.Video.Media._ID,
//            MediaStore.Audio.Media.DISPLAY_NAME,
//            MediaStore.Audio.Media.AUTHOR,
//            MediaStore.Audio.Media.ALBUM,
//            MediaStore.Audio.Media.DURATION,
//            MediaStore.Video.Media.SIZE
//        )
//
//// Show only videos that are at least 5 minutes in duration.
//        val selection = "${MediaStore.Audio.Media.DURATION} >= ?"
//        val selectionArgs = arrayOf(
//            TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES).toString()
//        )
//
//// Display videos in alphabetical order based on their display name.
//        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
//        val contentResolver: ContentResolver = requireContext().contentResolver
//        val query: Cursor? = contentResolver.query(
//            collection,
//            projection,
//            selection,
//            selectionArgs,
//            sortOrder
//        )
//        query?.use { cursor ->
//            // Cache column indices.
//            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
////            val urlColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
//            val nameColumn =
//                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
//            val authorColum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.AUTHOR)
//            val albumColum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
//            val durationColumn =
//                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
//            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
//
//            while (cursor.moveToNext()) {
//                // Get values of columns for a given video.
//                val id = cursor.getLong(idColumn)
//                val name = cursor.getString(nameColumn)
//                val author = cursor.getString(authorColum)
//                val album = cursor.getString(albumColum)
//                val duration = cursor.getInt(durationColumn)
//                val size = cursor.getInt(sizeColumn)
//
//                val contentUri: Uri = ContentUris.withAppendedId(
//                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                    id
//                )
//                // Stores column values and the contentUri in a local object
//                // that represents the media file.
//                audios += Music(contentUri, name, author, album)
//            }
//        }
//        return audios
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MusicPlayerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MusicPlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}