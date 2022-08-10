package org.acme;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.acme.entity.Person;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class PersonServiceTest {

    @Inject
    PersonService service;

    /*
     * update tests to understand inconsistent behaviour between
     * io.quarkus.hibernate.orm.panache.PanacheEntityBase.update and implicit update by changing entity attributes
     */
    @Test
    public void testCreatePerson() {
        String firstname = "Max";
        String newFirstname = "Paul";
        String newFirstname2 = "Paul2";
        String newFirstname3 = "Paul3";

        service.createPerson(firstname);                    // Person created, firstname = 'Max', created in DB after this line
        Person person = Person.findById(1L);                // returns created Person
        assertEquals(firstname, person.firstname);

        Person.updateFirstnameStatic(newFirstname);         // updates firstname: 'Max' -> 'Paul', updated in DB after this line
        Person personCached = Person.findById(1L);
        assertEquals(firstname, personCached.firstname);

        Person personUpdated = Person.findByIdForce(1L);    // clear cached entity to force query of db to get updated Person
        assertEquals(newFirstname, personUpdated.firstname);

//        personUpdated.updateFirstname(newFirstname2);       // FIXME person firstname not updated in db
        service.updatePerson(personUpdated, newFirstname2); // FIXME person firstname not updated in db
                                                            // firstname = 'Paul2' set in entity, but NOT persisted in DB, so it is not working as described in the docu
                                                            // in  'https://quarkus.io/guides/hibernate-orm-panache#most-useful-operations' by:
                                                            // "note that once persisted, you don't need to explicitly save your entity: all
                                                            // modifications are automatically persisted on transaction commit."

        service.findAndUpdatePerson(personUpdated, newFirstname2); // person firstname now finally updated in db
        service.findAndUpdatePerson2(1L, newFirstname3); // person firstname now finally updated in db

        String s="";
    }

}