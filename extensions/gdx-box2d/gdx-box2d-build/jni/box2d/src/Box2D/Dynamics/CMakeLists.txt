
INCLUDE_DIRECTORIES(
	${BOX2D_PHYSICS_SOURCE_DIR}/src
)

SET(Dynamics_SRCS
	b2Body.cpp
	b2ContactManager.cpp
	b2Fixture.cpp
	b2Island.cpp
	b2World.cpp
	b2WorldCallbacks.cpp
	Joints/b2DistanceJoint.cpp
	Joints/b2FrictionJoint.cpp
	Joints/b2GearJoint.cpp
	Joints/b2Joint.cpp
	Joints/b2MotorJoint.cpp
	Joints/b2MouseJoint.cpp
	Joints/b2PrismaticJoint.cpp
	Joints/b2PulleyJoint.cpp
	Joints/b2RevoluteJoint.cpp
	Joints/b2RopeJoint.cpp
	Joints/b2WeldJoint.cpp
	Joints/b2WheelJoint.cpp
	Contacts/b2ChainAndCircleContact.cpp
	Contacts/b2ChainAndPolygonContact.cpp
	Contacts/b2CircleContact.cpp
	Contacts/b2Contact.cpp
	Contacts/b2ContactSolver.cpp
	Contacts/b2EdgeAndCircleContact.cpp
	Contacts/b2EdgeAndPolygonContact.cpp
	Contacts/b2PolygonAndCircleContact.cpp
	Contacts/b2PolygonContact.cpp
)

SET(Dynamics_HDRS
	b2Body.h
	b2ContactManager.h
	b2Fixture.h
	b2Island.h
	b2TimeStep.h
	b2World.h
	b2WorldCallbacks.h
	Joints/b2DistanceJoint.h
	Joints/b2FrictionJoint.h
	Joints/b2GearJoint.h
	Joints/b2Joint.h
	Joints/b2MotorJoint.h
	Joints/b2MouseJoint.h
	Joints/b2PrismaticJoint.h
	Joints/b2PulleyJoint.h
	Joints/b2RevoluteJoint.h
	Joints/b2RopeJoint.h
	Joints/b2WeldJoint.h
	Joints/b2WheelJoint.h
	Contacts/b2ChainAndCircleContact.h
	Contacts/b2ChainAndPolygonContact.h
	Contacts/b2CircleContact.h
	Contacts/b2Contact.h
	Contacts/b2ContactSolver.h
	Contacts/b2EdgeAndCircleContact.h
	Contacts/b2EdgeAndPolygonContact.h
	Contacts/b2PolygonAndCircleContact.h
	Contacts/b2PolygonContact.h
)

ADD_LIBRARY(Dynamics ${Dynamics_SRCS} ${Dynamics_HDRS})
SET_TARGET_PROPERTIES(Dynamics PROPERTIES VERSION ${BOX2D_VERSION})
#SET_TARGET_PROPERTIES(Dynamics PROPERTIES SOVERSION ${BOX2D__VERSION})