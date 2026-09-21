package com.example.vetsync.utils

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LogUtil{
    private const val NOMBRE_ARCHIVO = "vetsync_log.txt"

    fun registrarLog(mensaje: String, tipo: String = "INFO"){

        val file = File(NOMBRE_ARCHIVO)
        val formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val fechaHoraActual = LocalDateTime.now().format(formatoFecha)

        val registro = "[$fechaHoraActual] [$tipo] $mensaje \n"

        try{
            file.appendText(registro)
        }catch(e: Exception){
            println("ERROR CRITICO")
        }

    }

}