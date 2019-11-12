package edu.touro.mco152.bm;


import java.util.List;

/**
 * basic interface that can be implemented when making a new object to be used
 * as a BM tester
 */
public interface Worker {
    void tester();
    void process(List<DiskMark> markList);
}
