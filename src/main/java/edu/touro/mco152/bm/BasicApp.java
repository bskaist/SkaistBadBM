
package edu.touro.mco152.bm;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.touro.mco152.bm.persist.DiskRun;

import edu.touro.mco152.bm.ui.SelectFrame;


/**
 * Primary class for global variables.
 */
public class BasicApp {

    public BasicApp(int numOfMarks, int numOfBlocks, int blockSizeKb) {
        this.numOfMarks = numOfMarks;
        this.numOfBlocks = numOfBlocks;
        this.blockSizeKb = blockSizeKb;
    }

    public static final String APP_CACHE_DIR = System.getProperty("user.home") + File.separator + ".jDiskMark";
    public static final String PROPERTIESFILE = "jdm.properties";
    public static final String DATADIRNAME = "jDiskMarkData";
    public static final int MEGABYTE = 1024 * 1024;
    public static final int KILOBYTE = 1024;
    public static final int IDLE_STATE = 0;
    public static final int DISK_TEST_STATE = 1;

    public static enum State {IDLE_STATE, DISK_TEST_STATE};
    public static State state = State.IDLE_STATE;

    public static Properties p;
    public static File locationDir = null;
    public static File dataDir = null;
    public static File testFile = null;

    // options
    public static boolean multiFile = true;
    public static boolean autoRemoveData = false;
    public static boolean autoReset = true;
    public static boolean showMaxMin = true;
    public static boolean writeSyncEnable = true;

    // run configuration
    public static boolean readTest = false;
    public static boolean writeTest = true;
    public static DiskRun.BlockSequence blockSequence = DiskRun.BlockSequence.SEQUENTIAL;
    public static int numOfMarks;      // desired number of marks
    public static int numOfBlocks;     // desired number of blocks
    public static int blockSizeKb;    // size of a block in KBs

    public static DiskWorker worker = null;
    public static int nextMarkNumber = 1;   // number of the next mark
    public static double wMax = -1, wMin = -1, wAvg = -1;
    public static double rMax = -1, rMin = -1, rAvg = -1;


    public static void main(String args[]) {
        /* Create and display the form */
        BasicApp BA = new BasicApp(25,32,512);
        BA.startBenchmark();
    }

    /**
     * Get the version from the build properties. Defaults to 0.0 if not found.
     * @return
     */
    public static String getVersion() {
        Properties bp = new Properties();
        String version = "0.0";
        try {
            bp.load(new FileInputStream("build.properties"));
            version = bp.getProperty("version");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        return version;
    }

    public static void loadConfig() {
        File pFile = new File(PROPERTIESFILE);
        if (!pFile.exists()) { return; }
        try {
            InputStream is = new FileInputStream(pFile);
            p.load(is);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        String value;
        value = p.getProperty("locationDir", System.getProperty("user.home"));
        locationDir = new File(value);
        value = p.getProperty("multiFile", String.valueOf(multiFile));
        multiFile = Boolean.valueOf(value);
        value = p.getProperty("autoRemoveData", String.valueOf(autoRemoveData));
        autoRemoveData = Boolean.valueOf(value);
        value = p.getProperty("autoReset", String.valueOf(autoReset));
        autoReset = Boolean.valueOf(value);
        value = p.getProperty("blockSequence", String.valueOf(blockSequence));
        blockSequence = DiskRun.BlockSequence.valueOf(value);
        value = p.getProperty("showMaxMin", String.valueOf(showMaxMin));
        showMaxMin = Boolean.valueOf(value);
        value = p.getProperty("numOfFiles", String.valueOf(numOfMarks));
        numOfMarks = Integer.valueOf(value);
        value = p.getProperty("numOfBlocks", String.valueOf(numOfBlocks));
        numOfBlocks = Integer.valueOf(value);
        value = p.getProperty("blockSizeKb", String.valueOf(blockSizeKb));
        blockSizeKb = Integer.valueOf(value);
        value = p.getProperty("writeTest", String.valueOf(writeTest));
        writeTest = Boolean.valueOf(value);
        value = p.getProperty("readTest", String.valueOf(readTest));
        readTest = Boolean.valueOf(value);
        value = p.getProperty("writeSyncEnable", String.valueOf(writeSyncEnable));
        writeSyncEnable = Boolean.valueOf(value);
    }

    public static void saveConfig() {
        p.setProperty("locationDir", App.locationDir.getAbsolutePath());
        p.setProperty("multiFile", String.valueOf(multiFile));
        p.setProperty("autoRemoveData", String.valueOf(autoRemoveData));
        p.setProperty("autoReset", String.valueOf(autoReset));
        p.setProperty("blockSequence", String.valueOf(blockSequence));
        p.setProperty("showMaxMin", String.valueOf(showMaxMin));
        p.setProperty("numOfFiles", String.valueOf(numOfMarks));
        p.setProperty("numOfBlocks", String.valueOf(numOfBlocks));
        p.setProperty("blockSizeKb", String.valueOf(blockSizeKb));
        p.setProperty("writeTest", String.valueOf(writeTest));
        p.setProperty("readTest", String.valueOf(readTest));
        p.setProperty("writeSyncEnable", String.valueOf(writeSyncEnable));

        try {
            OutputStream out = new FileOutputStream(new File(PROPERTIESFILE));
            p.store(out, "jDiskMark Properties File");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SelectFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch  (IOException ex) {
            Logger.getLogger(SelectFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getConfigString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Config for Java Disk Mark ").append(getVersion()).append('\n');
        sb.append("readTest: ").append(readTest).append('\n');
        sb.append("writeTest: ").append(writeTest).append('\n');
        sb.append("locationDir: ").append(locationDir).append('\n');
        sb.append("multiFile: ").append(multiFile).append('\n');
        sb.append("autoRemoveData: ").append(autoRemoveData).append('\n');
        sb.append("autoReset: ").append(autoReset).append('\n');
        sb.append("blockSequence: ").append(blockSequence).append('\n');
        sb.append("showMaxMin: ").append(showMaxMin).append('\n');
        sb.append("numOfFiles: ").append(numOfMarks).append('\n');
        sb.append("numOfBlocks: ").append(numOfBlocks).append('\n');
        sb.append("blockSizeKb: ").append(blockSizeKb).append('\n');
        return sb.toString();
    }

    public static void loadSavedRuns() {
        System.out.println("loading stored run data");
    }

    public static void clearSavedRuns() {
        DiskRun.deleteAll();

        loadSavedRuns();
    }

    public static void msg(String message) {
        System.out.println(message);
    }

    public static void cancelBenchmark() {
        if (worker == null) {
            msg("worker is null abort...");
            return;
        }
        //worker.cancel(true);
    }

    public static void startBenchmark() {
        p = new Properties();
        loadConfig();
        System.out.println(App.getConfigString());

        System.setProperty("derby.system.home", APP_CACHE_DIR);
        loadSavedRuns();


        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() { saveConfig(); }
        });
        //1. check that there isn't already a worker in progress
        if (state == State.DISK_TEST_STATE) {
            //if (!worker.isCancelled() && !worker.isDone()) {
            msg("Test in progress, aborting...");
            return;
            //}
        }

        //2. check can write to location
        if (locationDir.canWrite() == false) {
            msg("Selected directory can not be written to... aborting");
            return;
        }

        //3. update state
        state = State.DISK_TEST_STATE;

        //4. create data dir reference
        dataDir = new File (locationDir.getAbsolutePath()+File.separator+DATADIRNAME);

        //5. remove existing test data if exist
        if (App.autoRemoveData && dataDir.exists()) {
            if (dataDir.delete()) {
                msg("removed existing data dir");
            } else {
                msg("unable to remove existing data dir");
            }
        }

        //6. create data dir if not already present
        if (dataDir.exists() == false) { dataDir.mkdirs(); }

        //7. start disk worker thread
        worker = new DiskWorker();
        worker.addPropertyChangeListener((final PropertyChangeEvent event) -> {
            switch (event.getPropertyName()) {
                case "progress":
                    int value = (Integer)event.getNewValue();
                    long kbProcessed = (value) * App.targetTxSizeKb() / 100;
                    break;
                case "state":
                    break;
            }
        });
        worker.tester();
    }

    public static long targetMarkSizeKb() {
        return blockSizeKb * numOfBlocks;
    }

    public static long targetTxSizeKb() {
        return blockSizeKb * numOfBlocks * numOfMarks;
    }

    public static void updateMetrics(DiskMark mark) {
        if (mark.type==DiskMark.MarkType.WRITE) {
            if (wMax==-1 || wMax < mark.getBwMbSec()) {
                wMax = mark.getBwMbSec();
            }
            if (wMin==-1 || wMin > mark.getBwMbSec()) {
                wMin = mark.getBwMbSec();
            }
            if (wAvg==-1) {
                wAvg = mark.getBwMbSec();
            } else {
                int n = mark.getMarkNum();
                wAvg = (((double)(n-1)*wAvg)+mark.getBwMbSec())/(double)n;
            }
            mark.setCumAvg(wAvg);
            mark.setCumMax(wMax);
            mark.setCumMin(wMin);
        } else {
            if (rMax==-1 || rMax < mark.getBwMbSec()) {
                rMax = mark.getBwMbSec();
            }
            if (rMin==-1 || rMin > mark.getBwMbSec()) {
                rMin = mark.getBwMbSec();
            }
            if (rAvg==-1) {
                rAvg = mark.getBwMbSec();
            } else {
                int n = mark.getMarkNum();
                rAvg = (((double)(n-1)*rAvg)+mark.getBwMbSec())/(double)n;
            }
            mark.setCumAvg(rAvg);
            mark.setCumMax(rMax);
            mark.setCumMin(rMin);
        }
    }

    static public void resetSequence() {
        nextMarkNumber = 1;
    }

    static public void resetTestData() {
        nextMarkNumber = 1;
        wAvg = -1;
        wMax = -1;
        wMin = -1;
        rAvg = -1;
        rMax = -1;
        rMin = -1;
    }
}
