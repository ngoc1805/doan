package ui.compoment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import viewmodel.DerpartmentViewModel

@Composable
fun DepartmentTab(onAddClick: () -> Unit) {
    val departmentViewModel = remember { DerpartmentViewModel() }
    LaunchedEffect(Unit) {
        departmentViewModel.fetchDepartment()
    }
    val departments by departmentViewModel.departments.collectAsState()

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Danh sách khoa",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF1E88E5),
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { departmentViewModel.fetchDepartment() },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Image(
                    painter = painterResource("Icons/refresh.png"),
                    contentDescription = "Refresh",
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onAddClick,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1E88E5)),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            departments?.let { departmentList ->
                items(departmentList) { department ->
                    DepartmentCard(
                        id = department.id,
                        title = department.name,
                        detail = department.description
                    )
                }
            }
        }
    }
}