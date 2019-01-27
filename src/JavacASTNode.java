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

import com.sun.source.tree.Tree;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public class JavacASTNode {

  private final Tree tree;
  private final Element element;
  private final TypeMirror typeMirror;
  private final long startPosition;
  private final long endPosition;
  private final List<JavacASTNode> children = new ArrayList<>();

  JavacASTNode(Tree tree, Element element, TypeMirror typeMirror, long startPosition,
      long endPosition) {
    this.tree = tree;
    this.element = element;
    this.typeMirror = typeMirror;
    this.startPosition = startPosition;
    this.endPosition = endPosition;
  }

  void addChild(JavacASTNode child) {
    children.add(child);
  }

  Tree getTree() {
    return tree;
  }

  Optional<Element> getElement() {
    return Optional.ofNullable(element);
  }

  Optional<TypeMirror> getTypeMirror() {
    return Optional.ofNullable(typeMirror);
  }

  long getStartPosition() {
    return startPosition;
  }

  long getEndPosition() {
    return endPosition;
  }

  Stream<JavacASTNode> getChildren() {
    return children.stream();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(tree.getKind().toString());
    if (element != null && !element.getSimpleName().toString().isEmpty()) {
      sb.append(" (").append(element.getSimpleName().toString()).append(")");
    }
    return sb.toString();
  }
}
