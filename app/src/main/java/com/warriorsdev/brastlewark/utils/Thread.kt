package com.warriorsdev.brastlewark.utils

import java.util.concurrent.Executors

/**
 * Executes a given block of code in a given SingleThreadExecutor.
 * @param block Block of code to run in a worker thread.
 */
fun runOnWorkerThread(block: () -> Unit) {
    val executor = Executors.newSingleThreadExecutor()
    executor.execute(block)
    executor.shutdown()
}