CREATE TABLE refund_approvals (
 id UUID PRIMARY KEY,
 order_id UUID NOT NULL REFERENCES orders(id),
 amount NUMERIC(12,2) NOT NULL,
 reason VARCHAR(300) NOT NULL,
 idempotency_key VARCHAR(100) NOT NULL UNIQUE,
 execution_id VARCHAR(100),
 status VARCHAR(20) NOT NULL,
 decision_by VARCHAR(120),
 decision_reason VARCHAR(500),
 created_at TIMESTAMPTZ NOT NULL,
 decided_at TIMESTAMPTZ
);
CREATE INDEX idx_refund_approvals_status ON refund_approvals(status);
