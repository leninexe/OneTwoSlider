# OneTwoSlider
OneTwoSlider is an Android library for adding a customizable slider with either one or two handles to your application. The library works with Android since SDK 23.

![screenshot](https://github.com/leninexe/OneTwoSlider/blob/master/screenshots/screenshot.png)

## Features
While there exist several slider libs for Android which allow you to select values on a scale between a min and a max value they were hard to cusomize for my application and didn't support setting a second value. 

OneTwoSlider allows you to easily customize the slider to meet your design requirements and also supports a second handle for selecting two values, e.g. for selecting a filter range.

Additionally OneTwoSlider allows you to add conversion factors, which can be used to use an internal value range for your data structure but another value range for display - e.g. for kilometers to mile conversion.

If it comes to labelling, OneTwoSlider supports floating values, which shows the selected value directly above the selection handle. The floating value can be customized in color, textsize and fontfamily.

Reading out values is done by attaching a listener to the slider which is called every time when a selection value changes.

## Download

## Usage
OneTwoSlider can easily be integrated in your XML-Layout by using the following code.

Initial values of the slider can set in code by using the following code.

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
slider_suffix | "" (empty string) | String value that is used as suffix for floating values (if displayed)
slider_max_suffix | "" (empty string) | String value that is used as suffix instead of slider_suffix for floating values if value is maximum value (if displayed)
slider_show_floating_values | false | Set to true if you want to show floating values above the slider handle(s)
slider_floating_values_textsize | 12sp | Textsize of the floating values (if displayed)
slider_floating_values_textcolor | @color/default_selection_color | Textcolor of the floating values; if not explicitly set slider_selection_color is used (if displayed)
slider_floating_values_fontfamily | default font | Typeface used for the floating values (if displayed)
slider_conversion_factor | 1f | Conversion factor used for converting floating values from internal used values (if displayed)
slider_conversion_min_value | "" (empty string) | Min value for a converted floating value (if displayed)
slider_conversion_max_value | "" (empty string) | Max value for a converted floating value (if displayed)

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
