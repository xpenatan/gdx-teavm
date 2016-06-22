package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btQuaternion;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/** @author xpenatan */
public class btRigidBody extends btCollisionObject {
	/*JNI
		#include <src/bullet/BulletDynamics/Dynamics/btRigidBody.h>
		#include <src/custom/gdx/common/envHelper.h>
	*/
	
	protected btMotionState motionState;
	
	btTypedConstraint typedConstraint = new btTypedConstraint(0, false);
	
	@Override
	protected void create() {} // block super class from creating obj;
	
	public btRigidBody(btRigidBodyConstructionInfo constructionInfo) {
		this.shape = constructionInfo.collisionShape;
		this.motionState = constructionInfo.motionState;
		resetObj(createNative(constructionInfo.cPointer), true);
	}
	
	public btRigidBody(float mass, btMotionState motionState, btCollisionShape collisionShape, Vector3 localInertia) {
		this.shape = collisionShape;
		this.motionState = motionState;
		resetObj(createNative(mass, motionState != null ? motionState.cPointer : 0, collisionShape != null ? collisionShape.cPointer : 0, localInertia.x,localInertia.y,localInertia.z), true);
	}
	
	public btRigidBody(float mass, btMotionState motionState, btCollisionShape collisionShape) {
		this.shape = collisionShape;
		this.motionState = motionState;
		resetObj(createNative(mass, motionState != null ? motionState.cPointer : 0, collisionShape != null ? collisionShape.cPointer : 0, 0,0,0), true);
	}
	
	private static native long createNative(long constructionInfoAddr); /*
		EnvHelper::env = env;
		btRigidBody::btRigidBodyConstructionInfo * constInfo = (btRigidBody::btRigidBodyConstructionInfo *)constructionInfoAddr;
		jlong ret = (jlong)new btRigidBody(*constInfo);
		EnvHelper::env = 0;
		return ret;
	*/
	/*[0;X;L]
	 	var con = Bullet.wrapPointer(constructionInfoAddr, Bullet.btRigidBodyConstructionInfo);
		var cobj = new Bullet.btRigidBody(con);
		return Bullet.getPointer(cobj);
	*/
	
	private static native long createNative(float mass, long motionStateAddr, long collisionShapeAddr, float x, float y, float z); /*
		EnvHelper::env = env;
		btMotionState * motionState = (btMotionState *)motionStateAddr;
		btCollisionShape * collShape = (btCollisionShape *)collisionShapeAddr;
		btVector3 vec(x,y,z);
		jlong ret = (jlong)new btRigidBody(mass, motionState, collShape, vec);
		EnvHelper::env = 0;
		return ret;
	*/
	/*[0;X;L]
	 	var motion = Bullet.wrapPointer(motionStateAddr, Bullet.btMotionState);
	 	var shape = Bullet.wrapPointer(collisionShapeAddr, Bullet.btCollisionShape);
	 	var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		var cobj = new Bullet.btRigidBody(mass,motion,shape,vec);
		return Bullet.getPointer(cobj);
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.btRigidBody);
		}
	*/
	
	@Override
	protected void delete() {
		super.delete();
		if(motionState != null)
			motionState.dispose();
		motionState = null;
	}

	public void setContactSolverType(int value) {
		checkPointer();
		setContactSolverType(cPointer, value);
	}
	/*[0;X;L]
		checkPointer(); #J
		this.$$$jsObj.set_m_contactSolverType(value);
	*/

	private static native void setContactSolverType(long addr, int value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		cobj->m_contactSolverType = value;
	*/
	/*[0;X;D]*/

	public int getContactSolverType() {
		checkPointer();
		return getContactSolverType(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		return this.$$$jsObj.get_m_contactSolverType();
	*/

	private static native int getContactSolverType(long addr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		return cobj->m_contactSolverType;
	*/
	/*[0;X;D]*/

	public void setFrictionSolverType(int value) {
		checkPointer();
		setFrictionSolverType(cPointer, value);
	}
	/*[0;X;L]
		checkPointer(); #J
		this.$$$jsObj.set_m_frictionSolverType(value);
	*/

	private static native void setFrictionSolverType(long addr, int value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		cobj->m_frictionSolverType = value;
	*/
	/*[0;X;D]*/

	public int getFrictionSolverType() {
		checkPointer();
		return getFrictionSolverType(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		return this.$$$jsObj.get_m_frictionSolverType();
	*/

	private static native int getFrictionSolverType(long addr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		return cobj->m_frictionSolverType;
	*/
	/*[0;X;D]*/

	public void proceedToTransform(Matrix4 newTrans) {
		checkPointer();
		proceedToTransform(cPointer, newTrans.val);
	}
	/*[0;X;L]
		checkPointer(); #J
		float [] value = newTrans.val; #J
		var tran = Bullet.MyTemp.prototype.btTran();
		tran.setFromOpenGLMatrix(value);
		this.$$$jsObj.proceedToTransform(tran);
	*/

	private static native void proceedToTransform(long addr, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btTransform tran;
		tran.setFromOpenGLMatrix(value);
		cobj->proceedToTransform(tran);
	*/
	/*[0;X;D]*/

	public void predictIntegratedTransform(float step, Matrix4 predictedTransform) {
		checkPointer();
		predictIntegratedTransform(cPointer, step, predictedTransform.val);
	}
	/*[0;X;L]
		checkPointer(); #J
		float [] value = predictedTransform.val; #J
		var tran = Bullet.MyTemp.prototype.btTran();
		tran.setFromOpenGLMatrix(value);
		this.$$$jsObj.predictIntegratedTransform(step, tran);
	 */
	
	private static native void predictIntegratedTransform(long addr, float step, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btTransform tran;
		tran.setFromOpenGLMatrix(value);
		cobj->predictIntegratedTransform(step, tran);
	 */
	/*[0;X;D]*/

	public void saveKinematicState(float step) {
		checkPointer();
		saveKinematicState(cPointer, step);
	}
	/*[0;X;L]
		checkPointer(); #J
		this.$$$jsObj.saveKinematicState(step);
	 */

	private static native void saveKinematicState(long addr, float step); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		cobj->saveKinematicState(step);
	 */
	/*[0;X;D]*/

	public void applyGravity() {
		checkPointer();
		applyGravity(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		this.$$$jsObj.applyGravity();
	*/

	private static native void applyGravity(long addr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		cobj->applyGravity();
	*/
	/*[0;X;D]*/

	public void setGravity(Vector3 acceleration) {
		checkPointer();
		setGravity(cPointer, acceleration.x, acceleration.y, acceleration.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=acceleration.x,y=acceleration.y,z=acceleration.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		this.$$$jsObj.setGravity(vec);
	*/

	private static native void setGravity(long addr, float x,float y, float z); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec(x,y,z);
		cobj->setGravity(vec);
	*/
	/*[0;X;D]*/

	public void getGravity(Vector3 out) {
		checkPointer();
		getGravity(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.getGravity();
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z); #J
	*/

	private static native void getGravity(long addr, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		const btVector3 & vec = cobj->getGravity();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/

	public void setDamping(float lin_damping, float ang_damping) {
		checkPointer();
		setDamping(cPointer, lin_damping, ang_damping);
	}
	/*[0;X;L]
		checkPointer(); #J
		this.$$$jsObj.setDamping(lin_damping,ang_damping);
	*/

	private static native void setDamping(long addr, float lin_damping, float ang_damping); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		cobj->setDamping(lin_damping, ang_damping);
	*/
	/*[0;X;D]*/

	public float getLinearDamping() {
		checkPointer();
		return getLinearDamping(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		return this.$$$jsObj.getLinearDamping();
	*/

	private static native float getLinearDamping(long addr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		return cobj->getLinearDamping();
	*/
	/*[0;X;D]*/

	public float getAngularDamping() {
		checkPointer();
		return getAngularDamping(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		return this.$$$jsObj.getAngularDamping();
	*/

	private static native float getAngularDamping(long addr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		return cobj->getAngularDamping();
	*/
	/*[0;X;D]*/

	public float getLinearSleepingThreshold() {
		checkPointer();
		return getLinearSleepingThreshold(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		return this.$$$jsObj.getLinearSleepingThreshold();
	*/

	private static native float getLinearSleepingThreshold(long addr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		return cobj->getLinearSleepingThreshold();
	*/
	/*[0;X;D]*/

	public float getAngularSleepingThreshold() {
		checkPointer();
		return getAngularSleepingThreshold(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		return this.$$$jsObj.getAngularSleepingThreshold();
	*/

	private static native float getAngularSleepingThreshold(long addr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		return cobj->getAngularSleepingThreshold();
	*/
	/*[0;X;D]*/

	public void applyDamping(float timeStep) {
		checkPointer();
		applyDamping(cPointer, timeStep);
	}
	/*[0;X;L]
		checkPointer(); #J
		this.$$$jsObj.applyDamping(timeStep);
	*/

	private static native void applyDamping(long addr, float timeStep); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		cobj->applyDamping(timeStep);
	*/
	/*[0;X;D]*/

	public void setMassProps(float mass, Vector3 inertia) {
		checkPointer();
		setMassProps(cPointer, mass, inertia.x, inertia.y, inertia.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=inertia.x,y=inertia.y,z=inertia.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		this.$$$jsObj.setMassProps(mass, vec);
	*/

	private static native void setMassProps(long addr, float mass, float x, float y, float z); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec(x,y,z);
		cobj->setMassProps(mass, vec);
	*/
	/*[0;X;D]*/

	public void getLinearFactor(Vector3 out) {
		checkPointer();
		getLinearFactor(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.getLinearFactor();
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z); #J
	*/

	private static native void getLinearFactor(long addr, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		const btVector3 & vec = cobj->getLinearFactor();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/

	public void setLinearFactor(Vector3 linearFactor) {
		checkPointer();
		setLinearFactor(cPointer, linearFactor.x, linearFactor.y, linearFactor.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=linearFactor.x,y=linearFactor.y,z=linearFactor.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		this.$$$jsObj.setLinearFactor(vec);
	*/

	private static native void setLinearFactor(long addr, float x, float y, float z); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec(x,y,z);
		cobj->setLinearFactor(vec);
	*/
	/*[0;X;D]*/

	public float getInvMass() {
		checkPointer();
		return getInvMass(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		return this.$$$jsObj.getInvMass();
	*/

	private static native float getInvMass(long addr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		return cobj->getInvMass();
	*/
	/*[0;X;D]*/

	public void getInvInertiaTensorWorld(Matrix3 out) {
		checkPointer();
		getInvInertiaTensorWorld(cPointer, out.val);
	}
	/*[0;X;L]
		checkPointer(); #J
		var mat = this.$$$jsObj.getInvInertiaTensorWorld();
		value[0] = mat.getColumn(0).x();
		value[1] = mat.getColumn(0).y();
		value[2] = mat.getColumn(0).z();
		value[3] = mat.getColumn(1).x();
		value[4] = mat.getColumn(1).y();
		value[5] = mat.getColumn(1).z();
		value[6] = mat.getColumn(2).x();
		value[7] = mat.getColumn(2).y();
		value[8] = mat.getColumn(2).z();
	*/
	
	private static native void getInvInertiaTensorWorld(long addr, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		const btMatrix3x3 & mat = cobj->getInvInertiaTensorWorld();
		value[0] = (jfloat) mat.getColumn(0).getX();
		value[1] = (jfloat) mat.getColumn(0).getY();
		value[2] = (jfloat) mat.getColumn(0).getZ();
		value[3] = (jfloat) mat.getColumn(1).getX();
		value[4] = (jfloat) mat.getColumn(1).getY();
		value[5] = (jfloat) mat.getColumn(1).getZ();
		value[6] = (jfloat) mat.getColumn(2).getX();
		value[7] = (jfloat) mat.getColumn(2).getY();
		value[8] = (jfloat) mat.getColumn(2).getZ();
	*/
	/*[0;X;D]*/

	public void integrateVelocities(float step) {
		checkPointer();
		integrateVelocities(cPointer, step);
	}
	/*[0;X;L]
		checkPointer(); #J
		this.$$$jsObj.integrateVelocities(step);
	*/

	private static native void integrateVelocities(long addr, float step); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		cobj->integrateVelocities(step);
	*/
	/*[0;X;D]*/

	public void setCenterOfMassTransform (Matrix4 xform) {
		checkPointer();
		setCenterOfMassTransform(cPointer, xform.val);
	}
	/*[0;X;L]
		checkPointer(); #J
		float [] value = xform.val; #J
		var tran = Bullet.MyTemp.prototype.btTran();
		tran.setFromOpenGLMatrix(value);
		this.$$$jsObj.setCenterOfMassTransform(tran);
	*/

	private static native void setCenterOfMassTransform(long addr, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btTransform tra;
		tra.setFromOpenGLMatrix(value);
		cobj->setCenterOfMassTransform(tra);
	*/
	/*[0;X;D]*/

	public void applyCentralForce (Vector3 force) {
		checkPointer();
		applyCentralForce(cPointer, force.x, force.y, force.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=force.x,y=force.y,z=force.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		this.$$$jsObj.applyCentralForce(vec);
	*/

	private static native void applyCentralForce(long addr, float x,float y,float z); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec(x,y,z);
		cobj->applyCentralForce(vec);
	*/
	/*[0;X;D]*/

	public void getTotalForce(Vector3 out) {
		checkPointer();
		getTotalForce(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.getTotalForce();
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z); #J
	*/

	private static native void getTotalForce(long addr, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		const btVector3 & vec = cobj->getTotalForce();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/

	public void getTotalTorque(Vector3 out) {
		checkPointer();
		getTotalTorque(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.getTotalTorque();
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z); #J
	*/

	private static native void getTotalTorque(long addr, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		const btVector3 & vec = cobj->getTotalTorque();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/

	public void getInvInertiaDiagLocal(Vector3 out) {
		checkPointer();
		getInvInertiaDiagLocal(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.getInvInertiaDiagLocal();
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z); #J
	*/

	private static native void getInvInertiaDiagLocal(long addr, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		const btVector3 & vec = cobj->getInvInertiaDiagLocal();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/

	public void setInvInertiaDiagLocal(Vector3 diagInvInertia) {
		checkPointer();
		setInvInertiaDiagLocal(cPointer, diagInvInertia.x, diagInvInertia.y, diagInvInertia.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=diagInvInertia.x,y=diagInvInertia.y,z=diagInvInertia.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		this.$$$jsObj.setInvInertiaDiagLocal(vec);
	*/

	private static native void setInvInertiaDiagLocal(long addr, float x,float y,float z); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec(x,y,z);
		cobj->setInvInertiaDiagLocal(vec);
	*/
	/*[0;X;D]*/

	public void setSleepingThresholds(float linear, float angular) {
		checkPointer();
		setSleepingThresholds(cPointer, linear, angular);
	}
	/*[0;X;L]
		checkPointer(); #J
		this.$$$jsObj.setSleepingThresholds(linear, angular);
	*/

	private static native void setSleepingThresholds(long addr, float linear, float angular); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		cobj->setSleepingThresholds(linear, angular);
	*/
	/*[0;X;D]*/

	public void applyTorque(Vector3 torque) {
		checkPointer();
		applyTorque(cPointer, torque.x, torque.y, torque.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=torque.x,y=torque.y,z=torque.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		this.$$$jsObj.applyTorque(vec);
	*/

	private static native void applyTorque(long addr, float x,float y,float z); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec(x,y,z);
		cobj->applyTorque(vec);
	*/
	/*[0;X;D]*/

	public void applyForce(Vector3 force, Vector3 rel_pos) {
		checkPointer();
		applyForce(cPointer, force.x, force.y, force.z, rel_pos.x, rel_pos.y, rel_pos.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x1=force.x,y1=force.y,z1=force.z; #J
		float x2=rel_pos.x,y2=rel_pos.y,z2=rel_pos.z; #J
		var vec1 = Bullet.MyTemp.prototype.btVec3_1(x1,y1,z1);
		var vec2 = Bullet.MyTemp.prototype.btVec3_2(x2,y2,z2);
		this.$$$jsObj.applyForce(vec1, vec2);
	*/

	private static native void applyForce(long addr, float x1,float y1,float z1, float x2,float y2,float z2); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec1(x1,y1,z1);
		btVector3 vec2(x2,y2,z2);
		cobj->applyForce(vec1,vec2);
	*/
	/*[0;X;D]*/

	public void applyCentralImpulse(Vector3 impulse) {
		checkPointer();
		applyCentralImpulse(cPointer, impulse.x, impulse.y, impulse.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=impulse.x,y=impulse.y,z=impulse.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		this.$$$jsObj.applyCentralImpulse(vec);
	*/

	private static native void applyCentralImpulse(long addr, float x,float y,float z); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec(x,y,z);
		cobj->applyCentralImpulse(vec);
	*/
	/*[0;X;D]*/

	public void applyTorqueImpulse(Vector3 torque) {
		checkPointer();
		applyTorqueImpulse(cPointer, torque.x, torque.y, torque.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=torque.x,y=torque.y,z=torque.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		this.$$$jsObj.applyTorqueImpulse(vec);
	*/

	private static native void applyTorqueImpulse(long addr, float x,float y,float z); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec(x,y,z);
		cobj->applyTorqueImpulse(vec);
	*/
	/*[0;X;D]*/

	public void applyImpulse(Vector3 impulse, Vector3 rel_pos) {
		checkPointer();
		applyImpulse(cPointer, impulse.x, impulse.y, impulse.z, rel_pos.x, rel_pos.y, rel_pos.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x1=impulse.x,y1=impulse.y,z1=impulse.z; #J
		float x2=rel_pos.x,y2=rel_pos.y,z2=rel_pos.z; #J
		var vec1 = Bullet.MyTemp.prototype.btVec3_1(x1,y1,z1);
		var vec2 = Bullet.MyTemp.prototype.btVec3_2(x2,y2,z2);
		this.$$$jsObj.applyImpulse(vec1, vec2);
	*/

	private static native void applyImpulse(long addr, float x1,float y1,float z1, float x2,float y2,float z2); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec1(x1,y1,z1);
		btVector3 vec2(x2,y2,z2);
		cobj->applyImpulse(vec1,vec2);
	*/
	/*[0;X;D]*/

	public void clearForces() {
		checkPointer();
		clearForces(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		this.$$$jsObj.clearForces();
	*/

	private static native void clearForces(long addr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		cobj->clearForces();
	*/
	/*[0;X;D]*/

	public void updateInertiaTensor() {
		checkPointer();
		updateInertiaTensor(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		this.$$$jsObj.updateInertiaTensor();
	*/

	private static native void updateInertiaTensor(long addr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		cobj->updateInertiaTensor();
	*/
	/*[0;X;D]*/

	public void getCenterOfMassPosition(Vector3 out) {
		checkPointer();
		getCenterOfMassPosition(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.getCenterOfMassPosition();
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z); #J
	*/

	private static native void getCenterOfMassPosition(long addr, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		const btVector3 & vec = cobj->getCenterOfMassPosition();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/

	public void getOrientation(Quaternion out) {
		checkPointer();
		getOrientation(cPointer, btQuaternion.localArr_1);
		out.set(btQuaternion.localArr_1[0], btQuaternion.localArr_1[1], btQuaternion.localArr_1[2], btQuaternion.localArr_1[3]);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0,w=0; #J
		var quat = this.$$$jsObj.getOrientation();
		x = quat.x();
		y = quat.y();
		z = quat.z();
		w = quat.w();
		out.set(x,y,z,w); #J
	*/

	private static native void getOrientation(long addr, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btQuaternion quat = cobj->getOrientation();
		value[0] = quat.x();
		value[1] = quat.y();
		value[2] = quat.z();
		value[3] = quat.w();
	*/
	/*[0;X;D]*/

	public void getCenterOfMassTransform(Matrix4 out) {
		checkPointer();
		getCenterOfMassTransform(cPointer, out.val);
	}
	/*[0;X;L]
		checkPointer(); #J
		Object tran = null; #J
		tran = this.$$$jsObj.getCenterOfMassTransform();
		btTransform.getOpenGLMatrix(tran, out.val); #J 
	*/

	private static native void getCenterOfMassTransform(long addr, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		const btTransform & tra = cobj->getCenterOfMassTransform();
		tra.getOpenGLMatrix(value);
	*/
	/*[0;X;D]*/

	public void getLinearVelocity(Vector3 out) {
		checkPointer();
		getLinearVelocity(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.getLinearVelocity();
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z); #J
	*/

	private static native void getLinearVelocity(long addr, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		const btVector3 & vec = cobj->getLinearVelocity();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/

	public void getAngularVelocity(Vector3 out) {
		checkPointer();
		getAngularVelocity(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.getAngularVelocity();
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z); #J
	*/

	private static native void getAngularVelocity(long addr, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		const btVector3 & vec = cobj->getAngularVelocity();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/

	public void setLinearVelocity(Vector3 lin_vel) {
		checkPointer();
		setLinearVelocity(cPointer, lin_vel.x, lin_vel.y, lin_vel.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=lin_vel.x,y=lin_vel.y,z=lin_vel.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		this.$$$jsObj.setLinearVelocity(vec);
	*/

	private static native void setLinearVelocity(long addr, float x,float y,float z); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec(x,y,z);
		cobj->setLinearVelocity(vec);
	*/
	/*[0;X;D]*/

	public void setAngularVelocity(Vector3 ang_vel) {
		checkPointer();
		setAngularVelocity(cPointer, ang_vel.x, ang_vel.y, ang_vel.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=ang_vel.x,y=ang_vel.y,z=ang_vel.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		this.$$$jsObj.setAngularVelocity(vec);
	*/

	private static native void setAngularVelocity(long addr, float x,float y,float z); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec(x,y,z);
		cobj->setAngularVelocity(vec);
	*/
	/*[0;X;D]*/

	public void getVelocityInLocalPoint(Vector3 rel_pos, Vector3 out) {
		checkPointer();
		getVelocityInLocalPoint(cPointer, rel_pos.x, rel_pos.y, rel_pos.z, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=rel_pos.x,y=rel_pos.y,z=rel_pos.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		vec = this.$$$jsObj.getVelocityInLocalPoint(vec);
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z); #J
	*/

	private static native void getVelocityInLocalPoint(long addr, float x,float y,float z, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec(x,y,z);
		btVector3 out = cobj->getVelocityInLocalPoint(vec);
		value[0] = out.x();
		value[1] = out.y();
		value[2] = out.z();
	*/
	/*[0;X;D]*/

	public void translate(Vector3 v) {
		checkPointer();
		translate(cPointer, v.x, v.y, v.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=v.x,y=v.y,z=v.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		this.$$$jsObj.translate(vec);
	*/

	private static native void translate(long addr, float x,float y,float z); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec(x,y,z);
		cobj->translate(vec);
	*/
	/*[0;X;D]*/

	public void getAabb(Vector3 aabbMin, Vector3 aabbMax) {
		checkPointer();
		getAabb(cPointer, btVector3.localArr_1, btVector3.localArr_2);
		aabbMin.set(btVector3.localArr_1);
		aabbMax.set(btVector3.localArr_2);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0; #J
		var vec1 = Bullet.MyTemp.prototype.btVec3_1(0,0,0);
		var vec2 = Bullet.MyTemp.prototype.btVec3_2(0,0,0);
		this.$$$jsObj.getAabb(vec1, vec2);
		x = vec1.x();
		y = vec1.y();
		z = vec1.z();
		aabbMin.set(x,y,z); #J
		x = vec2.x();
		y = vec2.y();
		z = vec2.z();
		aabbMax.set(x,y,z); #J
	*/

	private static native void getAabb(long addr, float [] value1, float [] value2); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec1;
		btVector3 vec2;
		cobj->getAabb(vec1, vec2);
		value1[0] = vec1.x();
		value1[1] = vec1.y();
		value1[2] = vec1.z();
		value2[0] = vec2.x();
		value2[1] = vec2.y();
		value2[2] = vec2.z();
	*/
	/*[0;X;D]*/

	public float computeImpulseDenominator(Vector3 pos, Vector3 normal) {
		checkPointer();
		return computeImpulseDenominator(cPointer, pos.x, pos.y, pos.z, normal.x, normal.y, normal.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x1=pos.x,y1=pos.y,z1=pos.z; #J
		float x2=normal.x,y2=normal.y,z2=normal.z; #J
		var vec1 = Bullet.MyTemp.prototype.btVec3_1(x1,y1,z1);
		var vec2 = Bullet.MyTemp.prototype.btVec3_2(x2,y2,z2);
		return this.$$$jsObj.computeImpulseDenominator(vec1, vec2);
	*/

	private static native float computeImpulseDenominator(long addr, float x1,float y1, float z1, float x2,float y2,float z2); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec1(x1,y1,z1);
		btVector3 vec2(x2,y2,z2);
		return cobj->computeImpulseDenominator(vec1,vec2);
	*/
	/*[0;X;D]*/

	public float computeAngularImpulseDenominator(Vector3 axis) {
		checkPointer();
		return computeAngularImpulseDenominator(cPointer, axis.x, axis.y, axis.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=axis.x,y=axis.y,z=axis.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		return this.$$$jsObj.computeAngularImpulseDenominator(vec);
	*/

	private static native float computeAngularImpulseDenominator(long addr, float x1,float y1,float z1); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec1(x1,y1,z1);
		return cobj->computeAngularImpulseDenominator(vec1);
	*/
	/*[0;X;D]*/

	public void updateDeactivation(float timeStep) {
		checkPointer();
		updateDeactivation(cPointer, timeStep);
	}
	/*[0;X;L]
		checkPointer(); #J
		this.$$$jsObj.updateDeactivation(timeStep);
	*/

	private static native void updateDeactivation(long addr, float timeStep); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		cobj->updateDeactivation(timeStep);
	*/
	/*[0;X;D]*/

	public boolean wantsSleeping() {
		checkPointer();
		return wantsSleeping(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		return this.$$$jsObj.wantsSleeping();
	*/

	private static native boolean wantsSleeping(long addr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		return cobj->wantsSleeping();
	*/
	/*[0;X;D]*/

	public btMotionState getMotionState() {
		return motionState;
	}

	public void setMotionState(btMotionState motionState) {
		checkPointer();
		this.motionState = motionState;
		setMotionState(cPointer, motionState != null ? motionState.cPointer : 0);
	}
	/*[0;X;L]
		checkPointer(); #J
		this.motionState = motionState; #J
		Object jsObj = null; #J
		if(motionState != null) #J
			jsObj = motionState.$$$jsObj;
		this.$$$jsObj.setMotionState(jsObj);
	*/

	private static native void setMotionState(long addr, long motionStateAddr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btMotionState * motionState = (btMotionState *)motionStateAddr;
		EnvHelper::env = env;
		cobj->setMotionState(motionState);
		EnvHelper::env = 0;
	*/
	/*[0;X;D]*/

	public void setAngularFactor(Vector3 angFac) {
		checkPointer();
		setAngularFactor(cPointer, angFac.x, angFac.y, angFac.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=angFac.x,y=angFac.y,z=angFac.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		this.$$$jsObj.setAngularFactor(vec);
	*/

	private static native void setAngularFactor(long addr, float x1,float y1,float z1); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec1(x1,y1,z1);
		cobj->setAngularFactor(vec1);
	*/
	/*[0;X;D]*/

	public void getAngularFactor(Vector3 out) {
		checkPointer();
		getAngularFactor(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.getAngularFactor();
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z); #J
	*/

	private static native void getAngularFactor(long addr, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		const btVector3 & vec = cobj->getAngularFactor();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/

	public boolean isInWorld() {
		checkPointer();
		return isInWorld(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		return this.$$$jsObj.isInWorld();
	*/

	private static native boolean isInWorld(long addr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		return cobj->isInWorld();
	*/
	/*[0;X;D]*/

	/**
	 * btTypedConstraint object is not cached. If btTypedConstraint is manually created dont let parameter object get garbage collected.
	 */
	public void addConstraintRef(btTypedConstraint c) {
		checkPointer();
		c.checkPointer();
		addConstraintRef(cPointer, c.cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		c.checkPointer(); #J
		this.$$$jsObj.addConstraintRef(c.$$$jsObj);
	*/

	private static native void addConstraintRef(long addr, long typedConstraintAddr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btTypedConstraint * typedCon = (btTypedConstraint *)typedConstraintAddr;
		cobj->addConstraintRef(typedCon);
	*/
	/*[0;X;D]*/

	public void removeConstraintRef(btTypedConstraint c) {
		checkPointer();
		c.checkPointer();
		removeConstraintRef(cPointer, c.cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		c.checkPointer(); #J
		this.$$$jsObj.removeConstraintRef(c.$$$jsObj);
	*/

	private static native void removeConstraintRef(long addr, long typedConstraintAddr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btTypedConstraint * typedCon = (btTypedConstraint *)typedConstraintAddr;
		cobj->removeConstraintRef(typedCon);
	*/
	/*[0;X;D]*/

	/**
	 * Dont keep a reference of this object. Will get replaced by other index calls.
	 */
	public btTypedConstraint getConstraintRef(int index) {
		checkPointer();
		typedConstraint.resetObj(getConstraintRef(cPointer, index), false);
		return typedConstraint;
	}
	/*[0;X;L]
		checkPointer(); #J
		Object jsConstraint = null; #J
		jsConstraint = this.$$$jsObj.getConstraintRef(index);
		if(jsConstraint == null) #J
			return null; #J
		long addr = 0; #J
		addr = Bullet.getPointer(jsConstraint);
		typedConstraint.resetObj(addr, false); #J
		return typedConstraint; #J
	*/

	private static native long getConstraintRef(long addr, int index); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		return (jlong)cobj->getConstraintRef(index);
	*/
	/*[0;X;D]*/

	public int getNumConstraintRefs() {
		checkPointer();
		return getNumConstraintRefs(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		return this.$$$jsObj.getNumConstraintRefs();
	*/

	private static native int getNumConstraintRefs(long addr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		return cobj->getNumConstraintRefs();
	*/
	/*[0;X;D]*/

	public void setFlags(int flags) {
		checkPointer();
		setFlags(cPointer, flags);
	}
	/*[0;X;L]
		checkPointer(); #J
		this.$$$jsObj.setFlags(flags);
	*/

	private static native void setFlags(long addr, int flags); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		cobj->setFlags(flags);
	*/
	/*[0;X;D]*/

	public int getFlags() {
		checkPointer();
		return getFlags(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		return this.$$$jsObj.getFlags();
	*/

	private static native int getFlags(long addr); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		return cobj->getFlags();
	*/
	/*[0;X;D]*/
	
	public void computeGyroscopicImpulseImplicit_World(float dt, Vector3 out) {
		checkPointer();
		computeGyroscopicImpulseImplicit_World(cPointer, dt, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.computeGyroscopicImpulseImplicit_World(dt);
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z); #J
	*/

	private static native void computeGyroscopicImpulseImplicit_World(long addr, float dt, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec = cobj->computeGyroscopicImpulseImplicit_World(dt);
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/

	public void computeGyroscopicImpulseImplicit_Body(float step, Vector3 out) {
		checkPointer();
		computeGyroscopicImpulseImplicit_Body(cPointer, step, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.computeGyroscopicImpulseImplicit_Body(step);
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z); #J
	*/

	private static native void computeGyroscopicImpulseImplicit_Body(long addr, float step, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec = cobj->computeGyroscopicImpulseImplicit_Body(step);
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/

	public void computeGyroscopicForceExplicit(float maxGyroscopicForce, Vector3 out) {
		checkPointer();
		computeGyroscopicForceExplicit(cPointer, maxGyroscopicForce, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.computeGyroscopicForceExplicit(maxGyroscopicForce);
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z); #J
	*/

	private static native void computeGyroscopicForceExplicit(long addr, float maxGyroscopicForce, float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec = cobj->computeGyroscopicForceExplicit(maxGyroscopicForce);
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/
	
	public void getLocalInertia(Vector3 out) {
		checkPointer();
		getLocalInertia(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.getLocalInertia();
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z); #J
	*/

	private static native void getLocalInertia(long addr,  float [] value); /*
		btRigidBody * cobj = (btRigidBody *)addr;
		btVector3 vec = cobj->getLocalInertia();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/

	static public class btRigidBodyConstructionInfo extends BulletBase {
		
		btMotionState motionState;
		btCollisionShape collisionShape;
		
		public btRigidBodyConstructionInfo(float mass, btMotionState motionState, btCollisionShape collisionShape, Vector3 localInertia) {
			this.motionState = motionState;
			this.collisionShape = collisionShape;
			resetObj(createNative(mass, motionState != null ? motionState.cPointer : 0, collisionShape != null ? collisionShape.cPointer : 0, localInertia.x,localInertia.y,localInertia.z), true);
		}
	
	public btRigidBodyConstructionInfo(float mass, btMotionState motionState, btCollisionShape collisionShape) {
			this.motionState = motionState;
			this.collisionShape = collisionShape;
			resetObj(createNative(mass, motionState != null ? motionState.cPointer : 0, collisionShape != null ? collisionShape.cPointer : 0, 0, 0, 0), true);
		}
		
	private static native long createNative(float mass, long motionStateAddr, long collisionShapeAddr, float x, float y, float z); /*
			btMotionState * motionState = (btMotionState *)motionStateAddr;
			btCollisionShape * collShape = (btCollisionShape *)collisionShapeAddr;
			btVector3 vec(x,y,z);
			return (jlong)new btRigidBody::btRigidBodyConstructionInfo(mass, motionState, collShape, vec);
		*/
	
	public void setLinearDamping(float value) {
			checkPointer();
			setLinearDamping(cPointer, value);
		}
	/*[0;X;D]*/
	
		private static native void setLinearDamping(long addr, float value); /*
			btRigidBody::btRigidBodyConstructionInfo * construct = (btRigidBody::btRigidBodyConstructionInfo *)addr;
			construct->m_linearDamping = value;
		*/
		/*[0;X;D]*/
		
		//TODO need to finish other methods
	}
}
