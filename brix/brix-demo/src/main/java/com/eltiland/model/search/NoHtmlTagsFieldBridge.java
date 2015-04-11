package com.eltiland.model.search;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * Removes all markup from string value. Leaves text only.
 */
public class NoHtmlTagsFieldBridge implements FieldBridge {

    /**
     * Accepts text nodes only.
     */
    private static final NodeFilter TEXT_NODE_FILTER = new NodeFilter() {
        @Override
        public boolean accept(Node node) {
            return node instanceof TextNode;
        }
    };

    @Override
    public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {

        String result = StringUtils.defaultString((String) value);

        try {
            Lexer lexer = new Lexer(result);

            NodeList list = new NodeList(); // text nodes will be put here

            // iterate over nodes
            for (Node node = lexer.nextNode(); node != null; node = lexer.nextNode()) {
                node.collectInto(list, TEXT_NODE_FILTER);
            }

            // concatenate all text in one string
            result = list.asString();
        } catch (ParserException ignore) {
        }

        document.add(new Field(name, result, luceneOptions.getStore(), luceneOptions.getIndex()));
    }
}
