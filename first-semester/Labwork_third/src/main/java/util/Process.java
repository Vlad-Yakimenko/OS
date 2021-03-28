package util;

public class Process {

    public int cpuTime;
    public int IOBlocking;
    public int cpuDone;
    public int IONext;
    public int numBlocked;

    public Process(int cpuTime, int IOBlocking, int cpuDone, int IONext, int numBlocked) {
        this.cpuTime = cpuTime;
        this.IOBlocking = IOBlocking;
        this.cpuDone = cpuDone;
        this.IONext = IONext;
        this.numBlocked = numBlocked;
    }
}
