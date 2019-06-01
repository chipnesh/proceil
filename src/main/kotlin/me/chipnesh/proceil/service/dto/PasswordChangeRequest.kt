package me.chipnesh.proceil.service.dto

/**
 * A DTO representing a password change required data - current and new password.
 */
data class PasswordChangeRequest(var currentPassword: String? = null, var newPassword: String? = null)
