package com.example.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// step 1. new a taskQueue, and start task.
// step 2. put a task to the queue.
// step 3. finish the task.
// step 4. stop task.

public class TaskQueue {

    BlockingQueue<Task> mTaskList = new LinkedBlockingQueue<Task>(2);

    private boolean stop = false;

    private Thread doTaskThread;

    public void startTask() {
        doTaskThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!stop) {
                    Task task = getTask();
                    task.execute();
                }
            }
        });
    }

    public void stopTask() {
        stop = true;
        System.out.println("[ServerThread] shutdown.");
        if (doTaskThread != null) {
            doTaskThread.interrupt();
        }
    }

    public synchronized Task getTask() {
        while (mTaskList.size() == 0) {
            try {
                this.wait();
            } catch (InterruptedException ie) {
                return null;
            }
        }

        Task task = null;
        try {
            task = mTaskList.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return task;
    }

    public synchronized void putTask(final Task task) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mTaskList.add(task);
                this.notifyAll();
            }
        }).start();
    }

}
