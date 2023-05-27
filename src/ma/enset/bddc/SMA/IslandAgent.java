package ma.enset.bddc.SMA;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import ma.enset.bddc.GAUtils;
import ma.enset.bddc.Individual;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class IslandAgent extends Agent {
    private Individual[] population = new Individual[GAUtils.POPULATION_SIZE];
    private  Individual individual1;
    public Individual individual2;

    @Override
    protected void setup() {
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName("island");
        serviceDescription.setType("ga");
        dfAgentDescription.addServices(serviceDescription);

        try {
            DFService.register(this,dfAgentDescription);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
        SequentialBehaviour sequentialBehaviour=new SequentialBehaviour();
        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {

            }
        });

        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                initializePopulation();
                sortPopulation();

            }
        });
        sequentialBehaviour.addSubBehaviour(new Behaviour() {
            @Override
            public void action() {
                sortPopulation();

                crossover();

                mutation();

            }

            @Override
            public boolean done() {
                return false;
            }
        });
        addBehaviour(sequentialBehaviour);
    }
    public void initializePopulation() {
        for (int i = 0; i < GAUtils.POPULATION_SIZE; i++) {
            population[i] = new Individual();
            population[i].calculateFitness();

        }
    }
    public void selection() {
        individual1 = population[0];
        individual2 = population[1];
    }

    public void crossover() {
        //  selection();
        individual1 = new Individual(Arrays.copyOf(population[0].getChromosome(), GAUtils.CHROMOSOME_SIZE));
        individual2 = new Individual(Arrays.copyOf(population[1].getChromosome(), GAUtils.CHROMOSOME_SIZE));

        Random random = new Random();
        int crossPoint = random.nextInt(GAUtils.CHROMOSOME_SIZE - 1);
        crossPoint++;
        for (int i = 0; i < crossPoint; i++) {
            individual1.getChromosome()[i] = population[1].getChromosome()[i];
            individual2.getChromosome()[i] = population[0].getChromosome()[i];
        }


//        System.out.println("Crossover Point : " + crossPoint);
//        System.out.println("GAWith0and1.Individual 1 : " + Arrays.toString(individual1.getChromosome()));
//        System.out.println("GAWith0and1.Individual 2 : " + Arrays.toString(individual2.getChromosome()));
    }

    public void showPopulation() {
        for (Individual i : population) {
            System.out.println(Arrays.toString(i.getChromosome()) + " = " + i.getFitness());
//            System.out.println(Arrays.toString(i.getChromosome()));
        }
    }

    public void sortPopulation() {
        Arrays.sort(population, Comparator.reverseOrder());
    }

    public void mutation() {
        Random random = new Random();
        //GAWith0and1.Individual 1
        if (random.nextDouble() > GAUtils.MUTATION_PROBABILITY) {
            int index = random.nextInt(GAUtils.CHROMOSOME_SIZE);
//            individual1.getChromosome()[index]=1-individual1.getChromosome()[index];
            individual1.getChromosome()[index]=GAUtils.ALPHABET.charAt(random.nextInt(GAUtils.ALPHABET.length()));

        }
        //GAWith0and1.Individual 2
        if (random.nextDouble() > GAUtils.MUTATION_PROBABILITY) {
            int index = random.nextInt(GAUtils.CHROMOSOME_SIZE);
            individual2.getChromosome()[index]=GAUtils.ALPHABET.charAt(random.nextInt(GAUtils.ALPHABET.length()));

        }


//        System.out.println("After Mutation : ");
//        System.out.println("GAWith0and1.Individual 1 : " + Arrays.toString(individual1.getChromosome()));
//        System.out.println("GAWith0and1.Individual 2 : " + Arrays.toString(individual2.getChromosome()));
        individual1.calculateFitness();
        individual2.calculateFitness();
        population[GAUtils.POPULATION_SIZE - 2] = individual1;
        population[GAUtils.POPULATION_SIZE - 1] = individual2;

    }




    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }
}
