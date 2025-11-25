package com.example.a06_04

import android.graphics.Canvas
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a06_04.ui.theme._06_04Theme
import kotlinx.coroutines.delay
import kotlin.math.max

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    // Use the gyroscope to detect phone tilt.
    private var gyroscope: Sensor? = null

    private var _x by mutableFloatStateOf(0f)
    private var _y by mutableFloatStateOf(0f)
    private var _accuracy by mutableStateOf("Unknown")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Sensor Manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            _06_04Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Maze(gx = _x, gy = _y)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            _x = it.values[0]
            _y = it.values[1]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _accuracy = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "High"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Medium"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "Low"
            SensorManager.SENSOR_STATUS_UNRELIABLE -> "Unreliable"
            else -> "Unknown"
        }
    }
}
@Composable
fun Maze(gx: Float, gy: Float) {
    var ballX by remember { mutableStateOf(370f) }
    var ballY by remember { mutableStateOf(710f) }
    val ballRadius = 20f

    // Add walls and obstacles for a simple maze game.
    val walls = listOf(
        Rect(50f, 50f, 750f, 70f),
        Rect(50f, 50f, 70f, 750f),
        Rect(50f, 730f, 750f, 750f),
        Rect(730f, 50f, 750f, 750f),

        Rect(50f, 150f, 350f, 170f),
        Rect(450f, 150f, 750f, 170f),

        Rect(50f, 250f, 550f, 270f),
        Rect(150f, 250f, 170f, 400f),

        Rect(550f, 250f, 570f, 400f),

        Rect(250f, 480f, 650f, 500f),

        Rect(450f, 350f, 470f, 650f),

        Rect(150f, 550f, 300f, 570f),
        Rect(150f, 550f, 170f, 750f),

        Rect(250f, 650f, 650f, 670f),

        Rect(650f, 650f, 670f, 750f)
    )


    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        walls.forEach { drawRect(Color.Blue, it.topLeft, it.size) }

        var newX = ballX + gx * 5
        var newY = ballY + gy * 5

        walls.forEach { wall ->
            if (circleRectCollision(newX, ballY, wall)) {
                newX = ballX
            }
            if (circleRectCollision(ballX, newY, wall)) {
                newY = ballY
            }
        }

        ballX = newX.coerceIn(ballRadius, size.width - ballRadius)
        ballY = newY.coerceIn(ballRadius, size.height - ballRadius)


        drawCircle(Color.Green, radius = ballRadius, center = Offset(ballX, ballY))
    }
}

private fun circleRectCollision(cx: Float, cy: Float, rect: Rect): Boolean {
    val closestX = max(rect.left, kotlin.math.min(cx, rect.right))
    val closestY =  max(rect.top, kotlin.math.min(cy, rect.bottom))
    val dx = cx - closestX
    val dy = cy - closestY
    return dx * dx + dy * dy < 12.0 * 12.0
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    _06_04Theme {
        Maze(gx = 0f, gy = 0f)

    }
}