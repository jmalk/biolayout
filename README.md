biolayout
=========

MSc project adding 4D visualisation to open source graph layout software - www.biolayout.org

BioLayout Express 3D is a Java implementation of the Fruchterman-Reingold algorithm for graph layout. Nodes in a graph are placed according to similarity.

This project aims to add four dimensional geometry and a user interface allowing rotations in this space.

To compile the code and install I recommend using Maven, available from http://maven.apache.org
Install maven as directed on their website, then run "mvn install" in the directory the source code is saved in. This creates a new directory named "target" within which you will find BioLayout Express 3D development builds.

There have been some problems running the program on Linux-based operating systems, which seem to be solved by specifying which version of OpenGl to use with the following command:

MESA_GL_VERSION_OVERRIDE=2.1 java -jar <name-of-jar-file>

Included in this directory are some example shapes - fourAxes.txt, zwPlane.txt, hypercube.txt, hyperpyramid.txt. To create your own, edit these or use the BioLayout manual for more detailed instructions on the format.

The controls for manipulating the view in BioLayout are as normal with the addition of two new rotations. The conventional 3-D rotations implemented in BioLayout are performed by left-clicking and dragging the mouse. To rotate the x-w and y-w axes, hold down Ctrl while clicking and dragging.


Editing The Source Code

The java code for BioLayout can be found in src/main/java/org/BioLayoutExpress3D

The Graph.Matrix5D and Graph.Vector5D classes were added for this project.
Graph.GraphRenderer3D, Utils.Point3D and Files.Parsers.CoreParser were altered from the original source code.
