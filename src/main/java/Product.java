import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalTime;

public class Product {
    @JsonProperty("Id")
    private int id;

    @JsonProperty("Название")
    private String name;

    @JsonProperty("Цена")
    private int price;

    @JsonProperty("Дата добавления")
    private String localDate;

    @JsonProperty("Время")
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = ParseDeserializer.class)
    private LocalTime localTime;

    public Product() {}

    public Product(int id, String name, int price, String localDate, LocalTime localTime) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.localDate = localDate;
        this.localTime = localTime;
    }

    public Product(String str) {
        String[] parts = str.split("\t");

        this.id = Integer.parseInt(parts[0]);
        this.name = parts[1];
        this.price = Integer.parseInt(parts[2]);
        this.localDate = parts[3];
        this.localTime = LocalTime.parse(parts[4]);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLocalDate() {
        return localDate;
    }

    public void setLocalDate(String localDate) {
        this.localDate = localDate;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public void printProduct(){
        System.out.println("Информация о товаре:");
        System.out.println(">>> Id --> " + id);
        System.out.println(">>> Название --> " + name);
        System.out.println(">>> Цена --> " + price);
        System.out.println(">>> Дата добавления --> " + localDate + " | " + localTime);
        System.out.println("---------------------------------------------------------");
    }

    public String toString() {
        return  id + "\t" +
                name + "\t" +
                price + "\t" +
                localDate + "\t" + localTime;
    }
}
