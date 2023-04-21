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