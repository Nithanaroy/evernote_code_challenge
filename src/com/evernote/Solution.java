package com.evernote;

import java.io.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class Solution {

	public static void main(String[] args) throws IOException, JAXBException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command, note = "", line;
		
		do {
			line = br.readLine();
			note += line;
		} while(!line.equals("</note>"));
		
		JAXBContext context = JAXBContext.newInstance(Note.class);
		Unmarshaller un = context.createUnmarshaller();
		Note n = (Note) un.unmarshal(new ByteArrayInputStream(note.getBytes()));
	}
}
