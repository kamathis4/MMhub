package com.example.domain.model

data class RepoData(
    val name:String,
    val stars:Int,
    val description:Any?="Null",
    val language:Any?
)
