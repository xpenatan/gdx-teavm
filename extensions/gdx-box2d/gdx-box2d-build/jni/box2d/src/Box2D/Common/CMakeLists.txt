
INCLUDE_DIRECTORIES(
	${BOX2D_PHYSICS_SOURCE_DIR}/src
)

SET(Common_SRCS
	b2BlockAllocator.cpp
	b2Draw.cpp
	b2Math.cpp
	b2Settings.cpp
	b2StackAllocator.cpp
	b2Timer.cpp
)

SET(Common_HDRS
	b2BlockAllocator.h
	b2Draw.h
	b2GrowableStack.h
	b2Math.h
	b2Settings.h
	b2StackAllocator.h
	b2Timer.h
)

ADD_LIBRARY(Common ${Common_SRCS} ${Common_HDRS})
SET_TARGET_PROPERTIES(Common PROPERTIES VERSION ${BOX2D_VERSION})
#SET_TARGET_PROPERTIES(Common PROPERTIES SOVERSION ${BOX2D__VERSION})