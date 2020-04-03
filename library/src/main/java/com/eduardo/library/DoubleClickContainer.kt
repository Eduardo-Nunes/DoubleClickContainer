package com.eduardo.library

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.transition.Fade
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import kotlinx.coroutines.*
import org.jetbrains.anko.backgroundResource
import kotlin.math.max
import kotlin.math.roundToInt

private const val REVEAL_DELAY = 450L
private const val START_REVEAL_RADIUS = 15
private const val BACKGROUND_REVEAL_TAG = "reveal_background"

class DoubleClickContainer : RelativeLayout {

    var clicksInterface: IDoubleClickCallback? = null

    private var orientation: Int? = null
    private var resetUIFeedbackJob: Job? = null

    private val gestureListener = object : GestureDetector.OnDoubleTapListener {
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            handleEventEvent(e)
            clicksInterface?.onDoubleClick()
            return false
        }

        override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
            return false
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            handleEventEvent(e)
            clicksInterface?.onSingleClick()
            return false
        }
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val attributeSet = context.obtainStyledAttributes(
            attrs, R.styleable.DoubleClickContainer, defStyle, 0
        )

        orientation = attributeSet.getInt(
            R.styleable.DoubleClickContainer_orientation, 2
        )

        attributeSet.recycle()

        setupListeners()
    }

    private fun handleEventEvent(e: MotionEvent?) {
        resetUIFeedbackJob?.cancel()
        resetUIFeedbackJob = null
        revealDoubleClickBackground(e)
    }

    private fun revealDoubleClickBackground(event: MotionEvent?) {
        if (event == null) {
            performClick()
            backgroundResource = getBackgroundResource()
            resetUiFeedBack()
            return
        }

        val revealLayout = View(context).apply {
            backgroundResource = getBackgroundResource()
            tag = BACKGROUND_REVEAL_TAG
        }
        val revealFinalX = measuredWidth
        val revealFinalY = measuredHeight

        val centerX = event.x.roundToInt()
        val centerY = event.y.roundToInt()

        val finalRadius = max(revealFinalX, revealFinalY).toFloat()
        addView(revealLayout)
        val revealAnimation = ViewAnimationUtils.createCircularReveal(
            revealLayout,
            centerX,
            centerY,
            convertDpToPx(START_REVEAL_RADIUS),
            finalRadius
        )
        revealAnimation.interpolator = AccelerateDecelerateInterpolator()
        revealAnimation.duration = REVEAL_DELAY

        revealAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)

                revealLayout.postOnAnimationDelayed({
                    TransitionManager.beginDelayedTransition(this@DoubleClickContainer, Fade())
                    revealLayout.visibility = ViewGroup.GONE
                }, REVEAL_DELAY.div(2))

                resetUiFeedBack()
            }
        })

        revealAnimation.start()
    }

    private fun resetUiFeedBack() {
        resetUIFeedbackJob?.cancel()
        resetUIFeedbackJob = null
        resetUIFeedbackJob = CoroutineScope(Dispatchers.Default).launch {
            delay(REVEAL_DELAY.div(2))
            withContext(Dispatchers.Main) {
                hide()
            }
        }
    }

    private fun hide() {
        resetUIFeedbackJob?.cancel()
        background = null
    }

    private fun setupListeners() {
        setupUIFeedback()
        setDoubleClickGestureDetector(gestureListener)
    }

    private fun setupUIFeedback() {
        isFocusable = true
        isClickable = true
    }

    private fun getBackgroundResource(): Int {
        return when (orientation) {
            DirectionMode.FORWARD.type -> R.drawable.right_background
            DirectionMode.REWIND.type -> R.drawable.left_background
            else -> R.color.light_white_16
        }
    }
}
