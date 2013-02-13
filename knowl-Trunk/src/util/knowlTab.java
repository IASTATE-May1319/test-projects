package util;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * 
 * @author tyler johnson
 *
 */
public class knowlTab {
	JPanel tab;
	JEditorPane jep;
	JScrollPane jsp;
	String name;
	ImageIcon icon;
	JCheckBox box;
	Dimension tabMinDim = new Dimension(600,400);
	/**
	 * 
	 * @param name - the name that will appear on the tab 
	 */
	public knowlTab(String name){
			
		this.name = name;
		tab = new JPanel();
		tab.setPreferredSize(new Dimension(580, 400));
		jep = new JEditorPane();
		jep.setEditable(false);
		jep.setPreferredSize(new Dimension(560, 400));
		jsp = new JScrollPane(jep);
		jsp.setPreferredSize(new Dimension(580, 400));
		tab.add(jsp);
		setVisible(false);
		}
	
	/**
	 * 
	 * @param name- the name that will appear on the tab 
	 * @param iconPath - the path of the icon that will be used with the tab
	 */
	public knowlTab(String name, String iconPath){
		
		this.name = name;
		this.icon = genIcon(iconPath);
		tab = new JPanel();
		tab.setMinimumSize(tabMinDim);
		}
	
	public void setVisible(boolean set) {
		jsp.setVisible(set);
	}
	
	/**
	 * 
	 * @return - returns the name of the tab
	 */
	public String getName(){
		return this.name;
	}
	/**
	 * 
	 * @return - returns the JPanel that will be added to the tabs pane
	 */
	public JPanel getTab(){
		return this.tab;
	}
	
	public JEditorPane getJep() {
		return jep;
	}

	public void setJep(JEditorPane jep) {
		this.jep = jep;
	}

	//will be used to check for correct path and that file exists and the return an image icon
	private ImageIcon genIcon(String iconPath){
		//add code to check for correct file path
		
		
		return new ImageIcon(iconPath);
		
		
	}
	
	public void tieCheckBox(JCheckBox box){
		this.box = box;
		
	}
	
	public boolean isChecked(){
		return box.isSelected();
	}
	
}
