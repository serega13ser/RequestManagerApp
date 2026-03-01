package com.serega.requestmanager.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serega.requestmanager.data.local.RequestEntity
import com.serega.requestmanager.data.mapper.toDomainRequest
import com.serega.requestmanager.data.mapper.toEntityRequest
import com.serega.requestmanager.domain.repository.RequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RequestViewModel @Inject constructor(
    private val repository: RequestRepository,
) : ViewModel() {

    private val _formState = MutableStateFlow(RequestFormState())
    val formState = _formState.asStateFlow()

    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading

    private var _errorMessage by mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage

    private var _isSuccess by mutableStateOf(false)
    val isSuccess: Boolean get() = _isSuccess

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _requests: StateFlow<List<RequestEntity>> = repository.getAllRequests()
        .map { list -> list.map { it.toEntityRequest() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<RequestListUiState> = combine(
        _requests,
        _searchQuery
    ) { requests, query ->
        val filtered = requests.filter {
            query.isEmpty() || it.orderNumber.contains(query, true) || it.address.contains(
                query,
                true
            )
        }

        val inputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateOut = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val dayOut = SimpleDateFormat("EEEE", Locale.getDefault())

        val grouped = filtered.groupBy { it.requestDate.substringBefore(" ").trim() }
            .toList()
            .sortedByDescending { (dateStr, _) ->
                try {
                    inputFormat.parse(dateStr)
                } catch (e: Exception) {
                    null
                } ?: Date(0)
            }
            .map { (dateStr, list) ->
                val dateObj = try {
                    inputFormat.parse(dateStr)
                } catch (e: Exception) {
                    null
                }
                RequestGroup(
                    dateDisplay = dateObj?.let { dateOut.format(it) } ?: dateStr,
                    dayOfWeek = dateObj?.let {
                        dayOut.format(it).replaceFirstChar { c -> c.titlecase() }
                    } ?: "",
                    requests = list
                )
            }

        RequestListUiState(
            groupedRequests = grouped,
            totalCount = requests.size,
            localCount = requests.count { !it.isSynced },
            syncedCount = requests.count { it.isSynced },
            filteredCount = filtered.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RequestListUiState())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateAddress(v: String) {
        _formState.update {
            it.copy(
                address = v,
                addressError = if (v.isBlank()) "Введите адрес" else null
            )
        }
    }

    fun updateResponseCenter(v: String) {
        _formState.update { it.copy(responseCenter = v) }
    }

    fun updateDivision(v: String) {
        _formState.update { it.copy(division = v) }
    }

    fun updateObjectType(v: String) {
        _formState.update { it.copy(objectType = v) }
    }

    fun updateProblemDescription(v: String) {
        _formState.update { it.copy(problemDescription = v) }
    }

    fun updateClientContacts(v: String) {
        _formState.update {
            it.copy(
                clientContacts = v,
                clientContactsError = if (v.isBlank()) "Введите контакты" else null
            )
        }
    }

    fun updateManager(v: String) {
        _formState.update { it.copy(manager = v) }
    }

    fun validateForm(): Boolean {
        val current = _formState.value
        val orderErr = if (current.orderNumber.isBlank()) "Введите номер ОБ" else null
        val addrErr = if (current.address.isBlank()) "Введите адрес" else null
        val contactErr = if (current.clientContacts.isBlank()) "Введите контакты клиента" else null

        _formState.update {
            it.copy(
                orderNumberError = orderErr,
                addressError = addrErr,
                clientContactsError = contactErr
            )
        }

        return orderErr == null && addrErr == null && contactErr == null
    }

    fun clearForm() {
        _formState.value = RequestFormState()
        _errorMessage = null
        _isSuccess = false
    }

    fun saveRequest() {
        if (!validateForm()) return

        viewModelScope.launch {
            _isLoading = true
            _errorMessage = null
            _isSuccess = false

            try {
                val state = _formState.value
                val currentTime = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US).format(Date())
                val requestEntity = RequestEntity(
                    orderNumber = state.orderNumber,
                    address = state.address,
                    responseCenter = state.responseCenter,
                    division = state.division,
                    objectType = state.objectType,
                    problemDescription = state.problemDescription,
                    clientContacts = state.clientContacts,
                    manager = state.manager,
                    requestDate = currentTime,
                )

                val id = repository.insertRequest(requestEntity.toDomainRequest())

                try {
                    repository.syncRequest(id)
                } catch (e: Exception) {
                    _errorMessage = "Сохранено локально. Ошибка сети: ${e.message}"
                }

                _isSuccess = true
                clearForm()

            } catch (e: Exception) {
                _errorMessage = "Критическая ошибка: ${e.message}"
            } finally {
                _isLoading = false
            }
        }
    }

    fun deleteRequest(id: Long) {
        viewModelScope.launch {
            repository.deleteRequest(id)
        }
    }

    fun retrySync(id: Long) {
        viewModelScope.launch {
            try {
                repository.syncRequest(id)
            } catch (e: Exception) {
                _errorMessage = "Ошибка синхронизации: ${e.message}"
            }
        }
    }

    fun updateOrderNumber(v: String) {
        _formState.update {
            it.copy(
                orderNumber = v,
                orderNumberError = if (v.isBlank()) "Введите номер ОБ" else null
            )
        }
    }

    fun resetSuccess() {
        _isSuccess = false
    }
}
