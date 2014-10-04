package com.evernote;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "note")
@XmlType(propOrder = {"guid", "created", "tags", "content"})
public class Note {

	private String guid, content;
	private Date created;
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



	public Date getCreated() {
		return created;
	}



	public void setCreated(Date created) {
		this.created = created;
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
