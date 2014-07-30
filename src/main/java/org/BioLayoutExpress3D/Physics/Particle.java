package org.BioLayoutExpress3D.Physics;

import java.util.*;

/**
*
* The Particle class is used for a particle object for explosions, trails & other physics effects.
*
* @see org.BioLayoutExpress3D.Physics.Vector
* @see org.BioLayoutExpress3D.Physics.ParticlesGenerator
* @author Thanos Theo, Michael Kargas, 2008-2009
* @version 3.0.0.0
*/

public final class Particle
{
    /**
    *  Vector position of the particle.
    */
    public Vector position = new Vector();

    /**
    *  Vector velocity of the particle.
    */
    public Vector velocity = new Vector();

    /**
    *  Vector acceleration of the particle.
    */
    public Vector acceleration = new Vector();

    /**
    *  Life variable of the particle.
    */
    public float life = 0.0f;

    /**
    *  Life decrease variable of the particle.
    */
    public float lifeDecrease = 0.0f;

    /**
    *  Theta variable of the particle.
    */
    public float theta = 0.0f;

    /**
    *  Exists boolean variable of the particle.
    */
    public boolean exists = false;

    /**
    *  The first constructor of the particle.
    */
    public Particle() {}

    /**
    *  The second constructor of the particle. Initializes the particle with initial values.
    */
    public Particle(Vector position, Vector velocity, Vector acceleration, float life, float lifeDecrease, boolean exists)
    {
        this.position.x = position.x;
        this.position.y = position.y;
        this.velocity.x = velocity.x;
        this.velocity.y = velocity.y;
        this.acceleration.x = acceleration.x;
        this.acceleration.y = acceleration.y;
        this.life = life;
        this.lifeDecrease = lifeDecrease;
        this.exists = exists;
    }

    /**
    *  Creates and returns a particle with given values.
    */
    public static Particle createParticle(Vector position, Vector velocity, Vector acceleration, float life, float lifeDecrease)
    {
        return new Particle(position, velocity, acceleration, life, lifeDecrease, true);
    }

    /**
    *  Creates and returns an arraylist of particles with given values simulating an explosion.
    */
    public static ArrayList<Particle> createParticleExplosion(int numberOfParticles, Vector position, double speedMultiplier, double gravity, float life, float lifeDecrease)
    {
        ArrayList<Particle> allParticles = new ArrayList<Particle>(numberOfParticles);

        int RAND_MAX = 65536;
        double velocityX = 0.0;
        double velocityY = 0.0;
        double accelerationX = 0.0;
        double accelerationY = 0.0;

        for (int i = 0; i < numberOfParticles; i++)
        {
            velocityX = speedMultiplier * (double)(org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, RAND_MAX) - RAND_MAX / 2) / (RAND_MAX / 2); // between -1.0 & 1.0
            velocityY = speedMultiplier * (double)(org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, RAND_MAX) - RAND_MAX / 2) / (RAND_MAX / 2); // between -1.0 & 1.0
            accelerationX = 0.0;
            accelerationY = gravity;

            allParticles.add( createParticle(position, new Vector(velocityX, velocityY), new Vector(accelerationX, accelerationY), life, lifeDecrease) );
        }

        return allParticles;
    }

    /**
    *  Creates and returns an arraylist of particles with given values simulating an impact explosion.
    */
    public static ArrayList<Particle> createParticleImpactExplosion(int numberOfParticles, Vector position, Vector velocity, double speedmul, int degrees, double gravity, float life, float lifeDecrease)
    {
        ArrayList<Particle> allParticles = new ArrayList<Particle>(numberOfParticles);

        int RAND_MAX = 65536;
        Vector rotationVelocity = new Vector();
        Vector acceleration = new Vector();
        double flrand = 0.0;
        double sprand = 0.0;
        double angle  = 0.0;

        Vector.vectorNormalize(velocity);

        for (int i = 0; i < numberOfParticles; i++)
        {
            flrand = Math.random(); // between 0.0 - 1.0
            angle = ((double)degrees / 57.3) * (flrand - 0.5); // 57.3 = 180 / PI, toRadians()

            Vector.vectorRotate(velocity, rotationVelocity, angle);

            sprand = speedmul * ( 0.5 + ( (double)org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, RAND_MAX) / (double)(2 * RAND_MAX) ) ); // sprand = speed random, between 0.5 - 1.0
            rotationVelocity.x *= sprand;
            rotationVelocity.y *= sprand;
            acceleration.x = 0.0;
            acceleration.y = gravity;

            allParticles.add( createParticle(position, rotationVelocity, acceleration, life, lifeDecrease) );
        }

        return allParticles;
    }

    /**
    *  Updates the values of a given particle.
    */
    public void updateParticle()
    {
        position.x += velocity.x;
        position.y += velocity.y;
        velocity.x += acceleration.x;
        velocity.y += acceleration.y;
        life       -= lifeDecrease;

        if (life <= 0.0f)
        {
            life = 0.0f;
            exists = false;
        }
    }


}