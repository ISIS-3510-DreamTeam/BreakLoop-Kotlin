package com.dreamteam.breakloop.ui.main.offline.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.enums.ActivityPhase
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.LoadingContent
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.Typography

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
                    .background(ColorPalette.Neutral.Snow)
                    .padding(16.dp),
               verticalArrangement = Arrangement.Center,
               horizontalAlignment = Alignment.CenterHorizontally
          ) {
               Text(
                    text = "Activity not found",
                    style = Typography.titleMedium,
                    color = ColorPalette.Neutral.t1000
               )
               TextButton(onClick = onBack) { Text("Back") }
          }

          is ActivityDetailUiState.Content -> {
               val activity = state.activity

               Column(
                    modifier = Modifier
                         .fillMaxSize()
                         .background(ColorPalette.Neutral.Snow)
                         .verticalScroll(rememberScrollState())
                         .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
               ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         IconButton(onClick = onBack) {
                              Icon(
                                   imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                   contentDescription = "Back",
                                   tint = ColorPalette.Neutral.t1000
                              )
                         }
                         Text(
                              text = "ACTIVITY",
                              style = Typography.labelSmall,
                              color = ColorPalette.Neutral.t700
                         )
                    }

                    ActivityHero(activity)

                    when (state.phase) {
                         ActivityPhase.READY -> Column(
                              verticalArrangement = Arrangement.spacedBy(12.dp)
                         ) {
                              Text(
                                   text = "Put your phone away and give this your full attention.",
                                   style = Typography.bodyMedium,
                                   color = ColorPalette.Neutral.t700,
                                   textAlign = TextAlign.Center,
                                   modifier = Modifier.fillMaxWidth()
                              )
                              BreakLoopButton(
                                   text = "Start & Log Activity",
                                   onClick = viewModel::start
                              )
                         }

                         ActivityPhase.RUNNING -> {
                              val total = activity.durationMin * 60
                              val remaining = (state.remainingSeconds / total.toFloat()).coerceIn(0f, 1f)
                              Column(
                                   modifier = Modifier.fillMaxWidth(),
                                   horizontalAlignment = Alignment.CenterHorizontally,
                                   verticalArrangement = Arrangement.spacedBy(16.dp)
                              ) {
                                   CircularTimer(
                                        remainingFraction = remaining,
                                        remainingSeconds = state.remainingSeconds
                                   )
                                   Text(
                                        text = "Put your phone face down. You've got this.",
                                        style = Typography.bodyMedium,
                                        color = ColorPalette.Neutral.t700,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                   )
                                   BreakLoopButton(
                                        text = "I'm done",
                                        onClick = viewModel::finish
                                   )
                              }
                         }

                         ActivityPhase.COMPLETED -> Column(
                              modifier = Modifier.fillMaxWidth(),
                              horizontalAlignment = Alignment.CenterHorizontally,
                              verticalArrangement = Arrangement.spacedBy(16.dp)
                         ) {
                              PixelBadge(
                                   color = ColorPalette.Fern.t100,
                                   size = 96.dp,
                                   shape = CircleShape
                              ) {
                                   Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = ColorPalette.Fern.t500,
                                        modifier = Modifier.size(52.dp)
                                   )
                              }
                              Text(
                                   text = "NICE WORK",
                                   style = Typography.labelSmall,
                                   color = ColorPalette.Fern.t500
                              )
                              Text(
                                   text = "+${activity.xp} XP earned!",
                                   style = Typography.headlineSmall,
                                   color = ColorPalette.Neutral.t1000
                              )
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

@Composable
private fun ActivityHero(activity: OfflineActivity) {
     Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp)
     ) {
          PixelBadge(
               color = categoryColor(activity.category),
               size = 112.dp,
               shape = RoundedCornerShape(28.dp)
          ) {
               Icon(
                    imageVector = categoryIcon(activity.category),
                    contentDescription = activity.category.name,
                    tint = ColorPalette.Neutral.t1000,
                    modifier = Modifier.size(56.dp)
               )
          }
          Text(
               text = activity.category.name,
               style = Typography.labelSmall,
               color = ColorPalette.SpicyPaprika.t700
          )
          Text(
               text = activity.title,
               style = Typography.headlineMedium,
               color = ColorPalette.Neutral.t1000,
               textAlign = TextAlign.Center
          )
          Text(
               text = activity.prompt,
               style = Typography.bodyMedium,
               color = ColorPalette.Neutral.t700,
               textAlign = TextAlign.Center,
               modifier = Modifier.padding(horizontal = 8.dp)
          )
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
               DetailPill(Icons.Outlined.Timer, "${activity.durationMin} MIN", ColorPalette.Neutral.t1000)
               DetailPill(Icons.Outlined.Bolt, "+${activity.xp} XP", ColorPalette.Fern.t500)
          }
     }
}

/** Ficha con borde y sombra sólida, el mismo lenguaje pixel art de BreakLoopCard pero sin la tarjeta. */
@Composable
private fun PixelBadge(
     color: Color,
     size: Dp,
     shape: androidx.compose.ui.graphics.Shape,
     content: @Composable () -> Unit,
) {
     val ink = ColorPalette.Neutral.t1000
     Box(modifier = Modifier.size(size)) {
          Box(
               Modifier
                    .matchParentSize()
                    .offset(x = 4.dp, y = 4.dp)
                    .background(ink, shape)
          )
          Box(
               modifier = Modifier
                    .matchParentSize()
                    .background(color, shape)
                    .border(2.dp, ink, shape),
               contentAlignment = Alignment.Center
          ) { content() }
     }
}

@Composable
private fun CircularTimer(remainingFraction: Float, remainingSeconds: Int) {
     val ink = ColorPalette.Neutral.t1000
     val trackColor = ColorPalette.Neutral.t100
     val progressColor = ColorPalette.SpicyPaprika.t500
     val diameter = 240.dp

     Box(
          modifier = Modifier.size(diameter),
          contentAlignment = Alignment.Center
     ) {
          // sombra sólida + disco interior
          Box(
               Modifier
                    .matchParentSize()
                    .offset(x = 4.dp, y = 4.dp)
                    .background(ink, CircleShape)
          )
          Box(
               Modifier
                    .matchParentSize()
                    .background(ColorPalette.GoldenOrange.t100, CircleShape)
                    .border(2.dp, ink, CircleShape)
          )

          Canvas(modifier = Modifier.matchParentSize().padding(14.dp)) {
               val stroke = 16.dp.toPx()
               val inset = stroke / 2
               val arcSize = Size(size.width - stroke, size.height - stroke)
               val topLeft = Offset(inset, inset)
               // pista segmentada, como las barras de vida de los juegos retro
               drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(
                         width = stroke,
                         pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 5.dp.toPx()))
                    )
               )
               drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 360f * remainingFraction,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(
                         width = stroke,
                         pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 5.dp.toPx()))
                    )
               )
          }

          Column(
               horizontalAlignment = Alignment.CenterHorizontally,
               verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
               Text(
                    text = "IN PROGRESS",
                    style = Typography.labelSmall,
                    color = ColorPalette.SpicyPaprika.t700
               )
               Text(
                    text = "%02d:%02d".format(remainingSeconds / 60, remainingSeconds % 60),
                    style = Typography.displayMedium,
                    color = ColorPalette.Neutral.t1000
               )
          }
     }
}

@Composable
private fun DetailPill(icon: ImageVector, text: String, color: Color) {
     val shape = RoundedCornerShape(50)
     Row(
          modifier = Modifier
               .border(1.5.dp, ColorPalette.Neutral.t1000, shape)
               .padding(horizontal = 10.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
     ) {
          Icon(
               imageVector = icon,
               contentDescription = null,
               tint = color,
               modifier = Modifier.size(14.dp)
          )
          Text(text = text, style = Typography.labelSmall, color = color)
     }
}
