package com.roatola.vectorparsercompose

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest


@Composable
fun VectorParser(
    modifier: Modifier,
    vectorResource: Int,
    contentDescription: String,
    contentScale: ContentScale = ContentScale.Fit,
    onLoad: (paths: Map<String, VectorDrawableCompat.VFullPath>, group: Map<String, VectorDrawableCompat.VGroup>) -> Unit
) {
    val context = LocalContext.current

    val vectorFinder by remember(vectorResource) {
        mutableStateOf(
            VectorChildFinder(
                context,
                vectorResource
            )
        )
    }
    val groups = vectorFinder.all
    onLoad.invoke(groups.first, groups.second)
    vectorFinder.vectorDrawable.clearColorFilter()

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(vectorFinder.vectorDrawable)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
    )
}

@Composable
fun VectorParserView(
    modifier: Modifier,
    vectorResource: Int,
    contentDescription: String,
    contentScale: ScaleType = ScaleType.FIT_XY,
    onLoad: (paths: Map<String, VectorDrawableCompat.VFullPath>, group: Map<String, VectorDrawableCompat.VGroup>) -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AndroidView(factory = { ctx ->
            val linerLayout = LinearLayout(ctx)
            linerLayout.orientation = LinearLayout.HORIZONTAL

            val image = ImageView(ctx).apply {
                setImageResource(vectorResource)
                this.contentDescription = contentDescription
                scaleType = contentScale

                val layout = LinearLayout.LayoutParams(
                    MATCH_PARENT,
                    MATCH_PARENT
                )
                layout.weight = 1f
                layoutParams = layout
            }

            linerLayout.addView(image)

            linerLayout
        }, update = { it ->
            val image = it.getChildAt(0)!!

            val vector = VectorChildFinder(
                it.context, vectorResource,
                image as ImageView?
            ).all
            onLoad(vector.first, vector.second)
        }, modifier = Modifier.matchParentSize())
    }
}













