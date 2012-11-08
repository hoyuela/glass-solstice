package com.discover.mobile.utility.proxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

import com.google.common.base.Throwables;

public class Main {
	
	public static final String DISCOVER_HOST = "mst0.m.discovercard.com";
	public static final int DISCOVER_PORT = 443;
	
	public static final int LISTEN_PORT = 9080;
	
	public static void main(final String[] args) {
		try {
			new Main().runProxy();
			System.out.println("\nMain.main(): done");
		} catch(final Exception e) {
			e.printStackTrace();
			
			System.exit(1);
		}
	}
	
	private final ExecutorService executor = Executors.newFixedThreadPool(2);

	private Socket receivingSocket;
	private SSLSocket outgoingSocket;
	private BufferedReader outgoingReader;
	private OutputStream outgoingStream;
	private BufferedReader receivingReader;
	private OutputStream receivingStream;
	
	private void runProxy() throws Exception {
		final Future<?> outgoingFuture, returningFuture;
		
		System.out.println("Main.startListening(): creating receivingServerSocket...");
		final ServerSocket receivingServerSocket = new ServerSocket(LISTEN_PORT);
		try {
			System.out.println("Main.startListening(): waiting for an incoming connection...");
			receivingSocket = receivingServerSocket.accept();
			
			System.out.printf("Main.startListening(): connecting to '%s:%s'%n", DISCOVER_HOST, DISCOVER_PORT);
			outgoingSocket = createOutgoingSocket();
			try {
				outgoingStream = outgoingSocket.getOutputStream();
				outgoingReader = new BufferedReader(new InputStreamReader(outgoingSocket.getInputStream()));
				receivingStream = receivingSocket.getOutputStream();
				receivingReader = new BufferedReader(new InputStreamReader(receivingSocket.getInputStream()));
				
				outgoingFuture = executor.submit(new OutgoingRunner());
				returningFuture = executor.submit(new ReturningRunner());
				
				returningFuture.get();
				outgoingFuture.get();
			} finally {
				outgoingSocket.close();
			}
		} finally {
			receivingServerSocket.close();
		}
	}
	
	private SSLSocket createOutgoingSocket() throws Exception {
		final SSLContext sslContext = SSLContext.getDefault();
		return (SSLSocket) sslContext.getSocketFactory().createSocket(DISCOVER_HOST, DISCOVER_PORT);
	}
	
	private void proxyOutgoingTraffic() throws Exception {
		System.out.println();
		
		pipeAndPrint(receivingReader, outgoingStream, true);
	}
	
	private void proxyReturningTraffic() throws Exception {
		System.out.println();
		
		pipeAndPrint(outgoingReader, receivingStream, false);
	}
	
	private void pipeAndPrint(final BufferedReader in, final OutputStream out, final boolean outgoing) throws Exception {
		final String prefix = outgoing ? "-> " : "<- ";
		final Charset charset = Charset.forName("iso-8859-1");
		
		boolean lastWasAuthorization = false;
		
		String line;
		while((line = in.readLine()) != null) {
			String filteredLine;
			if(line.contains("192.168.4.120")) {
				filteredLine = line.replaceAll("192\\.168\\.4\\.120:9080", DISCOVER_HOST);
				
				System.out.println("XX " + prefix + line);
			} else if(line.contains(DISCOVER_HOST)) {
				filteredLine = line.replaceAll(DISCOVER_HOST, "192\\.168\\.4\\.120:9080");

				System.out.println("XX " + prefix + line);
			} else
				filteredLine = line;
			
			if(line.trim().isEmpty() && lastWasAuthorization) {
				System.out.println("XX " + prefix + line);
				continue;
			}
			lastWasAuthorization = line.startsWith("Authorization");

			System.out.println(prefix + filteredLine);
			
			if(filteredLine.isEmpty())
				filteredLine = "\r\n";
			
			final byte[] encodedLine = charset.encode(filteredLine + "\r\n").array();
			out.write(encodedLine);
		}
		
		System.out.printf("%nMain.pipeAndPrint(): done, outgoing: %s%n", outgoing);
	}
	
	private class OutgoingRunner implements Runnable {
		@Override
		public void run() {
			System.out.println("OutgoingRunner.run()");
			
			try {
				proxyOutgoingTraffic();
			} catch(final Exception e) {
				throw Throwables.propagate(e);
			}
		}
	}
	
	private class ReturningRunner implements Runnable {
		@Override
		public void run() {
			System.out.println("ReturningRunner.run()");
			
			try {
				proxyReturningTraffic();
			} catch(final Exception e) {
				throw Throwables.propagate(e);
			}
		}
	}
	
}








