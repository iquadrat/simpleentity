/*
 * Created on Dec 2, 2007
 *
 */
package org.povworld.serialize;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import org.povworld.serialize.testclasses.Key;
import org.povworld.serialize.testclasses.Value;

import com.simpleentity.util.collection.CollectionUtil;

public class JavaClassesSerializableTest extends AbstractSerializationTest {

  public void test_serialize_lists() {
    checkList(new ArrayList<String>());
    checkList(new LinkedList<String>());
    checkList(new Vector<String>());
    checkList(new Stack<String>());
    checkList(new CopyOnWriteArrayList<String>());
    checkList(Collections.synchronizedList(new LinkedList<String>()));
  }

  private void checkList(List<String> list) {
    list.add("hello world");
    list.add("");
    list.add("foo");
    list.add(null);
    list.add("bar");
    List<String> actual = serializeAndDeserialize(list);
    assertEquals(actual, list);
    assertEquals(actual.getClass(), list.getClass());
  }

  public void test_serialize_maps() {
    checkMap(new HashMap<Key, Value>());
    checkMap(new LinkedHashMap<Key, Value>());
//    checkMap(new IdentityHashMap<Key, Value>());
    checkMap(new TreeMap<Key, Value>());
    checkMap(new Hashtable<Key, Value>());
  }
  
  private static class ByToStringLengthComparator implements Comparator<String>, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(String o1, String o2) {
      if (o1.length() == o2.length()) {
        return o1.compareTo(o2);
      }
      return o1.length() - o2.length();
    }
  }
  
  public void test_treeset_with_comparator() {
    TreeSet<String> set = new TreeSet<String>(new ByToStringLengthComparator());
    
    List<String> expected = Arrays.asList("As","Test","Word","Hello","World","Length");
    
    set.add(new String("Word"));
    set.add(new String("Length"));
    set.add(new String("World"));
    set.add(new String("Test"));
    set.add(new String("Hello"));
    set.add(new String("As"));
    assertEquals(expected, new ArrayList<String>(set));
    
    TreeSet<String> set2 = serializeAndDeserialize(set);
    assertNotSame(set, set2);
    assertEquals(expected, new ArrayList<String>(set2));
  }

  private void checkMap(Map<Key, Value> map) {
    map.put(new Key(12), new Value("hello", 19));
    map.put(new Key(17), new Value("foo", 0.014));
    if (!(map instanceof Hashtable)) {
      map.put(new Key(23), null);
    }
    if (!(map instanceof TreeMap) && !(map instanceof Hashtable)) {
      map.put(null, new Value("null", 0));
    }
    Map<Key, Value> actual = serializeAndDeserialize(map);
    assertEquals(map, actual);
    assertEquals(map.getClass(), actual.getClass());
  }

  public void test_serialize_string() {
    assertEquals("", serializeAndDeserialize(""));
    assertEquals("Hello kitty!", serializeAndDeserialize("Hello kitty!"));
    assertEquals("+()=/*()=ç*]@|¼12", serializeAndDeserialize("+()=/*()=ç*]@|¼12"));
  }

  public void test_serialize_bitset() {
    BitSet bitSet = new BitSet(721);
    for (int i = 0; i < bitSet.size(); ++i) {
      if (!BigInteger.valueOf(i).isProbablePrime(20)) {
        continue;
      }
      bitSet.flip(i);
    }
    BitSet actual = serializeAndDeserialize(bitSet);
    assertEquals(bitSet, actual);
  }

  public void test_serialize_queue() {
    checkQueue(new ArrayBlockingQueue<Value>(32, true));
    checkQueue(new ConcurrentLinkedQueue<Value>());
    checkQueue(new LinkedBlockingQueue<Value>());
    checkQueue(new PriorityQueue<Value>());
    checkQueue(new ArrayDeque<Value>());
  }

  private void checkQueue(Queue<Value> queue) {
    queue.add(new Value("foo", 73.1));
    queue.add(new Value("neg", -1));
    queue.add(new Value("bar", 17.91));

    Queue<Value> actual = serializeAndDeserialize(queue);
    assertQueueEquals(queue, actual);
    assertEquals(queue.getClass(), actual.getClass());
    assertNotSame(queue, actual);
  }

  private void assertQueueEquals(Queue<Value> queue, Queue<Value> actual) {
    assertTrue(CollectionUtil.iteratesEqualSequence(queue.iterator(), actual.iterator()));
  }

  public void test_serialize_pattern() {
    Pattern pattern = Pattern.compile("([a-f])*xi?.(foo)?[0-8]*");
    Pattern actual = serializeAndDeserialize(pattern);
    assertEquals(pattern.pattern(), actual.pattern());
  }

  public void test_serialize_byte_buffer() {
    ByteBuffer buffer = ByteBuffer.allocate(61);
    buffer.putDouble(15.32);
    buffer.putInt(23112);
    buffer.putShort((short) -123);
    buffer.flip();

    ByteBuffer actual = serializeAndDeserialize(buffer);
    assertEquals(buffer, actual);
  }

  public void test_serialize_biginteger() {
    BigInteger i = new BigInteger("23489072389478923478923748923748923784283421991345");
    BigInteger actual = serializeAndDeserialize(i);
    assertEquals(i, actual);
  }

  public void test_serialize_InetAddress() throws UnknownHostException {
    InetAddress address = InetAddress.getLocalHost();
    InetAddress actual = serializeAndDeserialize(address);
    assertEquals(address, actual);

    address = InetAddress.getByAddress(new byte[] { (byte) 192, (byte) 168, 77, (byte) 192 });
    actual = serializeAndDeserialize(address);
    assertEquals(address, actual);

    address = InetAddress.getByAddress(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 });
    actual = serializeAndDeserialize(address);
    assertEquals(address, actual);
  }

  public void test_serialize_random() {
    Random rnd = new Random(231923);
    Random actual = serializeAndDeserialize(rnd);
    assertEquals(rnd.nextDouble(), actual.nextDouble());
  }
  
  static class Super {
      protected final Set<Super> fSet = new HashSet<Super>(); 
  }
  
  static class Sub extends Super {
      private final int fId;
      public Sub(int id) {
          fId = id;
          fSet.add(this);
      }
    @Override
    public int hashCode() {
        return fId;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Sub other = (Sub) obj;
        if (fId != other.fId)
            return false;
        return true;
    }
    
    public boolean isSelfContained() {
        return fSet.contains(this);
    }
      
  }
  
  public void test_serialize_cyclic_map() {
      Sub sub = new Sub(8123);
      assertTrue(sub.isSelfContained());
      Sub actual = serializeAndDeserialize(sub);
      assertEquals(sub, actual);
      assertTrue(actual.isSelfContained());
  }

}
