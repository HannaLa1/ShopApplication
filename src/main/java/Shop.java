import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.sql.*;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Shop {
    private final List<Product> products = new ArrayList<>();
    private final List<Product> orders = new ArrayList<>();
    private final LinkedList<Product> productListSell = new LinkedList<>();
    private final String FILE_NAME_IO = "ShopApplication/src/main/java/ShopUnit13/result.txt";
    private final String sellFile = "ShopApplication/src/main/java/ShopUnit13/sellProducts.txt";
    private final DBShop dbShop = new DBShop();
    private final String DELETE_PRODUCT = "DELETE FROM products WHERE id = ?";
    private final String UPDATE_PRODUCT = "UPDATE products SET name = ?, price = ? WHERE id = ?";
    private final String INSERT_BUYING_PRODUCT = "INSERT INTO purchasedProducts VALUES(?, ?, ?, ?, ?)";

    public void addProduct(Product product) throws SQLException {
        boolean flag = products.stream()
                .noneMatch(item -> item.getId() == product.getId());

        if (flag) {
            products.add(product);
            String INSERT_NEW_PRODUCT = "INSERT INTO products VALUES(?, ?, ?, ?, ?)";
            try(PreparedStatement preparedStatement = dbShop.getConnection().prepareStatement(INSERT_NEW_PRODUCT)){
                preparedStatement.setInt(1, product.getId());
                preparedStatement.setString(2, product.getName());
                preparedStatement.setInt(3, product.getPrice());
                preparedStatement.setString(4, product.getLocalDate());
                preparedStatement.setTime(5, Time.valueOf(product.getLocalTime()));
                preparedStatement.execute();
            }
        }
    }

    public void addProductForSell(Product product) {
        boolean flag = productListSell.stream()
                .noneMatch(item -> item.getId() == product.getId());

        if (flag) {
            productListSell.add(product);
        }
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public List<Product> getOrders() {
        return orders;
    }

    public void deleteProduct(int id) {
        products.stream()
                .filter(i -> i.getId() == id)
                .findFirst()
                .ifPresentOrElse(i -> {
                            System.out.println("\n>>> Удалён продукт с id = " + id + "!");
                            products.remove(i);
                            try(PreparedStatement preparedStatement = dbShop.getConnection().prepareStatement(DELETE_PRODUCT)){
                                preparedStatement.setInt(1, id);
                                preparedStatement.executeUpdate();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        },
                        () -> {
                            System.out.println("*Нет товара с таким id!*");
                        });
    }

    public void buyProduct(int id) {
        products.stream()
                .filter(i -> i.getId() == id)
                .findFirst()
                .ifPresentOrElse(i -> {
                            System.out.println("\n>>> Куплен продукт с id = " + id + "!");
                            products.remove(i);
                            try(PreparedStatement preparedStatement = dbShop.getConnection().prepareStatement(DELETE_PRODUCT);
                                PreparedStatement preparedStatement1 = dbShop.getConnection().prepareStatement(INSERT_BUYING_PRODUCT)){
                                preparedStatement.setInt(1, id);
                                preparedStatement.executeUpdate();

                                preparedStatement1.setInt(1, i.getId());
                                preparedStatement1.setString(2, i.getName());
                                preparedStatement1.setInt(3, i.getPrice());
                                preparedStatement1.setString(4, i.getLocalDate());
                                preparedStatement1.setTime(5, Time.valueOf(i.getLocalTime()));
                                preparedStatement1.execute();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        },
                        () -> {
                            System.out.println("*Нет товара с таким id!*");
                        });
    }

    public void filterByPrice(int from, int to) {
        products.stream()
                .filter(p -> p.getPrice() >= from)
                .filter(p -> p.getPrice() <= to)
                .forEach(Product::printProduct);
    }

    public void filterByPriceWriteToJsonFile(int from, int to) throws IOException {
        List<Product> list = products.stream()
                .filter(p -> p.getPrice() >= from)
                .filter(p -> p.getPrice() <= to)
                .collect(Collectors.toList());

        ConverterToJSON.toJSON(list);
        //writeProductToFileForFilter(FILE_NAME_IO, list);
    }

    public void sortPriceDecreasing() {
        products.sort(Comparator.comparing(Product::getPrice).reversed());
    }

    public void sortPriceIncreasing() {
        products.sort(Comparator.comparing(Product::getPrice));
    }

    public void sortByOrder() {
        products.sort(Comparator.comparing(Product::getLocalDate).thenComparing(Product::getLocalTime).reversed());
    }

    public void getProductsFromDB() throws SQLException {
        String SELECT_PRODUCTS = "SELECT * FROM products";
        try(Statement statement = dbShop.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery(SELECT_PRODUCTS)) {

            while (resultSet.next()){
                products.add(new Product(resultSet.getInt("id"),  resultSet.getString("name"),
                        resultSet.getInt("price"), resultSet.getString("localeDate"),
                        LocalTime.parse(String.valueOf(resultSet.getTime("localeTime")))));
            }
        }
    }

    public void orderHistory() throws SQLException {
        String SELECT_ORDER_HISTORY = "SELECT * FROM purchasedProducts";
        try(Statement statement = dbShop.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery(SELECT_ORDER_HISTORY)) {

            while (resultSet.next()){
                orders.add(new Product(resultSet.getInt("id"),  resultSet.getString("name"),
                        resultSet.getInt("price"), resultSet.getString("localeDate"),
                        LocalTime.parse(String.valueOf(resultSet.getTime("localeTime")))));
            }
        }
    }

    public void editProduct(Product product) {
        products.stream()
                .filter(item -> item.getId() == product.getId())
                .findFirst()
                .ifPresentOrElse(x -> {
                            x.setName(product.getName());
                            x.setPrice(product.getPrice());
                            try(PreparedStatement preparedStatement = dbShop.getConnection().prepareStatement(UPDATE_PRODUCT)){
                                preparedStatement.setString(1, product.getName());
                                preparedStatement.setInt(2, product.getPrice());
                                preparedStatement.setInt(3, product.getId());
                                preparedStatement.executeUpdate();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        },
                        () -> {
                            System.out.println("*Нет товара с таким id!*");
                        });
    }

    public void writeProductToFile(String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            products.forEach(s -> {
                try {
                    bw.write(s + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void writeProductToFileForSell(String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            productListSell.forEach(s -> {
                try {
                    bw.write(s + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void writeProductToFileForFilter(String fileName, List<Product> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            list.forEach(s -> {
                try {
                    bw.write(s + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void parseFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String str;

            while ((str = br.readLine()) != null) {
                String[] arr = str.split("\n");

                Arrays.stream(arr)
                        .filter(s -> !s.equals(""))
                        .map(Product::new)
                        .forEach(products::add);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void toJavaObject() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String baseFile = "ShopApplication/src/main/java/ShopUnit13/jsonResult.json";
        List<Product> productList = Arrays.asList(mapper.readValue(new File(baseFile), Product[].class));
        products.addAll(productList);
    }

    public synchronized void get() throws InterruptedException {
        while (productListSell.size() < 1){
            try {
                wait();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        productListSell.removeFirst();
        writeProductToFileForSell(sellFile);
        System.out.println(">>>Товар куплен!");
        notify();
        Thread.sleep(500);
    }

    public synchronized void put() throws InterruptedException {
        while (productListSell.size() >= 10){
            try {
                wait();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        addProductForSell(new Application().inputData());
        writeProductToFileForSell(sellFile);
        System.out.println(">>>Товар добавлен!");
        notify();
        Thread.sleep(500);
    }
}
