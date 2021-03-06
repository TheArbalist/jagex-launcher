/*
 * Copyright 2003-2005 Sun Microsystems, Inc.  All Rights Reserved.
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


# include "incls/_precompiled.incl"
# include "incls/_jvmtiGetLoadedClasses.cpp.incl"


// The closure for GetLoadedClasses and GetClassLoaderClasses
class JvmtiGetLoadedClassesClosure : public StackObj {
  // Since the SystemDictionary::classes_do callback
  // doesn't pass a closureData pointer,
  // we use a thread-local slot to hold a pointer to
  // a stack allocated instance of this structure.
 private:
  jobject _initiatingLoader;
  int     _count;
  Handle* _list;
  int     _index;

 private:
  // Getting and setting the thread local pointer
  static JvmtiGetLoadedClassesClosure* get_this() {
    JvmtiGetLoadedClassesClosure* result = NULL;
    JavaThread* thread = JavaThread::current();
    result = thread->get_jvmti_get_loaded_classes_closure();
    return result;
  }
  static void set_this(JvmtiGetLoadedClassesClosure* that) {
    JavaThread* thread = JavaThread::current();
    thread->set_jvmti_get_loaded_classes_closure(that);
  }

 public:
  // Constructor/Destructor
  JvmtiGetLoadedClassesClosure() {
    JvmtiGetLoadedClassesClosure* that = get_this();
    assert(that == NULL, "JvmtiGetLoadedClassesClosure in use");
    _initiatingLoader = NULL;
    _count = 0;
    _list = NULL;
    _index = 0;
    set_this(this);
  }

  JvmtiGetLoadedClassesClosure(jobject initiatingLoader) {
    JvmtiGetLoadedClassesClosure* that = get_this();
    assert(that == NULL, "JvmtiGetLoadedClassesClosure in use");
    _initiatingLoader = initiatingLoader;
    _count = 0;
    _list = NULL;
    _index = 0;
    set_this(this);
  }

  ~JvmtiGetLoadedClassesClosure() {
    JvmtiGetLoadedClassesClosure* that = get_this();
    assert(that != NULL, "JvmtiGetLoadedClassesClosure not found");
    set_this(NULL);
    _initiatingLoader = NULL;
    _count = 0;
    if (_list != NULL) {
      FreeHeap(_list);
      _list = NULL;
    }
    _index = 0;
  }

  // Accessors.
  jobject get_initiatingLoader() {
    return _initiatingLoader;
  }

  int get_count() {
    return _count;
  }

  void set_count(int value) {
    _count = value;
  }

  Handle* get_list() {
    return _list;
  }

  void set_list(Handle* value) {
    _list = value;
  }

  int get_index() {
    return _index;
  }

  void set_index(int value) {
    _index = value;
  }

  Handle get_element(int index) {
    if ((_list != NULL) && (index < _count)) {
      return _list[index];
    } else {
      assert(false, "empty get_element");
      return Handle();
    }
  }

  void set_element(int index, Handle value) {
    if ((_list != NULL) && (index < _count)) {
      _list[index] = value;
    } else {
      assert(false, "bad set_element");
    }
  }

  // Other predicates
  bool available() {
    return (_list != NULL);
  }

#ifdef ASSERT
  // For debugging.
  void check(int limit) {
    for (int i = 0; i < limit; i += 1) {
      assert(Universe::heap()->is_in(get_element(i)()), "check fails");
    }
  }
#endif

  // Public methods that get called within the scope of the closure
  void allocate() {
    _list = NEW_C_HEAP_ARRAY(Handle, _count);
    assert(_list != NULL, "Out of memory");
    if (_list == NULL) {
      _count = 0;
    }
  }

  void extract(JvmtiEnv *env, jclass* result) {
    for (int index = 0; index < _count; index += 1) {
      result[index] = (jclass) env->jni_reference(get_element(index));
    }
  }

  // Finally, the static methods that are the callbacks
  static void increment(klassOop k) {
    JvmtiGetLoadedClassesClosure* that = JvmtiGetLoadedClassesClosure::get_this();
    if (that->get_initiatingLoader() == NULL) {
      for (klassOop l = k; l != NULL; l = Klass::cast(l)->array_klass_or_null()) {
        that->set_count(that->get_count() + 1);
      }
    } else if (k != NULL) {
      // if initiating loader not null, just include the instance with 1 dimension
      that->set_count(that->get_count() + 1);
    }
  }

  static void increment_with_loader(klassOop k, oop loader) {
    JvmtiGetLoadedClassesClosure* that = JvmtiGetLoadedClassesClosure::get_this();
    if (loader == JNIHandles::resolve(that->get_initiatingLoader())) {
      for (klassOop l = k; l != NULL; l = Klass::cast(l)->array_klass_or_null()) {
        that->set_count(that->get_count() + 1);
      }
    }
  }

  static void prim_array_increment_with_loader(klassOop array, oop loader) {
    JvmtiGetLoadedClassesClosure* that = JvmtiGetLoadedClassesClosure::get_this();
    if (loader == JNIHandles::resolve(that->get_initiatingLoader())) {
      that->set_count(that->get_count() + 1);
    }
  }

  static void add(klassOop k) {
    JvmtiGetLoadedClassesClosure* that = JvmtiGetLoadedClassesClosure::get_this();
    if (that->available()) {
      if (that->get_initiatingLoader() == NULL) {
        for (klassOop l = k; l != NULL; l = Klass::cast(l)->array_klass_or_null()) {
          oop mirror = Klass::cast(l)->java_mirror();
          that->set_element(that->get_index(), mirror);
          that->set_index(that->get_index() + 1);
        }
      } else if (k != NULL) {
        // if initiating loader not null, just include the instance with 1 dimension
        oop mirror = Klass::cast(k)->java_mirror();
        that->set_element(that->get_index(), mirror);
        that->set_index(that->get_index() + 1);
      }
    }
  }

  static void add_with_loader(klassOop k, oop loader) {
    JvmtiGetLoadedClassesClosure* that = JvmtiGetLoadedClassesClosure::get_this();
    if (that->available()) {
      if (loader == JNIHandles::resolve(that->get_initiatingLoader())) {
        for (klassOop l = k; l != NULL; l = Klass::cast(l)->array_klass_or_null()) {
          oop mirror = Klass::cast(l)->java_mirror();
          that->set_element(that->get_index(), mirror);
          that->set_index(that->get_index() + 1);
        }
      }
    }
  }

  // increment the count for the given basic type array class (and any
  // multi-dimensional arrays). For example, for [B we check for
  // [[B, [[[B, .. and the count is incremented for each one that exists.
  static void increment_for_basic_type_arrays(klassOop k) {
    JvmtiGetLoadedClassesClosure* that = JvmtiGetLoadedClassesClosure::get_this();
    assert(that != NULL, "no JvmtiGetLoadedClassesClosure");
    for (klassOop l = k; l != NULL; l = Klass::cast(l)->array_klass_or_null()) {
      that->set_count(that->get_count() + 1);
    }
  }

  // add the basic type array class and its multi-dimensional array classes to the list
  static void add_for_basic_type_arrays(klassOop k) {
    JvmtiGetLoadedClassesClosure* that = JvmtiGetLoadedClassesClosure::get_this();
    assert(that != NULL, "no JvmtiGetLoadedClassesClosure");
    assert(that->available(), "no list");
    for (klassOop l = k; l != NULL; l = Klass::cast(l)->array_klass_or_null()) {
      oop mirror = Klass::cast(l)->java_mirror();
      that->set_element(that->get_index(), mirror);
      that->set_index(that->get_index() + 1);
    }
  }
};


jvmtiError
JvmtiGetLoadedClasses::getLoadedClasses(JvmtiEnv *env, jint* classCountPtr, jclass** classesPtr) {
  // Since SystemDictionary::classes_do only takes a function pointer
  // and doesn't call back with a closure data pointer,
  // we can only pass static methods.

  JvmtiGetLoadedClassesClosure closure;
  {
    // To get a consistent list of classes we need MultiArray_lock to ensure
    // array classes aren't created, and SystemDictionary_lock to ensure that
    // classes aren't added to the system dictionary,
    MutexLocker ma(MultiArray_lock);
    MutexLocker sd(SystemDictionary_lock);

    // First, count the classes
    SystemDictionary::classes_do(&JvmtiGetLoadedClassesClosure::increment);
    Universe::basic_type_classes_do(&JvmtiGetLoadedClassesClosure::increment);
    // Next, fill in the classes
    closure.allocate();
    SystemDictionary::classes_do(&JvmtiGetLoadedClassesClosure::add);
    Universe::basic_type_classes_do(&JvmtiGetLoadedClassesClosure::add);
    // Drop the SystemDictionary_lock, so the results could be wrong from here,
    // but we still have a snapshot.
  }
  // Post results
  jclass* result_list;
  jvmtiError err = env->Allocate(closure.get_count() * sizeof(jclass),
                                 (unsigned char**)&result_list);
  if (err != JVMTI_ERROR_NONE) {
    return err;
  }
  closure.extract(env, result_list);
  *classCountPtr = closure.get_count();
  *classesPtr = result_list;
  return JVMTI_ERROR_NONE;
}

jvmtiError
JvmtiGetLoadedClasses::getClassLoaderClasses(JvmtiEnv *env, jobject initiatingLoader,
                                             jint* classCountPtr, jclass** classesPtr) {
  // Since SystemDictionary::classes_do only takes a function pointer
  // and doesn't call back with a closure data pointer,
  // we can only pass static methods.
  JvmtiGetLoadedClassesClosure closure(initiatingLoader);
  {
    // To get a consistent list of classes we need MultiArray_lock to ensure
    // array classes aren't created, and SystemDictionary_lock to ensure that
    // classes aren't added to the system dictionary,
    MutexLocker ma(MultiArray_lock);
    MutexLocker sd(SystemDictionary_lock);
    // First, count the classes in the system dictionary which have this loader recorded
    // as an initiating loader. For basic type arrays this information is not recorded
    // so GetClassLoaderClasses will return all of the basic type arrays. This is okay
    // because the defining loader for basic type arrays is always the boot class loader
    // and these classes are "visible" to all loaders.
    SystemDictionary::classes_do(&JvmtiGetLoadedClassesClosure::increment_with_loader);
    Universe::basic_type_classes_do(&JvmtiGetLoadedClassesClosure::increment_for_basic_type_arrays);
    // Next, fill in the classes
    closure.allocate();
    SystemDictionary::classes_do(&JvmtiGetLoadedClassesClosure::add_with_loader);
    Universe::basic_type_classes_do(&JvmtiGetLoadedClassesClosure::add_for_basic_type_arrays);
    // Drop the SystemDictionary_lock, so the results could be wrong from here,
    // but we still have a snapshot.
  }
  // Post results
  jclass* result_list;
  jvmtiError err = env->Allocate(closure.get_count() * sizeof(jclass),
                                 (unsigned char**)&result_list);
  if (err != JVMTI_ERROR_NONE) {
    return err;
  }
  closure.extract(env, result_list);
  *classCountPtr = closure.get_count();
  *classesPtr = result_list;
  return JVMTI_ERROR_NONE;
}
