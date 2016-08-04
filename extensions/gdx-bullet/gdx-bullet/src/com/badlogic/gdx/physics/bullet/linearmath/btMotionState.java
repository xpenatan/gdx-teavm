package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.BulletBase;
/*[0;X] com.dragome.commons.compiler.annotations.MethodAlias */
/** @author xpenatan */
public class btMotionState extends BulletBase {
	/*JNI
	#include <src/bullet/LinearMath/btMotionState.h>
	#include <src/custom/gdx/common/envHelper.h>
	
	static jmethodID getWorldTransformID = 0;
	static jmethodID setWorldTransformID = 0;
	
	class CustomMotionState : public btMotionState {
		private:
			jobject obj; //weak Global reference
		
		public:
			CustomMotionState(jobject obj) {
				this->obj = obj;
			}
		
			virtual void getWorldTransform(btTransform& worldTrans) const {
				// called when body is initialized.
				jfloatArray valArray = (jfloatArray)EnvHelper::env->CallObjectMethod(obj, getWorldTransformID);
				btVector3 & origin = worldTrans.getOrigin();
				btMatrix3x3 & matrix3x3 = worldTrans.getBasis();
				btVector3 & row0 = matrix3x3[0];
				btVector3 & row1 = matrix3x3[1];
				btVector3 & row2 = matrix3x3[2];
				float* array = (float*)EnvHelper::env->GetPrimitiveArrayCritical(valArray, 0);
				row0.setValue(array[0],array[4],array[8]);
				row1.setValue(array[1],array[5],array[9]);
				row2.setValue(array[2],array[6],array[10]);
				origin.setValue(array[12], array[13], array[14]);
				EnvHelper::env->ReleasePrimitiveArrayCritical(valArray, array, 0);
			}
			
			virtual void setWorldTransform(const btTransform& worldTrans)  {
				// called when body is moved.
				btVector3 origin = worldTrans.getOrigin();
				btMatrix3x3 matrix3x3 = worldTrans.getBasis();
				btVector3 row0 = matrix3x3.getRow(0);
				btVector3 row1 = matrix3x3.getRow(1);
				btVector3 row2 = matrix3x3.getRow(2);
				
				EnvHelper::env->CallVoidMethod(obj, setWorldTransformID, 
				(jfloat)row0.x(), (jfloat)row0.y(), (jfloat)row0.z(), 
				(jfloat)row1.x(), (jfloat)row1.y(), (jfloat)row1.z(), 
				(jfloat)row2.x(), (jfloat)row2.y(), (jfloat)row2.z(),
				origin.x(), origin.y(), origin.z());
			}
		};
	*/
	
	public btMotionState() {
		resetObj(createNative(), true);
	}
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;D]*/
	
	private static native void deletePointer(long addr); /*
		CustomMotionState * cobj = (CustomMotionState *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
	
	private native long createNative(); /*
		if(!getWorldTransformID) {
			jclass cls = env->GetObjectClass(object);
			getWorldTransformID = env->GetMethodID(cls, "getWorldTransform", "()[F");
			setWorldTransformID = env->GetMethodID(cls, "setWorldTransform", "(FFFFFFFFFFFF)V");
		}
		jobject weakRef = env->NewWeakGlobalRef(object);
		return (jlong)new CustomMotionState(weakRef);
	*/
	/*[0;X;L]
		var jsMotionState = new Bullet.MyMotionState();
		jsMotionState.self = this; 
		jsMotionState.getWorldTransform = this.getT;
		jsMotionState.setWorldTransform = this.setT;
		return Bullet.getPointer(jsMotionState); #E
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr,this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.MyMotionState); #EVAL
		}
	*/
	
	/*[0;X;F;L]
		@MethodAlias(local_alias= "getT")
		private void getWorldTran(long worldTrans) {
			Matrix4 tmpMat = btTransform.tmp_param1; #J
			tmpMat.idt(); #J
			btMotionState motion = this.self = btMotionState #EVAL
			motion.getWorldTransform(tmpMat); #J
			var worldTrans = Bullet.wrapPointer(worldTrans, Bullet.btTransform);
			var origin = worldTrans.getOrigin();
			var matrix3x3 = worldTrans.getBasis();
			var row0 = matrix3x3.getRow(0);
			var row1 = matrix3x3.getRow(1);
			var row2 = matrix3x3.getRow(2);
			x, tmpMat.val[0] #P
			y, tmpMat.val[4] #P
			z, tmpMat.val[8] #P
			row0.setValue(x, y, z);
			x, tmpMat.val[1] #P
			y, tmpMat.val[5] #P
			z, tmpMat.val[9] #P
			row1.setValue(x, y, z);
			x, tmpMat.val[2] #P
			y, tmpMat.val[6] #P
			z, tmpMat.val[10] #P
			row2.setValue(x, y, z);
			x, tmpMat.val[12] #P
			y, tmpMat.val[13] #P
			z, tmpMat.val[14] #P
			origin.setValue(x, y, z);
		}
	*/
	
	/*[0;X;F;L]
		@MethodAlias(local_alias= "setT")
		private void setWorldTran(long worldTrans) {
			Matrix4 tmpMat = btTransform.tmp_param1; #J
			
			var worldTrans=Bullet.wrapPointer(worldTrans,Bullet.btTransform);
			var origin=worldTrans.getOrigin();
			var matrix3x3=worldTrans.getBasis();
			var row0=matrix3x3.getRow(0);
			var row1=matrix3x3.getRow(1);
			var row2=matrix3x3.getRow(2);
			tmpMat.val[0] = row0.x(); #EVALFLOAT
			tmpMat.val[1] = row1.x(); #EVALFLOAT
			tmpMat.val[2] = row2.x(); #EVALFLOAT
			tmpMat.val[3] = 0; #J
			tmpMat.val[4] = row0.y(); #EVALFLOAT
			tmpMat.val[5] = row1.y(); #EVALFLOAT
			tmpMat.val[6] = row2.y(); #EVALFLOAT
			tmpMat.val[7] = 0; #J
			tmpMat.val[8] = row0.z(); #EVALFLOAT
			tmpMat.val[9] = row1.z(); #EVALFLOAT
			tmpMat.val[10] = row2.z(); #EVALFLOAT
			tmpMat.val[11] = 0; #J
			tmpMat.val[12] = origin.x(); #EVALFLOAT
			tmpMat.val[13] = origin.y(); #EVALFLOAT
			tmpMat.val[14] = origin.z(); #EVALFLOAT
			tmpMat.val[15] = 1.0f; #J
			btMotionState motion = this.self = btMotionState #EVAL
			motion.setWorldTransform(tmpMat); #J
		}
	 */

	private float [] getWorldTransform() {
		btTransform.tmp_param1.idt();
		getWorldTransform(btTransform.tmp_param1);
		return btTransform.tmp_param1.val;
	}
	/*[0;X;D]*/

	private void setWorldTransform(
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			float x3, float y3, float z3,
			float x,  float y,  float z) {
		btTransform.tmp_param1.val[0] = x1;
		btTransform.tmp_param1.val[1] = x2;
		btTransform.tmp_param1.val[2] = x3;
		btTransform.tmp_param1.val[3] = 0;
		btTransform.tmp_param1.val[4] = y1;
		btTransform.tmp_param1.val[5] = y2;
		btTransform.tmp_param1.val[6] = y3;
		btTransform.tmp_param1.val[7] = 0;
		btTransform.tmp_param1.val[8] = z1;
		btTransform.tmp_param1.val[9] = z2;
		btTransform.tmp_param1.val[10] = z3;
		btTransform.tmp_param1.val[11] = 0;
		btTransform.tmp_param1.val[12] = x;
		btTransform.tmp_param1.val[13] = y;
		btTransform.tmp_param1.val[14] = z;
		btTransform.tmp_param1.val[15] = 1.0f;
		setWorldTransform(btTransform.tmp_param1);
	}
	/*[0;X;D]*/
	
	
	/**
	 * Called to initialize body position. Modify worldTrans.
	 */
	public void getWorldTransform(Matrix4 worldTrans) {
	}
	
	/**
	 * Called when rigid body change position. Update your render matrix with worldTrans.
	 */
	public void setWorldTransform(Matrix4 worldTrans) {
	}
}
