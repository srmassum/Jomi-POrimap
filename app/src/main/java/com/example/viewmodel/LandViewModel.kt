package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.LandRepository
import com.example.model.AreaUnit
import com.example.model.KathaStandard
import com.example.model.LandRecord
import com.example.model.LandShape
import com.example.model.LengthUnit
import com.example.util.BengaliFormat
import com.example.util.LandMath
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class LandViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LandRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = LandRepository(db.landRecordDao())
    }

    val historyRecords: StateFlow<List<LandRecord>> = repository.allRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

    private val _selectedShape = MutableStateFlow(LandShape.RECTANGLE)
    val selectedShape: StateFlow<LandShape> = _selectedShape.asStateFlow()

    private val _selectedUnit = MutableStateFlow(LengthUnit.FEET)
    val selectedUnit: StateFlow<LengthUnit> = _selectedUnit.asStateFlow()

    private val _inputValues = MutableStateFlow<Map<String, String>>(
        mapOf("rect_l" to "৬০", "rect_w" to "৪০")
    )
    val inputValues: StateFlow<Map<String, String>> = _inputValues.asStateFlow()

    private val _nTriangles = MutableStateFlow(2)
    val nTriangles: StateFlow<Int> = _nTriangles.asStateFlow()

    private val _focusedFieldKey = MutableStateFlow<String?>(null)
    val focusedFieldKey: StateFlow<String?> = _focusedFieldKey.asStateFlow()

    private val _landName = MutableStateFlow("")
    val landName: StateFlow<String> = _landName.asStateFlow()

    private val _kathaStandard = MutableStateFlow(KathaStandard.STANDARD_720)
    val kathaStandard: StateFlow<KathaStandard> = _kathaStandard.asStateFlow()

    private val _heroDisplayUnit = MutableStateFlow(AreaUnit.SQ_FEET)
    val heroDisplayUnit: StateFlow<AreaUnit> = _heroDisplayUnit.asStateFlow()

    // Calculated Area in Sq Ft
    val calculatedAreaSqFt: StateFlow<Double> = combine(
        _selectedShape,
        _selectedUnit,
        _inputValues,
        _nTriangles
    ) { shape, unit, values, count ->
        LandMath.calculateAreaSqFt(shape, unit, values, count)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Double.NaN)

    // Validation warning
    val warningMessage: StateFlow<String?> = combine(
        _selectedShape,
        _inputValues,
        _nTriangles
    ) { shape, values, count ->
        LandMath.getValidationWarning(shape, values, count)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Converter State
    private val _converterAmount = MutableStateFlow("১")
    val converterAmount: StateFlow<String> = _converterAmount.asStateFlow()

    private val _converterFromUnit = MutableStateFlow(AreaUnit.KATHA)
    val converterFromUnit: StateFlow<AreaUnit> = _converterFromUnit.asStateFlow()

    // Toast/Snackbar notifications
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    fun setActiveTab(tab: Int) {
        _activeTab.value = tab
    }

    fun toggleDarkTheme(currentSystemDark: Boolean) {
        val current = _isDarkTheme.value ?: currentSystemDark
        _isDarkTheme.value = !current
    }

    fun setShape(shape: LandShape) {
        _selectedShape.value = shape
        _focusedFieldKey.value = null
    }

    fun setUnit(newUnit: LengthUnit, convertValues: Boolean = true) {
        val oldUnit = _selectedUnit.value
        if (oldUnit == newUnit) return

        if (convertValues) {
            val ratio = oldUnit.feetRatio / newUnit.feetRatio
            val currentMap = _inputValues.value
            val newMap = currentMap.toMutableMap()
            currentMap.forEach { (key, strVal) ->
                val num = BengaliFormat.parseNumber(strVal)
                if (num > 0) {
                    val converted = num * ratio
                    val formatted = if (converted % 1.0 == 0.0) {
                        BengaliFormat.toBengaliDigits(converted.roundToInt().toString())
                    } else {
                        BengaliFormat.formatNumber(converted, 2)
                    }
                    newMap[key] = formatted
                }
            }
            _inputValues.value = newMap
            emitMessage("মাপ ${newUnit.shortBn}-এ রূপান্তর করা হয়েছে")
        }
        _selectedUnit.value = newUnit
    }

    fun updateInputValue(key: String, value: String) {
        val fullKey = "${_selectedShape.value.id}_$key"
        val map = _inputValues.value.toMutableMap()
        map[fullKey] = value
        _inputValues.value = map
    }

    fun setFocusedField(fieldKey: String?) {
        _focusedFieldKey.value = fieldKey
    }

    fun setLandName(name: String) {
        _landName.value = name
    }

    fun setKathaStandard(standard: KathaStandard) {
        _kathaStandard.value = standard
    }

    fun setHeroDisplayUnit(unit: AreaUnit) {
        _heroDisplayUnit.value = unit
    }

    fun cycleHeroDisplayUnit() {
        val options = listOf(
            AreaUnit.SQ_FEET,
            AreaUnit.SHOTOK,
            AreaUnit.KATHA,
            AreaUnit.BIGHA,
            AreaUnit.ACRE,
            AreaUnit.SQ_METER
        )
        val currentIndex = options.indexOf(_heroDisplayUnit.value)
        val nextIndex = (currentIndex + 1) % options.size
        _heroDisplayUnit.value = options[nextIndex]
    }

    fun addTriangle() {
        if (_nTriangles.value < 5) {
            _nTriangles.value += 1
        }
    }

    fun removeTriangle() {
        if (_nTriangles.value > 1) {
            _nTriangles.value -= 1
        }
    }

    fun saveCurrentCalculation() {
        val sqFt = calculatedAreaSqFt.value
        if (sqFt.isNaN() || sqFt <= 0) {
            emitMessage("সঠিক মাপ দিন")
            return
        }

        viewModelScope.launch {
            val record = LandRecord(
                name = _landName.value.trim(),
                shapeId = _selectedShape.value.id,
                unitId = _selectedUnit.value.id,
                totalSqFt = sqFt,
                serializedInputs = BengaliFormat.serializeMap(_inputValues.value),
                triangleCount = _nTriangles.value,
                timestamp = System.currentTimeMillis()
            )
            repository.insert(record)
            _landName.value = ""
            emitMessage("হিসাব ইতিহাসে সংরক্ষিত হয়েছে")
        }
    }

    fun deleteRecord(record: LandRecord) {
        viewModelScope.launch {
            repository.delete(record)
            emitMessage("হিসাব মুছে ফেলা হয়েছে")
        }
    }

    fun restoreRecord(record: LandRecord) {
        viewModelScope.launch {
            repository.insert(record)
            emitMessage("হিসাব পুনরুদ্ধার করা হয়েছে")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
            emitMessage("সব ইতিহাস মুছে ফেলা হয়েছে")
        }
    }

    fun loadRecordToCalculator(record: LandRecord) {
        _selectedShape.value = LandShape.fromId(record.shapeId)
        _selectedUnit.value = LengthUnit.fromId(record.unitId)
        _nTriangles.value = record.triangleCount
        val map = BengaliFormat.deserializeMap(record.serializedInputs)
        if (map.isNotEmpty()) {
            _inputValues.value = map
        }
        _activeTab.value = 0
        emitMessage("হিসাব লোড করা হয়েছে")
    }

    fun updateConverterAmount(amount: String) {
        _converterAmount.value = amount
    }

    fun setConverterFromUnit(unit: AreaUnit) {
        _converterFromUnit.value = unit
    }

    fun emitMessage(message: String) {
        viewModelScope.launch {
            _userMessage.emit(message)
        }
    }
}
