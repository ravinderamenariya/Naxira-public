package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.NaxiraDarkBackground
import com.example.ui.theme.NaxiraLavender
import com.example.ui.theme.NaxiraLavenderSoft
import com.example.ui.theme.NaxiraPrimaryPurple
import com.example.ui.theme.NaxiraSecondaryPurple
import com.example.ui.viewmodel.NaxiraViewModel

enum class AppNavDestination(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    INVOICES("Invoices", Icons.Default.Receipt),
    KHATA("Khata", Icons.Default.MenuBook),
    CLIENTS("Clients", Icons.Default.Group),
    LEADS("Leads CRM", Icons.Default.BusinessCenter),
    PROJECTS("Projects", Icons.Default.Folder),
    EXPENSES("Expenses", Icons.Default.MoneyOff),
    QUOTATIONS("Quotations", Icons.Default.Description),
    SERVICES("Services", Icons.Default.DesignServices),
    REPORTS("Reports", Icons.Default.Assessment),
    SETTINGS("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(
    viewModel: NaxiraViewModel,
    onSignOut: () -> Unit
) {
    var currentScreen by remember { mutableStateOf(AppNavDestination.DASHBOARD) }
    var showMoreSheet by remember { mutableStateOf(false) }
    var selectedClientFilterForInvoices by remember { mutableStateOf<String?>(null) }
    var selectedClientFilterForKhata by remember { mutableStateOf<String?>(null) }

    // Quick Action Dialog Triggers
    var triggerCreateInvoice by remember { mutableStateOf(false) }
    var triggerAddExpense by remember { mutableStateOf(false) }
    var triggerAddClient by remember { mutableStateOf(false) }

    // Back handling: If on secondary screen, go back to Dashboard
    BackHandler(enabled = currentScreen != AppNavDestination.DASHBOARD) {
        currentScreen = AppNavDestination.DASHBOARD
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Card(
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.naxira_logo),
                                    contentDescription = "Naxira Logo",
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "NAXIRA",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = currentScreen.label,
                                fontSize = 11.sp,
                                color = NaxiraLavender,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { currentScreen = AppNavDestination.SETTINGS },
                        modifier = Modifier.testTag("topbar_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NaxiraPrimaryPurple
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                val primaryNavItems = listOf(
                    AppNavDestination.DASHBOARD,
                    AppNavDestination.INVOICES,
                    AppNavDestination.KHATA,
                    AppNavDestination.CLIENTS
                )

                primaryNavItems.forEach { item ->
                    val isSelected = currentScreen == item
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            currentScreen = item
                            if (item != AppNavDestination.INVOICES) selectedClientFilterForInvoices = null
                            if (item != AppNavDestination.KHATA) selectedClientFilterForKhata = null
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = NaxiraPrimaryPurple,
                            indicatorColor = NaxiraPrimaryPurple,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }

                // "More" Menu Item
                val isMoreSelected = !primaryNavItems.contains(currentScreen)
                NavigationBarItem(
                    selected = isMoreSelected,
                    onClick = { showMoreSheet = true },
                    icon = { Icon(Icons.Default.Menu, contentDescription = "More") },
                    label = { Text("More", fontSize = 11.sp, fontWeight = if (isMoreSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = NaxiraPrimaryPurple,
                        indicatorColor = NaxiraPrimaryPurple,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                AppNavDestination.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToInvoices = { currentScreen = AppNavDestination.INVOICES },
                    onNavigateToKhata = { currentScreen = AppNavDestination.KHATA },
                    onNavigateToClients = { currentScreen = AppNavDestination.CLIENTS },
                    onNavigateToLeads = { currentScreen = AppNavDestination.LEADS },
                    onNavigateToExpenses = { currentScreen = AppNavDestination.EXPENSES },
                    onOpenCreateInvoice = {
                        currentScreen = AppNavDestination.INVOICES
                        triggerCreateInvoice = true
                    },
                    onOpenAddExpense = {
                        currentScreen = AppNavDestination.EXPENSES
                        triggerAddExpense = true
                    },
                    onOpenAddClient = {
                        currentScreen = AppNavDestination.CLIENTS
                        triggerAddClient = true
                    }
                )

                AppNavDestination.INVOICES -> InvoicesScreen(
                    viewModel = viewModel,
                    initialClientId = selectedClientFilterForInvoices,
                    onOpenCreateInvoiceTrigger = triggerCreateInvoice
                )

                AppNavDestination.KHATA -> KhataLedgerScreen(
                    viewModel = viewModel,
                    initialClientId = selectedClientFilterForKhata
                )

                AppNavDestination.CLIENTS -> ClientCRMScreen(
                    viewModel = viewModel,
                    onNavigateToInvoicesWithClient = { clientId ->
                        selectedClientFilterForInvoices = clientId
                        currentScreen = AppNavDestination.INVOICES
                    },
                    onNavigateToKhataWithClient = { clientId ->
                        selectedClientFilterForKhata = clientId
                        currentScreen = AppNavDestination.KHATA
                    }
                )

                AppNavDestination.LEADS -> LeadManagementScreen(
                    viewModel = viewModel,
                    onLeadConverted = {
                        currentScreen = AppNavDestination.CLIENTS
                    }
                )

                AppNavDestination.PROJECTS -> ProjectsScreen(
                    viewModel = viewModel
                )

                AppNavDestination.EXPENSES -> ExpensesScreen(
                    viewModel = viewModel,
                    onOpenAddExpenseTrigger = triggerAddExpense
                )

                AppNavDestination.QUOTATIONS -> QuotationsScreen(
                    viewModel = viewModel,
                    onNavigateToInvoices = { currentScreen = AppNavDestination.INVOICES }
                )

                AppNavDestination.SERVICES -> ServicesCatalogScreen(
                    viewModel = viewModel
                )

                AppNavDestination.REPORTS -> ReportsScreen(
                    viewModel = viewModel
                )

                AppNavDestination.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    onSignOut = onSignOut
                )
            }
        }
    }

    // "More" Modules Grid Bottom Sheet
    if (showMoreSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "NAXIRA Agency Modules",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaxiraPrimaryPurple
                )
                Spacer(modifier = Modifier.height(16.dp))

                val moreModules = listOf(
                    AppNavDestination.LEADS,
                    AppNavDestination.PROJECTS,
                    AppNavDestination.EXPENSES,
                    AppNavDestination.QUOTATIONS,
                    AppNavDestination.SERVICES,
                    AppNavDestination.REPORTS,
                    AppNavDestination.SETTINGS
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    moreModules.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { module ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (currentScreen == module) NaxiraLavenderSoft else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            currentScreen = module
                                            showMoreSheet = false
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = module.icon,
                                            contentDescription = null,
                                            tint = if (currentScreen == module) NaxiraPrimaryPurple else NaxiraSecondaryPurple,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = module.label,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (currentScreen == module) NaxiraPrimaryPurple else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
