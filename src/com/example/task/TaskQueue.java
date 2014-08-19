package com.example.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

// step 1. new a taskQueue, and start task.
// step 2. put a task to the queue.
// step 3. finish the task.
// step 4. stop task.

public class TaskQueue {

    protected static final String TAG = "TaskQueue";

    private List<Task> mRunningTaskList = new ArrayList<Task>();

    private List<Task> mTaskList = new ArrayList<Task>();

    private List<Task> mFinishedTaskList = new ArrayList<Task>();

    private boolean stop = false;

    private String PutLock = "putting";

    private String TackLock = "Tacking";

    private Thread doTaskThread;

    public TaskQueue() {
        startTask();
    }

    private void startTask() {

        doTaskThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "[doTaskThread] startTask().");

                while (!stop) {

                    Log.d(TAG, "while getTask()");
                    Task task = getTask();

                    Log.d(TAG, "execute() id = " + task.getID());
                    task.execute();
                    mFinishedTaskList.add(task);
                    if (mFinishedTaskList.size() == mTaskList.size()) {
                        stopTask();
                    }
                }
            }
        });
        doTaskThread.start();
    }

    private void stopTask() {
        stop = true;

        Log.d(TAG, "[ServerThread] shutdown.");

        if (doTaskThread != null) {
            doTaskThread.interrupt();
        }
    }

    private Task getTask() {
        Log.d(TAG, " getTask()");
        synchronized (PutLock) {
            Log.d(TAG, " PutLock()");
            while (mRunningTaskList.size() == 0) {
                try {
                    Log.d(TAG, "mRunningTaskList.size() == 0");
                    PutLock.wait(1000);
                } catch (InterruptedException ie) {
                    return null;
                }
            }
        }

        Log.d(TAG, "mRunningTaskList.size() == " + mRunningTaskList.size());

        Task task = null;

        task = mRunningTaskList.remove(0);

        Log.d(TAG, "getTask() id = " + task.getID());
        return task;
    }

    private void putRunningTaskQueue(final Task task) {
        synchronized (PutLock) {
            Log.d(TAG, "putRunningTaskQueue id = " + task.getID());
            mRunningTaskList.add(task);
            PutLock.notifyAll();
        }
    }

    public synchronized void putTask(List<Task> tasks) {
        mTaskList = tasks;
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (mTaskList != null && mTaskList.size() > 0) {
                    for (Task task : mTaskList) {
                        Log.d(TAG, "putTask(List<Task> tasks) id = " + task.getID());
                        putRunningTaskQueue(task);
                    }
                }
            }
        }).start();
    }


}
