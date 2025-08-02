package com.example.crompass.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.crompass.viewmodel.ReviewViewModel
import com.example.crompass.model.Review
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import com.example.crompass.R
import com.example.crompass.screen.components.Dropdown
import com.example.crompass.screen.components.ReviewCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    navController: NavHostController,
    reviewViewModel: ReviewViewModel = viewModel()
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        reviewViewModel.getAllPublicReviews()
        userId?.let { reviewViewModel.getPrivateUserReviews(it) }
    }

    val reviews by reviewViewModel.reviews.collectAsState()
    val destinationNameMap by reviewViewModel.destinationNames.collectAsState()

    val userReviews by reviewViewModel.userReviews.observeAsState(emptyList())

    val isDestinationMapReady = destinationNameMap.isNotEmpty()

    // Trigger fetching destination names after reviews load
    LaunchedEffect(reviews) {
        reviews.map { it.destinationId }.distinct().forEach { destinationId ->
            reviewViewModel.getDestinationName(destinationId)
        }
    }
    LaunchedEffect(userReviews) {
        userReviews.map { it.destinationId }.distinct().forEach { destinationId ->
            reviewViewModel.getDestinationName(destinationId)
        }
    }

    // Compute destination names, ensuring no duplicates and "All" is only at the start
    val allLabel = stringResource(R.string.all)
    val destinationNames = remember(destinationNameMap) {
        val names = destinationNameMap.values.distinct().filter { it != "All" }
        listOf(allLabel) + names
    }
    var selectedDestination by remember(destinationNames) {
        mutableStateOf(destinationNames.firstOrNull() ?: "")
    }

    // Maintain separate filtered lists for public and private reviews
    var filteredPublicReviews by remember { mutableStateOf(emptyList<Review>()) }
    var filteredPrivateReviews by remember { mutableStateOf(emptyList<Review>()) }

    // Refetch filtered lists only after destinationNameMap is available/changed
    LaunchedEffect(destinationNameMap, reviews, selectedDestination, userReviews) {
        filteredPrivateReviews = if (selectedDestination == allLabel) {
            userReviews
        } else {
            userReviews.filter { destinationNameMap[it.destinationId] == selectedDestination }
        }

        filteredPublicReviews = if (selectedDestination == allLabel) {
            reviews.filter { it.public }
        } else {
            reviews.filter { it.public && destinationNameMap[it.destinationId] == selectedDestination }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0), // ⬅️ ovo makne status bar padding
                title = {
                    Text(
                        text = stringResource(R.string.reviews),
                        color = MaterialTheme.colorScheme.onPrimary)
                        },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Dropdown(
                label = stringResource(R.string.filter_by_destination),
                options = destinationNames,
                selectedOption = selectedDestination,
                onOptionSelected = { selectedDestination = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (filteredPrivateReviews.isNotEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.my_private_reviews),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(filteredPrivateReviews) { review ->
                        ReviewCard(review = review, destinationNameMap = destinationNameMap)
                    }
                }

                if (filteredPublicReviews.isNotEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.public_reviews),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(filteredPublicReviews) { review ->
                        ReviewCard(review = review, destinationNameMap = destinationNameMap)
                    }
                }
            }
        }
    }
}