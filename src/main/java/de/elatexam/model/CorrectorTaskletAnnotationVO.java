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
public class CorrectorTaskletAnnotationVO {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;

  /**
   * @return the key
   */
  public Key getKey() {
    return key;
  }
  @Persistent
	private String corrector;
  @Persistent
  private Text text;
	/**
	 * @param corrector
	 * @param text
	 */
	public CorrectorTaskletAnnotationVO(String corrector, String text) {
		super();
		this.corrector = corrector;
    this.text = new Text(text);
	}

	/**
	 * @return the corrector
	 */
	public String getCorrector() {
		return corrector;
	}
	/**
	 * @param corrector the corrector to set
	 */
	public void setCorrector(String corrector) {
		this.corrector = corrector;
	}
	/**
	 * @return the text
	 */
	public String getText() {
    return text.getValue();
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
    this.text = new Text(text);
	}

}
