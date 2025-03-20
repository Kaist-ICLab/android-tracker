package com.example.listener_test_app

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(
//                    this, Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.POST_NOTIFICATIONS, Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
//                    1
//                )
//            }
//        }
//
//        enableEdgeToEdge()
//        setContent {
//            AndroidtrackerTheme {
//                ListenerTestApp()
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AppBar(
//    title: String,
//    changePageIndex: (Int) -> Unit,
//) {
//    var showDropDownMenu by remember { mutableStateOf(false) }
//
//    TopAppBar(
//        title = { Text(text = title) },
//        actions = {
//            IconButton(onClick = { showDropDownMenu = true }) {
//                Icon(Icons.Filled.MoreVert, null)
//            }
//            DropdownMenu(
//                expanded = showDropDownMenu,
//                onDismissRequest = { showDropDownMenu = false }
//            ) {
//                DropdownMenuItem(
//                    text = { Text(text = "Notification") },
//                    onClick = {
//                        changePageIndex(0)
//                        showDropDownMenu = false
//                    }
//                )
//                DropdownMenuItem(
//                    text = { Text(text = "Accessibility") },
//                    onClick = {
//                        changePageIndex(1)
//                        showDropDownMenu = false
//                    }
//                )
//            }
//        },
//    )
//}
//
//@SuppressLint("BatteryLife")
//@Composable
//fun ListenerTestApp(
//    modifier: Modifier = Modifier
//) {
//    val notificationViewModel: NotificationViewModel = viewModel()
//    val accessibilityViewModel: AccessibilityViewModel = viewModel()
//    var pageIndex by remember { mutableIntStateOf(0) }
//
//    val appTitle = listOf("Notification Test",  "Accessibility Test")
//
//    Scaffold(
//        topBar = {
//            AppBar(
//                title = appTitle[pageIndex],
//                changePageIndex = { pageIndex = it }
//            ) },
//
//        modifier = Modifier.fillMaxSize(),
//        content = { innerPadding ->
//            when(pageIndex) {
//                0 -> NotificationTest(
//                    viewModel = notificationViewModel,
//                    modifier = Modifier.padding(innerPadding)
//                )
//                else -> AccessibilityTest(
//                    viewModel = accessibilityViewModel,
//                    modifier = Modifier.padding(innerPadding)
//                )
//            }
//        }
//    )
//}
//
//@Composable
//@Preview(showBackground = true)
//fun AppBarPreview() {
//    MaterialTheme {
//        AppBar(
//            title = "Testing Test",
//            changePageIndex = {}
//        )
//    }
//}