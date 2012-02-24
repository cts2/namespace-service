package edu.mayo.cts2.framework.plugin.namespace.ui;

import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class InputDialog extends Window {
    Recipient r;
    TextField tf = new TextField();

    public InputDialog(final Window parent, String question, Recipient recipient) {
        r = recipient;
        
        setCaption(question);
        setModal(true);
        getContent().setWidth("250px");
        addComponent(tf);
        final Window dialog = this;
        addComponent(new Button("Ok", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                boolean success = r.gotInput(tf.toString());
                if(success){
                	parent.removeWindow(dialog);
                } else {
                	tf.setComponentError(
                            new UserError("URI is not unique."));
                }
            }
        }));
        tf.setWidth("100%");
        parent.addWindow(this);
    }

    public interface Recipient {
        public boolean gotInput(String uri);
    }
}