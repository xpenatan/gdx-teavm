package com.badlogic.gdx.utils;

import com.github.xpenatan.gdx.backend.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.backend.web.dom.WindowWrapper;

/**
 * @author xpenatan
 */
public class Timer {

	static private final int CANCELLED = -1;
	static private final int FOREVER = -2;

	/**
	 * Timer instance for general application wide usage. Static methods on Timer
	 * make convenient use of this instance.
	 */
	static Timer instance = new Timer();

	static public Timer instance() {
		if (instance == null) {
			instance = new Timer();
		}
		return instance;
	}

	private final Array<Task> tasks = new Array<>(false, 8);

	public Timer() {

	}

	/**
	 * Schedules a task to occur once as soon as possible, but not sooner than the
	 * start of the next frame.
	 */
	public static Timer.Task postTask(Timer.Task task) {
		return scheduleTask(task, 0, 0, 0);
	}

	/** Schedules a task to occur once after the specified delay. */
	public static Timer.Task scheduleTask(Timer.Task task, float delaySeconds) {
		return scheduleTask(task, delaySeconds, 0, 0);
	}

	/**
	 * Schedules a task to occur once after the specified delay and then repeatedly
	 * at the specified interval until cancelled.
	 */
	public static Timer.Task scheduleTask(Timer.Task task, float delaySeconds, float intervalSeconds) {
		return scheduleTask(task, delaySeconds, intervalSeconds, FOREVER);
	}

	/**
	 * Schedules a task to occur once after the specified delay and then a number of
	 * additional times at the specified interval.
	 */
	public static Timer.Task scheduleTask(Timer.Task task, float delaySeconds, float intervalSeconds, int repeatCount) {
		// if (task.repeatCount != CANCELLED) throw new IllegalArgumentException("The
		// same task may not be scheduled twice.");
		WindowWrapper window = WebApplicationConfiguration.JSHelper.getCurrentWindow();
		window.setTimeout(task, (int) (delaySeconds * 1000));
		return task;
	}

	public static Task schedule(Runnable task, float delaySeconds) {
		return scheduleTask(new Task() {
			@Override
			public void run() {
				task.run();
			}
		}, delaySeconds);
	}

	/**
	 * Schedules a task on {@link #instance}.
	 *
	 * @see #scheduleTask(Task, float, float)
	 */
	public static Task schedule(Runnable task, float delaySeconds, float intervalSeconds) {
		return scheduleTask(new Task() {
			@Override
			public void run() {
				task.run();
			}
		}, delaySeconds, intervalSeconds);
	}

	/**
	 * Schedules a task on {@link #instance}.
	 *
	 * @see #scheduleTask(Task, float, float, int)
	 */
	public static Task schedule(Runnable task, float delaySeconds, float intervalSeconds, int repeatCount) {
		return scheduleTask(new Task() {
			@Override
			public void run() {
				task.run();
			}
		}, delaySeconds, intervalSeconds, repeatCount);
	}

	/**
	 * Stops the timer, tasks will not be executed and time that passes will not be
	 * applied to the task delays.
	 */
	public void stop() {

	}

	/** Starts the timer if it was stopped. */
	public void start() {

	}

	/** Cancels all tasks. */
	public void clear() {
		synchronized (tasks) {
			for (int i = 0, n = tasks.size; i < n; i++)
				tasks.get(i).cancel();
			tasks.clear();
		}
	}

	long update(long timeMillis, long waitMillis) {
		return 0;
	}

	/** Adds the specified delay to all tasks. */
	public void delay(long delayMillis) {

	}

	static void wake() {

	}

	static public Timer.Task post(Timer.Task task) {
		return instance().postTask(task);
	}

	static public Timer.Task schedule(Timer.Task task, float delaySeconds) {
		return instance().scheduleTask(task, delaySeconds);
	}

	static public Timer.Task schedule(Timer.Task task, float delaySeconds, float intervalSeconds) {
		return instance().scheduleTask(task, delaySeconds, intervalSeconds);
	}

	static public Timer.Task schedule(Timer.Task task, float delaySeconds, float intervalSeconds, int repeatCount) {
		return instance().scheduleTask(task, delaySeconds, intervalSeconds, repeatCount);
	}

	/**
	 * Runnable with a cancel method.
	 *
	 * @see Timer
	 * @author Nathan Sweet
	 */
	static abstract public class Task implements Runnable {
		long executeTimeMillis;
		long intervalMillis;
		int repeatCount = CANCELLED;

		/**
		 * If this is the last time the task will be ran or the task is first cancelled,
		 * it may be scheduled again in this method.
		 */
		abstract public void run();

		/**
		 * Cancels the task. It will not be executed until it is scheduled again. This
		 * method can be called at any time.
		 */
		public void cancel() {
			executeTimeMillis = 0;
			repeatCount = CANCELLED;
		}

		/**
		 * Returns true if this task is scheduled to be executed in the future by a
		 * timer.
		 */
		public boolean isScheduled() {
			return repeatCount != CANCELLED;
		}

		/** Returns the time when this task will be executed in milliseconds */
		public long getExecuteTimeMillis() {
			return executeTimeMillis;
		}
	}
}