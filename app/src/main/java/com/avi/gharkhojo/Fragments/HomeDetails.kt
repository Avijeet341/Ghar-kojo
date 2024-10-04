package com.avi.gharkhojo.Fragments

import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.AnimatedStateListDrawable
import android.net.Uri
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.avi.gharkhojo.Adapter.MyViewPagerAdapter
import com.avi.gharkhojo.Chat.ChatRoom
import com.avi.gharkhojo.Fragments.HomeDetailsDirections.Companion.actionHomeDetailsToTabLayoutFragment
import com.avi.gharkhojo.MainActivity
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentHomeDetailsBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class HomeDetails : Fragment() {

    private var _binding: FragmentHomeDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookMark: ImageButton
    private lateinit var shareButton: ImageButton
    private lateinit var chatBtn: ImageButton
    private lateinit var backButton: ImageButton
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

    private var post: Post? = null
    private lateinit var photoAdapter: MyViewPagerAdapter

    private val handler = Handler(Looper.getMainLooper())
    private val autoSlideRunnable = object : Runnable {
        override fun run() {
            val currentItem = binding.viewPager.currentItem
            val nextItem = (currentItem + 1) % post?.imageList?.map { it.value }?.flatten()?.size!!
            binding.viewPager.setCurrentItem(nextItem, true)
            handler.postDelayed(this, 3000)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeDetailsBinding.inflate(inflater, container, false)
        Initialization()
        setupViewPager()
        setupCopyButton()
        setupBookmarkButton()
        post = arguments?.getParcelable("post")!!
        loadData()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData() {
        photoAdapter.updateData(post?.imageList?.map { it.value }?.flatten() as ArrayList<String>)
        bedroomNumber.text =(post?.noOfBedRoom?:0).toString()
        bathroomNumber.text =(post?.noOfBathroom?:0).toString()
        kitchenNumber.text =(post?.noOfKitchen?:0).toString()
        floorNumber.text =(post?.floorPosition?:0).toString()
        balconyNumber.text =(post?.noOfBalcony?:0).toString()
        areaNumber.text = (post?.builtUpArea?:0).toString()
        nameText.text = post?.ownerName
        //descriptionText.text = post?.description
        price.text = post?.rent
        BHKNumber.text = "${post?.noOfBedRoom!!+post?.noOfBathroom!!+post?.noOfKitchen!!+1}"
        propertyType.text = post?.propertyType
        ownerName.text = post?.ownerName
        tenantsServedNumber.text = post?.tenantServed.toString()
        postDateDay.text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            .format(Date.from(Instant.ofEpochMilli(post?.postTime?.toLong()?:0)))

        houseNoText.text = post?.houseNumber.toString()
        RoadLaneText.text = post?.road_lane.toString()
        ColonyText.text = post?.colony.toString()
        AreaText.text = post?.area.toString()
        LandmarkText.text = post?.landMark.toString()
        CityText.text = post?.city.toString()
        binding.StateText.text = post?.state
        PinCodeText.text = post?.pincode.toString()
        furnishingText.text = post?.furnished
        BuiltUpAreaText.text = post?.builtUpArea
        PreferredTenantText.text = post?.preferredTenants

       LiftIcon.setImageResource(if (post?.hasLift == true) R.drawable.ic_tick else R.drawable.ic_cross)
        GeneratorIcon.setImageResource(if (post?.hasGenerator == true) R.drawable.ic_tick else R.drawable.ic_cross)
        GasIcon.setImageResource(if (post?.hasGasService == true) R.drawable.ic_tick else R.drawable.ic_cross)
        SecurityGuardIcon.setImageResource(if (post?.hasSecurityGuard == true) R.drawable.ic_tick else R.drawable.ic_cross)
        ParkingIcon.setImageResource(if (post?.hasParking == true) R.drawable.ic_tick else R.drawable.ic_cross)

        Glide.with(requireContext()).load(post?.ownerImage)
            .placeholder(R.drawable.vk)
            .into(binding.profileImage)

        binding.mapButton.setOnClickListener{
            navigateToGoogleMaps(post?.latitude?:0.0,post?.longitude?:0.0)
        }

        chatBtn.setOnClickListener{

            openChatRoom()
        }
        messageButton.setOnClickListener{
            openChatRoom()
        }

        callButton.setOnClickListener{
            makeCall(post?.phoneNumber?:"")
        }



    }

    private fun openChatRoom() {
        if(post?.userId!= FirebaseAuth.getInstance().currentUser?.uid) {
            var intent: Intent = Intent(context, ChatRoom::class.java)
            intent.putExtra(ChatRoom.IMG_ARG, post?.ownerImage)
            intent.putExtra(ChatRoom.NAME_ARG, post?.ownerName)
            intent.putExtra(ChatRoom.UID_ARG, post?.userId)
            context?.startActivity(intent)
        }else{
            Toast.makeText(context, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post {
            gradientSweepTextColorAnimation()
        }

        binding.viewCharges.setOnClickListener {
            showViewChargesBottomSheet()
        }

    }

    private fun Initialization() {

        //ToolBar
        bookMark = binding.bookMarkButton
        shareButton = binding.shareButton
        chatBtn = binding.chatBtn
        backButton = binding.backButton


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
        feedbackButton=binding.feedbackButton

        // Great Things About Property
        GreatThingsText = binding.GreatThingsText
    }
    private fun hideBottomNavBar() {
        (activity as? MainActivity)?.hideBottomNavBar()
    }
    private fun showBottomNavBar() {
        (activity as? MainActivity)?.showBottomNavBar()
    }

    private fun setupBookmarkButton() {
        var isBookmarked = false
        val unbookmarkedColor = ContextCompat.getColor(requireContext(), R.color.bookmark_unbookmarked)
        val bookmarkedColor = ContextCompat.getColor(requireContext(), R.color.bookmark_bookmarked)


        bookMark.setImageResource(R.drawable.bookmark_animation)
        bookMark.setColorFilter(unbookmarkedColor)

        bookMark.setOnClickListener {
            isBookmarked = !isBookmarked

            if (isBookmarked) {
                // Bookmark
                bookMark.setColorFilter(bookmarkedColor)
                Toast.makeText(context, "Bookmarked", Toast.LENGTH_SHORT).show()
            } else {
                // Unbookmark
                bookMark.setColorFilter(unbookmarkedColor)
                bookMark.setImageResource(R.drawable.bookmark_animation)
                Toast.makeText(context, "Bookmark Removed", Toast.LENGTH_SHORT).show()
            }


            (bookMark.drawable as? AnimatedStateListDrawable)?.let { drawable ->
                drawable.setState(if (isBookmarked) intArrayOf(android.R.attr.state_checked) else intArrayOf())
            }
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



    private fun gradientSweepTextColorAnimation() {
        val colors = intArrayOf(
            0xFF4285F4.toInt(),
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
        photoAdapter = MyViewPagerAdapter {
            navigateToTabLayoutFragment()
        }
        binding.viewPager.adapter = photoAdapter
        binding.viewPager.setPageTransformer(getTransformation())
        binding.viewPager.offscreenPageLimit = 3
        handler.post(autoSlideRunnable)
    }

    private fun navigateToTabLayoutFragment() {
            val bundle = Bundle()
        bundle.putParcelable("post", post)
        findNavController().navigate(R.id.action_homeDetails_to_tabLayoutFragment, bundle)
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
        val bottomSheet = ViewChargesUserBottomSheetFragment.newInstance()
        bottomSheet.arguments = Bundle().apply {
            putString("rent", binding.price.text.toString())
            putString("parkingCharge", if (post?.isParkingChargeIncluded == true) "0" else post?.parkingCharge)
            putString("maintenanceCharge", post?.maintenanceCharge)
            putString("deposit", post?.deposit)
        }
        bottomSheet.show(childFragmentManager, ViewChargesUserBottomSheetFragment.TAG)
    }

    private fun navigateToGoogleMaps(latitude: Double, longitude: Double) {
        val geoUri = "geo:$latitude,$longitude"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
        intent.setPackage("com.google.android.apps.maps") // Ensure it opens in Google Maps if installed

        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Google Maps is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun makeCall(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CALL_PHONE), 1)
        } else {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //showBottomNavBar()
        handler.removeCallbacks(autoSlideRunnable)
        _binding = null
    }
}
