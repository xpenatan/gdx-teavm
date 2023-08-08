#include <emscripten.h>
#include <LinearMath/btScalar.h>
#include <LinearMath/btVector3.h>
#include <LinearMath/btQuaternion.h>
#include <LinearMath/btTransform.h>
#include <LinearMath/btMatrix3x3.h>
#include <LinearMath/btMotionState.h>
#include <LinearMath/btIDebugDraw.h>
#include <LinearMath/btAlignedObjectArray.h>
#include <BulletCollision/CollisionDispatch/btCollisionWorld.h>
#include <BulletCollision/NarrowPhaseCollision/btRaycastCallback.h>
#include <BulletCollision/CollisionShapes/btShapeHull.h>
#include <BulletCollision/CollisionShapes/btTriangleIndexVertexArray.h>


typedef btAlignedObjectArray<btVector3>	MyVector3Array;
typedef btAlignedObjectArray<float>	MyScalarArray;
typedef btAlignedObjectArray<const btCollisionObject *>	MyCollisionObjectArray;

class Bullet {
	public: 
		static int getBTVersion() {
			return btGetVersion();
		}
		
		static void setVertices(btIndexedMesh *mesh, float * vertices, int sizeInBytesOfEachVertex, int vertexCount, int positionOffsetInBytes) {
			unsigned char *data = (unsigned char *)vertices;
			mesh->m_vertexBase = &(data[positionOffsetInBytes]);
			mesh->m_vertexStride = sizeInBytesOfEachVertex;
			mesh->m_numVertices = vertexCount;
			mesh->m_vertexType = PHY_FLOAT;
		}
		
		static void setIndices(btIndexedMesh *mesh, short *indices, int indexOffset, int indexCount) {
			mesh->m_triangleIndexBase = (unsigned char*)&(indices[indexOffset]);
			mesh->m_triangleIndexStride = 3 * sizeof(short);
			mesh->m_numTriangles = indexCount / 3;
			mesh->m_indexType = PHY_SHORT;
		}
		
		static const btVector3& getVertexPointer(btShapeHull *hull, int index) {
			const btVector3* array = hull->getVertexPointer();
			return array[index];
		}
};

class MyTemp {
	public:
		static btVector3 btVec3_1(float x, float y, float z) {
			return btVector3(x,y,z);
		}
		static btVector3 btVec3_2(float x, float y, float z) {
			return btVector3(x,y,z);
		}
		static btQuaternion btQuat() {
			return btQuaternion();
		}
		static btTransform btTran() {
			return btTransform();
		}
		static btMatrix3x3 btMat3() {
			return btMatrix3x3();
		}
};


class MyMotionState : public btMotionState {
	public:
		MyMotionState(){}
		void getWorldTransform(btTransform& arg0) const {
			EM_ASM_INT({
			  var self = Module['getCache'](Module['MyMotionState'])[$0];
			  if (!self.hasOwnProperty('getWorldTransform')) throw 'a JSImplementation must implement all functions, you forgot MyMotionState::getWorldTransform.';
			  self['getWorldTransform']($1);
			}, (int)this, (int)&arg0);
		}
		void setWorldTransform(const btTransform& arg0) {
			EM_ASM_INT({
			  var self = Module['getCache'](Module['MyMotionState'])[$0];
			  if (!self.hasOwnProperty('setWorldTransform')) throw 'a JSImplementation must implement all functions, you forgot MyMotionState::setWorldTransform.';
			  self['setWorldTransform']($1);
			}, (int)this, (int)&arg0);
		}
		void __destroy__() {
			EM_ASM_INT({
			  var self = Module['getCache'](Module['MyMotionState'])[$0];
			  if (!self.hasOwnProperty('__destroy__')) throw 'a JSImplementation must implement all functions, you forgot MyMotionState::__destroy__.';
			  self['__destroy__']();
			}, (int)this);
		}
};

class MyDebugDraw : public btIDebugDraw {
	public:
		MyDebugDraw(){}
		void drawLine(const btVector3& arg0, const btVector3& arg1, const btVector3& arg2) {
			EM_ASM_INT({
			  var self = Module['getCache'](Module['MyDebugDraw'])[$0];
			  if (!self.hasOwnProperty('drawLine')) throw 'a JSImplementation must implement all functions, you forgot MyDebugDraw::drawLine.';
			  self['drawLine']($1,$2,$3);
			}, (int)this, (int)&arg0, (int)&arg1, (int)&arg2);
		}
		void drawContactPoint(const btVector3& arg0, const btVector3& arg1, float arg2, int arg3, const btVector3& arg4) {
			EM_ASM_INT({
			  var self = Module['getCache'](Module['MyDebugDraw'])[$0];
			  if (!self.hasOwnProperty('drawContactPoint')) throw 'a JSImplementation must implement all functions, you forgot MyDebugDraw::drawContactPoint.';
			  self['drawContactPoint']($1,$2,$3,$4,$5);
			}, (int)this, (int)&arg0, (int)&arg1, arg2, arg3, (int)&arg4);
		}
		void draw3dText(const btVector3& arg0, const char* arg1) {
			EM_ASM_INT({
			  var self = Module['getCache'](Module['MyDebugDraw'])[$0];
			  if (!self.hasOwnProperty('draw3dText')) throw 'a JSImplementation must implement all functions, you forgot MyDebugDraw::draw3dText.';
			  self['draw3dText']($1,$2);
			}, (int)this, (int)&arg0, arg1);
		}
		
		void setDebugMode(int debugMode) {}
	
		int getDebugMode() const {
			return EM_ASM_INT({
			  var self = Module['getCache'](Module['MyDebugDraw'])[$0];
			  if (!self.hasOwnProperty('getDebugMode')) throw 'a JSImplementation must implement all functions, you forgot MyDebugDraw::getDebugMode.';
			  return self['getDebugMode']();
			}, (int)this);
		}
		
		void reportErrorWarning(const char* warningString) {}
		
		void __destroy__() {
			EM_ASM_INT({
			  var self = Module['getCache'](Module['MyDebugDraw'])[$0];
			  if (!self.hasOwnProperty('__destroy__')) throw 'a JSImplementation must implement all functions, you forgot MyDebugDraw::__destroy__.';
			  self['__destroy__']();
			}, (int)this);
		}
};

class MyRayResultCallback : public btCollisionWorld::RayResultCallback {
	public:
		MyRayResultCallback(){}
	
		float addSingleResult(btCollisionWorld::LocalRayResult & arg0, bool arg1) {
			return EM_ASM_DOUBLE({
			  var self = Module['getCache'](Module['MyRayResultCallback'])[$0];
			  if (!self.hasOwnProperty('addSingleResult')) throw 'a JSImplementation must implement all functions, you forgot MyRayResultCallback::addSingleResult.';
			  return self['addSingleResult']($1,$2);
			}, (int)this, (int)&arg0, arg1);
		}
		void __destroy__() {
			EM_ASM_INT({
			  var self = Module['getCache'](Module['MyRayResultCallback'])[$0];
			  if (!self.hasOwnProperty('__destroy__')) throw 'a JSImplementation must implement all functions, you forgot MyRayResultCallback::__destroy__.';
			  self['__destroy__']();
			}, (int)this);
		}
};

class MyClosestRayResultCallback : public btCollisionWorld::ClosestRayResultCallback {
	public:
		MyClosestRayResultCallback(const btVector3&	rayFromWorld,const btVector3& rayToWorld) : btCollisionWorld::ClosestRayResultCallback(rayFromWorld, rayToWorld){}

		float addSingleResult(btCollisionWorld::LocalRayResult & arg0, bool arg1) {
			return EM_ASM_DOUBLE({
			  var self = Module['getCache'](Module['MyClosestRayResultCallback'])[$0];
			  if (!self.hasOwnProperty('addSingleResult')) throw 'a JSImplementation must implement all functions, you forgot MyClosestRayResultCallback::addSingleResult.';
			  return self['addSingleResult']($1,$2);
			}, (int)this, (int)&arg0, arg1);
		}
		void __destroy__() {
			EM_ASM_INT({
			  var self = Module['getCache'](Module['MyClosestRayResultCallback'])[$0];
			  if (!self.hasOwnProperty('__destroy__')) throw 'a JSImplementation must implement all functions, you forgot MyClosestRayResultCallback::__destroy__.';
			  self['__destroy__']();
			}, (int)this);
		}
		
		float addSingleResultSuper(btCollisionWorld::LocalRayResult & arg0, bool arg1) {
			return btCollisionWorld::ClosestRayResultCallback::addSingleResult(arg0, arg1);
		}
};

class MyAllHitsRayResultCallback : public btCollisionWorld::AllHitsRayResultCallback {
	public:
		MyAllHitsRayResultCallback(const btVector3&	rayFromWorld,const btVector3& rayToWorld) : btCollisionWorld::AllHitsRayResultCallback(rayFromWorld, rayToWorld){}

		float addSingleResult(btCollisionWorld::LocalRayResult & arg0, bool arg1) {
			return EM_ASM_DOUBLE({
			  var self = Module['getCache'](Module['MyAllHitsRayResultCallback'])[$0];
			  if (!self.hasOwnProperty('addSingleResult')) throw 'a JSImplementation must implement all functions, you forgot MyAllHitsRayResultCallback::addSingleResult.';
			  return self['addSingleResult']($1,$2);
			}, (int)this, (int)&arg0, arg1);
		}
		void __destroy__() {
			EM_ASM_INT({
			  var self = Module['getCache'](Module['MyAllHitsRayResultCallback'])[$0];
			  if (!self.hasOwnProperty('__destroy__')) throw 'a JSImplementation must implement all functions, you forgot MyAllHitsRayResultCallback::__destroy__.';
			  self['__destroy__']();
			}, (int)this);
		}
		
		float addSingleResultSuper(btCollisionWorld::LocalRayResult & arg0, bool arg1) {
			return btCollisionWorld::AllHitsRayResultCallback::addSingleResult(arg0, arg1);
		}
};

class MybtTriangleRaycastCallback : public btTriangleRaycastCallback {
	public:
		MybtTriangleRaycastCallback(const btVector3& from, const btVector3& to, unsigned int flags=0) : btTriangleRaycastCallback(from, to, flags){}

		float reportHit(const btVector3& arg0, btScalar arg1, int arg2, int arg3) {
			return EM_ASM_DOUBLE({
			  var self = Module['getCache'](Module['MybtTriangleRaycastCallback'])[$0];
			  if (!self.hasOwnProperty('reportHit')) throw 'a JSImplementation must implement all functions, you forgot MybtTriangleRaycastCallback::reportHit.';
			  return self['reportHit']($1,$2,$3,$4);
			}, (int)this, (int)&arg0, arg1, arg2, arg3);
		}
		void __destroy__() {
			EM_ASM_INT({
			  var self = Module['getCache'](Module['MybtTriangleRaycastCallback'])[$0];
			  if (!self.hasOwnProperty('__destroy__')) throw 'a JSImplementation must implement all functions, you forgot MybtTriangleRaycastCallback::__destroy__.';
			  self['__destroy__']();
			}, (int)this);
		}
};