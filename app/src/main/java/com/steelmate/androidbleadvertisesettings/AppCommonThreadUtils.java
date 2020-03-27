package com.steelmate.androidbleadvertisesettings;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * created by XuTi on 2019/4/29 15:38
 */
public class AppCommonThreadUtils {
    private static ThreadPoolExecutor sThreadPoolExecutor;

    public static ThreadPoolExecutor getThreadPoolExecutor(final String name) {
        if (sThreadPoolExecutor == null) {
            ThreadFactory threadFactory = new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable r) {
                    return new Thread(r, name);
                }
            };
            sThreadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                                         60L, TimeUnit.SECONDS,
                                                         new SynchronousQueue<Runnable>(), threadFactory);
            sThreadPoolExecutor.setThreadFactory(threadFactory);
        }
        return sThreadPoolExecutor;
    }

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return getThreadPoolExecutor("");
    }

    public static void doBackgroundWork(Runnable runnable, final String name) {
        getThreadPoolExecutor(name).execute(runnable);
    }


    public static void doBackgroundWork(Runnable runnable) {
        doBackgroundWork(runnable, "app background work thread");
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static void doMainWork(Runnable runnable) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(runnable);
    }

    /**
     * Timer在执行定时任务时只会创建一个线程，所以如果存在多个任务，且任务时间过长，超过了两个任务的间隔时间，会发生一些缺陷
     * 如果Timer调度的某个TimerTask抛出异常，Timer会停止所有任务的运行
     * Timer执行周期任务时依赖系统时间,修改系统时间容易导致任务被挂起（如果当前时间小于执行时间）
     * ---------------------
     * 作者：Smart_Arvin
     * 来源：CSDN
     * 原文：https://blog.csdn.net/u010229714/article/details/73845748
     * 版权声明：本文为博主原创文章，转载请附上博文链接！
     *
     * @param timerTask
     * @param delay
     * @param period
     */
    public static void startTimerTask(TimerTask timerTask, long delay, long period) {
        new Timer().schedule(timerTask, delay, period);
    }


    /**
     * 循环任务，按照上一次任务的发起时间计算下一次任务的开始时间
     */
    public static void scheduleAtFixedRate() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //execute task
            }
        };
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r);
            }
        };
        ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(1, threadFactory);
        pool.scheduleAtFixedRate(task, 0, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 循环任务，以上一次任务的结束时间计算下一次任务的开始时间
     */
    public static void scheduleWithFixedDelay() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //execute task
            }
        };
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r);
            }
        };
        ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(1, threadFactory);
        pool.scheduleWithFixedDelay(task, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public static Handler getThreadHandler() {
        HandlerThread handlerThread = new HandlerThread("");
        handlerThread.start();
        return new Handler(handlerThread.getLooper());
    }

    public static Handler getMainHandler() {
        return new Handler(Looper.getMainLooper());
    }

    public static class LimitTimesTimerTaskController {
        private volatile int                                             mTimes;
        private          long                                            mDelay;
        private          long                                            mPeriod;
        private          LimitTimesTimerTask.LimitTimesTimerTaskCallback mLimitTimesTimerTaskCallback;
        private          LimitTimesTimerTask                             mLimitTimesTimerTask;

        public LimitTimesTimerTaskController(int times, long delay, long period, LimitTimesTimerTask.LimitTimesTimerTaskCallback limitTimesTimerTaskCallback) {
            mTimes = times;
            mDelay = delay;
            mPeriod = period;
            mLimitTimesTimerTaskCallback = limitTimesTimerTaskCallback;
        }


        public void startLimitTimesTimerTak() {
            cancelLimitTimesTimerTask();
            mLimitTimesTimerTask = new LimitTimesTimerTask(mTimes, mLimitTimesTimerTaskCallback);
            startTimerTask(mLimitTimesTimerTask, mDelay, mPeriod);
        }

        public void cancelLimitTimesTimerTask() {
            if (mLimitTimesTimerTask != null) {
                mLimitTimesTimerTask.cancel();
            }
        }

        public int getHavingDoneTaskTimes(){
            return mTimes-mLimitTimesTimerTask.getTimes();
        }

        public void setTimes(int times) {
            mTimes = times;
        }
    }
}
