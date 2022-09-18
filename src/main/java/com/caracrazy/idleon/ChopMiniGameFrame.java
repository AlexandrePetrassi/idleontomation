package com.caracrazy.idleon;

public final class ChopMiniGameFrame {

    private final Integer speed;

    public Integer getSpeed() {
        return speed;
    }

    private final Integer position;

    public Integer getPosition() {
        return position;
    }

    private final Long time;

    public Long getTime() {
        return time;
    }

    private final Long deltaTime;

    public Long getDeltaTime() {
        return deltaTime;
    }

    private final Long averageDeltaTime;

    public Long getAverageDeltaTime() {
        return averageDeltaTime;
    }

    public ChopMiniGameFrame(int position, ChopMiniGameFrame previous) {
        if (previous == null) previous = new ChopMiniGameFrame();
        this.position = position;
        this.time = System.currentTimeMillis();
        this.deltaTime = calculateDeltaTime(time, previous);
        Integer deltaPosition = calculateDeltaPosition(position, previous);
        this.speed = calculateCurrentSpeed(deltaTime, deltaPosition);
        this.averageDeltaTime = calculateAverageDeltaTime(deltaTime, previous);
    }

    public ChopMiniGameFrame() {
        speed = null;
        position = null;
        time = null;
        deltaTime = null;
        averageDeltaTime = null;
    }

    public int getDirection() {
        if (speed == null) return 0;
        return (int) Math.signum(speed);
    }

    private Integer calculateDeltaPosition(int position, ChopMiniGameFrame previous) {
        if (previous.position == null) return null;
        return position - previous.position;
    }

    private static Long calculateDeltaTime(long time, ChopMiniGameFrame previous) {
        if (previous.time == null) return null;
        return time - previous.time;
    }

    private static Integer calculateCurrentSpeed(Long deltaTime, Integer deltaPosition) {
        if (deltaTime == null || deltaTime == 0) return null;
        return (int) (deltaPosition / deltaTime);
    }

    private static Long calculateAverageDeltaTime(Long deltaTime, ChopMiniGameFrame previous) {
        if (previous.averageDeltaTime == null) return deltaTime;
        return (deltaTime + previous.averageDeltaTime) / 2;
    }
}
