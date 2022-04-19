package com.example.oop

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.*
import java.util.concurrent.*
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.N)
class ArenaActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val threadPool = ThreadPoolExecutor(
			10, Int.MAX_VALUE, 0,
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

		taskOrchestra.doChota()
	}

//	private fun getRes(
//		t: MyTask,
//		blockedResourceA: ArrayBlockingQueue<ResourceA>,
//		blockedResourceB: ArrayBlockingQueue<ResourceB>
//	): Boolean {
//		synchronized(resourceA) {
//			synchronized(resourceB) {
//				while (resourceA.size < t.needResourceA && resourceB.size < t.needResourceB) {
//
//				}
//				resourceA.drainTo(blockedResourceA, t.needResourceA)
//				resourceB.drainTo(blockedResourceB, t.needResourceB)
//			}
//		}
//		return true
//	}

	/**
	 * пуш модель нада
	 */
//	private fun taskJob(
//		t: MyTask,
//		blockedResourceA: ArrayBlockingQueue<ResourceA>,
//		blockedResourceB: ArrayBlockingQueue<ResourceB>
//	) {
//		try {
//			// todo всё равно прилетает типо "надо 14 в блоке 5" (((((
//			Log.d(
//				"ASD",
//				"need ${t.needResourceB}  в блоке ${blockedResourceB.size} приоритет ${t.thread.priority}"
//			)
//			t.thread.start()
////			taskOrchestra.taskFinished()
//
//			// освоболждение
//			//todo шото всё локаю, синхранизирую и херня какая то
//			synchronized(resourceA) {
//				synchronized(resourceB) {
//					blockedResourceA.drainTo(resourceA)
//					blockedResourceA.clear()
//					blockedResourceB.drainTo(resourceB)
//					blockedResourceB.clear()
//				}
//			}
//		} catch (e: InterruptedException) {
//			e.stackTrace
//		}
//	}
}

/**
 * я так заебалась блять
 * почему так непонятно то нихуя
 * скока можно
 * скорее бы в отпуск
 */