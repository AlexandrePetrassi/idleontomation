package com.caracrazy.idleon;

public final class ChopMiniGameState {

    ChopMiniGameFrame previous = new ChopMiniGameFrame();

    ChopMiniGameFrame current = new ChopMiniGameFrame();

    public ChopMiniGameFrame getCurrent() {
        return current;
    }

    private void setCurrentPosition(int newPosition) {
        previous = current;
        current = new ChopMiniGameFrame(newPosition, previous);
    }

    public ChopMiniGameState update(Integer newPosition) {
        setCurrentPosition(newPosition);
        return this;
    }

    public Integer getNextPosition() {
        return current.getPosition() + (int) (current.getSpeed() * current.getAverageDeltaTime());
    }

    @Override
    public String toString() {
        return "Game{" +
                "previousSpeed=" + previous.getSpeed() +
                ", previousPosition=" + previous.getPosition() +
                ", currentPosition=" + current.getPosition() +
                '}';
    }
}
