/**
 * Copyright 2006 - 2008 BLUEBAMBOO International Inc. 
 *           All rights reserved.
 *
 *
 * BLUEBAMBOO PROPRIETARY/CONFIDENTIAL.
 *
 */

/** 
 * File name:            LinkedList.java
 * 
 * Originally developed: Commands.Wu
 *
 * Create date :         2006-10-23
 * 
 * Description:          This LinkedList class.
 * Linked list implementation of the <tt>List</tt> interface.  Implements all
 * optional list operations, and permits all elements (including
 * <tt>null</tt>).  In addition to implementing the <tt>List</tt> interface,
 * the <tt>LinkedList</tt> class provides uniformly named methods to
 * <tt>get</tt>, <tt>remove</tt> and <tt>insert</tt> an element at the
 * beginning and end of the list.  These operations allow linked lists to be
 * used as a stack, queue, or double-ended queue (deque).<p>
 *
 * All of the stack/queue/deque operations could be easily recast in terms of
 * the standard list operations.  They're included here primarily for
 * convenience, though they may run slightly faster than the equivalent List
 * operations.<p>
 *
 * All of the operations perform as could be expected for a doubly-linked
 * list.  Operations that index into the list will traverse the list from
 * the begining or the end, whichever is closer to the specified index.<p>
 * 
 * Version:              0.1
 * 
 * Contributors:         
 * 
 * Modifications: 
 * name          version           reasons
 * 
 */

package com.commands.util;

import java.util.NoSuchElementException;


public class LinkedListSimply
{
  private transient Entry header = new Entry(null, null, null);
  private transient int currentSize = 0;

  /**
   * Constructs an empty list.
   */
  public LinkedListSimply()
  {
    header.next = header.previous = header;
  }

  /**
   * Returns the number of elements in this list.
   *
   * @return the number of elements in this list.
   */
  public int size()
  {
    return currentSize;
  }

  /**
   * Appends the specified element to the end of this list.
   *
   * @param o element to be appended to this list.
   * @return <tt>true</tt> (as per the general contract of
   * <tt>Collection.add</tt>).
   */
  public boolean add(Object o)
  {
    addBefore(o, header);
    return true;
  }

  /**
   * Removes the first occurrence of the specified element in this list.  If
   * the list does not contain the element, it is unchanged.  More formally,
   * removes the element with the lowest index <tt>i</tt> such that
   * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt> (if such an
   * element exists).
   *
   * @param o element to be removed from this list, if present.
   * @return <tt>true</tt> if the list contained the specified element.
   */
  public boolean remove(Object o)
  {
    if (o == null)
    {
      for (Entry e = header.next; e != header; e = e.next)
      {
        if (e.element == null)
        {
          remove(e);
          return true;
        }
      }
    } else
    {
      for (Entry e = header.next; e != header; e = e.next)
      {
        if (o.equals(e.element))
        {
          remove(e);
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Removes all of the elements from this list.
   */
  public void clear()
  {
    header.next = header.previous = header;
    currentSize = 0;
  }

  // Positional Access Operations

  /**
   * Returns the element at the specified position in this list.
   *
   * @param index index of element to return.
   * @return the element at the specified position in this list.
   * 
   * @throws IndexOutOfBoundsException if the specified index is is out of
   * range (<tt>index &lt; 0 || index &gt;= currentSize()</tt>).
   */
  public Object get(int index)
  {
    return entry(index).element;
  }

  /**
   * Replaces the element at the specified position in this list with the
   * specified element.
   *
   * @param index index of element to replace.
   * @param element element to be stored at the specified position.
   * @return the element previously at the specified position.
   * @throws IndexOutOfBoundsException if the specified index is out of
   *		  range (<tt>index &lt; 0 || index &gt;= currentSize()</tt>).
   */
  public Object set(int index, Object element)
  {
    Entry e = entry(index);
    Object oldVal = e.element;
    e.element = element;
    return oldVal;
  }

  /**
   * Removes the element at the specified position in this list.  Shifts any
   * subsequent elements to the left (subtracts one from their indices).
   * Returns the element that was removed from the list.
   *
   * @param index the index of the element to removed.
   * @return the element previously at the specified position.
   * 
   * @throws IndexOutOfBoundsException if the specified index is out of
   * 		  range (<tt>index &lt; 0 || index &gt;= currentSize()</tt>).
   */
  public Object remove(int index)
  {
    Entry e = entry(index);
    remove(e);
    return e.element;
  }

  /**
   * Return the indexed entry.
   */
  private Entry entry(int index)
  {
    if (index < 0 || index >= currentSize)
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + currentSize);
    Entry e = header;
    if (index < (currentSize >> 1))
    {
      for (int i = 0; i <= index; i++)
        e = e.next;
    } else
    {
      for (int i = currentSize; i > index; i--)
        e = e.previous;
    }
    return e;
  }

  private static class Entry
  {
    Object element;
    Entry next;
    Entry previous;

    Entry(Object element, Entry next, Entry previous)
    {
      this.element = element;
      this.next = next;
      this.previous = previous;
    }
  }

  private Entry addBefore(Object o, Entry e)
  {
    Entry newEntry = new Entry(o, e, e.previous);
    newEntry.previous.next = newEntry;
    newEntry.next.previous = newEntry;
    currentSize++;
    return newEntry;
  }

  private void remove(Entry e)
  {
    if (e == header)
      throw new NoSuchElementException();

    e.previous.next = e.next;
    e.next.previous = e.previous;
    currentSize--;
  }

}
