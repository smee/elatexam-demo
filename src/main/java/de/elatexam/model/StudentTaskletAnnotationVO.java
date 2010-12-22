/*

Copyright (C) 2010 Steffen Dienst

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
/**
 *
 */
package de.elatexam.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * @author Steffen Dienst
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class StudentTaskletAnnotationVO {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;
  @Persistent
  private Text text;
  @Persistent
	private Long date;
  @Persistent
	private boolean acknowledged;


	/**
	 * @param text
	 * @param date
	 * @param acknowledged
	 */
	public StudentTaskletAnnotationVO(String text, Long date, boolean acknowledged) {
    this.text = new Text(text);
		this.date = date;
		this.acknowledged = acknowledged;
	}

  /**
   * @return the key
   */
  public Key getKey() {
    return key;
  }
	/**
	 * @return Returns the date.
	 */
	public Long getDate() {
		return date;
	}
	/**
	 * @param date The date to set.
	 */
	public void setDate(Long date) {
		this.date = date;
	}
	/**
	 * @return Returns the text.
	 */
	public String getText() {
    return text.getValue();
	}
	/**
	 * @param text The text to set.
	 */
	public void setText(String text) {
    this.text = new Text(text);
	}
	/**
	 * @return Returns the acknowledged.
	 */
	public boolean isAcknowledged() {
		return acknowledged;
	}
	/**
	 * @param acknowledged The acknowledged to set.
	 */
	public void setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}



}
