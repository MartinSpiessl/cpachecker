/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2014  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package org.sosy_lab.cpachecker.cfa.parser.eclipse.java;

import static com.google.common.base.Preconditions.checkArgument;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.sosy_lab.common.LogManager;
import org.sosy_lab.cpachecker.cfa.types.java.JArrayType;
import org.sosy_lab.cpachecker.cfa.types.java.JBasicType;
import org.sosy_lab.cpachecker.cfa.types.java.JClassOrInterfaceType;
import org.sosy_lab.cpachecker.cfa.types.java.JClassType;
import org.sosy_lab.cpachecker.cfa.types.java.JInterfaceType;
import org.sosy_lab.cpachecker.cfa.types.java.JSimpleType;
import org.sosy_lab.cpachecker.cfa.types.java.JType;


public abstract class TypeConverter {

  @SuppressWarnings("unused")
  private final LogManager logger;

  public TypeConverter(LogManager pLogger) {
    logger = pLogger;
  }

  public final JType convert(Type t) {

    // TODO Not all Types implemented (Paramized, Wildcard)

    // The Reason for this method is, that not all Types
    // have to be gotten by resolving their binding.
    // It is unnecessary for Array Types and primitive Types e.g.

    if (t.getNodeType() == ASTNode.PRIMITIVE_TYPE) {
      return convert((PrimitiveType) t);
    } else if (t.getNodeType() == ASTNode.ARRAY_TYPE) {
      return convert((ArrayType) t);
    } else if (t.getNodeType() == ASTNode.QUALIFIED_TYPE) {
      return convert((QualifiedType) t);
    } else if (t.getNodeType() == ASTNode.SIMPLE_TYPE) {
      return convert((SimpleType) t);
    } else if (t.getNodeType() == ASTNode.PARAMETERIZED_TYPE) {
      return convert(((ParameterizedType) t).getType());
    } else {
      return new JSimpleType(JBasicType.UNSPECIFIED);
    }
  }

  private JType convert(QualifiedType t) {
    ITypeBinding binding = t.resolveBinding();

    boolean canBeResolved = binding != null;

    if (canBeResolved) {
      return convert(binding);
    } else {
      return new JSimpleType(JBasicType.UNSPECIFIED);
    }
  }

  private JType convert(SimpleType t) {
    ITypeBinding binding = t.resolveBinding();
    boolean canBeResolved = binding != null;

    if (canBeResolved) {
      return convert(binding);
    } else {
      return new JSimpleType(JBasicType.UNSPECIFIED);
    }
  }

  public final JType convert(ITypeBinding t) {
    //TODO Needs to be completed (Wildcard, Parameterized type etc)

    if (t == null) {
      return new JSimpleType(JBasicType.UNSPECIFIED);
    } else if (t.isPrimitive()) {
      return new JSimpleType(convertPrimitiveType(t.getName()));
    } else if (t.isArray()) {
      return new JArrayType(convert(t.getElementType()), t.getDimensions());
    } else if (t.isClass() || t.isEnum()) {
      return convertClassType(t);
    } else if (t.isInterface()) {
      return convertInterfaceType(t);
    } else {
      return new JSimpleType(JBasicType.UNSPECIFIED);
    }
  }

  public abstract JClassType convertClassType(ITypeBinding t);

  public abstract JInterfaceType convertInterfaceType(ITypeBinding t);

  public final JClassOrInterfaceType convertClassOrInterfaceType(ITypeBinding pT) {

    checkArgument(pT.isClass() || pT.isEnum() || pT.isInterface());

    if (pT.isInterface()) {
      return convertInterfaceType(pT);
    } else {
      return convertClassType(pT);
    }
  }

  private JSimpleType convert(final PrimitiveType t) {

    PrimitiveType.Code primitiveTypeName = t.getPrimitiveTypeCode();
    return new JSimpleType(convertPrimitiveType(primitiveTypeName.toString()));
  }

  private JBasicType convertPrimitiveType(String primitiveTypeName) {

    JBasicType type;
    if (primitiveTypeName.equals("boolean")) {
      type = JBasicType.BOOLEAN;
    } else if (primitiveTypeName.equals("char")) {
      type = JBasicType.CHAR;
    } else if (primitiveTypeName.equals("double")) {
      type = JBasicType.DOUBLE;
    } else if (primitiveTypeName.equals("float")) {
      type = JBasicType.FLOAT;
    } else if (primitiveTypeName.equals("int")) {
      type = JBasicType.INT;
    } else if (primitiveTypeName.equals("void")) {
      type = JBasicType.VOID;
    } else if (primitiveTypeName.equals("long")) {
      type = JBasicType.LONG;
    } else if (primitiveTypeName.equals("short")) {
      type = JBasicType.SHORT;
    } else if (primitiveTypeName.equals("byte")) {
      type = JBasicType.BYTE;
    } else {
      throw new CFAGenerationRuntimeException(
                   "Unknown primitive type " + primitiveTypeName);
    }

    return type;
  }

  private JArrayType convert(final ArrayType t) {
    return new JArrayType(convert((t.getElementType())), t.getDimensions());
  }

}
