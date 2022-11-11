package com.badlogic.gdx.physics.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author xpenatan
 */
public class BulletBase implements Disposable {

    /**
     * Low level usage. Don't change the pointer.
     */
    public long cPointer;
    private boolean cMemOwn;
    private boolean disposed;

	/*[0;X;F]
		public Object jsObj; // cached obj
	*/

    /**
     * Do not call directly. Called when object is owned and dispose is called.
     */
    protected void delete() {
    }
	/*[0;X;L]
		jsObj, this.jsObj #P
		Bullet.destroy(jsObj);
	*/

    /**
     * Internal use. Can cause memory leak if not used correctly.
     */
    public final void resetObj(long cPtr, boolean cMemoryOwn) {
        cMemOwn = cMemoryOwn;
        cPointer = cPtr;
        disposed = false;
    }
	/*[0;X;L]
		cMemOwn = cMemoryOwn; #J
		cPointer = cPtr; #J
		disposed = false; #J
		if(cPtr == 0) { #J
			this.jsObj = null; #J
		} #J
		else { #J
			cacheObj(); #J
		} #J
	*/

	/*[0;X;F;L]
		protected void cacheObj() {
			jsObj, this.jsObj #P

		}
	*/

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BulletBase) && (((BulletBase)obj).cPointer == this.cPointer);
    }

    @Override
    public int hashCode() {
        return (int)cPointer;
    }

    /**
     * Take ownership of the native instance, causing the native object to be deleted when this object gets out of scope or calling dispose.
     * Only call it if you know what your doing.
     */
    public void takeOwnership() {
        cMemOwn = true;
    }

    /**
     * Release ownership of the native instance, causing the native object NOT to be deleted when this object gets out of scope or calling dispose.
     * Only call it if you know what your doing.
     */
    public void releaseOwnership() {
        cMemOwn = false;
    }

    public boolean hasOwnership() {
        return cMemOwn;
    }

    @Override
    final public void dispose() {
        if(cPointer != 0 && cMemOwn) {
            if(disposed) {
                if(Bullet.enableLogging && Gdx.app != null)
                    Gdx.app.error("Bullet", "Already disposed " + toString());
            }
            else {
                if(Bullet.enableLogging && Gdx.app != null)
                    Gdx.app.debug("Bullet", "Disposing " + toString());
                destroy();
            }
        }
    }

    private void destroy() {
        try {
            delete();
            disposed = true;
            cPointer = 0;
        }
        catch(Throwable e) {
            if(Gdx.app != null)
                Gdx.app.error("Bullet", "Exception while destroying " + toString(), e);
        }
    }
	/*[0;X;L]
		try { #J
			delete(); #J
			disposed = true; #J
			cPointer = 0; #J
			jsObj = null; #J
		} #J
		catch (Throwable e) { #J
			if(Gdx.app != null) #J
				Gdx.app.error("Bullet", "Exception while destroying " + toString(), e); #J
		} #J
	*/

    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + cPointer + "," + cMemOwn + ")";
    }

    @Override
    protected void finalize() throws Throwable {
        if(cPointer != 0 && cMemOwn && disposed == false) {

            if(Bullet.enableLogging) {
                String debugText = "Disposing " + toString() + " due to garbage collection. Memmory Leak Warning. Call Dispose instead.";
                if(Gdx.app != null)
                    Gdx.app.error("Bullet", debugText);
                else
                    System.out.println("Bullet - " + debugText);
            }
            destroy();
        }
        super.finalize();
    }

    /**
     * Internal use
     */
    public void checkPointer() {
        if(cPointer == 0)
            throw new NullPointerException("Tried to access native object with null pointer " + getClass().getSimpleName());
    }
}