package org.sosy_lab.cpachecker.cfa.ast;

public final class IASTParameterDeclaration extends IASTNode implements
    org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration {

  private final IASTDeclSpecifier specifier;
  private final IASTDeclarator    declarator;

  public IASTParameterDeclaration(final String pRawSignature,
      final IASTFileLocation pFileLocation, final IASTDeclSpecifier pSpecifier,
      final IASTDeclarator pDeclarator) {
    super(pRawSignature, pFileLocation);
    specifier = pSpecifier;
    declarator = pDeclarator;
  }

  @Override
  public IASTDeclSpecifier getDeclSpecifier() {
    return specifier;
  }

  @Override
  public IASTDeclarator getDeclarator() {
    return declarator;
  }
  
  @Override
  public IASTNode[] getChildren(){
    return new IASTNode[] {specifier, declarator};
  }

  @Override
  @Deprecated
  public void setDeclSpecifier(
      final org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier pArg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void setDeclarator(
      final org.eclipse.cdt.core.dom.ast.IASTDeclarator pArg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public IASTParameterDeclaration copy() {
    throw new UnsupportedOperationException();
  }
}
