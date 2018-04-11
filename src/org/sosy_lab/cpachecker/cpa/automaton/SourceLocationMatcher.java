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
package org.sosy_lab.cpachecker.cpa.automaton;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.util.Objects;
import java.util.Optional;
import org.sosy_lab.cpachecker.cfa.ast.FileLocation;

class SourceLocationMatcher {

  private static abstract class BaseFileNameMatcher implements Predicate<FileLocation> {

    private final Optional<String> originFileName;

    private BaseFileNameMatcher(Optional<String> originFileName) {
      this.originFileName = checkNotNull(originFileName);
    }

    @Override
    public boolean apply(FileLocation pFileLocation) {
      if (!originFileName.isPresent()) {
        return true;
      }
      String fileName = this.originFileName.get();
      String fileLocationFileName = pFileLocation.getFileName();
      fileName = getBaseName(fileName);
      fileLocationFileName = getBaseName(fileLocationFileName);
      return fileName.equals(fileLocationFileName);
    }

    private String getBaseName(String pOf) {
      int index = pOf.lastIndexOf('/');
      if (index == -1) {
        index = pOf.lastIndexOf('\\');
      }
      if (index == -1) {
        return pOf;
      } else {
        return pOf.substring(index + 1);
      }
    }

    @Override
    public int hashCode() {
      return originFileName.hashCode();
    }

    @Override
    public String toString() {
      return originFileName.isPresent() ? "FILE " + originFileName : "TRUE";
    }

    protected Optional<String> getOriginFileName() {
      return originFileName;
    }

  }

  static class LineMatcher extends BaseFileNameMatcher {

    private final int startLineNumber;

    private final int endLineNumber;

    private final boolean origin;

    public LineMatcher(
        Optional<String> pFileName, int pStartLineNumber, int pEndLineNumber, boolean pOrigin) {
      super(pFileName);
      Preconditions.checkArgument(pStartLineNumber <= pEndLineNumber);
      this.startLineNumber = pStartLineNumber;
      this.endLineNumber = pEndLineNumber;
      this.origin = pOrigin;
    }

    public LineMatcher(Optional<String> pFileName, int pStartLineNumber, int pEndLineNumber) {
      this(pFileName, pStartLineNumber, pEndLineNumber, true);
    }

    @Override
    public int hashCode() {
      return Objects.hash(getOriginFileName(), startLineNumber, endLineNumber, origin);
    }

    @Override
    public boolean equals(Object pObj) {
      if (this == pObj) {
        return true;
      }
      if (!(pObj instanceof LineMatcher)) {
        return false;
      }
      LineMatcher other = (LineMatcher) pObj;
      return origin == other.origin
          && startLineNumber == other.startLineNumber
          && endLineNumber == other.endLineNumber
          && Objects.equals(getOriginFileName(), other.getOriginFileName());
    }

    @Override
    public boolean apply(FileLocation pFileLocation) {
      int compStartingLine =
          origin ? pFileLocation.getStartingLineInOrigin() : pFileLocation.getStartingLineNumber();
      int compEndingLine =
          origin ? pFileLocation.getEndingLineInOrigin() : pFileLocation.getEndingLineNumber();
      return super.apply(pFileLocation)
          && startLineNumber <= compEndingLine
          && compStartingLine <= endLineNumber;
    }

    @Override
    public String toString() {
      String prefix = "LINE ";
      if (origin) {
        prefix = "ORIGIN " + prefix;
      }
      if (startLineNumber == endLineNumber) {
        return prefix + startLineNumber;
      }
      return prefix + startLineNumber + "-" + endLineNumber;
    }
  }

  static class OffsetMatcher extends BaseFileNameMatcher {

    private final int startOffset;

    private final int endOffset;

    OffsetMatcher(Optional<String> pOriginFileName, int pStartOffset, int pEndOffset) {
      super(pOriginFileName);
      Preconditions.checkArgument(pStartOffset <= pEndOffset);
      this.startOffset = pStartOffset;
      this.endOffset = pEndOffset;
    }

    @Override
    public int hashCode() {
      return Objects.hash(getOriginFileName(), startOffset, endOffset);
    }

    @Override
    public boolean equals(Object pObj) {
      if (this == pObj) {
        return true;
      }
      if (!(pObj instanceof OffsetMatcher)) {
        return false;
      }
      OffsetMatcher other = (OffsetMatcher) pObj;
      return Objects.equals(getOriginFileName(), other.getOriginFileName())
          && startOffset == other.startOffset
          && endOffset == other.endOffset;
    }

    @Override
    public boolean apply(FileLocation pFileLocation) {
      int locationEndOffset = pFileLocation.getNodeOffset() + pFileLocation.getNodeLength() - 1;
      return super.apply(pFileLocation)
          && pFileLocation.getNodeOffset() <= endOffset
          && startOffset <= locationEndOffset;
    }

    @Override
    public String toString() {
      if (startOffset == endOffset) {
        return "OFFSET " + startOffset;
      }
      return "OFFSET " + startOffset + "-" + endOffset;
    }
  }
}
