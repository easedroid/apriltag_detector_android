
cmake_minimum_required(VERSION 3.4.1)


set(pathToProject C\:\\Users\\amitd\\AndroidStudioProjects\\AprilTagDetector\\apriltagdetection)
set(libs ${pathToProject}\\src\\main\\jniLibs)

include_directories(${pathToProject}\\src\\main\\apriltag)


#[[file(GLOB SRC
		"*.h"
		"*.c"
		"common/*.h"
		"common/*.c"
		)]]

#add_executable(${SRC})

find_library(log-lib log)
find_library(jnigraphics-lib jnigraphics)

file(GLOB SRC   "apriltag.c"
		"apriltag_jni.c"
		#"src/main/apriltag/apriltag_opencv.cpp"
		"apriltag_quad_thresh.c"
		"common/g2d.c"
		"common/getopt.c"
		"common/homography.c"
		"common/image_f32.c"
		"common/image_u8.c"
		"common/image_u8x3.c"
		"common/matd.c"
		"common/pnm.c"
		"common/string_util.c"
		"common/svd22.c"
		"common/time_util.c"
		"common/unionfind.c"
		"common/workerpool.c"
		"common/zarray.c"
		"common/zhash.c"
		"common/zmaxheap.c"
		"apriltag_pose.c"
		"tag36h11.c"
		"tagCustom48h12.c"
		"tagStandard52h13.c"
		"tagCustom48h12_6k_12k.c"
		#"src/main/apriltag/tagCustom48h12_6_10.c"
		#[=["src/main/apriltag/example/opencv_demo.cc"]=]
		#"src/main/apriltag/apriltag_cpp_jni.cc"
		)


add_library(# Specifies the name of the library.
		apriltag

		# Sets the library as a shared library.
		SHARED
		#STATIC

		# Provides a relative path to your source file(s).
		${SRC}
		#IMPORTED
		)

set_target_properties( apriltag
		PROPERTIES IMPORTED_LOCATION
		${libs}/${ANDROID_ABI}/libapriltag.so )

target_link_libraries(apriltag
		${log-lib} ${jnigraphics-lib})
