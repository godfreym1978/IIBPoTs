package com.AggregateStockQuote;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.custommonkey.xmlunit.*;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.testing.mq.Util;

public class StockSingleReqTest extends XMLTestCase {

	@Test
	public void test() throws IOException, InterruptedException, SAXException {
		// fail("Not yet implemented");
		String inputFile = System.getProperty("user.dir")
				+ "/InputFile/StockQuoteReqSingle.xml";
		String outputFile = System.getProperty("user.dir")
				+ "/OutputFile/StockQuoteRepSingle.xml";
		String controlFile = System.getProperty("user.dir")
				+ "/ExpectedFile/StockQuoteRepSingle.xml";

		Util newUtil = new Util();
		/*
		newUtil.writeMessageToQueue("WQM.INT", "AGG.REQ", inputFile);
		TimeUnit.SECONDS.sleep(7);
		newUtil.readMessage("WQM.INT", "AGG.REP", outputFile);
		*/
		
		newUtil.queueRequestReply("WQM.INT", 
				"AGG.REQ", 
				"AGG.REP", 
				inputFile, 
				outputFile,
				8);

		String controlXML = newUtil.readFile(controlFile);
		String testXML = newUtil.readFile(outputFile);

		DifferenceListener myDifferenceListener = new IgnoreTextAndAttributeValuesDifferenceListener();
		Diff myDiff = new Diff(controlXML, testXML);
		myDiff.overrideDifferenceListener(myDifferenceListener);
		assertTrue("test XML matches control skeleton XML " + myDiff,
				myDiff.similar());
		// assertTrue("Comparing test xml to control xml", controlXML, testXML);

	}
}
