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
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.avi.gharkhojo.Adapter.MyViewPagerAdapter
import com.avi.gharkhojo.Adapter.MyViewPagerAdapter2
import com.avi.gharkhojo.MainActivity
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentHomeDetailsBinding
import kotlin.math.abs

class HomeDetails : Fragment() {

    private var _binding: FragmentHomeDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPager: ViewPager2
    private lateinit var price: TextView
    private lateinit var viewCharges: TextView
    private lateinit var bedroomNumber: TextView
    private lateinit var bathroomNumber: TextView
    private lateinit var kitchenNumber: TextView
    private lateinit var floorNumber: TextView
    private lateinit var balconyNumber: TextView
    private lateinit var areaNumber: TextView
    private lateinit var nameText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var messageButton:FrameLayout
    private lateinit var callButton:FrameLayout
    private lateinit var negotiateRent: TextView
    private lateinit var BHKNumber: TextView
    private lateinit var propertyType: TextView
    private lateinit var houseNoText: TextView
    private lateinit var RoadLaneText: TextView
    private lateinit var ColonyText: TextView
    private lateinit var AreaText: TextView
    private lateinit var LandmarkText: TextView
    private lateinit var CityText: TextView
    private lateinit var PinCodeText: TextView
    private lateinit var copyButton: ImageButton
    private lateinit var furnishingText: TextView
    private lateinit var BuiltUpAreaText: TextView
    private lateinit var PreferredTenantText: TextView
    private lateinit var LiftIcon: ImageView
    private lateinit var GeneratorIcon: ImageView
    private lateinit var GasIcon: ImageView
    private lateinit var SecurityGuardIcon: ImageView
    private lateinit var ParkingIcon: ImageView
    private lateinit var ownerName: TextView
    private lateinit var tenantsServedNumber: TextView
    private lateinit var postDateDay: TextView
    private lateinit var postDateMonth: TextView
    private lateinit var postDateYear: TextView
    private lateinit var feedbackButton: Button
    private lateinit var GreatThingsText: TextView

    private lateinit var photoAdapter: MyViewPagerAdapter2
    private val imageResIds = listOf(
        R.drawable.home1,
        R.drawable.home2,
        R.drawable.home3,
        R.drawable.home4,
        R.drawable.home5,
        R.drawable.home6,
        R.drawable.home7,
        R.drawable.home8,
        R.drawable.home9,
        R.drawable.home10,
        R.drawable.home11,
        R.drawable.home12,
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
       // hideBottomNavBar()
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
    private fun hideBottomNavBar() {
        (activity as? MainActivity)?.hideBottomNavBar()
    }
    private fun showBottomNavBar() {
        (activity as? MainActivity)?.showBottomNavBar()
    }



    private fun setupCopyButton() {
        binding.copyButton.setOnClickListener {
            val address = buildAddress()
            copyToClipboard(address)
            Toast.makeText(context, "Address copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildAddress(): String {
        return StringBuilder().apply {
            append("House No:${binding.houseNoText.text},")
            append("Road&Lane No:${binding.RoadLaneText.text},")
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
        // ViewPager for Images
        viewPager=binding.viewPager

        // Set charges
        price = binding.price
        viewCharges=binding.viewCharges

        // CarView for House Contents
        bedroomNumber = binding.bedroomNumber
        bathroomNumber = binding.bathroomNumber
        kitchenNumber = binding.kitchenNumber
        floorNumber = binding.floorNumber
        balconyNumber = binding.balconyNumber
        areaNumber = binding.areaNumber

        // Owner Profile
        nameText = binding.nameText
        descriptionText = binding.descriptionText
        messageButton = binding.messageButton
        callButton = binding.callButton

        // Negotiation
        negotiateRent = binding.negotiateRent

        // BHK and Property Type
        BHKNumber = binding.BHKNumber
        propertyType = binding.propertyType

        // Property Location
        houseNoText = binding.houseNoText
        RoadLaneText = binding.RoadLaneText
        ColonyText = binding.ColonyText
        AreaText = binding.AreaText
        LandmarkText = binding.LandmarkText
        CityText = binding.CityText
        PinCodeText = binding.PinCodeText
        copyButton=binding.copyButton

        // Built-Up Area, Furnishing, and Preferred Tenant
        furnishingText = binding.furnishingText
        BuiltUpAreaText = binding.BuiltUpAreaText
        PreferredTenantText = binding.PreferredTenantText

        // Other Benefits
        LiftIcon = binding.LiftIcon // agar true hai to ic_tick else ic_cross
        GeneratorIcon = binding.GeneratorIcon
        GasIcon = binding.GasIcon
        SecurityGuardIcon = binding.SecurityGuardIcon
        ParkingIcon = binding.ParkingIcon

        // Owner Details
        ownerName = binding.ownerName
        tenantsServedNumber = binding.tenantsServedNumber
        postDateDay = binding.postDateDay
        postDateMonth = binding.postDateMonth
        postDateYear = binding.postDateYear
        feedbackButton=binding.feedbackButton

        // Great Things About Property
        GreatThingsText = binding.GreatThingsText
    }

    private fun gradientSweepTextColorAnimation() {
        val colors = intArrayOf(
            0xFFA0DAFE.toInt(),
            0xFF00ecbc.toInt(),
            0xFF007FFF.toInt()
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
        photoAdapter = MyViewPagerAdapter2(imageResIds) {
            navigateToTabLayoutFragment()
        }
        binding.viewPager.adapter = photoAdapter
        binding.viewPager.setPageTransformer(getTransformation())
        binding.viewPager.offscreenPageLimit = 3
        handler.post(autoSlideRunnable)
    }

    private fun navigateToTabLayoutFragment() {
        findNavController().navigate(R.id.action_homeDetails_to_tabLayoutFragment)
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
        //showBottomNavBar()
        handler.removeCallbacks(autoSlideRunnable)
        _binding = null
    }
}
