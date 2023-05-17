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

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class TxAndEntityManagerTest {

    @Inject
    TransactionManager transactionManager;

    @Test
    public void test() throws SystemException {
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
        QuarkusTransaction.commit(); //flush/clear/close EntityManager -> Person persisted in DB,
        assertFalse(isManagedEntity(p));

        //find with TX
        QuarkusTransaction.begin();
            assertNotNull(transactionManager.getTransaction());
            Person personFoundFromDB = Person.findById(p.id);
            assertTrue(isManagedEntity(personFoundFromDB));

            //TODO update person in db  by separate tx here to demo that personFoundFromEM is returned fom EM (cache)

            Person personFoundFromEM = Person.findById(p.id); //returns cached person from entitymanager
            Person.getEntityManager().detach(personFoundFromDB);
            assertFalse(isManagedEntity(personFoundFromDB));
            Person personFoundFromDB3 = Person.findById(p.id); //query to db
            Person.getEntityManager().refresh(personFoundFromDB3); // update entity <- db

            //TODO update person in db  by separate tx here to demo that personFoundFromEM is returned fom EM (cache)
            Person personFoundFromEM2 = Person.findById(p.id); //returns cached person from entitymanager
            assertTrue(personFoundFromEM2 == personFoundFromDB3);
            Person.getEntityManager().refresh(personFoundFromDB3);
        QuarkusTransaction.commit();

        //find without TX
        logTX();
        personFoundFromDB = Person.findById(p.id);
        Log.info("person string: " + personFoundFromDB.toString());
        assertTrue(isManagedEntity(personFoundFromDB));
        assertNull(transactionManager.getTransaction());

        QuarkusTransaction.begin();
            logTX();
            Log.info("person string: " + Person.findById(p.id));
            logPersonInSameTX(p.id); //call to demo that same query in same tx returns cached entity from entity manager
        QuarkusTransaction.rollback();
    }
    @Test
    public void test2() throws SystemException {
        QuarkusTransaction.begin();
            Person p = new Person();
            p.id=1L;
            p.firstname = "firstname";
            p.lastname = "Muster";
        QuarkusTransaction.commit();

        Person foundPerson = Person.findById(p.id);
        foundPerson.firstname= "firstnameUpdated";

        Person.getEntityManager().refresh(foundPerson);

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


    @Transactional
    public void logPersonInSameTX(Long pid) {
        logTX();
        Log.info("person string: " + Person.findById(pid)); //Person entity returned from entity manager NOT from DB
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

}