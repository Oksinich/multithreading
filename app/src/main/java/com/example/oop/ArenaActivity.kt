package com.example.oop

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import java.util.concurrent.*

@RequiresApi(Build.VERSION_CODES.N)
class ArenaActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val threadPool = ThreadPoolExecutor(
			4, Int.MAX_VALUE, 0,
			TimeUnit.SECONDS,
			LinkedBlockingQueue(10)
		)

		val taskOrchestra = TaskOrchestra(
			threadPool
		)

		/**
		 * Создаем очередь задач
		 */
		threadPool.execute {
			while (true) {
				Thread.sleep(500)
				taskOrchestra.newTask()
			}
		}
	}
}

/**
 * я так заебалась блять
 * почему так непонятно то нихуя
 * скока можно
 * скорее бы в отпуск
 */