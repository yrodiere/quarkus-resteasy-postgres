package org.acme;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
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

    @Transactional
    public void findAndUpdatePerson(Person personArg, String newFirstname){
        Person person = Person.findById(personArg.id);
        person.firstname = newFirstname;
    }
}