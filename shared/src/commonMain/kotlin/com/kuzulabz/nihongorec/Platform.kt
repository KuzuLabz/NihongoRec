package com.kuzulabz.nihongorec

enum class Platform {
    ANDROID,
    IOS,
    DESKTOP
}

expect fun getPlatform(): Platform