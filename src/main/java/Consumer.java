public class Consumer implements Runnable{
    Shop shop;

    public Consumer(Shop shop) {
        this.shop = shop;
    }

    public void run(){
        for (int i = 1; i < 13; i++) {
            try {
                shop.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
