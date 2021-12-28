import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentAccessExecutor {
	public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(9);
        for (int i = 1; i < 10; i++) {
        	Runnable runnableTask = new ConcurrentAccessTask(i);
        	executorService.submit(runnableTask);
        }
        executorService.shutdown();
    }
}
