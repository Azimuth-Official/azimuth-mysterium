package day.azimuth.mysterium

import kotlinx.coroutines.flow.StateFlow

interface MystNodeManager {
    val isRunning: StateFlow<Boolean>
    suspend fun start(): Result<Unit>
    suspend fun stop(): Result<Unit>
}
