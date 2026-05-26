package emu.com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/** Executes a number of actions at the same time. */
public class TParallelAction extends Action {
    Array<Action> actions = new Array<Action>(4);
    private boolean complete;

    public TParallelAction() {
    }

    public TParallelAction(Action action1) {
        addAction(action1);
    }

    public TParallelAction(Action action1, Action action2) {
        addAction(action1);
        addAction(action2);
    }

    public TParallelAction(Action action1, Action action2, Action action3) {
        addAction(action1);
        addAction(action2);
        addAction(action3);
    }

    public TParallelAction(Action action1, Action action2, Action action3, Action action4) {
        addAction(action1);
        addAction(action2);
        addAction(action3);
        addAction(action4);
    }

    public TParallelAction(Action action1, Action action2, Action action3, Action action4, Action action5) {
        addAction(action1);
        addAction(action2);
        addAction(action3);
        addAction(action4);
        addAction(action5);
    }

    public boolean act(float delta) {
        if(complete) {
            return true;
        }
        complete = true;
        Pool pool = getPool();
        setPool(null);
        try {
            return actActions(delta);
        }
        finally {
            setPool(pool);
        }
    }

    private boolean actActions(float delta) {
        Array<Action> localActions = actions;
        return actActionAt(localActions, 0, localActions.size, delta);
    }

    private boolean actActionAt(Array<Action> localActions, int index, int count, float delta) {
        if(index >= count || actor == null) {
            return complete;
        }
        Action currentAction = localActions.get(index);
        if(currentAction.getActor() != null && !currentAction.act(delta)) {
            complete = false;
        }
        if(actor == null) {
            return true;
        }
        return actActionAt(localActions, index + 1, count, delta);
    }

    public void restart() {
        complete = false;
        Array<Action> localActions = actions;
        for(int i = 0, n = localActions.size; i < n; i++) {
            localActions.get(i).restart();
        }
    }

    public void reset() {
        super.reset();
        actions.clear();
    }

    public void addAction(Action action) {
        actions.add(action);
        if(actor != null) {
            action.setActor(actor);
        }
    }

    public void setActor(Actor actor) {
        Array<Action> localActions = actions;
        for(int i = 0, n = localActions.size; i < n; i++) {
            localActions.get(i).setActor(actor);
        }
        super.setActor(actor);
    }

    public Array<Action> getActions() {
        return actions;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder(64);
        buffer.append(super.toString());
        buffer.append('(');
        Array<Action> localActions = actions;
        for(int i = 0, n = localActions.size; i < n; i++) {
            if(i > 0) {
                buffer.append(", ");
            }
            buffer.append(localActions.get(i));
        }
        buffer.append(')');
        return buffer.toString();
    }
}
