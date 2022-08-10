package org.acme;


import io.quarkus.hibernate.orm.panache.Panache;
import org.acme.entity.Person;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class PersonService {
    @Inject
    Logger log;

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
        //FIXME shouldnt make this personArg managed again? "When using JPA, to reassociate a detached entity to an active EntityManager, you can use the merge operation."
        // https://stackoverflow.com/questions/912659/what-is-the-proper-way-to-re-attach-detached-objects-in-hibernate/60661154#60661154
        Panache.getEntityManager().merge(personArg);
        log.info("PanacheEntityBase.getEntityManager().contains(personArg)="+ Panache.getEntityManager().contains(personArg));
        Person person = Person.findById(personArg.id);
        log.info("PanacheEntityBase.getEntityManager().contains(person)="+Panache.getEntityManager().contains(person));

        person.firstname = newFirstname;
    }

    // As long as we work with entites with transaction they keep managed and updates are working
    // As soon as we exit a transaction boundary, the entitymananger is cleared and closed, entities get detached
    // Only way to re-attach them/get them back in managed state seems to be a find
    @Transactional
    public void findAndUpdatePerson2(Long personId, String newFirstname){
        Person person = findPerson(personId);
        person.firstname = newFirstname;
    }

    public Person findPerson(Long personId){
        return Person.findById(personId);
    }
}