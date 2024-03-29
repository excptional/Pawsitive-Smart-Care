package com.by.pawsitive.db.data_classes

data class User(
    val UID: String? = null,
    val OwnerName: String? = null,
    val OwnerImage: String? = null,
    val PetName: String? = null,
    val PetImage: String? = null,
    val PetAge: String? = null,
    val PetGender: String? = null,
    val Species: String? = null,
    val Email: String? = null,
    val Password: String? = null
)
