package com.example.dmfast.api


data class MonsterList(

    val count : Int,
    val results : List<Monster>)
data class Monster(val index: String, val name: String, val url: String)

data class DetailedMonster(val id: Int = 0,
                           val name: String,
                           val challenge_rating : Int,
                           val armor_class : ArmorClass
)

data class ArmorClass(val type: String, val value: Int)