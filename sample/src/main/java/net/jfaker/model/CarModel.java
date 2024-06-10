package net.jfaker.model;

public class CarModel {

    private final long id;
    private final String color;
    private final String brand;
    private final int year;

    public static CarModelBuilder builder(){
        return new CarModelBuilder();
    }

    public CarModel(final long id, final String color, final String brand, final int year) {
        this.id = id;
        this.color = color;
        this.brand = brand;
        this.year = year;
    }

    public long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getBrand() {
        return brand;
    }

    public int getYear() {
        return year;
    }


    public static final class CarModelBuilder {
        private long id;
        private String color;
        private String brand;
        private int year;

        public CarModelBuilder withId(long id) {
            this.id = id;
            return this;
        }

        public CarModelBuilder withColor(String color) {
            this.color = color;
            return this;
        }

        public CarModelBuilder withBrand(String brand) {
            this.brand = brand;
            return this;
        }

        public CarModelBuilder withYear(int year) {
            this.year = year;
            return this;
        }

        public CarModel build() {
            return new CarModel(id, color, brand, year);
        }
    }
}
