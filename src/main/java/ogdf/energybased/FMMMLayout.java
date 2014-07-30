package ogdf.energybased;

/*
 * $Revision: 2565 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-07 17:14:54 +0200 (Sa, 07. Jul 2012) $
 ***************************************************************/
/**
 * \file \brief Implementation of Fast Multipole Multilevel Method (FM^3).
 *
 * \author Stefan Hachul
 *
 * \par License: This file is part of the Open Graph Drawing Framework (OGDF).
 *
 * \par Copyright (C)<br> See README.txt in the root directory of the OGDF installation for details.
 *
 * \par This program is free software; you can redistribute it and/or modify it under the terms of the GNU General
 * Public License Version 2 or 3 as published by the Free Software Foundation; see the file LICENSE.txt included in the
 * packaging of this file for details.
 *
 * \par This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * \par You should have received a copy of the GNU General Public License along with this program; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * \see http://www.gnu.org/copyleft/gpl.html
 **************************************************************
 */
import java.lang.*;
import java.util.*;
import java.io.*;
import ogdf.basic.*;
import org.BioLayoutExpress3D.Utils.ref;
import org.BioLayoutExpress3D.CoreUI.Dialogs.LayoutProgressBarDialog;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

public class FMMMLayout
{
    //! Possible page formats.
    enum PageFormatType
    {
        pfPortrait, //!< A4 portrait page.
        pfLandscape, //!< A4 landscape page.
        pfSquare     //!< Square format.
    };

    //! Trade-off between run-time and quality.
    public enum QualityVsSpeed
    {
        qvsGorgeousAndEfficient, //!< Best quality.
        qvsBeautifulAndFast, //!< Medium quality and speed.
        qvsNiceAndIncredibleSpeed //!< Best speed.
    };

    //! Specifies how the length of an edge is measured.
    enum EdgeLengthMeasurement
    {
        elmMidpoint, //!< Measure from center point of edge end points.
        elmBoundingCircle //!< Measure from border of circle s surrounding edge end points.
    };

    //! Specifies in which case it is allowed to tip over drawings of connected components.
    enum TipOver
    {
        toNone,
        toNoGrowingRow,
        toAlways
    };

    //! Specifies how connected components are sorted before the packing algorithm is applied.
    enum PreSort
    {
        psNone, //!< Do not presort.
        psDecreasingHeight, //!< Presort by decreasing height of components.
        psDecreasingWidth   //!< Presort by decreasing width of components.
    };

    //! Specifies how sun nodes of galaxies are selected.
    public enum GalaxyChoice
    {
        gcUniformProb,
        gcNonUniformProbLowerMass,
        gcNonUniformProbHigherMass
    };

    //! Specifies how MaxIterations is changed in subsequent multilevels.
    enum MaxIterChange
    {
        micConstant,
        micLinearlyDecreasing,
        micRapidlyDecreasing
    };

    //! Specifies how the initial placement is generated.
    public enum InitialPlacementMult
    {
        ipmSimple,
        ipmAdvanced
    };

    //! Specifies the force model.
    enum ForceModel
    {
        fmFruchtermanReingold, //!< The force-model by Fruchterman, Reingold.
        fmEades, //!< The force-model by Eades.
        fmNew                  //!< The new force-model.
    };

    //! Specifies how to calculate repulsive forces.
    public enum RepulsiveForcesMethod
    {
        rfcExact, //!< Exact calculation.
        rfcGridApproximation, //!< Grid approximation.
        rfcNMM                //!< Calculation as for new multipole method.
    };

    //! Specifies the stop criterion.
    public enum StopCriterion
    {
        scFixedIterations, //!< Stop if fixedIterations() is reached.
        scThreshold, //!< Stop if threshold() is reached.
        scFixedIterationsOrThreshold //!< Stop if fixedIterations() or threshold() is reached.
    };

    //! Specifies how the initial placement is done.
    public enum InitialPlacementForces
    {
        ipfUniformGrid, //!< Uniform placement on a grid.
        ipfRandomTime, //!< Random placement (based on current time).
        ipfRandomRandIterNr, //!< Random placement (based on randIterNr()).
        ipfKeepPositions     //!< No change in placement.
    };

    //! Specifies how the reduced bucket quadtree is constructed.
    public enum ReducedTreeConstruction
    {
        rtcPathByPath, //!< Path-by-path construction.
        rtcSubtreeBySubtree //!< Subtree-by-subtree construction.
    };

    //! Specifies how to calculate the smallest quadratic cell surrounding particles of a node in the reduced bucket quadtree.
    public enum SmallestCellFinding
    {
        scfIteratively, //!< Iteratively (in constant time).
        scfAluru        //!< According to formula by Aluru et al. (in constant time).
    };

    //! Returns the runtime (=CPU-time) of the layout algorithm in seconds.
    double getCpuTime()
    {
        return time_total;
    }

    /**
     * @}
     * @name High-level options Allow to specify the most relevant parameters.
     * @{
     */
    //! Returns the current setting of option useHighLevelOptions.
    /**
     * If set to true, the high-level options are used to set all low-level options. Usually, it is sufficient just to
     * set high-level options; if you want to be more specific, set this parameter to false and set the low level
     * options.
     */
    public boolean useHighLevelOptions()
    {
        return m_useHighLevelOptions;
    }

    //! Sets the option useHighLevelOptions to \a uho.
    public void useHighLevelOptions(boolean uho)
    {
        m_useHighLevelOptions = uho;
    }

    //! Sets single level option, no multilevel hierarchy is created if b == true
    void setSingleLevel(boolean b)
    {
        m_singleLevel = b;
    }

    //! Returns the current setting of option pageFormat.
    /**
     * This option defines the desired aspect ratio of the drawing area. - \a pfPortrait: A4 page in portrait
     * orientation - \a pfLandscape: A4 page in landscape orientation - \a pfSquare: square page format
     */
    PageFormatType pageFormat()
    {
        return m_pageFormat;
    }

    //! Sets the option pageRatio to \a t.
    void pageFormat(PageFormatType t)
    {
        m_pageFormat = t;
    }

    //! Returns the current setting of option unitEdgeLength.
    public double unitEdgeLength()
    {
        return m_unitEdgeLength;
    }

    //! Sets the option unitEdgeLength to \a x.
    public void unitEdgeLength(double x)
    {
        m_unitEdgeLength = ((x > 0.0) ? x : 1);
    }

    //! Returns the current setting of option newInitialPlacement.
    /**
     * This option defines if the initial placement of the nodes at the coarsest multilevel is varied for each distinct
     * call of FMMMLayout or keeps always the same.
     */
    public boolean newInitialPlacement()
    {
        return m_newInitialPlacement;
    }

    //! Sets the option newInitialPlacement to \a nip.
    public void newInitialPlacement(boolean nip)
    {
        m_newInitialPlacement = nip;
    }

    //! Returns the current setting of option qualityVersusSpeed.
    /**
     * Indicates if the algorithm is tuned either for best quality or best speed. - \a qvsGorgeousAndEfficient: gorgeous
     * quality and efficient speed - \a qvsBeautifulAndFast: beautiful quality and fast speed - \a
     * qvsNiceAndIncredibleSpeed: nice quality and incredible speed
     */
    public QualityVsSpeed qualityVersusSpeed()
    {
        return m_qualityVersusSpeed;
    }

    //! Sets the option qualityVersusSpeed to \a qvs.
    public void qualityVersusSpeed(QualityVsSpeed qvs)
    {
        m_qualityVersusSpeed = qvs;
    }

    /**
     * @}
     * @name General low-level options The low-level options in this and the following sections are meant for experts or
     * interested people only.
     * @{
     */
    //! Sets the seed of the random number generator.
    void randSeed(int p)
    {
        m_randSeed = ((0 <= p) ? p : 1);
    }

    //! Returns the seed of the random number generator.
    int randSeed()
    {
        return m_randSeed;
    }

    //! Returns the current setting of option edgeLengthMeasurement.
    /**
     * This option indicates how the length of an edge is measured. Possible values: - \a elmMidpoint: from center to
     * center - \a elmBoundingCircle: the distance between the two tight circles bounding the graphics of two adjacent
     * nodes
     */
    EdgeLengthMeasurement edgeLengthMeasurement()
    {
        return m_edgeLengthMeasurement;
    }

    //! Sets the option edgeLengthMeasurement to \a elm.
    void edgeLengthMeasurement(EdgeLengthMeasurement elm)
    {
        m_edgeLengthMeasurement = elm;
    }

    //! Returns the current setting of option maxIntPosExponent.
    /**
     * This option defines the exponent used if allowedPositions() == \a apExponent.
     */
    int maxIntPosExponent()
    {
        return m_maxIntPosExponent;
    }

    //! Sets the option maxIntPosExponent to \a e.
    void maxIntPosExponent(int e)
    {
        m_maxIntPosExponent = (((e >= 31) && (e <= 51)) ? e : 31);
    }

    /**
     * @}
     * @name Options for the divide et impera step
     * @{
     */
    //! Returns the current setting of option pageRatio.
    /**
     * This option defines the desired aspect ratio of the rectangular drawing area.
     */
    double pageRatio()
    {
        return m_pageRatio;
    }

    //! Sets the option pageRatio to \a r.
    void pageRatio(double r)
    {
        m_pageRatio = ((r > 0) ? r : 1);
    }

    //! Returns the current setting of option stepsForRotatingComponents.
    /**
     * This options determines the number of times each connected component is rotated with angles between 0 and 90
     * degree to obtain a bounding rectangle with small area.
     */
    int stepsForRotatingComponents()
    {
        return m_stepsForRotatingComponents;
    }

    //! Sets the option stepsForRotatingComponents to \a n.
    void stepsForRotatingComponents(int n)
    {
        m_stepsForRotatingComponents = ((0 <= n) ? n : 0);
    }

    //! Returns the current setting of option tipOverCCs.
    /**
     * Defines in which case it is allowed to tip over drawings of connected components. Possible values: - \a toNone:
     * not allowed at all - \a toNoGrowingRow: only if the height of the packing row does not grow - \a toAlways: always
     * allowed
     */
    TipOver tipOverCCs()
    {
        return m_tipOverCCs;
    }

    //! Sets the option tipOverCCs to \a to.
    void tipOverCCs(TipOver to)
    {
        m_tipOverCCs = to;
    }

    //! Returns the  minimal distance between connected components.
    double minDistCC()
    {
        return m_minDistCC;
    }

    //! Sets the  minimal distance between connected components to \a x.
    void minDistCC(double x)
    {
        m_minDistCC = ((x > 0) ? x : 1);
    }

    //! Returns the current setting of option presortCCs.
    /**
     * This option defines if the connected components are sorted before the packing algorithm is applied. Possible
     * values: - \a psNone: no sorting - \a psDecreasingHeight: sorted by decreasing height - \a psDecreasingWidth:
     * sorted by decreasing width
     */
    PreSort presortCCs()
    {
        return m_presortCCs;
    }

    //! Sets the option presortCCs to \a ps.
    void presortCCs(PreSort ps)
    {
        m_presortCCs = ps;
    }

    /**
     * @}
     * @name Options for the multilevel step
     * @{
     */
    //! Returns the current setting of option minGraphSize.
    /**
     * This option determines the number of nodes of a graph in the multilevel representation for which no more
     * collapsing of galaxies is performed (i.e. the graph at the highest level).
     */
    int minGraphSize()
    {
        return m_minGraphSize;
    }

    //! Sets the option minGraphSize to \a n.
    void minGraphSize(int n)
    {
        m_minGraphSize = ((n >= 2) ? n : 2);
    }

    //! Returns the current setting of option galaxyChoice.
    /**
     * This option defines how sun nodes of galaxies are selected. Possible values: - \a gcUniformProb: selecting by
     * uniform random probability - \a gcNonUniformProbLowerMass: selecting by non-uniform probability depending on the
     * star masses (prefering nodes with lower star mass) - \a gcNonUniformProbHigherMass: as above but prefering nodes
     * with higher star mass
     */
    GalaxyChoice galaxyChoice()
    {
        return m_galaxyChoice;
    }

    //! Sets the option galaxyChoice to \a gc.
    void galaxyChoice(GalaxyChoice gc)
    {
        m_galaxyChoice = gc;
    }

    //! Returns the current setting of option randomTries.
    /**
     * This option defines the number of tries to get a random node with minimal star mass (used in case of
     * galaxyChoice() == gcNonUniformProbLowerMass and galaxyChoice() == gcNonUniformProbHigherMass).
     */
    int randomTries()
    {
        return m_randomTries;
    }

    //! Sets the option randomTries to \a n.
    void randomTries(int n)
    {
        m_randomTries = ((n >= 1) ? n : 1);
    }

    //! Returns the current setting of option maxIterChange.
    /**
     * This option defines how MaxIterations is changed in subsequent multilevels. Possible values: - \a micConstant:
     * kept constant at the force calculation step at every level - \a micLinearlyDecreasing: linearly decreasing from
     * MaxIterFactor*FixedIterations to FixedIterations - \a micRapidlyDecreasing: rapdily decreasing from
     * MaxIterFactor*FixedIterations to FixedIterations
     */
    MaxIterChange maxIterChange()
    {
        return m_maxIterChange;
    }

    //! Sets the option maxIterChange to \a mic.
    void maxIterChange(MaxIterChange mic)
    {
        m_maxIterChange = mic;
    }

    //! Returns the current setting of option maxIterFactor.
    /**
     * This option defines the factor used for decrasing MaxIterations (in case of maxIterChange() ==
     * micLinearlyDecreasing or maxIterChange() == micRapidlyDecreasing).
     */
    public int maxIterFactor()
    {
        return m_maxIterFactor;
    }

    //! Sets the option maxIterFactor to \a f.
    public void maxIterFactor(int f)
    {
        m_maxIterFactor = ((f >= 1) ? f : 1);
    }

    //! Returns the current setting of option initialPlacementMult.
    /**
     * This option defines how the initial placement is generated. Possible values: - \a ipmSimple: only using
     * information about placement of nodes on higher levels - \a ipmAdvanced: using additional information about the
     * placement of all inter - \a solar system nodes
     */
    InitialPlacementMult initialPlacementMult()
    {
        return m_initialPlacementMult;
    }

    //! Sets the option initialPlacementMult to \a ipm.
    void initialPlacementMult(InitialPlacementMult ipm)
    {
        m_initialPlacementMult = ipm;
    }

    /**
     * @}
     * @name Options for the force calculation step
     * @{
     */
    //! Returns the used force model.
    /**
     * Possibly values: - \a fmFruchtermanReingold: model of Fruchterman and Reingold - \a fmEades: model of Eades - \a
     * fmNew: new model
     */
    ForceModel forceModel()
    {
        return m_forceModel;
    }

    //! Sets the used force model to \a fm.
    void forceModel(ForceModel fm)
    {
        m_forceModel = fm;
    }

    //! Returns the strength of the springs.
    double springStrength()
    {
        return m_springStrength;
    }

    //! Sets the strength of the springs to \a x.
    void springStrength(double x)
    {
        m_springStrength = ((x > 0) ? x : 1);
    }

    //! Returns the strength of the repulsive forces.
    double repForcesStrength()
    {
        return m_repForcesStrength;
    }

    //! Sets the strength of the repulsive forces to \a x.
    void repForcesStrength(double x)
    {
        m_repForcesStrength = ((x > 0) ? x : 1);
    }

    //! Returns the current setting of option repulsiveForcesCalculation.
    /**
     * This option defines how to calculate repulsive forces. Possible values: - \a rfcExact: exact calculation (slow) -
     * \a rfcGridApproximation: grid approxiamtion (inaccurate) - \a rfcNMM: like in NMM (= New Multipole Method; fast
     * and accurate)
     */
    public RepulsiveForcesMethod repulsiveForcesCalculation()
    {
        return m_repulsiveForcesCalculation;
    }

    //! Sets the option repulsiveForcesCalculation to \a rfc.
    public void repulsiveForcesCalculation(RepulsiveForcesMethod rfc)
    {
        m_repulsiveForcesCalculation = rfc;
    }

    //! Returns the stop criterion.
    /**
     * Possible values: - \a rscFixedIterations: stop if fixedIterations() is reached - \a rscThreshold: stop if
     * threshold() is reached - \a rscFixedIterationsOrThreshold: stop if fixedIterations() or threshold() is reached
     */
    public StopCriterion stopCriterion()
    {
        return m_stopCriterion;
    }

    //! Sets the stop criterion to \a rsc.
    public void stopCriterion(StopCriterion rsc)
    {
        m_stopCriterion = rsc;
    }

    //! Returns the threshold for the stop criterion.
    /**
     * (If the average absolute value of all forces in an iteration is less then threshold() then stop.)
     */
    double threshold()
    {
        return m_threshold;
    }

    //! Sets the threshold for the stop criterion to \a x.
    void threshold(double x)
    {
        m_threshold = ((x > 0) ? x : 0.1);
    }

    //! Returns the fixed number of iterations for the stop criterion.
    public int fixedIterations()
    {
        return m_fixedIterations;
    }

    //! Sets the fixed number of iterations for the stop criterion to \a n.
    public void fixedIterations(int n)
    {
        m_fixedIterations = ((n >= 1) ? n : 1);
    }

    //! Returns the scaling factor for the forces.
    double forceScalingFactor()
    {
        return m_forceScalingFactor;
    }

    //! Sets the scaling factor for the forces to \ f.
    void forceScalingFactor(double f)
    {
        m_forceScalingFactor = ((f > 0) ? f : 1);
    }

    //! Returns the current setting of option coolTemperature.
    /**
     * If set to true, forces are scaled by coolValue()^(actual iteration) * forceScalingFactor(); otherwise forces are
     * scaled by forceScalingFactor().
     */
    boolean coolTemperature()
    {
        return m_coolTemperature;
    }

    //! Sets the option coolTemperature to \a b.
    void coolTemperature(boolean b)
    {
        m_coolTemperature = b;
    }

    //! Returns the current setting of option coolValue.
    /**
     * This option defines the value by which forces are decreased if coolTemperature is true.
     */
    double coolValue()
    {
        return m_coolValue;
    }

    //! Sets the option coolValue to \a x.
    void coolValue(double x)
    {
        m_coolValue = (((x > 0) && (x <= 1)) ? x : 0.99);
    }

    //! Returns the current setting of option initialPlacementForces.
    /**
     * This option defines how the initial placement is done. Possible values: - \a ipfUniformGrid: uniform on a grid -
     * \a ipfRandomTime: random based on actual time - \a ipfRandomRandIterNr: random based on randIterNr() - \a
     * ipfKeepPositions: no change in placement
     */
    public InitialPlacementForces initialPlacementForces()
    {
        return m_initialPlacementForces;
    }

    //! Sets the option initialPlacementForces to \a ipf.
    public void initialPlacementForces(InitialPlacementForces ipf)
    {
        m_initialPlacementForces = ipf;
    }

    /**
     * @}
     * @name Options for the postprocessing step
     * @{
     */
    //! Returns the current setting of option resizeDrawing.
    /**
     * If set to true, the resulting drawing is resized so that the average edge length is the desired edge length times
     * resizingScalar().
     */
    boolean resizeDrawing()
    {
        return m_resizeDrawing;
    }

    //! Sets the option resizeDrawing to \a b.
    void resizeDrawing(boolean b)
    {
        m_resizeDrawing = b;
    }

    //! Returns the current setting of option resizingScalar.
    /**
     * This option defines a parameter to scale the drawing if resizeDrawing() is true.
     */
    double resizingScalar()
    {
        return m_resizingScalar;
    }

    //! Sets the option resizingScalar to \a s.
    void resizingScalar(double s)
    {
        m_resizingScalar = ((s > 0) ? s : 1);
    }

    //! Returns the number of iterations for fine tuning.
    public int fineTuningIterations()
    {
        return m_fineTuningIterations;
    }

    //! Sets the number of iterations for fine tuning to \a n.
    public void fineTuningIterations(int n)
    {
        m_fineTuningIterations = ((n >= 0) ? n : 0);
    }

    //! Returns the curent setting of option fineTuneScalar.
    /**
     * This option defines a parameter for scaling the forces in the fine-tuning iterations.
     */
    double fineTuneScalar()
    {
        return m_fineTuneScalar;
    }

    //! Sets the option fineTuneScalar to \a s
    void fineTuneScalar(double s)
    {
        m_fineTuneScalar = ((s >= 0) ? s : 1);
    }

    //! Returns the current setting of option adjustPostRepStrengthDynamically.
    /**
     * If set to true, the strength of the repulsive force field is calculated dynamically by a formula depending on the
     * number of nodes; otherwise the strength are scaled by PostSpringStrength and PostStrengthOfRepForces.
     */
    boolean adjustPostRepStrengthDynamically()
    {
        return m_adjustPostRepStrengthDynamically;
    }

    //! Sets the option adjustPostRepStrengthDynamically to \a b.
    void adjustPostRepStrengthDynamically(boolean b)
    {
        m_adjustPostRepStrengthDynamically = b;
    }

    //! Returns the strength of the springs in the postprocessing step.
    double postSpringStrength()
    {
        return m_postSpringStrength;
    }

    //! Sets the strength of the springs in the postprocessing step to \a x.
    void postSpringStrength(double x)
    {
        m_postSpringStrength = ((x > 0) ? x : 1);
    }

    //! Returns the strength of the repulsive forces in the postprocessing step.
    double postStrengthOfRepForces()
    {
        return m_postStrengthOfRepForces;
    }

    //! Sets the strength of the repulsive forces in the postprocessing step to \a x.
    void postStrengthOfRepForces(double x)
    {
        m_postStrengthOfRepForces = ((x > 0) ? x : 1);
    }

    /**
     * @}
     * @name Options for repulsive force approximation methods
     * @{
     */
    //! Returns the current setting of option frGridQuotient.
    /**
     * The number k of rows and columns of the grid is sqrt(|V|) / frGridQuotient(). (Note that in
     * [Fruchterman,Reingold] frGridQuotient is 2.)
     */
    int frGridQuotient()
    {
        return m_frGridQuotient;
    }

    //! Sets the option frGridQuotient to \a p.
    void frGridQuotient(int p)
    {
        m_frGridQuotient = ((0 <= p) ? p : 2);
    }

    //! Returns the current setting of option nmTreeConstruction.
    /**
     * This option defines how the reduced bucket quadtree is constructed. Possible values: - \a rtcPathByPath: path by
     * path construction - \a rtcSubtreeBySubtree: subtree by subtree construction
     */
    ReducedTreeConstruction nmTreeConstruction()
    {
        return m_NMTreeConstruction;
    }

    //! Sets the option nmTreeConstruction to \a rtc.
    void nmTreeConstruction(ReducedTreeConstruction rtc)
    {
        m_NMTreeConstruction = rtc;
    }

    //! Returns the current setting of option nmSmallCell.
    /**
     * This option defines how the smallest quadratic cell that surrounds the particles of a node in the reduced bucket
     * quadtree is calculated. Possible values: - \a scfIteratively: iteratively (in constant time) - \a scfAluru: by
     * the formula by Aluru et al. (in constant time)
     */
    SmallestCellFinding nmSmallCell()
    {
        return m_NMSmallCell;
    }

    //! Sets the option nmSmallCell to \a scf.
    void nmSmallCell(SmallestCellFinding scf)
    {
        m_NMSmallCell = scf;
    }

    //! Returns the current setting of option nmParticlesInLeaves.
    /**
     * Defines the maximal number of particles that are contained in a leaf of the reduced bucket quadtree.
     */
    int nmParticlesInLeaves()
    {
        return m_NMParticlesInLeaves;
    }

    //! Sets the option nmParticlesInLeaves to \a n.
    void nmParticlesInLeaves(int n)
    {
        m_NMParticlesInLeaves = ((n >= 1) ? n : 1);
    }

    //! Returns the precision \a p for the <i>p</i>-term multipole expansions.
    int nmPrecision()
    {
        return m_NMPrecision;
    }

    //! Sets the precision for the multipole expansions to \ p.
    void nmPrecision(int p)
    {
        m_NMPrecision = ((p >= 1) ? p : 1);
    }
    //! @}
    //high level options
    boolean m_useHighLevelOptions; //!< The option for using high-level options.
    PageFormatType m_pageFormat; //!< The option for the page format.
    double m_unitEdgeLength; //!< The unit edge length.
    boolean m_newInitialPlacement; //!< The option for new initial placement.
    QualityVsSpeed m_qualityVersusSpeed; //!< The option for quality-vs-speed trade-off.
    //low level options
    //general options
    int m_randSeed; //!< The random seed.
    EdgeLengthMeasurement m_edgeLengthMeasurement; //!< The option for edge length measurement.
    int m_maxIntPosExponent; //!< The option for the used	exponent.
    //options for divide et impera step
    double m_pageRatio; //!< The desired page ratio.
    int m_stepsForRotatingComponents; //!< The number of rotations.
    TipOver m_tipOverCCs; //!< Option for tip-over of connected components.
    double m_minDistCC; //!< The separation between connected components.
    PreSort m_presortCCs; //!< The option for presorting connected components.
    //options for multilevel step
    boolean m_singleLevel; //!< Option for pure single level.
    int m_minGraphSize; //!< The option for minimal graph size.
    GalaxyChoice m_galaxyChoice; //!< The selection of galaxy nodes.
    int m_randomTries; //!< The number of random tries.
    MaxIterChange m_maxIterChange; //!< The option for how to change MaxIterations.
    //!< If maxIterChange != micConstant, the iterations are decreased
    //!< depending on the level, starting from
    //!< ((maxIterFactor()-1) * fixedIterations())
    int m_maxIterFactor; //!< The factor used for decreasing MaxIterations.
    InitialPlacementMult m_initialPlacementMult; //!< The option for creating initial placement.
    //options for force calculation step
    ForceModel m_forceModel; //!< The used force model.
    double m_springStrength; //!< The strengths of springs.
    double m_repForcesStrength; //!< The strength of repulsive forces.
    RepulsiveForcesMethod m_repulsiveForcesCalculation; //!< Option for how to calculate repulsive forces.
    StopCriterion m_stopCriterion; //!< The stop criterion.
    double m_threshold; //!< The threshold for the stop criterion.
    int m_fixedIterations; //!< The fixed number of iterations for the stop criterion.
    double m_forceScalingFactor; //!< The scaling factor for the forces.
    boolean m_coolTemperature; //!< The option for how to scale forces.
    double m_coolValue; //!< The value by which forces are decreased.
    InitialPlacementForces m_initialPlacementForces; //!< The option for how the initial placement is done.
    //options for postprocessing step
    boolean m_resizeDrawing; //!< The option for resizing the drawing.
    double m_resizingScalar; //!< Parameter for resizing the drawing.
    int m_fineTuningIterations; //!< The number of iterations for fine tuning.
    double m_fineTuneScalar; //!< Parameter for scaling forces during fine tuning.
    boolean m_adjustPostRepStrengthDynamically; //!< The option adjustPostRepStrengthDynamically.
    double m_postSpringStrength; //!< The strength of springs during postprocessing.
    double m_postStrengthOfRepForces; //!< The strength of repulsive forces during postprocessing.
    //options for repulsive force approximation methods
    int m_frGridQuotient; //!< The grid quotient.
    ReducedTreeConstruction m_NMTreeConstruction; //!< The option for how to construct reduced bucket quadtree.
    SmallestCellFinding m_NMSmallCell; //!< The option for how to calculate smallest quadtratic cells.
    int m_NMParticlesInLeaves; //!< The maximal number of particles in a leaf.
    int m_NMPrecision; //!< The precision for multipole expansions.
    //other variables
    double max_integer_position; //!< The maximum value for an integer position.
    double cool_factor; //!< Needed for scaling the forces if coolTemperature is true.
    double average_ideal_edgelength; //!< Measured from center to center.
    double boxlength; //!< Holds the length of the quadratic comput. box.
    int number_of_components; //!< The number of components of the graph.
    DPoint down_left_corner; //!< Holds down left corner of the comput. box.
    NodeArray<Double> radius; //!< Holds the radius of the surrounding circle for each node.
    double time_total; //!< The runtime (=CPU-time) of the algorithm in seconds.
    FruchtermanReingold FR; //!< Class for repulsive force calculation (Fruchterman, Reingold).
    //NMM NM; //!< Class for repulsive force calculation.
    Random random;

    public FMMMLayout()
    {
        initialize_all_options();
    }

    private LayoutProgressBarDialog progressDialog;

//--------------------------- most important functions --------------------------------
    public void call(GraphAttributes GA, LayoutProgressBarDialog progressDialog)
    {
        Graph G = GA.constGraph();
        EdgeArray<Double> edgelength = new EdgeArray<Double>(G, 1.0, Factory.DOUBLE);
        call(GA, edgelength, progressDialog);
    }

    public void call(GraphAttributes GA, EdgeArray<Double> edgeLength, LayoutProgressBarDialog progressDialog)
    {
        random = new Random(37112);
        numexcept.random = random;
        FR = new FruchtermanReingold();
        //NM = new NMM(random);
        this.progressDialog = progressDialog;

        progressDialog.prepareProgressBar(100, "FMMM layout", true);
        progressDialog.startProgressBar();

        Graph G = GA.constGraph();
        NodeArray<NodeAttributes> A = new NodeArray<NodeAttributes>(G, Factory.NODE_ATTRIBUTES);       //stores the attributes of the nodes (given by L)
        EdgeArray<EdgeAttributes> E = new EdgeArray<EdgeAttributes>(G, Factory.EDGE_ATTRIBUTES);       //stores the edge attributes of G
        Graph G_reduced = new Graph();                      //stores a undirected simple and loopfree copy
        //of G
        EdgeArray<EdgeAttributes> E_reduced = new EdgeArray<EdgeAttributes>();  //stores the edge attributes of G_reduced
        NodeArray<NodeAttributes> A_reduced = new NodeArray<NodeAttributes>();  //stores the node attributes of G_reduced

        if (G.numberOfNodes() > 1)
        {
            if (useHighLevelOptions())
            {
                update_low_level_options_due_to_high_level_options_settings();
            }
            import_NodeAttributes(G, GA, A);
            import_EdgeAttributes(G, edgeLength, E);

            /*double t_total;
             usedTime(t_total);*/
            max_integer_position = Math.pow(2.0, maxIntPosExponent());
            radius = new NodeArray<Double>();
            init_ind_ideal_edgelength(G, A, E);
            make_simple_loopfree(G, A, E, G_reduced, A_reduced, E_reduced);
            call_DIVIDE_ET_IMPERA_step(G_reduced, A_reduced, E_reduced);
            //time_total = usedTime(t_total);

            export_NodeAttributes(G_reduced, A_reduced, GA);
        }
        else //trivial cases
        {
            if (G.numberOfNodes() == 1)
            {
                node v = G.firstNode();
                GA.setPosition(v, PointFactory.INSTANCE.newDPoint());
            }
        }

        FR.shutdown();

        progressDialog.endProgressBar();
        progressDialog.stopProgressBar();
    }

    void call_DIVIDE_ET_IMPERA_step(
            Graph G,
            NodeArray<NodeAttributes> A,
            EdgeArray<EdgeAttributes> E)
    {
        NodeArray<Integer> component = new NodeArray<Integer>(G, Factory.INTEGER); //holds for each node the index of its component
        number_of_components = SimpleGraphAlg.connectedComponents(G, component);//calculate components of G
        List<Graph> G_sub = new ArrayList<Graph>();
        List<NodeArray<NodeAttributes>> A_sub = new ArrayList<NodeArray<NodeAttributes>>();
        List<EdgeArray<EdgeAttributes>> E_sub = new ArrayList<EdgeArray<EdgeAttributes>>();

        for (int i = 0; i < number_of_components; i++)
        {
            G_sub.add(new Graph());
            A_sub.add(new NodeArray<NodeAttributes>());
            E_sub.add(new EdgeArray<EdgeAttributes>());
        }

        create_maximum_connected_subGraphs(G, A, E, G_sub, A_sub, E_sub, component);

        for (int i = 0; i < number_of_components; i++)
        {
            if (progressDialog.userHasCancelled())
            {
                return;
            }

            call_MULTILEVEL_step_for_subGraph(G_sub.get(i), A_sub.get(i), E_sub.get(i), i, number_of_components);
        }

        pack_subGraph_drawings(A, G_sub, A_sub);
        //delete_all_subGraphs(G_sub,A_sub,E_sub);
    }

    void call_MULTILEVEL_step_for_subGraph(
            Graph G,
            NodeArray<NodeAttributes> A,
            EdgeArray<EdgeAttributes> E,
            int comp_index,
            int num_components)
    {
        Multilevel Mult = new Multilevel(random);

        int max_level = 30;//sufficient for all graphs with upto pow(2,30) nodes!
        //adapt mingraphsize such that no levels are created beyond input graph.
        if (m_singleLevel)
        {
            m_minGraphSize = G.numberOfNodes();
        }
        List<Graph> G_mult_ptr = new ArrayList<Graph>();
        List<NodeArray<NodeAttributes>> A_mult_ptr = new ArrayList<NodeArray<NodeAttributes>>();
        List<EdgeArray<EdgeAttributes>> E_mult_ptr = new ArrayList<EdgeArray<EdgeAttributes>>();

        for (int i = 0; i < max_level + 1; i++)
        {
            G_mult_ptr.add(new Graph());
            A_mult_ptr.add(new NodeArray<NodeAttributes>());
            E_mult_ptr.add(new EdgeArray<EdgeAttributes>());
        }

        progressDialog.incrementProgress(0);
        progressDialog.setText("FMMM layout" +
                    ", component " + (comp_index + 1) + " of " + num_components +
                    ", creating multilevel representations");

        max_level = Mult.create_multilevel_representations(G, A, E, randSeed(),
                galaxyChoice(), minGraphSize(),
                randomTries(), G_mult_ptr, A_mult_ptr,
                E_mult_ptr);

        for (int i = max_level; i >= 0; i--)
        {
            if (progressDialog.userHasCancelled())
            {
                return;
            }

            if (i == max_level)
            {
                create_initial_placement(G_mult_ptr.get(i), A_mult_ptr.get(i));
            }
            else
            {
                Mult.find_initial_placement_for_level(i, initialPlacementMult(), G_mult_ptr, A_mult_ptr, E_mult_ptr);
                update_boxlength_and_cornercoordinate(G_mult_ptr.get(i), A_mult_ptr.get(i));
            }
            call_FORCE_CALCULATION_step(G_mult_ptr.get(i), A_mult_ptr.get(i), E_mult_ptr.get(i),
                    i, max_level, comp_index, num_components);
        }
        //Mult.delete_multilevel_representations(G_mult_ptr,A_mult_ptr,E_mult_ptr,max_level);
    }

    void call_FORCE_CALCULATION_step(
            Graph G,
            NodeArray<NodeAttributes> A,
            EdgeArray<EdgeAttributes> E,
            int act_level,
            int max_level,
            int comp_index,
            int num_components)
    {
        int ITERBOUND = 10000;//needed to guarantee termination if
        //stopCriterion() == scThreshold
        if (G.numberOfNodes() > 1)
        {
            int iter = 1;
            int max_mult_iter = get_max_mult_iter(act_level, max_level, G.numberOfNodes());
            double actforcevectorlength = threshold() + 1;

            if (stopCriterion() == StopCriterion.scThreshold)
            {
                max_mult_iter = ITERBOUND;
            }

            NodeArray<DPoint> F_rep = new NodeArray<DPoint>(G, Factory.DPOINT); //stores rep. forces
            NodeArray<DPoint> F_attr = new NodeArray<DPoint>(G, Factory.DPOINT); //stores attr. forces
            NodeArray<DPoint> F = new NodeArray<DPoint>(G, Factory.DPOINT); //stores resulting forces
            NodeArray<DPoint> last_node_movement = new NodeArray<DPoint>(G, Factory.DPOINT);//stores the force vectors F of the last
            //iterations (needed to avoid oscillations)

            set_average_ideal_edgelength(G, E);//needed for easy scaling of the forces
            make_initialisations_for_rep_calc_classes(G);

            progressDialog.prepareProgressBar(max_mult_iter,
                    "FMMM layout" +
                    ", component " + (comp_index + 1) + " of " + num_components +
                    ", level " + ((max_level - act_level) + 1) + " of " + (max_level + 1), true);

            while (((stopCriterion() == StopCriterion.scFixedIterations) && (iter <= max_mult_iter)) ||
                    ((stopCriterion() == StopCriterion.scThreshold) && (actforcevectorlength >= threshold()) &&
                    (iter <= max_mult_iter)) ||
                    ((stopCriterion() == StopCriterion.scFixedIterationsOrThreshold) && (iter <= max_mult_iter) &&
                    (actforcevectorlength >= threshold())))
            {//while
                calculate_forces(G, A, E, F, F_attr, F_rep, last_node_movement, iter, 0);
                if (stopCriterion() != StopCriterion.scFixedIterations)
                {
                    actforcevectorlength = get_average_forcevector_length(G, F);
                }

                if (progressDialog.userHasCancelled())
                {
                    return;
                }

                progressDialog.incrementProgress(iter);
                iter++;
            }//while

            if (!progressDialog.userHasCancelled() && act_level == 0)
            {
                call_POSTPROCESSING_step(G, A, E, F, F_attr, F_rep, last_node_movement, comp_index, num_components);
            }

            //deallocate_memory_for_rep_calc_classes();
        }
    }

    void call_POSTPROCESSING_step(
            Graph G,
            NodeArray<NodeAttributes> A,
            EdgeArray<EdgeAttributes> E,
            NodeArray<DPoint> F,
            NodeArray<DPoint> F_attr,
            NodeArray<DPoint> F_rep,
            NodeArray<DPoint> last_node_movement,
            int comp_index,
            int num_components)
    {
        progressDialog.prepareProgressBar(10,
                "FMMM layout" +
                ", component " + (comp_index + 1) + " of " + num_components +
                ", post-processing", true);

        for (int i = 1; i <= 10; i++)
        {
            if (progressDialog.userHasCancelled())
            {
                return;
            }

            progressDialog.incrementProgress(i);
            calculate_forces(G, A, E, F, F_attr, F_rep, last_node_movement, i, 1);
        }

        if ((resizeDrawing() == true))
        {
            adapt_drawing_to_ideal_average_edgelength(G, A, E);
            update_boxlength_and_cornercoordinate(G, A);
        }

        progressDialog.prepareProgressBar(fineTuningIterations(),
                "FMMM layout" +
                ", component " + (comp_index + 1) + " of " + num_components +
                ", fine-tuning", true);

        for (int i = 1; i <= fineTuningIterations(); i++)
        {
            if (progressDialog.userHasCancelled())
            {
                return;
            }

            progressDialog.incrementProgress(i);
            calculate_forces(G, A, E, F, F_attr, F_rep, last_node_movement, i, 2);
        }

        if ((resizeDrawing() == true))
        {
            adapt_drawing_to_ideal_average_edgelength(G, A, E);
        }
    }

//------------------------- functions for pre/post-processing -------------------------
    void initialize_all_options()
    {
        //setting high level options
        useHighLevelOptions(false);
        pageFormat(PageFormatType.pfSquare);
        unitEdgeLength(100);
        newInitialPlacement(false);
        qualityVersusSpeed(QualityVsSpeed.qvsBeautifulAndFast);

        //setting low level options
        //setting general options
        randSeed(100);
        edgeLengthMeasurement(EdgeLengthMeasurement.elmBoundingCircle);
        maxIntPosExponent(40);

        //setting options for the divide et impera step
        pageRatio(1.0);
        stepsForRotatingComponents(10);
        tipOverCCs(TipOver.toNoGrowingRow);
        minDistCC(100);
        presortCCs(PreSort.psDecreasingHeight);

        //setting options for the multilevel step
        minGraphSize(50);
        galaxyChoice(GalaxyChoice.gcNonUniformProbLowerMass);
        randomTries(20);
        maxIterChange(MaxIterChange.micLinearlyDecreasing);
        maxIterFactor(10);
        initialPlacementMult(InitialPlacementMult.ipmAdvanced);
        m_singleLevel = false;

        //setting options for the force calculation step
        forceModel(ForceModel.fmNew);
        springStrength(1);
        repForcesStrength(1);
        repulsiveForcesCalculation(RepulsiveForcesMethod.rfcNMM);
        stopCriterion(StopCriterion.scFixedIterationsOrThreshold);
        threshold(0.01);
        fixedIterations(30);
        forceScalingFactor(0.05);
        coolTemperature(false);
        coolValue(0.99);
        initialPlacementForces(InitialPlacementForces.ipfRandomRandIterNr);

        //setting options for postprocessing
        resizeDrawing(true);
        resizingScalar(1);
        fineTuningIterations(20);
        fineTuneScalar(0.2);
        adjustPostRepStrengthDynamically(true);
        postSpringStrength(2.0);
        postStrengthOfRepForces(0.01);

        //setting options for different repulsive force calculation methods
        frGridQuotient(2);
        nmTreeConstruction(ReducedTreeConstruction.rtcSubtreeBySubtree);
        nmSmallCell(SmallestCellFinding.scfIteratively);
        nmParticlesInLeaves(25);
        nmPrecision(4);
    }

    void update_low_level_options_due_to_high_level_options_settings()
    {
        PageFormatType pf = pageFormat();
        double uel = unitEdgeLength();
        boolean nip = newInitialPlacement();
        QualityVsSpeed qvs = qualityVersusSpeed();

        //update
        initialize_all_options();
        useHighLevelOptions(true);
        pageFormat(pf);
        unitEdgeLength(uel);
        newInitialPlacement(nip);
        qualityVersusSpeed(qvs);

        if (pageFormat() == PageFormatType.pfSquare)
        {
            pageRatio(1.0);
        }
        else if (pageFormat() == PageFormatType.pfLandscape)
        {
            pageRatio(1.4142);
        }
        else //pageFormat() == pfPortrait
        {
            pageRatio(0.7071);
        }

        if (newInitialPlacement())
        {
            initialPlacementForces(InitialPlacementForces.ipfRandomTime);
        }
        else
        {
            initialPlacementForces(InitialPlacementForces.ipfRandomRandIterNr);
        }

        if (qualityVersusSpeed() == QualityVsSpeed.qvsGorgeousAndEfficient)
        {
            fixedIterations(60);
            fineTuningIterations(40);
            nmPrecision(6);
        }
        else if (qualityVersusSpeed() == QualityVsSpeed.qvsBeautifulAndFast)
        {
            fixedIterations(30);
            fineTuningIterations(20);
            nmPrecision(4);
        }
        else //qualityVersusSpeed() == qvsNiceAndIncredibleSpeed
        {
            fixedIterations(15);
            fineTuningIterations(10);
            nmPrecision(2);
        }
    }

    void import_NodeAttributes(
            Graph G,
            GraphAttributes GA,
            NodeArray<NodeAttributes> A)
    {
        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            node v = i.next();
            DPoint position = GA.position(v);
            A.get(v).set_NodeAttributes(GA.width(v), GA.height(v), position, null, null);
        }
    }

    void import_EdgeAttributes(
            Graph G,
            EdgeArray<Double> edgeLength,
            EdgeArray<EdgeAttributes> E)
    {
        double length;

        for (Iterator<edge> i = G.edgesIterator(); i.hasNext();)
        {
            edge e = i.next();
            if (edgeLength.get(e) > 0) //no negative edgelength allowed
            {
                length = edgeLength.get(e);
            }
            else
            {
                length = 1;
            }

            E.get(e).set_EdgeAttributes(length, null, null);
        }
    }

    void init_ind_ideal_edgelength(
            Graph G,
            NodeArray<NodeAttributes> A,
            EdgeArray<EdgeAttributes> E)
    {
        if (edgeLengthMeasurement() == EdgeLengthMeasurement.elmMidpoint)
        {
            for (Iterator<edge> i = G.edgesIterator(); i.hasNext();)
            {
                edge e = i.next();
                E.get(e).set_length(E.get(e).get_length() * unitEdgeLength());
            }
        }
        else //(edgeLengthMeasurement() == elmBoundingCircle)
        {
            set_radii(G, A);
            for (Iterator<edge> i = G.edgesIterator(); i.hasNext();)
            {
                edge e = i.next();
                E.get(e).set_length(E.get(e).get_length() * unitEdgeLength() + radius.get(e.source()) +
                        radius.get(e.target()));
            }
        }
    }

    void set_radii(Graph G, NodeArray<NodeAttributes> A)
    {
        radius.init(G, Factory.DOUBLE);
        double w, h;
        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            node v = i.next();
            w = A.get(v).get_width() / 2;
            h = A.get(v).get_height() / 2;
            radius.set(v, new Double(Math.sqrt(w * w + h * h)));
        }
    }

    void export_NodeAttributes(
            Graph G_reduced,
            NodeArray<NodeAttributes> A_reduced,
            GraphAttributes GA)
    {
        for (Iterator<node> i = G_reduced.nodesIterator(); i.hasNext();)
        {
            node v_copy = i.next();

            GA.setPosition(A_reduced.get(v_copy).get_original_node(), A_reduced.get(v_copy).get_position());
        }
    }

    void make_simple_loopfree(
            Graph G,
            NodeArray<NodeAttributes> A,
            EdgeArray<EdgeAttributes> E,
            Graph G_reduced,
            NodeArray<NodeAttributes> A_reduced,
            EdgeArray<EdgeAttributes> E_reduced)
    {
        node u_orig, v_orig, v_reduced;
        edge e_reduced, e_orig;

        //create the reduced Graph G_reduced and save in A/E links to node/edges of G_reduced
        //create G_reduced as a copy of G without selfloops!

        G_reduced.clear();
        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            v_orig = i.next();
            A.get(v_orig).set_copy_node(G_reduced.newNode());
        }

        for (Iterator<edge> i = G.edgesIterator(); i.hasNext();)
        {
            e_orig = i.next();
            u_orig = e_orig.source();
            v_orig = e_orig.target();
            if (u_orig != v_orig)
            {
                E.get(e_orig).set_copy_edge(G_reduced.newEdge(A.get(u_orig).get_copy_node(),
                        A.get(v_orig).get_copy_node()));
            }
            else
            {
                E.get(e_orig).set_copy_edge(null);//mark this edge as deleted
            }
        }

        //remove parallel (and reversed) edges from G_reduced
        EdgeArray<Double> new_edgelength = new EdgeArray<Double>(G_reduced, Factory.DOUBLE);
        List<edge> S = new ArrayList<edge>();
        S.clear();
        delete_parallel_edges(G, E, G_reduced, S, new_edgelength);

        //make A_reduced, E_reduced valid for G_reduced
        A_reduced.init(G_reduced, Factory.NODE_ATTRIBUTES);
        E_reduced.init(G_reduced, Factory.EDGE_ATTRIBUTES);

        //import information for A_reduced, E_reduced and links to the original nodes/edges
        //of the copy nodes/edges
        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            v_orig = i.next();
            v_reduced = A.get(v_orig).get_copy_node();
            A_reduced.get(v_reduced).set_NodeAttributes(
                    A.get(v_orig).get_width(),
                    A.get(v_orig).get_height(),
                    A.get(v_orig).get_position(),
                    v_orig, null);
        }
        for (Iterator<edge> i = G.edgesIterator(); i.hasNext();)
        {
            e_orig = i.next();
            e_reduced = E.get(e_orig).get_copy_edge();
            if (e_reduced != null)
            {
                E_reduced.get(e_reduced).set_EdgeAttributes(E.get(e_orig).get_length(), e_orig, null);
            }
        }

        //update edgelength of copy edges in G_reduced associated with a set of parallel
        //edges in G
        update_edgelength(S, new_edgelength, E_reduced);
    }

    void delete_parallel_edges(
            Graph G,
            EdgeArray<EdgeAttributes> E,
            Graph G_reduced,
            List<edge> S,
            EdgeArray<Double> new_edgelength)
    {
        ListIterator<Edge> EdgeIterator;
        edge e_act, e_save = null;
        Edge f_act = new Edge();
        List<Edge> sorted_edges = new ArrayList<Edge>();
        EdgeArray<edge> original_edge = new EdgeArray<edge>(G_reduced, Factory.EDGE); //helping array
	int save_s_index = 0, save_t_index = 0, act_s_index, act_t_index;
        int counter = 1;
        Graph Graph_ptr = G_reduced;

        //save the original edges for each edge in G_reduced
        for (Iterator<edge> i = G.edgesIterator(); i.hasNext();)
        {
            e_act = i.next();
            if (E.get(e_act).get_copy_edge() != null) //e_act is no self_loops
            {
                original_edge.set(E.get(e_act).get_copy_edge(), e_act);
            }
        }

        for (Iterator<edge> i = G_reduced.edgesIterator(); i.hasNext();)
        {
            e_act = i.next();
            f_act.set_Edge(e_act, Graph_ptr);
            sorted_edges.add(f_act);
        }

        // FIXME: may not be correct
        Collections.sort(sorted_edges, new java.util.Comparator<Edge>()
        {
            @Override
            public int compare(Edge a, Edge b)
            {
		int a_index = a.get_edge().source().index() -
                    a.get_edge().target().index();
                int b_index = b.get_edge().source().index() -
                    b.get_edge().target().index();

                return b_index - a_index;
            }
        });

        //now parallel edges are consecutive in sorted_edges
        for (Edge e : sorted_edges)
        {//for
            e_act = e.get_edge();
            act_s_index = e_act.source().index();
            act_t_index = e_act.target().index();

            if (e != sorted_edges.get(0))
            {//if
                if ((act_s_index == save_s_index && act_t_index == save_t_index) ||
                        (act_s_index == save_t_index && act_t_index == save_s_index))
                {
                    if (counter == 1) //first parallel edge
                    {
                        S.add(e_save);
                        new_edgelength.set(e_save, E.get(original_edge.get(e_save)).get_length() +
                                E.get(original_edge.get(e_act)).get_length());
                    }
                    else //more then two parallel edges
                    {
                        new_edgelength.set(e_save, new_edgelength.get(e_save) +
                                E.get(original_edge.get(e_act)).get_length());
                    }

                    E.get(original_edge.get(e_act)).set_copy_edge(null); //mark copy of edge as deleted
                    G_reduced.delEdge(e_act);                    //delete copy edge in G_reduced
                    counter++;
                }
                else
                {
                    if (counter > 1)
                    {
                        new_edgelength.set(e_save, new_edgelength.get(e_save) / counter);
                        counter = 1;
                    }
                    save_s_index = act_s_index;
                    save_t_index = act_t_index;
                    e_save = e_act;
                }
            }//if
            else //first edge
            {
                save_s_index = act_s_index;
                save_t_index = act_t_index;
                e_save = e_act;
            }
        }//for

        //treat special case (last edges were multiple edges)
        if (counter > 1)
        {
            new_edgelength.set(e_save, new_edgelength.get(e_save) / counter);
        }
    }

    void update_edgelength(
            List<edge> S,
            EdgeArray<Double> new_edgelength,
            EdgeArray<EdgeAttributes> E_reduced)
    {
        edge e;
        while (!S.isEmpty())
        {
            e = S.get(0);
            S.remove(0); //S.popFrontRet();
            E_reduced.get(e).set_length(new_edgelength.get(e));
        }
    }

    double get_post_rep_force_strength(int n)
    {
        return Math.min(0.2, 400.0 / (double) n);
    }

//------------------------- functions for divide et impera step -----------------------
    void create_maximum_connected_subGraphs(
            Graph G,
            NodeArray<NodeAttributes> A,
            EdgeArray<EdgeAttributes> E,
            List<Graph> G_sub,
            List<NodeArray<NodeAttributes>> A_sub,
            List<EdgeArray<EdgeAttributes>> E_sub,
            NodeArray<Integer> component)
    {
        node u_orig, v_orig, v_sub;
        edge e_sub, e_orig;
        int i;

        //create the subgraphs and save links to subgraph nodes/edges in A
        for (Iterator<node> iter = G.nodesIterator(); iter.hasNext();)
        {
            v_orig = iter.next();
            A.get(v_orig).set_subgraph_node(G_sub.get(component.get(v_orig)).newNode());
        }

        for (Iterator<edge> iter = G.edgesIterator(); iter.hasNext();)
        {
            e_orig = iter.next();
            u_orig = e_orig.source();
            v_orig = e_orig.target();
            E.get(e_orig).set_subgraph_edge(G_sub.get(component.get(u_orig)).newEdge(A.get(u_orig).get_subgraph_node(), A.get(v_orig).get_subgraph_node()));
        }

        //make A_sub,E_sub valid for the subgraphs
        for (i = 0; i < number_of_components; i++)
        {
            A_sub.get(i).init(G_sub.get(i), Factory.NODE_ATTRIBUTES);
            E_sub.get(i).init(G_sub.get(i), Factory.EDGE_ATTRIBUTES);
        }

        //import information for A_sub,E_sub and links to the original nodes/edges
        //of the subGraph nodes/edges

        for (Iterator<node> iter = G.nodesIterator(); iter.hasNext();)
        {
            v_orig = iter.next();
            v_sub = A.get(v_orig).get_subgraph_node();
            A_sub.get(component.get(v_orig)).get(v_sub).set_NodeAttributes(A.get(v_orig).get_width(),
                    A.get(v_orig).get_height(), A.get(v_orig).get_position(),
                    v_orig, null);
        }

        for (Iterator<edge> iter = G.edgesIterator(); iter.hasNext();)
        {
            e_orig = iter.next();
            e_sub = E.get(e_orig).get_subgraph_edge();
            v_orig = e_orig.source();
            E_sub.get(component.get(v_orig)).get(e_sub).set_EdgeAttributes(E.get(e_orig).get_length(),
                    e_orig, null);
        }
    }

    void pack_subGraph_drawings(
            NodeArray<NodeAttributes> A,
            List<Graph> G_sub,
            List<NodeArray<NodeAttributes>> A_sub)
    {
        ref<Double> aspect_ratio_area = new ref<Double>(0.0);
        ref<Double> bounding_rectangles_area = new ref<Double>(0.0);
        MAARPacking P = new MAARPacking();
        List<Rectangle> R = new ArrayList<Rectangle>();

        if (stepsForRotatingComponents() == 0) //no rotation
        {
            calculate_bounding_rectangles_of_components(R, G_sub, A_sub);
        }
        else
        {
            rotate_components_and_calculate_bounding_rectangles(R, G_sub, A_sub);
        }

        P.pack_rectangles_using_Best_Fit_strategy(R, pageRatio(), presortCCs(),
                tipOverCCs(), aspect_ratio_area, bounding_rectangles_area);
        export_node_positions(A, R, G_sub, A_sub);
    }

    void calculate_bounding_rectangles_of_components(
            List<Rectangle> R,
            List<Graph> G_sub,
            List<NodeArray<NodeAttributes>> A_sub)
    {
        int i;
        Rectangle r;
        R.clear();

        for (i = 0; i < number_of_components; i++)
        {
            r = calculate_bounding_rectangle(G_sub.get(i), A_sub.get(i), i);
            R.add(r);
        }
    }

    Rectangle calculate_bounding_rectangle(
            Graph G,
            NodeArray<NodeAttributes> A,
            int componenet_index)
    {
        Rectangle r = new Rectangle();
        node v;
        double x_min = Double.MAX_VALUE,
                x_max = Double.MIN_VALUE,
                y_min = Double.MAX_VALUE,
                y_max = Double.MIN_VALUE,
                act_x_min, act_x_max, act_y_min, act_y_max;
        double max_boundary;//the maximum of half of the width and half of the height of
        //each node; (needed to be able to tipp rectangles over without
        //having access to the height and width of each node)

        for (Iterator<node> iter = G.nodesIterator(); iter.hasNext();)
        {
            v = iter.next();
            max_boundary = Math.max(A.get(v).get_width() / 2, A.get(v).get_height() / 2);
            if (v == G.firstNode())
            {
                x_min = A.get(v).get_position().getX() - max_boundary;
                x_max = A.get(v).get_position().getX() + max_boundary;
                y_min = A.get(v).get_position().getY() - max_boundary;
                y_max = A.get(v).get_position().getY() + max_boundary;
            }
            else
            {
                act_x_min = A.get(v).get_position().getX() - max_boundary;
                act_x_max = A.get(v).get_position().getX() + max_boundary;
                act_y_min = A.get(v).get_position().getY() - max_boundary;
                act_y_max = A.get(v).get_position().getY() + max_boundary;
                if (act_x_min < x_min)
                {
                    x_min = act_x_min;
                }
                if (act_x_max > x_max)
                {
                    x_max = act_x_max;
                }
                if (act_y_min < y_min)
                {
                    y_min = act_y_min;
                }
                if (act_y_max > y_max)
                {
                    y_max = act_y_max;
                }
            }
        }

        //add offset
        x_min -= minDistCC() / 2;
        x_max += minDistCC() / 2;
        y_min -= minDistCC() / 2;
        y_max += minDistCC() / 2;

        r.set_rectangle(x_max - x_min, y_max - y_min, x_min, y_min, componenet_index);
        return r;
    }

    DPoint get_barycenter_position_of_component(Graph G, NodeArray<NodeAttributes> A)
    {
        DPoint sum = PointFactory.INSTANCE.newDPoint();
        for (Iterator<node> iter = G.nodesIterator(); iter.hasNext();)
        {
            node v = iter.next();
            sum = sum.plus(A.get(v).get_position());
        }

        return sum.scaled(1.0 / G.numberOfNodes());
    }

    void rotate_components_and_calculate_bounding_rectangles(
            List<Rectangle> R,
            List<Graph> G_sub,
            List<NodeArray<NodeAttributes>> A_sub)
    {
        int i, j;
        double sin_j, cos_j;
        double angle, act_area, act_area_PI_half_rotated = 0.0, best_area;
        double ratio, new_width, new_height;
        List<NodeArray<DPoint>> best_coords = new ArrayList<NodeArray<DPoint>>(number_of_components);
        List<NodeArray<DPoint>> old_coords = new ArrayList<NodeArray<DPoint>>(number_of_components);
        node v_sub;
        Rectangle r_act, r_best;
        DPoint new_pos = PointFactory.INSTANCE.newDPoint(), new_dlc = PointFactory.INSTANCE.newDPoint();

        R.clear(); //make R empty

        for (i = 0; i < number_of_components; i++)
        {//allcomponents

            //init r_best, best_area and best_(old)coords
            r_best = calculate_bounding_rectangle(G_sub.get(i), A_sub.get(i), i);
            best_area = calculate_area(r_best.get_width(), r_best.get_height(),
                    number_of_components);
            best_coords.add(i, new NodeArray<DPoint>(G_sub.get(i), Factory.DPOINT));
            old_coords.add(i, new NodeArray<DPoint>(G_sub.get(i), Factory.DPOINT));

            for (Iterator<node> iter = G_sub.get(i).nodesIterator(); iter.hasNext();)
            {
                v_sub = iter.next();
                DPoint p = A_sub.get(i).get(v_sub).get_position();
                old_coords.get(i).set(v_sub, PointFactory.INSTANCE.newDPoint(p));
                best_coords.get(i).set(v_sub, PointFactory.INSTANCE.newDPoint(p));
            }

            //rotate the components
            for (j = 1; j <= stepsForRotatingComponents(); j++)
            {
                //calculate new positions for the nodes, the new rectangle and area
                angle = (Math.PI * 0.5) * (double) j / (double) (stepsForRotatingComponents() + 1);
                sin_j = Math.sin(angle);
                cos_j = Math.cos(angle);
                for (Iterator<node> iter = G_sub.get(i).nodesIterator(); iter.hasNext();)
                {
                    v_sub = iter.next();
                    new_pos.setX(cos_j * old_coords.get(i).get(v_sub).getX() -
                            sin_j * old_coords.get(i).get(v_sub).getY());
                    new_pos.setY(sin_j * old_coords.get(i).get(v_sub).getX() +
                            cos_j * old_coords.get(i).get(v_sub).getY());
                    new_pos.setZ(old_coords.get(i).get(v_sub).getZ());
                    A_sub.get(i).get(v_sub).set_position(new_pos);
                }

                r_act = calculate_bounding_rectangle(G_sub.get(i), A_sub.get(i), i);
                act_area = calculate_area(r_act.get_width(), r_act.get_height(),
                        number_of_components);
                if (number_of_components == 1)
                {
                    act_area_PI_half_rotated = calculate_area(r_act.get_height(),
                            r_act.get_width(),
                            number_of_components);
                }

                //store placement of the nodes with minimal area (in case that
                //number_of_components >1) else store placement with minimal aspect ratio area
                if (act_area < best_area)
                {
                    r_best = r_act;
                    best_area = act_area;
                    for (Iterator<node> iter = G_sub.get(i).nodesIterator(); iter.hasNext();)
                    {
                        v_sub = iter.next();

                        best_coords.get(i).set(v_sub, PointFactory.INSTANCE.newDPoint(A_sub.get(i).get(v_sub).get_position()));
                    }
                }
                else if ((number_of_components == 1) && (act_area_PI_half_rotated < best_area))
                { //test if rotating further with PI_half would be an improvement
                    r_best = r_act;
                    best_area = act_area_PI_half_rotated;
                    for (Iterator<node> iter = G_sub.get(i).nodesIterator(); iter.hasNext();)
                    {
                        v_sub = iter.next();

                        best_coords.get(i).set(v_sub, PointFactory.INSTANCE.newDPoint(A_sub.get(i).get(v_sub).get_position()));
                    }
                    //the needed rotation step follows in the next if statement
                }
            }

            //tipp the smallest rectangle over by angle PI/2 around the origin if it makes the
            //aspect_ratio of r_best more similar to the desired aspect_ratio
            ratio = r_best.get_width() / r_best.get_height();

            if ((pageRatio() < 1 && ratio > 1) || (pageRatio() >= 1 && ratio < 1))
            {
                for (Iterator<node> iter = G_sub.get(i).nodesIterator(); iter.hasNext();)
                {
                    v_sub = iter.next();
                    new_pos = best_coords.get(i).get(v_sub).rotate90InZPlane();
                    best_coords.get(i).set(v_sub, new_pos);
                }

                //calculate new rectangle
                new_dlc.setX(r_best.get_old_dlc_position().getY() * (-1) - r_best.get_height());
                new_dlc.setY(r_best.get_old_dlc_position().getX());
                new_dlc.setZ(r_best.get_old_dlc_position().getZ());
                new_width = r_best.get_height();
                new_height = r_best.get_width();
                r_best.set_width(new_width);
                r_best.set_height(new_height);
                r_best.set_old_dlc_position(new_dlc);
            }

            // Move the component back into the tiled layout plane (on average)
            DPoint bary_center = get_barycenter_position_of_component(G_sub.get(i), A_sub.get(i));
            for (Iterator<node> iter = G_sub.get(i).nodesIterator(); iter.hasNext();)
            {
                v_sub = iter.next();
                DPoint p = best_coords.get(i).get(v_sub);
                best_coords.get(i).get(v_sub).setZ(p.getZ() - bary_center.getZ());
            }

            //save the computed information in A_sub and R
            for (Iterator<node> iter = G_sub.get(i).nodesIterator(); iter.hasNext();)
            {
                v_sub = iter.next();
                A_sub.get(i).get(v_sub).set_position(best_coords.get(i).get(v_sub));
            }
            R.add(r_best);

        }//allcomponents
    }

    double calculate_area(double width, double height, int comp_nr)
    {
        if (comp_nr == 1)  //calculate aspect ratio area of the rectangle
        {
            double ratio = width / height;

            if (ratio < pageRatio()) //scale width
            {
                return (width * height * (pageRatio() / ratio));
            }
            else //scale height
            {
                return (width * height * (ratio / pageRatio()));
            }
        }
        else  //calculate area of the rectangle
        {
            return width * height;
        }
    }

    void export_node_positions(
            NodeArray<NodeAttributes> A,
            List<Rectangle> R,
            List<Graph> G_sub,
            List<NodeArray<NodeAttributes>> A_sub)
    {
        ListIterator<Rectangle> RectIterator;
        int i;
        node v_sub;
        DPoint newpos, tipped_pos, tipped_dlc;

        for (Rectangle r : R)
        {//for
            i = r.get_component_index();
            if (r.is_tipped_over())
            {//if
                //calculate tipped coordinates of the nodes
                for (Iterator<node> iter = G_sub.get(i).nodesIterator(); iter.hasNext();)
                {
                    v_sub = iter.next();
                    tipped_pos = A_sub.get(i).get(v_sub).get_position().rotate90InZPlane();
                    A_sub.get(i).get(v_sub).set_position(tipped_pos);
                }
            }//if

            for (Iterator<node> iter = G_sub.get(i).nodesIterator(); iter.hasNext();)
            {
                v_sub = iter.next();
                newpos = A_sub.get(i).get(v_sub).get_position().plus(r.get_new_dlc_position()).minus(
                        r.get_old_dlc_position());
                A.get(A_sub.get(i).get(v_sub).get_original_node()).set_position(newpos);
            }
        }//for
    }

//----------------------- functions for multilevel step -----------------------------
    int get_max_mult_iter(int act_level, int max_level, int node_nr)
    {
        int iter;
        if (maxIterChange() == MaxIterChange.micConstant) //nothing to do
        {
            iter = fixedIterations();
        }
        else if (maxIterChange() == MaxIterChange.micLinearlyDecreasing) //linearly decreasing values
        {
            if (max_level == 0)
            {
                iter = fixedIterations() + ((maxIterFactor() - 1) * fixedIterations());
            }
            else
            {
                iter = fixedIterations() + (int) ((double) act_level / (double) max_level *
                        (((maxIterFactor() - 1)) * fixedIterations()));
            }
        }
        else //maxIterChange == micRapidlyDecreasing (rapidly decreasing values)
        {
            if (act_level == max_level)
            {
                iter = fixedIterations() + (int) ((maxIterFactor() - 1) * fixedIterations());
            }
            else if (act_level == max_level - 1)
            {
                iter = fixedIterations() + (int) (0.5 * (maxIterFactor() - 1) * fixedIterations());
            }
            else if (act_level == max_level - 2)
            {
                iter = fixedIterations() + (int) (0.25 * (maxIterFactor() - 1) * fixedIterations());
            }
            else //act_level >= max_level - 3
            {
                iter = fixedIterations();
            }
        }

        return iter;
    }

//-------------------------- functions for force calculation ---------------------------
    void calculate_forces(
            Graph G,
            NodeArray<NodeAttributes> A,
            EdgeArray<EdgeAttributes> E,
            NodeArray<DPoint> F,
            NodeArray<DPoint> F_attr,
            NodeArray<DPoint> F_rep,
            NodeArray<DPoint> last_node_movement,
            int iter,
            int fine_tuning_step)
    {
        calculate_attractive_forces(G, A, E, F_attr);
        calculate_repulsive_forces(G, A, F_rep);
        add_attr_rep_forces(G, F_attr, F_rep, F, iter, fine_tuning_step);
        prevent_oscilations(G, F, last_node_movement, iter);
        move_nodes(G, A, F);
        update_boxlength_and_cornercoordinate(G, A);
    }

    void init_boxlength_and_cornercoordinate(
            Graph G,
            NodeArray<NodeAttributes> A)
    {
        //boxlength is set

        double MIN_NODE_SIZE = 10;
        double BOX_SCALING_FACTOR = 1.1;
        double w = 0, h = 0;       //helping variables

        for (Iterator<node> iter = G.nodesIterator(); iter.hasNext();)
        {
            node v = iter.next();
            w += Math.max(A.get(v).get_width(), MIN_NODE_SIZE);
            h += Math.max(A.get(v).get_height(), MIN_NODE_SIZE);
        }

        boxlength = Math.ceil(Math.max(w, h) * BOX_SCALING_FACTOR);

        //down left corner of comp. box is the origin
        down_left_corner = PointFactory.INSTANCE.newDPoint();
    }

    void create_initial_placement(Graph G, NodeArray<NodeAttributes> A)
    {
        int i, j, k;
        node v;

        if (initialPlacementForces() == InitialPlacementForces.ipfKeepPositions) // don't change anything
        {
            init_boxlength_and_cornercoordinate(G, A);
        }
        else if (initialPlacementForces() == InitialPlacementForces.ipfUniformGrid) //set nodes to the midpoints of a  grid
        {//(uniform on a grid)
            init_boxlength_and_cornercoordinate(G, A);
            int level = (int) (Math.ceil(Math.log(G.numberOfNodes()) / Math.log(4.0)));
            int m = (int) (Math.pow(2.0, level)) - 1;
            boolean finished = false;
            double blall = boxlength / (m + 1); //boxlength for boxes at the lowest level (depth)
            List<node> all_nodes = new ArrayList<node>(G.numberOfNodes());

            for (Iterator<node> iter = G.nodesIterator(); iter.hasNext();)
            {
                v = iter.next();
                all_nodes.add(v);
            }
            v = all_nodes.get(0);
            k = 0;
            i = 0;
            while ((!finished) && (i <= m))
            {//while1
                j = 0;
                while ((!finished) && (j <= m))
                {//while2
                    A.get(v).get_position().setX(boxlength * i / (m + 1) + blall / 2);
                    A.get(v).get_position().setY(boxlength * j / (m + 1) + blall / 2);
                    if (k == G.numberOfNodes() - 1)
                    {
                        finished = true;
                    }
                    else
                    {
                        k++;
                        v = all_nodes.get(k);
                    }
                    j++;
                }//while2
                i++;
            }//while1
        }//(uniform on a grid)
        else //randomised distribution of the nodes;
        {//(random)
            init_boxlength_and_cornercoordinate(G, A);
            if (initialPlacementForces() == InitialPlacementForces.ipfRandomTime)//(RANDOM based on actual CPU-time)
            {
                random.setSeed(System.currentTimeMillis() / 1000);
            }
            else if (initialPlacementForces() == InitialPlacementForces.ipfRandomRandIterNr)//(RANDOM based on seed)
            {
                random.setSeed(randSeed());
            }

            for (Iterator<node> iter = G.nodesIterator(); iter.hasNext();)
            {
                v = iter.next();
                DPoint rndp = PointFactory.INSTANCE.newDPoint();
                rndp.setX(random.nextDouble() * (boxlength - 2) + 1);
                rndp.setY(random.nextDouble() * (boxlength - 2) + 1);
                rndp.setZ(random.nextDouble() * (boxlength - 2) + 1);
                A.get(v).set_position(rndp);
            }
        }//(random)
        update_boxlength_and_cornercoordinate(G, A);
    }

    void init_F(Graph G, NodeArray<DPoint> F)
    {
        for (Iterator<node> iter = G.nodesIterator(); iter.hasNext();)
        {
            node v = iter.next();
            F.set(v, PointFactory.INSTANCE.newDPoint());
        }
    }

    void make_initialisations_for_rep_calc_classes(Graph G)
    {
        if (repulsiveForcesCalculation() == RepulsiveForcesMethod.rfcExact)
        {
            FR.make_initialisations(boxlength, down_left_corner, frGridQuotient());
        }
        else if (repulsiveForcesCalculation() == RepulsiveForcesMethod.rfcGridApproximation)
        {
            FR.make_initialisations(boxlength, down_left_corner, frGridQuotient());
        }
        else //(repulsiveForcesCalculation() == RepulsiveForcesCalculation.rfcNMM
        {
            /*NM.make_initialisations(G, boxlength, down_left_corner,
                    nmParticlesInLeaves(), nmPrecision(),
                    nmTreeConstruction(), nmSmallCell());*/
        }
    }

    //! Calculates repulsive forces for each node.
    void calculate_repulsive_forces(
            Graph G,
            NodeArray<NodeAttributes> A,
            NodeArray<DPoint> F_rep)
    {
        final int EXACT_FORCES_THRESHOLD = 50;

        if (G.numberOfNodes() < EXACT_FORCES_THRESHOLD || repulsiveForcesCalculation() == RepulsiveForcesMethod.rfcExact)
        {
            FR.calculate_exact_repulsive_forces(G, A, F_rep);
        }
        else if (repulsiveForcesCalculation() == RepulsiveForcesMethod.rfcGridApproximation)
        {
            FR.calculate_approx_repulsive_forces(G, A, F_rep);
        }
        else //repulsiveForcesCalculation() == rfcNMM
        {
            //NM.calculate_repulsive_forces(G, A, F_rep);
        }
    }

    void calculate_attractive_forces(
            Graph G,
            NodeArray<NodeAttributes> A,
            EdgeArray<EdgeAttributes> E,
            NodeArray<DPoint> F_attr)
    {
        edge e;
        node u, v;
        double norm_v_minus_u, scalar;
        DPoint vector_v_minus_u, f_u = PointFactory.INSTANCE.newDPoint();
        DPoint nullpoint = PointFactory.INSTANCE.newDPoint();

        //initialisation
        init_F(G, F_attr);

        //calculation
        for (Iterator<edge> i = G.edgesIterator(); i.hasNext();)
        {//for
            e = i.next();

            u = e.source();
            v = e.target();
            vector_v_minus_u = A.get(v).get_position().minus(A.get(u).get_position());
            norm_v_minus_u = vector_v_minus_u.norm();
            if (vector_v_minus_u.equals(nullpoint))
            {
                f_u = nullpoint;
            }
            else if (!numexcept.f_near_machine_precision(norm_v_minus_u, f_u))
            {
                scalar = f_attr_scalar(norm_v_minus_u, E.get(e).get_length()) / norm_v_minus_u;
                f_u = vector_v_minus_u.scaled(scalar);
            }

            F_attr.set(v, F_attr.get(v).minus(f_u));
            F_attr.set(u, F_attr.get(u).plus(f_u));
        }//for
    }

    double f_attr_scalar(double d, double ind_ideal_edge_length)
    {
        double s = 0.0;

        if (forceModel() == ForceModel.fmFruchtermanReingold)
        {
            s = d * d / (ind_ideal_edge_length * ind_ideal_edge_length * ind_ideal_edge_length);
        }
        else if (forceModel() == ForceModel.fmEades)
        {
            double c = 10;
            if (d == 0)
            {
                s = -1e10;
            }
            else
            {
                s = c * (Math.log(d / ind_ideal_edge_length) / Math.log(2)) / (ind_ideal_edge_length);
            }
        }
        else if (forceModel() == ForceModel.fmNew)
        {
            double c = Math.log(d / ind_ideal_edge_length) / Math.log(2);
            if (d > 0)
            {
                s = c * d * d /
                        (ind_ideal_edge_length * ind_ideal_edge_length * ind_ideal_edge_length);
            }
            else
            {
                s = -1e10;
            }
        }
        else if (DEBUG_BUILD)
        {
            println(" Error  f_attr_scalar");
        }

        return s;
    }

    void add_attr_rep_forces(
            Graph G,
            NodeArray<DPoint> F_attr,
            NodeArray<DPoint> F_rep,
            NodeArray<DPoint> F,
            int iter,
            int fine_tuning_step)
    {
        node v;
        DPoint f, force = PointFactory.INSTANCE.newDPoint();
        DPoint nullpoint = PointFactory.INSTANCE.newDPoint();
        double norm_f, scalar;
        double act_spring_strength, act_rep_force_strength;

        //set cool_factor
        if (coolTemperature() == false)
        {
            cool_factor = 1.0;
        }
        else if ((coolTemperature() == true) && (fine_tuning_step == 0))
        {
            if (iter == 1)
            {
                cool_factor = coolValue();
            }
            else
            {
                cool_factor *= coolValue();
            }
        }

        if (fine_tuning_step == 1)
        {
            cool_factor /= 10.0; //decrease the temperature rapidly
        }
        else if (fine_tuning_step == 2)
        {
            if (iter <= fineTuningIterations() - 5)
            {
                cool_factor = fineTuneScalar(); //decrease the temperature rapidly
            }
            else
            {
                cool_factor = (fineTuneScalar() / 10.0);
            }
        }

        //set the values for the spring strength and strength of the rep. force field
        if (fine_tuning_step <= 1)//usual case
        {
            act_spring_strength = springStrength();
            act_rep_force_strength = repForcesStrength();
        }
        else if (!adjustPostRepStrengthDynamically())
        {
            act_spring_strength = postSpringStrength();
            act_rep_force_strength = postStrengthOfRepForces();
        }
        else //adjustPostRepStrengthDynamically())
        {
            act_spring_strength = postSpringStrength();
            act_rep_force_strength = get_post_rep_force_strength(G.numberOfNodes());
        }

        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            v = i.next();
            f = F_attr.get(v).scaled(act_spring_strength).plus(F_rep.get(v).scaled(act_rep_force_strength));
            f = f.scaled(average_ideal_edgelength * average_ideal_edgelength);

            norm_f = f.norm();
            if (f == nullpoint)
            {
                force = nullpoint;
            }
            else if (numexcept.f_near_machine_precision(norm_f, force))
            {
                restrict_force_to_comp_box(force);
            }
            else
            {
                scalar = Math.min(norm_f * cool_factor * forceScalingFactor(),
                        max_radius(iter)) / norm_f;
                force = f.scaled(scalar);
            }
            F.set(v, PointFactory.INSTANCE.newDPoint(force));
        }
    }

    void move_nodes(
            Graph G,
            NodeArray<NodeAttributes> A,
            NodeArray<DPoint> F)
    {
        node v;

        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            v = i.next();
            A.get(v).set_position(A.get(v).get_position().plus(F.get(v)));
        }
    }

    void update_boxlength_and_cornercoordinate(
            Graph G,
            NodeArray<NodeAttributes> A)
    {
        node v;
        double xmin, xmax, ymin, ymax, zmin, zmax;
        DPoint midpoint;

        v = G.firstNode();
        midpoint = A.get(v).get_position();
        xmin = xmax = midpoint.getX();
        ymin = ymax = midpoint.getY();
        zmin = zmax = midpoint.getZ();

        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            v = i.next();
            midpoint = A.get(v).get_position();
            if (midpoint.getX() < xmin)
            {
                xmin = midpoint.getX();
            }
            if (midpoint.getX() > xmax)
            {
                xmax = midpoint.getX();
            }
            if (midpoint.getY() < ymin)
            {
                ymin = midpoint.getY();
            }
            if (midpoint.getY() > ymax)
            {
                ymax = midpoint.getY();
            }
            if (midpoint.getZ() < zmin)
            {
                zmin = midpoint.getZ();
            }
            if (midpoint.getZ() > zmax)
            {
                zmax = midpoint.getZ();
            }
        }

        //set down_left_corner and boxlength

        down_left_corner.setX(Math.floor(xmin - 1));
        down_left_corner.setY(Math.floor(ymin - 1));
        down_left_corner.setZ(Math.floor(zmin - 1));
        boxlength = Math.ceil(Math.max(Math.max(zmax - zmin, ymax - ymin), xmax - xmin) * 1.01 + 2);

        //exception handling: all nodes have same x and y coordinate
        if (boxlength <= 2)
        {
            boxlength = G.numberOfNodes() * 20;
            down_left_corner.setX(Math.floor(xmin) - (boxlength / 2));
            down_left_corner.setY(Math.floor(ymin) - (boxlength / 2));
            down_left_corner.setZ(Math.floor(zmin) - (boxlength / 2));
        }

        //export the boxlength and down_left_corner values to the rep. calc. classes

        if (repulsiveForcesCalculation() == RepulsiveForcesMethod.rfcExact ||
                repulsiveForcesCalculation() == RepulsiveForcesMethod.rfcGridApproximation)
        {
            FR.update_boxlength_and_cornercoordinate(boxlength, down_left_corner);
        }
        else //repulsiveForcesCalculation() == rfcNMM
        {
            //NM.update_boxlength_and_cornercoordinate(boxlength, down_left_corner);
        }
    }

    //! Describes the max. radius of a move in one time step, depending on the number of iterations.
    double max_radius(int iter)
    {
        return (iter == 1) ? boxlength / 1000 : boxlength / 5;
    }

    void set_average_ideal_edgelength(
            Graph G,
            EdgeArray<EdgeAttributes> E)
    {
        double averagelength = 0;
        edge e;

        if (G.numberOfEdges() > 0)
        {
            for (Iterator<edge> i = G.edgesIterator(); i.hasNext();)
            {
                e = i.next();
                averagelength += E.get(e).get_length();
            }
            average_ideal_edgelength = averagelength / G.numberOfEdges();
        }
        else
        {
            average_ideal_edgelength = 50;
        }
    }

    double get_average_forcevector_length(Graph G, NodeArray<DPoint> F)
    {
        double lengthsum = 0;
        node v;
        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            v = i.next();
            lengthsum += F.get(v).norm();
        }
        lengthsum /= G.numberOfNodes();
        return lengthsum;
    }

    void prevent_oscilations(
            Graph G,
            NodeArray<DPoint> F,
            NodeArray<DPoint> last_node_movement,
            int iter)
    {

        double pi_times_1_over_6 = 0.52359878;
        double pi_times_2_over_6 = 2 * pi_times_1_over_6;
        double pi_times_3_over_6 = 3 * pi_times_1_over_6;
        double pi_times_4_over_6 = 4 * pi_times_1_over_6;
        double pi_times_5_over_6 = 5 * pi_times_1_over_6;
        double pi_times_7_over_6 = 7 * pi_times_1_over_6;
        double pi_times_8_over_6 = 8 * pi_times_1_over_6;
        double pi_times_9_over_6 = 9 * pi_times_1_over_6;
        double pi_times_10_over_6 = 10 * pi_times_1_over_6;
        double pi_times_11_over_6 = 11 * pi_times_1_over_6;

        DPoint nullpoint = PointFactory.INSTANCE.newDPoint();
        double fi; //angle in [0,2pi) measured counterclockwise
        double norm_old, norm_new, quot_old_new;

        if (iter > 1) //usual case
        {//if1
            node v;
            for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
            {
                v = i.next();
                DPoint force_new = PointFactory.INSTANCE.newDPoint(F.get(v));
                DPoint force_old = PointFactory.INSTANCE.newDPoint(last_node_movement.get(v));
                norm_new = F.get(v).norm();
                norm_old = last_node_movement.get(v).norm();
                if ((norm_new > 0) && (norm_old > 0))
                {//if2
                    quot_old_new = norm_old / norm_new;

                    //prevent oszilations
                    fi = force_old.angle(force_new);
                    if (((fi <= pi_times_1_over_6) || (fi >= pi_times_11_over_6)) &&
                            ((norm_new > (norm_old * 2.0))))
                    {
                        F.set(v, F.get(v).scaled(quot_old_new * 2.0));
                    }
                    else if ((fi >= pi_times_1_over_6) && (fi <= pi_times_2_over_6) &&
                            (norm_new > (norm_old * 1.5)))
                    {
                        F.set(v, F.get(v).scaled(quot_old_new * 1.5));
                    }
                    else if ((fi >= pi_times_2_over_6) && (fi <= pi_times_3_over_6) &&
                            (norm_new > (norm_old)))
                    {
                        F.set(v, F.get(v).scaled(quot_old_new));
                    }
                    else if ((fi >= pi_times_3_over_6) && (fi <= pi_times_4_over_6) &&
                            (norm_new > (norm_old * 0.66666666)))
                    {
                        F.set(v, F.get(v).scaled(quot_old_new * 0.66666666));
                    }
                    else if ((fi >= pi_times_4_over_6) && (fi <= pi_times_5_over_6) &&
                            (norm_new > (norm_old * 0.5)))
                    {
                        F.set(v, F.get(v).scaled(quot_old_new * 0.5));
                    }
                    else if ((fi >= pi_times_5_over_6) && (fi <= pi_times_7_over_6) &&
                            (norm_new > (norm_old * 0.33333333)))
                    {
                        F.set(v, F.get(v).scaled(quot_old_new * 0.33333333));
                    }
                    else if ((fi >= pi_times_7_over_6) && (fi <= pi_times_8_over_6) &&
                            (norm_new > (norm_old * 0.5)))
                    {
                        F.set(v, F.get(v).scaled(quot_old_new * 0.5));
                    }
                    else if ((fi >= pi_times_8_over_6) && (fi <= pi_times_9_over_6) &&
                            (norm_new > (norm_old * 0.66666666)))
                    {
                        F.set(v, F.get(v).scaled(quot_old_new * 0.66666666));
                    }
                    else if ((fi >= pi_times_9_over_6) && (fi <= pi_times_10_over_6) &&
                            (norm_new > (norm_old)))
                    {
                        F.set(v, F.get(v).scaled(quot_old_new));
                    }
                    else if ((fi >= pi_times_10_over_6) && (fi <= pi_times_11_over_6) &&
                            (norm_new > (norm_old * 1.5)))
                    {
                        F.set(v, F.get(v).scaled(quot_old_new * 1.5));
                    }
                }//if2
                last_node_movement.set(v, F.get(v));
            }
        }//if1
        else if (iter == 1)
        {
            init_last_node_movement(G, F, last_node_movement);
        }
    }

    void init_last_node_movement(
            Graph G,
            NodeArray<DPoint> F,
            NodeArray<DPoint> last_node_movement)
    {
        node v;
        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            v = i.next();
            last_node_movement.set(v, PointFactory.INSTANCE.newDPoint(F.get(v)));
        }
    }

    void adapt_drawing_to_ideal_average_edgelength(
            Graph G,
            NodeArray<NodeAttributes> A,
            EdgeArray<EdgeAttributes> E)
    {
        edge e;
        node v;
        double sum_real_edgelength = 0;
        double sum_ideal_edgelength = 0;
        double area_scaling_factor;
        DPoint new_pos;

        for (Iterator<edge> i = G.edgesIterator(); i.hasNext();)
        {
            e = i.next();
            sum_ideal_edgelength += E.get(e).get_length();
            sum_real_edgelength += (A.get(e.source()).get_position().minus(A.get(e.target()).get_position())).norm();
        }

        if (sum_real_edgelength == 0) //very very unlike case
        {
            area_scaling_factor = 1;
        }
        else
        {
            area_scaling_factor = sum_ideal_edgelength / sum_real_edgelength;
        }

        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            v = i.next();
            new_pos = A.get(v).get_position().scaled(resizingScalar() * area_scaling_factor);
            A.get(v).set_position(new_pos);
        }
    }

    void restrict_force_to_comp_box(DPoint force)
    {
        double x_min = down_left_corner.getX();
        double x_max = down_left_corner.getX() + boxlength;
        double y_min = down_left_corner.getY();
        double y_max = down_left_corner.getY() + boxlength;
        double z_min = down_left_corner.getZ();
        double z_max = down_left_corner.getZ() + boxlength;

        if (force.getX() < x_min)
        {
            force.setX(x_min);
        }
        else if (force.getX() > x_max)
        {
            force.setX(x_max);
        }

        if (force.getY() < y_min)
        {
            force.setY(y_min);
        }
        else if (force.getY() > y_max)
        {
            force.setY(y_max);
        }

        if (force.getZ() < z_min)
        {
            force.setZ(z_min);
        }
        else if (force.getZ() > z_max)
        {
            force.setZ(z_max);
        }
    }
}
