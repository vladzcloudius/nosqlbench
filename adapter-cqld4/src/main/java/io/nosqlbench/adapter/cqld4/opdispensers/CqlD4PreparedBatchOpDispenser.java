package io.nosqlbench.adapter.cqld4.opdispensers;

import com.datastax.oss.driver.api.core.CqlSession;
import io.nosqlbench.adapter.cqld4.optypes.Cqld4CqlOp;
import io.nosqlbench.engine.api.activityimpl.BaseOpDispenser;
import io.nosqlbench.engine.api.templating.ParsedOp;
import io.nosqlbench.nb.api.errors.BasicError;

public class CqlD4PreparedBatchOpDispenser extends BaseOpDispenser<Cqld4CqlOp> {

    private final CqlSession session;
    private final ParsedOp cmd;

    public CqlD4PreparedBatchOpDispenser(CqlSession session, ParsedOp cmd) {
        super(cmd);
        this.session = session;
        this.cmd = cmd;
    }

    @Override
    public Cqld4CqlOp apply(long value) {
        throw new BasicError("this is not implemented yet.");
    }
}
