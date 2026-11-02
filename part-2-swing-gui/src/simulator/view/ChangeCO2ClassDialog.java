package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.*;

import simulator.model.RoadMap;
import simulator.model.Vehicle;

public class ChangeCO2ClassDialog extends JDialog {

	private JPanel panelPrincipal;
	private JLabel textoIntro;
	private JLabel etiquetaVehiculo;
	private JComboBox<Vehicle> comboVehiculos;
	private JLabel etiquetaCO2;
	private JComboBox<Integer> comboCO2;
	private JLabel etiquetaTicks;
	private JSpinner spinnerTicks;

	private JPanel panelControles;
	private JPanel panelBotones;
	private JButton botonOk;
	private JButton botonCancelar;

	private int estado = 0;
	private DefaultComboBoxModel<Vehicle> modeloVehiculos;
	private DefaultComboBoxModel<Integer> modeloCO2;

	public ChangeCO2ClassDialog(Frame ventanaPadre) {
		super(ventanaPadre, true);
		initGUI();
	}

	private void initGUI() {
		setTitle("Cambiar clase CO2");

		panelPrincipal = new JPanel();
		panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
		setContentPane(panelPrincipal);

		textoIntro = new JLabel("<html>Programa un evento para cambiar la clase de CO2 de un vehículo tras unos ticks.</html>");
		textoIntro.setAlignmentX(CENTER_ALIGNMENT);
		panelPrincipal.add(textoIntro);
		panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));

		panelControles = new JPanel();
		panelControles.setAlignmentX(CENTER_ALIGNMENT);
		panelPrincipal.add(panelControles);

		etiquetaVehiculo = new JLabel("Vehículo:");
		modeloVehiculos = new DefaultComboBoxModel<>();
		comboVehiculos = new JComboBox<>(modeloVehiculos);
		panelControles.add(etiquetaVehiculo);
		panelControles.add(comboVehiculos);

		etiquetaCO2 = new JLabel("Clase CO2:");
		modeloCO2 = new DefaultComboBoxModel<>();
		comboCO2 = new JComboBox<>(modeloCO2);
		panelControles.add(etiquetaCO2);
		panelControles.add(comboCO2);

		etiquetaTicks = new JLabel("Ticks:");
		spinnerTicks = new JSpinner(new SpinnerNumberModel(10, 1, 99999, 1));
		spinnerTicks.setPreferredSize(new Dimension(80, 30));
		panelControles.add(etiquetaTicks);
		panelControles.add(spinnerTicks);

		panelBotones = new JPanel();
		panelBotones.setAlignmentX(CENTER_ALIGNMENT);
		panelPrincipal.add(panelBotones);

		botonCancelar = new JButton("Cancelar");
		botonCancelar.addActionListener((ActionEvent e) -> {
			estado = 0;
			setVisible(false);
		});
		panelBotones.add(botonCancelar);

		botonOk = new JButton("OK");
		botonOk.addActionListener((ActionEvent e) -> {
			if (modeloVehiculos.getSelectedItem() != null && modeloCO2.getSelectedItem() != null) {
				estado = 1;
				setVisible(false);
			}
		});
		panelBotones.add(botonOk);

		setPreferredSize(new Dimension(500, 200));
		pack();
		setResizable(false);
		setVisible(false);
	}

	public int open(RoadMap mapa) {
		modeloVehiculos.removeAllElements();
		for (Vehicle v : mapa.getVehicles()) 
			modeloVehiculos.addElement(v);
		
		modeloCO2.removeAllElements();
		for (int i = 0; i <= 10; i++)
			modeloCO2.addElement(i);

		setLocation(getParent().getLocation().x + 10, getParent().getLocation().y + 10);
		setVisible(true);
		return estado;
	}

	public Integer getTicks() {
		return (Integer) spinnerTicks.getValue();
	}

	public Integer getCO2Class() {
		return (Integer) comboCO2.getSelectedItem();
	}

	public Vehicle getVehicle() {
		return (Vehicle) comboVehiculos.getSelectedItem();
	}
}
