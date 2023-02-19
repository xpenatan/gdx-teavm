package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import com.badlogic.gdx.utils.Array;

/**
 * @author xpenatan
 */
public class btBvhTriangleMeshShape extends btTriangleMeshShape {
    protected final static Array<btBvhTriangleMeshShape> instances = new Array<btBvhTriangleMeshShape>();

    protected static <T extends MeshPart> btBvhTriangleMeshShape getInstance(final Array<T> meshParts) {
        for(final btBvhTriangleMeshShape instance : instances) {
            if(instance.meshInterface instanceof btTriangleIndexVertexArray
                    && btTriangleIndexVertexArray.compare((btTriangleIndexVertexArray)(instance.meshInterface), meshParts))
                return instance;
        }
        return null;
    }

    public static <T extends MeshPart> btBvhTriangleMeshShape obtain(final Array<T> meshParts) {
        btBvhTriangleMeshShape result = getInstance(meshParts);
        if(result == null) {
            result = new btBvhTriangleMeshShape(btTriangleIndexVertexArray.obtain(meshParts), true);
            instances.add(result);
        }
        result.obtain();
        return result;
    }

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    public btBvhTriangleMeshShape(btStridingMeshInterface meshInterface, boolean useQuantizedAabbCompression) {
        initObject(createNative(meshInterface.getCPointer(), useQuantizedAabbCompression), true);
    }

    private btStridingMeshInterface meshInterface = null;

    /**
     * @return The {@link btStridingMeshInterface} this shape encapsulates.
     */
    public btStridingMeshInterface getMeshInterface() {
        return meshInterface;
    }

    /*[-C++;-NATIVE]
        return (jlong)new btBvhTriangleMeshShape((btStridingMeshInterface*)meshInterfaceAddr, useQuantizedAabbCompression);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btBvhTriangleMeshShape(meshInterfaceAddr, useQuantizedAabbCompression);
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative(long meshInterfaceAddr, boolean useQuantizedAabbCompression);

    /*[-C++;-NATIVE]
        delete (btBvhTriangleMeshShape*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btBvhTriangleMeshShape);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);

    public void performRaycast(btTriangleCallback callback, btVector3 raySource, btVector3 rayTarget) {
        performRaycastNATIVE(cPointer, (int) callback.getCPointer(), (int) raySource.getCPointer(), (int) rayTarget.getCPointer());
    }

    //TODO Improve generator pointer casting

    /*[-C++;-NATIVE]
        btBvhTriangleMeshShape* nativeObject = (btBvhTriangleMeshShape*)addr;
        btVector3 * raySource = (btVector3*)raySourceAddr;
        btVector3 * rayTarget = (btVector3*)rayTargetAddr;
        btTriangleCallback * callback = (btTriangleCallback*)callbackAddr;
        nativeObject->performRaycast(callback, *raySource, *rayTarget);
    */
    private static native void performRaycastNATIVE(long addr, long callbackAddr, long raySourceAddr, long rayTargetAddr);

}