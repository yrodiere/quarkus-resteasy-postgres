package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.acme.entity.Person;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.SystemException;

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
        service.createPerson();                 // Person created, firstname = 'Max', created in DB after this line

        Person person = Person.findById(1L);    // returns created Person
        Person.updateFirstnameStatic();         // updates firstname: 'Max' -> 'Paul', updated in DB after this line

        Person person2 = Person.findById(1L);   // returns created/not updated Person: firstname = 'Max' ...WTF?
                                                // Why is outdated Person returned instead of the updated one from db?

        person2.updateFirstname();              // firstname = 'Paul2' set in entity, but NOT persisted in DB, so it is not working as described in the docu
                                                // in  'https://quarkus.io/guides/hibernate-orm-panache#most-useful-operations' by:
                                                // "note that once persisted, you don't need to explicitly save your entity: all
                                                // modifications are automatically persisted on transaction commit."
    }

}