package com.example.huertohogardefinitiveedition.data.repository

import com.example.huertohogardefinitiveedition.data.model.QrResult

class QrRepository {
    fun processQrContent(content: String): QrResult {
        // Aquí podrías guardar o procesar el QR en una BD o API
        return QrResult(content)
    }
}