package com.example.skylogic.view.mapSelectionView

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.util.GeoPoint

fun createCustomMarkerIcon(context: Context): BitmapDrawable {
    val size = 120
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val cx = size / 2f
    val pinBottom = size.toFloat() - 8f
    val pinTop = 18f
    val pinRadius = 28f

    val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = RadialGradient(
            cx, cx,
            pinRadius + 20f,
            intArrayOf(
                Color.argb(80, 77, 163, 255),
                Color.argb(30, 77, 163, 255),
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.6f, 1f),
            Shader.TileMode.CLAMP
        )
    }
    canvas.drawCircle(cx, cx, pinRadius + 20f, glowPaint)

    val pinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(77, 163, 255)
        style = Paint.Style.FILL
    }
    canvas.drawCircle(cx, pinTop + pinRadius, pinRadius, pinPaint)

    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    canvas.drawCircle(cx, pinTop + pinRadius, pinRadius, strokePaint)

    val tailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(77, 163, 255)
    }
    val path = Path().apply {
        moveTo(cx - 12f, pinTop + pinRadius + 16f)
        lineTo(cx + 12f, pinTop + pinRadius + 16f)
        lineTo(cx, pinBottom)
        close()
    }
    canvas.drawPath(path, tailPaint)

    val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    canvas.drawCircle(cx, pinTop + pinRadius, 10f, dotPaint)

    return BitmapDrawable(context.resources, bitmap)
}

fun animateMarkerDrop(mapView: MapView, marker: Marker, finalPoint: GeoPoint) {

    val startLat = finalPoint.latitude + 8.0
    val endLat   = finalPoint.latitude

    val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 700
        interpolator = BounceInterpolator()

        addUpdateListener { anim ->
            val progress = anim.animatedValue as Float
            val currentLat = startLat + (endLat - startLat) * progress
            marker.position = GeoPoint(currentLat, finalPoint.longitude)
            mapView.invalidate()
        }
    }
    animator.start()
}

fun animateMarkerPulse(mapView: MapView, marker: Marker) {
    val scaleUp = ValueAnimator.ofFloat(1f, 1.4f).apply {
        duration = 200
        interpolator = DecelerateInterpolator()
        addUpdateListener {
            val scale = it.animatedValue as Float
            marker.alpha = scale - 0.2f
            mapView.invalidate()
        }
    }

    val scaleDown = ValueAnimator.ofFloat(1.4f, 1f).apply {
        duration = 300
        interpolator = DecelerateInterpolator()
        addUpdateListener {
            val scale = it.animatedValue as Float
            marker.alpha = scale - 0.2f
            mapView.invalidate()
        }
    }

    val normalAlpha = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1
        addUpdateListener {
            marker.alpha = 1f
            mapView.invalidate()
        }
    }

    AnimatorSet().apply {
        playSequentially(scaleUp, scaleDown, normalAlpha)
        startDelay = 700
        start()
    }
}

fun placeEnhancedMarker(
    context: Context,
    mapView: MapView,
    point: GeoPoint,
    title: String,
    currentMarker: Marker?
): Marker {

    currentMarker?.let { mapView.overlays.remove(it) }

    val newMarker = Marker(mapView).apply {
        position    = GeoPoint(point.latitude + 8.0, point.longitude)
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        this.title  = title
        icon        = createCustomMarkerIcon(context)
        showInfoWindow()
    }

    mapView.overlays.add(newMarker)

    animateMarkerDrop(mapView, newMarker, point)
    animateMarkerPulse(mapView, newMarker)

    mapView.invalidate()
    return newMarker
}