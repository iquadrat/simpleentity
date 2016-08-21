/*
 * Created on Nov 29, 2007
 *
 */
package com.simpleentity.serialize;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.simpleentity.serialize.object.IObjectSerializer;

/**
 * An object graph serializer can be used to serialize an object including all of its
 * fields and all referenced objects to a sequence of bytes which can be written to
 * a permanent storage. Later on, the graph of objects can be restored from the 
 * persisted data.
 * <p>
 * TODO Meta data evolution?
 * <p>
 * Limitations:
 * <ul>
 *     <li>
 *     The serialization protocol of Java's {@link Serializable} is not fully supported.
 *     It is recommended that you write a custom serializer for the class if it requires
 *     special handling and registers it using 
 *     {@link IObjectGraphSerializer#registerSerializer(Class, IObjectSerializer)}.
 *     However, most classes from the java.* packages which implement the Serializable 
 *     interface should work. If you encounter a class from java.* which makes 
 *     problems with this serializer, please report it.       
 *     </li>
 * </ul>
 * 
 * @author micha
 *
 */
public interface IObjectGraphSerializer extends IObjectSerializerRegistry {
  
  /**
   * Serializes the given object including the reference graph outgoing
   * from this root object. The root object must not be <tt>null</tt>.
   * 
   * @return an array of byte buffers containing the bytes of the serialized objects mesh
   * 
   * @throws SerializationError if anything goes wrong
   */
  public ByteBuffer[] serializeObjectGraph(Object rootObject);

  /**
   * Deserializes a graph of objects and returns its root node.
   * 
   * @throws SerializationError if anything goes wrong
   */
  public Object deserializeObjectGraph(ByteBuffer bytes);
  
}