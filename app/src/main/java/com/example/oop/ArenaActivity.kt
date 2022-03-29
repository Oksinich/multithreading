package com.example.oop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.lang.Exception
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.random.Random

class ArenaActivity : AppCompatActivity() {

	private val mBlockingQueue: PriorityBlockingQueue<MyTask> = PriorityBlockingQueue()

	private val resACount = Random.nextInt(15, 25)
	private val resBCount = Random.nextInt(15, 25)

	private var resourceA: LinkedBlockingQueue<ResourceA> = LinkedBlockingQueue(resACount)

	private var resourceB: LinkedBlockingQueue<ResourceB> = LinkedBlockingQueue(resBCount)
	private val lock = ReentrantLock()
	private val condition = lock.newCondition()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		fillResources()

		mBlockingQueue.clear()
		Thread {
			val blockedResourceA: ArrayBlockingQueue<ResourceA> = ArrayBlockingQueue(15)
			val blockedResourceB: ArrayBlockingQueue<ResourceB> = ArrayBlockingQueue(15)
			while (true) {
				val task = mBlockingQueue.take()
				if (getRes(task, blockedResourceA, blockedResourceB)) {
					taskJob(1, task, blockedResourceA, blockedResourceB)
				}
			}
		}.start()

		Thread {
			val blockedResourceA: ArrayBlockingQueue<ResourceA> = ArrayBlockingQueue(15)
			val blockedResourceB: ArrayBlockingQueue<ResourceB> = ArrayBlockingQueue(15)
			while (true) {
				val task = mBlockingQueue.take()
				if (getRes(task, blockedResourceA, blockedResourceB)) {
					taskJob(2, task, blockedResourceA, blockedResourceB)
				}
			}
		}.start()


		Thread {
			val blockedResourceA: ArrayBlockingQueue<ResourceA> = ArrayBlockingQueue(15)
			val blockedResourceB: ArrayBlockingQueue<ResourceB> = ArrayBlockingQueue(15)
			while (true) {
				val task = mBlockingQueue.take()
				if (getRes(task, blockedResourceA, blockedResourceB)) {
					taskJob(3, task, blockedResourceA, blockedResourceB)
				}
			}
		}.start()

		/**
		 * Создаем очередь задач
		 */
		Thread {
			while (true) {
				Thread.sleep(500)
				mBlockingQueue.put(
					MyTask(
						ThreadLocalRandom.current().nextInt(1, 5), Runnable {
							Thread.sleep(Random.nextLong(1000, 4000))
						},
						Random.nextInt(1, 15),
						Random.nextInt(1, 15)
					)
				)
			}
		}.start()
	}

	private fun fillResources() {
		resourceA.clear()
		resourceB.clear()
		for (i in 0 until resACount) {
			resourceA.add(ResourceA())
		}

		for (i in 0 until resBCount) {
			resourceB.add(ResourceB())
		}
	}

	private fun getRes(
		t: MyTask,
		blockedResourceA: ArrayBlockingQueue<ResourceA>,
		blockedResourceB: ArrayBlockingQueue<ResourceB>
	): Boolean {
		synchronized(resourceA) {
			synchronized(resourceB) {
				lock.lock()
				while (resourceA.size < t.needResourceA && resourceB.size < t.needResourceB) {
					if (t.priority == 1) {
						condition.await()
						Log.d("RES", "ЖДЁМ")
					} else {
						mBlockingQueue.put(t)
						Log.d("RES", "БЕРЁМ НЕКСТ")
						return false
					}
				}
				resourceA.drainTo(blockedResourceA, t.needResourceA)
				resourceB.drainTo(blockedResourceB, t.needResourceB)
			}
		}
		lock.unlock()
		return true
	}

	/**
	 * пуш модель нада
	 */
	private fun taskJob(
		threadId: Int,
		t: MyTask,
		blockedResourceA: ArrayBlockingQueue<ResourceA>,
		blockedResourceB: ArrayBlockingQueue<ResourceB>
	) {
		try {
			// todo всё равно прилетает типо "надо 14 в блоке 5" (((((
			Log.d(
				"NEED",
				"need ${t.needResourceB}  в блоке ${blockedResourceB.size} приоритет ${t.priority}"
			)
			t.runnable.run()
			Log.d("ASDASD", "DONE $threadId")

			// освоболждение
			//todo шото всё локаю, синхранизирую и херня какая то
			lock.lock()
			synchronized(resourceA) {
				synchronized(resourceB) {
					blockedResourceA.drainTo(resourceA)
					blockedResourceA.clear()
					blockedResourceB.drainTo(resourceB)
					blockedResourceB.clear()
					condition.signalAll()
				}
			}
		} catch (e: InterruptedException) {
			e.stackTrace
		} finally {
			lock.unlock()
		}
	}
}