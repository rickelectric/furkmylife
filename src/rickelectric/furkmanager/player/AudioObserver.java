package rickelectric.furkmanager.player;

import java.util.Observer;

import javax.swing.JSlider;

import rickelectric.furkmanager.views.swingmods.JButtonLabel;

public interface AudioObserver extends Observer {

	JButtonLabel getPlayButton();
	JButtonLabel getStopButton();
	JSlider getPositionSlider();
	void detachObserver();
	void reattachObserver();

}
