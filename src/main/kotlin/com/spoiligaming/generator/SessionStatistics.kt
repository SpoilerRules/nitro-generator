package com.spoiligaming.generator

object SessionStatistics {
    var validNitroCodes = 0
    var invalidNitroCodes = 0
    val successRate: Float
        get() =
            if (validNitroCodes + invalidNitroCodes == 0) {
                0f // DIVIDING BY 0 IS UNDEFINED AND IMPOSSIBLE
            } else {
                (validNitroCodes.toFloat() / (validNitroCodes + invalidNitroCodes)) * 100
            }

    fun incrementValidNitroCodes() {
        validNitroCodes += 1
    }

    fun incrementInvalidNitroCodes() {
        invalidNitroCodes += 1
    }
}
