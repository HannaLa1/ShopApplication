public class Producer implements Runnable{
    Shop shop;

    public Producer(Shop shop) {
        this.shop = shop;
    }

    public void run(){
        for (int i = 1; i < 13; i++) {
            try {
                shop.put();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
