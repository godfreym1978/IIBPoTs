package com.testing.mq;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.MQQueue;
import com.ibm.mq.constants.MQConstants;

public class Util {

	public void sendFileHTTPRequest(String URL, String reqFile,
			String repFile, String contentType) {
		FileReader fr = null;
		try {
			URL url = new URL(URL);
			String document = reqFile;
			fr = new FileReader(document);
			char[] buffer = new char[1024 * 10];
			int bytes_read = 0;
			if ((bytes_read = fr.read(buffer)) != -1) {
				URLConnection urlc = url.openConnection();
				urlc.setDoOutput(true);
				urlc.setDoInput(true);
				urlc.setRequestProperty("content-type", contentType);
				PrintWriter pw = new PrintWriter(urlc.getOutputStream());
				pw.write(buffer, 0, bytes_read);
				pw.close();
				BufferedReader inBR = new BufferedReader(new InputStreamReader(
						urlc.getInputStream()));
				String inputLine;
				BufferedWriter outBW = new BufferedWriter(new FileWriter(
						new File(repFile)));
				while ((inputLine = inBR.readLine()) != null)
					outBW.write(inputLine);
				inBR.close();
				outBW.close();
			}
			fr.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public  String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();

		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
		}
		reader.close();
		return stringBuilder.toString();
	}

	public  void queueRequestReply(String queueMgr, String queueRequest,
			String queueReply, String inputFile, String outputFile, int timeoutSec) throws InterruptedException {

		MQQueueManager qMgr = null;
		MQQueue mqQueueRequest = null;
		MQQueue mqQueueReply = null;
		MQPutMessageOptions pmo = null;
		MQGetMessageOptions gmo = null;
		MQMessage msg = null;
		String str = "CORRELID";
		byte byteArray[] = str.getBytes();

		try {
			/*write the message*/
			qMgr = new MQQueueManager(queueMgr);
			int openOptions = 17;
			mqQueueRequest = qMgr.accessQueue(queueRequest, openOptions);

			String strMsg = readFile(inputFile);
			msg = new MQMessage();
			msg.writeString(strMsg);

			msg.messageId = byteArray;
			pmo = new MQPutMessageOptions();

			mqQueueRequest.put(msg, pmo);
			msg.clearMessage();
			mqQueueRequest.close();
			
			/*wait for some time*/
			TimeUnit.SECONDS.sleep(timeoutSec);
			/*read the message*/
			mqQueueReply = qMgr.accessQueue(queueReply, openOptions);
			msg = new MQMessage();
			
			gmo = new MQGetMessageOptions();
			gmo.options = MQConstants.MQOO_INPUT_SHARED;

			msg.messageId = byteArray;
			mqQueueReply.get(msg, gmo);

			String strMessage = msg.readStringOfByteLength(msg
					.getMessageLength());
			File newTextFile = new File(outputFile);

			FileWriter fw = new FileWriter(newTextFile);
			fw.write(strMessage);
			fw.close();

			msg.clearMessage();
			mqQueueReply.close();
			qMgr.disconnect();

		} catch (MQException ex) {
			System.out
					.println("A WebSphere MQ Error occured : Completion Code "
							+ ex.completionCode + " Reason Code "
							+ ex.reasonCode);
		} catch (IOException ex) {
			System.out
					.println("An IOException occured whilst writing to the message buffer: "
							+ ex);
		}

	}
	public  void writeMessageToQueue(String queueMgr, String queueName,
			String fileName) {
		try {
			MQQueueManager qMgr = new MQQueueManager(queueMgr);
			int openOptions = 17;
			MQQueue queue = qMgr.accessQueue(queueName, openOptions);

			String qmessage = readFile(fileName);
			MQMessage msg = new MQMessage();
			msg.writeString(qmessage);

			MQPutMessageOptions pmo = new MQPutMessageOptions();

			queue.put(msg, pmo);
			queue.close();
			qMgr.disconnect();
		} catch (MQException ex) {
			System.out
					.println("A WebSphere MQ Error occured : Completion Code "
							+ ex.completionCode + " Reason Code "
							+ ex.reasonCode);
		} catch (IOException ex) {
			System.out
					.println("An IOException occured whilst writing to the message buffer: "
							+ ex);
		}
	}

	public  void readMessageFromQueue(String qManager, String qResponse,
			String outputFile) {
		MQQueueManager qMgr = null;
		MQQueue queue = null;
		try {

			qMgr = new MQQueueManager(qManager);
			int openOptions = 17;
			queue = qMgr.accessQueue(qResponse, openOptions);
			MQMessage msg = new MQMessage();
			// msg.messageId = "hello".getBytes();
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = MQConstants.MQOO_INPUT_SHARED;

			String str = "CORRELID";
			byte byteArray[] = str.getBytes();

			msg.messageId = byteArray;
			queue.get(msg, gmo);

			String strMessage = msg.readStringOfByteLength(msg
					.getMessageLength());
			File newTextFile = new File(outputFile);

			FileWriter fw = new FileWriter(newTextFile);
			fw.write(strMessage);
			fw.close();

			msg.clearMessage();
			queue.close();
			qMgr.disconnect();
		} catch (MQException ex) {
			System.out
					.println("A WebSphere MQ Error occured : Completion Code "
							+ ex.completionCode + " Reason Code "
							+ ex.reasonCode);
		} catch (IOException ex) {
			System.out
					.println("An IOException occured whilst writing to the message buffer: "
							+ ex);
		}
	}

	public  byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
