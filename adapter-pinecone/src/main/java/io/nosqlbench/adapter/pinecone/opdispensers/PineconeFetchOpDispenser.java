package io.nosqlbench.adapter.pinecone.opdispensers;

import io.nosqlbench.adapter.pinecone.PineconeSpace;
import io.nosqlbench.adapter.pinecone.ops.PineconeOp;
import io.nosqlbench.engine.api.activityimpl.uniform.DriverAdapter;
import io.nosqlbench.engine.api.templating.ParsedOp;

public class PineconeFetchOpDispenser extends PineconeOpDispenser {
    protected PineconeFetchOpDispenser(DriverAdapter<PineconeOp, PineconeSpace> adapter, ParsedOp op) {
        super(adapter, op);
    }

    @Override
    public PineconeOp apply(long value) {
        return null;
    }
}
