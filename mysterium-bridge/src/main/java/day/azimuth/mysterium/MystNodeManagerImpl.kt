package day.azimuth.mysterium

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import mysterium.Mysterium
import mysterium.MobileNode

class MystNodeManagerImpl(private val context: Context) : MystNodeManager {

    private val _isRunning = MutableStateFlow(false)
    override val isRunning: StateFlow<Boolean> = _isRunning

    private var node: MobileNode? = null
    private val mutex = Mutex()

    override suspend fun start(): Result<Unit> = mutex.withLock {
        withContext(Dispatchers.IO) {
            runCatching {
                if (node == null) {
                    val dataDir = context.filesDir.resolve("mysterium").also { it.mkdirs() }
                    Log.i(TAG, "Starting Mysterium node in ${dataDir.canonicalPath}")
                    node = Mysterium.newNode(dataDir.canonicalPath, Mysterium.defaultProviderNodeOptions())
                    Log.i(TAG, "Mysterium node started")
                }
                _isRunning.value = true
            }
        }
    }

    override suspend fun stop(): Result<Unit> = mutex.withLock {
        withContext(Dispatchers.IO) {
            runCatching {
                Log.i(TAG, "Stopping Mysterium node")
                node?.shutdown()
                node = null
                _isRunning.value = false
                Log.i(TAG, "Mysterium node stopped")
                Unit
            }
        }
    }

    companion object {
        private const val TAG = "MystNodeManager"
    }
}
