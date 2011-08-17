/*
 * Created on Oct 7, 2007
 *
 */
package com.simpleentity.util.collection;

import java.util.*;

import com.simpleentity.util.Assert;

public class CompoundIterator<T, I extends Iterator<T>> implements Iterator<T> {

  protected final List<I> fIterators;
  protected int           fIndex;

  /**
   * @param iterators
   *          [own]
   */
  public CompoundIterator(List<I> iterators) {
    Assert.isFalse(iterators.isEmpty());
    fIterators = iterators;
    fIndex = 0;
  }

  public boolean hasNext() {
    int index = fIndex;
    for (;;) {
      if (index >= fIterators.size()) return false;
      if (fIterators.get(index).hasNext()) return true;
      index++;
    }
  }

  public T next() {
    while (!fIterators.get(fIndex).hasNext()) {
      fIndex++;
    }
    return fIterators.get(fIndex).next();
  }

  public void remove() {
    fIterators.get(fIndex).remove();
  }

}
