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

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

/**
 * @author Steffen Dienst
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class TaskDefVO {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;
  @Persistent
	private long id;
  @Persistent
	private boolean stopped;
  @Persistent
	private boolean visible;
  @Persistent
  private Blob xml;
  @Persistent
  private Date creationDate;
  @Persistent
  private String username;

  public TaskDefVO() {
    this.creationDate = new Date();
    this.id = -1; // marker for unused value as id==0 would be valid
  }

  /**
   * @return the key
   */
  public Key getKey() {
    return key;
  }

  /**
   * @return the creationDate
   */
  public Date getCreationDate() {
    return creationDate;
  }

	/**
	 * @return Returns the id.
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return Returns the stopped.
	 */
	public boolean isStopped() {
		return stopped;
	}
	/**
	 * @param stopped The stopped to set.
	 */
	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}
	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}
	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

  public void setXml(byte[] byteArray) {
    this.xml = new Blob(byteArray);
  }

  public byte[] getXml() {
    return this.xml.getBytes();
  }

  /**
   * @param user
   *          the user to set
   */
  public void setUsername(String user) {
    this.username = user;
  }

  /**
   * @return the user
   */
  public String getUsername() {
    return username;
  }


}
