package java.nio;

public abstract class XBuffer {

    /** <code>UNSET_MARK</code> means the mark has not been set. */
    final static int UNSET_MARK = -1;

    /** The capacity of this buffer, which never change. */
    final int capacity;

    /** <code>limit - 1</code> is the last element that can be read or written. Limit must be no less than zero and no greater than
     * <code>capacity</code>. */
    int limit;

    /** Mark is where position will be set when <code>reset()</code> is called. Mark is not set by default. Mark is always no less
     * than zero and no greater than <code>position</code>. */
    int mark = UNSET_MARK;

    /** The current position of this buffer. Position is always no less than zero and no greater than <code>limit</code>. */
    int position = 0;

    /** Construct a buffer with the specified capacity.
     *
     * @param capacity the capacity of this buffer. */
    XBuffer(int capacity) {
        super();
        if (capacity < 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = this.limit = capacity;
    }

    /** Returns the capacity of this buffer.
     *
     * @return the number of elements that are contained in this buffer.
     * @since Android 1.0 */
    public final int capacity () {
        return capacity;
    }

    /** Clears this buffer.
     * <p>
     * While the content of this buffer is not changed, the following internal changes take place: the current position is reset
     * back to the start of the buffer, the value of the buffer limit is made equal to the capacity and mark is cleared.
     * </p>
     *
     * @return this buffer.
     * @since Android 1.0 */
    public final XBuffer clear () {
        position = 0;
        mark = UNSET_MARK;
        limit = capacity;
        return this;
    }

    /** Flips this buffer.
     * <p>
     * The limit is set to the current position, then the position is set to zero, and the mark is cleared.
     * </p>
     * <p>
     * The content of this buffer is not changed.
     * </p>
     *
     * @return this buffer.
     * @since Android 1.0 */
    public final XBuffer flip () {
        limit = position;
        position = 0;
        mark = UNSET_MARK;
        return this;
    }

    /** Indicates if there are elements remaining in this buffer, that is if {@code position < limit}.
     *
     * @return {@code true} if there are elements remaining in this buffer, {@code false} otherwise.
     * @since Android 1.0 */
    public final boolean hasRemaining () {
        return position < limit;
    }

    /** Indicates whether this buffer is read-only.
     *
     * @return {@code true} if this buffer is read-only, {@code false} otherwise.
     * @since Android 1.0 */
    public abstract boolean isReadOnly ();

    /** Returns the limit of this buffer.
     *
     * @return the limit of this buffer.
     * @since Android 1.0 */
    public final int limit () {
        return limit;
    }

    /** Sets the limit of this buffer.
     * <p>
     * If the current position in the buffer is in excess of <code>newLimit</code> then, on returning from this call, it will have
     * been adjusted to be equivalent to <code>newLimit</code>. If the mark is set and is greater than the new limit, then it is
     * cleared.
     * </p>
     *
     * @param newLimit the new limit, must not be negative and not greater than capacity.
     * @return this buffer.
     * @exception IllegalArgumentException if <code>newLimit</code> is invalid.
     * @since Android 1.0 */
    public final XBuffer limit (int newLimit) {
        if (newLimit < 0 || newLimit > capacity) {
            throw new IllegalArgumentException();
        }

        limit = newLimit;
        if (position > newLimit) {
            position = newLimit;
        }
        if ((mark != UNSET_MARK) && (mark > newLimit)) {
            mark = UNSET_MARK;
        }
        return this;
    }

    /** Marks the current position, so that the position may return to this point later by calling <code>reset()</code>.
     *
     * @return this buffer.
     * @since Android 1.0 */
    public final XBuffer mark () {
        mark = position;
        return this;
    }

    /** Returns the position of this buffer.
     *
     * @return the value of this buffer's current position.
     * @since Android 1.0 */
    public final int position () {
        return position;
    }

    /** Sets the position of this buffer.
     * <p>
     * If the mark is set and it is greater than the new position, then it is cleared.
     * </p>
     *
     * @param newPosition the new position, must be not negative and not greater than limit.
     * @return this buffer.
     * @exception IllegalArgumentException if <code>newPosition</code> is invalid.
     * @since Android 1.0 */
    public final XBuffer position (int newPosition) {
        if (newPosition < 0 || newPosition > limit) {
            throw new IllegalArgumentException();
        }

        position = newPosition;
        if ((mark != UNSET_MARK) && (mark > position)) {
            mark = UNSET_MARK;
        }
        return this;
    }

    /** Returns the number of remaining elements in this buffer, that is {@code limit - position}.
     *
     * @return the number of remaining elements in this buffer.
     * @since Android 1.0 */
    public final int remaining () {
        return limit - position;
    }

    /** Resets the position of this buffer to the <code>mark</code>.
     *
     * @return this buffer.
     * @exception InvalidMarkException if the mark is not set.
     * @since Android 1.0 */
    public final XBuffer reset () {
        if (mark == UNSET_MARK) {
            throw new InvalidMarkException();
        }
        position = mark;
        return this;
    }

    /** Rewinds this buffer.
     * <p>
     * The position is set to zero, and the mark is cleared. The content of this buffer is not changed.
     * </p>
     *
     * @return this buffer.
     * @since Android 1.0 */
    public final XBuffer rewind () {
        position = 0;
        mark = UNSET_MARK;
        return this;
    }
}