package org.BioLayoutExpress3D.Physics;

import java.awt.image.*;
import java.util.*;
import java.util.concurrent.*;
import static java.lang.Math.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.texture.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Textures.DrawTextureSFXs.*;

/**
*
* The ParticlesGenerator class is used for generating various particle effects.
*
* @see org.BioLayoutExpress3D.Physics.Particle
* @author Thanos Theo, Michael Kargas, 2008-2009
* @version 3.0.0.0
*/

public final class ParticlesGenerator
{
    /**
    *  Variable to store how many particles will be used per particle generation.
    */
    private static final int NUMBER_OF_PARTICLES = 3;

    /**
    *  CopyOnWriteArrayList to store multiple particle generations.
    */
    private CopyOnWriteArrayList< ArrayList<Particle> > particleGeneratorEffect = new CopyOnWriteArrayList< ArrayList<Particle> >();

    /**
    *  Texture to be used as a particle image texture.
    */
    private Texture particleImageTexture = null;

    /**
    *  The first constructor of the ParticlesGenerator class.
    */
    public ParticlesGenerator()
    {
        this( org.BioLayoutExpress3D.StaticLibraries.ImageProducer.createOpaqueBufferedImage(5, 5) );
    }

    /**
    *  The second constructor of the ParticlesGenerator class. A BufferedImage is specified for a particle to be rendered.
    */
    public ParticlesGenerator(BufferedImage particleImage)
    {
        particleImageTexture = TextureProducer.createTextureFromBufferedImageAndDeleteOrigContext(particleImage, true);
    }

    /**
    *  Creates a particle generator simulation (1).
    */
    public void particlesGenerator1(int width, int height)
    {
        Vector position = new Vector(width, height);

        particleGeneratorEffect.add( Particle.createParticleExplosion(NUMBER_OF_PARTICLES, position, 1.0, 0.0005, 1.0f, 0.004f) );
    }

    /**
    *  Creates a particle generator simulation (2).
    */
    public void particlesGenerator2(int width, int height)
    {
        Vector position = new Vector();
        position.x = width / 2 + sin(System.currentTimeMillis() / 1500.0) * width / 2;   // 'travel' in X axis with sin()
        position.y = height / 2 + sin(System.currentTimeMillis() / 1000.0) * height / 2; // 'travel' in Y axis with sin()

        particleGeneratorEffect.add( Particle.createParticleExplosion(NUMBER_OF_PARTICLES, position, 1.0, 0.0005, 1.0f, 0.02f) );
    }

    /**
    *  Creates a particle generator simulation (3).
    */
    public void particlesGenerator3(int width, int height, boolean rightMostPartOfScreen)
    {
        Vector position = null;
        Vector velocity = new Vector();

        if (!rightMostPartOfScreen)
        {
            position = new Vector(0, height / 2);
            velocity.x = 1.0;
        }
        else
        {
            position = new Vector(width, height / 2);
            velocity.x = -1.0;
        }

        velocity.y = sin(System.currentTimeMillis() / 384.0); // oscillation in Y axis

        particleGeneratorEffect.add( Particle.createParticleImpactExplosion(NUMBER_OF_PARTICLES, position, velocity, 8.0, 25, 0.05, 1.0f, 0.01f) );
    }

    /**
    *  Creates a particle generator simulation (4).
    */
    public void particlesGenerator4(int width, int height)
    {
        Vector position = new Vector(width / 2, height / 2);
        Vector velocity = new Vector();
        velocity.x = sin(System.currentTimeMillis() / 256.0); // whole circle rotation, sin() in X axis, cos() in Y axis
        velocity.y = cos(System.currentTimeMillis() / 256.0);

        particleGeneratorEffect.add( Particle.createParticleImpactExplosion(NUMBER_OF_PARTICLES, position, velocity, 8.0, 25, 0.05, 1.0f, 0.01f) );
    }


    /**
    *  Checks and returns the state if this set of generated particles has finished/diseased (all of them).
    */
    private boolean haveAllGeneratedParticlesFaded(ArrayList<Particle> particles)
    {
        for (Particle particle : particles)
            if (particle.exists)
                return false;

        return true;
    }

    /**
    *  Updates all generated particles.
    */
    public void updateAllGeneratedParticles()
    {
        for (ArrayList<Particle> particles : particleGeneratorEffect)
        {
            if ( haveAllGeneratedParticlesFaded(particles) )
            {
                particleGeneratorEffect.remove(particles);
                continue;
            }

            for (Particle particle : particles)
                if (particle.exists)
                    particle.updateParticle();
        }
    }

    /**
    *  Renders all generated particles. Use float precision for rendering in this case.
    */
    public void renderAllGeneratedParticles(GL2 gl)
    {
        renderAllGeneratedParticles(gl, 1.0f);
    }

    /**
    *  Renders all generated particles.
    *  Overloaded version which provides a given alpha value to blend with the particle's life one. Use float precision for rendering in this case.
    */
    public void renderAllGeneratedParticles(GL2 gl,  float alpha)
    {
        for (ArrayList<Particle> particles : particleGeneratorEffect)
            for (Particle particle : particles)
                if (particle.exists)
                    drawTexture(gl, particleImageTexture, particle.position.x - particleImageTexture.getImageWidth() / 2.0, particle.position.y - particleImageTexture.getImageHeight() / 2.0, particle.life * alpha);
    }

    /**
    *  RotoZooms & renders all generated particles. Use float precision for rendering in this case.
    */
    public void rotoZoomRenderAllGeneratedParticles(GL2 gl)
    {
        rotoZoomRenderAllGeneratedParticles(gl, 1.0f);
    }

    /**
    *  RotoZooms & renders all generated particles.
    *  Overloaded version which provides a given alpha value to blend with the particle's life one. Use float precision for rendering in this case.
    */
    public void rotoZoomRenderAllGeneratedParticles(GL2 gl,  float alpha)
    {
        for (ArrayList<Particle> particles : particleGeneratorEffect)
            for (Particle particle : particles)
                if (particle.exists)
                    drawRotoZoomTexture(gl, particleImageTexture, particle.position.x - particleImageTexture.getImageWidth() / 2.0, particle.position.y - particleImageTexture.getImageHeight() / 2.0, (particle.theta += 10.0f), particle.life * alpha);
    }

    /**
    *  The manual destructor of this class.
    */
    public void destructor(GL2 gl)
    {
        particleImageTexture = null;

        particleGeneratorEffect = null;
    }


}
