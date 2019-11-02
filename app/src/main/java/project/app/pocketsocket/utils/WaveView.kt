package project.app.pocketsocket.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat.getColor
import project.app.pocketsocket.R
import java.util.*
import kotlin.math.min

class WaveView : RelativeLayout {

    private var waveColor: Int = 0
    private var waveStrokeWidth: Float = 0.toFloat()
    private var waveRadius: Float = 0.toFloat()
    private var waveDurationTime: Int = 0
    private var waveAmount: Int = 0
    private var waveDelay: Int = 0
    private var waveScale: Float = 0.toFloat()
    private var waveType: Int = 0
    private var paint: Paint? = null
    private var isWaveAnimationRunning = false
    private var animatorSet: AnimatorSet? = null
    private var animatorList: ArrayList<Animator>? = null
    private var waveParams: LayoutParams? = null
    private val waveViewList = ArrayList<WaveViewHelperClass>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (isInEditMode)
            return

        requireNotNull(attrs) {
            "May be attributes not provided to this view."
        }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView)
        waveColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            typedArray.getColor(R.styleable.WaveView_wv_color, getColor(context, R.color.waveColor))
        } else{
            @Suppress("DEPRECATION")
            typedArray.getColor(R.styleable.WaveView_wv_color, resources.getColor(R.color.waveColor))
        }
        waveStrokeWidth = typedArray.getDimension(
            R.styleable.WaveView_wv_strokeWidth,
            resources.getDimension(R.dimen.waveStrokeWidth)
        )
        waveRadius = typedArray.getDimension(
            R.styleable.WaveView_wv_radius,
            resources.getDimension(R.dimen.waveRadius)
        )
        waveDurationTime = typedArray.getInt(R.styleable.WaveView_wv_duration, DEFAULT_DURATION_TIME)
        waveAmount = typedArray.getInt(R.styleable.WaveView_wv_waveAmount, DEFAULT_WAVE_COUNT)
        waveScale = typedArray.getFloat(R.styleable.WaveView_wv_scale, DEFAULT_SCALE)
        waveType = typedArray.getInt(R.styleable.WaveView_wv_type, DEFAULT_FILL_TYPE)
        typedArray.recycle()

        waveDelay = waveDurationTime / waveAmount

        paint = Paint()
        paint!!.isAntiAlias = true
        if (waveType == DEFAULT_FILL_TYPE) {
            waveStrokeWidth = 0f
            paint!!.style = Paint.Style.FILL
        }
        else {
            paint!!.style = Paint.Style.STROKE
        }
        paint!!.color = waveColor

        waveParams = LayoutParams(
            (2 * (waveRadius + waveStrokeWidth)).toInt(),
            (2 * (waveRadius + waveStrokeWidth)).toInt()
        )
        waveParams!!.addRule(CENTER_IN_PARENT, TRUE)

        animatorSet = AnimatorSet()
        animatorSet!!.duration = waveDurationTime.toLong()
        animatorSet!!.interpolator = AccelerateDecelerateInterpolator()
        animatorList = ArrayList()

        for (i in 0 until waveAmount) {
            val waveView = WaveViewHelperClass(getContext())
            addView(waveView, waveParams)
            waveViewList.add(waveView)
            val scaleXAnimator = ObjectAnimator.ofFloat(waveView, "ScaleX", 1.0f, waveScale)
            scaleXAnimator.repeatCount = ObjectAnimator.INFINITE
            scaleXAnimator.repeatMode = ObjectAnimator.RESTART
            scaleXAnimator.startDelay = (i * waveDelay).toLong()
            animatorList!!.add(scaleXAnimator)
            val scaleYAnimator = ObjectAnimator.ofFloat(waveView, "ScaleY", 1.0f, waveScale)
            scaleYAnimator.repeatCount = ObjectAnimator.INFINITE
            scaleYAnimator.repeatMode = ObjectAnimator.RESTART
            scaleYAnimator.startDelay = (i * waveDelay).toLong()
            animatorList!!.add(scaleYAnimator)
            val alphaAnimator = ObjectAnimator.ofFloat(waveView, "Alpha", 1.0f, 0f)
            alphaAnimator.repeatCount = ObjectAnimator.INFINITE
            alphaAnimator.repeatMode = ObjectAnimator.RESTART
            alphaAnimator.startDelay = (i * waveDelay).toLong()
            animatorList!!.add(alphaAnimator)
        }

        animatorSet!!.playTogether(animatorList)
    }

    private inner class WaveViewHelperClass(context: Context) : View(context) {

        init {
            this.visibility = INVISIBLE
        }

        override fun onDraw(canvas: Canvas) {
            val radius = min(width, height) / 2
            canvas.drawCircle(radius.toFloat(), radius.toFloat(), radius - waveStrokeWidth, paint!!)
        }
    }

    fun startWaveAnimation() {
        if (!isWaveAnimationRunning) {
            for (waveView in waveViewList) {
                waveView.visibility = View.VISIBLE
            }
            animatorSet!!.start()
            isWaveAnimationRunning = true
        }
    }

    fun stopWaveAnimation() {
        if (isWaveAnimationRunning) {
            animatorSet!!.end()
            isWaveAnimationRunning = false
        }
    }

    companion object {

        private const val DEFAULT_WAVE_COUNT = 6
        private const val DEFAULT_DURATION_TIME = 3000
        private const val DEFAULT_SCALE = 6.0f
        private const val DEFAULT_FILL_TYPE = 0
    }
}

