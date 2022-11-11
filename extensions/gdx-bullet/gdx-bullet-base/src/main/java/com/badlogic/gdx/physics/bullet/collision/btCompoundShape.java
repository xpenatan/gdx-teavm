package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;

/**
 * @author xpenatan
 */
public class btCompoundShape extends btCollisionShape {

    public btCompoundShape() {
        initObject(createNative(), true);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btCompoundShape();
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCompoundShape);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);

    public void addChildShape(Matrix4 localTransform, btCollisionShape shape) {
        // Required. it's called from Bullet class
        btTransform.convert(localTransform, btTransform.TEMP_0);
        btTransform btLocalTransform = btTransform.TEMP_0;
        addChildShapeNATIVE((int)cPointer, (int)btLocalTransform.getCPointer(), (int)shape.getCPointer());
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCompoundShape);
        jsObj.addChildShape(localTransformAddr, shapeAddr);
     */
    private static native void addChildShapeNATIVE(long addr, long localTransformAddr, long shapeAddr);
}