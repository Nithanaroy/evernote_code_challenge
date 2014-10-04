package com.evernote;

import java.io.*;
import java.util.Date;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

public class Solution {

	public static void main(String[] args) throws Exception{
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String command = null;
			Map<String, Note> notes = new HashMap<String, Note>();

			while(true) {
				command = br.readLine();
				if(command == null)
					break;
				if (command.equals("CREATE") || command.equals("UPDATE")) {
					Note n = getNote(br);
					notes.put(n.getGuid(), n); // for update GUID will be the
												// same
				} else if (command.equals("DELETE")) {
					String guid = br.readLine();
					notes.remove(guid);
				} else if (command.equals("SEARCH")) {
					handleSearch(br, notes.values());
				} else {
					// Dont know what to do
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}


	}

	private static void handleSearch(BufferedReader br, Collection<Note> notes)
			throws IOException, ParseException {

		String searchCrieteria = br.readLine();
		String key = "";
		char searchType = 'n';
		int count = 0;

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
				key = searchCrieteria.replaceFirst("created:", "");
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
			count = 0;
			for (Note n : notes) {
				regex = Pattern.compile(key, Pattern.MULTILINE
						| Pattern.CASE_INSENSITIVE);
				regexMatcher = regex.matcher(n.getContent());
				if (regexMatcher.find()) {
					if(count > 0)
						System.out.print("," + n.getGuid());
					else
						System.out.print(n.getGuid());
					count++;
				}
			}	
		}

		// tag
		else if (searchType == 't') {
			count = 0;
			for (Note n : notes) {
				for(String tag: n.getTags()) {
					regex = Pattern.compile(key, Pattern.MULTILINE
							| Pattern.CASE_INSENSITIVE);
					regexMatcher = regex.matcher(tag);
					if (regexMatcher.find()) {
						if(count > 0)
							System.out.print("," + n.getGuid());
						else
							System.out.print(n.getGuid());
						count++;
						break;
					}
				}
			}	
		}
		
		// time
		else if(searchType == 'd') {
			count = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        	Date givenDate = (Date) sdf.parse(key);
			for (Note n : notes) {
				if(n.getCreated_at().compareTo(givenDate) > 0)
					if(count > 0)
						System.out.print("," + n.getGuid());
					else
						System.out.print(n.getGuid());
					count++;
			}	
		}
		
		if(count > 0)
			System.out.println();
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



@XmlRootElement(name = "note")
@XmlType(propOrder = { "guid", "created", "tags", "content" })
class Note {

	private String guid, content;
	private String created;
	private Date created_at;
	private String[] tags;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) throws ParseException {
		this.created = created;
		this.created_at = (Date) DatatypeConverter.parseDateTime(created).getTime();
	}

	public Date getCreated_at() {
		return created_at;
	}

	@XmlElement(name = "tag")
	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return guid;
	}

}
