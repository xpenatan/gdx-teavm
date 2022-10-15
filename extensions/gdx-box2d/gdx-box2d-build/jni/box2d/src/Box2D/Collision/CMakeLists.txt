
INCLUDE_DIRECTORIES(
	${BOX2D_PHYSICS_SOURCE_DIR}/src
)

SET(Collision_SRCS
	b2BroadPhase.cpp
	b2CollideCircle.cpp
	b2CollideEdge.cpp
	b2CollidePolygon.cpp
	b2Collision.cpp
	b2Distance.cpp
	b2DynamicTree.cpp
	b2TimeOfImpact.cpp
	Shapes/b2ChainShape.cpp
	Shapes/b2CircleShape.cpp
	Shapes/b2EdgeShape.cpp
	Shapes/b2PolygonShape.cpp
)

SET(Collision_HDRS
	b2TimeOfImpact.h
	b2DynamicTree.h
	b2Distance.h
	b2Collision.h
	b2BroadPhase.h
	Shapes/b2ChainShape.h
	Shapes/b2CircleShape.h
	Shapes/b2EdgeShape.h
	Shapes/b2PolygonShape.h
	Shapes/b2Shape.h
)

ADD_LIBRARY(Collision ${Collision_SRCS} ${Collision_HDRS})
SET_TARGET_PROPERTIES(Collision PROPERTIES VERSION ${BOX2D_VERSION})
#SET_TARGET_PROPERTIES(Collision PROPERTIES SOVERSION ${BOX2D_VERSION})