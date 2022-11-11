package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;
/*[0;X] com.dragome.commons.compiler.annotations.MethodAlias */

/**
 * @author xpenatan
 */
public class btIDebugDraw extends BulletBase {

	/*JNI
		#include <src/bullet/LinearMath/btIDebugDraw.h>
		#include <src/custom/gdx/common/envHelper.h>
		#include <iostream>
		using namespace std;
		
		static jmethodID drawLineID = 0;
		static jmethodID drawContactPointID = 0;
		
		class CustomDebugDraw : public btIDebugDraw {
			private:
				jobject obj; //weak Global reference
				int debugMode;
			public:
				CustomDebugDraw(jobject obj) {
					this->obj = obj;
					debugMode = 0;
				}
				
				virtual void drawLine(const btVector3& from,const btVector3& to,const btVector3& color) {
					EnvHelper::env->CallVoidMethod(obj, drawLineID, 
						(jfloat)from.x(), (jfloat)from.y(), (jfloat)from.z(), 
						(jfloat)to.x(), (jfloat)to.y(), (jfloat)to.z(), 
						(jfloat)color.x(), (jfloat)color.y(), (jfloat)color.z());
				
				}
				
				virtual void drawContactPoint(const btVector3& PointOnB,const btVector3& normalOnB,btScalar distance,int lifeTime,const btVector3& color) {
					EnvHelper::env->CallVoidMethod(obj, drawContactPointID, 
						(jfloat)PointOnB.x(), (jfloat)PointOnB.y(), (jfloat)PointOnB.z(), 
						(jfloat)normalOnB.x(), (jfloat)normalOnB.y(), (jfloat)normalOnB.z(), 
						(jfloat)color.x(), (jfloat)color.y(), (jfloat)color.z(), distance, lifeTime);
				}
				
				virtual void reportErrorWarning(const char* warningString) {
				
				}
				
				virtual void draw3dText(const btVector3& location,const char* textString) {
				
				}
				
				virtual void	setDebugMode(int debugMode) {
					this->debugMode = debugMode;
				}
				
				virtual int	getDebugMode() const {
					return debugMode; // stored in c++ so there is no performance hit
				}
		};
	*/

    public btIDebugDraw() {
        resetObj(createNative(), true);
    }

    private native long createNative(); /*
		if(!drawLineID) {
			jclass cls = env->GetObjectClass(object);
			drawLineID = env->GetMethodID(cls, "drawLine", "(FFFFFFFFF)V");
			drawContactPointID = env->GetMethodID(cls, "drawContactPoint", "(FFFFFFFFFFI)V");
		}
		jobject weakRef = env->NewWeakGlobalRef(object);
		return  (jlong)new CustomDebugDraw(weakRef);
	*/
	/*[0;X;L]
		var debugDraw=new Bullet.MyDebugDraw(); #B
		debugDraw.self = this;
		debugDraw.drawLine = this.drawFuc;
		debugDraw.drawContactPoint = this.drawCont;
		debugDraw.getDebugMode = this.getDebug;
		return Bullet.getPointer(debugDraw); 
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.MyDebugDraw); #EVAL
		}
	*/
	
	/*[0;X;F;L]
		@MethodAlias(local_alias= "drawFuc")
		private void debugDrawFuc(long vecFrom, long vecTo, long color) {
			Vector3 tmp1 = com.badlogic.gdx.physics.bullet.linearmath.btVector3.vector3_1; #J
			Vector3 tmp2 = com.badlogic.gdx.physics.bullet.linearmath.btVector3.vector3_2; #J
			Vector3 tmp3 = com.badlogic.gdx.physics.bullet.linearmath.btVector3.vector3_3; #J
			vecFrom=Bullet.wrapPointer(vecFrom,Bullet.btVector3);
			vecTo=Bullet.wrapPointer(vecTo,Bullet.btVector3);
			color=Bullet.wrapPointer(color,Bullet.btVector3);
			tmp1.x = vecFrom.x(); #EVALFLOAT
			tmp1.y = vecFrom.y(); #EVALFLOAT
			tmp1.z = vecFrom.z(); #EVALFLOAT
			tmp2.x = vecTo.x(); #EVALFLOAT
			tmp2.y = vecTo.y(); #EVALFLOAT
			tmp2.z = vecTo.z(); #EVALFLOAT
			tmp3.x = color.x(); #EVALFLOAT
			tmp3.y = color.y(); #EVALFLOAT
			tmp3.z = color.z(); #EVALFLOAT
			btIDebugDraw debugDraw = this.self = btIDebugDraw #EVAL
			debugDraw.drawLine(tmp1, tmp2, tmp3); #J
		}
	*/
	
	/*[0;X;F;L]
		@MethodAlias(local_alias= "drawCont")
		private void drawContactFunc(long pointOnB,long normalOnB, float distance, int lifeTime, long color) {
			Vector3 tmp1 = com.badlogic.gdx.physics.bullet.linearmath.btVector3.vector3_1; #J
			Vector3 tmp2 = com.badlogic.gdx.physics.bullet.linearmath.btVector3.vector3_2; #J
			Vector3 tmp3 = com.badlogic.gdx.physics.bullet.linearmath.btVector3.vector3_3; #J
			vecFrom=Bullet.wrapPointer(pointOnB,Bullet.btVector3);
			vecTo=Bullet.wrapPointer(normalOnB,Bullet.btVector3);
			color=Bullet.wrapPointer(color,Bullet.btVector3);
			tmp1.x = vecFrom.x(); #EVALFLOAT
			tmp1.y = vecFrom.y(); #EVALFLOAT
			tmp1.z = vecFrom.z(); #EVALFLOAT
			tmp2.x = vecTo.x(); #EVALFLOAT
			tmp2.y = vecTo.y(); #EVALFLOAT
			tmp2.z = vecTo.z(); #EVALFLOAT
			tmp3.x = color.x(); #EVALFLOAT
			tmp3.y = color.y(); #EVALFLOAT
			tmp3.z = color.z(); #EVALFLOAT
			btIDebugDraw debugDraw = this.self = btIDebugDraw #EVAL
			debugDraw.drawContactPoint(tmp1, tmp2, distance, lifeTime, tmp3); #J
		}
	*/
	
	/*[0;X;F;L]
		@MethodAlias(local_alias= "getDebug")
		private int drawContactFunc() {
			btIDebugDraw debugDraw = this.self = btIDebugDraw #EVAL
			return debugDraw.getDebugMode(); #J
		}
	 */

    @Override
    protected void delete() {
        deletePointer(cPointer);
    }
    /*[0;X;D]*/

    private static native void deletePointer(long addr); /*
		CustomDebugDraw * cobj = (CustomDebugDraw *)addr;
		delete cobj;
	*/
    /*[0;X;D]*/

    private void drawLine(float fromX, float fromY, float fromZ, float toX, float toY, float toZ, float colorX, float colorY, float colorZ) {
        btVector3.vector3_1.set(fromX, fromY, fromZ);
        btVector3.vector3_2.set(toX, toY, toZ);
        btVector3.vector3_3.set(colorX, colorY, colorZ);
        drawLine(btVector3.vector3_1, btVector3.vector3_2, btVector3.vector3_3);
    }
    /*[0;X;D]*/

    public void drawLine(Vector3 from, Vector3 to, Vector3 color) {
    }

    private void drawContactPoint(float pointOnBX, float pointOnBY, float pointOnBZ, float normalOnX, float normalOnY, float normalOnZ, float colorX, float colorY, float colorZ, float distance, int lifeTime) {
        btVector3.vector3_1.set(pointOnBX, pointOnBY, pointOnBZ);
        btVector3.vector3_2.set(normalOnX, normalOnY, normalOnZ);
        btVector3.vector3_3.set(colorX, colorY, colorZ);
        drawContactPoint(btVector3.vector3_1, btVector3.vector3_2, distance, lifeTime, btVector3.vector3_3);
    }
    /*[0;X;D]*/

    public void drawContactPoint(Vector3 pointOnB, Vector3 normalOnB, float distance, int lifeTime, Vector3 color) {
    }

    public void reportErrorWarning(String warningString) {
        //FIXME not impl
    }

    public void draw3dText(Vector3 location, String textString) { //FIXME not impl
    }

    public void setDebugMode(int debugMode) {
        setDebugMode(cPointer, debugMode);
    }

    private static native void setDebugMode(long addr, int debugMode); /*
		CustomDebugDraw * cobj = (CustomDebugDraw *)addr;
		cobj->setDebugMode(debugMode);
	*/

    public int getDebugMode() {
        return 0;
    }

    public final static class DebugDrawModes {
        public final static int DBG_NoDebug = 0;
        public final static int DBG_DrawWireframe = 1;
        public final static int DBG_DrawAabb = 2;
        public final static int DBG_DrawFeaturesText = 4;
        public final static int DBG_DrawContactPoints = 8;
        public final static int DBG_NoDeactivation = 16;
        public final static int DBG_NoHelpText = 32;
        public final static int DBG_DrawText = 64;
        public final static int DBG_ProfileTimings = 128;
        public final static int DBG_EnableSatComparison = 256;
        public final static int DBG_DisableBulletLCP = 512;
        public final static int DBG_EnableCCD = 1024;
        public final static int DBG_DrawConstraints = (1 << 11);
        public final static int DBG_DrawConstraintLimits = (1 << 12);
        public final static int DBG_FastWireframe = (1 << 13);
        public final static int DBG_DrawNormals = (1 << 14);
        public final static int DBG_DrawFrames = (1 << 15);
        public final static int DBG_MAX_DEBUG_DRAW_MODE = DBG_DrawFrames + 1;
    }
}
