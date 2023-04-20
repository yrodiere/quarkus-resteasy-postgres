package org.acme;

import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.acme.entity.Person;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class PersonServiceTest {

    @Inject
    PersonService service;

    @Inject
    TransactionManager transactionManager;

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
    @Test
    public void testUpdatePerson() throws SystemException {
        assertNull(transactionManager.getTransaction());

        String firstname = "Max";
        service.createPerson(firstname);

        Person person = Person.findById(1L);
        assertTrue(isManagedEntity(person)); //person is managed
        assertNull(transactionManager.getTransaction());        //there is no active TX


        String newFirstname = "Paul";
        //person is a managed entity but without active TX. When passed to a transactional method, person is not managed/updated anymore
        service.updatePerson(person, newFirstname);
        assertNull(transactionManager.getTransaction());



        //TX MUST BE STARTED HERE, otherwise person object will be detached in PersonService.updatePerson method, thus not updated
        QuarkusTransaction.begin();
        logTX();
        person = Person.findById(1L);
        assertTrue(isManagedEntity(person));
        logEntityManagedState(person);

        newFirstname = "Paul";
        service.updatePerson(person, newFirstname); //This method "joins" the TX created in the test here by code
        logEntityManagedState(person);
        QuarkusTransaction.commit();

        logTX();
        logEntityManagedState(person);

        //just after exiting the TX of this test method, the updated will be persisted to DB

        QuarkusTransaction.begin();
        logTX();
        Person personUpdated = Person.findById(1L);
        assertEquals(newFirstname, personUpdated.firstname);
        QuarkusTransaction.rollback();
    }

    private static void logEntityManagedState(Person person) {
        Log.info("Person object is managed: " + isManagedEntity(person));
    }

    private void logTX() {
        try {
            if (transactionManager.getTransaction() != null) {
                Log.info("tx: " + transactionManager.getTransaction().toString());
            } else {
                Log.info("tx: none");
            }
        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testEntityManagerAndTx() throws SystemException {
        QuarkusTransaction.begin();
            Person p = new Person();
            p.id=1L;
            p.firstname = "firstname";
            p.lastname = "Muster";

            assertFalse(isManagedEntity(p));
            Person.persist(p);
            assertTrue(isManagedEntity(p));

            doInSameTX(p);
            doInNewTX(p);

            Person personFound = Person.findById(p.id); //returns Person from EntityManager as it is not yet persisted to DB
            assertNotNull(personFound);
        QuarkusTransaction.commit(); //persists Person p to DB, flush/clear/close EntityManager
        assertFalse(isManagedEntity(p));

        //find with TX
        QuarkusTransaction.begin();
            assertNotNull(transactionManager.getTransaction());
            Person personFoundFromDB = Person.findById(p.id);
            assertTrue(isManagedEntity(personFoundFromDB));
        QuarkusTransaction.commit();

        //find without TX
        personFoundFromDB = Person.findById(p.id);
        assertTrue(isManagedEntity(personFoundFromDB));
        assertNull(transactionManager.getTransaction());
    }

    private static boolean isManagedEntity(Person p) {
        return Person.getEntityManager().contains(p);
    }

    /*
    just to demo that Person p is still managed by joining same tx of caller (-> same entity manager)
    */
    @Transactional
    public void doInSameTX(Person p) {
        assertTrue(isManagedEntity(p));
    }

    /*
    just to demo that Person p get detached when passed to new tx (-> new entity manager)
    */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void doInNewTX(Person p) {
        assertFalse(isManagedEntity(p));
    }


}