package at.leninexe.onetwosliderlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.layout_slider.view.*

class Slider : ConstraintLayout {

  private var listener: SliderListener? = null
  private var min = 0
  private var max = 100
  private var currentValue = 0
  private var currentOtherValue: Int? = null
  private var activeHandle: View? = null
  private var moving: Moving = Moving.Unknown
  private var minPos = 0f
  private var maxPos = 0f
  private var suffix = ""
  private var maxSuffix = ""
  private var showFloatingValues = false
  private var ptFloatingValue = Paint()

  private var conversionFactor: Float? = null
  private var conversionMinValue: Int? = null
  private var conversionMaxValue: Int? = null

  constructor(context: Context) : this(context, null, 0)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init(context, attrs)
  }

  private fun init(context: Context, attrs: AttributeSet?) {
    inflate(context, R.layout.layout_slider, this)

    context.obtainStyledAttributes(attrs, R.styleable.Slider).apply {
      val barHeight = getDimension(R.styleable.Slider_slider_bar_height, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics) /*4dp*/)
      val barColor = ColorDrawable(getColor(R.styleable.Slider_slider_bar_color, context.getColor(R.color.default_bar_color)))
      val selectionColor = ColorDrawable(getColor(R.styleable.Slider_slider_selection_color, context.getColor(R.color.default_selection_color)))
      val handleDrawable = getDrawable(R.styleable.Slider_slider_handle_drawable) ?: context.getDrawable(R.drawable.default_slider_handle)
      val handleWidth = getDimension(R.styleable.Slider_slider_handle_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics))
      val handleHeight = getDimension(R.styleable.Slider_slider_handle_height, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics))

      setMinValue(getInt(R.styleable.Slider_slider_min_value, 0))
      setMaxValue(getInt(R.styleable.Slider_slider_max_value, 100))

      setSuffix(getString(R.styleable.Slider_slider_suffix))
      setMaxSuffix(getString(R.styleable.Slider_slider_max_suffix))

      setShowFloatingLabels(getBoolean(R.styleable.Slider_slider_show_floating_values, false))

      setBarHeight(barHeight.toInt())
      setBarColor(barColor)
      setSelectionColor(selectionColor)
      setHandleDrawable(handleDrawable)

      setHandleWidth(handleWidth.toInt())
      setHandleHeight(handleHeight.toInt())

      setFloatingLabelsTextcolor(getColor(R.styleable.Slider_slider_floating_values_textcolor, getColor(R.styleable.Slider_slider_selection_color, context.getColor(R.color.default_selection_color))))
      setFloatingLabelsTextsize(getDimension(R.styleable.Slider_slider_floating_values_textsize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics)))

      val fontRef = getResourceId(R.styleable.Slider_slider_floating_values_fontfamily, -1)

      if (fontRef != -1) {
        setFloatingLabelsFontfamily(ResourcesCompat.getFont(context, fontRef))
      }

      // Section conversions
      setConversionFactor(getFloat(R.styleable.Slider_slider_conversion_factor, 1f))

      if (hasValue(R.styleable.Slider_slider_conversion_min_value)) {
        setConversionMinValue(getInt(R.styleable.Slider_slider_conversion_min_value, min))
      }

      if (hasValue(R.styleable.Slider_slider_conversion_max_value)) {
        setConversionMaxValue(getInt(R.styleable.Slider_slider_conversion_max_value, max))
      }

      recycle()
    }

    setWillNotDraw(false)

    post {
      requestLayout()
    }
  }

  private fun calculateValueForX(x: Float, limit: Int?): Int {
    if (limit != null) {
      when (moving) {
        is Moving.Max -> {
          return Math.min(Math.max(Math.max(min + ((x - minPos) / (maxPos - minPos) * (max - min)).toInt(), limit), min), max)
        }
        is Moving.Min -> {
          return Math.min(Math.max(Math.min(min + ((x - minPos) / (maxPos - minPos) * (max - min)).toInt(), limit), min), max)
        }
      }
    }

    return Math.min(Math.max(min + ((x - minPos) / (maxPos - minPos) * (max - min)).toInt(), min), max)
  }

  private fun calculateXForValue(value: Int): Float {
    if (value <= min) {
      return minPos
    }

    if (value >= max) {
      return maxPos
    }

    return minPos + (value - min).toFloat() / (max - min) * (maxPos - minPos)
  }

  private fun catchHandle(x: Float, y: Float): View? {
    if (y >= handle1.y && y <= handle1.y + handle1.height) {
      if (x >= handle1.x && x <= handle1.x + handle1.width) {
        return handle1
      } else if (x >= handle2.x && x <= handle2.x + handle2.width) {
        return handle2
      }
    }

    return null
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    minPos = (handle1.width / 2).toFloat()
    maxPos = (width - handle1.width / 2).toFloat()

    invalidate()
  }

  override fun performClick(): Boolean {
    super.performClick()
    return false
  }

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    event?.apply {
      when (this.action) {
        MotionEvent.ACTION_DOWN -> {
          // Catch handle
          activeHandle = catchHandle(event.x, event.y)

          moving = when (activeHandle) {
            handle1 -> {
              when {
                (currentValue > currentOtherValue ?: min) -> Moving.Max
                (currentValue < currentOtherValue ?: min) -> Moving.Min
                else -> Moving.Unknown
              }
            }
            handle2 -> {
              val otherVal = currentOtherValue

              if (otherVal != null) {
                when {
                  (otherVal > currentValue) -> Moving.Max
                  (otherVal < currentValue) -> Moving.Min
                  else -> Moving.Unknown
                }
              } else {
                Moving.Unknown
              }
            }
            else -> Moving.Unknown
          }

          return true
        }
        MotionEvent.ACTION_UP -> {
          // Release handle
          activeHandle = null
          moving = Moving.Unknown

          parent.requestDisallowInterceptTouchEvent(false)
          performClick()

          return true
        }
        MotionEvent.ACTION_MOVE -> {
          // Move handle
          activeHandle?.apply {
            parent.requestDisallowInterceptTouchEvent(true)

            if (activeHandle == handle1) {
              currentValue = calculateValueForX(event.x, currentOtherValue)

              if (moving == Moving.Unknown) {
                moving = when {
                  (currentValue > currentOtherValue ?: min) -> Moving.Max
                  (currentValue < currentOtherValue ?: min) -> Moving.Min
                  else -> Moving.Unknown
                }
              }

              this@Slider.invalidate()
            } else if (activeHandle == handle2) {
              currentOtherValue = calculateValueForX(event.x, currentValue)

              currentOtherValue?.apply {
                if (moving == Moving.Unknown) {
                  moving = when {
                    (this > currentValue) -> Moving.Max
                    (this < currentValue) -> Moving.Min
                    else -> Moving.Unknown
                  }
                }
              }

              this@Slider.invalidate()
            }
          }

          return true
        }
      }
    }

    return false
  }

  override fun onDraw(canvas: Canvas?) {
    handle1.x = calculateXForValue(currentValue) - handle1.width / 2

    val otherVal = currentOtherValue

    if (otherVal != null) {
      // Min-Max
      handle2.x = calculateXForValue(otherVal) - handle1.width / 2

      val minValue = Math.min(currentValue, otherVal)
      val maxValue = Math.max(currentValue, otherVal)

      selection.left = Math.min(handle1.x, handle2.x).toInt() + (handle1.width / 2)
      selection.right = Math.max(handle1.x, handle2.x).toInt() + (handle1.width / 2)

      // Draw floating label
      if (showFloatingValues) {
        if (minValue != maxValue) {
          val textMin = getTextValue(minValue)
          val textMax = getTextValue(maxValue)
          val width1 = ptFloatingValue.measureText(textMin)
          val width2 = ptFloatingValue.measureText(textMax)
          var x1 = Math.min(handle1.x, handle2.x) + (handle1.width / 2)
          var x2 = Math.max(handle1.x, handle2.x) + (handle1.width / 2)

          x1 = Math.max(0 + width1 / 2, x1)
          x1 = Math.min(this.width - width1 / 2, x1)

          x2 = Math.max(0 + width2 / 2, x2)
          x2 = Math.min(this.width - width2 / 2, x2)

          if (x1 + width1 / 2 + handle1.width / 2 > x2 - width2 / 2) {
            val text = getCombinedText(minValue, maxValue)
            val width = ptFloatingValue.measureText(text)
            var x = Math.min(handle1.x, handle2.x) + (handle1.width / 2) + Math.abs(handle1.x - handle2.x) / 2

            x = Math.max(0 + width / 2, x)
            x = Math.min(this.width - width / 2, x)

            canvas?.drawText(text, x, floatingLabelSpacer.bottom.toFloat(), ptFloatingValue)
          } else {
            canvas?.drawText(textMin, x1, floatingLabelSpacer.bottom.toFloat(), ptFloatingValue)
            canvas?.drawText(textMax, x2, floatingLabelSpacer.bottom.toFloat(), ptFloatingValue)
          }
        } else {
          val text = getTextValue(minValue)
          val width = ptFloatingValue.measureText(text)
          var x = handle1.x + handle1.width / 2

          x = Math.max(0 + width / 2, x)
          x = Math.min(this.width - width / 2, x)

          canvas?.drawText(text, x, floatingLabelSpacer.bottom.toFloat(), ptFloatingValue)
        }
      }

      listener?.valueChanged(minValue, maxValue)
    } else {
      // Only Max
      selection.left = bar.left
      selection.right = handle1.x.toInt() + (handle1.width / 2)

      // Draw floating label
      if (showFloatingValues) {
        val text = getTextValue(currentValue)
        val width = ptFloatingValue.measureText(text)
        var x = handle1.x + handle1.width / 2

        x = Math.max(0 + width / 2, x)
        x = Math.min(this.width - width / 2, x)

        canvas?.drawText(text, x, floatingLabelSpacer.bottom.toFloat(), ptFloatingValue)
      }

      listener?.valueChanged(currentValue, null)
    }
  }

  fun setListener(newListener: SliderListener?) {
    listener = newListener
  }

  fun getMinValue() = min

  fun setMinValue(value: Int) {
    min = value

    if (currentValue < min) {
      currentValue = min
    }

    if ((currentOtherValue ?: min) < min) {
      currentOtherValue = min
    }

    invalidate()
  }

  fun getMaxValue() = max

  fun setMaxValue(value: Int) {
    max = value

    if (currentValue > max) {
      currentValue = max
    }

    if ((currentOtherValue ?: max) > max) {
      currentOtherValue = max
    }

    invalidate()
  }

  fun setValues(min: Int, max: Int? = null) {
    currentValue = min
    currentOtherValue = max

    if (currentOtherValue != null) {
      handle2.visibility = View.VISIBLE
    }

    invalidate()
  }

  fun setSuffix(suf: String?) {
    suffix = suf ?: ""
  }

  fun getSuffix() = suffix

  fun setMaxSuffix(suf: String?) {
    maxSuffix = suf ?: ""
  }

  fun getMaxSuffix() = maxSuffix

  fun setBarHeight(barHeight: Int) {
    val params = bar.layoutParams
    params.height = barHeight
    bar.layoutParams = params

    val selectionParams = selection.layoutParams
    selectionParams.height = barHeight
    selection.layoutParams = selectionParams
  }

  fun setBarColor(color: ColorDrawable) {
    bar.background = color
  }

  fun setSelectionColor(color: ColorDrawable) {
    selection.background = color
  }

  fun setHandleDrawable(drawable: Drawable) {
    handle1.setImageDrawable(drawable)
    handle2.setImageDrawable(drawable)
  }

  fun setHandleWidth(width: Int) {
    val params1 = handle1.layoutParams
    params1.width = width
    handle1.layoutParams = params1

    val params2 = handle2.layoutParams
    params2.width = width
    handle2.layoutParams = params2

    val barParams = bar.layoutParams as ConstraintLayout.LayoutParams
    barParams.marginStart = (width / 2)
    barParams.marginEnd = (width / 2)
    bar.layoutParams = barParams
  }

  fun setHandleHeight(height: Int) {
    val params1 = handle1.layoutParams
    params1.height = height
    handle1.layoutParams = params1

    val params2 = handle2.layoutParams
    params2.height = height
    handle2.layoutParams = params2
  }

  fun setShowFloatingLabels(show: Boolean) {
    showFloatingValues = show

    if (showFloatingValues) {
      ptFloatingValue.textAlign = Paint.Align.CENTER

      floatingLabelSpacer.visibility = View.VISIBLE
    } else {
      floatingLabelSpacer.visibility = View.GONE
    }

    invalidate()
    requestLayout()
  }

  fun setFloatingLabelsTextsize(textsizePx: Float) {
    floatingLabelSpacer.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsizePx)
    ptFloatingValue.textSize = textsizePx

    invalidate()
    requestLayout()
  }

  fun setFloatingLabelsTextcolor(textcolor: Int) {
    ptFloatingValue.color = textcolor

    invalidate()
  }

  fun setFloatingLabelsFontfamily(typeface: Typeface?) {
    if (typeface != null) {
      ptFloatingValue.typeface = typeface
    }

    invalidate()
  }

  fun setConversionFactor(factor: Float) {
    conversionFactor = factor

    invalidate()
  }

  fun setConversionMinValue(min: Int) {
    conversionMinValue = min

    invalidate()
  }

  fun setConversionMaxValue(max: Int) {
    conversionMaxValue = max

    invalidate()
  }

  private fun convertValue(value: Int): Int {
    val converted = (value * (conversionFactor?:1f)).toInt()

    if (conversionMaxValue != null && (converted > conversionMaxValue!! || value == max)) {
      return conversionMaxValue!!
    }

    if (conversionMinValue != null && (converted < conversionMinValue!! || value == min)) {
      return conversionMinValue!!
    }

    return converted
  }

  private fun getTextValue(value: Int): String {
    return if (conversionFactor != null) {
      val converted = convertValue(value)

      if (value == max) {
        "$converted$maxSuffix"
      } else {
        "$converted$suffix"
      }
    } else {
      if (value == max) {
        "$value$maxSuffix"
      } else {
        "$value$suffix"
      }
    }
  }

  private fun getCombinedText(minValue: Int, maxValue: Int): String {
    return if (conversionFactor != null) {
      val minConverted = convertValue(minValue)
      val maxConverted = convertValue(maxValue)

      if (maxValue == max) {
        "$minConverted - $maxConverted$maxSuffix"
      } else {
        "$minConverted - $maxConverted$suffix"
      }
    } else {
      if (maxValue == max) {
        "$minValue - $maxValue$maxSuffix"
      } else {
        "$minValue - $maxValue$suffix"
      }
    }
  }

  private sealed class Moving {
    object Unknown : Moving()
    object Min : Moving()
    object Max: Moving()
  }

  interface SliderListener {
    fun valueChanged(minValue: Int, maxValue: Int?)
  }
}