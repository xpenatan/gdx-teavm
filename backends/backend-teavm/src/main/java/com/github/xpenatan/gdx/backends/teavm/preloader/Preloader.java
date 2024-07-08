package com.github.xpenatan.gdx.backends.teavm.preloader;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.github.xpenatan.gdx.backends.teavm.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaFileHandle;
import com.github.xpenatan.gdx.backends.teavm.dom.DataTransferWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.DragEventWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.EventListenerWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.EventWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.FileListWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.FileReaderWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.FileWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.backends.teavm.filesystem.FileData;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSArrayReader;
import org.teavm.jso.core.JSPromise;

/**
 * @author xpenatan
 */
public class Preloader {
    public int assetTotal = -1;

    private static final String ASSET_FOLDER = "assets/";
    private static final String SCRIPTS_FOLDER = "scripts/";

    public final String baseUrl;

    private HashSet<String> assetInQueue;

    public Preloader(String newBaseURL, HTMLCanvasElementWrapper canvas, TeaApplication teaApplication) {
        baseUrl = newBaseURL;
        assetInQueue = new HashSet<>();
        setupFileDrop(canvas, teaApplication);
    }

    private void setupFileDrop(HTMLCanvasElementWrapper canvas, TeaApplication teaApplication) {
        TeaApplicationConfiguration config = teaApplication.getConfig();
        if(config.windowListener != null) {
            HTMLDocumentWrapper document = canvas.getOwnerDocument();
            document.addEventListener("dragenter", new EventListenerWrapper() {
                @Override
                public void handleEvent(EventWrapper evt) {
                    evt.preventDefault();
                }
            }, false);
            document.addEventListener("dragover", new EventListenerWrapper() {
                @Override
                public void handleEvent(EventWrapper evt) {
                    evt.preventDefault();
                }
            }, false);
            document.addEventListener("drop", new EventListenerWrapper() {
                @Override
                public void handleEvent(EventWrapper evt) {
                    evt.preventDefault();
                    DragEventWrapper event = (DragEventWrapper)evt;
                    DataTransferWrapper dataTransfer = event.getDataTransfer();
                    FileListWrapper files = dataTransfer.getFiles();
                    downloadDroppedFile(config, files);
                }
            });
        }
    }

    private JSPromise<FileData> getFile(String name, FileWrapper fileWrapper) {
        JSPromise<FileData> success = new JSPromise<>((resolve, reject) -> {
            FileReaderWrapper fileReader = FileReaderWrapper.create();
            fileReader.readAsArrayBuffer(fileWrapper);

            fileReader.addEventListener("load", new EventListenerWrapper() {
                @Override
                public void handleEvent(EventWrapper evt) {
                    FileReaderWrapper target = (FileReaderWrapper)evt.getTarget();
                    ArrayBufferWrapper arrayBuffer = target.getResultAsArrayBuffer();
                    Int8ArrayWrapper data = TypedArrays.createInt8Array(arrayBuffer);
                    byte[] bytes = TypedArrays.toByteArray(data);
                    FileData fielData = new FileData(name, bytes);
                    resolve.accept(fielData);
                }
            });
        });

        return success;
    }

    private void downloadDroppedFile(TeaApplicationConfiguration config, FileListWrapper files) {
        int totalDraggedFiles = files.getLength();
        if(totalDraggedFiles > 0) {
            Array<String> droppedFiles = new Array<>();
            var promises = new JSArray<JSPromise<FileData>>();
            for(int i = 0; i < totalDraggedFiles; i++) {
                FileWrapper fileWrapper = files.get(i);
                String name = fileWrapper.getName();

                if(config.windowListener.acceptFileDropped(name)) {
                    JSPromise<FileData> promiss = getFile(name, fileWrapper);
                    promises.push(promiss);
                }
            }

            JSPromise<JSArrayReader<FileData>> all = JSPromise.all(promises);
            all.then(array -> {
                int length = array.getLength();
                FileData [] arr = new FileData[length];
                for(int i = 0; i < length; i++) {
                    FileData fileData = array.get(i);
                    arr[i] = fileData;
                }
                config.windowListener.filesDropped(arr);
                return "success";
            }, reason -> {
                return "failure";
            }).onSettled(() -> {
                return null;
            });
        }
    }

    private String getAssetUrl() {
        return baseUrl + ASSET_FOLDER;
    }

    private String getScriptUrl() {
        return baseUrl + SCRIPTS_FOLDER;
    }

    public void preload(TeaApplicationConfiguration config, final String assetFileUrl) {
        AssetLoaderListener<Blob> listener = new AssetLoaderListener<>() {
            @Override
            public void onSuccess(String url, Blob result) {
                Int8ArrayWrapper data = result.getData();
                byte[] byteArray = TypedArrays.toByteArray(data);
                String assets = new String(byteArray);
                String[] lines = assets.split("\n");

                assetTotal = lines.length;

                for(String line : lines) {
                    String[] tokens = line.split(":");
                    if(tokens.length != 4) {
                        throw new GdxRuntimeException("Invalid assets description file.");
                    }

                    String assetUrl = tokens[2].trim();
                    assetUrl = assetUrl.trim();
                    if(assetUrl.isEmpty()) {
                        continue;
                    }
                    String fileTypeStr = tokens[0];
                    String assetTypeStr = tokens[1];

                    FileType fileType = FileType.Internal;
                    if(fileTypeStr.equals("c")) {
                        fileType = FileType.Classpath;
                    }
                    else if(fileTypeStr.equals("l")) {
                        fileType = FileType.Local;
                    }
                    AssetType assetType = AssetType.Binary;
                    if(assetTypeStr.equals("d")) assetType = AssetType.Directory;

                    loadAsset(assetType, fileType, assetUrl);
                }
            }

            @Override
            public void onFailure(String url) {
                System.out.println("ErrorLoading: " + assetFileUrl);
            }
        };

        AssetDownloader.getInstance().load(true, getAssetUrl() + assetFileUrl, AssetType.Binary, listener);
    }

    public boolean isAssetInQueue(String path) {
        String path1 = fixPath(path);
        return assetInQueue.contains(path1);
    }

    public boolean isAssetLoaded(FileType fileType, String path) {
        String path1 = fixPath(path);
        FileHandle fileHandle = Gdx.files.getFileHandle(path1, fileType);
        return fileHandle.exists();
    }

    public void loadAsset(AssetType assetType, FileType fileType, String path) {
        String path1 = fixPath(path);

        if(path1.isEmpty()) {
            return;
        }

        if(assetInQueue.contains(path1)) {
            return;
        }

        FileHandle fileHandle = Gdx.files.getFileHandle(path1, fileType);
        if(fileHandle.exists()) {
            // File already exist, don't download it again.
            return;
        }

        assetInQueue.add(path1);
        AssetDownloader.getInstance().load(true, getAssetUrl() + path1, AssetType.Binary, new AssetLoaderListener<>() {
            @Override
            public void onProgress(double amount) {
            }

            @Override
            public void onFailure(String url) {
                assetInQueue.remove(path1);
            }

            @Override
            public void onSuccess(String url, Object result) {
                assetInQueue.remove(path1);
                Blob blob = (Blob)result;
                AssetType type = AssetType.Binary;
                TeaFileHandle internalFile = (TeaFileHandle)Gdx.files.getFileHandle(path1, fileType);
                if(assetType == AssetType.Directory) {
                    internalFile.mkdirsInternal();
                }
                else {
                    Int8ArrayWrapper data = blob.getData();
                    byte[] byteArray = TypedArrays.toByteArray(data);
                    OutputStream output = internalFile.write(false, 4096);
                    try {
                        output.write(byteArray);
                    }
                    catch(IOException ex) {
                        throw new GdxRuntimeException("Error writing file: " + internalFile + " (" + internalFile.type() + ")", ex);
                    }
                    finally {
                        StreamUtils.closeQuietly(output);
                    }
                }
            }
        });
    }

    public void loadScript(boolean async, final String url, final AssetLoaderListener<Object> listener) {
        AssetDownloader.getInstance().loadScript(async, getScriptUrl() + url, listener);
    }

    public int getQueue() {
        return AssetDownloader.getInstance().getQueue();
    }

    private String fixPath(String path1) {
        path1 = path1.trim().replace("\\", "/");
        if(path1.startsWith("/")) {
            path1 = path1.substring(1);
        }
        return path1;
    }
}