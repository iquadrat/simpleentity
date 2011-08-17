/*
 * Copyright 2010 Micha Riser
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.simpleentity.util.collection;

import java.util.*;

/**
 * Unrolls a nested iterable. The outer iterable may not contain <code>null</code> values!
 *  
 * @author micha
 *
 * @param <T> value type
 */
public class CompoundIterable<T> implements Iterable<T> {
  
  protected Iterable<? extends Iterable<? extends T>> fCompound;
  
  public static <T> CompoundIterable<T> create(Iterable<? extends Iterable<? extends T>> values) {
    return new CompoundIterable<T>(values);
  }
  
  public CompoundIterable(Iterable<? extends Iterable<? extends T>> compound) {
    fCompound = compound;
  }
  
  public CompoundIterable(Iterable<? extends T>... iterables) {
    this(Arrays.asList(iterables));
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {
      
      private final Iterator<? extends Iterable<? extends T>> fValueIter;
      
      /**
       * Invariant: fCurrentIter == null || fCurrentIter.hasNext() == true
       */
      private Iterator<? extends T>                           fCurrentIter;
      
      { // this is the constructor
        fValueIter = fCompound.iterator();
        findNextIter();
      }
      
      @Override
      public boolean hasNext() {
        return fCurrentIter != null;
      }
      
      @Override
      public T next() {
        if (fCurrentIter == null) throw new NoSuchElementException();
        T result = fCurrentIter.next();
        if (!fCurrentIter.hasNext()) findNextIter();
        return result;
      }
      
      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
      
      private void findNextIter() {
        do {
          if (!fValueIter.hasNext()) {
            fCurrentIter = null;
            return;
          }
          fCurrentIter = fValueIter.next().iterator();
        } while (!fCurrentIter.hasNext());
      }
      
    };
  }
  
}
