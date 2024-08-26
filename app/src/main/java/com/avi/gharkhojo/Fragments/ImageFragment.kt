package com.avi.gharkhojo.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.avi.gharkhojo.R

class ImageFragment : Fragment() {

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_IMAGE_COUNT = "imageCount"

        fun newInstance(title: String, imageCount: Int): ImageFragment {
            val fragment = ImageFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putInt(ARG_IMAGE_COUNT, imageCount)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image, container, false)

        // Load images based on title and image count
        val title = arguments?.getString(ARG_TITLE)
        val imageCount = arguments?.getInt(ARG_IMAGE_COUNT)

        // Load your images into the layout

        return view
    }
}
