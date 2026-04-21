package com.httpbin.pojo;

public class Anything_Pojo {
    private Integer id;
    private String name;
    private Boolean active;
    private String status;
    private Integer age;

    // Constructors for different operations
    public Anything_Pojo() {} 
    
    // For POST
    public Anything_Pojo(int id, String name, boolean active) {
        this.id = id; this.name = name; this.active = active;
    }
    
    // For PUT/PATCH
    public void setStatus(String status) { this.status = status; }
    public void setAge(int age) { this.age = age; }

    // Getters/Setters
    public Integer getId() { return id; }
    public String getName() { return name; }
    public Boolean getActive() { return active; }
    public String getStatus() { return status; }
    public Integer getAge() { return age; }
}
