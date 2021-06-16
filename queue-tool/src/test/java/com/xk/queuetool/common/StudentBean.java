package com.xk.queuetool.common;

import java.io.Serializable;

/**
 * @author xiongkai
 * @version 1.0
 * @date 2021-06-16 14:23
 */
public class StudentBean implements Serializable {

    private Integer id;

    private String name;

    public StudentBean(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "StudentBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
