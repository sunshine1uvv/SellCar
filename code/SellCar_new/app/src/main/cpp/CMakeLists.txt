cmake_minimum_required(VERSION 3.10.2)

project("myapplication")

add_library(
    native-lib
    SHARED
    src/main/cpp/native-lib.c
)

find_library(
    log-lib
    log
)

target_link_libraries(
    native-lib
    ${log-lib}
)