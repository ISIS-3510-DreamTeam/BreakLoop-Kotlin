package com.dreamteam.breakloop.data.sync

interface SyncService {
    //Guardar implica esperar a una red, para no bloquear el hilo se usa suspend
    suspend fun saveProgress(progress: ProgressData)
    //Debe reflejar que puede que el usuario todavía no tenga progreso guardado
    suspend fun restoreProgress(): ProgressData?
}