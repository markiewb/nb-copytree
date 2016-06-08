/*
 * Copyright 2016 Benno Markiewicz (benno.markiewicz@googlemail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.markiewb.netbeans.plugins.copytree;

import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.datatransfer.ExClipboard;

@ActionID(
        category = "Edit",
        id = "de.markiewb.netbeans.plugins.copytree.CopyTree"
)
@ActionRegistration(
        displayName = "#CTL_CopyTree"
)
@Messages("CTL_CopyTree=Copy Tree")
@ActionReferences({
    @ActionReference(path = "Navigator/Actions/Hierarchy/text/x-java", position = 1153),
    @ActionReference(path = "Navigator/Actions/Members/text/x-java", position = 1250),})
public final class CopyTreeAction implements ActionListener {

    private final List<Node> context;

    public CopyTreeAction(List<Node> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Node first = context.get(0);

        //get root node
        Node rootNode = first;
        while (rootNode != null && rootNode.getParentNode() != null) {
            rootNode = rootNode.getParentNode();
        }

        //create hierarchy as text
        StringBuilder sb = new StringBuilder();
        Children children = rootNode.getChildren();
        Node[] ndoes = children.getNodes();
        if (ndoes != null && ndoes.length > 0) {
            visitNodeHierarchy(ndoes[0], ndoes[0].getChildren().getNodes(), sb, 0);
        }

        //copy hierarchy to clipboard
        ExClipboard clipboard = Lookup.getDefault().lookup(ExClipboard.class);
        clipboard.setContents(new StringSelection(sb.toString()), null);
    }

    private void visitNodeHierarchy(Node parent, Node[] children, StringBuilder sb, int depth) {
        for (int i = 0; i < depth; i++) {
            sb.append("\t");
        }
        sb.append(getName(parent));
        sb.append(System.lineSeparator());

        {
            for (Node node : children) {
                Children child = node.getChildren();
                visitNodeHierarchy(node, child.getNodes(), sb, depth + 1);
            }
        }
    }

    private String getName(Node parent) {

        final String html = stripHtml(parent.getHtmlDisplayName());
        if (null == html || "".equals(html)) {
            return parent.getDisplayName();
        }
        return html.replaceAll("&lt;", "<");
    }

    private String stripHtml(String text) {
        if (null == text) {
            return null;
        }
        return text.replaceAll("<[^>]*>", "");
    }
}
