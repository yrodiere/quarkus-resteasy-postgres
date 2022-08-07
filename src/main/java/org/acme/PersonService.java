package org.acme;


import org.acme.entity.Person;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class PersonService {
    @Transactional
    public void createPerson() {
        Person p = new Person();
        p.id=1L;
        p.firstname = "Max";
        p.lastname = "Muster";
        p.persist();
    }
}