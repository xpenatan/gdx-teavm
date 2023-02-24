package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.utils.Clipboard;
import org.teavm.jso.JSBody;

/**
 * Clipboard implementation copied over from libGDX/GWT. Paste only works within the libGDX application.
 */
public class TeaClipboard implements Clipboard {

  private boolean requestedWritePermissions = false;
  private boolean hasWritePermissions = true;

  private final ClipboardWriteHandler writeHandler = new ClipboardWriteHandler();

  private String content = "";

  @Override
  public boolean hasContents () {
    String contents = getContents();
    return contents != null && !contents.isEmpty();
  }

  @Override
  public String getContents () {
    return content;
  }

  @Override
  public void setContents (String content) {
    this.content = content;
    if (requestedWritePermissions || TeaApplication.getAgentInfo().isFirefox()) {
      if (hasWritePermissions) setContentNATIVE(content);
    } else {
      TeaPermissions.queryPermission("clipboard-write", writeHandler);
      requestedWritePermissions = true;
    }
  }

  @JSBody(params = {"content"}, script =
          "if (\"clipboard\" in navigator) {\n" +
          "    navigator.clipboard.writeText(content);\n" +
          "}")
  private static native void setContentNATIVE(String content);

  private class ClipboardWriteHandler implements TeaPermissions.TeaPermissionResult {
    @Override
    public void granted () {
      hasWritePermissions = true;
      setContentNATIVE(content);
    }

    @Override
    public void denied () {
      hasWritePermissions = false;
    }

    @Override
    public void prompt () {
      hasWritePermissions = true;
      setContentNATIVE(content);
    }
  }
}
