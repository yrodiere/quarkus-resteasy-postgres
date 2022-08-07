package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.transaction.SystemException;
import javax.transaction.Transactional;

@Entity
public class Person extends PanacheEntityBase {
    @Id
    @Column
    public Long id;
    public String firstname;
    public String lastname;

    public Person() {
    }

    @Transactional
    public static void updateFirstnameStatic(){
        update("firstname='Paul' where id=1");
    }

    @Transactional
    public void updateFirstname() {
        firstname = "Paul2";
    }
}
