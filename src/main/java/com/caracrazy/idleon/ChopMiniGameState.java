package com.caracrazy.idleon;

public final class ChopMiniGameState {

    private Integer previousSpeed;

    public Integer getPreviousSpeed() {
        return previousSpeed;
    }

    private Integer currentSpeed;

    public Integer getCurrentSpeed() {
        return currentSpeed;
    }

    private Integer previousPosition;

    public Integer getPreviousPosition() {
        return previousPosition;
    }

    private Integer currentPosition;

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    private Long currentTime;

    public Long getCurrentTime() {
        return currentTime;
    }

    private Long previousTime;

    public Long getPreviousTime() {
        return previousTime;
    }

    private Long previousDeltaTime;

    public Long getPreviousDeltaTime() {
        return previousDeltaTime;
    }

    private Long currentDeltaTime;

    public Long getCurrentDeltaTime() {
        return currentDeltaTime;
    }

    private Integer previousDeltaPosition;

    public Integer getPreviousDeltaPosition() {
        return previousDeltaPosition;
    }

    private Integer currentDeltaPosition;

    private Integer calculateDeltaPosition() {
        if (previousPosition == null || currentPosition == null) return null;
        return currentPosition - previousPosition;
    }

    private Long calculateDeltaTime() {
        if (previousTime == null || currentTime == null) return null;
        return currentTime - previousTime;
    }

    private Integer calculateCurrentSpeed() {
        if (currentDeltaPosition == null) return null;
        if (currentDeltaTime == null || currentDeltaTime == 0) return null;
        return (int) (currentDeltaPosition / currentDeltaTime);
    }

    private void setCurrentPosition(Integer newPosition) {
        previousPosition = currentPosition;
        previousTime = currentTime;
        previousDeltaTime = currentDeltaTime;
        previousDeltaPosition = currentDeltaPosition;
        previousSpeed = currentSpeed;

        currentPosition = newPosition;
        currentTime = System.nanoTime();
        currentDeltaTime = calculateDeltaTime();
        currentDeltaPosition = calculateDeltaPosition();
        currentSpeed = calculateCurrentSpeed();
    }

    public ChopMiniGameState update(Integer newPosition) {
        setCurrentPosition(newPosition);
        return this;
    }

    public Long averageDeltaTime() {
        if (currentDeltaTime == null || previousDeltaTime == null) return null;
        return currentDeltaTime / previousDeltaTime;
    }

    public Integer getNextPosition() {
        return getCurrentPosition() + (int) (getCurrentSpeed() * averageDeltaTime());
    }

    public boolean isSwitchingDirection() {
        return getCurrentDirection() != getPreviousDirection();
    }

    public int getCurrentDirection() {
        return (int) Math.signum(getCurrentSpeed());
    }

    public int getPreviousDirection() {
        return (int) Math.signum(getPreviousSpeed());
    }

    @Override
    public String toString() {
        return "Game{" +
                "previousSpeed=" + previousSpeed +
                ", previousPosition=" + previousPosition +
                ", currentPosition=" + currentPosition +
                '}';
    }
}
