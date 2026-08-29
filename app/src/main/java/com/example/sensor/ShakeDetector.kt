package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * ShakeDetector
 *
 * Professional hardware sensor listener logic for detecting user physical shakes.
 * Employs a low-pass filter to smooth gravity vectors and isolates user-applied
 * linear acceleration forces.
 *
 * Specifications:
 * - Sensor: Sensor.TYPE_ACCELEROMETER
 * - Threshold: 2.7 G (approx 26.48 m/s²)
 * - Slop Time: 500ms debounce window
 */
class ShakeDetector(
    context: Context,
    private val thresholdG: Float = DEFAULT_SHAKE_THRESHOLD_G,
    private val slopTimeMs: Long = DEFAULT_SLOP_TIME_MS,
    private val onShakeListener: () -> Unit
) : SensorEventListener {

    companion object {
        const val DEFAULT_SHAKE_THRESHOLD_G = 2.7f
        const val DEFAULT_SLOP_TIME_MS = 500L
        private const val ALPHA = 0.8f // Low-pass filter smoothing factor
    }

    private val sensorManager: SensorManager? =
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer: Sensor? =
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val gravity = FloatArray(3) { 0f }
    private val linearAcceleration = FloatArray(3) { 0f }

    private var lastShakeTimestamp: Long = 0L
    private var isListening: Boolean = false

    fun start(): Boolean {
        if (isListening || accelerometer == null || sensorManager == null) {
            return false
        }
        val registered = sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
        isListening = registered
        return registered
    }

    fun stop() {
        if (isListening && sensorManager != null) {
            sensorManager.unregisterListener(this)
            isListening = false
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        // 1. Apply Low-Pass Filter to extract and smooth gravity
        gravity[0] = ALPHA * gravity[0] + (1f - ALPHA) * event.values[0]
        gravity[1] = ALPHA * gravity[1] + (1f - ALPHA) * event.values[1]
        gravity[2] = ALPHA * gravity[2] + (1f - ALPHA) * event.values[2]

        // 2. High-pass filter: isolate pure linear acceleration (user shake force)
        linearAcceleration[0] = event.values[0] - gravity[0]
        linearAcceleration[1] = event.values[1] - gravity[1]
        linearAcceleration[2] = event.values[2] - gravity[2]

        // 3. Convert to G-force units (1G = 9.80665 m/s²)
        val gX = linearAcceleration[0] / SensorManager.GRAVITY_EARTH
        val gY = linearAcceleration[1] / SensorManager.GRAVITY_EARTH
        val gZ = linearAcceleration[2] / SensorManager.GRAVITY_EARTH

        val totalGForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

        // 4. Check threshold and slop time window
        if (totalGForce >= thresholdG) {
            val now = System.currentTimeMillis()
            if (now - lastShakeTimestamp >= slopTimeMs) {
                lastShakeTimestamp = now
                onShakeListener.invoke()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op for shake detection
    }
}
