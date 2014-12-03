package rickelectric.furkmanager.player;

import java.util.Observer;

import javax.swing.JSlider;

import rickelectric.furkmanager.views.swingmods.JButtonLabel;

public interface VideoObserver extends Observer {

	JButtonLabel getPlayButton();
	JButtonLabel getStopButton();
	JSlider getPositionSlider();
	void detachObserver();
	void reattachObserver();
	JButtonLabel getFullscreenButton();

}
