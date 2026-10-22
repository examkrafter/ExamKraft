package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttemptEntity
import com.example.data.model.QuestionEntity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RedContainer
import com.example.ui.theme.RedError
import org.json.JSONObject
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    attempt: AttemptEntity,
    questions: List<QuestionEntity>,
    onRetakeTest: (testId: Long) -> Unit,
    onViewAnalytics: () -> Unit,
    onBackToDashboard: () -> Unit
) {
    BackHandler {
        onBackToDashboard()
    }

    val answersMap = remember(attempt.answersJson) {
        val map = mutableMapOf<Int, Int>()
        try {
            val json = JSONObject(attempt.answersJson)
            val keys = json.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                map[k.toInt()] = json.getInt(k)
            }
        } catch (_: Exception) {}
        map
    }

    val score = attempt.score
    val total = attempt.totalQuestions
    val percentage = if (total > 0) (score * 100) / total else 0

    val timeMinutes = attempt.timeSpentSeconds / 60
    val timeSeconds = attempt.timeSpentSeconds % 60
    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", timeMinutes, timeSeconds)

    val gradeColor = when {
        percentage >= 80 -> GreenSuccess
        percentage >= 50 -> GoldAccent
        else -> RedError
    }

    val gradeText = when {
        percentage >= 80 -> "Excellent! Banker Mastery Level"
        percentage >= 60 -> "Good Effort! Review Explanations Below"
        else -> "Needs Revision on Circulars & Provisions"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Score & Performance Review",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToDashboard) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Dashboard")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onRetakeTest(attempt.testId) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Retake Test")
                    }

                    Button(
                        onClick = onViewAnalytics,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Insights, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("All Analytics")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))

                // Score Hero Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = attempt.testTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Circular Score Badge
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(gradeColor.copy(alpha = 0.12f))
                                .border(3.dp, gradeColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$percentage%",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = gradeColor
                                )
                                Text(
                                    text = "$score / $total",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = gradeText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = gradeColor
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Correct", style = MaterialTheme.typography.labelSmall, color = GreenSuccess)
                                Text("$score", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Incorrect", style = MaterialTheme.typography.labelSmall, color = RedError)
                                Text("${total - score}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Time Taken", style = MaterialTheme.typography.labelSmall, color = NavyPrimary)
                                Text(formattedTime, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUESTION BY QUESTION REVIEW",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Question Explanations List
            itemsIndexed(questions) { idx, q ->
                val selectedOpt = answersMap[idx] ?: -1
                val isCorrect = selectedOpt == q.correctOptionIndex
                val isUnanswered = selectedOpt < 0

                val optionLetters = listOf("A", "B", "C", "D")
                val options = listOf(q.optionA, q.optionB, q.optionC, q.optionD)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question ${idx + 1}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )

                            // Status Chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when {
                                            isCorrect -> GreenContainer
                                            isUnanswered -> MaterialTheme.colorScheme.surfaceVariant
                                            else -> RedContainer
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when {
                                            isCorrect -> Icons.Default.Check
                                            isUnanswered -> Icons.Default.Info
                                            else -> Icons.Default.Close
                                        },
                                        contentDescription = null,
                                        tint = when {
                                            isCorrect -> GreenSuccess
                                            isUnanswered -> MaterialTheme.colorScheme.onSurfaceVariant
                                            else -> RedError
                                        },
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = when {
                                            isCorrect -> "Correct"
                                            isUnanswered -> "Skipped"
                                            else -> "Incorrect"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isCorrect -> GreenSuccess
                                            isUnanswered -> MaterialTheme.colorScheme.onSurfaceVariant
                                            else -> RedError
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = q.questionText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Options Review
                        options.forEachIndexed { optIdx, optText ->
                            val isThisSelected = selectedOpt == optIdx
                            val isThisCorrect = q.correctOptionIndex == optIdx

                            val optBg = when {
                                isThisCorrect -> GreenSuccess.copy(alpha = 0.1f)
                                isThisSelected && !isThisCorrect -> RedError.copy(alpha = 0.1f)
                                else -> Color.Transparent
                            }

                            val borderCol = when {
                                isThisCorrect -> GreenSuccess
                                isThisSelected && !isThisCorrect -> RedError
                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(optBg)
                                    .border(1.dp, borderCol, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${optionLetters[optIdx]}.",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = when {
                                        isThisCorrect -> GreenSuccess
                                        isThisSelected && !isThisCorrect -> RedError
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = optText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isThisCorrect) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Correct Answer",
                                        tint = GreenSuccess,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else if (isThisSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Your Wrong Choice",
                                        tint = RedError,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Explanation Box
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavyPrimary.copy(alpha = 0.06f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Banking Authority Explanation:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = q.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
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
