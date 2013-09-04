package grizzly;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.ReadHandler;
import org.glassfish.grizzly.http.io.NIOInputStream;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.grizzly.memory.ByteBufferManager;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.strategies.SameThreadIOStrategy;

public class UploadHttpHandlerSample {
    private static final Logger LOGGER = Grizzly.logger(UploadHttpHandlerSample.class);


    public static void main(String[] args) {

        // create a basic server that listens on port 8080.
        final HttpServer server = HttpServer.createSimpleServer();

        
        final TCPNIOTransport transport = server.getListeners().iterator().next().getTransport();
        
//        If we want to try direct byte buffers?
//        final ByteBufferManager mm = new ByteBufferManager(true, 128 * 1024,
//                ByteBufferManager.DEFAULT_SMALL_BUFFER_SIZE);
        
//        transport.setMemoryManager(mm);

        transport.setIOStrategy(SameThreadIOStrategy.getInstance());
        transport.setSelectorRunnersCount(4);
        
        final ServerConfiguration config = server.getServerConfiguration();

        // Map the path, /upload, to the NonBlockingUploadHandler
        config.addHttpHandler(new NonBlockingUploadHandler(), "/upload");

        try {
            server.start();
            LOGGER.info("Press enter to stop the server...");
            System.in.read();
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, ioe.toString(), ioe);
        } finally {
            server.shutdownNow();
        }
    }

    /**
     * This handler using non-blocking streams to read POST data and store it
     * to the local file.
     */
    private static class NonBlockingUploadHandler extends HttpHandler {
        
        private final AtomicInteger counter = new AtomicInteger();

        // -------------------------------------------- Methods from HttpHandler


        @Override
        public void service(final Request request,
                            final Response response) throws Exception {

            final NIOInputStream in = (NIOInputStream) request.getInputStream(); // get the NIO stream
            
            final FileChannel fileChannel = new FileOutputStream(
                    "/tmp/" + counter.incrementAndGet()).getChannel();
            
            response.suspend();  // !!! suspend the Request

            // If we don't have more data to read - onAllDataRead() will be called
            in.notifyAvailable(new ReadHandler() {

                public void onDataAvailable() throws Exception {
                    LOGGER.log(Level.FINE, "[onDataAvailable] length: {0}", in.readyData());
                    storeAvailableData(in, fileChannel);
                    in.notifyAvailable(this);
                }

                public void onError(Throwable t) {
                    LOGGER.log(Level.WARNING, "[onError]", t);
                    response.setStatus(500, t.getMessage());
                    complete(true);
                    
                    if (response.isSuspended()) {
                        response.resume();
                    } else {
                        response.finish();                    
                    }
                }

                public void onAllDataRead() throws Exception {
                    LOGGER.log(Level.FINE, "[onAllDataRead] length: {0}", in.readyData());
                    storeAvailableData(in, fileChannel);
                    response.setStatus(HttpStatus.ACCEPTED_202);
                    complete(false);
                    response.resume();
                }
                
                private void complete(final boolean isError) {
                    try {
                        fileChannel.close();
                    } catch (IOException e) {
                        if (!isError) {
                            response.setStatus(500, e.getMessage());
                        }
                    }
                    
                    try {
                        in.close();
                    } catch (IOException e) {
                        if (!isError) {
                            response.setStatus(500, e.getMessage());
                        }
                    }                                        
                }
            });

        }

        private static void storeAvailableData(NIOInputStream in, FileChannel fileChannel)
                throws IOException {
            // Get the Buffer directly from NIOInputStream
            final Buffer buffer = in.readBuffer();
            // Retrieve ByteBuffer
            final ByteBuffer byteBuffer = buffer.toByteBuffer();
            
            try {
                while(byteBuffer.hasRemaining()) {
                    // Write the ByteBuffer content to the file
                    fileChannel.write(byteBuffer);
                }
            } finally {
                // we can try to dispose the buffer
                buffer.tryDispose();
            }
        }

    } // END NonBlockingUploadHandler    
}
