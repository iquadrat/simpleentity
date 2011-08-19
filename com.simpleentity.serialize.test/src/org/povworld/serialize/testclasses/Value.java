/*
 * Created on Dec 2, 2007
 *
 */
package org.povworld.serialize.testclasses;


public class Value implements Comparable<Value> {
  public String fName;
  public double fNumber;

  public Value(String name, double number) {
    fName = name;
    fNumber = number;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + fName.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final Value other = (Value) obj;
    if (!fName.equals(other.fName)) return false;
    if (Double.doubleToLongBits(fNumber) != Double.doubleToLongBits(other.fNumber)) return false;
    return true;
  }
  
  @Override
  public String toString() {
    return fName;
  }

  public int compareTo(Value o) {
    return Double.compare(fNumber, o.fNumber);
  }
  
}