package com.example.learnkotlin.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListPopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.learnkotlin.BuildConfig
import com.example.learnkotlin.R
import com.example.learnkotlin.core.extensions.dpToPx
import com.example.learnkotlin.core.extensions.setSafeOnClick
import com.example.learnkotlin.databinding.FragmentHomeBinding
import com.example.learnkotlin.databinding.LayoutBusStopInfoBinding
import com.example.learnkotlin.domain.base.Command
import com.example.learnkotlin.domain.base.customview.SearchInputView
import com.example.learnkotlin.domain.model.home.BusStop
import com.example.learnkotlin.domain.model.home.LocationSearch
import com.example.learnkotlin.domain.model.home.SearchLocation
import com.example.learnkotlin.presentation.base.BaseFragment
import com.example.learnkotlin.presentation.base.baseDropdown.BaseDropdownAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.style.layers.PropertyFactory.iconAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.iconIgnorePlacement
import org.maplibre.android.style.layers.PropertyFactory.iconImage
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import androidx.core.graphics.createBitmap

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val viewModel: HomeViewModel by viewModels()
    private lateinit var map: MapLibreMap

    private lateinit var popupCurrentLocation: ListPopupWindow
    private lateinit var adapterCurrentLocation: BaseDropdownAdapter<SearchLocation>

    private lateinit var popupDestination: ListPopupWindow
    private lateinit var adapterDestination: BaseDropdownAdapter<SearchLocation>

    private lateinit var fusedClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest

    private var lastGeocodeLocation: android.location.Location? = null

    private var selectedField: SelectedField = SelectedField.CURRENT

    private var selectedFeature: Feature? = null

    private val popupBinding by lazy {
        LayoutBusStopInfoBinding.bind(binding.layoutBusStopInfo.root)
    }

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation
            if (location == null) {
                sendCommand(HomeCommand.ClearCurrentLocation)
                hideGpsMarker()
                return
            }

            // cập nhật marker GPS
            showGpsMarker(
                location.latitude,
                location.longitude
            )

            // cập nhật địa chỉ
            if (shouldReverseGeocode(location.latitude, location.longitude)) {
                sendCommand(
                    HomeCommand.ReverseLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        field = SelectedField.CURRENT
                    )
                )
            }
        }
    }

    private companion object {

        // GPS hiện tại
        const val GPS_SOURCE = "gps_source"
        const val GPS_LAYER = "gps_layer"
        const val GPS_ICON = "gps_icon"

        // Bus Stops
        const val STOP_SOURCE = "stop_source"
        const val STOP_LAYER = "stop_layer"
        const val STOP_ICON = "stop_icon"

        const val GEOCODE_DISTANCE_METERS = 50f
    }

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            when {
                fineGranted || coarseGranted -> {
                    onLocationPermissionGranted()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    onLocationPermissionDenied()
                }

                else -> {
                    onLocationPermissionPermanentlyDenied()
                }
            }
        }

    private fun onLocationPermissionPermanentlyDenied() {
        hideGpsMarker()
        fusedClient.removeLocationUpdates(locationCallback)
        sendCommand(HomeCommand.ClearCurrentLocation)
    }

    private fun onLocationPermissionDenied() {
        hideGpsMarker()
        fusedClient.removeLocationUpdates(locationCallback)
        sendCommand(HomeCommand.ClearCurrentLocation)
    }

    private fun onLocationPermissionGranted() {
        getCurrentLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedClient.lastLocation.addOnSuccessListener { _ ->
            fusedClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun shouldReverseGeocode(
        latitude: Double,
        longitude: Double
    ): Boolean {
        val current = android.location.Location("gps").apply {
            this.latitude = latitude
            this.longitude = longitude
        }
        val last = lastGeocodeLocation
        if (last == null) {
            lastGeocodeLocation = current
            return true
        }
        if (last.distanceTo(current) >= GEOCODE_DISTANCE_METERS) {
            lastGeocodeLocation = current
            return true
        }
        return false
    }

    private fun requestLocationPermission() {
        if (hasLocationPermission()) {
            onLocationPermissionGranted()
            return
        }
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarse = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fine || coarse
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun onInit() {
        initData()
        initMap()
        initView()
        initPopup()
        initAction()
        observeState()
        viewModel.getBusStops()
    }

    private fun initData() {
        fusedClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest =
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                3000L
            )
                .setMinUpdateIntervalMillis(1000L)
                .build()
    }

    private fun initView() {
        binding.lnFindRoute.setEnabledWithAlpha(
            !viewModel.state.value.selectedCurrentLocation?.name.isNullOrBlank(),
            !viewModel.state.value.selectedDestination?.name.isNullOrBlank(),
        )

        popupBinding.ivClose.setSafeOnClick {
            binding.layoutBusStopInfo.root.isVisible = false
        }

        popupBinding.lnTrackBuses.setSafeOnClick {

        }

    }

    private fun observeState() {
        collectState<HomeState, String>(
            selector = { it.currentKeyword }
        ) { keyword ->
            binding.lnFindRoute.setEnabledWithAlpha(
                !viewModel.state.value.selectedCurrentLocation?.name.isNullOrBlank(),
                !viewModel.state.value.selectedDestination?.name.isNullOrBlank(),
            )
            if (binding.sbCurrentLocation.getText() != keyword) {
                binding.sbCurrentLocation.setText(
                    keyword,
                    notifyTextChanged = false
                )
            }
        }

        collectState<HomeState, String>(
            selector = { it.destinationKeyword }
        ) { keyword ->
            binding.lnFindRoute.setEnabledWithAlpha(
                !viewModel.state.value.selectedCurrentLocation?.name.isNullOrBlank(),
                !viewModel.state.value.selectedDestination?.name.isNullOrBlank(),
            )
            if (binding.sbDestination.getText() != keyword) {
                binding.sbDestination.setText(
                    keyword,
                    notifyTextChanged = false
                )
            }
        }

        collectState<HomeState, SearchLocation?>(
            selector = { it.selectedCurrentLocation }
        ) { location ->
            if (!::map.isInitialized) return@collectState
            if (location == null) {
                moveDefault()
                return@collectState
            }
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location.latitude ?: return@collectState,
                        location.longitude ?: return@collectState
                    ),
                    16.0
                )
            )
        }

        collectState<HomeState, SearchLocation?>(
            selector = { it.selectedDestination }
        ) { location ->
            if (!::map.isInitialized) return@collectState

            if (location == null) return@collectState

            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location.latitude ?: return@collectState,
                        location.longitude ?: return@collectState
                    ),
                    16.0
                )
            )
        }

        collectState<HomeState, List<BusStop>>(
            selector = { it.busStops }
        ) { busStops ->
            if (!::map.isInitialized) return@collectState
            updateBusStopMarkers(busStops)
        }

    }

    private fun updateBusStopMarkers(busStops: List<BusStop>) {
        val style = map.style ?: return
        if (busStops.isEmpty()) return

        val features = busStops.mapNotNull { stop ->
            val lat = stop.latitude ?: return@mapNotNull null
            val lon = stop.longitude ?: return@mapNotNull null
            Feature.fromGeometry(
                Point.fromLngLat(lon, lat)
            ).apply {
                addStringProperty("id", stop.gtfsId)
                addStringProperty("name", stop.name)
            }
        }
        style.getSourceAs<GeoJsonSource>(STOP_SOURCE)
            ?.setGeoJson(FeatureCollection.fromFeatures(features))

        // Tự động di chuyển camera đến vùng có các trạm xe buýt
        if (features.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            features.forEach { feature ->
                val point = feature.geometry() as Point
                builder.include(LatLng(point.latitude(), point.longitude()))
            }
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150))
        }
    }

    fun View.setEnabledWithAlpha(enabledCurrentLocation: Boolean, enableDestination: Boolean) {
        isEnabled = enabledCurrentLocation && enableDestination
        alpha = if (enabledCurrentLocation && enableDestination) 1f else 0.65f
    }

    private fun initAction() {
        bindSearchBar(
            binding.sbCurrentLocation,
            popupCurrentLocation,
            HomeCommand.ClearCurrentLocation, {
                HomeCommand.SearchKeyLocation(it)
            },
            SelectedField.CURRENT
        )

        bindSearchBar(
            binding.sbDestination,
            popupDestination,
            HomeCommand.ClearDestination,
            { HomeCommand.SearchKeyDestination(it) },
            SelectedField.DESTINATION
        )

        binding.lnFindRoute.setSafeOnClick {
           // sendCommand(HomeCommand.FindRoute)
            startActivity(
                clazz = SearchResultActivity::class.java,
                data = LocationSearch(
                    viewModel.state.value.selectedCurrentLocation,
                    viewModel.state.value.selectedDestination
                )
            )
        }

    }

    private fun bindSearchBar(
        searchBar: SearchInputView,
        popup: ListPopupWindow,
        clearCommand: Command,
        searchCommand: (String) -> Command,
        field: SelectedField
    ) {

        searchBar.setOnTextChangedListener { text ->
            if (text.isBlank()) {
                popup.dismiss()
                sendCommand(clearCommand)
            } else {
                sendCommand(searchCommand(text))
            }
        }

        searchBar.setOnFocusChangeListener { hasFocus ->
            if (hasFocus) {
                selectedField = field
            }
            if (!hasFocus) return@setOnFocusChangeListener
            val keyword = searchBar.getText()
            if (keyword.isBlank()) {
                popup.dismiss()
                sendCommand(clearCommand)
            } else {
                //sendCommand(searchCommand(keyword))
            }
        }
    }

    private fun initPopup() {
        adapterCurrentLocation = BaseDropdownAdapter(
            context = requireContext(),
            items = mutableListOf(),
            selected = null,
            textProvider = { getTextCurrentLocation(it) }
        )
        popupCurrentLocation = createPopup(
            binding.viewAnchorCurrentLocation,
            adapterCurrentLocation
        ) {

            sendCommand(
                HomeCommand.SelectCurrentLocation(
                    it.name.orEmpty(),
                    it
                )
            )
        }
        adapterDestination = BaseDropdownAdapter(
            context = requireContext(),
            items = mutableListOf(),
            selected = null,
            textProvider = { getTextCurrentLocation(it) }
        )
        popupDestination = createPopup(
            binding.viewAnchorDestination,
            adapterDestination
        ) {

            sendCommand(
                HomeCommand.SelectDestination(
                    it.name.orEmpty(),
                    it
                )
            )
        }
    }

    private fun createPopup(
        anchor: View,
        adapter: BaseDropdownAdapter<SearchLocation>,
        onItemClick: (SearchLocation) -> Unit
    ): ListPopupWindow {
        return ListPopupWindow(requireContext()).apply {
            anchorView = anchor
            width = anchor.width
            height = 200.dpToPx(requireContext())
            isModal = false
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                adapter.getItem(position)?.let(onItemClick)
                dismiss()
            }
        }
    }

    private fun initMap() {
        binding.mapView.onCreate(null)
        binding.mapView.getMapAsync {
            map = it
            loadMapStyle()
        }
    }

    private fun loadMapStyle() {
        val styleUrl =
            "https://api.maptiler.com/maps/streets-v2/style.json?key=${BuildConfig.MAPTILER_API_KEY}"
        map.setStyle(styleUrl) {
            initMarkerLayers()
            if (hasLocationPermission()) {
                onLocationPermissionGranted()
            } else {
                moveDefault()
                requestLocationPermission()
            }
            updateBusStopMarkers(viewModel.state.value.busStops)
        }
        map.addOnMapClickListener { point ->
            val screenPoint = map.projection.toScreenLocation(point)
            val features = map.queryRenderedFeatures(screenPoint, STOP_LAYER)
            if (features.isNotEmpty()) {
                val feature = features[0]
                selectedFeature = feature
                showBusStopPopup(feature)
                return@addOnMapClickListener true
            }

            sendCommand(
                HomeCommand.ReverseLocation(
                    latitude = point.latitude,
                    longitude = point.longitude,
                    field = selectedField
                )
            )
            true
        }

        map.addOnCameraIdleListener {
            selectedFeature?.let(::showBusStopPopup)
        }
    }

    private fun showBusStopPopup(feature: Feature) {

        val point = feature.geometry() as Point

        val latLng = LatLng(
            point.latitude(),
            point.longitude()
        )

        val screenPoint = map.projection.toScreenLocation(latLng)

        showPopupAt(
            screenPoint.x.toFloat(),
            screenPoint.y.toFloat(),
            feature
        )
    }

    private fun showPopupAt(
        x: Float,
        y: Float,
        feature: Feature
    ) {
        val popup = binding.layoutBusStopInfo.root
        LayoutBusStopInfoBinding.bind(popup).apply {
            tvStopName.text = feature.getStringProperty("name")
        }
        popup.isVisible = true

        if (popup.width == 0) {
            popup.post {
                updatePopupPosition(x, y)
            }
        } else {
            updatePopupPosition(x, y)
        }
    }

    private fun updatePopupPosition(
        x: Float,
        y: Float
    ) {
        val popup = binding.layoutBusStopInfo.root

        popup.translationX = x - popup.width / 2f
        popup.translationY = y - popup.height - 16.dpToPx(requireContext())
    }

    private fun initMarkerLayers() {
        val style = map.style ?: return
        
        // GPS Layer
        if (style.getSource(GPS_SOURCE) == null) {
            getBitmapFromVectorDrawable(requireContext(), R.drawable.ic_my_location)?.let {
                style.addImage(GPS_ICON, it)
            }
            style.addSource(
                GeoJsonSource(
                    GPS_SOURCE,
                    FeatureCollection.fromFeatures(emptyArray())
                )
            )

            style.addLayer(
                SymbolLayer(
                    GPS_LAYER,
                    GPS_SOURCE
                ).withProperties(
                    iconImage(GPS_ICON),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true)
                )
            )
        }

        // Bus Stop Layer
        if (style.getSource(STOP_SOURCE) == null) {
            // Sử dụng ic_route_42 cho marker trạm xe buýt để dễ nhìn hơn
            getBitmapFromVectorDrawable(requireContext(), R.drawable.ic_stop_marker)?.let {
                style.addImage(STOP_ICON, it)
            }
            style.addSource(
                GeoJsonSource(
                    STOP_SOURCE,
                    FeatureCollection.fromFeatures(emptyArray())
                )
            )
            style.addLayer(
                SymbolLayer(
                    STOP_LAYER,
                    STOP_SOURCE
                ).withProperties(
                    iconImage(STOP_ICON),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true)
                )
            )
        }
    }

    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun showGpsMarker(
        latitude: Double,
        longitude: Double
    ) {
        val style = map.style ?: return
        style.getSourceAs<GeoJsonSource>(GPS_SOURCE)
            ?.setGeoJson(
                Feature.fromGeometry(
                    Point.fromLngLat(
                        longitude,
                        latitude
                    )
                )
            )
    }

    private fun hideGpsMarker() {
        val style = map.style ?: return
        style.getSourceAs<GeoJsonSource>(GPS_SOURCE)
            ?.setGeoJson(
                FeatureCollection.fromFeatures(emptyArray())
            )
    }

    private fun moveDefault() {
        val location = LatLng(
            21.0285,
            105.8542
        )
        val style = map.style ?: return
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                location,
                16.0
            )
        )
    }

    override fun handleEvent(event: Any) {
        when (event) {
            is HomeEvent.SearchLocationSuccess -> {
                showPopupLocationSuggest(
                    popupCurrentLocation,
                    adapterCurrentLocation,
                    binding.viewAnchorCurrentLocation,
                    viewModel.state.value.selectedCurrentLocation,
                    event.data
                )
            }

            is HomeEvent.SearchDestinationSuccess -> {
                showPopupLocationSuggest(
                    popupDestination,
                    adapterDestination,
                    binding.viewAnchorDestination,
                    viewModel.state.value.selectedDestination,
                    event.data
                )
            }

            is HomeEvent.GetSuggestRoutesSuccess -> {
                startActivity(
                    clazz = SearchResultActivity::class.java,
                    data = event.data
                )
            }

            else -> { /* To do*/
            }
        }
    }

    private fun showPopupLocationSuggest(
        popup: ListPopupWindow,
        adapter: BaseDropdownAdapter<SearchLocation>,
        anchor: View,
        selected: SearchLocation?,
        data: List<SearchLocation>?
    ) {

        adapter.submitList(data.orEmpty())
        adapter.setSelected(selected)

        if (data.isNullOrEmpty()) {
            popup.dismiss()
            return
        }

        if (!popup.isShowing) {
            popup.width = anchor.width
            popup.show()
        }
    }

    private fun getTextCurrentLocation(location: SearchLocation?): String {
        return location?.name.orEmpty()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        if (::map.isInitialized && hasLocationPermission()) {
            getCurrentLocation()
        }
    }

    override fun onPause() {
        fusedClient.removeLocationUpdates(locationCallback)
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.mapView.onStop()
        super.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        fusedClient.removeLocationUpdates(locationCallback)
        binding.mapView.onDestroy()
        super.onDestroyView()

    }

}