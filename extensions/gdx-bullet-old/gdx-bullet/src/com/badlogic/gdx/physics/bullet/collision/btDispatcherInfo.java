package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;

/**
 * @author xpenatan
 */
public class btDispatcherInfo extends BulletBase {

	/*JNI
		#include <src/bullet/BulletCollision/BroadphaseCollision/btDispatcher.h>
	*/

    public btDispatcherInfo(long cPtr, boolean cMemoryOwn) {
        resetObj(cPtr, cMemoryOwn);
    }
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btDispatcherInfo); #EVAL
		}
	 */

    public void setTimeStep(float value) {
        checkPointer();
        setTimeStep(cPointer, value);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.set_m_timeStep(value);
	*/

    private static native void setTimeStep(long addr, float value); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		dispatcherInfo->m_timeStep = value;
	*/
    /*[0;X;D]*/

    public float getTimeStep() {
        checkPointer();
        return getTimeStep(cPointer);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.get_m_timeStep();
	*/

    private static native float getTimeStep(long addr); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		return dispatcherInfo->m_timeStep;
	*/
    /*[0;X;D]*/

    public void setStepCount(int value) {
        checkPointer();
        setStepCount(cPointer, value);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.set_m_stepCount(value);
	*/

    private static native void setStepCount(long addr, int value); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		dispatcherInfo->m_stepCount = value;
	*/
    /*[0;X;D]*/

    public int getStepCount() {
        checkPointer();
        return getStepCount(cPointer);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.get_m_stepCount();
	*/

    private static native int getStepCount(long addr); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		return dispatcherInfo->m_stepCount;
	*/
    /*[0;X;D]*/

    public void setDispatchFunc(int value) {
        checkPointer();
        setDispatchFunc(cPointer, value);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.set_m_dispatchFunc(value);
	*/

    private static native void setDispatchFunc(long addr, int value); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		dispatcherInfo->m_dispatchFunc = value;
	*/
    /*[0;X;D]*/

    public int getDispatchFunc() {
        checkPointer();
        return getDispatchFunc(cPointer);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.get_m_dispatchFunc();
	*/

    private static native int getDispatchFunc(long addr); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		return dispatcherInfo->m_dispatchFunc;
	*/
    /*[0;X;D]*/

    public void setTimeOfImpact(float value) {
        checkPointer();
        setTimeOfImpact(cPointer, value);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.set_m_timeOfImpact(value);
	*/

    private static native void setTimeOfImpact(long addr, float value); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		dispatcherInfo->m_timeOfImpact = value;
	*/
    /*[0;X;D]*/

    public float getTimeOfImpact() {
        checkPointer();
        return getTimeOfImpact(cPointer);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.get_m_timeOfImpact();
	*/

    private static native float getTimeOfImpact(long addr); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		return dispatcherInfo->m_timeOfImpact;
	*/
    /*[0;X;D]*/

    public void setUseContinuous(boolean value) {
        checkPointer();
        setUseContinuous(cPointer, value);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.set_m_useContinuous(value);
	*/

    private static native void setUseContinuous(long addr, boolean value); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		dispatcherInfo->m_useContinuous = value;
	*/
    /*[0;X;D]*/

    public boolean getUseContinuous() {
        checkPointer();
        return getUseContinuous(cPointer);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.get_m_useContinuous();
	*/

    private static native boolean getUseContinuous(long addr); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		return dispatcherInfo->m_useContinuous;
	*/
    /*[0;X;D]*/

    /**
     * not used.
     */
    public void setDebugDraw(btIDebugDraw value) {
    }
    /*[0;X;D]*/

    /**
     * not used.
     */
    public btIDebugDraw getDebugDraw() { // TODO needs this method
        return null;
    }
    /*[0;X;D]*/

    public void setEnableSatConvex(boolean value) {
        checkPointer();
        setEnableSatConvex(cPointer, value);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.set_m_enableSatConvex(value);
	*/

    private static native void setEnableSatConvex(long addr, boolean value); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		dispatcherInfo->m_enableSatConvex = value;
	*/
    /*[0;X;D]*/

    public boolean getEnableSatConvex() {
        checkPointer();
        return getEnableSatConvex(cPointer);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.get_m_enableSatConvex();
	*/

    private static native boolean getEnableSatConvex(long addr); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		return dispatcherInfo->m_enableSatConvex;
	*/
    /*[0;X;D]*/

    public void setEnableSPU(boolean value) {
        checkPointer();
        setEnableSPU(cPointer, value);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.set_m_enableSPU(value);
	*/

    private static native void setEnableSPU(long addr, boolean value); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		dispatcherInfo->m_enableSPU = value;
	*/
    /*[0;X;D]*/

    public boolean getEnableSPU() {
        checkPointer();
        return getEnableSPU(cPointer);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.get_m_enableSPU();
	*/

    private static native boolean getEnableSPU(long addr); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		return dispatcherInfo->m_enableSPU;
	*/
    /*[0;X;D]*/

    public void setUseEpa(boolean value) {
        setUseEpa(cPointer, value);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.set_m_useEpa(value);
	*/

    private static native void setUseEpa(long addr, boolean value); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		dispatcherInfo->m_useEpa = value;
	*/
    /*[0;X;D]*/

    public boolean getUseEpa() {
        checkPointer();
        return getUseEpa(cPointer);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.get_m_useEpa();
	*/

    private static native boolean getUseEpa(long addr); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		return dispatcherInfo->m_useEpa;
	*/
    /*[0;X;D]*/

    public void setAllowedCcdPenetration(float value) {
        checkPointer();
        setAllowedCcdPenetration(cPointer, value);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.set_m_allowedCcdPenetration(value);
	*/

    private static native void setAllowedCcdPenetration(long addr, float value); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		dispatcherInfo->m_allowedCcdPenetration = value;
	*/
    /*[0;X;D]*/

    public float getAllowedCcdPenetration() {
        checkPointer();
        return getAllowedCcdPenetration(cPointer);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.get_m_allowedCcdPenetration();
	*/

    private static native float getAllowedCcdPenetration(long addr); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		return dispatcherInfo->m_allowedCcdPenetration;
	*/
    /*[0;X;D]*/

    public void setUseConvexConservativeDistanceUtil(boolean value) {
        checkPointer();
        setUseConvexConservativeDistanceUtil(cPointer, value);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.set_m_useConvexConservativeDistanceUtil(value);
	*/

    private static native void setUseConvexConservativeDistanceUtil(long addr, boolean value); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		dispatcherInfo->m_useConvexConservativeDistanceUtil = value;
	*/
    /*[0;X;D]*/

    public boolean getUseConvexConservativeDistanceUtil() {
        checkPointer();
        return getUseConvexConservativeDistanceUtil(cPointer);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.get_m_useConvexConservativeDistanceUtil();
	*/

    private static native boolean getUseConvexConservativeDistanceUtil(long addr); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		return dispatcherInfo->m_useConvexConservativeDistanceUtil;
	*/
    /*[0;X;D]*/

    public void setConvexConservativeDistanceThreshold(float value) {
        checkPointer();
        setConvexConservativeDistanceThreshold(cPointer, value);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.set_m_convexConservativeDistanceThreshold(value);
	*/

    private static native void setConvexConservativeDistanceThreshold(long addr, float value); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		dispatcherInfo->m_convexConservativeDistanceThreshold = value;
	*/
    /*[0;X;D]*/

    public float getConvexConservativeDistanceThreshold() {
        checkPointer();
        return getConvexConservativeDistanceThreshold(cPointer);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.get_m_convexConservativeDistanceThreshold();
	*/

    private static native float getConvexConservativeDistanceThreshold(long addr); /*
		btDispatcherInfo * dispatcherInfo = (btDispatcherInfo *)addr;
		return dispatcherInfo->m_convexConservativeDistanceThreshold;
	*/
    /*[0;X;D]*/
}
