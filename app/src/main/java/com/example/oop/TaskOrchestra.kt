package com.example.oop

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.*
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.N)
class TaskOrchestra(
    private val threadPoolExecutor: ThreadPoolExecutor
) {

    private val mBlockingQueue: PriorityBlockingQueue<MyTask> = PriorityBlockingQueue(
        50,
        Comparator.reverseOrder()
    )
    private val resourceA: MutableList<ResourceA> = mutableListOf()

    private val resourceB: MutableList<ResourceB> = mutableListOf()

    private val priorityArr =
        arrayListOf(Thread.MAX_PRIORITY, Thread.MIN_PRIORITY, Thread.NORM_PRIORITY)

    init {
        fillResources()
    }

    @Synchronized
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
        chooseTaskAndStart()
    }

    private fun chooseTaskAndStart() {

        val task = mBlockingQueue.take()
        Log.d("ASD", "${resourceA.size} before aaa   ${resourceB.size} bbbb")

        if (task.needResourceA <= resourceA.size && task.needResourceB <= resourceB.size) {
            resourceA.subList(0, task.needResourceA).clear()
            resourceB.subList(0, task.needResourceB).clear()
//            Log.d("ASD", "${task.needResourceA}  need a   ${task.needResourceB} need bbbb11")
            execute(task)
        } else {
            when (task.thread.priority) {
                Thread.MAX_PRIORITY -> {
                    Log.d("ASD", "tipo wait")
                    /**
                     * шо если я её тоже просто верну в очередь
                     * так переберутся приоритетные задачи, вдруг на какую то будут ресурсы
                     * если она единственная, то после завершения других задач всё равно она будет первая в очереди
                     */
                    mBlockingQueue.put(task)
                }

                else -> {
                    mBlockingQueue.put(task)
                    Log.d("ASD", "вернули обратно лоховскую задачу")
                }
            }
        }
    }


    private fun execute(task: MyTask) {
        Log.d("ASD", "EXECUTE")
        threadPoolExecutor.execute {
            task.thread.run()
            onTaskFinished(task)
        }
    }

    @Synchronized
    private fun onTaskFinished(
        task: MyTask
    ) {
        Log.d("ASD", "FINISHED")
        releaseResA(resourceA, task.needResourceA)
        releaseResB(resourceB, task.needResourceB)
        chooseTaskAndStart()
    }

    private fun getPriority(): Int {
        val pr = Random.nextInt(priorityArr.size)
        return priorityArr[pr]
    }

    @Synchronized
    private fun fillResources() {
        val resACount = Random.nextInt(15, 25)
        val resBCount = Random.nextInt(15, 25)
        Log.d("ASD", "all a $resACount  b $resBCount")

        for (i in 0 until resACount) {
            resourceA.add(ResourceA())
        }

        for (i in 0 until resBCount) {
            resourceB.add(ResourceB())
        }
    }

    @Synchronized
    private fun releaseResA(res: MutableList<ResourceA>, k: Int) {
        for (i in 0 until k) {
            res.add(ResourceA())
        }
    }

    @Synchronized
    private fun releaseResB(res: MutableList<ResourceB>, k: Int) {
        for (i in 0 until k) {
            res.add(ResourceB())
        }
    }
}
