package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;

/**
 * @author xpenatan
 */
public class btDiscreteDynamicsWorld extends btDynamicsWorld {

	/*JNI
		#include <src/bullet/BulletDynamics/Dynamics/btDiscreteDynamicsWorld.h>
	*/

    public btDiscreteDynamicsWorld(btDispatcher dispatcher, btBroadphaseInterface broadInterface, btConstraintSolver constraintSolver, btCollisionConfiguration collisionConfiguration) {
        this.dispatcher = dispatcher;
        this.broadphasePairCache = broadInterface;
        resetObj(createNative(dispatcher.cPointer, broadInterface.cPointer, constraintSolver.cPointer, collisionConfiguration.cPointer), true);
    }

    private native long createNative(long dispatcherAddr, long pairCacheAddr, long constraintSolverAddr, long collisionConfigurationAddr); /*
		btDispatcher * dispatcher = (btDispatcher *)dispatcherAddr;
		btBroadphaseInterface * broadphaseInterface = (btBroadphaseInterface *)pairCacheAddr;
		btConstraintSolver * constraintSolver = (btConstraintSolver *)constraintSolverAddr;
		btCollisionConfiguration * collisionConfiguration = (btCollisionConfiguration *)collisionConfigurationAddr;
		return (jlong)new btDiscreteDynamicsWorld(dispatcher,broadphaseInterface,constraintSolver,collisionConfiguration);
	*/
	/*[0;X;L]
		var dispatch = Bullet.wrapPointer(dispatcherAddr, Bullet.btDispatcher);
		var binterface = Bullet.wrapPointer(pairCacheAddr, Bullet.btBroadphaseInterface);
		var solver = Bullet.wrapPointer(constraintSolverAddr, Bullet.btConstraintSolver);
		var conf = Bullet.wrapPointer(collisionConfigurationAddr, Bullet.btCollisionConfiguration);
		var cobj = new Bullet.btDiscreteDynamicsWorld(dispatch,binterface,solver, conf);
		return Bullet.getPointer(cobj);
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btDiscreteDynamicsWorld); #EVAL
		}
	*/

    @Override
    protected void delete() {
        deletePointer(cPointer);
    }
    /*[0;X;D]*/

    private static native void deletePointer(long addr); /*
		btDiscreteDynamicsWorld * cobj = (btDiscreteDynamicsWorld *)addr;
		delete cobj;
	*/
    /*[0;X;D]*/
}
