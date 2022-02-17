package eu.ibagroup.easyrpa.openframework.googlesheets;

import eu.ibagroup.easyrpa.openframework.googlesheets.annotations.GSheetColumn;

public class UserEntity {

    @GSheetColumn(name = "name")
    private String name;

    @GSheetColumn(name = "age")
    private int age;

    public UserEntity(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public UserEntity() {
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

    @Override
    public String toString() {
        return "UserEntity{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
