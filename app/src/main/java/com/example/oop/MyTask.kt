package com.example.oop

class MyTask(
	val thread: Thread,
	val needResourceA: Int,
	val needResourceB: Int
): Comparable<MyTask> {

	override fun compareTo(other: MyTask): Int {
		return this.thread.priority.compareTo(other.thread.priority)
	}
}