# AutoChek
Android application


## Table of Contents

- [App](#app)
- [Architecture](#architecture)
- [ScreenShots](#screenshots)
- [Libraries](#libraries)

## App
A demo application showing cars to be purchase.
The project has been written in Kotlin language. For network requests, it uses Retrofit with Coroutines and Moshi 
Hilt has been used for Dependency injection.

## Architecture
The project is built using the MVVM architectural pattern. Mvvm allows for the separation of concern which also makes testing easier.

## ScreenShots
  <img src="art/loading_screen.jpg" width="200" style="max-width:100%;">

## Libraries

Libraries used in the whole application are:

- [Viewmodel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Manage UI related data in a lifecycle conscious way
- [Hilt](https://dagger.dev/hilt/) - Hilt provides a standard way to incorporate Dagger dependency injection into an Android application.
- [Kotlin.coroutines](https://developer.android.com/kotlin/coroutines?gclid=Cj0KCQjw1dGJBhD4ARIsANb6Odld-9wkN4Lkm6UJAvWRshusopwstZH5IXkSLzxv_Q5JYjgjozIywfcaAlS9EALw_wcB&gclsrc=aw.ds) - Concurrency design pattern that you can use on Android to simplify code that executes asynchronously. 
- [Retrofit](https://square.github.io/retrofit/) - Turns your HTTP API into a Java interface.
- [Glide](https://github.com/bumptech/glide) - Media management and image loading framework for Android