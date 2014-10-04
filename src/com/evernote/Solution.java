package com.evernote;

import java.io.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class Solution {

	public static void main(String[] args) throws IOException, JAXBException, ParseException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command = null;
		Map<String, Note> notes = new HashMap<String, Note>();

		do {
			command = br.readLine();
			if (command.equals("CREATE") || command.equals("UPDATE")) {
				Note n = getNote(br);
				notes.put(n.getGuid(), n); // for update GUID will be the same
			} else if (command.equals("DELETE")) {
				String guid = br.readLine();
				notes.remove(guid);
			} else if (command.equals("SEARCH")) {
				handleSearch(br, notes.values());
			} else {
				// Dont know what to do
			}
		} while (command != null);

		// handleSearch(br, notes.values());

	}

	private static void handleSearch(BufferedReader br, Collection<Note> notes)
			throws IOException, ParseException {

		String searchCrieteria = br.readLine();
		String key = "";
		char searchType = 'n';

		Pattern regex = Pattern.compile("^tag:", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		Matcher regexMatcher = regex.matcher(searchCrieteria);
		if (regexMatcher.find()) {
			searchType = 't';
			if (hasStarAtTheEnd(searchCrieteria)) 
				searchCrieteria = replaceLast(searchCrieteria, "\\*", ".*");
			key = "\\b" + searchCrieteria.replaceFirst("tag:", "") + "\\b";
		}
		
		if (searchType == 'n') {
			regex = Pattern.compile("^created:", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
			regexMatcher = regex.matcher(searchCrieteria);
			if (regexMatcher.find()) {
				searchType = 'd';
				key = "\\b" + searchCrieteria.replaceFirst("created:", "") + "\\b";
			}
		}
		
		if (searchType == 'n') {
			searchType = 'c';
			if (hasStarAtTheEnd(searchCrieteria))
				key = "\\b" + replaceLast(searchCrieteria, "\\*", ".*");
			else
				key = "\\b" + searchCrieteria + "\\b";
		}
		
		// content
		if (searchType == 'c') {
			for (Note n : notes) {
				regex = Pattern.compile(key, Pattern.MULTILINE
						| Pattern.CASE_INSENSITIVE);
				regexMatcher = regex.matcher(n.getContent());
				if (regexMatcher.find()) {
					System.out.println(n.getGuid());
				}
			}	
		}

		// tag
		else if (searchType == 't') {
			for (Note n : notes) {
				for(String tag: n.getTags()) {
					regex = Pattern.compile(key, Pattern.MULTILINE
							| Pattern.CASE_INSENSITIVE);
					regexMatcher = regex.matcher(tag);
					if (regexMatcher.find()) {
						System.out.println(n.getGuid());
						break;
					}
				}
			}	
		}
		
		// time
		else if(searchType == 'd') {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        	Date givenDate = (Date) sdf.parse(key);
			for (Note n : notes) {
				if(n.getCreated_at().compareTo(givenDate) > 0)
					System.out.println(n.getGuid());
			}	
		}
	}

	private static boolean hasStarAtTheEnd(String searchCrieteria) {
		Pattern regex;
		Matcher regexMatcher;
		regex = Pattern.compile("\\*$", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		regexMatcher = regex.matcher(searchCrieteria);
		return regexMatcher.find();
	}
	
	public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }

	/**
	 * Reads Standard Input for a XML Note and creates a note object
	 * 
	 * @param br
	 *            = Buffered Reader
	 * @return Note Object
	 * @throws IOException
	 * @throws JAXBException
	 */
	private static Note getNote(BufferedReader br) throws IOException,
			JAXBException {
		String line, note = "";
		do {
			line = br.readLine();
			note += line;
		} while (!line.equals("</note>"));

		JAXBContext context = JAXBContext.newInstance(Note.class);
		Unmarshaller un = context.createUnmarshaller();
		return (Note) un.unmarshal(new ByteArrayInputStream(note.getBytes()));
	}
}
