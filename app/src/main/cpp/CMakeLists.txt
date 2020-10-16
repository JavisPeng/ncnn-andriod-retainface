project(retinafacencnn)

cmake_minimum_required(VERSION 3.4.1)

# import ncnn library
add_library(ncnn STATIC IMPORTED)
# change this folder path to yours
set_target_properties(ncnn PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/ncnn-android-lib/${ANDROID_ABI}/libncnn.a)
include_directories(${CMAKE_SOURCE_DIR}/ncnn-android-lib/include/ncnn)


# openmp
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fopenmp -static-openmp")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fopenmp -static-openmp")


aux_source_directory(. DIR_SRCS)

add_library(retinafacencnn SHARED ${DIR_SRCS})


target_link_libraries(retinafacencnn
        ncnn
        android
        z
        log
        jnigraphics)
