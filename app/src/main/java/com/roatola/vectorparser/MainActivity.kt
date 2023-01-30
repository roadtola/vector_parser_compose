package com.roatola.vectorparser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roatola.vectorchildfinder.ui.theme.VectorChildFinderTheme
import com.roatola.vectorparsercompose.VectorParser
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VectorChildFinderTheme {

                val muscles by remember { mutableStateOf(populateList()) }
                var selectedMuscle by remember(muscles) {
                    mutableStateOf(muscles.firstOrNull { it.id == 4 } ?: MuscleModel())
                }

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Row(
                        Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        VectorParser(
                            modifier = Modifier
                                .weight(0.5f)
                                .height(400.dp),
                            vectorResource = R.drawable.ic_front_muscles,
                            contentDescription = "muscle man",
                            contentScale = ContentScale.FillBounds
                        ) { paths, group ->
                            // on every render new paths and groups are created
                            group.forEach {
                                it.value.setAlpha(0.3f)
                            }
                            group[selectedMuscle.string.string]?.setAlpha(0.9f)
                        }

                        MuscleList(
                            modifier = Modifier
                                .weight(0.5f)
                                .height(400.dp),
                            muscleList = muscles,
                            selectedIndex = selectedMuscle,
                            onSelected = {
                                selectedMuscle = it
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun MuscleList(
        modifier: Modifier,
        muscleList: List<MuscleModel>,
        selectedIndex: MuscleModel,
        onSelected: (MuscleModel) -> Unit
    ) {

        val lazyListState = rememberLazyListState()

        onSelected(if (selectedIndex.id == -1) {
            LaunchedEffect(Unit) {
                lazyListState.scrollToItem(0)
            }
            muscleList[3]
        } else selectedIndex)


        val nestedScrollConnectionFront =
            object : NestedScrollConnection {
                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    if (lazyListState.firstVisibleItemIndex > 0) {
                        val newSelectedScrollItem = lazyListState.firstVisibleItemIndex + 3
                        onSelected(muscleList[newSelectedScrollItem])
                    }

                    return super.onPostScroll(consumed, available, source)
                }

            }

        //when selected item from the list, scroll to that item
        val positionList = muscleList.filter { selectedIndex.id == it.id }
        if (positionList.isNotEmpty()) {
            val position = muscleList.indexOf(positionList[0])
            if (position != -1)
                LaunchedEffect(key1 = position) {
                    // don't scroll if item is in top 3 places and last 3 places, else scroll item to the middle of the screen when selected
                    delay(200)
                    val newPosition = when (position) {
                        in 0..3 -> position
                        in muscleList.size - 3 until muscleList.size -> position
                        else -> position - 3
                    }

                    if (position > 3)
                        lazyListState.animateScrollToItem(index = newPosition)
                }
        }

        LazyColumn(
            modifier = modifier
                .nestedScroll(nestedScrollConnectionFront)
                .padding(start = 20.dp),
            horizontalAlignment = Alignment.End, state = lazyListState
        ) {
            items(muscleList) { item ->

                val spacerColor =
                    if (item.id == selectedIndex.id) MaterialTheme.colors.surface else Color.Transparent
                val animSpacer =
                    animateColorAsState(targetValue = spacerColor, animationSpec = tween(300, 100))

                val textColor =
                    if (item.id == selectedIndex.id) Color.Black else MaterialTheme.colors.secondaryVariant
                val animColor =
                    animateColorAsState(targetValue = textColor, animationSpec = tween(500))

                val textTransition = updateTransition(
                    targetState = item.id == selectedIndex.id,
                    label = "text transition"
                )
                val textOffset by textTransition.animateOffset(
                    transitionSpec = {
                        tween(500)
                    }, label = "text offset"
                ) { animated ->
                    if (animated) Offset(x = -15f, y = 0f) else Offset(x = 0f, 0f)

                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(animSpacer.value)
                )

                Text(
                    text = item.name,
                    fontSize = if (item.id == selectedIndex.id) 24.sp else 22.sp,
                    fontFamily = robotoFont,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 5.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                        .selectable(
                            onClick = {
                                onSelected(item)
                            },
                            selected = selectedIndex.id == item.id
                        )
                        .offset(textOffset.x.dp, textOffset.y.dp),
                    textAlign = TextAlign.Start,
                    color = animColor.value
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(animSpacer.value)
                )
            }
        }
    }

    data class MuscleModel(
        val id: Int = 0,
        val name: String = "",
        val string: Muscles = Muscles.NECK
    )

    enum class Muscles(val string: String) {
        NECK("neck"),
        TRAP("trap"),
        CHEST("chest"),
        CHEST_ABS("chest_abs"),
        UPPER_ABS("upper_abs"),
        MIDDLE_ABS("middle_abs"),
        LOWER_ABS("lower_abs"),
        SIDE_ABS("side_abs"),
        QUAD("quad"),
        CALF("calf"),
        DELT("delt"),
        BICEPS("biceps"),
        FOREARM("forearm"),
        UNKNOWN("unknown");

        companion object {
            fun byNameIgnoreCaseOrNull(input: String): Muscles {
                return values().firstOrNull { it.string == input } ?: UNKNOWN
            }
        }
    }

    private fun populateList(): List<MuscleModel> {
        val res = mutableListOf<MuscleModel>()

        res.add(
            MuscleModel(
                id = 1,
                name = "Neck",
                string = Muscles.NECK
            )
        )
        res.add(
            MuscleModel(
                id = 2,
                name = "Trap",
                string = Muscles.TRAP
            )
        )

        res.add(
            MuscleModel(
                id = 3,
                name = "Chest",
                string = Muscles.CHEST
            )
        )

        res.add(
            MuscleModel(
                id = 4,
                name = "Chest Abs",
                string = Muscles.CHEST_ABS
            )
        )

        res.add(
            MuscleModel(
                id = 5,
                name = "Upper Abs",
                string = Muscles.UPPER_ABS
            )
        )
        res.add(
            MuscleModel(
                id = 6,
                name = "Middle Abs",
                string = Muscles.MIDDLE_ABS
            )
        )
        res.add(
            MuscleModel(
                id = 7,
                name = "Lower Abs",
                string = Muscles.LOWER_ABS
            )
        )
        res.add(
            MuscleModel(
                id = 8,
                name = "Side Abs",
                string = Muscles.SIDE_ABS
            )
        )
        res.add(
            MuscleModel(
                id = 9,
                name = "Quads",
                string = Muscles.QUAD
            )
        )

        res.add(
            MuscleModel(
                id = 10,
                name = "Calves",
                string = Muscles.CALF
            )
        )

        res.add(
            MuscleModel(
                id = 11,
                name = "Delts",
                string = Muscles.DELT
            )
        )
        res.add(
            MuscleModel(
                id = 12,
                name = "Biceps",
                string = Muscles.BICEPS
            )
        )
        res.add(
            MuscleModel(
                id = 13,
                name = "Forearms",
                string = Muscles.FOREARM
            )
        )

        return res

    }

    private val robotoFont = FontFamily(
        Font(R.font.roboto_slab_bold, weight = FontWeight.Bold),
        Font(R.font.roboto_slab_regular, weight = FontWeight.Normal),
        Font(R.font.roboto_slab_black, weight = FontWeight.Black),
        Font(R.font.roboto_slab_semibold, weight = FontWeight.Medium),
        Font(R.font.roboto_slab_light, weight = FontWeight.Light)
    )
}