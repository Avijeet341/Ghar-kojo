package com.avi.gharkhojo.Fragments

import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.avi.gharkhojo.Adapter.MyViewPagerAdapter
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentHomeDetailsBinding
import kotlin.math.abs

class HomeDetails : Fragment() {

    private var _binding: FragmentHomeDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var photoAdapter: MyViewPagerAdapter
    private val imageResIds = listOf(
        R.drawable.hall,
        R.drawable.hall,
        R.drawable.bedroom,
        R.drawable.entry,
        R.drawable.base,
        R.drawable.living,
        R.drawable.bath,
        R.drawable.shoe
    )

    private val handler = Handler(Looper.getMainLooper())
    private val autoSlideRunnable = object : Runnable {
        override fun run() {
            val currentItem = binding.viewPager.currentItem
            val nextItem = (currentItem + 1) % imageResIds.size
            binding.viewPager.setCurrentItem(nextItem, true)
            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Initialization()
        setupViewPager()
        setupCopyButton()


        view.post {
            gradientSweepTextColorAnimation()
        }

        binding.viewCharges.setOnClickListener {
            showViewChargesBottomSheet()
        }

    }

    private fun setupCopyButton() {
        binding.copyButton.setOnClickListener {
            val address = buildAddress()
            copyToClipboard(address)
            Toast.makeText(context, "Address copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildAddress(): String {
        val roadLane = binding.RoadLaneText.text.toString().split(",")
        val formattedRoadLane = if (roadLane.size == 2) {
            "Road ${roadLane[0].trim()}, Lane ${roadLane[1].trim()}"
        } else {
            binding.RoadLaneText.text.toString()
        }

        return StringBuilder().apply {
            append("House No:${binding.houseNoText.text},")
            append("$formattedRoadLane,")
            append("${binding.ColonyText.text} Colony,")
            append("${binding.LandmarkText.text},")
            append("${binding.CityText.text},")
            append("pincode:${binding.PinCodeText.text}")
        }.toString()
    }

    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("address", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun Initialization() {
        // Set charges
        binding.price.text = "₹3,899"
        // CarView for House Contents
        binding.bedroomNumber.text = "3"
        binding.bathroomNumber.text = "2"
        binding.kitchenNumber.text = "1"
        binding.floorNumber.text = "2"
        binding.balconyNumber.text = "2"
        binding.areaNumber.text = "1500"

        // Owner Profile
        binding.nameText.text = "Danile Foster"
        binding.descriptionText.text = "Housing Prime Center"

        // Negotiation
        binding.negotiateRent.text = "Negotiate Rent"

        // BHK and Property Type
        binding.BHKNumber.text = "3"
        binding.propertyType.text = "Apartment"

        // Property Location
        binding.houseNoText.text = "123"
        binding.RoadLaneText.text = "8,9"
        binding.ColonyText.text = "Green Park"
        binding.AreaText.text = "Downtown"
        binding.LandmarkText.text = "Near Park"
        binding.CityText.text = "Metropolis"
        binding.PinCodeText.text = "123456"

        // Built-Up Area, Furnishing, and Preferred Tenant
        binding.furnishingText.text = "Furnished"
        binding.BuiltUpAreaText.text = "1500"
        binding.PreferredTenantText.text = "Family, Bachelor, and Company"

        // Other Benefits
        binding.LiftIcon.setImageResource(R.drawable.ic_cross) // agar true hai to ic_tick else ic_cross
        binding.GeneratorIcon.setImageResource(R.drawable.ic_cross)
        binding.GasIcon.setImageResource(R.drawable.ic_tick)
        binding.SecurityGuardIcon.setImageResource(R.drawable.ic_tick)
        binding.ParkingIcon.setImageResource(R.drawable.ic_tick)

        // Owner Details
        binding.ownerName.text = "Surendra Kumar Panda"
        binding.tenantsServedNumber.text = "426"
        binding.postDateDay.text = "24"
        binding.postDateMonth.text = "June"
        binding.postDateYear.text = "2024"

        // Great Things About Property
        binding.GreatThingsText.text = "New flat, spacious room, cooperative society"
    }

    private fun gradientSweepTextColorAnimation() {
        val colors = intArrayOf(
            0xFFA0DAFB.toInt(),
            0xFF00ecbc.toInt(), // Green
            0xFF0A8ED9.toInt()  // Blue
        )

        val colorAnimator = ValueAnimator.ofArgb(*colors).apply {
            duration = 4000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener { animator ->
                _binding?.let {
                    val animatedValue = animator.animatedValue as Int
                    it.price.setTextColor(animatedValue)
                }
            }
        }

        colorAnimator.start()
    }

    private fun setupViewPager() {
        photoAdapter = MyViewPagerAdapter(imageResIds)
        binding.viewPager.adapter = photoAdapter
        binding.viewPager.setPageTransformer(getTransformation())
        binding.viewPager.offscreenPageLimit = 3
        handler.post(autoSlideRunnable)
    }

    private fun getTransformation(): CompositePageTransformer {
        return CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(30))
            addTransformer { page, position ->
                val alpha = 0.5f + 0.5f * (1 - abs(position))
                page.alpha = alpha
                page.scaleY = 0.85f + alpha * 0.15f

                val elevation = if (position == 0f) 5f else 0f
                page.translationZ = elevation

                val rotation = -20 * position
                page.rotation = rotation

                val depth = -120 * abs(position)
                page.cameraDistance = 8000f
                page.translationX = depth

                val fadeOut = if (position == 0f) 0f else 0.7f
                val fadeAlpha = 1 - fadeOut * abs(position)
                page.alpha = fadeAlpha

                val absPosition = abs(position)
                val bounceScale = if (absPosition > 1) 0.85f else (0.85f + (1 - absPosition) * 0.15f)
                page.scaleX = bounceScale
                page.scaleY = bounceScale

                if (page is ImageView) {
                    val saturation = 1 - 0.5f * abs(position)
                    val colorMatrix = ColorMatrix().apply { setSaturation(saturation) }
                    val filter = ColorMatrixColorFilter(colorMatrix)
                    page.colorFilter = filter
                }
            }
        }
    }

    private fun showViewChargesBottomSheet() {
        val bottomSheet = ViewChargesBottomSheet.newInstance()
        bottomSheet.show(childFragmentManager, ViewChargesBottomSheet.TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(autoSlideRunnable)
        _binding = null
    }
}
