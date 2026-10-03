package com.dreamteam.breakloop.ui.main.offline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.domain.enums.ActivityPhase
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.LoadingContent
import com.dreamteam.breakloop.ui.main.stats.components.MetricCard
import com.dreamteam.breakloop.ui.theme.ColorPalette

@Composable
fun ActivityDetailScreen(
     onBack: () -> Unit = {},
     viewModel: ActivityDetailViewModel = viewModel(),
) {
     val uiState by viewModel.uiState.collectAsStateWithLifecycle()

     when (val state = uiState) {
          ActivityDetailUiState.Loading -> LoadingContent()

          ActivityDetailUiState.NotFound -> Column(
               modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
               verticalArrangement = Arrangement.Center,
               horizontalAlignment = Alignment.CenterHorizontally
          ) {
               Text("Activity not found")
               TextButton(onClick = onBack) { Text("← Back") }
          }

          is ActivityDetailUiState.Content -> {
               val activity = state.activity

               Column(
                    modifier = Modifier
                         .fillMaxSize()
                         .background(ColorPalette.Neutral.Snow)
                         .verticalScroll(rememberScrollState())
                         .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
               ) {
                    // Encabezado
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         TextButton(onClick = onBack) { Text("← Back") }
                         Text(
                              text = "Activity Detail",
                              style = MaterialTheme.typography.headlineSmall
                         )
                    }

                    // Tarjeta principal
                    BreakLoopCard(modifier = Modifier.fillMaxWidth()) {
                         Box(
                              modifier = Modifier
                                   .size(64.dp)
                                   .background(
                                        color = categoryColor(activity.category),
                                        shape = RoundedCornerShape(4.dp)
                                   ),
                              contentAlignment = Alignment.Center
                         ) {
                              Text(
                                   text = activity.category.name.first().toString(),
                                   style = MaterialTheme.typography.headlineSmall,
                                   color = ColorPalette.Neutral.t1000
                              )
                         }
                         Text(
                              text = activity.title,
                              style = MaterialTheme.typography.headlineSmall
                         )
                         Text(
                              text = activity.category.name.uppercase(),
                              style = MaterialTheme.typography.labelSmall
                         )
                         Text(
                              text = activity.prompt,
                              style = MaterialTheme.typography.bodyMedium,
                              color = ColorPalette.Neutral.t700
                         )
                    }

                    // Métricas
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                         MetricCard(
                              label = "RECOMMENDED TIME",
                              value = "${activity.durationMin} min",
                              modifier = Modifier.weight(1f),
                              supportingText = "screen-free",
                              supportingColor = ColorPalette.Neutral.t700
                         )
                         MetricCard(
                              label = "HEARTH REWARD",
                              value = "+${activity.xp} XP",
                              modifier = Modifier.weight(1f),
                              supportingText = "on completion",
                              supportingColor = ColorPalette.Fern.t500
                         )
                    }

                    // Acción / temporizador según la fase de la actividad
                    when (state.phase) {
                         ActivityPhase.READY -> {
                              Column(
                                   verticalArrangement = Arrangement.spacedBy(8.dp)
                              ) {
                                   Text(
                                        text = "Put your phone away and give this your full attention.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ColorPalette.Neutral.t700
                                   )
                                   BreakLoopButton(
                                        text = "Start & Log Activity",
                                        onClick = viewModel::start
                                   )
                              }
                         }

                         ActivityPhase.RUNNING -> {
                              Column(
                                   verticalArrangement = Arrangement.spacedBy(12.dp)
                              ) {
                                   BreakLoopCard(modifier = Modifier.fillMaxWidth()) {
                                        Column(
                                             modifier = Modifier.fillMaxWidth(),
                                             horizontalAlignment = Alignment.CenterHorizontally,
                                             verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                             Text(
                                                  text = "IN PROGRESS",
                                                  style = MaterialTheme.typography.labelSmall,
                                                  color = ColorPalette.Neutral.t700
                                             )
                                             Text(
                                                  text = "%02d:%02d".format(
                                                       state.remainingSeconds / 60,
                                                       state.remainingSeconds % 60
                                                  ),
                                                  style = MaterialTheme.typography.displayMedium,
                                                  color = categoryColor(activity.category)
                                             )
                                             Text(
                                                  text = "Put your phone face down. You've got this.",
                                                  style = MaterialTheme.typography.bodyMedium,
                                                  color = ColorPalette.Neutral.t700,
                                                  textAlign = TextAlign.Center
                                             )
                                        }
                                   }
                                   BreakLoopButton(
                                        text = "I'm done",
                                        onClick = viewModel::finish
                                   )
                              }
                         }

                         ActivityPhase.COMPLETED -> {
                              Column(
                                   verticalArrangement = Arrangement.spacedBy(12.dp)
                              ) {
                                   BreakLoopCard(modifier = Modifier.fillMaxWidth()) {
                                        Column(
                                             modifier = Modifier.fillMaxWidth(),
                                             horizontalAlignment = Alignment.CenterHorizontally,
                                             verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                             Text(
                                                  text = "NICE WORK",
                                                  style = MaterialTheme.typography.labelSmall,
                                                  color = ColorPalette.Fern.t500
                                             )
                                             Text(
                                                  text = "+${activity.xp} XP earned!",
                                                  style = MaterialTheme.typography.headlineSmall,
                                                  color = ColorPalette.Fern.t500
                                             )
                                        }
                                   }
                                   BreakLoopButton(
                                        text = "Back to activities",
                                        onClick = onBack
                                   )
                              }
                         }
                    }
               }
          }
     }
}