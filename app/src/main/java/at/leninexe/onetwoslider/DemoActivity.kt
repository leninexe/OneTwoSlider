package at.leninexe.onetwoslider

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import at.leninexe.onetwosliderlib.Slider
import kotlinx.android.synthetic.main.activity_demo.*

class DemoActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_demo)

    firstSliderAge.setValues(18, 55)
    firstSliderDistance.setValues(150)
    firstSliderDistanceConversion.setValues(150)
    secondSliderAge.setValues(18, 55)
    secondSliderDistance.setValues(150)
    secondSliderDistanceConversion.setValues(150)

    firstSliderAge.setListener(object : Slider.SliderListener {
      override fun valueChanged(minValue: Int, maxValue: Int?) {

      }
    })

    firstSliderDistance.setListener(object : Slider.SliderListener {
      override fun valueChanged(minValue: Int, maxValue: Int?) {

      }
    })

    secondSliderAge.setListener(object : Slider.SliderListener {
      override fun valueChanged(minValue: Int, maxValue: Int?) {
        var text = if (minValue != maxValue) {
          "$minValue - $maxValue"
        } else {
          "$minValue"
        }

        text += if (maxValue != secondSliderAge.getMaxValue()) {
          secondSliderAge.getSuffix()
        } else {
          secondSliderAge.getMaxSuffix()
        }

        lblSecondSliderAge.text = text
      }
    })

    secondSliderDistance.setListener(object : Slider.SliderListener {
      override fun valueChanged(minValue: Int, maxValue: Int?) {
        val text = if (minValue == secondSliderDistance.getMaxValue()) {
          "$minValue${secondSliderDistance.getMaxSuffix()}"
        } else {
          "$minValue${secondSliderDistance.getSuffix()}"
        }

        lblSecondSliderDistance.text = text
      }
    })

    secondSliderDistanceConversion.setListener(object : Slider.SliderListener {
      override fun valueChanged(minValue: Int, maxValue: Int?) {
        val text = if (minValue == secondSliderDistanceConversion.getMaxValue()) {
          "100${secondSliderDistanceConversion.getMaxSuffix()}"
        } else if (minValue == secondSliderDistanceConversion.getMinValue()) {
          "2m"
        } else {
          "${kmToMilesConversion(minValue)}${secondSliderDistanceConversion.getSuffix()}"
        }

        lblSecondSliderDistanceConversion.text = text
      }

    })
  }

  private fun kmToMilesConversion(km: Int) = (km * 0.621371).toInt()
}
