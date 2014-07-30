package org.BioLayoutExpress3D.Files.Parsers;

import java.io.File;
import org.BioLayoutExpress3D.CoreUI.Dialogs.LayoutProgressBarDialog;
import org.BioLayoutExpress3D.CoreUI.LayoutFrame;
import org.BioLayoutExpress3D.Network.NetworkContainer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.IOException;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
 *
 * @author Tim Angus <tim.angus@roslin.ed.ac.uk>
 */
public class GmlFileParser extends CoreParser
{
    public class GmlFileListener extends gmlBaseListener
    {
        NetworkContainer nc;
        LayoutProgressBarDialog layoutProgressBarDialog;
        String sourceNodeId;

        public GmlFileListener(NetworkContainer nc, LayoutProgressBarDialog layoutProgressBarDialog)
        {
            this.nc = nc;
            this.layoutProgressBarDialog = layoutProgressBarDialog;
            sourceNodeId = "";
        }

        @Override
        public void enterKeyValue(gmlParser.KeyValueContext ctx)
        {
            String key = ctx.KEY().getText();
            String value = ctx.value().getText();

            if (key.equals("source") && value != null)
            {
                sourceNodeId = value;
            }
            else if (key.equals("target") && value != null && sourceNodeId.length() > 0)
            {
                String targetNodeId = value;
                nc.addNetworkConnection(sourceNodeId, targetNodeId, 0.0f);
                sourceNodeId = "";
            }

            layoutProgressBarDialog.incrementProgress(ctx.start.getStopIndex());
        }
    }

    public GmlFileParser(NetworkContainer nc, LayoutFrame layoutFrame)
    {
        super(nc, layoutFrame);
    }

    @Override
    public boolean init(File file, String fileExtension)
    {
        this.file = file;
        return true;
    }

    @Override
    public boolean parse()
    {
        isSuccessful = false;
        nc.setOptimized(false);

        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();

        try
        {
            String fileName = this.file.getAbsolutePath();
            ANTLRFileStream fileStream = new ANTLRFileStream(fileName);

            layoutProgressBarDialog.prepareProgressBar(fileStream.size(), "Parsing...");
            layoutProgressBarDialog.startProgressBar();

            gmlLexer lexer = new gmlLexer(fileStream);
            gmlParser parser = new gmlParser(new CommonTokenStream(lexer));
            ParseTree tree = parser.list();

            GmlFileListener gmlFileListener = new GmlFileListener(nc, layoutProgressBarDialog);
            ParseTreeWalker.DEFAULT.walk(gmlFileListener, tree);

            isSuccessful = true;
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD)
            {
                println("IOException in GmlFileParser.init():\n" + ioe.getMessage());
            }
        }
        catch (Exception e)
        {
            if (DEBUG_BUILD)
            {
                println("Exception in GmlFileParser.init():\n" + e.getMessage());
            }
        }
        finally
        {
            layoutProgressBarDialog.endProgressBar();
        }

        return isSuccessful;
    }
}
