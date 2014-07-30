package org.BioLayoutExpress3D.Graph.Camera;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import org.BioLayoutExpress3D.Utils.*;
import static java.lang.Math.*;
import static javax.media.opengl.GL2.*;
import static org.BioLayoutExpress3D.Graph.Camera.GraphCameraEyeTypes.*;
import static org.BioLayoutExpress3D.Graph.Camera.GraphCameraFinalVariables.*;

/**
* The GraphCameraEye class creates an asymmetric frustum, with the camera offset
* to the left or right depending on if the instance is for the left or right eye.
* The viewport for the camera is also stored here.
*
*
* @author Thanos Theo, Andrew Davison, 2007-2011
* @version 3.0.0.0
*
*/

public class GraphCameraEye
{

    /**
    *  CameraEyes info.
    */
    private GraphCameraEyeTypes eyeType = GraphCameraEyeTypes.CENTER_VIEW;

    /**
    *  Viewport dimensions.
    */
    private int viewPortX = 0, viewPortY = 0, viewPortWidth = 0, viewPortHeight = 0;

    /**
    *  Frustum info.
    */
    private double top = 0.0, bottom = 0.0, left = 0.0, right = 0.0;

    /**
    *  Intra Ocular Distance (eye separation distance) variable.
    */
    private double intraOcularDistance = 0.0;

    /**
    *  Frustum shift info variable.
    */
    private double frustumShift = 0.0;

    /**
    *  The first GraphCameraEye constructor.
    */
    public GraphCameraEye(GraphCameraEyeTypes eyeType)
    {
        this(eyeType, DEFAULT_INTRA_OCULAR_DISTANCE);
    }

    /**
    *  The second GraphCameraEye constructor.
    */
    public GraphCameraEye(GraphCameraEyeTypes eyeType, double intraOcularDistance)
    {
        this.eyeType = eyeType;

        setIntraOcularDistanceAndFrustumShift(intraOcularDistance);
        top = NEAR_DISTANCE * tan(FOV_Y * PI / 360.0);
        bottom = -top;
    }

    public double getTop() { return top; }
    public double getBottom() { return bottom; }
    public double getLeft() { return left; }
    public double getRight() { return right; }

    /**
    *  Sets the IntraOcularDistance & frustum shift variables.
    */
    public final void setIntraOcularDistanceAndFrustumShift(double intraOcularDistance)
    {
        this.intraOcularDistance = intraOcularDistance;

        if ( !eyeType.equals(CENTER_VIEW) )
            frustumShift = (intraOcularDistance / 2.0) * NEAR_DISTANCE / SCREEN_DISTANCE;
    }

    /**
    *  Updates the frustum dimensions.
    */
    public final void updateFrustumDimensions(int width, int height)
    {
        // update viewport coordinates
        viewPortWidth  = (width == 0) ?  1 : width;
        viewPortHeight = (height == 0) ? 1 : height;

        // calculate left and right near clipping plane dimensions
        double aspectRatio = (viewPortWidth <= viewPortHeight) ? ( (double)viewPortHeight / (double)viewPortWidth ) : ( (double)viewPortWidth / (double)viewPortHeight );
        left = bottom * aspectRatio;
        right = top   * aspectRatio;

        // modify left and right to specify an asymmetric frustum, else leave unchanged
        if ( eyeType.equals(LEFT_EYE) ) // left eye
        {
            // shift left eye frustum to the left
            left  += frustumShift;
            right += frustumShift;
        }
        else if ( eyeType.equals(RIGHT_EYE) ) // right eye
        {
            // shift right eye frustum to the right
            left  -= frustumShift;
            right -= frustumShift;
        }
        /*
        else if ( eyeType.equals(CENTER_VIEW) ) // center-of-view
        {
            // leave unchanged
        }
        */
    }

    /**
    *  Updates the viewport and frustum dimensions.
    */
    public final void updateViewPortAndFrustumDimensions(GL2 gl, int x, int y, int width, int height)
    {
        // update viewport coordinates
        viewPortX = x;
        viewPortY = y;
        viewPortWidth  = (width == 0) ?  1 : width;
        viewPortHeight = (height == 0) ? 1 : height;

        gl.glViewport(viewPortX, viewPortY, viewPortWidth, viewPortHeight); // update the viewport

        // calculate left and right near clipping plane dimensions
        double aspectRatio = (viewPortWidth <= viewPortHeight) ? ( (double)viewPortHeight / (double)viewPortWidth ) : ( (double)viewPortWidth / (double)viewPortHeight );
        left = bottom * aspectRatio;
        right = top   * aspectRatio;

        // modify left and right to specify an asymmetric frustum, else leave unchanged
        if ( eyeType.equals(LEFT_EYE) ) // left eye
        {
            // shift left eye frustum to the left
            left  += frustumShift;
            right += frustumShift;
        }
        else if ( eyeType.equals(RIGHT_EYE) ) // right eye
        {
            // shift right eye frustum to the right
            left  -= frustumShift;
            right -= frustumShift;
        }
        /*
        else if ( eyeType.equals(CENTER_VIEW) ) // center-of-view
        {
            // leave unchanged
        }
        */
    }

    /**
    *  Sets the perspective projection frustum for this camera.
    */
    public final void setProjection(GL2 gl, double left, double right, double bottom, double top)
    {
        gl.glMatrixMode(GL_PROJECTION); // set the view attributes
        gl.glLoadIdentity();

        // specifies a(n) (asymmetric if left-right eye used) frustum view volume
        gl.glFrustum(left, right, bottom, top, NEAR_DISTANCE, FAR_DISTANCE);

        gl.glMatrixMode(GL_MODELVIEW);
        // gl.glLoadIdentity(); // resetted in main renderer
    }

    /**
    *  Sets the perspective projection frustum for this camera.
    */
    public final void setProjection(GL2 gl)
    {
        setProjection(gl, this.left, this.right, this.bottom, this.top);
    }

    /**
    *  Sets the position for this camera.
    */
    public final void setCamera(GL2 gl, float translateDX, float translateDY, float scaleValue, float xRotate, float yRotate, float zRotate, Point3D focusPoint, boolean invertYAxis)
    {
        gl.glTranslated(-translateDX, translateDY, -scaleValue);
        if (invertYAxis) gl.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(xRotate, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(yRotate, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(zRotate, 0.0f, 0.0f, 1.0f);
        if (focusPoint != null) gl.glTranslatef(-focusPoint.x, -focusPoint.y, -focusPoint.z);
    }

    /**
    *  Sets the position for this camera (gluLookAt() case).
    */
    public final void setCamera(GL2 gl, GLU glu, double eyeX, double eyeY, double eyeZ, double xLookAt, double yLookAt, double zLookAt)
    {
        glu.gluLookAt(eyeX, eyeY, eyeZ, xLookAt, yLookAt, zLookAt, 0.0, 1.0, 0.0); // position camera
    }

    /**
    *  Sets the perspective projection frustum for this camera and its position.
    */
    public final void setProjectionAndCamera(GL2 gl, float translateDX, float translateDY, float scaleValue, float xRotate, float yRotate, float zRotate, Point3D focusPoint, boolean invertYAxis)
    {
        gl.glMatrixMode(GL_PROJECTION); // set the view attributes
        gl.glLoadIdentity();

        // specifies a(n) (asymmetric if left-right eye used) frustum view volume
        gl.glFrustum(left, right, bottom, top, NEAR_DISTANCE, FAR_DISTANCE);

        gl.glMatrixMode(GL_MODELVIEW);
        // gl.glLoadIdentity(); // resetted in main renderer

        // adjust for x-axis eye offset: shift left eye to the left & shift right eye to the right
        float eyeX = 0.0f;
        if ( eyeType.equals(LEFT_EYE) ) // left eye
        {
            // shift left eye to the left
            eyeX -= intraOcularDistance / 2.0f;
        }
        else if ( eyeType.equals(RIGHT_EYE) ) // right eye
        {
            // shift right eye to the right
            eyeX += intraOcularDistance / 2.0f;
        }
        /*
        else if ( eyeType.equals(CENTER_VIEW) ) // center-of-view
        {
            // leave unchanged
        }
        */

        gl.glTranslated(-(translateDX + eyeX), translateDY, -scaleValue);
        if (invertYAxis) gl.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(xRotate, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(yRotate, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(zRotate, 0.0f, 0.0f, 1.0f);
        if (focusPoint != null) gl.glTranslatef(-focusPoint.x, -focusPoint.y, -focusPoint.z);
    }

    /**
    *  Sets the perspective projection frustum for this camera and its position (gluLookAt() case).
    */
    public final void setProjectionAndCamera(GL2 gl, GLU glu, double eyeX, double eyeY, double eyeZ, double xLookAt, double yLookAt, double zLookAt)
    {
        gl.glMatrixMode(GL_PROJECTION); // set the view attributes
        gl.glLoadIdentity();

        // specifies a(n) (asymmetric if left-right eye used) frustum view volume
        gl.glFrustum(left, right, bottom, top, NEAR_DISTANCE, FAR_DISTANCE);

        gl.glMatrixMode(GL_MODELVIEW);
        // gl.glLoadIdentity(); // resetted in main renderer

        // adjust for x-axis eye offset: shift left eye to the left & shift right eye to the right
        if ( eyeType.equals(LEFT_EYE) ) // left eye
        {
            // shift left eye to the left
            eyeX -= intraOcularDistance / 2.0;
        }
        else if ( eyeType.equals(RIGHT_EYE) ) // right eye
        {
            // shift right eye to the right
            eyeX += intraOcularDistance / 2.0;
        }
        /*
        else if ( eyeType.equals(CENTER_VIEW) ) // center-of-view
        {
            // leave unchanged
        }
        */

        glu.gluLookAt(eyeX, eyeY, eyeZ, xLookAt, yLookAt, zLookAt, 0.0, 1.0, 0.0); // position camera
    }


}
