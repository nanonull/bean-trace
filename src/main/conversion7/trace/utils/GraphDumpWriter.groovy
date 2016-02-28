package conversion7.trace.utils

import groovy.json.internal.Charsets
import org.apache.commons.io.FileUtils

import static TraceUtils.getTimeStamp

class GraphDumpWriter {

    String threadName
    String startedAtStamp
    File threadFolder
    File templateFile
    int dumpIndex = 1

    GraphDumpWriter() {
        threadName = Thread.currentThread().getName()
        startedAtStamp = getTimeStamp(new Date(), "yyyy_MM_dd_HH_mm_ss")
        templateFile = ResourcesReader.findResource("d3js_index.html")
    }

    void createThreadFolder() {
        if (threadFolder == null) {
            threadFolder = new File("dumps/" + threadName + '_' + startedAtStamp)
            threadFolder.mkdirs()
        }
    }

    void write(String dumpName, Object dumpValue) {
        createThreadFolder()
        def dumpFileName = dumpName.replaceAll("[^a-zA-Z0-9.-]", "_")

        def dumpDir
        for (; ; dumpIndex++) {
            dumpDir = new File(threadFolder, dumpFileName + '_' + dumpIndex)
            if (!dumpDir.exists()) {
                break
            }
        }

        dumpDir.mkdirs()
        def dumpFile = new File(dumpDir, 'dump.json')
        FileUtils.writeStringToFile(dumpFile, String.valueOf(dumpValue)
                , Charsets.UTF_8.name())

        def dumpIndexFile = new File(dumpDir, 'index.html')
        FileUtils.copyFile(templateFile, dumpIndexFile)
    }

}
