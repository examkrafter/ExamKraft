package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttemptEntity
import com.example.data.model.TestEntity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldOnContainer
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RedError
import com.example.ui.viewmodel.QuotaUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    tests: List<TestEntity>,
    attempts: List<AttemptEntity>,
    quotaState: QuotaUiState,
    onOpenUploadDialog: () -> Unit,
    onOpenProDialog: () -> Unit,
    onStartTest: (testId: Long) -> Unit,
    onViewAttempt: (AttemptEntity) -> Unit,
    onViewAllAnalytics: () -> Unit,
    onDeleteTest: (testId: Long) -> Unit
) {
    val context = LocalContext.current
    val totalAttempts = attempts.size
    val totalQuestions = attempts.sumOf { it.totalQuestions }
    val totalScore = attempts.sumOf { it.score }
    val avgScorePct = if (totalQuestions > 0) (totalScore * 100) / totalQuestions else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ExamKraft",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (quotaState.isPro) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(GoldContainer)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "PRO",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldOnContainer
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Bankers Community Test Portal",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // WhatsApp Support Action
                    IconButton(
                        onClick = {
                            val url = "https://wa.me/919512808883?text=Hi%20ExamKraft%20Support%2C%20I%20have%20a%20query%20about%20banking%20tests."
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "WhatsApp Support",
                            tint = GreenSuccess
                        )
                    }

                    // Pro Upgrade Button if not Pro
                    if (!quotaState.isPro) {
                        OutlinedButton(
                            onClick = onOpenProDialog,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("₹499 Pro", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenUploadDialog,
                containerColor = NavyPrimary,
                contentColor = Color.White,
                icon = { Icon(imageVector = Icons.Default.UploadFile, contentDescription = null) },
                text = { Text("Upload PDF Test", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Quota Card
                if (quotaState.isPro) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = GoldContainer),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(GoldAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ExamKraft Pro Member",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldOnContainer
                                )
                                Text(
                                    text = "Unlimited daily AI question generations & priority support",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoldOnContainer.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Free Tier: 20 Questions / Day",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${quotaState.remainingToday} questions remaining today",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (quotaState.remainingToday > 5) GreenSuccess else RedError,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Button(
                                    onClick = onOpenProDialog,
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = ButtonDefaults.TextButtonContentPadding
                                ) {
                                    Icon(imageVector = Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Upgrade ₹499", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val progress = (quotaState.questionsGeneratedToday.toFloat() / quotaState.dailyLimit.toFloat()).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = NavyPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            // Hero Upload Action Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenUploadDialog() },
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Create Test From PDF",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Upload banking circulars, notes, or regulation PDFs to generate custom MCQs instantly.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Start Generation →",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // Analytics Metric Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "YOUR PERFORMANCE SUMMARY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    if (totalAttempts > 0) {
                        TextButton(onClick = onViewAllAnalytics) {
                            Text("Full Analytics", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Papers",
                        value = "$totalAttempts",
                        subtitle = "Attempted"
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Avg Accuracy",
                        value = "$avgScorePct%",
                        subtitle = "Overall Score"
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Solved",
                        value = "$totalQuestions",
                        subtitle = "Questions"
                    )
                }
            }

            // Available Banking Tests
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AVAILABLE BANKING TESTS (${tests.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            if (tests.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No tests generated yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(tests) { test ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NavyPrimary.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = test.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${test.questionCount} Questions",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = " • ${test.difficulty}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldAccent,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = " • ${test.targetDurationMinutes} mins",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(onClick = { onDeleteTest(test.id) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Test",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Button(
                                onClick = { onStartTest(test.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = ButtonDefaults.TextButtonContentPadding
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Start", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // WhatsApp Support Footer Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GreenContainer),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = GreenSuccess,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Bankers WhatsApp Support",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenSuccess
                                )
                                Text(
                                    text = "Helpline: 9512808883 (Queries & Pro Activation)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GreenSuccess.copy(alpha = 0.85f)
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = {
                                val url = "https://wa.me/919512808883?text=Hi%20ExamKraft%20Support"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = ButtonDefaults.TextButtonContentPadding
                        ) {
                            Text("Chat", color = GreenSuccess, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = NavyPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
