package org.BioLayoutExpress3D.ClassViewerUI.Tables;

import java.util.*;
import javax.swing.*;
import org.BioLayoutExpress3D.Analysis.*;
import org.BioLayoutExpress3D.ClassViewerUI.*;
import org.BioLayoutExpress3D.ClassViewerUI.Tables.TableModels.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import static org.BioLayoutExpress3D.ClassViewerUI.ClassViewerFrame.ClassViewerTabTypes.*;

/**
*
* @author Markus Brosch (mb8[at]sanger[dot]ac[dot]uk)
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class ClassViewerUpdateEntropyTable implements Runnable
{

    private static final int NUMBER_OF_STEPS = 6;

    private ClassViewerFrame classViewerFrame = null;
    private String annotationClass = null;
    private ClassViewerTableModelDetail analysisTableModel = null;
    private HashSet<String> selectedGenes = null;
    private JTabbedPane tabbedPane = null;

    private RelativeEntropyCalc relativeEntropyCalc = null;
    private LayoutProgressBarDialog layoutProgressBarDialog = null;

    /**
    *  The abortThread variable is used to silently abort the Runnable/Thread.
    */
    private volatile boolean abortThread = false;

    public ClassViewerUpdateEntropyTable(ClassViewerFrame classViewerFrame, LayoutFrame layoutFrame, String annotationClass, ClassViewerTableModelDetail analysisTableModel, HashSet<String> selectedGenes, JTabbedPane tabbedPane)
    {
        this.classViewerFrame = classViewerFrame;
        this.annotationClass = annotationClass;
        this.analysisTableModel = analysisTableModel;
        this.selectedGenes = selectedGenes;
        this.tabbedPane = tabbedPane;

        relativeEntropyCalc = new RelativeEntropyCalc( layoutFrame.getNetworkRootContainer() );
        layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
    }

    @Override
    public void run()
    {
        setThreadStarted();

        if ( (annotationClass != null) && !annotationClass.equals("") )
        {
            layoutProgressBarDialog.prepareProgressBar(NUMBER_OF_STEPS, " Calculating analysis values for one term...");
            layoutProgressBarDialog.startProgressBar();
            layoutProgressBarDialog.incrementProgress();

            Map<String, HashMap<String, String>> overRepData = relativeEntropyCalc.overRepForEachCluster(selectedGenes, annotationClass);

            if (abortThread)
            {
                layoutProgressBarDialog.endProgressBar();
                layoutProgressBarDialog.stopProgressBar();
                setThreadFinished();
                return;
            }
            layoutProgressBarDialog.incrementProgress();

            Map<String, String> observed      = overRepData.get("Obs");
            Map<String, String> expected      = overRepData.get("Exp");
            Map<String, String> fobs          = overRepData.get("Fobs");
            Map<String, String> fexp          = overRepData.get("Fexp");
            Map<String, String> overrep       = overRepData.get("OverRep");
            Map<String, String> expectedTrial = overRepData.get("ExpT");
            Map<String, String> zscore        = overRepData.get("Zscore");

            Map<String, Double>  entropies    = relativeEntropyCalc.relEntropy4Selection(selectedGenes, annotationClass);
            Map<String, Double>  fishers      = relativeEntropyCalc.fisherTestForEachCluster(selectedGenes, annotationClass);
            Map<String, Integer> members      = relativeEntropyCalc.clusterMembers(selectedGenes, annotationClass);

            if (abortThread)
            {
                layoutProgressBarDialog.endProgressBar();
                layoutProgressBarDialog.stopProgressBar();
                setThreadFinished();
                return;
            }
            layoutProgressBarDialog.incrementProgress();

            analysisTableModel.setTerm2Entropy(observed, expected, expectedTrial, fobs, fexp, overrep, zscore, entropies, fishers, members, annotationClass);

            if (abortThread)
            {
                layoutProgressBarDialog.endProgressBar();
                layoutProgressBarDialog.stopProgressBar();
                setThreadFinished();
                return;
            }
            layoutProgressBarDialog.incrementProgress();

            tabbedPane.setEnabledAt(ENTROPY_DETAILS_TAB.ordinal(), true);

            if (abortThread)
            {
                layoutProgressBarDialog.endProgressBar();
                layoutProgressBarDialog.stopProgressBar();
                setThreadFinished();
                return;
            }
            layoutProgressBarDialog.incrementProgress();

            analysisTableModel.fireTableStructureChanged();

            if (abortThread)
            {
                layoutProgressBarDialog.endProgressBar();
                layoutProgressBarDialog.stopProgressBar();
                setThreadFinished();
                return;
            }
            layoutProgressBarDialog.incrementProgress();

            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();

            if ( classViewerFrame.isVisible() )
                classViewerFrame.processAndSetWindowState();
        }

        setThreadFinished();
    }

    public void setAbortThread(boolean abortThread)
    {
        this.abortThread = abortThread;

        relativeEntropyCalc.setAbortThread(abortThread);
    }

    public boolean getAbortThread()
    {
        return abortThread;
    }

    boolean threadStarted = false;
    void setThreadStarted()
    {
        threadStarted = true;
    }

    public boolean getThreadStarted()
    {
        return threadStarted;
    }

    boolean threadFinished = false;
    void setThreadFinished()
    {
        threadFinished = true;
    }

    public boolean getThreadFinished()
    {
        return threadFinished;
    }
}