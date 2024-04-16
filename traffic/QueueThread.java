package traffic;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static traffic.Main.clearConsole;

public class QueueThread  extends Thread {

    private final AtomicBoolean running = new AtomicBoolean();
    private final int roads;
    private final static LinkedList<String> roadsQueue = new LinkedList<>();

    private final int interval;
    private int roadTimer;
    private int maxTime;
    public enum state {NOT_STARTED, MENU, SYSTEM}
    private state curState;
    private long timeSinceStarted = 0;


    public QueueThread(int roads, int interval) {
        this.interval = interval;
        this.roads = roads;
        this.maxTime = interval* roads;
        this.roadTimer = interval * roads;
        curState = state.NOT_STARTED;
        this.start();
        this.setName("QueueThread");
        }

    @Override
    public void run() {
        running.set(true);
        curState = state.MENU;
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!running.get()) {
                    timer.cancel();
                    timer.purge();
                    return;
                }

                if (timeSinceStarted > 0) roadTimer = roadTimer <= 1 ? maxTime : --roadTimer;
                timeSinceStarted++;

                if (curState == state.SYSTEM) {
                    if (!roadsQueue.isEmpty()) {
                    }
                    clearConsole();
                    printSystemInfo();
                }

            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);

        while(running.get()) {
        }
    }

    public void addRoad() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("input road name: ");
        String roadName = scanner.nextLine();
        if (roadsQueue.size() == roads) {
            System.out.println("queue is full");
        } else {
            roadsQueue.add(roadName);
            maxTime = interval * roadsQueue.size();
            roadTimer = Math.min(roadTimer + interval, maxTime);
            System.out.println(roadName + " Added!");
        }
    }

    public void deleteRoad() {
        if (roadsQueue.isEmpty()) {
            System.out.println("queue is empty`");
        } else {
            System.out.println(roadsQueue.pollFirst() + " Deleted!");
            maxTime = interval * roadsQueue.size();
        }
    }

    public void setState(state state) {
        this.curState = state;
    }

    public String getRoadState(int roadIndex, String roadName) {
        int max = interval * (roadsQueue.size() - roadIndex);
        int closedDecr = roadsQueue.size() - roadIndex;
        int offset = roadIndex != 0 ? closedDecr * interval : 0;
        int newOffset = roadTimer - offset <= 0 && roadsQueue.size() > 2 ? offset - (interval * (roadsQueue.size() -1)) - interval: offset;

        if (roadTimer <= max && roadTimer > max - interval)  {
            return "\u001B[32m" + roadName + " will be open for " + (roadTimer - max + interval) + "s." + "\u001B[0m";
        } else {
            return "\u001B[31m" + roadName + " will be closed for " + (roadTimer - newOffset) + "s." + "\u001B[0m";
        }
    }

    public void printSystemInfo() {
        System.out.printf("! %ds. have passed since system startup!\n", timeSinceStarted);
        System.out.printf("! Number of roads: %d !\n", roads);
        System.out.printf("! Interval: %d ! \n", interval);

        if (!roadsQueue.isEmpty()) {
            System.out.println();
            for (int i = 0; i < roadsQueue.size(); i++) {
                System.out.println(getRoadState(i, roadsQueue.get(i)));
            }
            System.out.println("\n");
        }

        System.out.printf("! Press \"Enter\" to open menu !\n");

    }

    public void kill() throws InterruptedException {
        running.set(false);
    }
}
