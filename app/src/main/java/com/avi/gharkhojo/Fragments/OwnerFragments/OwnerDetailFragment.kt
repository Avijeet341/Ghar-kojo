package com.avi.gharkhojo.Fragments.OwnerFragments

import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.avi.gharkhojo.Adapter.MyViewPagerAdapter
import com.avi.gharkhojo.Fragments.ViewChargesBottomSheet
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.Model.Upload
import com.avi.gharkhojo.OwnerActivity
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentOwnerDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class OwnerDetailFragment : Fragment() {

    private var _binding: FragmentOwnerDetailBinding? = null
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

    private lateinit var GreatThingsText: TextView

    private lateinit var photoAdapter: MyViewPagerAdapter
    private var databaseReference: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("Posts").child(
        FirebaseAuth.getInstance().currentUser!!.uid)
    private val imageResIds = ArrayList<String>()
    lateinit var post:Post
    private val handler = Handler(Looper.getMainLooper())
    private val autoSlideRunnable = object : Runnable {
        override fun run() {
            val currentItem = binding.viewPager.currentItem
            val nextItem = (currentItem + 1) % if(imageResIds.size==0) 1 else imageResIds.size
            binding.viewPager.setCurrentItem(nextItem, true)
            handler.postDelayed(this, 3000)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnerDetailBinding.inflate(inflater, container, false)
        post = arguments?.getParcelable("post")!!
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavBar()
        Initialization()
        setupViewPager()
        setupCopyButton()
        setupData()

        view.post {
            gradientSweepTextColorAnimation()
        }

        binding.viewCharges.setOnClickListener {
            showViewChargesBottomSheet()
        }

    }

    private fun setupData() {
        price.text = post.rent
        bedroomNumber.text = post.noOfBedRoom.toString()
        bathroomNumber.text = post.noOfBathroom.toString()
        kitchenNumber.text = post.noOfKitchen.toString()
        floorNumber.text = post.floorPosition
        balconyNumber.text = post.noOfBalcony.toString()
        areaNumber.text = "${post.builtUpArea}"

        BHKNumber.text = "${(post.noOfBedRoom?.plus(post.noOfKitchen!!) ?: 0) + 1}"
        propertyType.text = post.propertyType
        houseNoText.text = post.houseNumber.toString()
        RoadLaneText.text = post.road_lane.toString()
        ColonyText.text = post.colony.toString()
        AreaText.text = post.area.toString()
        LandmarkText.text = post.landMark.toString()
        CityText.text = post.city.toString()
        PinCodeText.text = post.pincode.toString()
        furnishingText.text = post.furnished.toString()
        BuiltUpAreaText.text = post.builtUpArea.toString()
        PreferredTenantText.text = post.preferredTenants.toString()
        LiftIcon.setImageResource(if (post.hasLift == true) R.drawable.ic_tick else R.drawable.ic_cross)
        GeneratorIcon.setImageResource(if (post.hasGenerator == true) R.drawable.ic_tick else R.drawable.ic_cross)
        GasIcon.setImageResource(if (post.hasGasService == true) R.drawable.ic_tick else R.drawable.ic_cross)
        SecurityGuardIcon.setImageResource(if (post.hasSecurityGuard == true) R.drawable.ic_tick else R.drawable.ic_cross)
        ParkingIcon.setImageResource(if (post.hasParking == true) R.drawable.ic_tick else R.drawable.ic_cross)
        ownerName.text = post.ownerName
        tenantsServedNumber.text = post.tenantServed
        postDateDay.text = getDateTime(post.postTime)
        GreatThingsText.text = post.description.toString()
        binding.stateText.text = post.state.toString()
    }

    private fun getDateTime(postTime: String?): CharSequence? {


        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateTime = Date(postTime!!.toLong())
        return sdf.format(dateTime)

    }

    private fun hideBottomNavBar() {
        (activity as? OwnerActivity)?.hideBottomNavBar()
    }

    private fun showBottomNavBar() {
        (activity as? OwnerActivity)?.showBottomNavBar()
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
            append("state:${binding.stateText.text}")
        }.toString()
    }

    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("address", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun Initialization() {

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
        LiftIcon = binding.liftIcon // agar true hai to ic_tick else ic_cross
        GeneratorIcon = binding.GeneratorIcon
        GasIcon = binding.GasIcon
        SecurityGuardIcon = binding.SecurityGuardIcon
        ParkingIcon = binding.ParkingIcon

        // Owner Details
        ownerName = binding.ownerName
        tenantsServedNumber = binding.tenantsServedNumber
        postDateDay = binding.postTime



        GreatThingsText = binding.GreatThingsText
    }

    private fun gradientSweepTextColorAnimation() {
        val colors = intArrayOf(
            0xFFFFEB3B.toInt(),
            0xFFFDD835.toInt(),
            0xFFF9A825.toInt()  // Blue
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setupViewPager() {

        photoAdapter = MyViewPagerAdapter()


        post.imageList.values.forEach {
            imageResIds.addAll(it)
        }

        photoAdapter.updateData(imageResIds)
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
        val bottomSheetFragment = ViewChargesBottomSheet()
       val bundle = Bundle()
        bundle.putString("rent",post.rent)
        bundle.putString("deposit",post.deposit)
        bundle.putString("parkingCharge",post.parkingCharge)
        bundle.putString("maintenanceCharge",post.maintenanceCharge)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacks(autoSlideRunnable)
        showBottomNavBar()
    }
}
