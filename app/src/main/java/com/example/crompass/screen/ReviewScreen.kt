package com.example.crompass.screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.crompass.viewmodel.ReviewViewModel
import com.example.crompass.model.Review

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    navController: NavHostController,
    reviewViewModel: ReviewViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        reviewViewModel.fetchAllPublicReviews()
    }

    val reviews by reviewViewModel.reviews.collectAsState()
    val destinationNameMap by reviewViewModel.destinationNames.collectAsState()

    // Trigger fetching destination names after reviews load
    LaunchedEffect(reviews) {
        reviews.map { it.destinationId }.distinct().forEach { destinationId ->
            reviewViewModel.fetchDestinationName(destinationId)
        }
    }

    // Compute destination names, ensuring no duplicates and "All" is only at the start
    val destinationNames = remember(destinationNameMap) {
        val names = destinationNameMap.values.distinct().filter { it != "All" }
        listOf("All") + names
    }
    var selectedDestination by remember { mutableStateOf("All") }

    // Only filter reviews after destination names are loaded
    var filteredReviews by remember { mutableStateOf(reviews) }
    // Refetch filteredReviews only after destinationNameMap is available/changed
    LaunchedEffect(destinationNameMap, reviews, selectedDestination) {
        filteredReviews = if (selectedDestination == "All") {
            reviews
        } else {
            reviews.filter {
                destinationNameMap[it.destinationId] == selectedDestination
            }
        }
    }

    // Refetch filteredReviews only after destination names are available
    LaunchedEffect(destinationNameMap) {
        filteredReviews = if (selectedDestination == "All") {
            reviews
        } else {
            reviews.filter {
                destinationNameMap[it.destinationId] == selectedDestination
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp) // adjust this padding as needed
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    readOnly = true,
                    value = selectedDestination,
                    onValueChange = {},
                    label = { Text("Filter by destination") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All") },
                        onClick = {
                            selectedDestination = "All"
                            expanded = false
                        }
                    )
                    // Show only unique, non-"All" names
                    destinationNames.filter { it != "All" }.forEach { name ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                selectedDestination = name
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredReviews) { review ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Rating: ${review.rating} ★", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(review.reviewText)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Destination: ${destinationNameMap[review.destinationId] ?: review.destinationId}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}