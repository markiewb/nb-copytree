/*
 * Copyright 2015 Benno Markiewicz (benno.markiewicz@googlemail.com).
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
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.CookieAction;
import org.openide.util.datatransfer.ExClipboard;

@ActionID(
        category = "Java",
        id = "de.markiewb.netbeans.plugins.copytree.CopyHierarchy"
)
@ActionRegistration(
        displayName = "#CTL_CopyHierarchy"
)
@Messages("CTL_CopyHierarchy=Copy Tree To Clipboard")
@ActionReferences({
    @ActionReference(path = "Navigator/Actions/Hierarchy/text/x-java", position = 1153)
})
public final class CopyHierarchyAction extends CookieAction {

    public CopyHierarchyAction() {
    }

    @Override
    public void performAction(Node[] context) {
        Node first = context[0];

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
        sb.append(parent.getDisplayName());
        sb.append(System.lineSeparator());
        
        {
            for (Node node : children) {
                Children child = node.getChildren();
                visitNodeHierarchy(node, child.getNodes(), sb, depth + 1);
            }
        }
    }

    @Override
    protected int mode() {
        return CookieAction.MODE_ALL;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return new Class[]{Node.class};
    }

    @Override
    public String getName() {
        return Bundle.CTL_CopyHierarchy();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return activatedNodes!=null && activatedNodes.length>0;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
