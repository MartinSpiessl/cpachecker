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
package org.sosy_lab.cpachecker.util.predicates.smt;

import org.sosy_lab.solver.api.BooleanFormula;
import org.sosy_lab.solver.api.BooleanFormulaManager;
import org.sosy_lab.solver.api.Formula;
import org.sosy_lab.solver.api.FormulaType;
import org.sosy_lab.solver.visitors.TraversalProcess;

import java.util.Collection;
import java.util.Set;


public class BooleanFormulaManagerView extends BaseManagerView implements BooleanFormulaManager {

  private final BooleanFormulaManager manager;

  BooleanFormulaManagerView(FormulaWrappingHandler pWrappingHandler,
      BooleanFormulaManager pManager) {
    super(pWrappingHandler);
    this.manager = pManager;
  }

  public BooleanFormula makeVariable(String pVar, int pI) {
    return makeVariable(FormulaManagerView.makeName(pVar, pI));
  }

  @Override
  public BooleanFormula not(BooleanFormula pBits) {
    return manager.not(pBits);
  }

  @Override
  public BooleanFormula and(BooleanFormula pBits1, BooleanFormula pBits2) {
    return manager.and(pBits1, pBits2);
  }

  @Override
  public BooleanFormula and(Collection<BooleanFormula> pBits) {
    return manager.and(pBits);
  }

  @Override
  public BooleanFormula and(BooleanFormula... bits) {
    return manager.and(bits);
  }

  @Override
  public BooleanFormula or(BooleanFormula pBits1, BooleanFormula pBits2) {
    return manager.or(pBits1, pBits2);
  }

  @Override
  public BooleanFormula or(Collection<BooleanFormula> pBits) {
    return manager.or(pBits);
  }

  @Override
  public BooleanFormula or(BooleanFormula... bits) {
    return manager.or(bits);
  }

  @Override
  public BooleanFormula xor(BooleanFormula pBits1, BooleanFormula pBits2) {
    return manager.xor(pBits1, pBits2);
  }

  @Override
  public <R> R visit(
      org.sosy_lab.solver.visitors.BooleanFormulaVisitor<R> visitor,
      BooleanFormula formula) {
    return manager.visit(visitor, formula);
  }

  @Override
  public void visitRecursively(
      org.sosy_lab.solver.visitors.BooleanFormulaVisitor<TraversalProcess> rFormulaVisitor,
      BooleanFormula f) {
    manager.visitRecursively(rFormulaVisitor, f);
  }

  @Override
  public BooleanFormula transformRecursively(
      org.sosy_lab.solver.visitors.BooleanFormulaTransformationVisitor pVisitor, BooleanFormula f) {
    return manager.transformRecursively(pVisitor, f);
  }

  @Override
  public Set<BooleanFormula> toConjunctionArgs(BooleanFormula f, boolean flatten) {
    return manager.toConjunctionArgs(f, flatten);
  }

  @Override
  public Set<BooleanFormula> toDisjunctionArgs(BooleanFormula f, boolean flatten) {
    return manager.toDisjunctionArgs(f, flatten);
  }

  @Override
  public BooleanFormula makeBoolean(boolean pValue) {
    return manager.makeBoolean(pValue);
  }

  @Override
  public BooleanFormula makeVariable(String pVar) {
    return manager.makeVariable(pVar);
  }

  @Override
  public boolean isTrue(BooleanFormula pFormula) {
    return manager.isTrue(pFormula);
  }

  @Override
  public boolean isFalse(BooleanFormula pFormula) {
    return manager.isFalse(pFormula);
  }

  @Override
  public <T extends Formula> T ifThenElse(BooleanFormula pCond, T pF1, T pF2) {
    Formula f1 = unwrap(pF1);
    Formula f2 = unwrap(pF2);
    FormulaType<T> targetType = getFormulaType(pF1);

    return wrap(targetType, manager.ifThenElse(pCond, f1, f2));
  }

  @Override
  public BooleanFormula equivalence(BooleanFormula pFormula1, BooleanFormula pFormula2) {
    return manager.equivalence(pFormula1, pFormula2);
  }

  @Override
  public BooleanFormula implication(BooleanFormula formula1, BooleanFormula formula2) {
    return manager.implication(formula1, formula2);
  }

  /**
   * Base class for visitors for boolean formulas that traverse recursively
   * through the formula and somehow transform it (i.e., return a boolean formula).
   * This class ensures that each identical subtree of the formula
   * is visited only once to avoid the exponential explosion.
   * When a subclass wants to traverse into a subtree of the formula,
   * it needs to call {@link #visitIfNotSeen(BooleanFormula)} to ensure this.
   *
   * By default this class implements the identity function.
   *
   * No guarantee on iteration order is made.
   */
  public static abstract class BooleanFormulaTransformationVisitor
      extends org.sosy_lab.solver.visitors.BooleanFormulaTransformationVisitor {

    protected BooleanFormulaTransformationVisitor(FormulaManagerView pFmgr) {
      super(pFmgr.getRawFormulaManager().getBooleanFormulaManager());
    }
  }
}
