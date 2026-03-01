package com.serega.requestmanager.ui.profile


import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateRequestScreen(
    onNavigateBack: () -> Unit,
    viewModel: RequestViewModel = hiltViewModel(),
) {
    val state by viewModel.formState.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading
    val isSuccess = viewModel.isSuccess
    val errorMessage = viewModel.errorMessage
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onNavigateBack()
            viewModel.resetSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Новая заявка") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    keyboardController?.hide()
                    viewModel.saveRequest()
                },
                icon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Отправить") },
                text = { Text("Отправить") },
                expanded = !viewModel.isLoading
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {

            RequestField(
                value = state.orderNumber,
                onValueChange = { viewModel.updateOrderNumber(it) },
                label = "Номер ОБ *",
                error = state.orderNumberError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            RequestField(
                value = state.address,
                onValueChange = { viewModel.updateAddress(it) },
                label = "Адрес *",
                error = state.addressError
            )

            RequestField(
                value = state.responseCenter,
                onValueChange = { viewModel.updateResponseCenter(it) },
                label = "Центр реагирования"
            )

            RequestField(
                value = state.division,
                onValueChange = { viewModel.updateDivision(it) },
                label = "Дивизион"
            )

            RequestDropdown(
                value = state.objectType,
                onValueChange = viewModel::updateObjectType,
                label = "Тип объекта",
                options = listOf("Квартира", "Офис", "Магазин", "Склад", "Другое")
            )

            RequestField(
                value = state.division,
                onValueChange = { viewModel.updateDivision(it) },
                label = "Дивизион"
            )

            RequestField(
                value = state.clientContacts,
                onValueChange = { viewModel.updateClientContacts(it) },
                label = "Контакты клиента *",
                error = state.clientContactsError
            )

            Spacer(modifier = Modifier.height(8.dp))

            RequestField(
                value = state.manager,
                onValueChange = { viewModel.updateManager(it) },
                label = "Менеджер"
            )

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            errorMessage?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = msg, modifier = Modifier.padding(16.dp))
                }
            }

            if (isSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Text(
                        text = "✅ Заявка сохранена!",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun RequestField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    singleLine: Boolean = true,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    Column(modifier = modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            isError = error != null,
            keyboardOptions = keyboardOptions
        )
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun RequestDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Box {
            OutlinedTextField(
                value = value,
                onValueChange = { },
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
            )
            // Прозрачный слой для клика
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
