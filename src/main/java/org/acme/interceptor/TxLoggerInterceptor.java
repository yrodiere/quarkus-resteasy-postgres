package org.acme.interceptor;


import io.quarkus.arc.Arc;
import io.quarkus.arc.InstanceHandle;
import io.quarkus.arc.Priority;
import org.jboss.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

@TxLogger
@Priority(10)
@Interceptor
public class TxLoggerInterceptor {

    @AroundInvoke
    Object logTx(InvocationContext ctx) throws Exception {
        Logger logger = Logger.getLogger(ctx.getTarget().getClass());

        Transaction tx = Arc.container().instance(TransactionManager.class).get().getTransaction();

        Object o = ctx.proceed();

        Transaction tx2 = Arc.container().instance(TransactionManager.class).get().getTransaction();

        if (tx != null && tx2 == null){
            logger.info("tx was closed");
        }


        return o;
    }
}
