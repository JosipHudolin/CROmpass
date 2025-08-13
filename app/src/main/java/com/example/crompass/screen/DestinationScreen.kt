package com.example.crompass.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.crompass.model.Destination
import com.example.crompass.viewmodel.DestinationViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.core.net.toUri
import com.example.crompass.model.Review
import com.example.crompass.viewmodel.ReviewViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.res.stringResource
import com.example.crompass.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.crompass.screen.components.Dropdown
import com.example.crompass.utils.LocalAppLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: DestinationViewModel = viewModel()
) {
    val reviewViewModel: ReviewViewModel = viewModel()
    val destinations by viewModel.filteredLocations.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val context = LocalContext.current

    val language = LocalAppLocale.current.currentLanguageCode

    var selectedDestination by remember { mutableStateOf<Destination?>(null) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var showReviewList by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState("")

    val allLocations by viewModel.allLocations.collectAsState()

    val allLabel = stringResource(R.string.all)
    val categoryMap = remember(allLocations, language, allLabel) {
        // key = DB category (eng), value = translated label for current app language
        val map = linkedMapOf<String, String>()
        map["All"] = allLabel
        allLocations
            .map { it.category to (it.categoryTranslations[language] ?: it.category) }
            .distinctBy { it.first }
            .forEach { (key, label) -> map[key] = label }
        map
    }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        TextField(
                            shape = RoundedCornerShape(20.dp),
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp, end = 10.dp),
                            label = { Text(stringResource(R.string.search_destinations)) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Category filter
            val translatedCategories = categoryMap.values.toList()
            val selectedCategoryTranslated = categoryMap[selectedCategory ?: "All"] ?: allLabel

            Dropdown(
                label = stringResource(R.string.select_category),
                options = translatedCategories,
                selectedOption = selectedCategoryTranslated,
                onOptionSelected = { selectedTranslatedCategory ->
                    val englishCategory = categoryMap.entries.firstOrNull { it.value == selectedTranslatedCategory }?.key ?: "All"
                    viewModel.updateCategory(englishCategory.takeIf { it != "All" })
                },
            )

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(LatLng(45.815399, 15.966568), 6f)
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                cameraPositionState = cameraPositionState,
                properties = com.google.maps.android.compose.MapProperties(isMyLocationEnabled = true),
                uiSettings = com.google.maps.android.compose.MapUiSettings(myLocationButtonEnabled = true),
                onMapClick = {
                    selectedDestination = null
                    showReviewDialog = false
                    showReviewList = false
                }
            ) {
                destinations.forEach { destination ->
                    val geoPoint = destination.location
                    if (geoPoint != null) {
                        val markerTitle = destination.nameTranslations?.get(language) ?: destination.name
                        val markerSnippet = destination.descriptionTranslations?.get(language) ?: destination.description
                        Marker(
                            state = MarkerState(position = LatLng(geoPoint.latitude, geoPoint.longitude)),
                            title = markerTitle,
                            snippet = markerSnippet,
                            onClick = {
                                selectedDestination = destination
                                false
                            }
                        )
                    }
                }
            }
            selectedDestination?.let { destination ->
                val displayName = destination.nameTranslations?.get(language) ?: destination.name
                val displayDescription = destination.descriptionTranslations?.get(language) ?: destination.description
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = displayName, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = displayDescription, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.navigate_with_google_maps),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable {
                                    val gmmIntentUri =
                                        "google.navigation:q=${destination.location.latitude},${destination.location.longitude}".toUri()
                                    val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                }
                                .padding(vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.leave_review),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable {
                                    showReviewDialog = true
                                }
                                .padding(vertical = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.check_reviews),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable {
                                    reviewViewModel.getPublicReviewsByDestination(destination.id)
                                    showReviewList = true
                                }
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }

            if (showReviewDialog && selectedDestination != null) {
                ReviewModal(
                    destination = selectedDestination!!,
                    onDismiss = { showReviewDialog = false },
                    onSubmit = { rating, text, isPublic ->
                        val review = Review(
                            destinationId = selectedDestination!!.id,
                            rating = rating,
                            reviewText = text,
                            public = isPublic,
                            userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                            timestamp = Timestamp.now()
                        )
                        reviewViewModel.submitReview(review)
                        showReviewDialog = false
                    }
                )
            }

            if (showReviewList) {
                val reviews by reviewViewModel.reviews.collectAsState(initial = emptyList())
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight(0.4f)
                        .padding(12.dp)
                ) {
                    items(reviews) { review ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(stringResource(R.string.rating) + ": ${review.rating} ★")
                                Text(review.reviewText ?: "")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewModal(
    destination: Destination,
    onDismiss: () -> Unit,
    onSubmit: (Int, String, Boolean) -> Unit
) {
    val ratingOptions = (1..5).toList()
    var selectedRating by remember { mutableStateOf(5) }
    var reviewText by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.leave_review_for, destination.name)) },
        text = {
            Column {
                Text(stringResource(R.string.rating))
                Row {
                    ratingOptions.forEach { star ->
                        Text(
                            text = "★",
                            color = if (star <= selectedRating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clickable { selectedRating = star }
                                .padding(end = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text(stringResource(R.string.your_review)) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    androidx.compose.material3.Checkbox(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it }
                    )
                    Text(stringResource(R.string.public_review))
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = {
                onSubmit(selectedRating, reviewText, isPublic)
            }) {
                Text(stringResource(R.string.submit))
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}