import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;
 
public class ReadWriteLock {
    private static ReentrantReadWriteLock rwl=new ReentrantReadWriteLock();
    private static  double data=0;
    static class readClass implements Runnable{
        @Override
        public void run() {
            rwl.readLock().lock();
            System.out.println("读数据："+data);
            rwl.readLock().unlock();
        }
    }
    
    static class writeClass implements Runnable{
        private double i;
        
        public writeClass(double i) {
            this.i = i;
        }
 
        @Override
        public void run() {
            rwl.writeLock().lock();
            data=i;
            System.out.println("写数据： "+data);
            rwl.writeLock().unlock();
        }
        
    }
     
    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool=Executors.newCachedThreadPool();
        for(int i=0;i<10;i++){
            pool.submit(new readClass());
            pool.submit(new writeClass((double)new Random().nextDouble()));
            pool.submit(new writeClass((double)new Random().nextDouble()));
            Thread.sleep(1000);
        }
         
        pool.shutdown();
    }
    
 
}