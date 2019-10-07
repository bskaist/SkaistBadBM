package edu.touro.mco152.bm;

import java.util.List;

public interface Worker {
    void Tester();
    void process(List<DiskMark> markList);
    void done();
}
