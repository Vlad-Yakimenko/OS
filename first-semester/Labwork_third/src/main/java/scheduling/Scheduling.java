package scheduling;
// simulation.  Init() initializes most of the variables by
// reading from a provided file.  scheduling.SchedulingAlgorithm.Run() is
// called from main() to run the simulation.  Summary-Results
// is where the summary results are written, and Summary-Processes
// is where the process scheduling summary is written.

// Created by Alexander Reeder, 2001 January 06

import java.io.*;
import java.util.*;

import util.Common;
import util.Results;
import util.sProcess;

import static scheduling.AlgorithmKt.Run;

public class Scheduling {

    private static int quantum = 100;
    private static int processnum = 5;
    private static int meanDev = 1000;
    private static int standardDev = 100;
    private static int runtime = 1000;
    private static final Vector<sProcess> processVector = new Vector<>();
    private static Results result = new Results("null", "null", 0);
    private static String resultsFile = "Summary-Results";

    private static void Init(String file) {
        File f = new File(file);
        String line;
        String tmp;
        int cputime;
        int ioblocking;
        double X;

        try {
            //BufferedReader in = new BufferedReader(new FileReader(f));
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            while ((line = in.readLine()) != null) {
                if (line.startsWith("numprocess")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    processnum = Common.s2i(st.nextToken());
                }
                if (line.startsWith("meandev")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    meanDev = Common.s2i(st.nextToken());
                }
                if (line.startsWith("standdev")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    standardDev = Common.s2i(st.nextToken());
                }
                if (line.startsWith("quantum")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    quantum = Common.s2i(st.nextToken());
                }
                if (line.startsWith("process")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    ioblocking = Common.s2i(st.nextToken());
                    X = Common.R1();
                    while (X == -1.0) {
                        X = Common.R1();
                    }
                    X = X * standardDev;
                    cputime = (int) (Math.random() * standardDev) + meanDev;
                    processVector.addElement(new sProcess(cputime, ioblocking, 0, 0, 0));
                }
                if (line.startsWith("runtime")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    runtime = Common.s2i(st.nextToken());
                }
            }
            in.close();
        } catch (IOException e) { /* Handle exceptions */ }
    }

    private static void debug() {
        int i;

        System.out.println("processnum " + processnum);
        System.out.println("meandevm " + meanDev);
        System.out.println("standdev " + standardDev);
        int size = processVector.size();
        for (i = 0; i < size; i++) {
            sProcess process = processVector.elementAt(i);
            System.out.println("process " + i + " " + process.cpuTime + " " + process.delay + " " + process.cpuDone + " " + process.numBlocked);
        }
        System.out.println("runtime " + runtime);
    }

    public static void main(String[] args) {
        int i;
        String filename = "scheduling.conf";

        File f = new File(filename);
        if (!(f.exists())) {
            System.out.println("Scheduling: error, file '" + f.getName() + "' does not exist.");
            System.exit(-1);
        }
        if (!(f.canRead())) {
            System.out.println("Scheduling: error, read of " + f.getName() + " failed.");
            System.exit(-1);
        }
        System.out.println("Working...");
        Init(filename);

        if (processVector.size() < processnum) {
            i = 0;
            while (processVector.size() < processnum) {
                double X = Common.R1();
                while (X == -1.0) {
                    X = Common.R1();
                }
                X = X * standardDev;
                int cputime = (int) X + meanDev;
                processVector.addElement(new sProcess(cputime, i * 100, 0, 0, 0));
                i++;
            }
        }
        result = Run(quantum, runtime, processVector, result);
        try {
            //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
            out.println("Scheduling Type: " + result.schedulingType);
            out.println("Scheduling Name: " + result.schedulingName);
            out.println("Simulation Run Time: " + result.computationTime);
            out.println("Mean: " + meanDev);
            out.println("Standard Deviation: " + standardDev);
            out.println("Process #\tCPU Time\tIO Blocking\tCPU Completed\tCPU Blocked");
            for (i = 0; i < processVector.size(); i++) {
                sProcess process = processVector.elementAt(i);
                out.print(i);
                if (i < 100) {
                    out.print("\t\t");
                } else {
                    out.print("\t");
                }
                out.print(process.cpuTime);
                if (process.cpuTime < 100) {
                    out.print(" (ms)\t\t");
                } else {
                    out.print(" (ms)\t");
                }
                out.print(process.delay);
                if (process.delay < 100) {
                    out.print(" (ms)\t\t");
                } else {
                    out.print(" (ms)\t");
                }
                out.print(process.cpuDone);
                if (process.cpuDone < 100) {
                    out.print(" (ms)\t\t");
                } else {
                    out.print(" (ms)\t");
                }
                out.println(process.numBlocked + " times");
            }
            out.close();
        } catch (IOException e) { /* Handle exceptions */ }
        System.out.println("Completed.");
    }
}

