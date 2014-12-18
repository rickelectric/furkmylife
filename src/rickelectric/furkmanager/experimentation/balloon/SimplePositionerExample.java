/**
 * Copyright (c) 2011-2013 Bernhard Pauler, Tim Molderez.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the 3-Clause BSD License
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/BSD-3-Clause
 */

package rickelectric.furkmanager.beta.balloon;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.positioners.CenteredPositioner;
import net.java.balloontip.styles.EdgedBalloonStyle;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.panels.Main_SettingsView;

/**
 * Simple application demonstrating a BalloonTip with a custom
 * BalloonTipPositioner
 * 
 * @author Tim Molderez
 */
public class SimplePositionerExample {
	/**
	 * Main method
	 * 
	 * @param args
	 *            command-line arguments (unused)
	 */
	public static void main(String[] args) {
		SettingsManager.init();

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Setup the application's window
				JFrame frame = new JFrame("Simple BalloonTipPositioner example");
				frame.setIconImage(new ImageIcon(TableExample.class
						.getResource("/rickelectric/furkmanager/img/fr.png"))
						.getImage());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				// Setup the content pane
				JPanel contentPane = new JPanel();
				contentPane.setLayout(null);
				frame.setContentPane(contentPane);

				// Add a button
				final JButton button = new JButton("Settings...");
				button.setBounds(10,100,120,25);
				contentPane.add(button);

				Main_SettingsView sv = new Main_SettingsView();
				// Now construct the balloon tip, with our own positioner
				final BalloonTip balloonTip = new BalloonTip(button, sv,
						new EdgedBalloonStyle(sv.getBackground(), Color.BLUE),
						new CenteredPositioner(200), null);
				/*
				 * balloonTip.setCloseButton(BalloonTip.getDefaultCloseButton(),
				 * false);
				 */
				balloonTip.setVisible(false);
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						balloonTip.setVisible(!balloonTip.isVisible());
					}
				});

				// Display the window
				frame.pack();
				frame.setSize(320, 240);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
