
package edu.touro.mco152.bm;

import static edu.touro.mco152.bm.BasicApp.KILOBYTE;
import static edu.touro.mco152.bm.BasicApp.MEGABYTE;
import static edu.touro.mco152.bm.BasicApp.blockSizeKb;
import static edu.touro.mco152.bm.BasicApp.dataDir;
import static edu.touro.mco152.bm.BasicApp.msg;
import static edu.touro.mco152.bm.BasicApp.numOfBlocks;
import static edu.touro.mco152.bm.BasicApp.numOfMarks;
import static edu.touro.mco152.bm.BasicApp.testFile;
import static edu.touro.mco152.bm.Marker.MarkType.READ;
import static edu.touro.mco152.bm.Marker.MarkType.WRITE;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.swing.JOptionPane;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;

/**
 * Basic implementation of the Worker interface that doesn't rely onr the GUI
 * Thread running the disk benchmarking. only one of these threads can run at
 * once.
 */
public class DiskWorker implements Worker {
    private volatile int progress;
    List<DiskMark> wMarkList = new ArrayList<>();
    List<DiskMark> rMarkList = new ArrayList<>();


    private final PropertyChangeSupport propertyChangeSupport;

    public DiskWorker() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    private void testRead(DiskRun run) {
        int wUnitsComplete = 0,
                rUnitsComplete = 0,
                unitsComplete;

        int wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
        int rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
        int unitsTotal = wUnitsTotal + rUnitsTotal;
        float percentComplete;

        int blockSize = blockSizeKb*KILOBYTE;
        byte [] blockArr = new byte [blockSize];
        for (int b=0; b<blockArr.length; b++) {
            if (b%2==0) {
                blockArr[b]=(byte)0xFF;
            }
        }

        DiskMark wMark, rMark;


        if (App.autoReset == true) {
            App.resetTestData();
        }

        int startFileNum = App.nextMarkNumber;

        for (int m=startFileNum; m<startFileNum+App.numOfMarks; m++) {

            if (App.multiFile == true) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator+"testdata"+m+".jdm");
            }
            rMark = new DiskMark(READ);
            rMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesReadInMark = 0;

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile,"r")) {
                    for (int b=0; b<numOfBlocks; b++) {
                        if (App.blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks-1);
                            rAccFile.seek(rLoc*blockSize);
                        } else {
                            rAccFile.seek(b*blockSize);
                        }
                        rAccFile.readFully(blockArr, 0, blockSize);
                        totalBytesReadInMark += blockSize;
                        rUnitsComplete++;
                        unitsComplete = rUnitsComplete + wUnitsComplete;
                        percentComplete = (float)unitsComplete/(float)unitsTotal * 100f;
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double)elapsedTimeNs / (double)1000000000;
            double mbRead = (double) totalBytesReadInMark / (double) MEGABYTE;
            rMark.setBwMbSec(mbRead / sec);
            msg("m:"+m+" READ IO is "+rMark.getBwMbSec()+" MB/s    "
                    + "(MBread "+mbRead+" in "+sec+" sec)");
            App.updateMetrics(rMark);
            rMarkList.add(rMark);
            process(rMarkList);

            run.setRunMax(rMark.getCumMax());
            run.setRunMin(rMark.getCumMin());
            run.setRunAvg(rMark.getCumAvg());
            run.setEndTime(new Date());
        }

    }

    private void testWrite(DiskRun run) {

        int wUnitsComplete = 0,
                rUnitsComplete = 0,
                unitsComplete;

        int wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
        int rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
        int unitsTotal = wUnitsTotal + rUnitsTotal;
        float percentComplete;

        int blockSize = blockSizeKb*KILOBYTE;
        byte [] blockArr = new byte [blockSize];
        for (int b=0; b<blockArr.length; b++) {
            if (b%2==0) {
                blockArr[b]=(byte)0xFF;
            }
        }

        DiskMark wMark, rMark;

        if (App.autoReset == true) {
            App.resetTestData();

        }

        int startFileNum = App.nextMarkNumber;


        if (BasicApp.multiFile == false) {
            testFile = new File(dataDir.getAbsolutePath()+File.separator+"testdata.jdm");
        }
        for (int m=startFileNum; m<startFileNum+App.numOfMarks; m++) {

            if (BasicApp.multiFile == true) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator+"testdata"+m+".jdm");
            }
            wMark = new DiskMark(WRITE);
            wMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesWrittenInMark = 0;

            String mode = "rw";
            if (App.writeSyncEnable) { mode = "rwd"; }

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile,mode)) {
                    for (int b=0; b<numOfBlocks; b++) {
                        if (App.blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks-1);
                            rAccFile.seek(rLoc*blockSize);
                        } else {
                            rAccFile.seek(b*blockSize);
                        }
                        rAccFile.write(blockArr, 0, blockSize);
                        totalBytesWrittenInMark += blockSize;
                        wUnitsComplete++;
                        unitsComplete = rUnitsComplete + wUnitsComplete;
                        percentComplete = (float)unitsComplete/(float)unitsTotal * 100f;
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double)elapsedTimeNs / (double)1000000000;
            double mbWritten = (double)totalBytesWrittenInMark / (double)MEGABYTE;
            wMark.setBwMbSec(mbWritten / sec);
            msg("m:"+m+" write IO is "+wMark.getBwMbSecAsString()+" MB/s     "
                    + "("+Util.displayString(mbWritten)+ "MB written in "
                    + Util.displayString(sec)+" sec)");
            App.updateMetrics(wMark);
            wMarkList.add(wMark);
            process(wMarkList);

            run.setRunMax(wMark.getCumMax());
            run.setRunMin(wMark.getCumMin());
            run.setRunAvg(wMark.getCumAvg());
            run.setEndTime(new Date());
        }
    }


    private void setRun(DiskRun run) {
        run.setNumMarks(App.numOfMarks);
        run.setNumBlocks(App.numOfBlocks);
        run.setBlockSize(App.blockSizeKb);
        run.setTxSize(App.targetTxSizeKb());
//        run.setDiskInfo(Util.getDiskInfo(dataDir));

        msg("disk info: ("+ run.getDiskInfo()+")");

    }

    @Override
    public void process(List<DiskMark> markList) {

    }

    @Override
    public void tester() {
        System.out.println("*** starting new worker thread");
        msg("Running readTest "+App.readTest+"   writeTest "+App.writeTest);
        msg("num files: "+App.numOfMarks+", num blks: "+App.numOfBlocks
                +", blk size (kb): "+App.blockSizeKb+", blockSequence: "+App.blockSequence);
        if(App.writeTest) {
            DiskRun run = new DiskRun(DiskRun.IOMode.WRITE, App.blockSequence);
            setRun(run);
            testWrite(run);

            EntityManager em = EM.getEntityManager();
            em.getTransaction().begin();
            em.persist(run);
            em.getTransaction().commit();

        }
        // try renaming all files to clear catch
        if (App.readTest && App.writeTest) {

        }

        if (App.readTest) {

            DiskRun run = new DiskRun(DiskRun.IOMode.READ, App.blockSequence);
            setRun(run);
            testRead(run);


            EntityManager em = EM.getEntityManager();
            em.getTransaction().begin();
            em.persist(run);
            em.getTransaction().commit();

        }
        App.nextMarkNumber += App.numOfMarks;

    }


    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChangeSupport().addPropertyChangeListener(listener);
    }

    public final PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }



}