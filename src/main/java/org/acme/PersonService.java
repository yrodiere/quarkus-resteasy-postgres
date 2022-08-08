package org.acme;


import org.acme.entity.Person;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class PersonService {
    @Transactional
    public void createPerson(String firstname) {
        Person p = new Person();
        p.id=1L;
        p.firstname = firstname;
        p.lastname = "Muster";
        p.persist();
    }

    @Transactional
    public void updatePerson(Person person, String newFirstname){
        person.firstname = newFirstname;
    }
}