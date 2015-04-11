package com.eltiland.ui.webinars.plugin.tab.components;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.AjaxDownloadLink;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Panel with certificate actions for webinar members.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class MemberCertificatePanel extends BaseEltilandPanel {

    public MemberCertificatePanel(String id) {
        super(id);

        add(new AjaxDownloadLink("downloadLink") {

            @Override
            public String getFileName() {
                return "certificate.pdf";
            }

            @Override
            public IResourceStream getResourceStream() {
                return new AbstractResourceStream() {
                    @Override
                    public InputStream getInputStream() throws ResourceStreamNotFoundException {
                        return MemberCertificatePanel.this.getInputStream();
                    }

                    @Override
                    public void close() throws IOException {
                    }
                };
            }
        });

        add(new EltiAjaxLink("sendLink") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                onSend(target);
            }
        });
    }

    protected abstract void onSend(AjaxRequestTarget target);

    protected abstract InputStream getInputStream();
}
