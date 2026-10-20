package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextList(items: List<String>) {
    LazyColumn {
        items(items.size) { index ->
            Card(
                modifier = Modifier,
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            ) {
                Text(items[index])
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
