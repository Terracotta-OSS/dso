/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.objectserver.managedobject.bytecode;

import com.tc.asm.ClassWriter;
import com.tc.asm.FieldVisitor;
import com.tc.asm.Label;
import com.tc.asm.MethodVisitor;
import com.tc.asm.Opcodes;
import com.tc.object.LiteralValues;
import com.tc.objectserver.managedobject.HasParentIdStorage;
import com.tc.util.AdaptedClassDumper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PhysicalStateClassLoader extends ClassLoader implements Opcodes {

  private static final String[] HAS_PARENT_ID_INTERFACES = new String[] { HasParentIdStorage.class.getName()
                                                             .replace('.', '/') };

  private static final String   PARENT_ID_FIELD          = "parentId";

  public PhysicalStateClassLoader(ClassLoader parent) {
    super(parent);
  }

  public PhysicalStateClassLoader() {
    super();
  }

  public byte[] createClassBytes(ClassSpec cs, Collection<FieldType> fields) {
    byte data[] = basicCreateClassBytes(cs, fields);
    AdaptedClassDumper.INSTANCE.write(cs.getGeneratedClassName(), data);
    return data;
  }

  public Class defineClassFromBytes(String className, int classId, byte[] clazzBytes, int offset, int length) {
    Class clazz = defineClass(className, clazzBytes, offset, length);
    return clazz;
  }

  private byte[] basicCreateClassBytes(ClassSpec cs, Collection<FieldType> fields) {
    String classNameSlash = cs.getGeneratedClassName().replace('.', '/');
    String superClassNameSlash = cs.getSuperClassName().replace('.', '/');
    ClassWriter cw = new ClassWriter(0); // don't compute maxs

    String[] interfaces = cs.generateParentIdStorage() ? HAS_PARENT_ID_INTERFACES : null;

    cw.visit(V1_5, ACC_PUBLIC | ACC_SUPER, classNameSlash, null, superClassNameSlash, interfaces);

    createConstructor(cw, superClassNameSlash);

    if (cs.generateParentIdStorage()) {
      createParentIDField(cw);
      createGetParentIDMethod(cw, classNameSlash);
      createSetParentIDMethod(cw, classNameSlash);
    }

    createFields(cw, fields);
    createGetClassNameMethod(cw, classNameSlash, cs);
    createGetLoaderDescriptionMethod(cw, classNameSlash, cs);
    createGetObjectReferencesMethod(cw, classNameSlash, cs, superClassNameSlash, fields);
    createBasicSetMethod(cw, classNameSlash, cs, superClassNameSlash, fields);
    createBasicDehydrateMethod(cw, classNameSlash, cs, superClassNameSlash, fields);
    createAddValuesMethod(cw, classNameSlash, cs, superClassNameSlash, fields);
    createWriteObjectMethod(cw, classNameSlash, cs, superClassNameSlash, fields);
    createReadObjectMethod(cw, classNameSlash, cs, superClassNameSlash, fields);
    createGetClassIdMethod(cw, classNameSlash, cs);

    cw.visitEnd();
    return cw.toByteArray();
  }

  // *************************************************************************************
  // The Code generated by this method looks (kind of) this.
  //
  // public String getLoaderDescription() {
  // return "System.ext";
  // }
  // *************************************************************************************
  private void createGetLoaderDescriptionMethod(ClassWriter cw, String classNameSlash, ClassSpec cs) {
    if (!cs.isDirectSubClassOfPhysicalMOState()) {
      // We dont have to regenerate this method as the super class would have it.
      return;
    }
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "getLoaderDescription", "()Ljava/lang/String;", null, null);
    mv.visitCode();
    mv.visitLdcInsn(cs.getLoaderDesc());
    mv.visitInsn(ARETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }

  // *************************************************************************************
  // The Code generated by this method looks (kind of) this.
  //
  // public String getClassName() {
  // return "com.tc.className";
  // }
  // *************************************************************************************
  private void createGetClassNameMethod(ClassWriter cw, String classNameSlash, ClassSpec cs) {
    if (!cs.isDirectSubClassOfPhysicalMOState()) {
      // We dont have to regenerate this method as the super class would have it.
      return;
    }
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "getClassName", "()Ljava/lang/String;", null, null);
    mv.visitCode();
    mv.visitLdcInsn(cs.getClassName());
    mv.visitInsn(ARETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }

  // *************************************************************************************
  // The Code generated by this method looks (kind of) this.
  //
  // protected int getClassId() {
  // return 8;
  // }
  // *************************************************************************************
  private void createGetClassIdMethod(ClassWriter cw, String classNameSlash, ClassSpec cs) {
    MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "getClassId", "()I", null, null);
    mv.visitLdcInsn(Integer.valueOf(cs.getClassID()));
    mv.visitInsn(IRETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }

  // *************************************************************************************
  // The Code generated by this method looks (kind of) this.
  //
  // long x;
  // Object o;
  // char c;
  // public void writeObject(ObjectOutput out) throws IOException {
  // out.writeLong(x);
  // out.writeObject(o);
  // out.writeChar(c);
  // }
  // *************************************************************************************
  private void createWriteObjectMethod(ClassWriter cw, String classNameSlash, ClassSpec cs, String superClassNameSlash,
                                       Collection<FieldType> fields) {
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "writeObject", "(Ljava/io/ObjectOutput;)V", null,
                                      new String[] { "java/io/IOException" });
    mv.visitCode();

    if (!cs.isDirectSubClassOfPhysicalMOState()) {
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKESPECIAL, superClassNameSlash, "writeObject", "(Ljava/io/ObjectOutput;)V");
    }

    for (FieldType f : fields) {
      mv.visitVarInsn(ALOAD, 1);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, classNameSlash, f.getLocalFieldName(), f.getType().getTypeDesc());
      LiteralValues fType = f.getType();
      mv.visitMethodInsn(INVOKEINTERFACE, "java/io/ObjectOutput", fType.getOutputMethodName(),
                         fType.getOutputMethodDescriptor());
    }
    mv.visitInsn(RETURN);
    mv.visitMaxs(3, 2);
    mv.visitEnd();
  }

  // *************************************************************************************
  // The Code generated by this method looks (kind of) this.
  //
  // long x;
  // Object o;
  // char c;
  //
  // public void readObject(ObjectInput in) throws IOException, ClassNotFoundException {
  // x = in.readLong();
  // o = in.readObject();
  // c = in.readChar();
  // }
  // *************************************************************************************
  private void createReadObjectMethod(ClassWriter cw, String classNameSlash, ClassSpec cs, String superClassNameSlash,
                                      Collection<FieldType> fields) {
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "readObject", "(Ljava/io/ObjectInput;)V", null, new String[] {
        "java/io/IOException", "java/lang/ClassNotFoundException" });
    mv.visitCode();

    if (!cs.isDirectSubClassOfPhysicalMOState()) {
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKESPECIAL, superClassNameSlash, "readObject", "(Ljava/io/ObjectInput;)V");
    }

    for (FieldType f : fields) {
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 1);
      LiteralValues fType = f.getType();
      mv.visitMethodInsn(INVOKEINTERFACE, "java/io/ObjectInput", fType.getInputMethodName(),
                         fType.getInputMethodDescriptor());
      mv.visitFieldInsn(PUTFIELD, classNameSlash, f.getLocalFieldName(), f.getType().getTypeDesc());
    }
    mv.visitInsn(RETURN);
    mv.visitMaxs(3, 2);
    mv.visitEnd();
  }

  // *************************************************************************************
  // The Code generated by this method looks (kind of) this.
  //
  // long x;
  // Object o;
  // public Map addValues(Map map) {
  // map.put("x", Long.valueOf(x));
  // map.put("o", o);
  // return map;
  // }
  // *************************************************************************************
  private void createAddValuesMethod(ClassWriter cw, String classNameSlash, ClassSpec cs, String superClassNameSlash,
                                     Collection<FieldType> fields) {
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "addValues", "(Ljava/util/Map;)Ljava/util/Map;", null, null);
    mv.visitCode();

    if (!cs.isDirectSubClassOfPhysicalMOState()) {
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKESPECIAL, superClassNameSlash, "addValues", "(Ljava/util/Map;)Ljava/util/Map;");
      mv.visitInsn(POP);
    }

    for (FieldType f : fields) {
      mv.visitVarInsn(ALOAD, 1);
      mv.visitLdcInsn(f.getQualifiedName());
      getObjectFor(mv, classNameSlash, f);
      mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put",
                         "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
      mv.visitInsn(POP);
    }
    mv.visitVarInsn(ALOAD, 1);
    mv.visitInsn(ARETURN);

    mv.visitMaxs(6, 2);
    mv.visitEnd();

  }

  // *************************************************************************************
  // The Code generated by this method looks (kind of) this.
  //
  // long x;
  // Object o;
  // protected void basicDehydrate(DNAWriter writer) {
  // writer.addPhysicalAction("x", Long.valueOf(x), false);
  // writer.addPhysicalAction("o", o, true);
  // }
  // *************************************************************************************
  private void createBasicDehydrateMethod(ClassWriter cw, String classNameSlash, ClassSpec cs,
                                          String superClassNameSlash, Collection<FieldType> fields) {
    MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "basicDehydrate", "(Lcom/tc/object/dna/api/DNAWriter;)V", null,
                                      null);
    mv.visitCode();

    if (!cs.isDirectSubClassOfPhysicalMOState()) {
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKESPECIAL, superClassNameSlash, "basicDehydrate", "(Lcom/tc/object/dna/api/DNAWriter;)V");
    }

    for (FieldType f : fields) {
      mv.visitVarInsn(ALOAD, 1);
      mv.visitLdcInsn(f.getQualifiedName());
      getObjectFor(mv, classNameSlash, f);
      if (f.canBeReferenced()) {
        mv.visitInsn(ICONST_1); // true
      } else {
        mv.visitInsn(ICONST_0); // false
      }
      // XXX:: We are calling DNAWriter methods from instrumented code !
      mv.visitMethodInsn(INVOKEINTERFACE, "com/tc/object/dna/api/DNAWriter", "addPhysicalAction",
                         "(Ljava/lang/String;Ljava/lang/Object;Z)V");
    }
    mv.visitInsn(RETURN);

    mv.visitMaxs(6, 2);
    mv.visitEnd();
  }

  // *************************************************************************************
  // The Code generated by this method looks (kind of) this.
  //
  // long x;
  // Object o;
  // protected Object basicSet(String f, Object value) {
  // if ("x".equals(f)) {
  // Object old = Long.valueOf(x);
  // x = ((Long) value).longValue();
  // return old;
  // }
  // if("o".equals(f)) {
  // Object old = o;
  // o = value;
  // return old;
  // }
  // throw new ClassNotCompatableException("Not found ! field = " + f + " value = " + value);
  // }
  // *************************************************************************************
  private void createBasicSetMethod(ClassWriter cw, String classNameSlash, ClassSpec cs, String superClassNameSlash,
                                    Collection<FieldType> fields) {
    MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "basicSet",
                                      "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
    mv.visitCode();

    for (FieldType f : fields) {
      mv.visitLdcInsn(f.getQualifiedName());
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
      Label l1 = new Label();
      mv.visitJumpInsn(IFEQ, l1);
      getObjectFor(mv, classNameSlash, f);
      mv.visitVarInsn(ASTORE, 3);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 2);
      getValueFrom(mv, classNameSlash, f);
      mv.visitFieldInsn(PUTFIELD, classNameSlash, f.getLocalFieldName(), f.getType().getTypeDesc());
      mv.visitVarInsn(ALOAD, 3);
      mv.visitInsn(ARETURN);
      mv.visitLabel(l1);
    }

    if (cs.isDirectSubClassOfPhysicalMOState()) {
      // throw Assertion Error
      mv.visitTypeInsn(NEW, "com/tc/objectserver/managedobject/bytecode/ClassNotCompatableException");
      mv.visitInsn(DUP);
      mv.visitTypeInsn(NEW, "java/lang/StringBuffer");
      mv.visitInsn(DUP);
      mv.visitLdcInsn("Not found ! field = ");
      mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuffer", "<init>", "(Ljava/lang/String;)V");
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                         "(Ljava/lang/String;)Ljava/lang/StringBuffer;");
      mv.visitLdcInsn(" value = ");
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                         "(Ljava/lang/String;)Ljava/lang/StringBuffer;");
      mv.visitVarInsn(ALOAD, 2);
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                         "(Ljava/lang/Object;)Ljava/lang/StringBuffer;");
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuffer", "toString", "()Ljava/lang/String;");
      mv.visitMethodInsn(INVOKESPECIAL, "com/tc/objectserver/managedobject/bytecode/ClassNotCompatableException",
                         "<init>", "(Ljava/lang/String;)V");
      mv.visitInsn(ATHROW);
    } else {
      // Call super class's implementation
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitVarInsn(ALOAD, 2);
      mv.visitMethodInsn(INVOKESPECIAL, superClassNameSlash, "basicSet",
                         "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;");
      mv.visitInsn(ARETURN);
    }

    mv.visitMaxs(5, 4);
    mv.visitEnd();
  }

  /**
   * This method generates code to the object reference from the top of the stack and convert it into a primitive type
   * (if needed) and leave that value in the top of the stack.
   */
  private void getValueFrom(MethodVisitor mv, String classNameSlash, FieldType f) {
    String classOnStack = f.getType().getClassNameSlashForPrimitives();
    if ("java/lang/Object".equals(classOnStack)) { return; }
    mv.visitTypeInsn(CHECKCAST, classOnStack);
    mv.visitMethodInsn(INVOKEVIRTUAL, classOnStack, f.getType().getMethodNameForPrimitives(), "()"
                                                                                              + f.getType()
                                                                                                  .getTypeDesc());

  }

  /**
   * This method generates code so that the object equivalent of the field is left on the stack.
   */
  private void getObjectFor(MethodVisitor mv, String classNameSlash, FieldType f) {
    String classToReturn = f.getType().getClassNameSlashForPrimitives();
    if ("java/lang/Object".equals(classToReturn)) {
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, classNameSlash, f.getLocalFieldName(), f.getType().getTypeDesc());
      return;
    }
    String fieldTypeDesc = f.getType().getTypeDesc();
    String constructorDesc = "(" + fieldTypeDesc + ")V";
    mv.visitTypeInsn(NEW, classToReturn);
    mv.visitInsn(DUP);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, classNameSlash, f.getLocalFieldName(), fieldTypeDesc);
    mv.visitMethodInsn(INVOKESPECIAL, classToReturn, "<init>", constructorDesc);

  }

  // *************************************************************************************
  // The Code generated by this method looks (kind of) this.
  //
  // private Object oid1;
  // public Set getObjectReferences() {
  // Set result = new HashSet(25);
  // if (oid1 instanceof ObjectID && !((ObjectID)oid1).isNull() ) {
  // result.add(oid1);
  // }
  // return result;
  // }
  // *************************************************************************************
  private void createGetObjectReferencesMethod(ClassWriter cw, String classNameSlash, ClassSpec cs,
                                               String superClassNameSlash, Collection<FieldType> fields) {
    List referenceFields = new ArrayList(fields.size());
    for (FieldType f : fields) {
      if (f.getType() == LiteralValues.OBJECT_ID) {
        referenceFields.add(f);
      }
    }

    // There is no references in this object and it is not a direct subclass of Physical Managed Object State
    if (referenceFields.size() == 0 && !cs.generateParentIdStorage() && !cs.isDirectSubClassOfPhysicalMOState()) {
      // The parent object has the necessary implementations
      return;
    }

    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "getObjectReferences", "()Ljava/util/Set;", null, null);
    mv.visitCode();

    // There is no references in this object
    if (referenceFields.size() == 0 && !cs.generateParentIdStorage()) {
      mv.visitFieldInsn(GETSTATIC, "java/util/Collections", "EMPTY_SET", "Ljava/util/Set;");
      mv.visitInsn(ARETURN);
      mv.visitMaxs(1, 1);
      mv.visitEnd();
      return;
    }

    int size = referenceFields.size();
    size += cs.generateParentIdStorage() ? 1 : 0;

    mv.visitTypeInsn(NEW, "java/util/HashSet");
    mv.visitInsn(DUP);
    mv.visitLdcInsn(Integer.valueOf(size));
    mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashSet", "<init>", "(I)V");
    mv.visitVarInsn(ASTORE, 1);

    if (!cs.isDirectSubClassOfPhysicalMOState()) {
      mv.visitVarInsn(ALOAD, 0);
      mv.visitMethodInsn(INVOKESPECIAL, superClassNameSlash, "getObjectReferences", "()Ljava/util/Set;");
      mv.visitVarInsn(ASTORE, 2);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitVarInsn(ALOAD, 2);
      mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "addAll", "(Ljava/util/Collection;)Z");
      mv.visitInsn(POP);
    }

    for (Iterator i = referenceFields.iterator(); i.hasNext();) {
      FieldType f = (FieldType) i.next();
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, classNameSlash, f.getLocalFieldName(), f.getType().getTypeDesc());
      mv.visitTypeInsn(INSTANCEOF, "com/tc/object/ObjectID");
      Label l2 = new Label();
      mv.visitJumpInsn(IFEQ, l2);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, classNameSlash, f.getLocalFieldName(), f.getType().getTypeDesc());
      mv.visitTypeInsn(CHECKCAST, "com/tc/object/ObjectID");
      mv.visitMethodInsn(INVOKEVIRTUAL, "com/tc/object/ObjectID", "isNull", "()Z");
      mv.visitJumpInsn(IFNE, l2);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, classNameSlash, f.getLocalFieldName(), f.getType().getTypeDesc());
      mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z");
      mv.visitInsn(POP);
      mv.visitLabel(l2);
    }
    if (cs.generateParentIdStorage()) {
      // add parentID too
      mv.visitVarInsn(ALOAD, 1);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, classNameSlash, PARENT_ID_FIELD, "Lcom/tc/object/ObjectID;");
      mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z");
      mv.visitInsn(POP);
    }
    mv.visitVarInsn(ALOAD, 1);
    mv.visitInsn(ARETURN);
    mv.visitMaxs(3, 3);
    mv.visitEnd();
  }

  private void createFields(ClassWriter cw, Collection<FieldType> fields) {
    for (FieldType f : fields) {
      FieldVisitor fv = cw.visitField(ACC_PRIVATE, f.getLocalFieldName(), f.getType().getTypeDesc(), null, null);
      fv.visitEnd();
    }
  }

  private void createParentIDField(ClassWriter cw) {
    FieldVisitor fv = cw.visitField(ACC_PRIVATE, PARENT_ID_FIELD, "Lcom/tc/object/ObjectID;", null, null);
    fv.visitEnd();
  }

  // *************************************************************************************
  // The Code generated by this method looks (kind of) this.
  //
  // ObjectID parentId;
  // public final ObjectID getParentID() {
  // return parentId;
  // }
  // *************************************************************************************
  private void createGetParentIDMethod(ClassWriter cw, String classNameSlash) {
    // The method is "final" since the parentID storage should only exist at one level in the hierarchy
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_FINAL, "getParentID", "()Lcom/tc/object/ObjectID;", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, classNameSlash, PARENT_ID_FIELD, "Lcom/tc/object/ObjectID;");
    mv.visitInsn(ARETURN);
    mv.visitMaxs(2, 1);
    mv.visitEnd();
  }

  // *************************************************************************************
  // The Code generated by this method looks (kind of) this.
  //
  // ObjectID parentId;
  // public final void setParentID(ObjectID id) {
  // parentId = id;
  // }
  // *************************************************************************************
  private void createSetParentIDMethod(ClassWriter cw, String classNameSlash) {
    // The method is "final" since the parentID storage should only exist at one level in the hierarchy
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_FINAL, "setParentID", "(Lcom/tc/object/ObjectID;)V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitFieldInsn(PUTFIELD, classNameSlash, PARENT_ID_FIELD, "Lcom/tc/object/ObjectID;");
    mv.visitInsn(RETURN);
    mv.visitMaxs(3, 2);
    mv.visitEnd();
  }

  private void createConstructor(ClassWriter cw, String superClassNameSlash) {
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, superClassNameSlash, "<init>", "()V");
    mv.visitInsn(RETURN);
    mv.visitMaxs(2, 2);
    mv.visitEnd();
  }

}
