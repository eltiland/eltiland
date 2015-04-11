package com.eltiland.bl.utils.http;

import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Simple DELETE method class.
 * It is needed because standard method does not support request body.
 *
 * @author Aleksey Plotnikov
 */
public class DeleteMethod extends PostMethod {
    public DeleteMethod(String uri) {
        super(uri);
    }

    @Override
    public String getName() {
        return "DELETE";
    }
}
