package com.github.didahdx.autochek.ui.fullScreenImage


import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.didahdx.autochek.R
import com.github.didahdx.autochek.databinding.FragmentImageBinding


class ImageFragment : Fragment() {


    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val IMAGE_URL = "image_url"
    }

    var imageUrl = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        if (arguments?.getString(IMAGE_URL).isNullOrEmpty()) this.findNavController().navigateUp()
        imageUrl = arguments?.getString(IMAGE_URL) ?: ""

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this)
            .load(imageUrl)
            .fitCenter()
            .error(R.drawable.ic_error_image)
            .addListener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    @Nullable e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }


                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    val bitmapDrawable = resource as BitmapDrawable
                    val bitmap = bitmapDrawable.bitmap
                    Palette.from(bitmap).maximumColorCount(32).generate { palette ->
                        if (palette != null) {
                            val rgb: Palette.Swatch? = palette.darkVibrantSwatch
                            if (rgb != null) {
                                binding.izCar.setBackgroundColor(rgb.rgb)
                            }
                        }
                    }
                    return false
                }


            })
            .into(binding.izCar)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}