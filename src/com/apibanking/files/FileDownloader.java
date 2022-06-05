
package com.apibanking.files;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

public class FileDownloader {

	public static void main(String[] args) {
		
		if(args == null || args.length < 2) {
			System.out.println("Please provide program arguments: urlListFile & outputFolder");
			System.exit(0);
		}

    	String urlListFile = args[0];
    	String outputFolder = args[1];

//		String wsdlListFilePath = "/Users/mayu/startup/code/WSDLDownloader/filelist.txt";
//		String outputFolder = "/Users/mayu/startup/code/WSDLDownloader/output";

//        String url = "http://www.dneonline.com/calculator.asmx?WSDL";

		if (!outputFolder.endsWith(getPathSeparator(outputFolder))) {
			outputFolder = outputFolder + getPathSeparator(outputFolder);
		}
		
		List<String> failedDownloads = new ArrayList<String>();

		try {

			System.out.println("Reading URL List from file: " + urlListFile);
			List<String> wsdlList = readList(urlListFile);
			System.out.println("Downloaded files will be saved to: " + outputFolder);
			for (String url : wsdlList) {
				try {
					downloadUsingStream(url, outputFolder + extractServiceName(url));
				} catch (FileNotFoundException e) {
					failedDownloads.add(url);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (failedDownloads.size() > 0) {
			System.out.println("\nDownload failed for below URLs...");
			for (String url : failedDownloads) {
				System.out.println(url);
			}
		}
	}

	private static String extractServiceName(String url) {
		String serviceName;

		serviceName = url.substring(url.lastIndexOf(getPathSeparator(url)) + 1, url.lastIndexOf("?"));
		System.out.println(serviceName);

		return serviceName;
	}

	private static String getPathSeparator(String url) {
		return url.contains("/") ? "/" : "\\";
	}

	private static List<String> readList(String filePath) {

		List<String> wsdlURLs = new ArrayList<String>();
		try {
			File file = new File(filePath); // creates a new file instance
			FileReader fr = new FileReader(file); // reads the file
			BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
			StringBuffer sb = new StringBuffer(); // constructs a string buffer with no characters
			String line;
			while ((line = br.readLine()) != null) {
				wsdlURLs.add(line);
			}
			fr.close(); // closes the stream and release the resources
			System.out.println("Number of wsdl files to download: " + wsdlURLs.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wsdlURLs;
	}

	private static void downloadUsingStream(String urlStr, String file) throws IOException {
		URL url = new URL(urlStr);
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		FileOutputStream fis = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int count = 0;
		while ((count = bis.read(buffer, 0, 1024)) != -1) {
			fis.write(buffer, 0, count);
		}
		fis.close();
		bis.close();
	}

	private static void downloadUsingNIO(String urlStr, String file) throws IOException {
		URL url = new URL(urlStr);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();
	}

}
