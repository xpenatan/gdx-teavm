package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public abstract class btIDebugDraw extends BulletBase {
    protected btIDebugDraw() {
        super("btIDebugDraw");
    }
//
//    public btIDebugDraw() {
//        resetObj(createNative(), true);
//    }
//
//    private long createNative() {
//		com.dragome.commons.javascript.ScriptHelper.evalNoResult("var debugDraw=new Bullet.MyDebugDraw();debugDraw.self=this;debugDraw.drawLine=this.drawFuc;debugDraw.drawContactPoint=this.drawCont;debugDraw.getDebugMode=this.getDebug;",this);
//		return com.dragome.commons.javascript.ScriptHelper.evalLong("Bullet.getPointer(debugDraw);",this);
//    }
//
//	protected void cacheObj() {
//		com.dragome.commons.javascript.ScriptHelper.put("addr",this.cPointer,this);
//		this.jsObj = com.dragome.commons.javascript.ScriptHelper.eval("Bullet.wrapPointer(addr,Bullet.MyDebugDraw);",this);
//	}
//
//	@MethodAlias(local_alias= "drawFuc")
//	private void debugDrawFuc(long vecFrom, long vecTo, long color) {
//		com.dragome.commons.javascript.ScriptHelper.put("color",color,this);
//		com.dragome.commons.javascript.ScriptHelper.put("vecTo",vecTo,this);
//		com.dragome.commons.javascript.ScriptHelper.put("vecFrom",vecFrom,this);
//		Vector3 tmp1 = com.badlogic.gdx.physics.bullet.linearmath.btVector3.vector3_1;
//		Vector3 tmp2 = com.badlogic.gdx.physics.bullet.linearmath.btVector3.vector3_2;
//		Vector3 tmp3 = com.badlogic.gdx.physics.bullet.linearmath.btVector3.vector3_3;
//		com.dragome.commons.javascript.ScriptHelper.evalNoResult("vecFrom=Bullet.wrapPointer(vecFrom,Bullet.btVector3);vecTo=Bullet.wrapPointer(vecTo,Bullet.btVector3);color=Bullet.wrapPointer(color,Bullet.btVector3);",this);
//		tmp1.x = com.dragome.commons.javascript.ScriptHelper.evalFloat("vecFrom.x();",this);
//		tmp1.y = com.dragome.commons.javascript.ScriptHelper.evalFloat("vecFrom.y();",this);
//		tmp1.z = com.dragome.commons.javascript.ScriptHelper.evalFloat("vecFrom.z();",this);
//		tmp2.x = com.dragome.commons.javascript.ScriptHelper.evalFloat("vecTo.x();",this);
//		tmp2.y = com.dragome.commons.javascript.ScriptHelper.evalFloat("vecTo.y();",this);
//		tmp2.z = com.dragome.commons.javascript.ScriptHelper.evalFloat("vecTo.z();",this);
//		tmp3.x = com.dragome.commons.javascript.ScriptHelper.evalFloat("color.x();",this);
//		tmp3.y = com.dragome.commons.javascript.ScriptHelper.evalFloat("color.y();",this);
//		tmp3.z = com.dragome.commons.javascript.ScriptHelper.evalFloat("color.z();",this);
//		btIDebugDraw debugDraw = (btIDebugDraw)com.dragome.commons.javascript.ScriptHelper.eval("this.self",this);
//		debugDraw.drawLine(tmp1, tmp2, tmp3);
//	}
//
//	private void drawContactFunc(long pointOnB,long normalOnB, float distance, int lifeTime, long color) {
//		com.dragome.commons.javascript.ScriptHelper.put("color",color,this);
//		com.dragome.commons.javascript.ScriptHelper.put("lifeTime",lifeTime,this);
//		com.dragome.commons.javascript.ScriptHelper.put("distance",distance,this);
//		com.dragome.commons.javascript.ScriptHelper.put("normalOnB",normalOnB,this);
//		com.dragome.commons.javascript.ScriptHelper.put("pointOnB",pointOnB,this);
//		Vector3 tmp1 = com.badlogic.gdx.physics.bullet.linearmath.btVector3.vector3_1;
//		Vector3 tmp2 = com.badlogic.gdx.physics.bullet.linearmath.btVector3.vector3_2;
//		Vector3 tmp3 = com.badlogic.gdx.physics.bullet.linearmath.btVector3.vector3_3;
//		com.dragome.commons.javascript.ScriptHelper.evalNoResult("vecFrom=Bullet.wrapPointer(pointOnB,Bullet.btVector3);vecTo=Bullet.wrapPointer(normalOnB,Bullet.btVector3);color=Bullet.wrapPointer(color,Bullet.btVector3);",this);
//		tmp1.x = com.dragome.commons.javascript.ScriptHelper.evalFloat("vecFrom.x();",this);
//		tmp1.y = com.dragome.commons.javascript.ScriptHelper.evalFloat("vecFrom.y();",this);
//		tmp1.z = com.dragome.commons.javascript.ScriptHelper.evalFloat("vecFrom.z();",this);
//		tmp2.x = com.dragome.commons.javascript.ScriptHelper.evalFloat("vecTo.x();",this);
//		tmp2.y = com.dragome.commons.javascript.ScriptHelper.evalFloat("vecTo.y();",this);
//		tmp2.z = com.dragome.commons.javascript.ScriptHelper.evalFloat("vecTo.z();",this);
//		tmp3.x = com.dragome.commons.javascript.ScriptHelper.evalFloat("color.x();",this);
//		tmp3.y = com.dragome.commons.javascript.ScriptHelper.evalFloat("color.y();",this);
//		tmp3.z = com.dragome.commons.javascript.ScriptHelper.evalFloat("color.z();",this);
//		btIDebugDraw debugDraw = (btIDebugDraw)com.dragome.commons.javascript.ScriptHelper.eval("this.self",this);
//		debugDraw.drawContactPoint(tmp1, tmp2, distance, lifeTime, tmp3);
//	}
//
//    public void drawLine(Vector3 from, Vector3 to, Vector3 color) {
//    }
//
//    public void drawContactPoint(Vector3 pointOnB, Vector3 normalOnB, float distance, int lifeTime, Vector3 color) {
//    }
//
//    public void reportErrorWarning(String warningString) {
//		//FIXME not impl
//    }
//
//    public void draw3dText(
//    Vector3 location, //FIXME not impl
//    String textString) {
//    }
//
//    public void setDebugMode(int debugMode) {
//        setDebugMode(cPointer, debugMode);
//    }
//
//    private static native void setDebugMode(long addr, int debugMode);
//
//    public int getDebugMode() {
//        return 0;
//    }
//
//    public static final class DebugDrawModes {
//        public static final int DBG_NoDebug = 0;
//        public static final int DBG_DrawWireframe = 1;
//        public static final int DBG_DrawAabb = 2;
//        public static final int DBG_DrawFeaturesText = 4;
//        public static final int DBG_DrawContactPoints = 8;
//        public static final int DBG_NoDeactivation = 16;
//        public static final int DBG_NoHelpText = 32;
//        public static final int DBG_DrawText = 64;
//        public static final int DBG_ProfileTimings = 128;
//        public static final int DBG_EnableSatComparison = 256;
//        public static final int DBG_DisableBulletLCP = 512;
//        public static final int DBG_EnableCCD = 1024;
//        public static final int DBG_DrawConstraints = (1 << 11);
//        public static final int DBG_DrawConstraintLimits = (1 << 12);
//        public static final int DBG_FastWireframe = (1 << 13);
//        public static final int DBG_DrawNormals = (1 << 14);
//        public static final int DBG_DrawFrames = (1 << 15);
//        public static final int DBG_MAX_DEBUG_DRAW_MODE = DBG_DrawFrames + 1;
//    }
}