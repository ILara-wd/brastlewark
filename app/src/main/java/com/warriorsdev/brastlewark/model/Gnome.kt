package com.warriorsdev.brastlewark.model

/**
 * Model class that contains the data of a Gnome.
 */
data class Gnome(
    val id: Int = -1,
    val name: String = "",
    val thumbnailUrl: String = "",
    val age: Int = 0,
    val weight: Int = 0,
    val height: Int = 0,
    val hairColor: String = "",
    val genre: String = (hairColor == "Pink").ternary("♀ FEMALE")?: "♂ MALE",
    val professions: List<String> = emptyList(),
    val friends: List<String> = emptyList()
)

infix fun <T : Any> Boolean.ternary(value: T): T? = if(this) value else null