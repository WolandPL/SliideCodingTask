package com.wolandpl.sliidecodingtask.ui.compose

import android.util.Patterns
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wolandpl.sliidecodingtask.domain.User
import com.wolandpl.sliidecodingtask.ui.R
import com.wolandpl.sliidecodingtask.ui.model.UsersViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("FunctionNaming")
@Composable
fun MainScreen(
    usersViewModel: UsersViewModel = viewModel()
) {
    var addDialogVisible by rememberSaveable { mutableStateOf(false) }
    var userToDelete by rememberSaveable { mutableStateOf<User?>(null) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp
                    ),
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name)
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .testTag(ADD_USER_BUTTON_TEST_TAG),
                onClick = {
                    addDialogVisible = true
                }
            ) {
                Icon(
                    Icons.Filled.Add,
                    stringResource(R.string.add_user)
                )
            }
        }
    ) { paddingValues ->
        if (usersViewModel.uiState.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag(LOADING_TEST_TAG)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .testTag(DATA_COLUMN_TEST_TAG)
            ) {
                val now = Calendar.getInstance().time.time

                usersViewModel.uiState.users.forEach { user ->
                    item {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            userToDelete = user
                                        }
                                    )
                                }
                                .testTag(DATA_ROW_TEST_TAG)
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(bottom = 8.dp),
                                text = user.name,
                                fontWeight = Bold,
                                fontSize = 18.sp
                            )

                            Text(
                                modifier = Modifier
                                    .padding(bottom = 8.dp),
                                text = user.email
                            )

                            Text(
                                modifier = Modifier
                                    .padding(bottom = 8.dp),
                                text = if (user.creationTime == -1L) {
                                    stringResource(id = R.string.creation_time_unknown)
                                } else {
                                    stringResource(
                                        id = R.string.creation_time,
                                        (now - user.creationTime) / 1000
                                    )
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        if (addDialogVisible) {
            var name by rememberSaveable { mutableStateOf("") }
            var email by rememberSaveable { mutableStateOf("") }

            var nameError by rememberSaveable { mutableStateOf("") }
            var emailError by rememberSaveable { mutableStateOf("") }

            val localContext = LocalContext.current

            Dialog(
                onDismissRequest = {
                    addDialogVisible = false
                }
            ) {
                Surface(
                    modifier = Modifier
                        .testTag(ADD_USER_DIALOG_TEST_TAG),
                    shape = RoundedCornerShape(10)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(vertical = 16.dp),
                            text = stringResource(R.string.enter_user_data),
                            fontWeight = Bold
                        )

                        OutlinedTextField(
                            modifier = Modifier
                                .testTag(ADD_USER_NAME_TEST_TAG),
                            value = name,
                            onValueChange = {
                                name = it

                                nameError = if (name.isEmpty()) {
                                    localContext.getString(R.string.empty_name)
                                } else {
                                    ""
                                }
                            },
                            label = {
                                Text(
                                    text = stringResource(R.string.name)
                                )
                            },
                            isError = nameError.isNotEmpty()
                        )
                        if (nameError.isNotEmpty()) {
                            Text(
                                text = nameError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }

                        OutlinedTextField(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .testTag(ADD_USER_EMAIL_TEST_TAG),
                            value = email,
                            onValueChange = {
                                email = it

                                emailError = if (email.isEmpty()) {
                                    localContext.getString(R.string.empty_email)
                                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                    localContext.getString(R.string.invalid_email)
                                } else {
                                    ""
                                }
                            },
                            label = {
                                Text(
                                    text = stringResource(R.string.email)
                                )
                            },
                            isError = emailError.isNotEmpty()
                        )
                        if (emailError.isNotEmpty()) {
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = emailError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        Button(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .testTag(ADD_USER_CONFIRM_BUTTON_TEST_TAG),
                            enabled = name.isNotEmpty() && email.isNotEmpty() &&
                                    nameError.isEmpty() && emailError.isEmpty(),
                            onClick = {
                                addDialogVisible = false
                                usersViewModel.addUser(name, email)
                            }
                        ) {
                            Text(
                                text = stringResource(id = android.R.string.ok)
                            )
                        }
                    }
                }
            }
        }

        userToDelete?.let {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Button(
                        modifier = Modifier
                            .testTag(DELETE_USER_CONFIRM_BUTTON_TEST_TAG),
                        onClick = {
                            usersViewModel.deleteUser(it)
                            userToDelete = null
                        }
                    ) {
                        Text(
                            text = stringResource(id = android.R.string.ok)
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            userToDelete = null
                        }
                    ) {
                        Text(
                            text = stringResource(id = android.R.string.cancel)
                        )
                    }
                },
                text = {
                    Text(
                        text = stringResource(R.string.delete_user_confirmation),
                        fontSize = 18.sp,
                        lineHeight = 20.sp,
                        maxLines = 2
                    )
                }
            )
        }

        usersViewModel.uiState.error?.let { errorMessage ->
            AlertDialog(
                modifier = Modifier
                    .testTag(ERROR_DIALOG_TEST_TAG),
                onDismissRequest = {
                    usersViewModel.errorShown()
                },
                confirmButton = {
                    Button(
                        onClick = {
                            usersViewModel.errorShown()
                        }
                    ) {
                        Text(
                            text = stringResource(id = android.R.string.ok)
                        )
                    }
                },
                text = {
                    Text(
                        text = errorMessage,
                        fontSize = 18.sp,
                        lineHeight = 20.sp,
                        maxLines = 2
                    )
                }
            )
        }
    }
}

const val LOADING_TEST_TAG = "LOADING_TEST_TAG"
const val DATA_COLUMN_TEST_TAG = "DATA_COLUMN_TEST_TAG"
const val DATA_ROW_TEST_TAG = "DATA_ROW_TEST_TAG"
const val ADD_USER_BUTTON_TEST_TAG = "ADD_USER_BUTTON_TEST_TAG"
const val ADD_USER_NAME_TEST_TAG = "ADD_USER_NAME_TEST_TAG"
const val ADD_USER_EMAIL_TEST_TAG = "ADD_USER_EMAIL_TEST_TAG"
const val ADD_USER_CONFIRM_BUTTON_TEST_TAG = "ADD_USER_CONFIRM_BUTTON_TEST_TAG"
const val ADD_USER_DIALOG_TEST_TAG = "ADD_USER_DIALOG_TEST_TAG"
const val DELETE_USER_CONFIRM_BUTTON_TEST_TAG = "DELETE_USER_CONFIRM_BUTTON_TEST_TAG"
const val ERROR_DIALOG_TEST_TAG = "ERROR_DIALOG_TEST_TAG"
