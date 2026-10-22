package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttemptEntity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RedError
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    attempts: List<AttemptEntity>,
    onSelectAttempt: (AttemptEntity) -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val totalAttempts = attempts.size
    val totalQuestions = attempts.sumOf { it.totalQuestions }
    val totalScore = attempts.sumOf { it.score }
    val averageScorePct = if (totalQuestions > 0) (totalScore * 100) / totalQuestions else 0
    val highestScorePct = attempts.maxOfOrNull {
        if (it.totalQuestions > 0) (it.score * 100) / it.totalQuestions else 0
    } ?: 0

    val highAccuracyCount = attempts.count { (it.score * 100) / it.totalQuestions >= 80 }
    val mediumAccuracyCount = attempts.count {
        val pct = (it.score * 100) / it.totalQuestions
        pct in 50..79
    }
    val lowAccuracyCount = attempts.count { (it.score * 100) / it.totalQuestions < 50 }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Exam Performance Analytics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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

                // Summary Dashboard Grid
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "BANKING MASTERY INDEX",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$averageScorePct%",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Average Score Across Papers",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Papers Attempted", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                                Text("$totalAttempts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Questions Solved", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                                Text("$totalQuestions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Peak Score", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                                Text("$highestScorePct%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GoldAccent)
                            }
                        }
                    }
                }
            }

            // Accuracy Distribution
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Score Accuracy Distribution",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        AccuracyBarRow(
                            label = "Mastery (>= 80%)",
                            count = highAccuracyCount,
                            total = totalAttempts,
                            color = GreenSuccess
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AccuracyBarRow(
                            label = "Average (50% - 79%)",
                            count = mediumAccuracyCount,
                            total = totalAttempts,
                            color = GoldAccent
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AccuracyBarRow(
                            label = "Needs Focus (< 50%)",
                            count = lowAccuracyCount,
                            total = totalAttempts,
                            color = RedError
                        )
                    }
                }
            }

            // Attempt History List
            item {
                Text(
                    text = "ALL ATTEMPTED PAPERS (${attempts.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            if (attempts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No papers attempted yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Take your first test from the Dashboard to see detailed analytics.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                items(attempts) { attempt ->
                    val pct = if (attempt.totalQuestions > 0) (attempt.score * 100) / attempt.totalQuestions else 0
                    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    val dateFormatted = sdf.format(Date(attempt.attemptedAt))

                    val scoreColor = when {
                        pct >= 80 -> GreenSuccess
                        pct >= 50 -> GoldAccent
                        else -> RedError
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectAttempt(attempt) },
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
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(scoreColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$pct%",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = scoreColor
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = attempt.testTitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${attempt.score}/${attempt.totalQuestions} Correct",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = scoreColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = " • $dateFormatted",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View Details",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AccuracyBarRow(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val progress = if (total > 0) count.toFloat() / total.toFloat() else 0f
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "$count papers", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}
