package rickelectric.furkmanager.idownloader;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class JProgressCell extends JProgressBar implements TableCellRenderer {
	private static final long serialVersionUID = 1L;

	public JProgressCell(int min, int max) {
		super(min, max);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected,
					boolean hasFocus, int row, int column) {
		float val=((Float) value).floatValue();
		setValue((int) val);
		if(val>0f){ 
			//System.out.println(""+val);
			String[] v=(""+val+"00000000").split("\\.");
			setString(v[0]+"."+v[1].substring(0,2)+" %");
		}
		return this;
	}
	
}
