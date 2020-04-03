package com.eduardo.library

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GestureDetectorCompat


fun ViewGroup.forEachChild(body: (View) -> Unit) {
    repeat(childCount - 1) { body.invoke(getChildAt(it)) }
}

fun View.removeFromParent() {
    (parent as ViewGroup).removeView(this)
}

fun View.convertDpToPx(dp: Int): Float = dp.times(context.resources.displayMetrics.density)

fun View.setDoubleClickGestureDetector(listener: GestureDetector.OnDoubleTapListener) {
    val detector = GestureDetectorCompat(context, createStubGestureDetector())

    detector.setOnDoubleTapListener(listener)

    setOnTouchListener { _, event ->
        detector.onTouchEvent(event)
    }
}

private fun createStubGestureDetector(): GestureDetector.OnGestureListener? {
    return object : GestureDetector.OnGestureListener {
        override fun onShowPress(e: MotionEvent?) {}

        override fun onSingleTapUp(e: MotionEvent?): Boolean = false

        override fun onDown(e: MotionEvent?): Boolean = false

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean = false

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean = false

        override fun onLongPress(e: MotionEvent?) {}
    }
}
