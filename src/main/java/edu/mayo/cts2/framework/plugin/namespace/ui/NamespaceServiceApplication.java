package edu.mayo.cts2.framework.plugin.namespace.ui;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.mayo.cts2.framework.model.service.namespace.MultiNameNamespaceReference;
import edu.mayo.cts2.framework.plugin.namespace.ui.NamespaceForm.AlternateNamesTable;
import edu.mayo.cts2.framework.service.namespace.NamespaceMaintenanceService;
import edu.mayo.cts2.framework.service.namespace.NamespaceQueryService;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
public class NamespaceServiceApplication extends Application implements InitializingBean {

	private static final long serialVersionUID = 3997440084794825688L;

    @Resource 
    private NamespaceMaintenanceService namespaceMaintenanceService;
    
    @Resource 
    private NamespaceQueryService namespaceQueryService;

	private static String[] fields = { "URI", "Preferred Name", "Alternate Names" };
    private static String[] visibleCols = new String[] { "URI", "Preferred Name", "Alternate Names" };

    private Table namespaceList = new Table();
    private Form naemspaceEditor;
    private HorizontalLayout bottomLeftCorner = new HorizontalLayout();
    private Button removalButton;
    private IndexedContainer namespaceData;

    @Override
    public void init() {
        initLayout();
        initContactAddRemoveButtons();
        initAddressList();
        initFilteringControls();
    }
   
    private void initLayout() {
    	HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        setMainWindow(new Window("CTS2 Namespace Service", splitPanel));
        VerticalLayout left = new VerticalLayout();
        left.setSizeFull();
        left.addComponent(namespaceList);
        namespaceList.setSizeFull();
        left.setExpandRatio(namespaceList, 1);
        splitPanel.addComponent(left);
        splitPanel.addComponent(naemspaceEditor);
        naemspaceEditor.setCaption("Namespace details editor");
        naemspaceEditor.setSizeFull();
        naemspaceEditor.getLayout().setMargin(true);
        naemspaceEditor.setImmediate(true);
        naemspaceEditor.setFormFieldFactory(new MyNestedFormFactory());

        bottomLeftCorner.setWidth("100%");
        left.addComponent(bottomLeftCorner);
    }

    private void initContactAddRemoveButtons() {
        // New item button
        bottomLeftCorner.addComponent(new Button("+",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                    	
                    	new InputDialog(getMainWindow(), "Please Input URI:",
                                new InputDialog.Recipient() {
                            public boolean gotInput(String uri) {

										if (!validateUri(uri)) {
											return false;
										} else {

											Object id = ((IndexedContainer) namespaceList
													.getContainerDataSource())
													.addItemAt(0);
											namespaceList.getItem(id)
													.getItemProperty("URI")
													.setValue(uri);
											namespaceList.getItem(id)
													.getItemProperty("URI")
													.setReadOnly(true);
											
											// Select the newly added item and
											// scroll to the item
											namespaceList.setValue(id);
											namespaceList
													.setCurrentPageFirstItemId(id);
											
											MultiNameNamespaceReference ref = new MultiNameNamespaceReference();
											ref.setUri(uri);
											namespaceMaintenanceService.save(ref);
											
											return true;
										}
                            }
                        });   
                    }
                }));

        // Remove item button
        removalButton = new Button("-", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	Item item = namespaceList.getItem(namespaceList.getValue());
            	namespaceMaintenanceService.delete((String)item.getItemProperty("URI").getValue());
            	
                namespaceList.removeItem(namespaceList.getValue());

                namespaceList.select(null);
            }
        });
        removalButton.setVisible(false);
        bottomLeftCorner.addComponent(removalButton);
    }
    
	private boolean validateUri(String uri) {

		for (Object id : namespaceList.getItemIds()) {
			if (uri.equals(((String) namespaceList.getContainerProperty(id, "URI")
					.getValue()))) {
				return false;
			}
		}

		return true;
	}

    private String[] initAddressList() {
        namespaceList.setContainerDataSource(namespaceData);
        namespaceList.setVisibleColumns(visibleCols);
        namespaceList.setSelectable(true);
        namespaceList.setImmediate(true);
        
        namespaceList.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                Object id = namespaceList.getValue();
                naemspaceEditor.setItemDataSource(id == null ? null : namespaceList
                        .getItem(id));
                removalButton.setVisible(id != null);
            }
        });
        return visibleCols;
    }

	private void initFilteringControls() {
        for (final String pn : visibleCols) {
            final TextField sf = new TextField();
            bottomLeftCorner.addComponent(sf);
            sf.setWidth("100%");
            sf.setInputPrompt(pn);
            sf.setImmediate(true);
            bottomLeftCorner.setExpandRatio(sf, 1);
           
            sf.addListener(new TextChangeListener() {
                public void textChange(TextChangeEvent event) {
                	String text = event.getText();
                    namespaceData.removeContainerFilters(pn);
                   
                        namespaceData.addContainerFilter(pn, text,
                                true, false);
                   
                    getMainWindow().showNotification(
                            "" + namespaceData.size() + " matches found");
                }
            });
        }
    }

    private IndexedContainer createContainer() {
        IndexedContainer ic = new IndexedContainer();
      
        for (String p : fields) {
            ic.addContainerProperty(p, String.class, "");
        }
        
        for(MultiNameNamespaceReference namespace : namespaceQueryService.getAllNamespaces()){
        	Object id = ic.addItem();
        	ic.getContainerProperty(id, "URI").setValue(namespace.getUri());
        	ic.getContainerProperty(id, "Preferred Name").setValue(namespace.getPreferredName());
        	ic.getContainerProperty(id, "URI").setReadOnly(true);
        	
        	ic.getContainerProperty(id, "Alternate Names").setValue(
        			StringUtils.join(namespace.getAlternateName(), ','));
        }
       
     
        return ic;
    }
    
    class MyNestedFormFactory extends DefaultFieldFactory {
        @Override
        public Field createField(Item item, Object propertyId,
                Component uiContext) {
            Field field;
            if ("Alternate Names".equals(propertyId)) {
            	AlternateNamesTable table = new AlternateNamesTable();

            	field = table;
            } else
                field = super.createField(item, propertyId, uiContext);
            return field;
        }
    }

	public void afterPropertiesSet() throws Exception {
		this.namespaceData = this.createContainer();
		this.naemspaceEditor = new NamespaceForm(
				this.namespaceData,
				this.namespaceMaintenanceService);
		
	}            

}