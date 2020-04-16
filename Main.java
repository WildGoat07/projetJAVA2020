import java.time.LocalDate;

import utilities.*;

public class Main {
    public static void main(String[] args) {
        Person pers = new Person("p", "n");
        Product prod = new CD(new Price(5), "title", LocalDate.now());
        Order comm = new Order(pers, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2), new Price(-1));
        comm.addProduct(prod);
        System.out.println(comm.getCost());
    }
}