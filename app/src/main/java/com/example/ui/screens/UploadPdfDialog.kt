package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.remote.BankingSampleDocument
import com.example.data.remote.SampleBankingData
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import com.example.ui.viewmodel.QuotaUiState

@Composable
fun UploadPdfDialog(
    quotaState: QuotaUiState,
    isGenerating: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onGenerate: (
        pdfUri: Uri?,
        pdfBytes: ByteArray?,
        title: String,
        documentName: String,
        questionCount: Int,
        difficulty: String,
        sampleText: String?
    ) -> Unit
) {
    val context = LocalContext.current
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPdfName by remember { mutableStateOf<String?>(null) }
    var selectedSampleDoc by remember { mutableStateOf<BankingSampleDocument?>(SampleBankingData.sampleDocuments.first()) }
    var testName by remember {
        mutableStateOf(SampleBankingData.sampleDocuments.first().title)
    }
    var questionCount by remember { mutableIntStateOf(5) }
    var selectedDifficulty by remember { mutableStateOf("Medium") }
    var validationError by remember { mutableStateOf<String?>(null) }

    // System File Picker for PDF files only
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val name = getFileName(context, uri)
            if (name.lowercase().endsWith(".pdf")) {
                selectedPdfUri = uri
                selectedPdfName = name
                selectedSampleDoc = null
                val cleanTitle = name.substringBeforeLast(".pdf").replace("_", " ")
                testName = cleanTitle
                validationError = null
            } else {
                Toast.makeText(context, "Only PDF files are supported!", Toast.LENGTH_LONG).show()
                validationError = "Please select a valid PDF file"
            }
        }
    }

    Dialog(
        onDismissRequest = { if (!isGenerating) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NavyPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "PDF Icon",
                                tint = NavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Create Banking MCQ Test",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (!isGenerating) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quota Status Indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (quotaState.isPro) GoldAccent.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (quotaState.isPro) "👑 Pro Status: Unlimited Generations"
                            else "Daily Free Quota: ${quotaState.remainingToday} / ${quotaState.dailyLimit} remaining",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (quotaState.isPro) GoldAccent else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // PDF Upload Area
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isGenerating) {
                            pdfPickerLauncher.launch(arrayOf("application/pdf"))
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = "Upload PDF",
                            tint = NavyPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedPdfName != null) "Selected: $selectedPdfName"
                            else "Tap to Upload PDF (Only .pdf files supported)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedPdfName != null) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedPdfName != null) GreenSuccess else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Supports RBI Circulars, Banking Guidelines, JAIIB/CAIIB Notes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Or Select Banking Sample PDF
                Text(
                    text = "— OR SELECT PRELOADED RBI CIRCULAR —",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SampleBankingData.sampleDocuments) { doc ->
                        val isSelected = selectedSampleDoc?.filename == doc.filename
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) NavyPrimary.copy(alpha = 0.12f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable(enabled = !isGenerating) {
                                    selectedSampleDoc = doc
                                    selectedPdfUri = null
                                    selectedPdfName = null
                                    testName = doc.title
                                    validationError = null
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = doc.filename.replace("_", " ").substringBeforeLast(".pdf"),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Test Name Input
                OutlinedTextField(
                    value = testName,
                    onValueChange = {
                        testName = it
                        validationError = null
                    },
                    label = { Text("Name Your Test") },
                    placeholder = { Text("e.g., RBI PSL Guidelines Mock Test") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isGenerating,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Question Count Selection
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Number of Questions:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 15, 20).forEach { count ->
                            val isSelected = questionCount == count
                            FilterChip(
                                selected = isSelected,
                                onClick = { if (!isGenerating) questionCount = count },
                                label = { Text("$count Qs") },
                                leadingIcon = if (isSelected) {
                                    { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Difficulty Selection
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Difficulty Level:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Easy", "Medium", "Hard", "Mixed").forEach { diff ->
                            val isSelected = selectedDifficulty == diff
                            FilterChip(
                                selected = isSelected,
                                onClick = { if (!isGenerating) selectedDifficulty = diff },
                                label = { Text(diff) },
                                leadingIcon = if (isSelected) {
                                    { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null
                            )
                        }
                    }
                }

                if (validationError != null || errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = validationError ?: errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Generate Button
                Button(
                    onClick = {
                        if (testName.isBlank()) {
                            validationError = "Please enter a name for the test"
                            return@Button
                        }
                        if (selectedPdfUri == null && selectedSampleDoc == null) {
                            validationError = "Please upload a PDF or select a banking document"
                            return@Button
                        }
                        if (!quotaState.isPro && questionCount > quotaState.remainingToday) {
                            validationError = "Daily quota exceeded! You have ${quotaState.remainingToday} questions left today. Upgrade to Pro for unlimited."
                            return@Button
                        }

                        val docName = selectedPdfName ?: selectedSampleDoc?.filename ?: "Banking_Document.pdf"
                        val sampleText = selectedSampleDoc?.sampleText

                        onGenerate(
                            selectedPdfUri,
                            null,
                            testName,
                            docName,
                            questionCount,
                            selectedDifficulty,
                            sampleText
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isGenerating,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Parsing PDF & Creating MCQs...")
                    } else {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Banking Test ($questionCount Qs)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun getFileName(context: Context, uri: Uri): String {
    var result = "Document.pdf"
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = it.getString(index)
                }
            }
        }
    }
    return result
}
