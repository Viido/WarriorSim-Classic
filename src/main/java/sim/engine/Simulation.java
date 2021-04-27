package sim.engine;

import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sim.Main;
import sim.settings.Settings;
import sim.settings.CharacterSetup;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulation {
    private Settings settings;
    private CharacterSetup characterSetup;

    private Logger logger = LogManager.getLogger(Simulation.class);

    public Simulation(Settings settings) {
        this.settings = settings;
        this.characterSetup = settings.getCharacterSetup();

        logger.debug("Simulation created." + characterSetup.toString());
    }

    public FightResult run(JFXProgressBar progressBar, Label averageDPS){
        Main.loggingEnabled = settings.getIterations() == 1;

        long start = System.currentTimeMillis();

        int cores;

        if(settings.getIterations() <= Runtime.getRuntime().availableProcessors()){
            cores = settings.getIterations();
        }else{
            cores = Runtime.getRuntime().availableProcessors();
        }


        int[] iterationsPerCore = splitIterations(cores);

        List<Double> results = new ArrayList<>();

        FightResult result = new FightResult();

        final AtomicInteger simulationProgress = new AtomicInteger(0);

        progressBar.setVisible(true);

        for(int i = 0; i < cores; i++){
            int chunk = iterationsPerCore[i];
            logger.debug("Chunk size: {}", chunk);


            Task<FightResult> task = new Task<>(){
                @Override
                protected FightResult call() throws Exception {
                    FightResult result = new FightResult();

                    try{
                        int counter = 0;

                        for(int j= 0; j < chunk; j++){

                            Fight fight = new Fight(settings);

                            if(counter % 10000 == 0){
                                Thread.sleep(1);
                                Platform.runLater(() -> progressBar.setProgress(simulationProgress.addAndGet(10000)/(double)settings.getIterations()));
                            }

                            counter ++;

                            FightResult result2 = fight.run();

                            result.merge(result2);
                        }

                        result.averageResults(chunk);

                    }catch (Throwable t){
                        t.printStackTrace();
                    }

                    return result;
                }
            };

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

            task.setOnSucceeded(e -> {
                result.merge(task.getValue());
                results.add(0.0);

                if(results.size() == cores){
                    long end = System.currentTimeMillis();

                    result.averageResults(cores);

                    logger.info("Crit: " + characterSetup.getWarrior().getCritMH() + " Hit: " +  characterSetup.getWarrior().getHit() + " Weapon Skill: " + characterSetup.getWarrior().getWeaponSkillMH());
                    logger.info(result);
                    logger.info("Average DPS: {}", result.getAverageDamage() / settings.getFightDuration());
                    logger.info("Sample size: " + settings.getIterations() + " Completed in: " + (end - start)/1000.0 + " seconds");

                    averageDPS.setText(Math.round(result.getAverageDamage() / settings.getFightDuration() * 100.0) / 100.0 + " DPS");
                    LogManager.shutdown();
                }
            });
        }

        return result;
    }

    private int[] splitIterations(int splits){
        int[] result = new int[splits];

        int approximateSplit = settings.getIterations()/splits;

        int missingIterations = settings.getIterations() - approximateSplit * splits;

        for(int i = 0; i < splits; i++){
            result[i] = approximateSplit;
        }

        for(int i = 0; i < missingIterations; i++){
            result[i] += 1;
        }

        return result;
    }
}
