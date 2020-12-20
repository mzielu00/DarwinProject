package ui;

import model.Animal;
import model.Plant;
import service.Simulation;
import service.SimulationProgressListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainGui {
    //pixels
    public static final int MAP_SIZE = 500;
    private static final int APP_SIZE = 600;
    private static final int BUTTON_SIZE_WIDTH = 100;
    private static final int BUTTON_SIZE_HEIGHT = 50;

    //other
    private static final int MAP_WIDTH = 50;
    private static final int MAP_HEIGHT = 50;
    private static final int JUNGLE_WIDTH = 10;
    private static final int JUNGLE_HEIGHT = 10;

    private static final int ANIMALS_NUMBER = 80;
    private static final int ANIMAL_STARTING_ENERGY = 80;
    private static final int PLANT_ENERGY = 80;

    private Simulation simulation;

    private MainGui() {
        initLayout();
    }

    private void initLayout() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Darwin Simulation");
        frame.pack();
        frame.setVisible(true);
        frame.setSize(APP_SIZE, APP_SIZE);

        JPanel panel = new JPanel();
        frame.add(panel);

        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(BUTTON_SIZE_WIDTH, BUTTON_SIZE_HEIGHT));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulation.start();
            }
        });

        JButton stopButton = new JButton("Stop");
        stopButton.setPreferredSize(new Dimension(BUTTON_SIZE_WIDTH, BUTTON_SIZE_HEIGHT));
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulation.stop();
            }
        });

        MapComponent mapComponent = new MapComponent(MAP_WIDTH, MAP_HEIGHT, JUNGLE_WIDTH, JUNGLE_HEIGHT);
        mapComponent.setPreferredSize(new Dimension(MAP_SIZE, MAP_SIZE));

        panel.add(mapComponent);
        panel.add(startButton);
        panel.add(stopButton);

        simulation = new Simulation(MAP_WIDTH, MAP_HEIGHT, JUNGLE_WIDTH, JUNGLE_HEIGHT, ANIMALS_NUMBER, ANIMAL_STARTING_ENERGY, PLANT_ENERGY);
        simulation.addEventListener(new SimulationProgressListener() {
            @Override
            public void update(List<Animal> animals, List<Plant> plants) {
                mapComponent.refresh(animals, plants);
            }
        });
        simulation.init();
    }

    public static void main(String[] args) {
        new MainGui();
    }
}
