package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Client
import com.example.data.model.Project
import com.example.data.model.ProjectTask
import com.example.ui.theme.NaxiraLavender
import com.example.ui.theme.NaxiraLavenderSoft
import com.example.ui.theme.NaxiraPrimaryPurple
import com.example.ui.theme.NaxiraSecondaryPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenSoft
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import com.example.ui.viewmodel.NaxiraViewModel

@Composable
fun ProjectsScreen(viewModel: NaxiraViewModel) {
    val context = LocalContext.current
    val projects by viewModel.projectsState.collectAsState()
    val clients by viewModel.clientsState.collectAsState()

    var selectedStatusFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var projectToEdit by remember { mutableStateOf<Project?>(null) }

    val statuses = listOf("All", "In Progress", "Review", "Completed", "On Hold", "Not Started")

    val filteredProjects = projects.filter {
        if (selectedStatusFilter == "All") true else it.status.equals(selectedStatusFilter, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("projects_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Status Filters
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statuses) { status ->
                    FilterChip(
                        selected = (selectedStatusFilter == status),
                        onClick = { selectedStatusFilter = status },
                        label = { Text(status, fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NaxiraSecondaryPurple,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (filteredProjects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = NaxiraLavender, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No projects in this stage", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            "Track client deliverables, milestones, and task checklists across Web, App, and SEO engagements.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProjects) { project ->
                        ProjectCard(
                            project = project,
                            onEdit = { projectToEdit = project },
                            onToggleTask = { taskIndex ->
                                val updatedTasks = project.tasks.toMutableList()
                                val current = updatedTasks[taskIndex]
                                updatedTasks[taskIndex] = current.copy(isCompleted = !current.isCompleted)
                                val completedCount = updatedTasks.count { it.isCompleted }
                                val progress = if (updatedTasks.isNotEmpty()) (completedCount * 100) / updatedTasks.size else project.progressPercentage
                                viewModel.updateProject(project.copy(tasks = updatedTasks, progressPercentage = progress))
                            }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = NaxiraPrimaryPurple,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_project_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Project")
        }
    }

    if (showAddDialog || projectToEdit != null) {
        AddEditProjectDialog(
            clients = clients,
            existingProject = projectToEdit,
            onDismiss = {
                showAddDialog = false
                projectToEdit = null
            },
            onSave = { project ->
                if (projectToEdit != null) {
                    viewModel.updateProject(project)
                } else {
                    viewModel.addProject(project)
                }
                showAddDialog = false
                projectToEdit = null
                Toast.makeText(context, "Project updated!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    onEdit: () -> Unit,
    onToggleTask: (Int) -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.projectName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${project.clientName} • ${project.category}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val (statusBg, statusFg) = when (project.status) {
                    "Completed" -> SuccessGreenSoft to SuccessGreen
                    "Review" -> WarningAmberSoft to WarningAmber
                    else -> NaxiraLavenderSoft to NaxiraPrimaryPurple
                }
                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = project.status, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = statusFg)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Progress: ${project.progressPercentage}%", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                if (project.deadline.isNotBlank()) {
                    Text("Deadline: ${project.deadline}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { project.progressPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = NaxiraSecondaryPurple,
                trackColor = NaxiraLavenderSoft
            )

            // Tasks checklist preview
            if (project.tasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    project.tasks.take(3).forEachIndexed { index, task ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleTask(index) }
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { onToggleTask(index) },
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = task.title,
                                fontSize = 11.5.sp,
                                color = if (task.isCompleted) Color.Gray else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (project.budget > 0) "Budget: ₹ ${String.format("%,.0f", project.budget)}" else "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaxiraPrimaryPurple
                )
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NaxiraSecondaryPurple, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun AddEditProjectDialog(
    clients: List<Client>,
    existingProject: Project? = null,
    onDismiss: () -> Unit,
    onSave: (Project) -> Unit
) {
    var projectName by remember { mutableStateOf(existingProject?.projectName ?: "") }
    var selectedClientId by remember { mutableStateOf(existingProject?.clientId ?: (clients.firstOrNull()?.id ?: "")) }
    var category by remember { mutableStateOf(existingProject?.category ?: "Website Development") }
    var budgetInput by remember { mutableStateOf(if (existingProject != null) "${existingProject.budget.toInt()}" else "35000") }
    var deadline by remember { mutableStateOf(existingProject?.deadline ?: "30 days") }
    var status by remember { mutableStateOf(existingProject?.status ?: "In Progress") }
    var progressSlider by remember { mutableStateOf((existingProject?.progressPercentage ?: 25).toFloat()) }

    val categories = listOf("Website Development", "App Development", "SEO", "Branding & Strategy", "Meta & Google Ads", "Custom Projects")
    val statuses = listOf("Not Started", "In Progress", "Review", "Completed", "On Hold")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingProject != null) "Edit Project" else "New Agency Project", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = projectName,
                        onValueChange = { projectName = it },
                        label = { Text("Project Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    Text("Client", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(clients) { c ->
                            FilterChip(
                                selected = (selectedClientId == c.id),
                                onClick = { selectedClientId = c.id },
                                label = { Text(c.name, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                item {
                    Text("Category", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = (category == cat),
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = budgetInput,
                            onValueChange = { budgetInput = it },
                            label = { Text("Budget (₹)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = deadline,
                            onValueChange = { deadline = it },
                            label = { Text("Deadline") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
                item {
                    Text("Status", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(statuses) { st ->
                            FilterChip(
                                selected = (status == st),
                                onClick = { status = st },
                                label = { Text(st, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                item {
                    Text("Completion Progress: ${progressSlider.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Slider(
                        value = progressSlider,
                        onValueChange = { progressSlider = it },
                        valueRange = 0f..100f
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (projectName.isNotBlank()) {
                        val client = clients.find { it.id == selectedClientId }
                        val project = (existingProject ?: Project()).copy(
                            projectName = projectName.trim(),
                            clientId = selectedClientId,
                            clientName = client?.name ?: "Client",
                            category = category,
                            budget = budgetInput.toDoubleOrNull() ?: 0.0,
                            deadline = deadline.trim(),
                            status = status,
                            progressPercentage = progressSlider.toInt(),
                            tasks = existingProject?.tasks ?: listOf(
                                ProjectTask(id = "1", title = "Discovery & Design Mockups", isCompleted = true),
                                ProjectTask(id = "2", title = "Core Development & Testing", isCompleted = false),
                                ProjectTask(id = "3", title = "Client Review & Final Handover", isCompleted = false)
                            )
                        )
                        onSave(project)
                    }
                },
                enabled = projectName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NaxiraPrimaryPurple)
            ) {
                Text("Save Project")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
