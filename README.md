# Apriltag Detector Android

## Goning to update very soon with all the usage sample

  -Add these below line to the project level build.gradle

     allprojects {
        repositories {
          ...
          maven { url 'https://jitpack.io' }
        }
	   }
     
   -Add these line to app level build.gradle file

      dependencies {
	        implementation 'com.github.easedroid:apriltag_detector_android:0.0.2'
	  }
