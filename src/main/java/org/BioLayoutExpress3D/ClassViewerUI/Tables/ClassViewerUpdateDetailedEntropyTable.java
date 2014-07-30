package org.BioLayoutExpress3D.ClassViewerUI.Tables;

import java.util.*;
import javax.swing.*;
import org.BioLayoutExpress3D.Analysis.*;
import org.BioLayoutExpress3D.ClassViewerUI.*;
import org.BioLayoutExpress3D.ClassViewerUI.Tables.TableModels.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import static org.BioLayoutExpress3D.ClassViewerUI.ClassViewerFrame.ClassViewerTabTypes.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Markus Brosch (mb8[at]sanger[dot]ac[dot]uk)
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class ClassViewerUpdateDetailedEntropyTable implements Runnable
{
    private ClassViewerFrame classViewerFrame = null;
    private HashSet<String> selectedGenes = null;
    private JTabbedPane tabbedPane = null;

    private RelativeEntropyCalc relEntropyCalc = null;
    private ClassViewerTableModelDetail modelDetail = null;
    private LayoutProgressBarDialog layoutProgressBarDialog = null;

    /**
    *  The abortThread variable is used to silently abort the Runnable/Thread.
    */
    private volatile boolean abortThread = false;

    public ClassViewerUpdateDetailedEntropyTable(ClassViewerFrame classViewerFrame, LayoutFrame layoutFrame, ClassViewerTableModelDetail modelDetail, HashSet<String> selectedGenes, JTabbedPane tabbedPane)
    {
        this.classViewerFrame = classViewerFrame;
        this.modelDetail = modelDetail;
        this.selectedGenes = selectedGenes;
        this.tabbedPane = tabbedPane;

        relEntropyCalc = new RelativeEntropyCalc( layoutFrame.getNetworkRootContainer() );
        layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
    }

    @Override
    public void run()
    {
        setThreadStarted();

        Set<String> annotationClasses = AnnotationTypeManagerBG.getInstanceSingleton().getAllTypes();
        int numberOfAllAnnotationClasses = annotationClasses.size();
        layoutProgressBarDialog.prepareProgressBar(numberOfAllAnnotationClasses, " Calculating analysis values for all terms of all classes...");
        layoutProgressBarDialog.startProgressBar();

        // analysis calc
        int overallEntropiesEntries = 0;
        Map<String, Map<String, Double>>  perType = new HashMap<String, Map<String, Double>>();
        Map<String, Map<String, Double>> fishers  = new HashMap<String, Map<String, Double>>();

        Map<String, HashMap<String, String>> Observed      = new HashMap<String, HashMap<String, String>>();
        Map<String, HashMap<String, String>> Expected      = new HashMap<String, HashMap<String, String>>();
        Map<String, HashMap<String, String>> Fobs          = new HashMap<String, HashMap<String, String>>();
        Map<String, HashMap<String, String>> Fexp          = new HashMap<String, HashMap<String, String>>();
        Map<String, HashMap<String, String>> OverRep       = new HashMap<String, HashMap<String, String>>();
        Map<String, HashMap<String, String>> ExpectedTrial = new HashMap<String, HashMap<String, String>>();
        Map<String, HashMap<String, String>> Zscore        = new HashMap<String, HashMap<String, String>>();

        Map<String, Map<String, Integer>> numberOfMembers = new HashMap<String, Map<String, Integer>>();

        for (String type : annotationClasses)
        {
            layoutProgressBarDialog.incrementProgress();

            if (abortThread)
            {
                layoutProgressBarDialog.endProgressBar();
                layoutProgressBarDialog.stopProgressBar();
                setThreadFinished();
                return;
            }

            Map<String, Double> entropies = relEntropyCalc.relEntropy4Selection(selectedGenes, type);

            if (entropies != null)
            {
                perType.put(type, entropies);

                if (DEBUG_BUILD) println("Doing Fishers:");

                Map<String, Double> fisher = relEntropyCalc.fisherTestForEachCluster(selectedGenes, type);

                if (abortThread)
                {
                    layoutProgressBarDialog.endProgressBar();
                    layoutProgressBarDialog.stopProgressBar();
                    setThreadFinished();
                    return;
                }

                if (DEBUG_BUILD) println("Doing OverRep:");

                fishers.put(type, fisher);

                Map<String, HashMap<String, String>> overRepData = relEntropyCalc.overRepForEachCluster(selectedGenes, type);

                if (abortThread)
                {
                    layoutProgressBarDialog.endProgressBar();
                    layoutProgressBarDialog.stopProgressBar();
                    setThreadFinished();
                    return;
                }

                Observed.put(type,  overRepData.get("Obs"));
                Expected.put(type,  overRepData.get("Exp"));
                Fobs.put(type,      overRepData.get("Fobs"));
                Fexp.put(type,      overRepData.get("Fexp"));
                OverRep.put(type,   overRepData.get("OverRep"));
                ExpectedTrial.put(type, overRepData.get("ExpT"));
                Zscore.put(type,    overRepData.get("Zscore"));

                Map<String, Integer> numberOfMember = relEntropyCalc.clusterMembers(selectedGenes, type);
                numberOfMembers.put(type, numberOfMember);

                if (abortThread)
                {
                    layoutProgressBarDialog.endProgressBar();
                    layoutProgressBarDialog.stopProgressBar();
                    setThreadFinished();
                    return;
                }

                overallEntropiesEntries += entropies.size();
            }
        }

        layoutProgressBarDialog.endProgressBar();
        layoutProgressBarDialog.stopProgressBar();

        // add these calculated values to model
        modelDetail.setSize(overallEntropiesEntries);
        layoutProgressBarDialog.prepareProgressBar(perType.keySet().size(), " Now updating table and inserting values...");
        layoutProgressBarDialog.startProgressBar();

        Set<String> keys = perType.keySet();
        for (String type : keys)
        {
            layoutProgressBarDialog.incrementProgress();

            if (abortThread)
            {
                layoutProgressBarDialog.endProgressBar();
                layoutProgressBarDialog.stopProgressBar();
                setThreadFinished();
                return;
            }

            Map<String, Double>  entropies =  perType.get(type);
            Map<String, Double>  fisher = fishers.get(type);
            Map<String, Integer> members = numberOfMembers.get(type);

            modelDetail.addAnalysisValues( Observed.get(type), Expected.get(type), ExpectedTrial.get(type), Fobs.get(type), Fexp.get(type),
                                           OverRep.get(type), Zscore.get(type), entropies, fisher, members, type);
        }

        if (abortThread)
        {
            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
            setThreadFinished();
            return;
        }

        layoutProgressBarDialog.prepareProgressBar(0, " Please wait, table structure is rendered...");
        layoutProgressBarDialog.setText("Almost done");
        layoutProgressBarDialog.startProgressBar();

        if (abortThread)
        {
            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
            setThreadFinished();
            return;
        }

        tabbedPane.setEnabledAt(ENTROPY_DETAILS_TAB.ordinal(), true);

        if (abortThread)
        {
            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
            setThreadFinished();
            return;
        }

        modelDetail.fireTableStructureChanged();

        if (abortThread)
        {
            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
            setThreadFinished();
            return;
        }

        layoutProgressBarDialog.endProgressBar();
        layoutProgressBarDialog.stopProgressBar();

        if ( classViewerFrame.isVisible() )
            classViewerFrame.processAndSetWindowState();

        setThreadFinished();
    }

    public void setAbortThread(boolean abortThread)
    {
        this.abortThread = abortThread;

        relEntropyCalc.setAbortThread(abortThread);
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