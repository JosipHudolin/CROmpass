package com.example.crompass.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.core.net.toUri
import com.example.crompass.model.Review
import com.example.crompass.viewmodel.ReviewViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

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

    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedDestination by remember { mutableStateOf<Destination?>(null) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var showReviewList by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        // Category filter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .clickable { isDropdownExpanded = true }
                .padding(12.dp)
        ) {
            Text(text = selectedCategory ?: "Select category")
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                val categories = listOf("All", "Nature", "History")
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            viewModel.updateCategory(if (category == "All") null else category)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // Request location permission
        LaunchedEffect(Unit) {
            // You should implement runtime permission request here if needed
            // For example, using Accompanist Permissions or your own logic
            // This is a placeholder for requesting location permission
        }

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
                    Marker(
                        state = MarkerState(position = LatLng(geoPoint.latitude, geoPoint.longitude)),
                        title = destination.name,
                        snippet = destination.description,
                        onClick = {
                            selectedDestination = destination
                            false
                        }
                    )
                }
            }
        }
        selectedDestination?.let { destination ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = destination.name, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = destination.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Navigate with Google Maps",
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
                        text = "Leave a Review",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable {
                                showReviewDialog = true
                            }
                            .padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Check Reviews",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable {
                                reviewViewModel.fetchPublicReviews(destination.id)
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
                        isPublic = isPublic,
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
                            Text("Rating: ${review.rating} ★")
                            Text(review.reviewText ?: "")
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
        title = { Text("Leave a Review for ${destination.name}") },
        text = {
            Column {
                Text("Rating")
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
                    label = { Text("Your review") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    androidx.compose.material3.Checkbox(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it }
                    )
                    Text("Public Review")
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = {
                onSubmit(selectedRating, reviewText, isPublic)
            }) {
                Text("Submit")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}