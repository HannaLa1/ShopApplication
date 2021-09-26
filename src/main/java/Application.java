import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Application {
    private final Shop shop = new Shop();
    private Scanner input = new Scanner(System.in);
    private String FILE_NAME_IO = "ShopApplication/src/main/java/ShopUnit13/result.txt";

    public void start() throws IOException, SQLException {
        cleanConsole();
        shop.getProductsFromDB();
        shop.orderHistory();

        int key;
        do {
            cleanConsole();
            mainMenu();

            key = expInputKey(0, 7);
            switch (key) {
                case 1 -> {
                    cleanConsole();
                    addProductSubmenu();

                    int key1 = expInputKey(0, 4);
                    switch (key1) {
                        case 1 -> {
                            cleanConsole();
                            shop.sortPriceIncreasing();
                            typeOfDataOutput();
                            pause();
                        }
                        case 2 -> {
                            cleanConsole();
                            shop.sortPriceDecreasing();
                            typeOfDataOutput();
                            pause();
                        }
                        case 3 -> {
                            cleanConsole();
                            shop.sortByOrder();
                            typeOfDataOutput();
                            pause();
                        }
                        case 4 -> {
                            cleanConsole();
                            inputFilter();
                            pause();
                        }
                        case 0 -> {
                        }
                    }
                }
                case 2 -> {
                    cleanConsole();
                    shop.addProduct(inputData());
                    pause();
                }
                case 3 -> {
                    cleanConsole();
                    System.out.println("Введите id товара >>> ");
                    int id = expInput();
                    shop.deleteProduct(id);
                    pause();
                }
                case 4 -> {
                    cleanConsole();
                    System.out.println("Данные товара для редактирования:");
                    shop.editProduct(inputData());
                    pause();
                }
                case 5 -> {
                    cleanConsole();
                    Producer producer = new Producer(shop);
                    Consumer consumer = new Consumer(shop);
                    Thread t1 = new Thread(producer);
                    Thread t2 = new Thread(consumer);
                    t1.start();
                    t2.start();

                    try {
                        t1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        t2.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    pause();
                }
                case 6 -> {
                    cleanConsole();
                    System.out.println("Введите id товара для покупки >>> ");
                    int id = expInput();
                    shop.buyProduct(id);
                    pause();
                }
                case 7 -> {
                    cleanConsole();
                    typeOfOrdersOutput();
                    pause();
                }
                case 0 -> {
                }
            }

        } while (key != 0);
    }

    private int expInputKey(int from, int to) {
        int value = 0;
        boolean flag;

        do {
            flag = true;
            if (input.hasNextInt()) {
                value = input.nextInt();

                if (value < from || value > to) {
                    System.out.println("Выберите действие повторно!");
                }
            } else {
                flag = false;
                input.next();
                System.out.println("Введите положительное целое число!");
            }
        } while (!flag || value < from || value > to);

        return value;
    }

    private int expInput() {
        int value = 0;
        boolean flag;

        do {
            flag = true;
            if (input.hasNextInt()) {
                value = input.nextInt();

                if (value <= 0) {
                    System.out.println("Введите размер ещё раз!");
                }
            } else {
                flag = false;
                input.next();
                System.out.println("Введите положительное целое число!");
            }
        } while (!flag || value <= 0);

        return value;
    }

    private String expInputString() {
        String str;
        String regex = "^[A-ZА-Я]\\s?([^\\d && \\S]\\s?)*(\\d\\s?)*$";

        input = new Scanner(System.in);
        boolean flag = false;

        do {
            str = input.nextLine();

            if (Pattern.matches(regex, str)) {
                flag = true;
            }

            if (!flag) {
                System.out.println("Введите название, еще раз!");
                System.out.println("[Начинается с большой буквы, может содержать цифры," +
                        " но только в конце.\n Может содержать пробелы между словами и цифрами, но не 2 подряд]");
            }
        } while (!flag);

        return str;
    }

    private void inputFilter() throws IOException {
        System.out.println("\n---------------------------------------------------------------");
        System.out.println("Вывести информацию [в консоль / в json файл]  (1/0)?");
        System.out.println("---------------------------------------------------------------");

        int key2 = expInputKey(0, 1);
        cleanConsole();
        switch (key2) {
            case 0 -> {
                System.out.println(">>>Введите диапазон цен -->\n");
                System.out.println("*Введите нижнюю границу: ");
                int limit1 = expInput();
                System.out.println("*Введите верхнюю границу: ");
                int limit2 = expInput();
                shop.filterByPriceWriteToJsonFile(limit1, limit2);
                System.out.println(">>>Информация записана в json файл!");
            }
            case 1 -> {
                System.out.println(">>>Введите диапазон цен -->\n");
                System.out.println("*Введите нижнюю границу: ");
                int limit1 = expInput();
                System.out.println("*Введите верхнюю границу: ");
                int limit2 = expInput();
                shop.filterByPrice(limit1, limit2);
            }
        }
    }

    public Product inputData() {
        System.out.println("\nВведите id товара >>> ");
        int id = expInput();
        System.out.println("Введите название товара >>> ");
        String name = expInputString();
        System.out.println("Введите цену товара >>> ");
        int price = expInput();

        return new Product(id, name, price, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE), LocalTime.now());
    }

    private void cleanConsole() {
        repeat(30, System.out::println);
    }

    private void outputProducts() {
        shop.getAllProducts().forEach(Product::printProduct);
    }

    private void outputOrders() {
        shop.getOrders().forEach(Product::printProduct);
    }

    private void mainMenu() {
        System.out.println("\n\t\t\t\t\t\t%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("\t\t\t\t\t\t|_____________________М А Г А З И Н____________________|");
        System.out.println("\t\t\t\t\t\t%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("\t\t\t\t\t\t________________________________________________________");
        System.out.println("\t\t\t\t\t\t|>>>>>>>>>>>>>>>>>>>|КОРЗИНА ТОВАРОВ|<<<<<<<<<<<<<<<<<<|");
        System.out.println("\t\t\t\t\t\t|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|");

        System.out.println("\t\t\t\t\t\t|====================|Главное меню|====================|");
        System.out.println("\t\t\t\t\t\t|______________________________________________________|");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|1 >>> Вывод всех товаров                              |");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|2 >>> Добавление товара                               |");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|3 >>> Удаление товара                                 |");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|4 >>> Редактирование товара                           |");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|5 >>> Покупка и производство товаров                  |");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|6 >>> Покупка товаров                                 |");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|7 >>> История покупок товаров                         |");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|0 >>> Выход                                           |");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|                   _____________________________      |");
        System.out.println("\t\t\t\t\t\t|                   |Выберите действие...  <0-7>|      |");
        System.out.println("\t\t\t\t\t\t|                   |                           |      |");
        System.out.println("\t\t\t\t\t\t|___________________|                           |______|");
        System.out.println("\t\t\t\t\t\t.....................>>> ");
    }

    private void addProductSubmenu() {
        System.out.println("\n\n\t\t\t\t\t\t             ||||||||||||||||||||||||||||||||           ");
        System.out.println("\t\t\t\t\t\t                    Сортировка товаров                  ");
        System.out.println("\t\t\t\t\t\t             ||||||||||||||||||||||||||||||||           ");
        System.out.println("\t\t\t\t\t\t________________________________________________________");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|1 >>> по цене(возрастание)                            |");
        System.out.println("\t\t\t\t\t\t|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|2 >>> по цене(убывание)                               |");
        System.out.println("\t\t\t\t\t\t|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|3 >>> по добавлению(сначала новые, потом более старые)|");
        System.out.println("\t\t\t\t\t\t|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|4 >>> фильтр по цене                                  |");
        System.out.println("\t\t\t\t\t\t|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|0 >>> *Назад*                                         |");
        System.out.println("\t\t\t\t\t\t|                                                      |");
        System.out.println("\t\t\t\t\t\t|         ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  |");
        System.out.println("\t\t\t\t\t\t|         :Выберите соответствующий параметр.. <0-4>:  |");
        System.out.println("\t\t\t\t\t\t|         :                                         :  |");
        System.out.println("\t\t\t\t\t\t|_________:                                         :__|");
        System.out.println("\t\t\t\t\t\t.............>>> ");
    }

    private void readDataFromFile() throws IOException {
        System.out.println("\n------------------------------------------------------------");
        System.out.println("Прочитать данные о товаре [из файла / из json файла]  (1/0)?");
        System.out.println("------------------------------------------------------------");

        int key2 = expInputKey(0, 1);
        cleanConsole();
        switch (key2) {
            case 0 -> shop.toJavaObject();
            case 1 -> shop.parseFile(FILE_NAME_IO);
        }
    }

    private void typeOfDataOutput() throws IOException {
        System.out.println("\n---------------------------------------------------------------");
        System.out.println("Вывести информацию [в консоль / в json файл]  (1/0)?");
        System.out.println("---------------------------------------------------------------");

        int key2 = expInputKey(0, 1);
        cleanConsole();

        switch (key2) {
            case 0 -> {
                ConverterToJSON.toJSON(shop.getAllProducts());
                System.out.println(">>>Информация записана в json файл!");
            }
            case 1 -> outputProducts();
        }
    }

    private void typeOfOrdersOutput() throws IOException {
        System.out.println("\n---------------------------------------------------------------");
        System.out.println("Вывести информацию [в консоль / в json файл]  (1/0)?");
        System.out.println("---------------------------------------------------------------");

        int key2 = expInputKey(0, 1);
        cleanConsole();

        System.out.println("***История покупок товаров -->");

        switch (key2) {
            case 0 -> {
                ConverterToJSON.toJSONOrders(shop.getOrders());
                System.out.println(">>>Информация записана в json файл!");
            }
            case 1 -> outputOrders();
        }
    }

    private static void repeat(int n, Runnable r) {
        for (int i = 0; i < n; i++) {
            r.run();
        }
    }

    private void pause() throws IOException {
        System.out.println("Press ENTER to continue...");
        System.in.read();
    }
}
