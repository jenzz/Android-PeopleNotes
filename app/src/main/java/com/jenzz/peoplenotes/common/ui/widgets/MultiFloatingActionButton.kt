package com.jenzz.peoplenotes.common.ui.widgets

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.widgets.MultiFloatingActionButtonState.Collapsed
import com.jenzz.peoplenotes.common.ui.widgets.MultiFloatingActionButtonState.Expanded
import com.jenzz.peoplenotes.ext.noRippleClickable
import com.jenzz.peoplenotes.ext.thenIf

@Composable
fun MultiFloatingActionButton(
    state: MultiFloatingActionButtonState,
    onStateChange: (MultiFloatingActionButtonState) -> Unit,
    items: List<MultiFloatingActionButtonItem>,
    onItemClick: (MultiFloatingActionButtonItem) -> Unit,
) {
    fun notifyStateChange() {
        val newState = if (state == Collapsed) Expanded else Collapsed
        onStateChange(newState)
    }

    val rotation by animateFloatAsState(
        targetValue = if (state == Expanded) 45f else 0f
    )
    Column(
        horizontalAlignment = Alignment.End,
    ) {
        AnimatedVisibility(
            visible = state == Expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column(
                modifier = Modifier.padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items.forEach { item ->
                    FloatingActionButton(
                        onClick = {
                            notifyStateChange()
                            onItemClick(item)
                        },
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                            Text(text = item.label)
                        }
                    }
                }
            }
        }
        FloatingActionButton(onClick = { notifyStateChange() }) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = stringResource(id = R.string.add),
                modifier = Modifier.rotate(rotation),
            )
        }
    }
}

@Composable
fun MultiFloatingActionButtonContentOverlay(
    modifier: Modifier = Modifier,
    state: MutableState<MultiFloatingActionButtonState>
) {
    val alpha = if (state.value == Expanded) 0.20f else 0f
    Box(
        modifier = modifier
            .thenIf(state.value == Expanded) {
                noRippleClickable(
                    onClick = { state.value = Collapsed },
                )
            }
            .alpha(animateFloatAsState(alpha).value)
            .background(Color.Black)
    )
}

enum class MultiFloatingActionButtonState {

    Collapsed,
    Expanded,
}

data class MultiFloatingActionButtonItem(
    val id: Int,
    val label: String,
    val icon: ImageVector,
)
