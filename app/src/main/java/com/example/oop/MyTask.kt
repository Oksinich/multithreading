package com.example.oop

import android.util.Log
import kotlin.random.Random

class MyTask(
	val priority: Int,
	val runnable: Runnable,
	val needResourceA: Int,
	val needResourceB: Int
): Comparable<MyTask> {

	override fun compareTo(other: MyTask): Int {
		return this.priority.compareTo(other.priority)
	}
}