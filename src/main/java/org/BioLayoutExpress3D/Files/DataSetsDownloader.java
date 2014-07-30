package org.BioLayoutExpress3D.Files;

import java.io.*;
import org.BioLayoutExpress3D.Utils.Path;
import java.util.*;
import org.BioLayoutExpress3D.Connections.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;
import org.BioLayoutExpress3D.Environment.DataFolder;

/**
*
* DataSetsDownloader is the data sets downloader class used to download the data sets from the biolayout server.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public class DataSetsDownloader extends HttpConnection implements IOUtils.IOUtilsStreamingListener
{
    /**
    *  Constant variable used for downloading.
    */
    private static final String REGEX = "#";

    private ArrayList<String> dataSetsControlFileData = null;
    private String[] dataSetDirectories = null;
    private String[] dataSetNames = null;
    private String[] dataSetLengths = null;

    /**
    *  String variable to be used for the repository.
    */
    private String repository = "";

    /**
    *  Integer variable to be used by the loading dialog.
    */
    private int numberOfIterations = 0;

    /**
    *  String variable to be used by the loading dialog.
    */
    private String currentDataSet = "";

    /**
    *  LayoutProgressBarDialog reference (reference for the loading dialog).
    */
    private LayoutProgressBarDialog layoutProgressBarDialog = null;

    /**
    *  The first constructor of the DataSetsDownloader class (without data sets names & without proxy related settings).
    */
    public DataSetsDownloader(LayoutProgressBarDialog layoutProgressBarDialog)
    {
        super();
        this.layoutProgressBarDialog = layoutProgressBarDialog;
    }

    /**
    *  The second constructor of the DataSetsDownloader class (with data sets names & without proxy related settings).
    */
    public DataSetsDownloader(String repository, String dataSets, LayoutProgressBarDialog layoutProgressBarDialog)
    {
        super();
        this.repository = repository;
        this.layoutProgressBarDialog = layoutProgressBarDialog;

        parseDataSetsData(dataSets);
    }

    /**
    *  The third constructor of the DataSetsDownloader class (with data sets names & with proxy related settings).
    */
    public DataSetsDownloader(Proxy proxy, String repository, String dataSets, LayoutProgressBarDialog layoutProgressBarDialog)
    {
        super(proxy);
        this.repository = repository;
        this.layoutProgressBarDialog = layoutProgressBarDialog;

        parseDataSetsData(dataSets);
    }

    /**
    *  Parses the data sets data.
    */
    private void parseDataSetsData(String dataSets)
    {
        String[] dataSetsAndLengths = dataSets.split("\\s+");
        dataSetNames = dataSetsAndLengths[0].split(REGEX);
        dataSetLengths = dataSetsAndLengths[1].split(REGEX);
        parseDataSetsDirectories();
    }

    /**
    *  Parses the data sets directories data.
    */
    private void parseDataSetsDirectories()
    {
        dataSetDirectories = new String[dataSetNames.length];
        for (int i = 0; i < dataSetNames.length; i++)
        {
            if ( dataSetNames[i].contains("/") )
            {
                int lastIndex = dataSetNames[i].lastIndexOf("/") + 1;
                dataSetDirectories[i] = dataSetNames[i].substring(0, lastIndex);
                dataSetNames[i] = dataSetNames[i].substring( lastIndex, dataSetNames[i].length() );
            }
            else
                dataSetDirectories[i] = "";
        }
    }

    /**
    *  Retrieves the data sets control file data from the repository.
    */
    public boolean retrieveDataSetsControlFileDataFromRepository()
    {
        try
        {
            String controlFileUrl = (repository.isEmpty() ? (BIOLAYOUT_EXPRESS_3D_DOMAIN_URL + BIOLAYOUT_SERVER_DATASETS_DIRECTORY) : repository) + BIOLAYOUT_DATASETS_CONTROL_FILE;
            dataSetsControlFileData = retrieveTextDataFromHttpConnection(controlFileUrl, true);

            if ( getManagedToConnect() )
            {
                int chooseRandomDataSetToLoad = org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange( 1, dataSetsControlFileData.size() );
                if (DEBUG_BUILD) println("Chosen dataset to load: " + dataSetsControlFileData.get(chooseRandomDataSetToLoad - 1) + " from the data sets control file's line: " + chooseRandomDataSetToLoad);
                parseDataSetsData( dataSetsControlFileData.get(chooseRandomDataSetToLoad - 1) );
            }
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD) println("Exception in retrieveDataSetsControlFileDataFromBioLayoutExpress3DWebPage() method:\n" + exc.getMessage());
        }

        return getManagedToConnect();
    }

    /**
    *  Retrieves & unzips the data sets after checking that each of them do not pre-exit on the client's machine.
    */
    public boolean retrieveDataSetsFromRepository()
    {
        String dataSetUrl = "";
        String dataSetFileName = "";
        boolean hasSucceeded = true;

        if (dataSetNames.length == 1)
        {
            dataSetUrl = (repository.isEmpty() ? (BIOLAYOUT_EXPRESS_3D_DOMAIN_URL + BIOLAYOUT_SERVER_DATASETS_DIRECTORY) : repository) + dataSetDirectories[0] + dataSetNames[0];
            dataSetFileName = Path.combine(DataFolder.get(), dataSetNames[0]);

            if ( !new File( IOUtils.getPrefix(dataSetFileName) ).exists() )
            {
                try
                {
                    hasSucceeded = loadDataSet(dataSetLengths[0], dataSetNames[0], dataSetUrl, dataSetFileName);
                }
                catch (Exception exc)
                {
                    if (DEBUG_BUILD) println("Exception in retrieveDataSetsFromRepository() method:\n" + exc.getMessage());
                }

                return hasSucceeded;
            }
            else
            {
                if (DEBUG_BUILD) println("DataSet " + IOUtils.getPrefix(dataSetNames[0]) + " is already cached on client's machine.");

                return true;
            }
        }
        else
        {
            boolean[] hasSucceededAllFiles = new boolean[dataSetNames.length];
            for (int i = 0; i < dataSetNames.length; i++)
            {
                dataSetUrl = (repository.isEmpty() ? (BIOLAYOUT_EXPRESS_3D_DOMAIN_URL + BIOLAYOUT_SERVER_DATASETS_DIRECTORY) : repository) + dataSetDirectories[i] + dataSetNames[i];
                dataSetFileName = Path.combine(DataFolder.get(), dataSetNames[i]);

                if ( !new File( IOUtils.getPrefix(dataSetFileName) ).exists() )
                {
                    try
                    {
                        hasSucceededAllFiles[i] = loadDataSet(dataSetLengths[i], dataSetNames[i], dataSetUrl, dataSetFileName);
                    }
                    catch (Exception exc)
                    {
                        if (DEBUG_BUILD) println("Exception in retrieveDataSetsFromRepository() method:\n" + exc.getMessage());
                    }
                }
                else
                {
                    if (DEBUG_BUILD) println("DataSet " + IOUtils.getPrefix(dataSetNames[i]) + " is already cached on client's machine.");

                    hasSucceededAllFiles[i] = true;
                }
            }

            for (boolean flag : hasSucceededAllFiles)
                if (!flag)
                    hasSucceeded = false;

            return hasSucceeded;
        }
    }

    /**
    *  Loads a given dataset.
    */
    private boolean loadDataSet(String dataSetLength, String dataSetName, String dataSetUrl, String dataSetFileName) throws Exception
    {
        boolean hasSucceeded = true;
        IOUtils.setListener(this);
        numberOfIterations = Integer.parseInt(dataSetLength) / IOUtils.getBufferSize();
        currentDataSet = IOUtils.getPrefix(dataSetName);
        if ( hasSucceeded = retrieveBinaryDataFromHttpConnection(dataSetUrl, dataSetFileName) )
        {
            IOUtils.removeListener();
            IOUtils.zipUncompressFile(dataSetFileName);
            new File(dataSetFileName).delete();
        }
        else
            IOUtils.removeListener();

        return hasSucceeded;
    }

    /**
    *  This method is called as a callback event when starting a streaming process.
    */
    @Override
    public void initStreamingProcess(int availableDataInBytes)
    {
        layoutProgressBarDialog.prepareProgressBar(numberOfIterations, "Downloading " + currentDataSet + " Data Set...");
        layoutProgressBarDialog.startProgressBar();
        if (DEBUG_BUILD) println("initStreamingProcess(): " + numberOfIterations);
    }

    /**
    *  This method is called as a callback event when iterating through a streaming process.
    */
    @Override
    public void iterateStreamingProcess()
    {
        layoutProgressBarDialog.incrementProgress();
        if (DEBUG_BUILD) println("iterateStreamingProcess()");
    }

    /**
    *  This method is called as a callback event when finishing a streaming process.
    */
    @Override
    public void finishStreamingProcess()
    {
        layoutProgressBarDialog.endProgressBar();
        layoutProgressBarDialog.stopProgressBar();
        if (DEBUG_BUILD) println("finishStreamingProcess()");
    }

    /**
    *  Gets the name of the data set to be loaded.
    */
    public String getDataSetName()
    {
        return IOUtils.getPrefix(Path.combine(DataFolder.get(), dataSetNames[0]));
    }

    /**
    *  Sets the name of the data sets downloader http connection.
    */
    public void setDataSetsDownloaderName(String dataSetsDownloader)
    {
        this.nameOfHttpConnection = dataSetsDownloader;
    }

    /**
    *  Gets the name of the data sets downloader http connection.
    */
    public String getDataSetsDownloaderName()
    {
        return nameOfHttpConnection;
    }


}