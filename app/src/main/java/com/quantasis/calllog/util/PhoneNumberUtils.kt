package com.quantasis.calllog.util

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.NumberParseException
import java.util.Locale

data class PhoneNumberParts(
    val countryCode: String?,
    val nationalNumber: String,
    val isValid: Boolean
)

object PhoneNumberUtils {

    /**
     * Extracts country code and national number using libphonenumber.
     * @param rawNumber Raw input number (can contain +, 0, etc.)
     * @param defaultRegion ISO 3166-1 two-letter country code (e.g., "IN", "US")
     */
    fun extractPhoneNumberParts(
        rawNumber: String,
        defaultRegion: String = Locale.getDefault().country
    ): PhoneNumberParts {
        val phoneUtil = PhoneNumberUtil.getInstance()
        return try {
            val numberProto = phoneUtil.parse(rawNumber, defaultRegion)

            val countryCode = "+${numberProto.countryCode}"
            val nationalNumber = numberProto.nationalNumber.toString()
            val isValid = phoneUtil.isValidNumber(numberProto)

            PhoneNumberParts(
                countryCode = countryCode,
                nationalNumber = nationalNumber,
                isValid = isValid
            )
        } catch (e: NumberParseException) {
            // Fallback if parsing fails
            PhoneNumberParts(
                countryCode = null,
                nationalNumber = rawNumber.filter { it.isDigit() },
                isValid = false
            )
        }
    }
}