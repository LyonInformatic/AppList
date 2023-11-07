package com.example.myapplication

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TaskListScreen(viewModel = TaskViewModel(application))
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.allTasks.collectAsState(initial = emptyList())
    var newTaskName by remember { mutableStateOf("") }
    var isDialogOpen by remember { mutableStateOf(false) }

    // Combina todas las tareas en una sola lista y ordénalas por estado (completadas o no completadas)
    val allTasks = tasks.sortedBy { it.isCompleted }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(top = 20.dp)
            .padding(bottom = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        if (isDialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    isDialogOpen = false
                },
                title = {
                    Text(stringResource(id = R.string.Text_title))
                },
                text = {
                    Column {
                        TextField(
                            value = newTaskName,
                            onValueChange = { newTaskName = it },
                            label = { Text(stringResource(id = R.string.Text_label)) },
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTaskName.isNotEmpty()) {
                                val newTask = Task(newTaskName)
                                viewModel.insertTask(newTask)
                                isDialogOpen = false
                                newTaskName = ""
                            }
                        }
                    ) {
                        Text(stringResource(id = R.string.button_add))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            isDialogOpen = false
                        }
                    ) {
                        Text(stringResource(id = R.string.button_cancel))
                    }
                }
            )
        }

        if (allTasks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart, // Ícono de lista vacía
                        contentDescription = "Lista vacía",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray // Puedes ajustar el color a tu preferencia
                    )
                    Text(stringResource(id = R.string.Text_box))
                }
            }
        } else {
            LazyColumn {
                items(allTasks) { task ->
                    TaskItem(task, onTaskClicked = { updatedTask ->
                        viewModel.updateTask(updatedTask)
                    }, onDeleteClicked = { taskToDelete ->
                        viewModel.deleteTask(taskToDelete)
                    })
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(bottom= 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = {
                isDialogOpen = true
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
                Text(stringResource(id = R.string.button_add))
            }
        }
    }
}



@Composable
fun TaskItem(task: Task, onTaskClicked: (Task) -> Unit, onDeleteClicked: (Task) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onTaskClicked(task) },
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

            )
        {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = {
                    // Aquí puedes actualizar el estado de la tarea como completada o no completada
                    val updatedTask = task.copy(isCompleted = it)
                    onTaskClicked(updatedTask)
                }
            )

            Text(
                text = task.title,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),

                )
            IconButton(
                onClick = {
                    onDeleteClicked(task) // Llama a la función onDeleteClicked con la tarea a eliminar
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete, // Ícono de basurero
                    contentDescription = "Eliminar"
                )
            }
        }
    }
}












