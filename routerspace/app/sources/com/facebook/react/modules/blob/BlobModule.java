package com.facebook.react.modules.blob;

import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import androidx.autofill.HintConstants;
import com.facebook.common.util.UriUtil;
import com.facebook.fbreact.specs.NativeBlobModuleSpec;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.network.NetworkingModule;
import com.facebook.react.modules.websocket.WebSocketModule;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.ByteString;
@ReactModule(name = BlobModule.NAME)
/* loaded from: classes.dex */
public class BlobModule extends NativeBlobModuleSpec {
    public static final String NAME = "BlobModule";
    private final Map<String, byte[]> mBlobs = new HashMap();
    private final WebSocketModule.ContentHandler mWebSocketContentHandler = new WebSocketModule.ContentHandler() { // from class: com.facebook.react.modules.blob.BlobModule.1
        @Override // com.facebook.react.modules.websocket.WebSocketModule.ContentHandler
        public void onMessage(String text, WritableMap params) {
            params.putString(UriUtil.DATA_SCHEME, text);
        }

        @Override // com.facebook.react.modules.websocket.WebSocketModule.ContentHandler
        public void onMessage(ByteString bytes, WritableMap params) {
            byte[] byteArray = bytes.toByteArray();
            WritableMap createMap = Arguments.createMap();
            createMap.putString("blobId", BlobModule.this.store(byteArray));
            createMap.putInt("offset", 0);
            createMap.putInt("size", byteArray.length);
            params.putMap(UriUtil.DATA_SCHEME, createMap);
            params.putString("type", "blob");
        }
    };
    private final NetworkingModule.UriHandler mNetworkingUriHandler = new NetworkingModule.UriHandler() { // from class: com.facebook.react.modules.blob.BlobModule.2
        @Override // com.facebook.react.modules.network.NetworkingModule.UriHandler
        public boolean supports(Uri uri, String responseType) {
            String scheme = uri.getScheme();
            return !(UriUtil.HTTP_SCHEME.equals(scheme) || UriUtil.HTTPS_SCHEME.equals(scheme)) && "blob".equals(responseType);
        }

        @Override // com.facebook.react.modules.network.NetworkingModule.UriHandler
        public WritableMap fetch(Uri uri) throws IOException {
            byte[] bytesFromUri = BlobModule.this.getBytesFromUri(uri);
            WritableMap createMap = Arguments.createMap();
            createMap.putString("blobId", BlobModule.this.store(bytesFromUri));
            createMap.putInt("offset", 0);
            createMap.putInt("size", bytesFromUri.length);
            createMap.putString("type", BlobModule.this.getMimeTypeFromUri(uri));
            createMap.putString(HintConstants.AUTOFILL_HINT_NAME, BlobModule.this.getNameFromUri(uri));
            createMap.putDouble("lastModified", BlobModule.this.getLastModifiedFromUri(uri));
            return createMap;
        }
    };
    private final NetworkingModule.RequestBodyHandler mNetworkingRequestBodyHandler = new NetworkingModule.RequestBodyHandler() { // from class: com.facebook.react.modules.blob.BlobModule.3
        @Override // com.facebook.react.modules.network.NetworkingModule.RequestBodyHandler
        public boolean supports(ReadableMap data) {
            return data.hasKey("blob");
        }

        @Override // com.facebook.react.modules.network.NetworkingModule.RequestBodyHandler
        public RequestBody toRequestBody(ReadableMap data, String contentType) {
            if (data.hasKey("type") && !data.getString("type").isEmpty()) {
                contentType = data.getString("type");
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            ReadableMap map = data.getMap("blob");
            return RequestBody.create(MediaType.parse(contentType), BlobModule.this.resolve(map.getString("blobId"), map.getInt("offset"), map.getInt("size")));
        }
    };
    private final NetworkingModule.ResponseHandler mNetworkingResponseHandler = new NetworkingModule.ResponseHandler() { // from class: com.facebook.react.modules.blob.BlobModule.4
        @Override // com.facebook.react.modules.network.NetworkingModule.ResponseHandler
        public boolean supports(String responseType) {
            return "blob".equals(responseType);
        }

        @Override // com.facebook.react.modules.network.NetworkingModule.ResponseHandler
        public WritableMap toResponseData(ResponseBody body) throws IOException {
            byte[] bytes = body.bytes();
            WritableMap createMap = Arguments.createMap();
            createMap.putString("blobId", BlobModule.this.store(bytes));
            createMap.putInt("offset", 0);
            createMap.putInt("size", bytes.length);
            return createMap;
        }
    };

    @Override // com.facebook.react.bridge.NativeModule
    public String getName() {
        return NAME;
    }

    public BlobModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override // com.facebook.react.bridge.BaseJavaModule, com.facebook.react.bridge.NativeModule, com.facebook.react.turbomodule.core.interfaces.TurboModule
    public void initialize() {
        BlobCollector.install(getReactApplicationContext(), this);
    }

    @Override // com.facebook.fbreact.specs.NativeBlobModuleSpec
    public Map<String, Object> getTypedExportedConstants() {
        Resources resources = getReactApplicationContext().getResources();
        int identifier = resources.getIdentifier("blob_provider_authority", "string", getReactApplicationContext().getPackageName());
        if (identifier == 0) {
            return MapBuilder.of();
        }
        return MapBuilder.of("BLOB_URI_SCHEME", UriUtil.LOCAL_CONTENT_SCHEME, "BLOB_URI_HOST", resources.getString(identifier));
    }

    public String store(byte[] data) {
        String uuid = UUID.randomUUID().toString();
        store(data, uuid);
        return uuid;
    }

    public void store(byte[] data, String blobId) {
        synchronized (this.mBlobs) {
            this.mBlobs.put(blobId, data);
        }
    }

    public void remove(String blobId) {
        synchronized (this.mBlobs) {
            this.mBlobs.remove(blobId);
        }
    }

    public byte[] resolve(Uri uri) {
        String lastPathSegment = uri.getLastPathSegment();
        String queryParameter = uri.getQueryParameter("offset");
        int parseInt = queryParameter != null ? Integer.parseInt(queryParameter, 10) : 0;
        String queryParameter2 = uri.getQueryParameter("size");
        return resolve(lastPathSegment, parseInt, queryParameter2 != null ? Integer.parseInt(queryParameter2, 10) : -1);
    }

    public byte[] resolve(String blobId, int offset, int size) {
        synchronized (this.mBlobs) {
            byte[] bArr = this.mBlobs.get(blobId);
            if (bArr == null) {
                return null;
            }
            if (size == -1) {
                size = bArr.length - offset;
            }
            if (offset > 0 || size != bArr.length) {
                bArr = Arrays.copyOfRange(bArr, offset, size + offset);
            }
            return bArr;
        }
    }

    public byte[] resolve(ReadableMap blob) {
        return resolve(blob.getString("blobId"), blob.getInt("offset"), blob.getInt("size"));
    }

    public byte[] getBytesFromUri(Uri contentUri) throws IOException {
        InputStream openInputStream = getReactApplicationContext().getContentResolver().openInputStream(contentUri);
        if (openInputStream == null) {
            throw new FileNotFoundException("File not found for " + contentUri);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            int read = openInputStream.read(bArr);
            if (read != -1) {
                byteArrayOutputStream.write(bArr, 0, read);
            } else {
                return byteArrayOutputStream.toByteArray();
            }
        }
    }

    public String getNameFromUri(Uri contentUri) {
        if (UriUtil.LOCAL_FILE_SCHEME.equals(contentUri.getScheme())) {
            return contentUri.getLastPathSegment();
        }
        Cursor query = getReactApplicationContext().getContentResolver().query(contentUri, new String[]{"_display_name"}, null, null, null);
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    return query.getString(0);
                }
            } finally {
                query.close();
            }
        }
        return contentUri.getLastPathSegment();
    }

    public long getLastModifiedFromUri(Uri contentUri) {
        if (UriUtil.LOCAL_FILE_SCHEME.equals(contentUri.getScheme())) {
            return new File(contentUri.toString()).lastModified();
        }
        return 0L;
    }

    public String getMimeTypeFromUri(Uri contentUri) {
        String fileExtensionFromUrl;
        String type = getReactApplicationContext().getContentResolver().getType(contentUri);
        if (type == null && (fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(contentUri.getPath())) != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFromUrl);
        }
        return type == null ? "" : type;
    }

    private WebSocketModule getWebSocketModule(String reason) {
        ReactApplicationContext reactApplicationContextIfActiveOrWarn = getReactApplicationContextIfActiveOrWarn();
        if (reactApplicationContextIfActiveOrWarn != null) {
            return (WebSocketModule) reactApplicationContextIfActiveOrWarn.getNativeModule(WebSocketModule.class);
        }
        return null;
    }

    @Override // com.facebook.fbreact.specs.NativeBlobModuleSpec
    public void addNetworkingHandler() {
        ReactApplicationContext reactApplicationContextIfActiveOrWarn = getReactApplicationContextIfActiveOrWarn();
        if (reactApplicationContextIfActiveOrWarn != null) {
            NetworkingModule networkingModule = (NetworkingModule) reactApplicationContextIfActiveOrWarn.getNativeModule(NetworkingModule.class);
            networkingModule.addUriHandler(this.mNetworkingUriHandler);
            networkingModule.addRequestBodyHandler(this.mNetworkingRequestBodyHandler);
            networkingModule.addResponseHandler(this.mNetworkingResponseHandler);
        }
    }

    @Override // com.facebook.fbreact.specs.NativeBlobModuleSpec
    public void addWebSocketHandler(final double idDouble) {
        int i = (int) idDouble;
        WebSocketModule webSocketModule = getWebSocketModule("addWebSocketHandler");
        if (webSocketModule != null) {
            webSocketModule.setContentHandler(i, this.mWebSocketContentHandler);
        }
    }

    @Override // com.facebook.fbreact.specs.NativeBlobModuleSpec
    public void removeWebSocketHandler(final double idDouble) {
        int i = (int) idDouble;
        WebSocketModule webSocketModule = getWebSocketModule("removeWebSocketHandler");
        if (webSocketModule != null) {
            webSocketModule.setContentHandler(i, null);
        }
    }

    @Override // com.facebook.fbreact.specs.NativeBlobModuleSpec
    public void sendOverSocket(ReadableMap blob, double idDouble) {
        int i = (int) idDouble;
        WebSocketModule webSocketModule = getWebSocketModule("sendOverSocket");
        if (webSocketModule != null) {
            byte[] resolve = resolve(blob.getString("blobId"), blob.getInt("offset"), blob.getInt("size"));
            if (resolve != null) {
                webSocketModule.sendBinary(ByteString.of(resolve), i);
            } else {
                webSocketModule.sendBinary((ByteString) null, i);
            }
        }
    }

    @Override // com.facebook.fbreact.specs.NativeBlobModuleSpec
    public void createFromParts(ReadableArray parts, String blobId) {
        ArrayList arrayList = new ArrayList(parts.size());
        int i = 0;
        for (int i2 = 0; i2 < parts.size(); i2++) {
            ReadableMap map = parts.getMap(i2);
            String string = map.getString("type");
            string.hashCode();
            if (string.equals("string")) {
                byte[] bytes = map.getString(UriUtil.DATA_SCHEME).getBytes(Charset.forName("UTF-8"));
                i += bytes.length;
                arrayList.add(i2, bytes);
            } else if (string.equals("blob")) {
                ReadableMap map2 = map.getMap(UriUtil.DATA_SCHEME);
                i += map2.getInt("size");
                arrayList.add(i2, resolve(map2));
            } else {
                throw new IllegalArgumentException("Invalid type for blob: " + map.getString("type"));
            }
        }
        ByteBuffer allocate = ByteBuffer.allocate(i);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            allocate.put((byte[]) it.next());
        }
        store(allocate.array(), blobId);
    }

    @Override // com.facebook.fbreact.specs.NativeBlobModuleSpec
    public void release(String blobId) {
        remove(blobId);
    }
}
