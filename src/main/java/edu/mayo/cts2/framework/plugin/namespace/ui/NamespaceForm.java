package edu.mayo.cts2.framework.plugin.namespace.ui;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import edu.mayo.cts2.framework.model.service.namespace.MultiNameNamespaceReference;
import edu.mayo.cts2.framework.service.namespace.NamespaceMaintenanceService;

@SuppressWarnings("serial")
public class NamespaceForm extends Form implements ClickListener {

	private static final long serialVersionUID = -4097827197023884874L;
	
    private NamespaceMaintenanceService namespaceMaintenanceService;
	
	private Button save = new Button("Save", (ClickListener) this);
	private Button cancel = new Button("Cancel", (ClickListener) this);
	private Button edit = new Button("Edit", (ClickListener) this);
	private Button addPrefix = new Button("Add Prefix", (ClickListener) this);

	public NamespaceForm(
			IndexedContainer container,
			NamespaceMaintenanceService namespaceMaintenanceService) {
		this.namespaceMaintenanceService = namespaceMaintenanceService;

		// Enable buffering so that commit() must be called for the form
		// before input is written to the data. (Form input is not written
		// immediately through to the underlying object.)
		setWriteThrough(false);
		
		this.setValidationVisible(true);
		this.setValidationVisibleOnCommit(true);

		final VerticalLayout footer = new VerticalLayout();
		footer.setSpacing(true);
		footer.setMargin(true);
		
		final HorizontalLayout toolbar = new HorizontalLayout();
		toolbar.setSpacing(true);
		toolbar.setMargin(true);
		toolbar.addComponent(save);
		toolbar.addComponent(cancel);
		toolbar.addComponent(edit);
		toolbar.addComponent(addPrefix);	

		footer.addComponent(toolbar);	
		footer.setVisible(false);

		this.setFooter(footer);	
	}
	
	@Override
	public void setItemDataSource(Item newDataSource) {
		if (newDataSource != null) {

			super.setItemDataSource(newDataSource);

			setReadOnly(true);
			getFooter().setVisible(true);
		} else {
			super.setItemDataSource(null);
			getFooter().setVisible(false);
		}
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		save.setVisible(!readOnly);
		cancel.setVisible(!readOnly);
		edit.setVisible(readOnly);
		addPrefix.setVisible(!readOnly);
	}

	public void buttonClick(ClickEvent event) {
		Button source = event.getButton();

		if (source == save) {
			/* If the given input is not valid there is no point in continuing */
			if (!isValid()) {
				return;
			}
		
			Item preCommitItem = this.getItemDataSource();
			String preCommitUri = (String) preCommitItem.getItemProperty("URI").getValue();
			
			commit();
			setReadOnly(true);
			
			Item postCommitItem = this.getItemDataSource();
			
			String postCommitUri = (String) postCommitItem.getItemProperty("URI").getValue();
			
			//if the URI has been changed, we need to delete the old record.
			//A URI change is equivalent to a DELETE of the old record and a 
			//save of the entire new record
			if(! preCommitUri.equals(postCommitUri)){
				this.namespaceMaintenanceService.delete(preCommitUri);
			}
			
			this.saveItem(postCommitItem);
			
		} else if (source == cancel) {
			
			
			AlternateNamesTable table = (AlternateNamesTable) this.getField("Alternate Names");
			table.getContainerDataSource().removeAllItems();
			table.setPropertyDataSource(this.getItemProperty("Alternate Names"));
		
			discard();
			setReadOnly(true);

		} else if (source == edit) {
			setReadOnly(false);
		}
		 else if (source == addPrefix) {
			AlternateNamesTable table = (AlternateNamesTable) this.getField("Alternate Names");
			Object id = table.addItem();
			table.getItem(id).getItemProperty("remove").setValue(
					createRemoveButton(table.getContainerDataSource(), id));
		}
	}
	

	private void saveItem(Item item) {
		MultiNameNamespaceReference ref = new MultiNameNamespaceReference();
		String uri = (String)item.getItemProperty("URI").getValue();
		String preferredName = (String)item.getItemProperty("Preferred Name").getValue();
		
		ref.setUri(uri);
		ref.setPreferredName(preferredName);
		
		AlternateNamesTable alternateNamesTable = (AlternateNamesTable) this.getField("Alternate Names");
		for(Object id : alternateNamesTable.getItemIds()){
			String altName = 
					(String) alternateNamesTable.getItem(id).getItemProperty("prefix").getValue();
			
			ref.addAlternateName(altName);
		}
		namespaceMaintenanceService.save(ref);
	}

	private static Button createRemoveButton(final Container container, final Object id){
	    	Button button = new Button("Remove");
	    	button.addListener(new ClickListener(){

				public void buttonClick(ClickEvent event) {
					container.removeItem(id);
				}
	    		
	    	});
	    	
	    	return button;
	    }
	
	public static class AlternateNamesTable extends Table {
	   
		private IndexedContainer alternateNamesContainer =
	        new IndexedContainer();

		protected AlternateNamesTable(){
			super();
		
			this.setContainerDataSource(alternateNamesContainer);
			alternateNamesContainer.addContainerProperty("prefix", String.class, "");
			alternateNamesContainer.addContainerProperty("remove", Button.class, null);
			
        	this.setVisibleColumns(new Object[]{"prefix", "remove"});
        	this.setCaption("Alternate Names");
        	this.setColumnWidth("prefix", 135);
        	this.setColumnAlignments(new String[]{ Table.ALIGN_LEFT, Table.ALIGN_CENTER } );	
		}

	    @Override
	    public void setPropertyDataSource(Property newDataSource) {
	      Object value = newDataSource.getValue();
	        
	      if(value != null){
	          String[] beans = ((String)value).split(",");
	          this.removeAllItems();
	           
	          this.setPageLength(0);
	           
	            for(String bean : beans){
	            	Object id = this.addItem();
	            	this.getItem(id).getItemProperty("prefix").setValue(bean.trim());
	            	this.getItem(id).getItemProperty("remove").setValue(
	            			createRemoveButton(alternateNamesContainer, id));
	            }
	      } 
	      
	      super.setPropertyDataSource(newDataSource);
	    }

	    @Override
	    public Object getValue() {
	        ArrayList<String> beans = new ArrayList<String>(); 
	        for (Object itemId: alternateNamesContainer.getItemIds()){
	        	String prefix = alternateNamesContainer.getItem(itemId).getItemProperty("prefix").toString();
	        	if(StringUtils.isNotBlank(prefix)){
	        		beans.add(prefix);
	        	}
	        }
	        return StringUtils.join(beans, ',');
	    }
	
	    @Override
	    public void setReadOnly(boolean readOnly) {
	       this.setEditable(!readOnly);
	       super.setReadOnly(readOnly);
	       for(Object id : alternateNamesContainer.getItemIds()){
	    	   Button removeButton = (Button)alternateNamesContainer.getContainerProperty(id, "remove").getValue();
	    	   removeButton.setEnabled(!readOnly);
	       }
	    }
	}

}
