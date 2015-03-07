package de.tabscript.cw.traj;

import android.graphics.Point;

/**
 * Created by chris on 3/7/15.
 */
public class PenPoint extends Point{
    private boolean mPenUp;

    public PenPoint(int x, int y) {
        super(x, y);
        mPenUp = false;
    }

    public PenPoint(int x, int y, boolean penup) {
        super(x, y);
        mPenUp = penup;
    }

    public void setPenUp(boolean penup) {
        mPenUp = penup;
    }

    public boolean isPenUp() {
        return mPenUp;
    }
}
