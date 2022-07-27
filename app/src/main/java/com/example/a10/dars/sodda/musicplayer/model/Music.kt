package com.example.a10.dars.sodda.musicplayer.model

import java.io.Serializable

class Music : Serializable {
    var id: Int? = null
    var uri: String? = null
    var name: String? = null
    var author: String? = null
    var duration: Int? = null
    var size: Int? = null
    var image: String? = null

    constructor(uri: String, name: String?, author: String?, image: String?) {
        this.uri = uri.toString()
        this.name = name
        this.author = author
        this.image = image
    }

    constructor(uri: String?, name: String?, author: String?) {
        this.uri = uri
        this.name = name
        this.author = author
    }

    constructor(uri: String?, name: String?, author: String?, duration: Int?) {
        this.uri = uri
        this.name = name
        this.author = author
        this.duration = duration
    }


}