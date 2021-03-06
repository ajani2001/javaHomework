package com.ajani2001.code;

import com.ajani2001.code.factory.Factory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SwingView {
    final JProgressBar accessoryStorageBar;
    final JProgressBar bodyStorageBar;
    final JProgressBar motorStorageBar;
    final JProgressBar carStorageBar;
    final JSlider accessorySuppliersDelaySlider;
    final JSlider bodySupplierDelaySlider;
    final JSlider motorSupplierDelaySlider;
    final JSlider dealersDelaySlider;
    final JLabel accessoryDelayLabel;
    final JLabel bodyDelayLabel;
    final JLabel motorDelayLabel;
    final JLabel dealerDelayLabel;
    final JTextArea amountTextArea;
    final JButton startButton;

    public SwingView(Factory factory) {
        JFrame window = new JFrame("Factory");
        window.setSize(1200, 400);
        window.getContentPane().setLayout(new GridBagLayout());
        window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                factory.stop();
                System.exit(0);
            }
        });

        accessoryStorageBar = new JProgressBar(0, factory.getAccessoryStorageCapacity());
        accessoryStorageBar.setStringPainted(true);
        bodyStorageBar = new JProgressBar(0, factory.getBodyStorageCapacity());
        bodyStorageBar.setStringPainted(true);
        motorStorageBar = new JProgressBar(0, factory.getMotorStorageCapacity());
        motorStorageBar.setStringPainted(true);
        carStorageBar = new JProgressBar(0, factory.getCarStorageCapacity());
        carStorageBar.setStringPainted(true);

        accessorySuppliersDelaySlider = new JSlider(0, 2000, (int) factory.getAccessorySuppliersDelay());
        accessorySuppliersDelaySlider.setMajorTickSpacing(500);
        accessorySuppliersDelaySlider.setMinorTickSpacing(100);
        accessorySuppliersDelaySlider.setPaintTicks(true);
        accessorySuppliersDelaySlider.setPaintLabels(true);
        accessorySuppliersDelaySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if(!source.getValueIsAdjusting()) {
                    factory.setAccessorySuppliersDelay(source.getValue());
                }
            }
        });
        bodySupplierDelaySlider = new JSlider(0, 2000, (int) factory.getBodySupplierDelay());
        bodySupplierDelaySlider.setMajorTickSpacing(500);
        bodySupplierDelaySlider.setMinorTickSpacing(100);
        bodySupplierDelaySlider.setPaintTicks(true);
        bodySupplierDelaySlider.setPaintLabels(true);
        bodySupplierDelaySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if(!source.getValueIsAdjusting()) {
                    factory.setBodySupplierDelay(source.getValue());
                }
            }
        });
        motorSupplierDelaySlider = new JSlider(0, 2000, (int) factory.getMotorSupplierDelay());
        motorSupplierDelaySlider.setMajorTickSpacing(500);
        motorSupplierDelaySlider.setMinorTickSpacing(100);
        motorSupplierDelaySlider.setPaintTicks(true);
        motorSupplierDelaySlider.setPaintLabels(true);
        motorSupplierDelaySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if(!source.getValueIsAdjusting()) {
                    factory.setMotorSupplierDelay(source.getValue());
                }
            }
        });
        dealersDelaySlider = new JSlider(0, 2000, (int) factory.getDealersDelay());
        dealersDelaySlider.setMajorTickSpacing(500);
        dealersDelaySlider.setMinorTickSpacing(100);
        dealersDelaySlider.setPaintTicks(true);
        dealersDelaySlider.setPaintLabels(true);
        dealersDelaySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if(!source.getValueIsAdjusting()) {
                    factory.setDealersDelay(source.getValue());
                }
            }
        });

        amountTextArea = new JTextArea("Accessory suppliers: "+factory.getAccessorySuppliersNumber()+", Dealers: "+factory.getDealersNumber());
        amountTextArea.setEditable(false);

        startButton = new JButton("Start");
        startButton.setEnabled(!factory.isStarted());
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                factory.start();
                startButton.setEnabled(false);
            }
        });

        accessoryDelayLabel = new JLabel("Accessory suppliers delay (ms)");
        bodyDelayLabel = new JLabel("Body supplier delay (ms)");
        motorDelayLabel = new JLabel("Motor supplier delay (ms)");
        dealerDelayLabel = new JLabel("Dealers delay (ms)");

        Thread cycleUpdater = new Thread(new Runnable() {
            final long delayMillis = 100;
            int accessoryStorageCapacity;
            int bodyStorageCapacity;
            int motorStorageCapacity;
            int carStorageCapacity;
            int accessorySuppliersNumber;
            int dealersNumber;

            {
                accessoryStorageCapacity = factory.getAccessoryStorageCapacity();
                bodyStorageCapacity = factory.getBodyStorageCapacity();
                motorStorageCapacity = factory.getMotorStorageCapacity();
                carStorageCapacity = factory.getCarStorageCapacity();
                accessorySuppliersNumber = factory.getAccessorySuppliersNumber();
                dealersNumber = factory.getDealersNumber();
            }
            @Override
            public void run() {
                while(true) {
                    int accessoryStorageItemNumber = factory.getAccessoryStorageItemNumber();
                    int bodyStorageItemNumber = factory.getBodyStorageItemNumber();
                    int motorStorageItemNumber = factory.getMotorStorageItemNumber();
                    int carStorageItemNumber = factory.getCarStorageItemNumber();
                    int accessorySupplied = factory.getAccessorySuppliedNumber();
                    int bodiesSupplied = factory.getCarBodiesSuppliedNumber();
                    int motorSupplied = factory.getCarMotorsSuppliedNumber();
                    int carsSupplied = factory.getCarsSuppliedNumber();
                    int tasksInQueue = factory.getWorkerTaskQueueSize();

                    accessoryStorageBar.setValue(accessoryStorageItemNumber);
                    accessoryStorageBar.setString("Accessory storage: "+accessoryStorageItemNumber+"/"+accessoryStorageCapacity);
                    bodyStorageBar.setValue(bodyStorageItemNumber);
                    bodyStorageBar.setString("Body storage: "+bodyStorageItemNumber+"/"+bodyStorageCapacity);
                    motorStorageBar.setValue(motorStorageItemNumber);
                    motorStorageBar.setString("Motor storage: "+motorStorageItemNumber+"/"+motorStorageCapacity);
                    carStorageBar.setValue(carStorageItemNumber);
                    carStorageBar.setString("Car storage: "+carStorageItemNumber+"/"+carStorageCapacity);

                    amountTextArea.setText("Accessory suppliers: "+accessorySuppliersNumber+", Dealers: "+dealersNumber+System.lineSeparator()+"Total supplied: accessory: "+accessorySupplied+", bodies: "+bodiesSupplied+", motors: "+motorSupplied+", cars: "+carsSupplied+System.lineSeparator()+"Tasks in queue: "+tasksInQueue);
                    startButton.setEnabled(!factory.isStarted());

                    try {
                        Thread.sleep(delayMillis);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });

        GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0);

        constraints.gridx = 1;
        window.getContentPane().add(accessoryDelayLabel, constraints);
        constraints.gridy = 1;
        window.getContentPane().add(accessorySuppliersDelaySlider, constraints);
        constraints.gridy = 2;
        window.getContentPane().add(bodyDelayLabel, constraints);
        constraints.gridy = 3;
        window.getContentPane().add(bodySupplierDelaySlider, constraints);
        constraints.gridy = 4;
        window.getContentPane().add(motorDelayLabel, constraints);
        constraints.gridy = 5;
        window.getContentPane().add(motorSupplierDelaySlider, constraints);
        constraints.gridy = 6;
        window.getContentPane().add(dealerDelayLabel, constraints);
        constraints.gridy = 7;
        window.getContentPane().add(dealersDelaySlider, constraints);

        constraints.gridy = constraints.gridx = 0;
        constraints.gridheight = 2;
        window.getContentPane().add(accessoryStorageBar, constraints);
        constraints.gridy = 2;
        window.getContentPane().add(bodyStorageBar, constraints);
        constraints.gridy = 4;
        window.getContentPane().add(motorStorageBar, constraints);
        constraints.gridy = 6;
        window.getContentPane().add(carStorageBar, constraints);

        constraints.gridheight = 1;
        constraints.gridy = 8;
        window.getContentPane().add(amountTextArea, constraints);

        constraints.gridx = 1;
        window.getContentPane().add(startButton, constraints);

        window.setVisible(true);
        cycleUpdater.start();
    }
}
