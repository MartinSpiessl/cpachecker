package org.sosy_lab.cpachecker.cfa.ast;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.parser.IToken;

public abstract class IASTNode implements org.eclipse.cdt.core.dom.ast.IASTNode {

  private final String           rawSignature;
  private final IASTFileLocation fileLocation;

  public IASTNode(final String pRawSignature,
      final IASTFileLocation pFileLocation) {
    rawSignature = pRawSignature;
    fileLocation = pFileLocation;
  }

  @Override
  public boolean accept(final ASTVisitor visitor) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public boolean contains(final org.eclipse.cdt.core.dom.ast.IASTNode pArg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public abstract IASTNode copy();

  @Override
  public abstract IASTNode[] getChildren();
  
  @Override
  @Deprecated
  public String getContainingFilename() {
    throw new UnsupportedOperationException();
  }

  @Override
  public IASTFileLocation getFileLocation() {
    return fileLocation;
  }

  @Override
  public IToken getLeadingSyntax() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public IASTFileLocation[] getNodeLocations() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public IASTNode getParent() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public ASTNodeProperty getPropertyInParent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getRawSignature() {
    return rawSignature;
  }

  @Override
  @Deprecated
  public IToken getSyntax() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public IToken getTrailingSyntax() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public IASTTranslationUnit getTranslationUnit() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public boolean isActive() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public boolean isFrozen() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public boolean isPartOfTranslationUnitFile() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void setParent(org.eclipse.cdt.core.dom.ast.IASTNode pArg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void setPropertyInParent(ASTNodeProperty pArg0) {
    throw new UnsupportedOperationException();
  }

}
