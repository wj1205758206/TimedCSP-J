package common.classes.assertion.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public final class PATThreadPool {

    private List<FairThread> WorkThreads = new ArrayList<>(16);


    private TarjanThread Tarjan;


    public FairThread ResultThread;


    public boolean JobFinished;


    private int FinishedThread;


    public int ThreadNumber;

    public PATThreadPool() {
        this.JobFinished = false;
        this.ResultThread = null;
        this.FinishedThread = 0;
        this.ThreadNumber = 0;
    }

    public void StartModelChecking(TarjanThread tarjan) {
        this.Tarjan = tarjan;
    }

    public void AddThread(FairThread thread) {
        synchronized (this.WorkThreads)
        {
            if (!this.JobFinished) {
                this.ThreadNumber++;
                this.WorkThreads.add(thread);
                thread.ReturnAction  = this::thread_ReturnAction;
                Executors.newCachedThreadPool().execute(() -> {
                    try {
                        thread.InternalStart(null);
                    } catch (Exception e) {
                        throw new RuntimeException("AddThread: " + e.getMessage());
                    }
                });
                //thread.ReturnAction += this.thread_ReturnAction;
                //ThreadPool.QueueUserWorkItem(new WaitCallback(thread.InternalStart), null);
            }
        }
    }

    public void StopAllThreads() {
        synchronized (this.WorkThreads)
        {
            for(FairThread fairThread : this.WorkThreads)
            {
                fairThread.CancelRequested = true;
            }
        }
    }

    private void thread_ReturnAction(FairThread fairThread) {
        synchronized (this.WorkThreads)
        {
            if (fairThread.result) {
                this.ResultThread = fairThread;
                this.JobFinished = true;
                this.Tarjan.JobFinished = true;
                for(FairThread fairThread2 : this.WorkThreads)
                {
                    fairThread2.CancelRequested = true;
                }
            }
            fairThread.ReturnAction = null;
            //fairThread.ReturnAction -= this.thread_ReturnAction;
            this.FinishedThread++;
            this.ThreadNumber--;
        }
    }

    public void WaitForAllDone() throws InterruptedException {
        synchronized (this)
        {
            while (!this.JobFinished && this.FinishedThread < this.WorkThreads.size()) {
                //Monitor.Wait(this, 100);
                this.wait(100);
            }
        }
    }

}
