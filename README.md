**jDiskMark** is a disk io benchmarking utility written in java, kindly made public by jamesmarkchan in https://sourceforge.net/projects/jdiskmark/  and protected under BSD License and Apache License V2.0.  See that repository for more detailed readme about functionality

This repo is for use by a Computer Methodology class, the changes may be unstable. Use the versions at https://sourceforge.net/projects/jdiskmark/ for more reliable source.

This code does what it need to very well, but it is not (yet) particularly object oriented, nor is it (yet) particularly extensible, no coincidence there :-) So for the time being we will call it **"BadBM"** and we will learn how to turn it into **GoodBM**.
 
**Usage**

1. Requires java 8. If using JDK 10 or newer, obtain EclipseLink version 2.7.3 jar, place it in lib directory (rename to eclipselink.jar)

2. to run outside of IDE:
   Unix         $ java -jar jDiskMark.jar
   On windows double click executable jar file.


Note that the jdm.properties file gets updated when the program is executed, the locationDir property gets set to the users home directory if not set already. If it is set and its wrong, execution of a benchmark will fail.

**Product Description**
The purpose of this program is to compute and provide some performance information about the local disk drive associated with the user’s home directory. It obtains this information by executing a series of arite and/or read operations against the disk and measuring the throughput over time. The benchmarks to be executed are defined by allowing the user to select whether to do a ‘read’, ‘write’ or ‘write-read’ benchmark and to indicate how sizeable the benchmark test data should be as well as some other parameters. A GUI is provided to request all of this information and to allow the user to start, stop and monitor the benchmark. An X/Y graph is produced dynamically during each ‘run to show the progress and disk performance. A history of runs with all performance data is maintained in a persistent location and is visible in the GUI, and any significant events or errors encountered are presented in the GUI.







== version info for classes ==

For MCO152 class, this repo's master branch is updated to this level:

level 5 - Add JUnit test - brute force headless-test of DiskWorker

level 4 - Gradle support

level 3 - JavaDoc Examples

level 2 - assorted updates to README.

level 1 - Cleaned up some warnings and added sample Javadoc (branches cleanWarings, docExamples), added meaningful gitignore.

level 0 - taken from jamesmarkchan repo at V0.4
