package org.sagebionetworks.workers.util.semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.common.util.progress.ProgressListener;
import org.sagebionetworks.common.util.progress.ProgressingRunner;
import org.sagebionetworks.common.util.progress.ThrottlingProgressCallback;
import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.database.semaphore.LockReleaseFailedException;

/**
 * This is not a singleton. A new instance of this gate must be created each
 * time you need one.
 * 
 * @param <T>
 *            The type of the ProgressingRunner
 * 
 */
public class SemaphoreGatedRunnerImpl<T> implements SemaphoreGatedRunner {

	private static final Logger log = LogManager
			.getLogger(SemaphoreGatedRunnerImpl.class);

	final CountingSemaphore semaphore;
	final ProgressingRunner<T> runner;
	final String lockKey;
	final long lockTimeoutSec;
	final int maxLockCount;
	final long throttleFrequencyMS;
	final ProgressCallback<T> progressCallback;

	/**
	 * 
	 * @param semaphore
	 *            A live database semaphore.
	 * @param config
	 *            configuration for this instances.
	 */
	public SemaphoreGatedRunnerImpl(CountingSemaphore semaphore,
			SemaphoreGatedRunnerConfiguration<T> config) {
		super();
		this.semaphore = semaphore;
		if (config == null) {
			throw new IllegalArgumentException("Configuration cannot be null");
		}
		this.runner = config.getRunner();
		this.lockKey = config.lockKey;
		this.lockTimeoutSec = config.getLockTimeoutSec();
		this.maxLockCount = config.getMaxLockCount();
		// the frequency that {@link ProgressCallback#progressMade(Object)}
		// calls can refresh the lock in the DB.
		this.throttleFrequencyMS = (this.lockTimeoutSec * 1000) / 3;
		this.progressCallback = new ThrottlingProgressCallback<T>(this.throttleFrequencyMS);
		validateConfig();
	}

	/**
	 * This is the run of the 'runnable'
	 */
	public void run() {
		try {
			// attempt to get a lock
			final String lockToken = semaphore.attemptToAcquireLock(
					this.lockKey, this.lockTimeoutSec, this.maxLockCount);
			
			// listen to progress events
			this.progressCallback.addProgressListener(new ProgressListener<T>() {

				@Override
				public void progressMade(T t) {
					// Give the lock more time
					semaphore.refreshLockTimeout(lockKey,
							lockToken, lockTimeoutSec);
				}
			});

			// Only proceed if a lock was acquired
			if (lockToken != null) {
				try {
					// Let the runner go while holding the lock
					runner.run(this.progressCallback);
				} finally {
					semaphore.releaseLock(this.lockKey, lockToken);
				}
			}
		}catch (LockReleaseFailedException e){
			/*
			 * Lock release failures usually mean workers are not correctly
			 * reporting progress and the semaphore is not working as expected
			 * so this exception is thrown
			 */
			throw e;
		}catch (Throwable e) {
			log.error("Error on key " + lockKey + ": ",e);
		}
	}

	private void validateConfig() {
		if (this.runner == null) {
			throw new IllegalArgumentException("Runner cannot be be null");
		}
		if (this.lockKey == null) {
			throw new IllegalArgumentException("Lock key cannot be null");
		}
		if (lockTimeoutSec < 1) {
			throw new IllegalArgumentException(
					"LockTimeoutSec cannot be less than one.");
		}
		if (maxLockCount < 1) {
			throw new IllegalArgumentException(
					"MaxLockCount cannot be less than one.");
		}
	}

}
