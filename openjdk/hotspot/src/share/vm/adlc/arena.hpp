/*
 * Copyright 1998-2002 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 */

// All classes in the virtual machine must be subclassed
// by one of the following allocation classes:
//
//
// For objects allocated in the C-heap (managed by: free & malloc).
// - CHeapObj
//
//
// For embedded objects.
// - ValueObj
//
// For classes used as name spaces.
// - AllStatic
//

class CHeapObj {
 public:
  void* operator new(size_t size);
  void  operator delete(void* p);
  void* new_array(size_t size);
};


// Base class for objects used as value objects.
// Calling new or delete will result in fatal error.

class ValueObj {
 public:
  void* operator new(size_t size);
  void operator delete(void* p);
};

// Base class for classes that constitute name spaces.

class AllStatic {
 public:
  void* operator new(size_t size);
  void operator delete(void* p);
};


//------------------------------Chunk------------------------------------------
// Linked list of raw memory chunks
class Chunk: public CHeapObj {
 public:
  void* operator new(size_t size, size_t length);
  void  operator delete(void* p, size_t length);
  Chunk(size_t length);

  enum {
      init_size =  1*1024,      // Size of first chunk
      size      = 32*1024       // Default size of an Arena chunk (following the first)
  };
  Chunk*       _next;           // Next Chunk in list
  size_t       _len;            // Size of this Chunk

  void chop();                  // Chop this chunk
  void next_chop();             // Chop next chunk

  // Boundaries of data area (possibly unused)
  char* bottom() const { return ((char*) this) + sizeof(Chunk);  }
  char* top()    const { return bottom() + _len; }
};


//------------------------------Arena------------------------------------------
// Fast allocation of memory
class Arena: public CHeapObj {
protected:
  friend class ResourceMark;
  friend class HandleMark;
  friend class NoHandleMark;
  Chunk *_first;                // First chunk
  Chunk *_chunk;                // current chunk
  char *_hwm, *_max;            // High water mark and max in current chunk
  void* grow(size_t x);         // Get a new Chunk of at least size x
  size_t _size_in_bytes;          // Size of arena (used for memory usage tracing)
public:
  Arena();
  Arena(size_t init_size);
  Arena(Arena *old);
  ~Arena()                      { _first->chop(); }
  char* hwm() const             { return _hwm; }

  // Fast allocate in the arena.  Common case is: pointer test + increment.
  void* Amalloc(size_t x) {
#ifdef _LP64
    x = (x + (8-1)) & ((unsigned)(-8));
#else
    x = (x + (4-1)) & ((unsigned)(-4));
#endif
    if (_hwm + x > _max) {
      return grow(x);
    } else {
      char *old = _hwm;
      _hwm += x;
      return old;
    }
  }
  // Further assume size is padded out to words
  // Warning:  in LP64, Amalloc_4 is really Amalloc_8
  void *Amalloc_4(size_t x) {
    assert( (x&(sizeof(char*)-1)) == 0, "misaligned size" );
    if (_hwm + x > _max) {
      return grow(x);
    } else {
      char *old = _hwm;
      _hwm += x;
      return old;
    }
  }

  // Fast delete in area.  Common case is: NOP (except for storage reclaimed)
  void Afree(void *ptr, size_t size) {
    if (((char*)ptr) + size == _hwm) _hwm = (char*)ptr;
  }

  void *Acalloc( size_t items, size_t x );
  void *Arealloc( void *old_ptr, size_t old_size, size_t new_size );

  // Reset this Arena to empty, and return this Arenas guts in a new Arena.
  Arena *reset(void);

  // Determine if pointer belongs to this Arena or not.
  bool contains( const void *ptr ) const;

  // Total of all chunks in use (not thread-safe)
  size_t used() const;

  // Total # of bytes used
  size_t size_in_bytes() const         {  return _size_in_bytes; }
  void   set_size_in_bytes(size_t size)  { _size_in_bytes = size;   }
};
