package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.HeroSection
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.ConverterScreen
import com.example.ui.screens.GuideScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.theme.BhumiMapokTheme
import com.example.viewmodel.LandViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainApp(
    viewModel: LandViewModel
) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val isDarkPref by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val systemDark = isSystemInDarkTheme()
    val isDark = isDarkPref ?: systemDark

    val selectedShape by viewModel.selectedShape.collectAsStateWithLifecycle()
    val selectedUnit by viewModel.selectedUnit.collectAsStateWithLifecycle()
    val inputValues by viewModel.inputValues.collectAsStateWithLifecycle()
    val nTriangles by viewModel.nTriangles.collectAsStateWithLifecycle()
    val focusedKey by viewModel.focusedFieldKey.collectAsStateWithLifecycle()
    val landName by viewModel.landName.collectAsStateWithLifecycle()
    val kathaStandard by viewModel.kathaStandard.collectAsStateWithLifecycle()
    val heroDisplayUnit by viewModel.heroDisplayUnit.collectAsStateWithLifecycle()
    val totalSqFt by viewModel.calculatedAreaSqFt.collectAsStateWithLifecycle()
    val warningMessage by viewModel.warningMessage.collectAsStateWithLifecycle()

    val converterAmount by viewModel.converterAmount.collectAsStateWithLifecycle()
    val converterFromUnit by viewModel.converterFromUnit.collectAsStateWithLifecycle()
    val historyRecords by viewModel.historyRecords.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.userMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // BackHandler: Return to Calculator tab if on another tab
    BackHandler(enabled = activeTab != 0) {
        viewModel.setActiveTab(0)
    }

    BhumiMapokTheme(darkTheme = isDark) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    val items = listOf(
                        Triple(0, "ক্যালকুলেটর", Icons.Default.Calculate),
                        Triple(1, "রূপান্তর", Icons.Default.SwapHoriz),
                        Triple(2, "ইতিহাস", Icons.Default.History),
                        Triple(3, "গাইড", Icons.Default.MenuBook)
                    )

                    items.forEach { (index, title, icon) ->
                        val isSelected = activeTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setActiveTab(index) },
                            icon = {
                                androidx.compose.material3.Icon(
                                    imageVector = icon,
                                    contentDescription = title
                                )
                            },
                            label = {
                                Text(
                                    text = title,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.testTag("nav_tab_$index")
                        )
                    }
                }
            }
        ) { innerPadding ->
            val scrollState = rememberScrollState()
            val focusManager = LocalFocusManager.current

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .imePadding()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
                    .verticalScroll(scrollState)
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                // Hero Header Section
                HeroSection(
                    activeTab = activeTab,
                    isDarkTheme = isDark,
                    onToggleTheme = { viewModel.toggleDarkTheme(systemDark) },
                    shape = selectedShape,
                    unit = selectedUnit,
                    values = inputValues,
                    nTriangles = nTriangles,
                    focusedKey = focusedKey,
                    totalSqFt = totalSqFt,
                    heroDisplayUnit = heroDisplayUnit,
                    kathaStandard = kathaStandard,
                    onSelectHeroUnit = viewModel::setHeroDisplayUnit
                )

                // Overlapping Content Views
                val overlapOffset = if (activeTab == 0) (-65).dp else (-25).dp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = overlapOffset)
                ) {
                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "tabContentAnim"
                    ) { tab ->
                        when (tab) {
                            0 -> CalculatorScreen(
                                shape = selectedShape,
                                unit = selectedUnit,
                                values = inputValues,
                                nTriangles = nTriangles,
                                landName = landName,
                                totalSqFt = totalSqFt,
                                kathaStandard = kathaStandard,
                                warningMessage = warningMessage,
                                onSelectShape = viewModel::setShape,
                                onSelectUnit = { viewModel.setUnit(it, convertValues = true) },
                                onValueChange = viewModel::updateInputValue,
                                onFieldFocusChange = viewModel::setFocusedField,
                                onLandNameChange = viewModel::setLandName,
                                onAddTriangle = viewModel::addTriangle,
                                onRemoveTriangle = viewModel::removeTriangle,
                                onSave = viewModel::saveCurrentCalculation,
                                onShowMessage = viewModel::emitMessage
                            )

                            1 -> ConverterScreen(
                                amountStr = converterAmount,
                                fromUnit = converterFromUnit,
                                kathaStandard = kathaStandard,
                                onAmountChange = viewModel::updateConverterAmount,
                                onSelectFromUnit = viewModel::setConverterFromUnit,
                                onShowMessage = viewModel::emitMessage
                            )

                            2 -> HistoryScreen(
                                records = historyRecords,
                                kathaStandard = kathaStandard,
                                onLoadRecord = viewModel::loadRecordToCalculator,
                                onDeleteRecord = viewModel::deleteRecord,
                                onClearAll = viewModel::clearAllHistory
                            )

                            3 -> GuideScreen(
                                currentStandard = kathaStandard,
                                onSelectStandard = viewModel::setKathaStandard
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
