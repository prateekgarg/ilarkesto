package ilarkesto.swing;

import ilarkesto.auth.LoginData;
import ilarkesto.auth.LoginDataProvider;
import ilarkesto.auth.PropertiesLoginDataProvider;
import ilarkesto.io.IO;

import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginPanel extends JPanel implements LoginDataProvider {

	private JTextField loginField;
	private JPasswordField passwordField;

	public LoginPanel() {
		loginField = new JTextField(15);
		passwordField = new JPasswordField(15);

		setLayout(new GridLayout(2, 2, 5, 5));
		add(new JLabel("Login:"));
		add(loginField);
		add(new JLabel("Password:"));
		add(passwordField);
	}

	public void setLoginData(LoginDataProvider login) {
		setLoginData(login.getLoginData());
	}

	public void setLoginData(LoginData login) {
		setLogin(login.getLogin());
		setPassword(login.getPassword());
	}

	public void setLogin(String login) {
		loginField.setText(login);
	}

	public void setPassword(String password) {
		passwordField.setText(password);
	}

	@Override
	public LoginData getLoginData() {
		return new LoginData(loginField.getText(), String.valueOf(passwordField.getPassword()));
	}

	public static LoginData showDialog(Component parent, String title) {
		LoginPanel lp = new LoginPanel();
		int option = JOptionPane.showConfirmDialog(parent, lp, title, JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE);
		return option == JOptionPane.OK_OPTION ? lp.getLoginData() : null;
	}

	public static LoginData showDialog(Component parent, String title, File storageFile) {
		Properties properties = IO.loadProperties(storageFile, IO.UTF_8);
		LoginPanel lp = new LoginPanel();
		PropertiesLoginDataProvider pldp = new PropertiesLoginDataProvider(properties);
		lp.setLoginData(pldp);
		int option = JOptionPane.showConfirmDialog(parent, lp, title, JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE);
		if (option == JOptionPane.OK_OPTION) {
			LoginData ld = lp.getLoginData();
			pldp.setLoginData(ld);
			IO.saveLoadedProperties(properties, title);
			return ld;
		}
		return null;
	}

}
