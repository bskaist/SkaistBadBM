package edu.touro.mco152.bm;

import java.text.DecimalFormat;

public class Marker {

    static DecimalFormat df = new DecimalFormat("###.###");

    public enum MarkType { READ,WRITE; }

    Marker(MarkType type) {
        this.type=type;
    }

    MarkType type;
    private int markNum = 0;

    public String toString() {
        return "Mark("+type+"): "+getMarkNum();
    }
    public int getMarkNum() {
        return markNum;
    }
    public void setMarkNum(int markNumb) {
        this.markNum = markNum;
    }

}
