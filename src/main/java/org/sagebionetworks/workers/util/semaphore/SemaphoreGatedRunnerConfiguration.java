package org.sagebionetworks.workers.util.semaphore;

import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.common.util.progress.ProgressingRunner;

/**
 * Configuration for a SemaphoreGatedRunner.
 * 
 * @param <T>
 *            The type of the ProgressingRunner
 */
public class SemaphoreGatedRunnerConfiguration<T> {

	ProgressingRunner<T> runner;
	ProgressCallback<T> progressCallack;
	String lockKey;
	long lockTimeoutSec = -1;
	int maxLockCount = -1;

	public SemaphoreGatedRunnerConfiguration() {
		super();
	}

	/**
	 * 
	 * @param runner
	 *            The runner that will be called when a lock is acquired. The
	 *            lock will be held for the duration of this runners run()
	 *            method.
	 * @param lockKey
	 *            The semaphore lock key that must be held in order to run the
	 *            runner.
	 * @param lockTimeoutSec
	 *            The timeout of the semaphore lock in seconds. The runner must
	 *            either terminate before this timeout expires or call
	 *            {@link ProgressCallback#progressMade()} to extend the timeout.
	 * @param maxLockCount
	 *            The maximum number of concurrent locks that can be issued for
	 *            the given semaphore key. If the runner is expected to be a
	 *            singleton, then set this value to one.
	 */
	public SemaphoreGatedRunnerConfiguration(ProgressingRunner<T> runner,
			String lockKey, long lockTimeoutSec, int maxLockCount) {
		this(runner, lockKey, lockTimeoutSec, maxLockCount, null);
	}

	/**
	 * 
	 * @param runner
	 *            The runner that will be called when a lock is acquired. The
	 *            lock will be held for the duration of this runners run()
	 *            method.
	 * @param lockKey
	 *            The semaphore lock key that must be held in order to run the
	 *            runner.
	 * @param lockTimeoutSec
	 *            The timeout of the semaphore lock in seconds. The runner must
	 *            either terminate before this timeout expires or call
	 *            {@link ProgressCallback#progressMade()} to extend the timeout.
	 * @param maxLockCount
	 *            The maximum number of concurrent locks that can be issued for
	 *            the given semaphore key. If the runner is expected to be a
	 *            singleton, then set this value to one.
	 * @param progressCallback
	 *            An optional parameter. When provided, progress made events
	 *            will be forwarded to this callback as well.
	 */
	public SemaphoreGatedRunnerConfiguration(ProgressingRunner<T> runner,
			String lockKey, long lockTimeoutSec, int maxLockCount,
			ProgressCallback<T> progressCallback) {
		super();
		this.runner = runner;
		this.lockKey = lockKey;
		this.lockTimeoutSec = lockTimeoutSec;
		this.maxLockCount = maxLockCount;
		this.progressCallack = progressCallback;
	}

	/**
	 * The runner that will be called when a lock is acquired. The lock will be
	 * held for the duration of this runner's run() method.
	 * 
	 * @return
	 */
	public ProgressingRunner<T> getRunner() {
		return runner;
	}

	/**
	 * The runner that will be called when a lock is acquired. The lock will be
	 * held for the duration of this runner's run() method.
	 * 
	 * @param runner
	 */
	public void setRunner(ProgressingRunner<T> runner) {
		this.runner = runner;
	}

	/**
	 * The semaphore lock key that must be held in order to run the runner.
	 * 
	 * @return
	 */
	public String getLockKey() {
		return lockKey;
	}

	/**
	 * The semaphore lock key that must be held in order to run the runner.
	 * 
	 * @param lockKey
	 */
	public void setLockKey(String lockKey) {
		this.lockKey = lockKey;
	}

	/**
	 * The timeout of the semaphore lock in seconds. The runner must either
	 * terminate before this timeout expires or call
	 * {@link ProgressCallback#progressMade()} to extend the timeout.
	 * 
	 * @return
	 */
	public long getLockTimeoutSec() {
		return lockTimeoutSec;
	}

	/**
	 * The timeout of the semaphore lock in seconds. The runner must either
	 * terminate before this timeout expires or call
	 * {@link ProgressCallback#progressMade()} to extend the timeout.
	 * 
	 * @param lockTimeoutSec
	 */
	public void setLockTimeoutSec(long lockTimeoutSec) {
		this.lockTimeoutSec = lockTimeoutSec;
	}

	/**
	 * The maximum number of concurrent locks that can be issued for the given
	 * semaphore key. If the runner is expected to be a singleton, then set this
	 * value to one.
	 * 
	 * @return
	 */
	public int getMaxLockCount() {
		return maxLockCount;
	}

	/**
	 * The maximum number of concurrent locks that can be issued for the given
	 * semaphore key. If the runner is expected to be a singleton, then set this
	 * value to one.
	 * 
	 * @param maxLockCount
	 */
	public void setMaxLockCount(int maxLockCount) {
		this.maxLockCount = maxLockCount;
	}

//	/**
//	 * An optional parameter. When provided, progress made events will be
//	 * forwarded to this callback as well.
//	 * 
//	 * @return
//	 */
//	public ProgressCallback<T> getProgressCallack() {
//		return progressCallack;
//	}
//
//	/**
//	 * An optional parameter. When provided, progress made events will be
//	 * forwarded to this callback as well.
//	 * 
//	 * @param progressCallack
//	 */
//	public void setProgressCallack(ProgressCallback<T> progressCallack) {
//		this.progressCallack = progressCallack;
//	}

}
