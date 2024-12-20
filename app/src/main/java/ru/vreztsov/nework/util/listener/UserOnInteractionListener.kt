package ru.vreztsov.nework.util.listener

import ru.vreztsov.nework.dto.User

interface UserOnInteractionListener {
    fun onClick(user: User)
    fun onSelect(user: User, isChecked: Boolean)
}