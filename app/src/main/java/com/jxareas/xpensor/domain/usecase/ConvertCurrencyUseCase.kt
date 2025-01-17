package com.jxareas.xpensor.domain.usecase

import com.jxareas.xpensor.domain.model.Currencies
import com.jxareas.xpensor.domain.repository.ConverterRepository
import com.jxareas.xpensor.utils.Resource
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ConvertCurrencyUseCase @Inject constructor(private val repository: ConverterRepository) {

    companion object {
        const val UNEXPECTED_ERROR = -1.0
        const val NO_INTERNET_CONNECTION = -2.0
    }

    suspend operator fun invoke(amount: Double, from: String, to: String): Double =
        if (from == to)
            amount
        else getCurrencyRate(amount, from, to)

    private suspend fun getCurrencyRate(amount: Double, from: String, to: String): Double {
        when (val response = repository.getCurrencyRates(from)) {
            is Resource.Success -> {
                val rates = response.data?.rates ?: return UNEXPECTED_ERROR
                val rate = when (to) {
                    Currencies.USD.name -> rates.usd
                    Currencies.EUR.name -> rates.eur
                    Currencies.NIO.name -> rates.nio
                    Currencies.CRC.name -> rates.crc
                    Currencies.GBP.name -> rates.gbp
                    else -> return UNEXPECTED_ERROR
                }
                return amount * rate
            }
            is Resource.Error -> return NO_INTERNET_CONNECTION
        }
    }


}