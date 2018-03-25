import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class FistPath {
    enum Monastery {
        GUAN_IN, GUAN_IAN
    }

    private class Fight extends RecursiveTask<Integer> {
        Integer fighter1, fighter2;
        Integer winner;
        
        public Fight(int fighter1, int fighter2) {
            this.fighter1 = fighter1;
            this.fighter2 = fighter2;
        }

        private Integer fight(Integer f1, Integer f2) {
            if (energyCapacity[f1] > energyCapacity[f2])
                return f1;
            else if (energyCapacity[f2] > energyCapacity[f1])
                return f2;
            return f1;
        }

        @Override
        protected Integer compute() {
            if (fighter1.equals(fighter2))
                return fighter1;
            if (fighter1 + 1 == fighter2)
                return fight(fighter1, fighter2);

            Integer splittingPoint = (fighter2 + fighter1) / 2;

            Fight fights1 = new Fight(fighter1, splittingPoint);
            fights1.fork(); //arranges to asynchronously execute this task
            Fight fights2 = new Fight(splittingPoint, fighter2);

            winner = fight(fights2.compute(), fights1.join()); //returns the result of the computation when it is done
            return winner;
        }
    }

    private Monastery[] buddhists;
    private int[] energyCapacity;

    public FistPath() {
        initialize();
    }

    private void initialize() {
        Random random = new Random();
        int amount = random.nextInt(100) + 20;
        buddhists = new Monastery[amount];
        energyCapacity = new int[amount];

        for (int i = 0; i < amount; i++) {
            boolean firstMonastery = random.nextBoolean();
            if (firstMonastery) {
                buddhists[i] = Monastery.GUAN_IN;
            } else {
                buddhists[i] = Monastery.GUAN_IAN;
            }

            energyCapacity[i] = random.nextInt(6000);
        }
    }

    public void runTournament() {
        ForkJoinPool pool = new ForkJoinPool();
        Fight competition = new Fight(0, buddhists.length - 1);
        pool.invoke(competition);
        System.out.println(buddhists[competition.winner]);
    }

    public static void main(String[] args) {
        FistPath fistPath = new FistPath();
        fistPath.runTournament();
    }
}
