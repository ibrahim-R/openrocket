package net.sf.openrocket.gui.dialogs.flightconfiguration;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import net.sf.openrocket.gui.util.GUIUtil;
import net.sf.openrocket.l10n.Translator;
import net.sf.openrocket.rocketcomponent.Rocket;
import net.sf.openrocket.startup.Application;

public class RenameConfigDialog extends JDialog {
	
	private static final Translator trans = Application.getTranslator();
	
	RenameConfigDialog(final FlightConfigurationDialog parent, final Rocket rocket) {
		super(parent, trans.get("edtmotorconfdlg.title.Renameconf"), Dialog.ModalityType.APPLICATION_MODAL);
		final String configId = rocket.getDefaultConfiguration().getFlightConfigurationID();
		
		JPanel panel = new JPanel(new MigLayout("fill"));
		
		// FIXME: Localize
		panel.add(new JLabel("Name for flight configuration:"), "span, wrap rel");
		
		final JTextField textbox = new JTextField(rocket.getFlightConfigurationName(configId));
		panel.add(textbox, "span, w 200lp, growx, wrap para");
		
		panel.add(new JPanel(), "growx");
		
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newName = textbox.getText();
				rocket.setFlightConfigurationName(configId, newName);
				RenameConfigDialog.this.setVisible(false);
			}
		});
		panel.add(okButton);
		
		JButton defaultButton = new JButton("Reset to default");
		defaultButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rocket.setFlightConfigurationName(configId, null);
				RenameConfigDialog.this.setVisible(false);
			}
		});
		panel.add(defaultButton);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RenameConfigDialog.this.setVisible(false);
			}
		});
		panel.add(cancel);
		
		this.add(panel);
		
		GUIUtil.setDisposableDialogOptions(this, okButton);
	}
	
}
