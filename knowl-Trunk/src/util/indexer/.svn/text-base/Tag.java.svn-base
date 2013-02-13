package util.indexer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tag {

	private String type;
	private List<Attribute> attributes;
	private List<Tag> children;
	private Tag parent;

	public Tag(String type, List<Attribute> attributes, List<Tag> children) {
		this.type = type;
		this.attributes = (attributes == null) ? new ArrayList<Attribute>(0) : attributes;
		this.children = (children == null) ? new ArrayList<Tag>(0) : children;

	}
	
	public void linesToFile(BufferedWriter out) {
		try {
			String line = "<";
			line += type;
			if(!attributes.isEmpty()) {
				for(Attribute attr : attributes) {
					if(attr != null) {
						line += " " + attr.toString();
					}
				}
			}
			line += ">";
			
			out.write(line);
			out.newLine();
			line = "";
			
			if(!children.isEmpty()) {
				for(Tag child : children) {
					if(child != null) {
						child.linesToFile(out);
					}
				}
			}
			
			out.write("</" + type + ">");
			out.newLine();
		} catch (IOException e) {
			
		}
	}
	
	public void removeConnectChildren() {
		if(parent != null) {
			if(parent.getChildren().size() == 1) {
				parent.removeChild(0);
				for(Tag child: children) {
					parent.addChild(child);
					child.setParent(parent);
				}
			} else {
				List<Tag> parentChildren = parent.getChildren();
				List<Tag> newParentChildren = new ArrayList<Tag>();
				for(int i = 0; i < parentChildren.size(); i++) {
					Tag parentChild = parentChildren.get(i);
					if(parentChild == this) {
						for(Tag child : children) {
							newParentChildren.add(child);
							child.setParent(parent);
						}
					} else {
						newParentChildren.add(parentChild);
					}
				}
				parent.setChildren(newParentChildren);
				children = new ArrayList<Tag>();
				parent = null;
			}
		}
	}
	
	public void removeIncludeChildren() {
		if(parent != null) {
			parent.removeChild(this);
		}
		for(int i = 0; i < children.size(); i++){
			Tag child = children.get(i);
			child.removeIncludeChildren();
		}
		parent = null;
	}
	
	public Tag getParent() {
		return parent;
	}

	public void setParent(Tag parent) {
		this.parent = parent;
	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public void addAttribute(Attribute attr) {
		this.attributes.add(attr);
	}
	
	public Attribute getAttribute(int index) throws IndexOutOfBoundsException {
		return this.attributes.get(index);
	}
	
	public void setAttribute(String attrName, String attrValue)  {
		if(!attributes.isEmpty()) {
			for(int i = 0; i < attributes.size(); i++) {
				Attribute attr = attributes.get(i);
				if(attr.name.equals(attrName.toLowerCase())) {
					attributes.get(i).value = attrValue;
				}
			}
		}
	}
	
	public void removeAttribute(int index) throws IndexOutOfBoundsException {
		this.attributes.remove(index);
	}
	
	public void removeAttribute(String attrName, String attrValue) {
		if(!attributes.isEmpty()) {
			for(int i = 0; i < attributes.size(); i++) {
				Attribute attr = attributes.get(i);
				if(attr.name.equals(attrName.toLowerCase()) && attr.value.equals(attrValue)) {
					removeAttribute(i);
				}
			}
		}
	}
	
	public void addChild(Tag child) {
		this.children.add(child);
	}
	
	public Tag getChild(int index) throws IndexOutOfBoundsException {
		return this.children.get(index);
	}
	
	public void removeChild(int index) throws IndexOutOfBoundsException {
		this.children.remove(index);
	}
	
	public void removeChild(Tag child) throws IndexOutOfBoundsException {
		this.children.remove(child);
	}
	
	public void replaceChild(Tag toFind, Tag toSub){
		for(int i = 0; i < children.size(); i++) {
			Tag child = children.get(i);
			if(child == toFind) {
				children.set(i, toSub);
			}
		}
	}

	public Tag() {
		this(null, null, null);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Tag> getChildren() {
		return children;
	}

	public void setChildren(List<Tag> children) {
		this.children = children;
	}
}
