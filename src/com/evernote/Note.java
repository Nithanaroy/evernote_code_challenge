package com.evernote;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "note")
@XmlType(propOrder = { "guid", "created", "tags", "content" })
public class Note {

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
		this.created_at = DatatypeConverter.parseDateTime(created).getTime();
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
