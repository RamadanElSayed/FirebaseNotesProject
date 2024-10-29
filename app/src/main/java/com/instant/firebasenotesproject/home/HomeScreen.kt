import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.instant.firebasenotesproject.home.HomeUiEffect
import com.instant.firebasenotesproject.home.HomeUiIntent
import com.instant.firebasenotesproject.home.HomeViewModel
import com.instant.firebasenotesproject.model.Note
import com.instant.firebasenotesproject.model.NoteStatus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    fullName: String,
    title: String,
    onEditNote: (String) -> Unit,
    onAddNote: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(HomeUiIntent.LoadNotes)
    }

    LaunchedEffect(viewModel) {
        viewModel.effectFlow.collectLatest { effect ->
            when (effect) {
                is HomeUiEffect.NavigateToEditNote -> onEditNote(effect.noteId)
                is HomeUiEffect.NavigateToAddNote -> onAddNote()
                is HomeUiEffect.NavigateToLogin -> onLogout()
                is HomeUiEffect.ShowError -> { /* Handle error if needed */
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { viewModel.handleIntent(HomeUiIntent.AddNote) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Note")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image and Details
                Row(
                    modifier = Modifier
                        //  .padding(16.dp)
                        .align(Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    ) {
                        uiState.userProfile?.profileImageUrl?.let { uri ->
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Welcome, ${uiState.userProfile?.firstName.orEmpty()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.userProfile?.jobTitle.orEmpty(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    // Sign-Out Button with Icon
                    TextButton(
                        onClick = { viewModel.handleIntent(HomeUiIntent.Logout) },
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        // Text("Sign Out", color = MaterialTheme.colorScheme.error)
                    }
                }


                // Tabs and Content
                val tabs = listOf("TODO", "In Progress", "Done")
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { coroutineScope.launch { pagerState.scrollToPage(index) } },
                            text = { Text(title) }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    pageSpacing = 16.dp
                ) { page ->
                    when (page) {
                        0 -> NoteList(
                            uiState.notes.filter { it.status == NoteStatus.TODO },
                            "TODO",
                            viewModel
                        )

                        1 -> NoteList(
                            uiState.notes.filter { it.status == NoteStatus.IN_PROGRESS },
                            "In Progress",
                            viewModel
                        )

                        2 -> NoteList(
                            uiState.notes.filter { it.status == NoteStatus.DONE },
                            "Done",
                            viewModel
                        )
                    }
                }
            }
        }

        // Loading Overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}


@Composable
fun NoteList(notes: List<Note>, status: String, viewModel: HomeViewModel) {
    if (notes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "There are no $status notes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(notes) { note ->
                NoteItem(
                    note = note,
                    onStatusChange = { newStatus ->
                        viewModel.handleIntent(HomeUiIntent.UpdateNoteStatus(note.id, newStatus))
                    },
                    onEdit = {
                        viewModel.handleIntent(HomeUiIntent.EditNote(note.id))
                    },
                    onDelete = {
                        viewModel.handleIntent(HomeUiIntent.DeleteNote(note.id))
                    }
                )
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onStatusChange: (NoteStatus) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = note.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = note.description)
            Text(
                text = "${note.date}, ${note.time}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    TextButton(onClick = { expanded = true }) {
                        Text(text = note.status.name)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("TODO") },
                            onClick = {
                                expanded = false
                                onStatusChange(NoteStatus.TODO)
                            },
                            leadingIcon = { Icon(Icons.Default.Check, contentDescription = "TODO") }
                        )
                        DropdownMenuItem(
                            text = { Text("In Progress") },
                            onClick = {
                                expanded = false
                                onStatusChange(NoteStatus.IN_PROGRESS)
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Build,
                                    contentDescription = "In Progress"
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Done") },
                            onClick = {
                                expanded = false
                                onStatusChange(NoteStatus.DONE)
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Done"
                                )
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                expanded = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }

                TextButton(onClick = { onEdit() }) {
                    Text("Edit")
                }
            }
        }
    }
}
