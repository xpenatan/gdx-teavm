
INCLUDE_DIRECTORIES(
${BULLET_PHYSICS_SOURCE_DIR}/src
	
)

#SUBDIRS( Solvers )

SET(BulletSoftBody_SRCS
	btDefaultSoftBodySolver.cpp
	btSoftBody.cpp
	btSoftBodyConcaveCollisionAlgorithm.cpp
	btSoftBodyHelpers.cpp
	btSoftBodyRigidBodyCollisionConfiguration.cpp
	btSoftRigidCollisionAlgorithm.cpp
	btSoftRigidDynamicsWorld.cpp
	btSoftSoftCollisionAlgorithm.cpp
)

SET(BulletSoftBody_HDRS
	btDefaultSoftBodySolver.h
	btSoftBody.h
	btSoftBodyConcaveCollisionAlgorithm.h
	btSoftBodyData.h
	btSoftBodyHelpers.h
	btSoftBodyInternals.h
	btSoftBodyRigidBodyCollisionConfiguration.h
	btSoftBodySolvers.h
	btSoftBodySolverVertexBuffer.h
	btSoftRigidCollisionAlgorithm.h
	btSoftRigidDynamicsWorld.h
	btSoftSoftCollisionAlgorithm.h
	btSparseSDF.h
)

ADD_LIBRARY(BulletSoftBody  ${BulletSoftBody_SRCS} ${BulletSoftBody_HDRS})