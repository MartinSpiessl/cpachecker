/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2019  Dirk Beyer
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
package org.sosy_lab.cpachecker.cfa.postprocessing.global;

import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Option;
import org.sosy_lab.common.configuration.Options;
import org.sosy_lab.cpachecker.cfa.CFA;
import org.sosy_lab.cpachecker.cfa.MutableCFA;
import org.sosy_lab.cpachecker.cfa.ast.FileLocation;
import org.sosy_lab.cpachecker.cfa.model.AssumeEdge;
import org.sosy_lab.cpachecker.cfa.model.BlankEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.cfa.model.CFATerminationNode;
import org.sosy_lab.cpachecker.cfa.model.FunctionEntryNode;
import org.sosy_lab.cpachecker.cfa.model.FunctionExitNode;
import org.sosy_lab.cpachecker.cfa.model.c.CLabelNode;
import org.sosy_lab.cpachecker.util.CFAUtils;
import org.sosy_lab.cpachecker.util.predicates.BlockOperator;

/**
 * Post-processing step for {@link CFA} that adds labels at specified points.
 */
@Options(prefix="cfa.addLabels")
public class LabelAdder {

  private static final String BLOCK_LABEL_NAME = "BLOCK_";
  private static final String EXIT_LABEL_NAME = "PROG_EXIT_";

  @Option(
      secure = true,
      name = "atBlocks",
      description = "Add a unique label for each basic block"
  )
  private boolean addBlockLabels = true;

  @Option(
      secure = true,
      name = "atProgramExit",
      description = "Add a unique label before each program exit"
  )
  private boolean addProgramExitLabels = false;

  private final Configuration config;

  public LabelAdder(final Configuration pConfig) throws InvalidConfigurationException {
    pConfig.inject(this);
    config = pConfig;
  }

  public void addLabels(final MutableCFA pCfa) {
    BlockOperator blk = new BlockOperator();
    try {
      config.inject(blk);
      blk.setCFA(pCfa);
    } catch (InvalidConfigurationException e) {
      // Should never happen here
      throw new AssertionError(e);
    }

    Set<CFANode> allNodes = ImmutableSet.copyOf(pCfa.getAllNodes());
    if (addBlockLabels) {
      addLabelsAtBlockStarts(pCfa, allNodes, blk);
    }
    if (addProgramExitLabels) {
      addLabelsAtProgramExits(pCfa, allNodes);
    }
  }

  /**
   * Add a unique label at each block start in the given CFA. The configuration of {@link
   * BlockOperator} is used to decide the size and structure of blocks.
   *
   * @param pCfa the CFA to add labels to
   */
  private void addLabelsAtBlockStarts(
      final MutableCFA pCfa, Collection<CFANode> pCandidates, final BlockOperator pBlk) {
    Collection<CFAEdge> edgesToLabel = new HashSet<>();
    int labelsAdded = 0;
    // We do this in two steps: We first get all of the original nodes at which we want labels,
    // and than we add them.
    // Otherwise, we'd have to keep track of original and newly added nodes so that we don't add two
    // labels
    // after another.
    for (CFANode n : pCandidates) {
      if (pBlk.isBlockEnd(n, -1) && !n.equals(pCfa.getMainFunction())) {
        // add labels before the block start, and not after,
        // so that we know that they are reachable.
        // if we add labels after the block start,
        // they may be unreachable (e.g., "return 0; BLOCK_END_1:;")
        FluentIterable<CFAEdge> edges = CFAUtils.leavingEdges(n);
        for (CFAEdge e : edges) {
          edgesToLabel.add(e);
        }
      }
    }

    for (CFAEdge e : edgesToLabel) {
      String labelName = BLOCK_LABEL_NAME + labelsAdded;
      if (e.getSuccessor() instanceof FunctionExitNode) {
          addLabelBefore(e, labelName, pCfa);
      } else {
        addLabelAfter(e, labelName, pCfa);
      }
      labelsAdded++;
    }
  }

  private void addLabelsAtProgramExits(final MutableCFA pCfa, Collection<CFANode> pCandidates) {
    int labelsAdded = 0;

    addLabelBefore(pCfa.getMainFunction().getExitNode(), EXIT_LABEL_NAME, labelsAdded, pCfa);
    labelsAdded++;

    for (CFANode n : pCandidates) {
      if (n instanceof CFATerminationNode) {
        addLabelBefore(n, EXIT_LABEL_NAME, labelsAdded, pCfa);
        labelsAdded++;
      }
    }
  }

  private int addLabelBefore(CFANode pNode, String pBlockSuffix, int pBlockNumberStart, MutableCFA pCfa) {
    final List<CFAEdge> enteringEdges = CFAUtils.enteringEdges(pNode).toList();
    int num = 0;
    for (CFAEdge e : enteringEdges)  {
      final String labelName = pBlockSuffix + (pBlockNumberStart + num);
      if (pNode instanceof FunctionExitNode) {
        addLabelBefore(e, labelName, pCfa);
      } else {
        addLabelAfter(e, labelName, pCfa);
      }
      num++;
    }
    return num;
  }

  private void addLabelBefore(CFAEdge pEdge, String pLabelName, MutableCFA pCfa) {
    final CFANode start = pEdge.getPredecessor();
    final CFANode end = pEdge.getSuccessor();
    final String functionName = start.getFunctionName();
    final FunctionCloner fc = new FunctionCloner(functionName, functionName, false);
    final CFANode labelNode = new CLabelNode(functionName, pLabelName);
    pCfa.addNode(labelNode);

    final CFAEdge redirectedEdge = fc.cloneEdge(pEdge, labelNode, end);
    start.removeLeavingEdge(pEdge);
    labelNode.addLeavingEdge(redirectedEdge);
    end.removeEnteringEdge(pEdge);
    end.addEnteringEdge(redirectedEdge);

    final CFAEdge labelEdge = new BlankEdge(pLabelName + ":; ", FileLocation.DUMMY, start, labelNode, "Label: " + pLabelName);
    start.addLeavingEdge(labelEdge);
    labelNode.addEnteringEdge(labelEdge);
  }

  private void addLabelAfter(CFAEdge pEdge, String pLabelName, MutableCFA pCfa) {
    final CFANode start = pEdge.getPredecessor();
    final CFANode end = pEdge.getSuccessor();
    final String functionName = start.getFunctionName();
    final FunctionCloner fc = new FunctionCloner(functionName, functionName, false);
    final CFANode labelConnector = new CFANode(functionName);
    final CFANode labelNode = new CLabelNode(functionName, pLabelName);
    pCfa.addNode(labelNode);
    pCfa.addNode(labelConnector);

    final CFAEdge redirectedEdge = fc.cloneEdge(pEdge, start, labelConnector);
    start.removeLeavingEdge(pEdge);
    start.addLeavingEdge(redirectedEdge);
    end.removeEnteringEdge(pEdge);
    labelConnector.addEnteringEdge(redirectedEdge);

    final CFAEdge labelEdge = new BlankEdge(pLabelName + ":; ", FileLocation.DUMMY, labelConnector, labelNode, "Label: " + pLabelName);
    labelConnector.addLeavingEdge(labelEdge);
    labelNode.addEnteringEdge(labelEdge);
    final CFAEdge connectorEdge = new BlankEdge("", FileLocation.DUMMY, labelNode, end, "label connector");
    labelNode.addLeavingEdge(connectorEdge);
    end.addEnteringEdge(connectorEdge);
  }
}
