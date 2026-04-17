@file:OptIn(
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.fitplan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fitplan.app.ui.theme.FitPlanTheme
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitPlanTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FitPlanApp()
                }
            }
        }
    }
}

@Composable
private fun FitPlanApp() {
    var heightText by rememberSaveable { mutableStateOf("") }
    var weightText by rememberSaveable { mutableStateOf("") }
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.Home.name) }

    val selectedScreen = AppScreen.valueOf(currentScreen)
    val heightCm = heightText.toDoubleOrNull()
    val weightKg = weightText.toDoubleOrNull()
    val result = PlanEngine.evaluate(heightCm, weightKg)

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = Color.Transparent,
        bottomBar = {
            AppBottomBar(
                selectedScreen = selectedScreen,
                onScreenSelected = { currentScreen = it.name }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF07111F),
                            Color(0xFF10203A),
                            Color(0xFF1C3256)
                        )
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x55FB923C),
                                Color.Transparent
                            )
                        )
                    )
            )
            when (selectedScreen) {
                AppScreen.Home -> HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    heightText = heightText,
                    weightText = weightText,
                    onHeightChange = { heightText = it.filterNumericInput() },
                    onWeightChange = { weightText = it.filterNumericInput() },
                    result = result,
                    onOpenInsights = {
                        if (result != null) currentScreen = AppScreen.Insights.name
                    }
                )
                AppScreen.Insights -> InsightsScreen(
                    modifier = Modifier.padding(innerPadding),
                    result = result,
                    onOpenPlan = {
                        if (result != null) currentScreen = AppScreen.Plan.name
                    }
                )
                AppScreen.Plan -> PlanScreen(
                    modifier = Modifier.padding(innerPadding),
                    result = result,
                    onStartAtHome = { currentScreen = AppScreen.Home.name }
                )
            }
        }
    }
}

@Composable
private fun AppBottomBar(
    selectedScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF08111F).copy(alpha = 0.96f),
        tonalElevation = 0.dp
    ) {
        AppScreen.entries.forEach { screen ->
            NavigationBarItem(
                selected = selectedScreen == screen,
                onClick = { onScreenSelected(screen) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label
                    )
                },
                label = { Text(screen.label) }
            )
        }
    }
}

@Composable
private fun HomeScreen(
    modifier: Modifier,
    heightText: String,
    weightText: String,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    result: PlanResult?,
    onOpenInsights: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            AppHeader(
                eyebrow = "FitPlan System",
                title = "Train with a plan that matches your current body condition.",
                subtitle = "This flow comes directly from the PDF brief: input metrics, calculate BMI, classify the condition, then generate a weekly recommendation."
            )
        }
        item {
            HeroSection()
        }
        item {
            InputCard(
                heightText = heightText,
                onHeightChange = onHeightChange,
                weightText = weightText,
                onWeightChange = onWeightChange,
                result = result,
                onPrimaryAction = onOpenInsights
            )
        }
        item {
            PreviewStrip(result = result)
        }
    }
}

@Composable
private fun InsightsScreen(
    modifier: Modifier,
    result: PlanResult?,
    onOpenPlan: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            AppHeader(
                eyebrow = "BMI Analysis",
                title = "See your category, strategy, and focus areas at a glance.",
                subtitle = "The recommendation engine turns a BMI result into a practical bulk, maintain, or cut direction."
            )
        }
        item {
            SummaryCard(result = result)
        }
        item {
            CategoryScaleCard(result = result)
        }
        item {
            StrategyCard(result = result, onOpenPlan = onOpenPlan)
        }
        item {
            RecoveryCard(result = result)
        }
    }
}

@Composable
private fun PlanScreen(
    modifier: Modifier,
    result: PlanResult?,
    onStartAtHome: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            AppHeader(
                eyebrow = "Weekly Plan",
                title = "Move from diagnosis into an actual training split.",
                subtitle = "This screen packages the recommended strategy into a full week of sessions."
            )
        }
        item {
            PlanOverviewCard(result = result, onStartAtHome = onStartAtHome)
        }
        if (result != null) {
            items(result.plan.days) { day ->
                WorkoutDayCard(day = day)
            }
        } else {
            item {
                EmptyPlanCard()
            }
        }
    }
}

@Composable
private fun AppHeader(
    eyebrow: String,
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = eyebrow.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF93C5FD)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFD7E6F7)
        )
    }
}

@Composable
private fun HeroSection() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x18FFFFFF)),
        shape = RoundedCornerShape(34.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFEA580C),
                            Color(0xFFF59E0B),
                            Color(0xFF0EA5E9)
                        )
                    )
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Generic workout templates are dead weight. FitPlan adapts the starting point to the user’s BMI first.",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickStat(label = "Instant BMI", icon = Icons.Rounded.Speed)
                QuickStat(label = "Body Category", icon = Icons.Rounded.Insights)
                QuickStat(label = "Training Split", icon = Icons.Rounded.Schedule)
            }
        }
    }
}

@Composable
private fun QuickStat(label: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.14f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = Color.White, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun InputCard(
    heightText: String,
    onHeightChange: (String) -> Unit,
    weightText: String,
    onWeightChange: (String) -> Unit,
    result: PlanResult?,
    onPrimaryAction: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(30.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Personal metrics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "Use centimetres and kilograms. FitPlan validates realistic ranges before generating the recommendation.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF475569)
            )
            OutlinedTextField(
                value = heightText,
                onValueChange = onHeightChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                label = { Text("Height (cm)") },
                leadingIcon = { Icon(Icons.Rounded.Speed, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = appTextFieldColors()
            )
            OutlinedTextField(
                value = weightText,
                onValueChange = onWeightChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                label = { Text("Weight (kg)") },
                leadingIcon = { Icon(Icons.Rounded.MonitorWeight, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = appTextFieldColors()
            )
            Button(
                onClick = onPrimaryAction,
                enabled = result != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEA580C),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFCBD5E1),
                    disabledContentColor = Color(0xFF64748B)
                )
            ) {
                Text(
                    text = if (result == null) "Enter valid metrics" else "Open insights",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun PreviewStrip(result: PlanResult?) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MiniPanel(
            title = "Status",
            value = result?.category?.label ?: "Awaiting",
            icon = Icons.Rounded.Insights
        )
        MiniPanel(
            title = "Direction",
            value = result?.strategy?.shortLabel ?: "Pending",
            icon = Icons.Rounded.Timeline
        )
        MiniPanel(
            title = "Split",
            value = if (result != null) "${result.plan.days.size} days" else "Locked",
            icon = Icons.Rounded.FitnessCenter
        )
    }
}

@Composable
private fun MiniPanel(
    title: String,
    value: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x18FFFFFF)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0x22FB923C))
                    .padding(10.dp)
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFFFFC38A))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = Color(0xFFB9C6D8), style = MaterialTheme.typography.labelLarge)
                Text(
                    value,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(result: PlanResult?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x16FFFFFF)),
        shape = RoundedCornerShape(30.dp)
    ) {
        if (result == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "BMI insights will appear here",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Valid ranges: 120-230 cm and 35-250 kg.",
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    MetricTile(
                        modifier = Modifier.weight(1f),
                        title = "BMI",
                        value = result.bmiLabel,
                        caption = "Calculated instantly",
                        icon = Icons.Rounded.AutoGraph
                    )
                    MetricTile(
                        modifier = Modifier.weight(1f),
                        title = "Category",
                        value = result.category.label,
                        caption = result.categoryRangeLabel,
                        icon = Icons.Rounded.LocalFireDepartment
                    )
                }
                MetricTile(
                    title = "Goal Track",
                    value = result.strategy.shortLabel,
                    caption = "Recommended primary direction",
                    icon = Icons.Rounded.Timeline
                )
            }
        }
    }
}

@Composable
private fun MetricTile(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    caption: String,
    icon: ImageVector
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF8FAFC))
            .padding(18.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFFEA580C))
        Spacer(modifier = Modifier.height(12.dp))
        Text(title, color = Color(0xFF475569), style = MaterialTheme.typography.labelLarge)
        Text(
            value,
            color = Color(0xFF0F172A),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(caption, color = Color(0xFF64748B), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun CategoryScaleCard(result: PlanResult?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(30.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Body condition scale",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold
            )
            if (result == null) {
                Text(
                    text = "Add your metrics on the Home tab to place your BMI on the scale.",
                    color = Color(0xFF475569)
                )
            } else {
                LinearProgressIndicator(
                    progress = { result.progressValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    color = Color(0xFFEA580C),
                    trackColor = Color(0xFFE2E8F0)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BmiCategory.entries.forEach { category ->
                        AssistChip(
                            onClick = {},
                            label = { Text(category.label) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (category == result.category) Color(0xFFFFEDD5) else Color(0xFFF1F5F9),
                                labelColor = if (category == result.category) Color(0xFF9A3412) else Color(0xFF475569)
                            )
                        )
                    }
                }
                Text(
                    text = "Current position: BMI ${result.bmiLabel}, ${result.category.label.lowercase()} range.",
                    color = Color(0xFF334155)
                )
            }
        }
    }
}

@Composable
private fun StrategyCard(
    result: PlanResult?,
    onOpenPlan: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(30.dp)
    ) {
        if (result == null) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Recommendation",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "The app recommends bulk, cut, or maintain after BMI validation.",
                    color = Color(0xFF475569)
                )
            }
        } else {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = result.strategy.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = result.strategy.summary,
                    color = Color(0xFF334155),
                    style = MaterialTheme.typography.bodyLarge
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    result.strategy.focusChips.forEach { chip ->
                        AssistChip(
                            onClick = {},
                            label = { Text(chip) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFFFFEDD5),
                                labelColor = Color(0xFF9A3412)
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = when (chip) {
                                        "Calorie Surplus" -> Icons.Rounded.Restaurant
                                        "Calorie Deficit" -> Icons.Rounded.LocalFireDepartment
                                        "Hydration" -> Icons.Rounded.WaterDrop
                                        else -> Icons.Rounded.FitnessCenter
                                    },
                                    contentDescription = null,
                                    tint = Color(0xFFEA580C)
                                )
                            }
                        )
                    }
                }
                Button(
                    onClick = onOpenPlan,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F172A),
                        contentColor = Color.White
                    )
                ) {
                    Text("Open weekly plan")
                }
            }
        }
    }
}

@Composable
private fun RecoveryCard(result: PlanResult?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x18FFFFFF)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Coach note",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = result?.strategy?.coachNote
                    ?: "Your first unlock is simply valid input. After that, FitPlan can route you into the correct strategy.",
                color = Color(0xFFD7E6F7),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun PlanOverviewCard(
    result: PlanResult?,
    onStartAtHome: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(30.dp)
    ) {
        if (result == null) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "No plan yet",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Return to Home, enter height and weight, then the weekly split will unlock here.",
                    color = Color(0xFF475569)
                )
                Button(
                    onClick = onStartAtHome,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEA580C),
                        contentColor = Color.White
                    )
                ) {
                    Text("Go to Home")
                }
            }
        } else {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Plan overview",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${result.plan.days.size} structured days aligned to a ${result.strategy.shortLabel.lowercase()} approach.",
                    color = Color(0xFF334155)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Progressive overload", "Consistency", "Recovery", "Hydration").forEach { item ->
                        AssistChip(
                            onClick = {},
                            label = { Text(item) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFFF1F5F9),
                                labelColor = Color(0xFF334155)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutDayCard(day: WorkoutDay) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = day.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = day.focus,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF475569)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFFFFEDD5))
                        .padding(12.dp)
                ) {
                    Icon(
                        Icons.Rounded.FitnessCenter,
                        contentDescription = null,
                        tint = Color(0xFFEA580C)
                    )
                }
            }
            day.exercises.forEach { exercise ->
                Text(
                    text = "• $exercise",
                    color = Color(0xFF1E293B),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun EmptyPlanCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x14FFFFFF)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Text(
            text = "Add valid height and weight values on the Home screen to unlock your workout schedule.",
            modifier = Modifier.padding(22.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun appTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color(0xFFF8FAFC),
    unfocusedContainerColor = Color(0xFFF8FAFC),
    disabledContainerColor = Color(0xFFF1F5F9),
    focusedIndicatorColor = Color(0xFFEA580C),
    unfocusedIndicatorColor = Color(0xFFCBD5E1),
    cursorColor = Color(0xFFEA580C),
    focusedTextColor = Color(0xFF0F172A),
    unfocusedTextColor = Color(0xFF0F172A)
)

private fun String.filterNumericInput(): String {
    val filtered = filter { it.isDigit() || it == '.' }
    val firstDot = filtered.indexOf('.')
    if (firstDot == -1) return filtered
    return buildString {
        filtered.forEachIndexed { index, c ->
            if (c != '.' || index == firstDot) append(c)
        }
    }
}

private object PlanEngine {
    fun evaluate(heightCm: Double?, weightKg: Double?): PlanResult? {
        if (heightCm == null || weightKg == null) return null
        if (heightCm !in 120.0..230.0 || weightKg !in 35.0..250.0) return null

        val bmi = weightKg / (heightCm / 100.0).pow(2)
        val category = when {
            bmi < 18.5 -> BmiCategory.Underweight
            bmi < 25.0 -> BmiCategory.Healthy
            bmi < 30.0 -> BmiCategory.Overweight
            else -> BmiCategory.Obese
        }

        val strategy = when (category) {
            BmiCategory.Underweight -> Strategy(
                title = "Lean bulk recommended",
                shortLabel = "Lean Bulk",
                summary = "Focus on progressive overload, compound lifts, and a small calorie surplus to build muscle without adding unnecessary fat.",
                focusChips = listOf("Calorie Surplus", "Strength Focus", "Recovery"),
                coachNote = "Keep the surplus controlled. The job is to add quality size, not drift into a sloppy bulk."
            )
            BmiCategory.Healthy -> Strategy(
                title = "Maintain and refine",
                shortLabel = "Maintain",
                summary = "Your BMI is in a balanced range. Prioritize consistency, strength gains, and body recomposition through a stable nutrition plan.",
                focusChips = listOf("Maintenance", "Hypertrophy", "Mobility"),
                coachNote = "This is the best place to sharpen body composition. Avoid unnecessary extremes."
            )
            BmiCategory.Overweight -> Strategy(
                title = "Controlled cut recommended",
                shortLabel = "Cut",
                summary = "Use higher training density, daily movement, and a moderate calorie deficit to reduce body fat while protecting muscle mass.",
                focusChips = listOf("Calorie Deficit", "Conditioning", "High Protein"),
                coachNote = "Protect strength while trimming body fat. A moderate deficit is easier to sustain and usually performs better."
            )
            BmiCategory.Obese -> Strategy(
                title = "Foundational fat-loss plan",
                shortLabel = "Cut",
                summary = "Start with low-impact training, full-body strength sessions, and sustainable calorie control to build adherence and reduce fatigue.",
                focusChips = listOf("Calorie Deficit", "Low Impact", "Consistency"),
                coachNote = "Adherence matters more than intensity at the start. Build momentum first, then push volume later."
            )
        }

        return PlanResult(
            bmi = bmi,
            category = category,
            strategy = strategy,
            plan = weeklyPlanFor(category)
        )
    }

    private fun weeklyPlanFor(category: BmiCategory): WorkoutPlan {
        val days = when (category) {
            BmiCategory.Underweight -> listOf(
                WorkoutDay("Day 1", "Upper body strength", listOf("Bench press 4 x 6", "Barbell row 4 x 8", "Overhead press 3 x 8", "Lat pulldown 3 x 10")),
                WorkoutDay("Day 2", "Lower body power", listOf("Back squat 4 x 6", "Romanian deadlift 4 x 8", "Walking lunges 3 x 10", "Standing calf raise 3 x 15")),
                WorkoutDay("Day 3", "Recovery + core", listOf("Incline walk 20 min", "Dead bug 3 x 12", "Plank 3 x 45 sec", "Mobility flow 10 min")),
                WorkoutDay("Day 4", "Push hypertrophy", listOf("Incline dumbbell press 4 x 10", "Chest fly 3 x 12", "Lateral raise 3 x 15", "Cable triceps pressdown 3 x 12")),
                WorkoutDay("Day 5", "Pull + legs", listOf("Trap bar deadlift 4 x 5", "Seated row 3 x 10", "Leg press 3 x 12", "Hammer curl 3 x 12")),
                WorkoutDay("Day 6", "Optional accessories", listOf("Face pulls 3 x 15", "Hip thrust 3 x 12", "Farmer carry 4 rounds", "Stretching 10 min")),
                WorkoutDay("Day 7", "Rest", listOf("Light walk", "Hydration focus", "Meal prep", "Sleep 8 hours"))
            )
            BmiCategory.Healthy -> listOf(
                WorkoutDay("Day 1", "Push", listOf("Bench press 4 x 8", "Overhead press 3 x 8", "Incline dumbbell press 3 x 10", "Lateral raise 3 x 15")),
                WorkoutDay("Day 2", "Pull", listOf("Pull-up or assisted pull-up 4 x 6", "Barbell row 4 x 8", "Single-arm dumbbell row 3 x 10", "EZ bar curl 3 x 12")),
                WorkoutDay("Day 3", "Legs", listOf("Front squat 4 x 6", "Romanian deadlift 4 x 8", "Bulgarian split squat 3 x 10", "Seated calf raise 3 x 15")),
                WorkoutDay("Day 4", "Active recovery", listOf("Zone 2 cardio 25 min", "Mobility session 15 min", "Hanging knee raise 3 x 12", "Foam rolling")),
                WorkoutDay("Day 5", "Upper hypertrophy", listOf("Incline bench 4 x 10", "Chest-supported row 4 x 10", "Arnold press 3 x 12", "Cable curl 3 x 15")),
                WorkoutDay("Day 6", "Conditioning", listOf("Sled push 8 rounds", "Battle ropes 6 rounds", "Kettlebell swing 3 x 20", "Farmer carry 4 rounds")),
                WorkoutDay("Day 7", "Rest", listOf("Walk 7,000+ steps", "Breathing work", "Meal planning", "Sleep target"))
            )
            BmiCategory.Overweight -> listOf(
                WorkoutDay("Day 1", "Full body A", listOf("Goblet squat 4 x 10", "Incline push-up 4 x 10", "Seated row 4 x 12", "Marching carry 3 rounds")),
                WorkoutDay("Day 2", "Cardio + core", listOf("Brisk walk 30 min", "Bike 15 min", "Bird dog 3 x 12", "Side plank 3 x 30 sec")),
                WorkoutDay("Day 3", "Full body B", listOf("Leg press 4 x 12", "Dumbbell bench press 4 x 10", "Lat pulldown 4 x 10", "Glute bridge 3 x 15")),
                WorkoutDay("Day 4", "Recovery", listOf("Stretching 15 min", "Easy walk 20 min", "Mobility drills", "Hydration focus")),
                WorkoutDay("Day 5", "Full body C", listOf("Box squat 4 x 8", "Cable row 4 x 12", "Dumbbell shoulder press 3 x 10", "Step-up 3 x 12")),
                WorkoutDay("Day 6", "Intervals", listOf("Treadmill incline 10 rounds", "Medicine ball slam 3 x 12", "Air bike 8 rounds", "Cooldown walk")),
                WorkoutDay("Day 7", "Rest", listOf("Light movement", "Protein-first meals", "Sleep 8 hours", "Weekly check-in"))
            )
            BmiCategory.Obese -> listOf(
                WorkoutDay("Day 1", "Foundational strength", listOf("Sit-to-stand 4 x 10", "Wall push-up 4 x 10", "Supported row 4 x 12", "Farmer hold 4 x 20 sec")),
                WorkoutDay("Day 2", "Low-impact cardio", listOf("Walk 20 min", "Stationary bike 15 min", "Breathing drills", "Gentle stretching")),
                WorkoutDay("Day 3", "Full body support", listOf("Leg press 3 x 12", "Machine chest press 3 x 12", "Lat pulldown 3 x 12", "Seated knee raise 3 x 10")),
                WorkoutDay("Day 4", "Recovery", listOf("Mobility flow 15 min", "Easy walk 15 min", "Hydration goal", "Early bedtime")),
                WorkoutDay("Day 5", "Strength + steps", listOf("Box squat 3 x 10", "Cable row 3 x 12", "Dumbbell deadlift 3 x 10", "Step count target")),
                WorkoutDay("Day 6", "Cardio confidence", listOf("Elliptical 15 min", "Walk 15 min", "Band pull-apart 3 x 15", "Cooldown stretch")),
                WorkoutDay("Day 7", "Rest", listOf("Light movement", "Meal prep", "Progress reflection", "Recovery focus"))
            )
        }
        return WorkoutPlan(days)
    }
}

private data class PlanResult(
    val bmi: Double,
    val category: BmiCategory,
    val strategy: Strategy,
    val plan: WorkoutPlan
) {
    val bmiLabel: String = "%.1f".format(bmi)
    val categoryRangeLabel: String = category.rangeLabel
    val progressValue: Float = (bmi.coerceIn(15.0, 40.0) - 15.0).div(25.0).toFloat()
}

private enum class BmiCategory(val label: String, val rangeLabel: String) {
    Underweight("Underweight", "< 18.5"),
    Healthy("Healthy", "18.5 - 24.9"),
    Overweight("Overweight", "25.0 - 29.9"),
    Obese("Obese", "30+")
}

private data class Strategy(
    val title: String,
    val shortLabel: String,
    val summary: String,
    val focusChips: List<String>,
    val coachNote: String
)

private data class WorkoutPlan(val days: List<WorkoutDay>)

private data class WorkoutDay(
    val title: String,
    val focus: String,
    val exercises: List<String>
)

private enum class AppScreen(
    val label: String,
    val icon: ImageVector
) {
    Home("Home", Icons.Rounded.Home),
    Insights("Insights", Icons.Rounded.Insights),
    Plan("Plan", Icons.Rounded.FitnessCenter)
}

@Preview(showBackground = true, backgroundColor = 0xFF07111F)
@Composable
private fun FitPlanPreview() {
    FitPlanTheme {
        FitPlanApp()
    }
}
