/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Trees;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class JavacASTViewer extends JPanel {

  private final JTree tree;
  private final JEditorPane sourcePane;
  private final JEditorPane infoPane;
  private final String source;

  private JavacASTViewer(String[] args) throws IOException {
    super(new GridBagLayout());
    String filename = args[0];
    source = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);

    tree = new JTree(getSwingTree(getJavacAST(filename)));
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    expandAllNodes(tree);
    tree.addTreeSelectionListener(this::valueChanged);
    addComponent(tree, 0);

    sourcePane = new JEditorPane();
    sourcePane.setEditable(false);
    updateSourcePane(0, source.length());
    addComponent(sourcePane, 1);

    infoPane = new JEditorPane();
    infoPane.setEditable(false);
    addComponent(infoPane, 2);
  }

  private void addComponent(JComponent component, int column) {
    Dimension d = new Dimension(400, 600);
    JScrollPane scrollPane = new JScrollPane(component);
    scrollPane.setPreferredSize(d);
    scrollPane.setMinimumSize(d);

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = column;
    constraints.gridy = 0;
    constraints.weightx = 0.3;
    constraints.weighty = 1;
    constraints.fill = GridBagConstraints.BOTH;
    add(scrollPane, constraints);
  }

  private static JavacASTNode getJavacAST(String filename) throws IOException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
    Iterable<? extends JavaFileObject> units = fileManager.getJavaFileObjectsFromStrings(
        Arrays.asList(filename));
    JavacTask task = (JavacTask) compiler.getTask(null, fileManager, null, null, null, units);
    Iterable<? extends CompilationUnitTree> asts = task.parse();
    task.analyze();
    JavacASTNode root = JavacASTVisitor.visit(asts.iterator().next(), Trees.instance(task));
    fileManager.close();
    return root;
  }

  private static DefaultMutableTreeNode getSwingTree(JavacASTNode javacASTNode) {
    DefaultMutableTreeNode swingNode = new DefaultMutableTreeNode(javacASTNode);
    javacASTNode.getChildren().map(JavacASTViewer::getSwingTree).forEach(swingNode::add);
    return swingNode;
  }

  private void expandAllNodes(JTree tree) {
    int row = 0;
    while (row < tree.getRowCount()) {
      tree.expandRow(row++);
    }
  }

  private void valueChanged(TreeSelectionEvent event) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
    if (node == null) {
      return;
    }
    JavacASTNode javacASTNode = (JavacASTNode) node.getUserObject();
    updateSourcePane(javacASTNode.getStartPosition(), javacASTNode.getEndPosition());
    updateInfoPane(javacASTNode);
  }

  private void updateSourcePane(long startPosition, long endPosition) {
    int start = (int) startPosition;
    int end = (int) endPosition;
    if (startPosition == Diagnostic.NOPOS || endPosition == Diagnostic.NOPOS) {
      start = end = 0;
    }
    sourcePane.setContentType("text/html");
    StringBuilder sb = new StringBuilder();
    sb.append("<pre>");
    sb.append(source.substring(0, start));
    sb.append("<span style=\"color: blue; font-weight:bold\">");
    sb.append(source.substring(start, end));
    sb.append("</span>");
    sb.append(source.substring(end));
    sb.append("<pre>");
    sourcePane.setText(sb.toString());
  }

  private void updateInfoPane(JavacASTNode javacASTNode) {
    StringBuilder sb = new StringBuilder();
    sb.append("TypeMirror\n");
    javacASTNode.getTypeMirror().ifPresent(sb::append);
    sb.append("\nElement\n");
    javacASTNode.getElement().ifPresent(sb::append);
    infoPane.setText(sb.toString());
  }

  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(() -> {
          try {
            JFrame frame = new JFrame("Javac AST Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new JavacASTViewer(args));
            frame.pack();
            frame.setVisible(true);
          } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
          }
        }
    );
  }
}
