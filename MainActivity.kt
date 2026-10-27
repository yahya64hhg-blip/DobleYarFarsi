package com.example.dobleyar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.net.Uri

data class CharacterVoice(
    val name: String,
    val voice: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DubApp()
            }
        }
    }
}

@Composable
fun DubApp() {
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var videoName by remember { mutableStateOf("هنوز فیلمی انتخاب نشده") }
    var status by remember { mutableStateOf("برای شروع، یک ویدیو انتخاب کن.") }
    var selectedLanguage by remember { mutableStateOf("فارسی") }
    var characters by remember {
        mutableStateOf(
            listOf(
                CharacterVoice("شخصیت ۱", "صدای فارسی بم"),
                CharacterVoice("شخصیت ۲", "صدای فارسی جوان"),
            )
        )
    }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            videoUri = uri
            videoName = uri.lastPathSegment ?: "ویدیوی انتخاب‌شده"
            status = "ویدیو انتخاب شد. برای دوبله، موتورهای پردازش باید متصل شوند."
        }
    }

    val voices = listOf(
        "صدای فارسی بم",
        "صدای فارسی میانه",
        "صدای فارسی جوان",
        "صدای فارسی زن",
        "صدای فارسی آرام"
    )

    CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("دوبله‌یار فارسی") }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "فیلمت را به دوبله فارسی تبدیل کن",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        "نسخه‌ی اولیه‌ی نمایشی؛ هر شخصیت می‌تواند صدای جداگانه داشته باشد.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                item {
                    Card {
                        Column(
                            Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("۱. انتخاب فیلم", style = MaterialTheme.typography.titleMedium)
                            Text(videoName)
                            Button(
                                onClick = { videoPicker.launch("video/*") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("انتخاب ویدیو از گوشی")
                            }
                        }
                    }
                }
                item {
                    Card {
                        Column(
                            Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("۲. زبان دوبله", style = MaterialTheme.typography.titleMedium)
                            Text("زبان خروجی: $selectedLanguage")
                            Text("زبان نسخه‌ی اول فارسی است.")
                        }
                    }
                }
                item {
                    Text("۳. صدای هر شخصیت", style = MaterialTheme.typography.titleMedium)
                }
                items(characters.indices.toList()) { index ->
                    Card {
                        Column(
                            Modifier.fillMaxWidth().padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = characters[index].name,
                                onValueChange = { newName ->
                                    characters = characters.toMutableList().also {
                                        it[index] = it[index].copy(name = newName)
                                    }
                                },
                                label = { Text("نام شخصیت") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = characters[index].voice,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("صدای دوبله") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    voices.forEach { voice ->
                                        DropdownMenuItem(
                                            text = { Text(voice) },
                                            onClick = {
                                                characters = characters.toMutableList().also {
                                                    it[index] = it[index].copy(voice = voice)
                                                }
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    OutlinedButton(
                        onClick = {
                            characters = characters + CharacterVoice(
                                "شخصیت ${characters.size + 1}",
                                voices.first()
                            )
                            status = "شخصیت جدید اضافه شد."
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("افزودن شخصیت")
                    }
                }
                item {
                    Button(
                        onClick = {
                            status = if (videoUri == null) {
                                "ابتدا یک ویدیو انتخاب کن."
                            } else {
                                "طرح دوبله آماده است؛ برای تولید خروجی باید سرویس تشخیص گفتار، ترجمه، تولید صدا و ترکیب ویدیو متصل شود."
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("شروع دوبله")
                    }
                }
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(status)
                        }
                    }
                }
                item {
                    Text(
                        "توجه: این نمونه هنوز صدای واقعی تولید نمی‌کند و فایل ویدیویی خروجی نمی‌سازد.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
