package ru.trueengineering.lib.logger.autoconfigure.logging.model_1;

/**
 * @author m.yastrebov
 */
public class CompositeClass {
    private String name;
    private int age;

    public CompositeClass(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public CompositeClass() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
