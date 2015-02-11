package rickelectric.swingmods;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;

import rickelectric.UtilBox;
import rickelectric.furkmanager.utils.ThreadPool;

public class FadeList extends List implements Opacible{
		private static final long serialVersionUID = 1L;
		private float alpha;
		
		public FadeList(){
			init();
			}
		
		public void init(){
			setAlpha(0.5f);
		}
		
		private boolean oscillation;
		private int oscSpeed=20;
		@Override
		public boolean isOscillating(){return oscillation;}
		@Override
		public void setOscillating(boolean oscillation){this.oscillation=oscillation;}
		@Override
		public int getOscSpeed(){return oscSpeed;}
		@Override
		public void setOscSpeed(int oscSpeed){this.oscSpeed=oscSpeed;}
		
		public void stopOscillation(){oscillation=false;}
        
		@Override
		public void setAlpha(float value){
			if(value>1||value<0) return;
			if(value<0.02) setVisible(false);
			else setVisible(true);
			if(alpha!=value) {
				float old=alpha;
				alpha=value;
				firePropertyChange("alpha", old, alpha);
				repaint();
			}
			
		}
		
		@Override
		public float getAlpha(){return alpha;}
		
		@Override
		public void paint(Graphics g){
			Graphics2D g2 =(Graphics2D)g.create();
			g2.setComposite(
				AlphaComposite.getInstance(AlphaComposite.SRC_OVER,getAlpha())
			);
			super.paint(g2);
			g2.dispose();
		}
		
		public void fadeIn(final int msec){
			ThreadPool.run(new Runnable(){
				@Override
				public void run(){
					while(getAlpha()<0.9f){
						setAlpha(alpha+0.02f);
						UtilBox.getInstance().wait(1000/msec);
					}
					setAlpha(1f);
				}
			});
		}
    }