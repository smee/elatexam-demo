package de.elatexam.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class Tools {
  public static final Logger log = Logger.getLogger(Tools.class.getName());


  /**
   * @param input
   * @return
   */
  public static String md5(String input) {
    MessageDigest md5;
    try {
      md5 = MessageDigest.getInstance("MD5");
      md5.reset();
      md5.update(input.getBytes());
      byte[] result = md5.digest();

      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < result.length; i++) {
        hexString.append(Integer.toHexString(0xFF & result[i]));
      }
      return hexString.toString();

    } catch (NoSuchAlgorithmException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static MemcacheService c() {
    return MemcacheServiceFactory.getMemcacheService();
  }

  public static byte[] serialize(Object obj) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos;
    try {
      oos = new ObjectOutputStream(baos);
      oos.writeObject(obj);
    } catch (IOException e) {
      log.log(Level.SEVERE, "Could not deserialize object", e);
    }
    return baos.toByteArray();
  }

  public static <T> T deserialize(byte[] data) {
    try {
      return (T) new ObjectInputStream(new ByteArrayInputStream(data)).readObject();
    } catch (IOException e) {
      log.log(Level.SEVERE, "Could not deserialize object", e);
    } catch (ClassNotFoundException e) {
      log.log(Level.SEVERE, "Could not deserialize object", e);
    }
    return null;
  }

}
