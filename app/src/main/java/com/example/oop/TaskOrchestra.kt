package com.example.oop

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.locks.ReentrantLock
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.N)
class TaskOrchestra(
	private val threadPoolExecutor: ThreadPoolExecutor
) {

	private val mBlockingQueue: PriorityBlockingQueue<MyTask> = PriorityBlockingQueue(
		50,
		Comparator.reverseOrder()
	)
	private val resACount = Random.nextInt(15, 25)
	private val resBCount = Random.nextInt(15, 25)

	private var resourceA: LinkedBlockingQueue<ResourceA> = LinkedBlockingQueue(resACount)

	private var resourceB: LinkedBlockingQueue<ResourceB> = LinkedBlockingQueue(resBCount)

	private val lock = ReentrantLock()
	private val c = lock.newCondition()

	private val priorityArr = arrayListOf(Thread.MAX_PRIORITY, Thread.MIN_PRIORITY, Thread.NORM_PRIORITY)

	init {
		fillResources()
	}

	fun newTask() {
		val thread = Thread {
			Thread.sleep(Random.nextLong(1000, 4000))
		}
		thread.priority = getPriority()

		mBlockingQueue.put(
			MyTask(
				thread,
				Random.nextInt(1, 15),
				Random.nextInt(1, 15)
			)
		)
	}

	fun taskFinished() {
		// чота ещё наверна
		c.signalAll()
		doChota()
	}

	fun doChota() {
		val blockedResourceA: ArrayBlockingQueue<ResourceA> = ArrayBlockingQueue(15)
		val blockedResourceB: ArrayBlockingQueue<ResourceB> = ArrayBlockingQueue(15)
		val task = mBlockingQueue.take()
		lock.lock()
		Log.d("ASD", "${resourceA.size}  ${resourceB.size}")
		if (task.needResourceA > resourceA.size || task.needResourceB > resourceB.size) {
			when (task.thread.priority) {
				Thread.MAX_PRIORITY -> {
					c.await()
				}
				else -> {
					mBlockingQueue.put(task)
				}
			}
		} else {
			resourceA.drainTo(blockedResourceA, task.needResourceA)
			resourceB.drainTo(blockedResourceB, task.needResourceB)

			lock.unlock()
			Log.d("ASD", "need b ${task.needResourceB} block ${blockedResourceB.size}")
			threadPoolExecutor.execute {
				try {
					task.thread.run()
					lock.lock()
					blockedResourceA.drainTo(resourceA)
					blockedResourceA.clear()
					blockedResourceB.drainTo(resourceB)
					blockedResourceB.clear()
					taskFinished()
					lock.unlock()
				} catch (e: IllegalThreadStateException) {
					Log.e("ASD", e.toString())
				}
			}

		}
	}

	@Synchronized
	private fun chooseTaskAndStart() {

	}

	@Synchronized
	private fun onTaskFinished() {

	}

	private fun getPriority(): Int {
		val pr = Random.nextInt(priorityArr.size)
		return priorityArr[pr]
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
}