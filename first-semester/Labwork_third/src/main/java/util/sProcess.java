package util;

public class sProcess {

    public int cpuTime;
    public int delay;
    public int cpuDone;
    public int ioNext;
    public int numBlocked;

    public sProcess(int cpuTime, int delay, int cpuDone, int ioNext, int numBlocked) {
        this.cpuTime = cpuTime;
        this.delay = delay;
        this.cpuDone = cpuDone;
        this.ioNext = ioNext;
        this.numBlocked = numBlocked;
    }
}
