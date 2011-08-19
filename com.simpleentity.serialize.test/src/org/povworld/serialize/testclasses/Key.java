/*
 * Created on Dec 2, 2007
 *
 */
package org.povworld.serialize.testclasses;


public class Key implements Comparable<Key> {

  final int fId;

  public Key(int id) {
    fId = id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + fId;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Key other = (Key) obj;
    if (fId != other.fId) {
      return false;
    }
    return true;
  }

  public int compareTo(Key o) {
    return fId - o.fId;
  }

}