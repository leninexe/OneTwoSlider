# OneTwoSlider
[JitPack](https://jitpack.io/#leninexe/OneTwoSlider/1.0.0)

OneTwoSlider is an Android library for adding a customizable slider with either one or two handles to your application. The library works with Android since SDK 23.

![screenshot](https://github.com/leninexe/OneTwoSlider/blob/master/screenshots/screenshot.png)

## Features
While there exist several slider libs for Android which allow you to select values on a scale between a min and a max value they were hard to cusomize for my application and didn't support setting a second value. 

OneTwoSlider allows you to easily customize the slider to meet your design requirements and also supports a second handle for selecting two values, e.g. for selecting a filter range.

Additionally OneTwoSlider allows you to add conversion factors, which can be used to use an internal value range for your data structure but another value range for display - e.g. for kilometers to mile conversion.

If it comes to labelling, OneTwoSlider supports floating values, which shows the selected value directly above the selection handle. The floating value can be customized in color, textsize and fontfamily.

Reading out values is done by attaching a listener to the slider which is called every time when a selection value changes.

## Installation
Add the [JitPack](https://jitpack.io) repository to your build file
```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
Add the dependency
```groovy
dependencies {
  implementation 'com.github.leninexe:OneTwoSlider:1.0.0'
}
```

## Usage
### Integrate in layout file
OneTwoSlider can easily be integrated in your XML-Layout by using the following code.
```xml
<at.leninexe.onetwosliderlib.Slider
  android:id="@+id/exampleSlider"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  android:layout_margin="10dp"
  app:layout_constraintTop_toTopOf="parent"
  app:layout_constraintBottom_toBottomOf="parent"
  app:slider_bar_color="#b3000000"
  app:slider_selection_color="#d21e28"
  app:slider_handle_drawable="@drawable/graphic_slider_handle"
  app:slider_handle_width="21.7dp"
  app:slider_handle_height="21.7dp"
  app:slider_min_value="18"
  app:slider_max_value="55"
  app:slider_max_suffix="+"
  app:slider_show_floating_values="true"
  />

<at.leninexe.onetwosliderlib.Slider
  android:id="@+id/otherSlider"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  android:layout_margin="10dp"
  app:layout_constraintTop_toTopOf="parent"
  app:layout_constraintBottom_toBottomOf="parent"
  app:slider_bar_color="#b3000000"
  app:slider_selection_color="#d21e28"
  app:slider_handle_drawable="@drawable/graphic_slider_handle"
  app:slider_handle_width="21.7dp"
  app:slider_handle_height="21.7dp"
  app:slider_min_value="2"
  app:slider_max_value="150"
  app:slider_suffix="m"
  app:slider_max_suffix="m+"
  app:slider_show_floating_values="true"
  app:slider_conversion_factor="0.621371"
  app:slider_conversion_min_value="2"
  app:slider_conversion_max_value="100"
  />
```
### Set values and add listeners in kotlin code
Initial values of the slider and the listener can set in code as follows.
```kotlin
// Set value for a slider with only one handle
exampleSlider.setValues(73)

// Set values for a slider with two handles
otherSlider.setValues(18, 55)

firstSliderAge.setListener(object : Slider.SliderListener {
    override fun valueChanged(minValue: Int, maxValue: Int?) {
      // Do Something with the value (one handle slider, maxValue is null)
    }
  })
  
otherSlider.setListener(object : Slider.SliderListener {
    override fun valueChanged(minValue: Int, maxValue: Int?) {
      // Do something with the values (two handle slider, maxValue is not null)
    }
  })
```

### Custom attributes
The following attributes exist to customize the OneTwoSlider to fit your requirements.

Attribute Name | Default Value | Description
-------------- | ------------- | -----------
slider_bar_height | 4dp | Height of the slider bar
slider_bar_color | @color/default_bar_color (#000000) | Background color of the slider bar
slider_selection_color | @color/default_selection_color (#006699) | Background color of the selection bar
slider_handle_drawable | @drawable/default_slider_handle | Drawable that is used for the handle(s)
slider_handle_width | 24dp | Width of the slider handle
slider_handle_height | 24dp | Height of the slider handle
slider_min_value | 0 | Minimum value of the slider
slider_max_value | 100 | Maximum value of the slider
slider_suffix | *empty string* | String value that is used as suffix for floating values (if displayed)
slider_max_suffix | *empty string* | String value that is used as suffix instead of slider_suffix for floating values if value is maximum value (if displayed)
slider_show_floating_values | false | Set to true if you want to show floating values above the slider handle(s)
slider_floating_values_textsize | 12sp | Textsize of the floating values (if displayed)
slider_floating_values_textcolor | @color/default_selection_color | Textcolor of the floating values; if not explicitly set slider_selection_color is used (if displayed)
slider_floating_values_fontfamily | default font | Typeface used for the floating values (if displayed)
slider_conversion_factor | 1f | Conversion factor used for converting floating values from internal used values (if displayed)
slider_conversion_min_value | *empty string* | Min value for a converted floating value (if displayed)
slider_conversion_max_value | *empty string* | Max value for a converted floating value (if displayed)

### Conversion factors
Out of a requirement that I had in a reasent project I integrated the possibility of adding conversion factors to OneTwoSlider. By doing so the displayed floating values are automatically calculated according to the provided conversion factor. 

As an example think of a OneTwoSlider used for setting a maximum distance range. Internally we use kilometers for storing the selected maximum distance but in some countries we want to show the selected maximum distance in miles. By using `slider_conversion_factor` we can easily achieve, that the displayed floating value is automatically modified to the correct distance in miles. As we want to limit the minimum selectable distance to 2 kilometers and the maximum distance to 150 kilometers we would automatically display 1 (rounded to Int) as minimum and 93 as maximum selectable value in miles. As these are not perfect values (in our opinion) you can further tweak the displayed values with the attributes `slider_conversion_min_value` and `slider_conversion_max_value`, which we set to *2* and *100* in our case. By doing so, the value *2* was shown when the user slides to the minimum and the value *100* is shown, if the user slides to the maximum selectable value.
 
The correct values for `slider_suffix` and `slider_max_suffix` are automatically shown in cases, where either the maximum value is selected or the `slider_conversion_max_value` is shown.

## License
```
Copyright [2018] [leninexe]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
