package com.github.xpenatan.gdx.backends.web;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.indexeddb.*;

import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Indexed DB to handle local files (load and save).
 *
 * @author noblemaster
 */
public final class WebFileHandleDBIndexed extends WebFileHandleDB {

  /** Our database with the files. */
  private IDBDatabase db;
  /** The table with the files. */
  private IDBObjectStore dbFiles;


  WebFileHandleDBIndexed() {
    // create the database
    int dbVersion = 1;
    final IDBOpenDBRequest openRequest = IDBFactory.getInstance().open("file-system", dbVersion);

    openRequest.setOnBlocked(new EventHandler() {
      @Override
      public void handleEvent() {
        Gdx.app.error("DB", "Open DB blocked.");
      }
    });
    openRequest.setOnUpgradeNeeded(new EventListener<IDBVersionChangeEvent>() {
      @Override
      public void handleEvent(IDBVersionChangeEvent idbVersionChangeEvent) {
        Gdx.app.debug("FileDB", "DB.setOnUpgradeNeeded.");
        db = openRequest.getResult();
        db.setOnError(new EventHandler() {
          @Override
          public void handleEvent() {
            Gdx.app.error("FileDB", "DB error.");
          }
        });
        db.setOnAbort(new EventHandler() {
          @Override
          public void handleEvent() {
            Gdx.app.error("FileDB", "DB abort.");
          }
        });

        dbFiles = db.createObjectStore("files", IDBObjectStoreParameters.create().keyPath("name"));
        dbFiles.createIndex("content", "content");
      }
    });
  }

  @Override
  public InputStream read(WebFileHandle file) {
    return null;
  }

  @Override
  public OutputStream write(WebFileHandle file, boolean append) {
    return null;
  }

  @Override
  public FileHandle[] list(WebFileHandle file) {
    return new FileHandle[0];
  }

  @Override
  public FileHandle[] list(WebFileHandle file, FileFilter filter) {
    return new FileHandle[0];
  }

  @Override
  public FileHandle[] list(WebFileHandle file, FilenameFilter filter) {
    return new FileHandle[0];
  }

  @Override
  public FileHandle[] list(WebFileHandle file, String suffix) {
    return new FileHandle[0];
  }

  @Override
  public boolean isDirectory(WebFileHandle file) {
    return false;
  }

  @Override
  public void mkdirs(WebFileHandle file) {

  }

  @Override
  public boolean exists(WebFileHandle file) {
    return false;
  }

  @Override
  public boolean delete(WebFileHandle file) {
    return false;
  }

  @Override
  public long length(WebFileHandle file) {
    return 0;
  }
}
