// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DefaultClientConnection.java

package org.apache.http.impl.conn;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.impl.SocketHttpClientConnection;
import org.apache.http.io.*;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

// Referenced classes of package org.apache.http.impl.conn:
//            LoggingSessionInputBuffer, Wire, LoggingSessionOutputBuffer, DefaultResponseParser

public class DefaultClientConnection extends SocketHttpClientConnection
    implements OperatedClientConnection, HttpContext
{

    public DefaultClientConnection()
    {
    }

    public final HttpHost getTargetHost()
    {
        return targetHost;
    }

    public final boolean isSecure()
    {
        return connSecure;
    }

    public final Socket getSocket()
    {
        return socket;
    }

    public void opening(Socket sock, HttpHost target)
        throws IOException
    {
        assertNotOpen();
        socket = sock;
        targetHost = target;
        if(shutdown)
        {
            sock.close();
            throw new IOException("Connection already shutdown");
        } else
        {
            return;
        }
    }

    public void openCompleted(boolean secure, HttpParams params)
        throws IOException
    {
        assertNotOpen();
        if(params == null)
        {
            throw new IllegalArgumentException("Parameters must not be null.");
        } else
        {
            connSecure = secure;
            bind(socket, params);
            return;
        }
    }

    public void shutdown()
        throws IOException
    {
        shutdown = true;
        try
        {
            super.shutdown();
            log.debug("Connection shut down");
            Socket sock = socket;
            if(sock != null)
                sock.close();
        }
        catch(IOException ex)
        {
            log.debug("I/O error shutting down connection", ex);
        }
    }

    public void close()
        throws IOException
    {
        try
        {
            super.close();
            log.debug("Connection closed");
        }
        catch(IOException ex)
        {
            log.debug("I/O error closing connection", ex);
        }
    }

    protected SessionInputBuffer createSessionInputBuffer(Socket socket, int buffersize, HttpParams params)
        throws IOException
    {
        if(buffersize == -1)
            buffersize = 8192;
        SessionInputBuffer inbuffer = super.createSessionInputBuffer(socket, buffersize, params);
        if(wireLog.isDebugEnabled())
            inbuffer = new LoggingSessionInputBuffer(inbuffer, new Wire(wireLog), HttpProtocolParams.getHttpElementCharset(params));
        return inbuffer;
    }

    protected SessionOutputBuffer createSessionOutputBuffer(Socket socket, int buffersize, HttpParams params)
        throws IOException
    {
        if(buffersize == -1)
            buffersize = 8192;
        SessionOutputBuffer outbuffer = super.createSessionOutputBuffer(socket, buffersize, params);
        if(wireLog.isDebugEnabled())
            outbuffer = new LoggingSessionOutputBuffer(outbuffer, new Wire(wireLog), HttpProtocolParams.getHttpElementCharset(params));
        return outbuffer;
    }

    protected HttpMessageParser createResponseParser(SessionInputBuffer buffer, HttpResponseFactory responseFactory, HttpParams params)
    {
        return new DefaultResponseParser(buffer, null, responseFactory, params);
    }

    public void update(Socket sock, HttpHost target, boolean secure, HttpParams params)
        throws IOException
    {
        assertOpen();
        if(target == null)
            throw new IllegalArgumentException("Target host must not be null.");
        if(params == null)
            throw new IllegalArgumentException("Parameters must not be null.");
        if(sock != null)
        {
            socket = sock;
            bind(sock, params);
        }
        targetHost = target;
        connSecure = secure;
    }

    public HttpResponse receiveResponseHeader()
        throws HttpException, IOException
    {
        HttpResponse response = super.receiveResponseHeader();
        if(log.isDebugEnabled())
            log.debug((new StringBuilder()).append("Receiving response: ").append(response.getStatusLine()).toString());
        if(headerLog.isDebugEnabled())
        {
            headerLog.debug((new StringBuilder()).append("<< ").append(response.getStatusLine().toString()).toString());
            org.apache.http.Header headers[] = response.getAllHeaders();
            org.apache.http.Header arr$[] = headers;
            int len$ = arr$.length;
            for(int i$ = 0; i$ < len$; i$++)
            {
                org.apache.http.Header header = arr$[i$];
                headerLog.debug((new StringBuilder()).append("<< ").append(header.toString()).toString());
            }

        }
        return response;
    }

    public void sendRequestHeader(HttpRequest request)
        throws HttpException, IOException
    {
        if(log.isDebugEnabled())
            log.debug((new StringBuilder()).append("Sending request: ").append(request.getRequestLine()).toString());
        super.sendRequestHeader(request);
        if(headerLog.isDebugEnabled())
        {
            headerLog.debug((new StringBuilder()).append(">> ").append(request.getRequestLine().toString()).toString());
            org.apache.http.Header headers[] = request.getAllHeaders();
            org.apache.http.Header arr$[] = headers;
            int len$ = arr$.length;
            for(int i$ = 0; i$ < len$; i$++)
            {
                org.apache.http.Header header = arr$[i$];
                headerLog.debug((new StringBuilder()).append(">> ").append(header.toString()).toString());
            }

        }
    }

    public Object getAttribute(String id)
    {
        return attributes.get(id);
    }

    public Object removeAttribute(String id)
    {
        return attributes.remove(id);
    }

    public void setAttribute(String id, Object obj)
    {
        attributes.put(id, obj);
    }

    private final Log log = LogFactory.getLog(getClass());
    private final Log headerLog = LogFactory.getLog("org.apache.http.headers");
    private final Log wireLog = LogFactory.getLog("org.apache.http.wire");
    private volatile Socket socket;
    private HttpHost targetHost;
    private boolean connSecure;
    private volatile boolean shutdown;
    private final Map attributes = new HashMap();
}
