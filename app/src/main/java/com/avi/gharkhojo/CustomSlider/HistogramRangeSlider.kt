package com.avi.gharkhojo.CustomSlider

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

class HistogramRangeSlider @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnRangeChangeListener {
        fun onRangeChanged(minPrice: Float, maxPrice: Float)
    }

    var onRangeChangeListener: OnRangeChangeListener? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val barWidth = 18f
    private val barSpacing = 3f
    private val thumbRadius = 50f
    private val cornerRadius = 4f
    private var histogramData: List<Float> = listOf()
    private var minAllowedValue = 0f
    private var maxAllowedValue = 1f
    private var valueRange = 1f

    private var leftThumbX = 0f
    private var rightThumbX = 0f
    private var isDraggingLeft = false
    private var isDraggingRight = false
    private var isOverlapped = false
    private var lastTouchX = 0f

    private val selectedColor = Color.parseColor("#007FFF")
    private val unselectedColor = Color.parseColor("#E4E4E4")
    private val thumbColor = Color.WHITE
    private val shadowColor = Color.parseColor("#20000000")
    private val thumbStrokeWidth = 4f
    private val thumbStrokeColor = Color.parseColor("#E4E4E4")
    private val bottomPadding = 40f

    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = thumbColor
        setShadowLayer(12f, 0f, 6f, shadowColor)
    }


    private val thumbStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = thumbStrokeColor
        style = Paint.Style.STROKE
        strokeWidth = thumbStrokeWidth
    }

    private var initialTouchY = 0f
    private var isScrollingParent = false
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun setHistogramData(data: List<Float>) {
        histogramData = data
        invalidate()
    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val height = height.toFloat() - thumbRadius * 2 - bottomPadding
        val maxDataValue = histogramData.maxOrNull() ?: 1f
        val barHeightMultiplier = 0.7f
        val baselineY = height + thumbRadius

        histogramData.forEachIndexed { index, value ->
            val left = index * (barWidth + barSpacing) + paddingLeft
            val barHeight = (value / maxDataValue * (height * barHeightMultiplier))
            val top = height - barHeight + thumbRadius
            val right = left + barWidth
            val rect = RectF(left, top, right, height + thumbRadius)

            val isSelected = left >= leftThumbX && right <= rightThumbX
            paint.color = if (isSelected) selectedColor else unselectedColor

            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        }

        paint.color = unselectedColor
        paint.strokeWidth = 4f
        canvas.drawLine(paddingLeft.toFloat(), baselineY, width.toFloat() - paddingRight, baselineY, paint)

        paint.color = selectedColor
        if (leftThumbX < rightThumbX) {
            canvas.drawLine(leftThumbX, baselineY, rightThumbX, baselineY, paint)
        }

        val thumbYPosition = height + thumbRadius
        if (isOverlapped) {
            canvas.drawCircle(leftThumbX, thumbYPosition, thumbRadius, thumbPaint)
            canvas.drawCircle(leftThumbX, thumbYPosition, thumbRadius - thumbStrokeWidth / 2, thumbStrokePaint)
        } else {
            canvas.drawCircle(leftThumbX, thumbYPosition, thumbRadius, thumbPaint)
            canvas.drawCircle(leftThumbX, thumbYPosition, thumbRadius - thumbStrokeWidth / 2, thumbStrokePaint)
            canvas.drawCircle(rightThumbX, thumbYPosition, thumbRadius, thumbPaint)
            canvas.drawCircle(rightThumbX, thumbYPosition, thumbRadius - thumbStrokeWidth / 2, thumbStrokePaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val minX = paddingLeft.toFloat() + thumbRadius
        val maxX = w.toFloat() - paddingRight - thumbRadius
        leftThumbX = minX
        rightThumbX = maxX
        updateRange()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val minX = paddingLeft.toFloat() + thumbRadius
        val maxX = width.toFloat() - paddingRight - thumbRadius

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchY = event.y
                lastTouchX = event.x
                val touchArea = thumbRadius * 1.5f
                when {
                    event.x in (leftThumbX - touchArea)..(leftThumbX + touchArea) -> {
                        isDraggingLeft = true
                        isDraggingRight = false
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                    event.x in (rightThumbX - touchArea)..(rightThumbX + touchArea) -> {
                        isDraggingRight = true
                        isDraggingLeft = false
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                    else -> {
                        isDraggingLeft = false
                        isDraggingRight = false
                        isScrollingParent = false
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastTouchX
                val dy = event.y - initialTouchY

                if (!isDraggingLeft && !isDraggingRight && !isScrollingParent) {
                    if (Math.abs(dy) > touchSlop && Math.abs(dy) > Math.abs(dx)) {
                        isScrollingParent = true
                        parent.requestDisallowInterceptTouchEvent(false)
                        return false
                    }
                }

                if (isDraggingLeft || isDraggingRight) {
                    lastTouchX = event.x

                    if (isOverlapped) {
                        if (dx > 0) {
                            // Moving right
                            leftThumbX = (leftThumbX + dx).coerceIn(minX, maxX)
                            if (leftThumbX > rightThumbX) {
                                val temp = leftThumbX
                                leftThumbX = rightThumbX
                                rightThumbX = temp
                                isDraggingLeft = false
                                isDraggingRight = true
                            }
                            isOverlapped = false
                        } else if (dx < 0) {
                            // Moving left
                            rightThumbX = (rightThumbX + dx).coerceIn(minX, maxX)
                            if (rightThumbX < leftThumbX) {
                                val temp = rightThumbX
                                rightThumbX = leftThumbX
                                leftThumbX = temp
                                isDraggingRight = false
                                isDraggingLeft = true
                            }
                            isOverlapped = false
                        }
                    } else {
                        when {
                            isDraggingLeft -> {
                                leftThumbX = (leftThumbX + dx).coerceIn(minX, rightThumbX)
                                if (leftThumbX == rightThumbX) {
                                    isOverlapped = true
                                }
                            }
                            isDraggingRight -> {
                                rightThumbX = (rightThumbX + dx).coerceIn(leftThumbX, maxX)
                                if (rightThumbX == leftThumbX) {
                                    isOverlapped = true
                                }
                            }
                        }
                    }

                    updateRange()
                    invalidate()
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDraggingLeft = false
                isDraggingRight = false
                isScrollingParent = false
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return isDraggingLeft || isDraggingRight || super.onTouchEvent(event)
    }




    fun setValueRange(minValue: Float, maxValue: Float) {
        minAllowedValue = minValue
        maxAllowedValue = maxValue
        valueRange = maxValue - minValue
        updateRange()
        invalidate()
    }

    fun getSelectedRange(): Pair<Float, Float> {
        val totalWidth = width.toFloat() - paddingLeft - paddingRight - 2 * thumbRadius
        val leftPosition = leftThumbX - (paddingLeft + thumbRadius)
        val rightPosition = rightThumbX - (paddingLeft + thumbRadius)

        val minValue = minAllowedValue + (valueRange * (leftPosition / totalWidth))
        val maxValue = minAllowedValue + (valueRange * (rightPosition / totalWidth))

        return Pair(
            minValue.coerceIn(minAllowedValue, maxAllowedValue),
            maxValue.coerceIn(minAllowedValue, maxAllowedValue)
        )
    }

    fun resetSlider() {
        leftThumbX = paddingLeft.toFloat() + thumbRadius
        rightThumbX = width.toFloat() - paddingRight - thumbRadius
        updateRange()
        invalidate()
    }

    private fun updateRange() {
        val (minPrice, maxPrice) = getSelectedRange()
        Log.d("RangeSlider", "Thumb X positions: left=$leftThumbX, right=$rightThumbX")
        Log.d("RangeSlider", "Calculated Prices: minPrice=$minPrice, maxPrice=$maxPrice")
        onRangeChangeListener?.onRangeChanged(minPrice, maxPrice)
    }
}