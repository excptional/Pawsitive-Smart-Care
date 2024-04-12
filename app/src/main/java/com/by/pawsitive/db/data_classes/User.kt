package com.by.pawsitive.db.data_classes

data class User(
    val uid: String? = null,
    val ownerName: String? = null,
    val ownerImage: String? = null,
    val petName: String? = null,
    val petImage: String? = null,
    val petAge: String? = null,
    val petGender: String? = null,
    val species: String? = null,
    val phone: String? = null,
    val password: String? = null
)
